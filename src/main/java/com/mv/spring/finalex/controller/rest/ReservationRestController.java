package com.mv.spring.finalex.controller.rest;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.spring.finalex.model.Reservation;
import com.mv.spring.finalex.service.ReservationService;

import jakarta.validation.Valid;

// REST Controller for reservation CRUD operations
@RestController
@RequestMapping("/api/reservations")
public class ReservationRestController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    // Get all reservations
    @GetMapping
    public ResponseEntity<?> getAllReservations() {
        System.out.println("Getting all reservations");

        List<Reservation> reservations = reservationService.findAll();

        try {
            // Serialize the reservations
            String reservationsJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservations);
            System.out.println("Retrieved reservations: " + reservationsJson);

            // Deserialize the reservations
            List<Reservation> deserializedReservations = objectMapper.readValue(reservationsJson,
                    new TypeReference<List<Reservation>>() {
                    });
            System.out.println("Deserialized reservations: " + deserializedReservations);

            return ResponseEntity.ok(deserializedReservations);

        } catch (JsonProcessingException e) {
            System.out.println("Error serializing reservations: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve reservations: " + e.getMessage()));
        }

    }

    // Get a reservation by ID
    @GetMapping("/{id}")
    public ResponseEntity<?> getReservationById(@PathVariable String id) {
        System.out.println("Getting reservation by ID: " + id);

        Optional<Reservation> optionalReservation = reservationService.findById(id);

        try {
            if (optionalReservation.isEmpty()) {
                System.out.println("Reservation not found with ID: " + id);
                return ResponseEntity.status(HttpStatus.NOT_FOUND)
                        .body(createErrorResponse("Reservation not found with ID: " + id));
            }
            // Serialize the reservation
            String reservationJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(optionalReservation.get());
            System.out.println("Retrieved reservation: " + reservationJson);

            // Deserialize the reservation
            Reservation deserializedReservation = objectMapper.readValue(reservationJson, Reservation.class);
            System.out.println("Deserialized reservation: " + deserializedReservation);

            return ResponseEntity.ok(deserializedReservation);
        } catch (JsonProcessingException e) {
            System.out.println("Error serializing reservation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to retrieve reservation: " + e.getMessage()));
        }

    }

    // Create a new reservation
    @PostMapping
    public ResponseEntity<?> createReservation(@Valid @RequestBody Reservation reservation) {
        try {
            // Serialize the reservation
            String reservationJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(reservation);
            
            System.out.println("Creating reservation: " + reservationJson);

            // Deserialize the reservation
            Reservation deserializedReservation = objectMapper.readValue(reservationJson, Reservation.class);

            Reservation savedReservation = reservationService.save(deserializedReservation);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reservation created successfully");
            response.put("reservation", savedReservation);

            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            System.out.println("Failed to create reservation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to create reservation: " + e.getMessage()));
        }
    }

    // Update a reservation
    @PutMapping("/{id}")
    public ResponseEntity<?> updateReservation(@PathVariable String id,
                                               @Valid @RequestBody Reservation reservationDetails) {
    
        Optional<Reservation> optionalReservation = reservationService.findById(id);
        if (optionalReservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Reservation not found with ID: " + id));
        }
    
        try {
            // Serialize the incoming reservation
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservationDetails);
    
            // Deserialize it back to ensure it can be mapped cleanly
            Reservation deserialized = objectMapper.readValue(json, Reservation.class);
    
            Reservation existing = optionalReservation.get();
            existing.setFirstName(deserialized.getFirstName());
            existing.setLastName(deserialized.getLastName());
            existing.setPhoneNumber(deserialized.getPhoneNumber());
            existing.setDetails(deserialized.getDetails());
            existing.setNumPassengers(deserialized.getNumPassengers());
            existing.setClassType(deserialized.getClassType());
            existing.setTime(deserialized.getTime());
            existing.setDate(deserialized.getDate());
    
            Reservation updated = reservationService.save(existing);
    
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reservation updated successfully");
            response.put("reservation", updated);
    
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {
            System.out.println("Error processing reservation update: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to update reservation: " + e.getMessage()));
        }
    }
    

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReservation(@PathVariable String id) {
        Optional<Reservation> optionalReservation = reservationService.findById(id);
        
        if (optionalReservation.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(createErrorResponse("Reservation not found with ID: " + id));
        }
    
        try {
            // Serialize and deserialize the reservation (for audit/log/demo)
            String json = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(optionalReservation.get());
            System.out.println("Deleting reservation: " + json);
            Reservation deserialized = objectMapper.readValue(json, Reservation.class);

            // Proceed with deletion
            reservationService.deleteById(deserialized.getId());
    
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "Reservation deleted successfully");
    
            return ResponseEntity.ok(response);
    
        } catch (Exception e) {
            System.out.println("Error deleting reservation: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(createErrorResponse("Failed to delete reservation: " + e.getMessage()));
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