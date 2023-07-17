package com.developer.silverheavens.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.developer.silverheavens.entities.Booking;

@Repository
public interface BookingRepo extends JpaRepository<Booking, Integer>,JpaSpecificationExecutor<Booking>{

}
