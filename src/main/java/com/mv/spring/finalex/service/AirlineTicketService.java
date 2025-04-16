package com.mv.spring.finalex.service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mv.spring.finalex.model.AirlineTicket;
import com.mv.spring.finalex.repo.AirlineTicketRepository;

@Service
public class AirlineTicketService {

    @Autowired
    private AirlineTicketRepository airlineTicketRepository;
    
    public List<AirlineTicket> findAll() {
        return airlineTicketRepository.findAll();
    }
    
    public Optional<AirlineTicket> findById(String id) {
        return airlineTicketRepository.findById(id);
    }
    
    public AirlineTicket save(AirlineTicket airlineTicket) {
        if (airlineTicket.getNumber() == null || airlineTicket.getNumber().isEmpty()) {
            // Format: TKT-YYYYMMDD-HHMMSS
            String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss"));
            airlineTicket.setNumber("TKT-" + timestamp);
        }
        return airlineTicketRepository.save(airlineTicket);
    }
    
    public void deleteById(String id) {
        airlineTicketRepository.deleteById(id);
    }
} 