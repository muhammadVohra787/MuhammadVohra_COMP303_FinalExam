package com.mv.spring.finalex.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.spring.finalex.dto.PaymentDTO;
import com.mv.spring.finalex.model.AirlineTicket;
import com.mv.spring.finalex.model.Reservation;
import com.mv.spring.finalex.service.AirlineTicketService;
import com.mv.spring.finalex.service.PaymentService;
import com.mv.spring.finalex.service.ReservationService;

// Controller for handling ticket-related operations including viewing and ticket summaries
@Controller
public class TicketController {

    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private AirlineTicketService airlineTicketService;
    
    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Displays ticket summary with reservation and payment information
    @GetMapping("/ticket-summary")
    public String showTicketSummary(Model model, @RequestParam String resId) {
        if (resId == null || resId.isEmpty()) {
            System.out.println("Ticket summary requested with empty reservation ID");
            return "redirect:/reservation";
        }
    
        Optional<Reservation> optionalReservation = reservationService.findById(resId);
        if (optionalReservation.isEmpty()) {
            System.out.println("Ticket summary requested for non-existent reservation ID: " + resId);
            return "redirect:/reservation";
        }
    
        Reservation reservation = optionalReservation.get();
        model.addAttribute("reservation", reservation);
    
        Optional<AirlineTicket> optionalTicket = airlineTicketService.findAll().stream()
            .filter(ticket -> resId.equals(ticket.getReservationId()))
            .findFirst();
    
        if (optionalTicket.isPresent()) {
            AirlineTicket ticket = optionalTicket.get();
            model.addAttribute("ticket", ticket);
    
            if (ticket.getPaymentId() != null) {
                paymentService.findById(ticket.getPaymentId()).ifPresent(payment -> {
                    model.addAttribute("payment", payment);
                    try {
                        PaymentDTO paymentDTO = objectMapper.convertValue(payment, PaymentDTO.class);
                        String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(paymentDTO);
                        System.out.println("PaymentDTO: " + json);
                    } catch (Exception e) {
                        System.out.println("Error serializing payment: " + e.getMessage());
                    }
                });
            }
        } else {
            System.out.println("No ticket found for reservation ID: " + resId);
        }
    
        return "ticket-summary";
    }
    
} 