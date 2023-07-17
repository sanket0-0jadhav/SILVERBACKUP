package com.developer.silverheavens.specifications;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.jpa.domain.Specification;

import com.developer.silverheavens.dto.RateFilter;
import com.developer.silverheavens.entities.Booking;
import com.developer.silverheavens.entities.Rate;

import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

public interface RateSpecification {

	//get with id
	public static Specification<Rate> byRateId(int rateId){
		return new Specification<Rate>() {
			private static final long serialVersionUID = 1L;

			@Override
			public Predicate toPredicate(Root<Rate> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
				return criteriaBuilder.equal(root.get("id"), rateId);
			}
		};
	}
	
	//get based on nights
	public static Specification<Rate> byNights(int nights){
		return (root,query,cb)->{return cb.equal(root.get("nights"), nights);};
	}
	
	//get based on nights
	public static Specification<Rate> minNights(int nights){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("nights"), nights);};
	}
	
	//get based on nights
	public static Specification<Rate> maxNights(int nights){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("nights"), nights);};
	}
	
	//get by value
	public static Specification<Rate> byValue(int value){
		return (root,query,cb)->{return cb.equal(root.get("value"), value);};
	}
		
	//get with min val
	public static Specification<Rate> hasMinValue(int value){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("value"), value);};
	}
	
	//get with max value
	public static Specification<Rate> hasMaxValue(int value){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("value"), value);};
	}
	
	//get by bungalow id
	public static Specification<Rate> byBungalowId(int bungalowId){
		return (root,query,cb)->{return cb.equal(root.get("bungalowId"), bungalowId);};
	}
	
	//get after from date
	public static Specification<Rate> afterOrEqualToFrom(LocalDate from){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("stayDateFrom"), from);};
	}
	
	//get only before date
	public static Specification<Rate> beforeOrEqualToFrom(LocalDate from){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("stayDateFrom"), from);};
	}
	
	//get after from date
	public static Specification<Rate> afterOrEqualToTo(LocalDate to){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("stayDateTo"), to);};
	}
		
	//get only before date
	public static Specification<Rate> beforeOrEqualToTo(LocalDate to){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("stayDateTo"), to);};
	}
	
	//get all active rates
	public static Specification<Rate> notClosed(){
		return (root,query,cb)->{return cb.isNull(root.get("closedDate"));};
	}
	
	//get all active rates
	public static Specification<Rate> closed(){
		return (root,query,cb)->{return cb.isNotNull(root.get("closedDate"));};
	}
	
	//active
	public static Specification<Rate> active(){
		return (root,query,cb)->{return cb.isNull(root.get("closedDate"));};
	}
	
	//get closed on
	public static Specification<Rate> closedOn(LocalDate closedDate){
		return (root,query,cb)->{return cb.equal(root.get("closedDate"), closedDate);};
	}
		
	//get closed before
	public static Specification<Rate> closedBefore(LocalDate closedDate){
		return (root,query,cb)->{return cb.lessThanOrEqualTo(root.get("closedDate"), closedDate);};
	}
	
	//get closed after
	public static Specification<Rate> closedAfter(LocalDate closedDate){
		return (root,query,cb)->{return cb.greaterThanOrEqualTo(root.get("stayDateTo"), closedDate);};
	}
	
	
	
	//Get Required rates FOR NEW RATE
	public static Specification<Rate> getRatesForInsertion(Rate newRate){
		return Specification.where(byBungalowId(newRate.getBungalowId())
				.and(notClosed()
				.and((beforeOrEqualToFrom(newRate.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newRate.getStayDateTo().minusDays(1))))
					.or(afterOrEqualToFrom(newRate.getStayDateFrom().plusDays(1)).and(beforeOrEqualToTo(newRate.getStayDateTo().minusDays(1))))
					.or(beforeOrEqualToFrom(newRate.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newRate.getStayDateFrom().minusDays(1))))
					.or(afterOrEqualToFrom(newRate.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newRate.getStayDateTo().minusDays(1))))
				).and(byNights(newRate.getNights()))));
	}
	
	//Get Required rates FOR BOOKING
	public static Specification<Rate> getRatesForInsertion(Booking newBooking){
		return Specification.where(byBungalowId(newBooking.getBungalowId())
				.and(notClosed()
				.and((beforeOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newBooking.getStayDateTo().minusDays(1))))
					.or(afterOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(beforeOrEqualToTo(newBooking.getStayDateTo().minusDays(1))))
					.or(beforeOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newBooking.getStayDateFrom().minusDays(1))))
					.or(afterOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newBooking.getStayDateTo().minusDays(1))))
				).and(byNights(newBooking.getNights()))));
	}
	
	//Get Required rates FOR BOOKING
//	public static Specification<Rate> getRatesForOptimisedInsertion(Booking newBooking){
//		return Specification.where(byBungalowId(newBooking.getBungalowId())
//				.and(notClosed()
//				.and((beforeOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newBooking.getStayDateTo().minusDays(1))))
//					.or(afterOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(beforeOrEqualToTo(newBooking.getStayDateTo().minusDays(1))))
//					.or(beforeOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newBooking.getStayDateFrom().minusDays(1))))
//					.or(afterOrEqualToFrom(newBooking.getStayDateFrom().plusDays(1)).and(afterOrEqualToTo(newBooking.getStayDateTo().minusDays(1))))
//				)));
//	}
	
	//Get Required rates BASED ON FILTER
	public static Specification<Rate> getRatesByFilter(RateFilter filter){
		List<Specification<Rate>> resultSpecList = new ArrayList<>();
		
		if(filter.getId()!=0) {
			resultSpecList.add(byRateId(filter.getId()));
		}
		if (filter.getStayDateFrom()!=null) {
			resultSpecList.add(afterOrEqualToFrom(filter.getStayDateFrom()));
		}
		if (filter.getStayDateTo()!=null) {
			resultSpecList.add(beforeOrEqualToTo(filter.getStayDateTo()));
		}
		if (filter.getNights()!=0) {
			resultSpecList.add(byNights(filter.getNights()));
		}
		if (filter.getMinNights()!=0) {
			resultSpecList.add(minNights(filter.getMinNights()));
		}
		if (filter.getMaxNights()!=0) {
			resultSpecList.add(maxNights(filter.getMaxNights()));
		}
		if (filter.getValue()!=0) {
			resultSpecList.add(byValue(filter.getValue()));
		}
		if (filter.getMinValue()!=0) {
			resultSpecList.add(hasMinValue(filter.getMinValue()));
		}
		if (filter.getMaxValue()!=0) {
			resultSpecList.add(hasMaxValue(filter.getMaxValue()));
		}
		if (filter.getBungalowId()!=0) {
			resultSpecList.add(byBungalowId(filter.getBungalowId()));
		}
		if (filter.getClosedDate()!=null) {
			resultSpecList.add(closedOn(filter.getClosedDate()));
		}
		if (filter.isClosedRates()) {
			resultSpecList.add(closed());
		}
		if (filter.isActiveRates()) {
			resultSpecList.add(active());
		}
		
		return Specification.allOf(resultSpecList);
		
	}
	
}
