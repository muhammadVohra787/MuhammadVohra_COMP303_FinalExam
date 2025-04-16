package com.mv.spring.finalex.controller;

import java.time.LocalDate;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.spring.finalex.dto.PaymentDTO;
import com.mv.spring.finalex.model.AirlineTicket;
import com.mv.spring.finalex.model.Payment;
import com.mv.spring.finalex.model.Reservation;
import com.mv.spring.finalex.service.AirlineTicketService;
import com.mv.spring.finalex.service.PaymentService;
import com.mv.spring.finalex.service.ReservationService;

import jakarta.validation.Valid;

// Controller for handling payment UI operations
@Controller
@RequestMapping("/payment")
public class PaymentViewController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private AirlineTicketService airlineTicketService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Displays payment form for a reservation
    @GetMapping
    public String showPaymentForm(Model model, 
                                 @RequestParam String resId,
                                 RedirectAttributes redirectAttributes) {
        System.out.println("GET /payment with resId: " + resId);
        
        if (resId == null || resId.isEmpty()) {
            System.out.println("No reservation ID provided for payment form");
            return "redirect:/reservation";
        }
        
        // Find the reservation
        java.util.Optional<Reservation> optionalReservation = reservationService.findById(resId);
        if (!optionalReservation.isPresent()) {
            System.out.println("Reservation not found for ID: " + resId);
            return "redirect:/reservation";
        }
        
        Reservation reservation = optionalReservation.get();
        model.addAttribute("reservation", reservation);
        
        // if we are in edit mode, add a new payment object if not already in model
        if (!model.containsAttribute("payment")) {
            Payment payment = new Payment();
            payment.setReservationId(resId);
            payment.Calculate(reservation.getNumPassengers(), reservation.getClassType());
            payment.setDate(LocalDate.now());
            model.addAttribute("payment", payment);
            
            try {
                // Convert Payment to PaymentDTO using ObjectMapper
                PaymentDTO paymentDTO = objectMapper.convertValue(payment, PaymentDTO.class);
                System.out.println("Payment calculation: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(paymentDTO));
            } catch (JsonProcessingException e) {
                System.out.println("Error serializing payment: " + e.getMessage());
            }
        }
        
        return "payment";
    }
    
    // Processes payment form submission and creates a ticket
    @PostMapping
    public String processPayment(@Valid @ModelAttribute("payment") Payment payment,
                               BindingResult result,
                               @RequestParam String resId,
                               RedirectAttributes redirectAttributes) {
        System.out.println("Processing payment for resId: " + resId);
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.payment", result);
            redirectAttributes.addFlashAttribute("payment", payment);
            return "redirect:/payment?resId=" + resId;
        }
        
        if (resId == null || resId.isEmpty()) {
            System.out.println("No reservation ID provided for payment processing");
            return "redirect:/reservation";
        }

        java.util.Optional<Reservation> optionalReservation = reservationService.findById(resId);
        if (!optionalReservation.isPresent()) {
            System.out.println("Reservation not found for ID: " + resId);
            return "redirect:/reservation";
        }
        Reservation reservation = optionalReservation.get();
        
        try {
            // Create and save the payment (credit card info will now be saved)
            payment.setReservationId(resId);
            payment.Calculate(reservation.getNumPassengers(), reservation.getClassType());
            payment.setDate(LocalDate.now());
        
            Payment savedPayment = paymentService.save(payment);
            
            try {
                // To remove the credit card info, we convert the payment to a PaymentDTO
                PaymentDTO paymentDTO = objectMapper.convertValue(savedPayment, PaymentDTO.class);
                System.out.println("Payment saved: " + objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(paymentDTO));
            } catch (JsonProcessingException e) {
                System.out.println("Error serializing saved payment: " + e.getMessage());
            }
        
            // Generate airline ticket
            AirlineTicket ticket = new AirlineTicket();
            ticket.setReservationId(resId);
            ticket.setPaymentId(savedPayment.getId());
            ticket.setPrice(payment.getAmount());
            ticket.setDetails("Flight details for " + reservation.getFirstName() + " " + reservation.getLastName());
        
            AirlineTicket savedTicket = airlineTicketService.save(ticket);
            
            // Update reservation with ticket number
            reservation.setTicketNumber(savedTicket.getNumber());
            reservationService.save(reservation);
        
            // Redirect to ticket summary with resId
            return "redirect:/ticket-summary?resId=" + resId;
        } catch (Exception e) {
            System.out.println("Error processing payment: " + e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Failed to process payment. Please try again.");
            return "redirect:/payment?resId=" + resId;
        }
    }
}