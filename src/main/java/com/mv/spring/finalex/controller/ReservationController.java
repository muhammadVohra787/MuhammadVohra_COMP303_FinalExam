package com.mv.spring.finalex.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mv.spring.finalex.model.Reservation;
import com.mv.spring.finalex.service.ReservationService;

import jakarta.validation.Valid;

// Controller for handling reservation operations and the home page
@Controller
@RequestMapping
public class ReservationController {

    @Autowired
    private ReservationService reservationService;

    @Autowired
    private ObjectMapper objectMapper;

    // Displays the home page with a list of all reservations
    @GetMapping("/")
    public String home(Model model) {
        // Get all reservations and add to model

        try {
            // Serialize the reservations
            String reservationsJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(reservationService.findAll());

            System.out.println("Home page - Loaded reservations: " + reservationsJson);

            // Deserialize the reservations
            List<Reservation> reservations = objectMapper.readValue(reservationsJson,
                    new TypeReference<List<Reservation>>() {
                    });

            model.addAttribute("reservations", reservations);
        } catch (JsonProcessingException e) {
            System.out.println("Error in home page serializing reservations: " + e.getMessage());
        }

        return "index";
    }

    // Displays the form for creating a new reservation
    @GetMapping("/reservation")
    public String showReservationForm(Model model) {
    	//add reservation object to model
		Reservation reservation = new Reservation();
		model.addAttribute("reservation", reservation);
        return "reservation";
    }

    // Processes form submission for creating a new reservation
    @PostMapping("/reservation")
    public String processReservation(@Valid @ModelAttribute("reservation") Reservation reservation,
                                     BindingResult result,
                                     RedirectAttributes redirectAttributes) {

        try {
            // Serialize reservation object
            String reservationJson = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(reservation);
            System.out.println("Processing reservation: " + reservationJson);

            // Deserialize for demo purposes
            Reservation deserialized = objectMapper.readValue(reservationJson, Reservation.class);

            if (result.hasErrors()) {
                // Serialize validation errors
                String errorsJson = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(result.getAllErrors());
                System.out.println("Validation errors: " + errorsJson);

                redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.reservation", result);
                redirectAttributes.addFlashAttribute("reservation", deserialized);
                return "redirect:/reservation";
            }

            // Save reservation
            Reservation saved = reservationService.save(deserialized);
            System.out.println("Saved reservation with ID: " + saved.getId());

            return "redirect:/payment?resId=" + saved.getId();

        } catch (Exception e) {
            System.out.println("Error processing reservation: " + e.getMessage());
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("reservation", reservation);
            return "redirect:/reservation";
        }
    }


    // Deletes a reservation by ID
    @PostMapping("/reservation/delete/{id}")
    public String deleteReservation(@PathVariable String id) {

        try {
            Optional<Reservation> reservation = reservationService.findById(id);

            if (reservation.isPresent()) {
                // Serialize the reservation
                String reservationJson = objectMapper.writerWithDefaultPrettyPrinter()
                        .writeValueAsString(reservation.get());

                // Log the reservation as JSON
                System.out.println("Deleting reservation: " + reservationJson);

                // Deserialize the reservation
                Reservation deserializedReservation = objectMapper.readValue(reservationJson, Reservation.class);

                reservationService.deleteById(deserializedReservation.getId());

            } else {
                System.out.println("Attempting to delete non-existent reservation with ID: " + id);
            }
        } catch (JsonProcessingException e) {
            System.out.println("Error processing reservation deletion: " + e.getMessage());
        }

        // Return to home page after deletion
        return "redirect:/";
    }

    // Displays the form for editing an existing reservation
    @GetMapping("/reservation/edit/{id}")
    public String showEditForm(@PathVariable String id, Model model) {
        // Find the reservation
        Optional<Reservation> optionalReservation = reservationService.findById(id);

        if (optionalReservation.isEmpty()) {
            System.out.println("Edit form requested for non-existent reservation ID: " + id);
            // If not found, redirect to home
            return "redirect:/";
        }

        try {
            // Serialize the reservation
            String reservationJson = objectMapper.writerWithDefaultPrettyPrinter()
                    .writeValueAsString(optionalReservation.get());
            // Log the reservation as JSON
            System.out.println("Showing edit form for reservation: " + reservationJson);

            // Deserialize the reservation
            Reservation deserializedReservation = objectMapper.readValue(reservationJson, Reservation.class);

            // Add reservation to model for editing
            model.addAttribute("reservation", deserializedReservation);

        } catch (JsonProcessingException e) {
            System.out.println("Error processing edit form: " + e.getMessage());
        }

        return "edit-reservation";
    }

    // Processes form submission for editing an existing reservation
    @PostMapping("/reservation/edit/{id}")
    public String processEdit(@PathVariable String id,
                              @Valid Reservation reservation,
                              BindingResult result) {
    
        try {

            // Serialize the reservation
            String reservationJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(reservation);
            System.out.println("Incoming Reservation (Pretty JSON):\n" + reservationJson);
    
            // Deserialize the reservation

            Reservation deserialized = objectMapper.readValue(reservationJson, Reservation.class);
    
            // If validation errors exist, print them as JSON
            if (result.hasErrors()) {
                String errorsJson = objectMapper
                        .writerWithDefaultPrettyPrinter()
                        .writeValueAsString(result.getAllErrors());
                System.out.println("Validation Errors Json:\n" + errorsJson);
    
                return "edit-reservation";
            }
            
            // Proceed with updating
            deserialized.setId(id); // keep original ID
            reservationService.save(deserialized);
            System.out.println("Updated reservation!");
    
        } catch (Exception e) {
            System.out.println("Jackson Processing Error: " + e.getMessage());
            e.printStackTrace();
        }
    
        return "redirect:/";
    }
    
}
