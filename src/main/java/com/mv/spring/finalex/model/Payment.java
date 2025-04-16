package com.mv.spring.finalex.model;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;


@Document(collection = "payments")
public class Payment {

    @Id
    private String id;
    
    @NotNull(message = "Amount is required")
    @Min(value = 1, message = "Amount must be at least 1")
    @JsonProperty("amount")
    private double amount;
    
    @NotNull(message = "Date is required")
    @JsonProperty("date")
    private LocalDate date;
    
    @JsonProperty("reservationId")
    private String reservationId;
    
    // Credit card fields - stored in MongoDB now
    @NotBlank(message = "Card holder name is required")
    @JsonProperty("cardHolderName")
    private String cardHolderName;
    
    @NotBlank(message = "Card number is required")
    @Pattern(regexp = "^\\d{16}$", message = "Card number must be exactly 16 digits")
    @JsonProperty("cardNumber")
    private String cardNumber;
    
    @Pattern(regexp = "^(0[1-9]|1[0-2])\\/\\d{2}$", message = "Expiry date must be in MM/YY format")
    @JsonProperty("expiryDate")
    private String expiryDate;
    
    @NotBlank(message = "CVV is required")
    @Pattern(regexp = "^\\d{3}$", message = "CVV must be exactly 3 digits")
    @JsonProperty("cvv")
    private String cvv;
    
    public void Calculate(int numPassengers, String classType){
    	if(classType.equals("Economy"))
			this.amount = 500.0 * numPassengers;
		else if(classType.equals("Business"))
			this.amount = 1000.0 * numPassengers;
		else if(classType.equals("First Class"))
			this.amount = 1500.0 * numPassengers;
		else
        this.amount = 1000.0 * numPassengers;
    }

    // Default constructor
    public Payment() {
    }
    
    // Getters and Setters
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
    
    public String getCardHolderName() {
        return cardHolderName;
    }

    public void setCardHolderName(String cardHolderName) {
        this.cardHolderName = cardHolderName;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public String getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(String expiryDate) {
        this.expiryDate = expiryDate;
    }

    public String getCvv() {
        return cvv;
    }

    public void setCvv(String cvv) {
        this.cvv = cvv;
    }
} 