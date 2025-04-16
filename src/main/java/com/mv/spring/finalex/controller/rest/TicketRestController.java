package com.mv.spring.finalex.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.mv.spring.finalex.dto.PaymentDTO;
import com.mv.spring.finalex.model.AirlineTicket;
import com.mv.spring.finalex.model.Payment;
import com.mv.spring.finalex.model.Reservation;
import com.mv.spring.finalex.service.AirlineTicketService;
import com.mv.spring.finalex.service.PaymentService;
import com.mv.spring.finalex.service.ReservationService;

@RestController
@RequestMapping("/api/tickets")
public class TicketRestController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private AirlineTicketService airlineTicketService;

    @Autowired
    private PaymentService paymentService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping
    public ResponseEntity<List<AirlineTicket>> getAllTickets() {
        List<AirlineTicket> tickets = airlineTicketService.findAll();
        try {
            String json = objectMapper.writeValueAsString(tickets);
            objectMapper.readValue(json, List.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ResponseEntity.ok(tickets);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getTicketById(@PathVariable String id) {
        Optional<AirlineTicket> optionalTicket = airlineTicketService.findById(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ticket not found with ID: " + id));
        }

        AirlineTicket ticket = optionalTicket.get();
        Optional<Reservation> optionalReservation = reservationService.findById(ticket.getReservationId());

        Map<String, Object> response = new HashMap<>();
        response.put("ticket", ticket);
        optionalReservation.ifPresent(reservation -> response.put("reservation", reservation));

        try {
            String ticketJson = objectMapper.writeValueAsString(ticket);
            objectMapper.readValue(ticketJson, AirlineTicket.class);
            optionalReservation.ifPresent(reservation -> {
                try {
                    String resJson = objectMapper.writeValueAsString(reservation);
                    objectMapper.readValue(resJson, Reservation.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}/summary")
    public ResponseEntity<?> getTicketSummary(@PathVariable String id) {
        Optional<AirlineTicket> optionalTicket = airlineTicketService.findById(id);
        if (optionalTicket.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Ticket not found with ID: " + id));
        }

        AirlineTicket ticket = optionalTicket.get();
        Optional<Reservation> optionalReservation = reservationService.findById(ticket.getReservationId());
        if (optionalReservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Reservation not found for this ticket"));
        }

        Reservation reservation = optionalReservation.get();
        Map<String, Object> response = new HashMap<>();

        try {
            String json = objectMapper.writeValueAsString(ticket);
            AirlineTicket deserializedTicket = objectMapper.readValue(json, AirlineTicket.class);

            response.put("ticket", deserializedTicket);
            response.put("reservation", reservation);

            if (deserializedTicket.getPaymentId() != null) {
                Optional<Payment> optionalPayment = paymentService.findById(deserializedTicket.getPaymentId());
                if (optionalPayment.isPresent()) {
                    Payment payment = optionalPayment.get();
                    PaymentDTO paymentDTO = objectMapper.convertValue(payment, PaymentDTO.class);
                    response.put("payment", paymentDTO);
                }
            }

            ObjectNode summaryNode = objectMapper.createObjectNode();
            summaryNode.put("reservationId", reservation.getId());
            summaryNode.put("passengerName", reservation.getFirstName() + " " + reservation.getLastName());
            summaryNode.put("ticketNumber", deserializedTicket.getNumber());
            summaryNode.put("classType", reservation.getClassType());
            summaryNode.put("passengers", reservation.getNumPassengers());
            summaryNode.put("price", deserializedTicket.getPrice());
            summaryNode.put("reservationDate", reservation.getDate().toString());

            response.put("summary", summaryNode);
            response.put("success", true);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Error processing ticket data: " + e.getMessage()));
        }
    }

    private Map<String, Object> createErrorResponse(String message) {
        Map<String, Object> response = new HashMap<>();
        response.put("success", false);
        response.put("message", message);
        return response;
    }
}
