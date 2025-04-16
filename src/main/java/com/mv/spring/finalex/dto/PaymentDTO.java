package com.mv.spring.finalex.dto;

import java.time.LocalDate;

/**
 * Data Transfer Object for Payment that excludes sensitive payment information
 * Used to return payment data in REST responses without exposing credit card details
 */
public class PaymentDTO {
    private String id;
    private double amount;
    private LocalDate date;
    private String reservationId;
    
    // Getters and setters
    public String getId() {
        return id;
    }
    
    public void setId(String id) {
        this.id = id;
    }
    
    public double getAmount() {
        return amount;
    }
    
    public void setAmount(double amount) {
        this.amount = amount;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public void setDate(LocalDate date) {
        this.date = date;
    }
    
    public String getReservationId() {
        return reservationId;
    }
    
    public void setReservationId(String reservationId) {
        this.reservationId = reservationId;
    }
} 