package com.mv.spring.finalex.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mv.spring.finalex.model.Reservation;
import com.mv.spring.finalex.repo.ReservationRepository;

@Service
public class ReservationService {

    @Autowired
    private ReservationRepository reservationRepository;
    
    public List<Reservation> findAll() {
        return reservationRepository.findAll();
    }
    
    public Optional<Reservation> findById(String id) {
        return reservationRepository.findById(id);
    }
    
    public Reservation save(Reservation reservation) {
        return reservationRepository.save(reservation);
    }
    
    public void deleteById(String id) {
        reservationRepository.deleteById(id);
    }
} 