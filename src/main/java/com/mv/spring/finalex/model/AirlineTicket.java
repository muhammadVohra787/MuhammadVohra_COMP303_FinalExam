package com.mv.spring.finalex.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Document(collection = "airlineTickets")
public class AirlineTicket {

    @Id
    private String id;
    
    @NotBlank(message = "Ticket number is required")
    @JsonProperty("number")
    private String number;
    
    @JsonProperty("details")
    private String details;
    
    @NotNull(message = "Price is required")
    @Min(value = 0, message = "Price must be non-negative")
    @JsonProperty("price")
    private double price;
    
    @JsonProperty("reservationId")
    private String reservationId;
    
    @JsonProperty("paymentId")
    private String paymentId;
    
    // Default constructor
    public AirlineTicket() {
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
    
    public String getReservationId() {
        return reservationId;
    }

    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setPaymentId(String paymentId) {
        this.paymentId = paymentId;
    }
} 