package com.developer.silverheavens.specifications;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.developer.silverheavens.dto.BookingFilter;
import com.developer.silverheavens.entities.Booking;

public interface BookingSpecification {

	public static Specification<Booking> byBungalowId(int bungalowId){
		return (root,query,cb)->{return cb.equal(root.get("bungalowId"), bungalowId);};
	}
	
	public static Specification<Booking> byId(int bookingid){
		return (root,query,cb)->{return cb.equal(root.get("id"), bookingid);};
	}
	
	//by nights
	public static Specification<Booking> byNights(int nights){
		return (root,query,cb)->{return cb.equal(root.get("nights"), nights);};
	}
	//by min nights
	public static Specification<Booking> minNights(int nights){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("nights"), nights);};
	}
	//by max nights
	public static Specification<Booking> maxNights(int nights){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("nights"), nights);};
	}
	
	//by price
	public static Specification<Booking> byPrice(float price){
		return (root,query,cb)->{return cb.equal(root.get("price"), price);};
	}
	//by min price
	public static Specification<Booking> minPrice(float price){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("price"), price);};
	}
	//by max price
	public static Specification<Booking> maxPrice(float price){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("price"), price);};
	}
	
	//get after from date
	public static Specification<Booking> afterOrEqualToFrom(LocalDate from){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("stayDateFrom"), from);};
	}
		
	//get only before date
	public static Specification<Booking> beforeOrEqualToFrom(LocalDate from){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("stayDateFrom"), from);};
	}
		
	//get after from date
	public static Specification<Booking> afterOrEqualToTo(LocalDate to){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("stayDateTo"), to);};
	}
			
	//get only before date
	public static Specification<Booking> beforeOrEqualToTo(LocalDate to){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("stayDateTo"), to);};
	}
		
	
	public static Specification<Booking> getOverlappingBookings(Booking newBooking){
		return Specification
			.where(byBungalowId(newBooking.getBungalowId())
			.and((afterOrEqualToFrom(newBooking.getStayDateFrom()).and(beforeOrEqualToTo(newBooking.getStayDateTo()))
				.or(beforeOrEqualToFrom(newBooking.getStayDateFrom()).and(afterOrEqualToTo(newBooking.getStayDateTo())))
				.or(afterOrEqualToFrom(newBooking.getStayDateFrom())).and(beforeOrEqualToFrom(newBooking.getStayDateTo()))
				.or((afterOrEqualToTo(newBooking.getStayDateFrom())).and(beforeOrEqualToTo(newBooking.getStayDateTo()))))));
	}
		
	//get only before date
	public static Specification<Booking> getBooksByFilter(BookingFilter filter){
		List<Specification<Booking>> specificationList = new ArrayList<>();
		if(filter.getId()!=0) {
			specificationList.add(byId(filter.getId()));
		}
		if(filter.getBungalowId()!=0) {
			specificationList.add(byBungalowId(filter.getBungalowId()));
		}
		if(filter.getNights()!=0) {
			specificationList.add(byNights(filter.getNights()));				
		}
		if(filter.getMinNights()!=0) {
			specificationList.add(minNights(filter.getMinNights()));
		}
		if(filter.getMaxNights()!=0) {
			specificationList.add(maxNights(filter.getMaxNights()));
		}
		if(filter.getStayDateFrom()!=null) {
			specificationList.add(afterOrEqualToFrom(filter.getStayDateFrom()));
		}
		if(filter.getStayDateTo()!=null) {
			specificationList.add(beforeOrEqualToTo(filter.getStayDateTo()));
		}
		if(filter.getPrice()!=0) {
			specificationList.add(byPrice(filter.getPrice()));		
		}
		if(filter.getMinPrice()!=0) {
			specificationList.add(minPrice(filter.getMinPrice()));
		}
		if(filter.getMaxPrice()!=0) {
			specificationList.add(maxPrice(filter.getMaxPrice()));
		}
			
		return Specification.allOf(specificationList);
	}
}
