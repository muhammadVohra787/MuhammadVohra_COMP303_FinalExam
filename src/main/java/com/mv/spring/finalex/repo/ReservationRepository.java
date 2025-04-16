package com.mv.spring.finalex.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import com.mv.spring.finalex.model.Reservation;

@Repository
public interface ReservationRepository extends MongoRepository<Reservation, String> {

} 