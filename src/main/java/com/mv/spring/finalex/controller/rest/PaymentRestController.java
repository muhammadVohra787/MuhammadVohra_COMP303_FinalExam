package com.mv.spring.finalex.controller.rest;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.spring.finalex.dto.PaymentDTO;
import com.mv.spring.finalex.model.AirlineTicket;
import com.mv.spring.finalex.model.Payment;
import com.mv.spring.finalex.model.Reservation;
import com.mv.spring.finalex.service.AirlineTicketService;
import com.mv.spring.finalex.service.PaymentService;
import com.mv.spring.finalex.service.ReservationService;

import jakarta.validation.Valid;

// REST Controller for payment operations
@RestController
@RequestMapping("/api/payments")
public class PaymentRestController {

    @Autowired
    private PaymentService paymentService;
    
    @Autowired
    private ReservationService reservationService;
    
    @Autowired
    private AirlineTicketService airlineTicketService;
    
    @Autowired
    private ObjectMapper objectMapper;
    
    // Get all payments (secured with DTO)
    @GetMapping
    public ResponseEntity<List<PaymentDTO>> getAllPayments() {
        System.out.println("Getting all payments");
        
        List<Payment> payments = paymentService.findAll();

        // Serialize the payments to DTOs to avoid exposing sensitive data
        List<PaymentDTO> paymentDTOs = payments.stream()
            .map(payment -> objectMapper.convertValue(payment, PaymentDTO.class))
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(paymentDTOs);
    }
    
    // Get payment by ID (secured with DTO)
    @GetMapping("/{id}")
    public ResponseEntity<?> getPaymentById(@PathVariable String id) {
        System.out.println("Getting payment by ID: " + id);
        
        Optional<Payment> optionalPayment = paymentService.findById(id);
        
        if (optionalPayment.isEmpty()) {
            System.out.println("Payment not found with ID: " + id);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(createErrorResponse("Payment not found with ID: " + id));
        }

        // Serialize the payment to DTO to avoid exposing sensitive data
        PaymentDTO paymentDTO = objectMapper.convertValue(optionalPayment.get(), PaymentDTO.class);
        
        return ResponseEntity.ok(paymentDTO);
    }
    
    // Get payment information for a reservation
    @GetMapping("/reservation/{reservationId}")
    public ResponseEntity<?> getPaymentInfoForReservation(@PathVariable String reservationId) {
        System.out.println("Getting payment info for reservation ID: " + reservationId);
        
        if (reservationId == null || reservationId.isEmpty()) {
            System.out.println("Reservation ID is empty");
            return ResponseEntity.badRequest().body(createErrorResponse("Reservation ID is required"));
        }
        
        // Find the reservation
        Optional<Reservation> optionalReservation = reservationService.findById(reservationId);
        if (optionalReservation.isEmpty()) {
            System.out.println("Reservation not found with ID: " + reservationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(createErrorResponse("Reservation not found"));
        }
        
        Reservation reservation = optionalReservation.get();
        
        // Create a new payment object
        Payment payment = new Payment();
        payment.setReservationId(reservationId);
        payment.Calculate(reservation.getNumPassengers(), reservation.getClassType());
        payment.setDate(LocalDate.now());
        
        
        // Serialize the payment to DTO to avoid exposing sensitive data
        PaymentDTO paymentDTO = objectMapper.convertValue(payment, PaymentDTO.class);
        
        // Return payment info with DTO
        Map<String, Object> response = new HashMap<>();
        response.put("payment", paymentDTO);
        response.put("reservation", reservation);
        
        return ResponseEntity.ok(response);
    }
    
    // Process a payment and create a ticket
    @PostMapping("/process")
    public ResponseEntity<?> processPayment(@Valid @RequestBody Payment payment) {
        String reservationId = payment.getReservationId();
        System.out.println("Processing payment for reservation ID: " + reservationId);
        
        if (reservationId == null || reservationId.isEmpty()) {
            System.out.println("Reservation ID is empty");
            return ResponseEntity.badRequest().body(createErrorResponse("Reservation ID is required"));
        }

        Optional<Reservation> optionalReservation = reservationService.findById(reservationId);
        if (optionalReservation.isEmpty()) {
            System.out.println("Reservation not found with ID: " + reservationId);
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                  .body(createErrorResponse("Reservation not found"));
        }
        
        Reservation reservation = optionalReservation.get();
    
        try {
            // Create and save the payment
            payment.Calculate(reservation.getNumPassengers(), reservation.getClassType());
            payment.setDate(LocalDate.now());
        
            Payment savedPayment = paymentService.save(payment);
            
            // Convert to DTO using ObjectMapper
            PaymentDTO paymentDTO = objectMapper.convertValue(savedPayment, PaymentDTO.class);
        
            // Generate airline ticket
            AirlineTicket ticket = new AirlineTicket();
            ticket.setReservationId(reservationId);
            ticket.setPaymentId(savedPayment.getId());
            ticket.setPrice(payment.getAmount());
            ticket.setDetails("Flight details for " + reservation.getFirstName() + " " + reservation.getLastName());
        
            AirlineTicket savedTicket = airlineTicketService.save(ticket);
        
            // Update reservation with ticket number
            reservation.setTicketNumber(savedTicket.getNumber());
            reservationService.save(reservation);
        
            // Return success response with ticket and payment details (DTO)
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("ticket", savedTicket);
            response.put("payment", paymentDTO);
            response.put("message", "Payment processed successfully");
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            System.out.println("Failed to process payment: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                  .body(createErrorResponse("Failed to process payment: " + e.getMessage()));
        }
    }
    
    // Helper method to create error response
    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
} 