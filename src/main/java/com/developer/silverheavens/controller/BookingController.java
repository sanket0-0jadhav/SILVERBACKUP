package com.developer.silverheavens.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.developer.silverheavens.dto.BookingFilter;
import com.developer.silverheavens.dto.ResponseDto;
import com.developer.silverheavens.dto.ResponseStatus;
import com.developer.silverheavens.entities.Booking;
import com.developer.silverheavens.exceptions.ValidationException;
import com.developer.silverheavens.service.BookingService;

import validators.CustomValidator;
import validators.ValidatorResult;

@RestController
@RequestMapping("/booking")
public class BookingController {
	
	@Autowired
	BookingService bookingService;
	
	//GET BOOKINGS
	@GetMapping
	public ResponseEntity<ResponseDto<List<Booking>>> getBookings(BookingFilter filter,Pageable pageable){
		List<Booking> responseBookings = bookingService.getBookings(filter,pageable);
		
		//create response
		ResponseDto<List<Booking>> resp = new ResponseDto<List<Booking>>(ResponseStatus.SUCCESS,responseBookings,null);
		return new ResponseEntity<ResponseDto<List<Booking>>>(resp,HttpStatus.OK);
	}
	
	//make new booking
	@PostMapping
	public ResponseEntity<ResponseDto<String>> createNewBooking(@RequestBody Booking newBooking){
		//Validation
		ValidatorResult result = CustomValidator.validateNewBooking(newBooking);
		if(!result.isValidationPassed()) {
			throw new ValidationException(result.getMessage().toString());
		}
		if(bookingService.insertNew(newBooking,null)) {
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Created new Booking with id : "+newBooking.getId(),null),HttpStatus.CREATED);
		}else {
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.FAIL,null,"Filed to create new booking!"),HttpStatus.BAD_REQUEST);
		}
	}
	
	//update an existing booking 
	@PatchMapping
	public ResponseEntity<ResponseDto<String>> updateRate(@RequestBody Booking updateBooking){
		if(bookingService.updateBooking(updateBooking)) {
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Updated!.",null),HttpStatus.OK);
		}else{
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.FAIL,null,"Error in updating."),HttpStatus.OK);
		} 
	}
	
	
	//make new booking
	@DeleteMapping("/{bookingId}")
	public ResponseEntity<ResponseDto<String>> deleteBooking(@PathVariable int bookingId){
		if(bookingService.deleteBooking(bookingId)) {			
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Booking deleted!",null),HttpStatus.OK); 			
		}else {
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.FAIL,null,"Cannot delete Booking!"),HttpStatus.BAD_REQUEST);
		}
	}
	
	
}
