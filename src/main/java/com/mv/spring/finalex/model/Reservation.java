package com.mv.spring.finalex.model;

import java.time.LocalDate;
import java.time.LocalTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

@Document(collection = "reservations")
public class Reservation {

    @Id
    private String id;
    
    @NotBlank(message = "First name is required")
    @JsonProperty("firstName")
    private String firstName;
    
    @NotBlank(message = "Last name is required")
    @JsonProperty("lastName")
    private String lastName;
    
    @Min(value = 1, message = "Number of passengers must be at least 1")
    @JsonProperty("numPassengers")
    private int numPassengers;
    
    @NotBlank(message = "Class type is required")
    @JsonProperty("classType")
    private String classType;
    
    @NotBlank(message = "Phone number is required")
    @Pattern(regexp = "\\d{10}", message = "Phone number must be 10 digits")
    @JsonProperty("phoneNumber")
    private String phoneNumber;
    
    @NotNull(message = "Time is required")
    @JsonProperty("time")
    private LocalTime time;
    
    @NotNull(message = "Date is required")
    @Future(message = "Date must be in the future")
    @JsonProperty("date")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date;
    
    @JsonProperty("ticketNumber")
    private String ticketNumber;
    
    @JsonProperty("details")
    private String details;
    
    // Default constructor
    public Reservation() {
    }
    
    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public int getNumPassengers() {
        return numPassengers;
    }

    public void setNumPassengers(int numPassengers) {
        this.numPassengers = numPassengers;
    }

    public String getClassType() {
        return classType;
    }

    public void setClassType(String classType) {
        this.classType = classType;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public LocalTime getTime() {
        return time;
    }

    public void setTime(LocalTime time) {
        this.time = time;
    }

    public LocalDate getDate() {
        return date;
    }

    public void setDate(LocalDate date) {
        this.date = date;
    }

    public String getTicketNumber() {
        return ticketNumber;
    }

    public void setTicketNumber(String ticketNumber) {
        this.ticketNumber = ticketNumber;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
} 