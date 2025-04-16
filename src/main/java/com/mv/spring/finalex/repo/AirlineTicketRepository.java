package com.mv.spring.finalex.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mv.spring.finalex.model.AirlineTicket;

@Repository
public interface AirlineTicketRepository extends MongoRepository<AirlineTicket, String> {
} 