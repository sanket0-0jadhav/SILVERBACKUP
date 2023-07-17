package com.developer.silverheavens.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.developer.silverheavens.dto.BookingFilter;
import com.developer.silverheavens.entities.Booking;
import com.developer.silverheavens.entities.Bungalow;
import com.developer.silverheavens.entities.Rate;
import com.developer.silverheavens.exceptions.DateOutOfRangeException;
import com.developer.silverheavens.exceptions.IdNotFoundException;
import com.developer.silverheavens.exceptions.ValidationException;
import com.developer.silverheavens.repository.BookingRepo;
import com.developer.silverheavens.repository.BungalowRepo;
import com.developer.silverheavens.repository.RateRepository;
import com.developer.silverheavens.specifications.BookingSpecification;
import com.developer.silverheavens.specifications.BungalowSpecification;
import com.developer.silverheavens.specifications.RateSpecification;
import com.developer.silverheavens.util.BookingUtil;

import validators.CustomValidator;
import validators.ValidatorResult;


@Service
public class BookingService {
	
//	@Autowired
//	EntityManager entityManager;
	
	@Autowired
	RateRepository rateRepo;
	
	@Autowired
	BungalowRepo bungalowRepo;

	@Autowired
	BookingRepo bookingRepo;
	
	//get bookings
	public List<Booking> getBookings(BookingFilter filter,Pageable pageable) {
		Page<Booking> bookingPage = bookingRepo.findAll(BookingSpecification.getBooksByFilter(filter),pageable);
		return bookingPage.getContent();
	}
	
	//insert new booking	
	public boolean insertNew(Booking newBooking,Booking refFromDbToUpdate) {
		//check if bungalow exist
		Optional<Bungalow> bungalowOpt = bungalowRepo.findById(newBooking.getBungalowId());
		
		//if not found
		if(bungalowOpt.isEmpty()) 
			throw new IdNotFoundException(Bungalow.class,newBooking.getBungalowId());
		
		//check if already booked for these dates
		List<Booking> overlappingList = bookingRepo.findAll(BookingSpecification.getOverlappingBookings(newBooking));
		//if updating
		if(refFromDbToUpdate!=null) {
			overlappingList.remove(refFromDbToUpdate);
		}
		System.out.println(overlappingList);
		if(!overlappingList.isEmpty()) {
			String bookedDates = overlappingList
					.stream()
					.map((b)->"["+b.getStayDateFrom()+" to "+b.getStayDateTo()+"]    ")
					.collect(Collectors.joining());
			throw new RuntimeException("Bungalow already booked in given days! : "+bookedDates);
		}
		
		//get affecting rates
		List<Rate> ratesList = rateRepo.findAll(RateSpecification.getRatesForInsertion(newBooking));
		
		
		Collections.sort(ratesList);
		
		//check if prices exist
		if(ratesList.isEmpty() 
				|| newBooking.getStayDateFrom().isBefore(ratesList.get(0).getStayDateFrom())
				|| newBooking.getStayDateTo().isAfter(ratesList.get(ratesList.size()-1).getStayDateTo()))
			throw new DateOutOfRangeException(newBooking.getStayDateFrom(),newBooking.getStayDateTo());
		
		//get price
		float price = BookingUtil.calculatePrice(newBooking,ratesList);
		
		newBooking.setPrice(price);
		bookingRepo.save(newBooking);
		//entityManager.clear();
		
		return true;
	}
	
	//UPDATE
	public boolean updateBooking(Booking updateBooking) {
		/*Validation*/
		ValidatorResult result = CustomValidator.validateNewBooking(updateBooking);
		if(!result.isValidationPassed()) {
			throw new ValidationException(result.getMessage().toString());
		}
		
		//check if bungalow exists
		Optional<Bungalow> bungalowOptional = bungalowRepo.findOne(BungalowSpecification.withId(updateBooking.getBungalowId()));
		if(bungalowOptional.isEmpty())
			throw new IdNotFoundException(Bungalow.class, updateBooking.getBungalowId());
		
		/*----ID IS DELETED----*/
		//Get from Optional
		Optional<Booking> refFromDbOptions = bookingRepo.findById(updateBooking.getId());
		
		//get by 
		if(refFromDbOptions.isEmpty()) {
			throw new IdNotFoundException(Booking.class, updateBooking.getId());
		}
		
		Booking refFromDb = refFromDbOptions.get();
	
		updateBooking.setId(refFromDb.getId());
		return this.insertNew(updateBooking,refFromDb);
		
	}
	
	//DELETE
	public boolean deleteBooking(int bookingId) {
		//Get from Optional
		Optional<Booking> refFromDbOptions = bookingRepo.findOne(BookingSpecification.byId(bookingId));
				
		//get by 
		if(refFromDbOptions.isEmpty()) {
			throw new IdNotFoundException(Booking.class, bookingId);
		}
		
		Booking refFromDb = refFromDbOptions.get();
		
//		if(!refFromDb.getStayDateFrom().isAfter(LocalDate.now())) {
//			throw new UpdateException(bookingId,"Cannot cancel booking after STAY_DATE_FROM.");
//		}
		
		//delete from DB
		bookingRepo.delete(refFromDb);
		//entityManager.clear();
		
		return true;
	}
	
}
