package com.developer.silverheavens.util;

import java.time.LocalDate;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import com.developer.silverheavens.entities.Booking;
import com.developer.silverheavens.entities.Rate;

public class BookingUtil {
	
	public static float calculatePrice(Booking newBooking,List<Rate> ratesList) {
		//fields
		//long totalDays = newBooking.getStayDateTo().toEpochDay() - newBooking.getStayDateFrom().toEpochDay();
		float price = 0;
		LocalDate bookingFrom = newBooking.getStayDateFrom(); 
		LocalDate bookingTo = newBooking.getStayDateTo(); 
		//calculate price
		Collections.sort(ratesList);
		//get rates required for calculation
		Rate rateAffectedByFrom = getRateAffectedByDate(bookingFrom,ratesList);
		Rate rateAffectedByTo = getRateAffectedByDate(bookingTo,ratesList);
		List<Rate> ratesInBetweenFromTo = getRangeAffectedByDate(bookingFrom,bookingTo,ratesList);
		
		//remove any repetition
		if(rateAffectedByFrom!=null)
			ratesInBetweenFromTo.remove(rateAffectedByFrom);
		if(rateAffectedByTo!=null)
			ratesInBetweenFromTo.remove(rateAffectedByTo);
		if(rateAffectedByFrom!=null && rateAffectedByTo!=null && rateAffectedByFrom==rateAffectedByTo)
			rateAffectedByTo = null;
		
		//calculate price
		price += getPriceByFirstSegment(bookingFrom,bookingTo, rateAffectedByFrom);
		price += getPriceByLastSegment(bookingTo, rateAffectedByTo);
		price += getPriceByRange(ratesInBetweenFromTo);
		
		//if booking for one day
		if(price==0)
			throw new RuntimeException("LOGICAL ERROR : calculatePrice");
		
		return price;
	}
	
	//get rate affected by from date
	private static Rate getRateAffectedByDate(LocalDate date,List<Rate> ratesList) {
		Optional<Rate> rateOptional = ratesList.stream().filter((r)->{
			return ((date.isAfter(r.getStayDateFrom()) || date.isEqual(r.getStayDateFrom()))&&
					(date.isBefore(r.getStayDateTo()) || date.isEqual(r.getStayDateTo())));
		}).findFirst();
		
		if(rateOptional.isEmpty())
			throw new RuntimeException("LOGICAL ERROR : getRatesForBookingFrom");
		return rateOptional.get();
	}
	
	//get rate affected by from & to
	private static List<Rate> getRangeAffectedByDate(LocalDate fromDate, LocalDate toDate,List<Rate> ratesList) {
		List<Rate> rateSegmentsInRange = ratesList
				.stream()
				.filter((r)->{
			return ((r.getStayDateFrom().isAfter(fromDate) || r.getStayDateFrom().isEqual(fromDate))
					&& (r.getStayDateTo().isBefore(toDate) || r.getStayDateTo().isEqual(toDate)));})
				//.toList();
				.collect(Collectors.toList());
		
		return rateSegmentsInRange;
	}
	
	//get price in first segment
	private static float getPriceByFirstSegment(LocalDate from,LocalDate to,Rate rateSegment) {
		long days = 0;
		if(rateSegment==null)
			return 0;
		else if(to.isBefore(rateSegment.getStayDateTo())) {
			days =  to.toEpochDay()-from.toEpochDay();
		}else {
			days = rateSegment.getStayDateTo().toEpochDay()-from.toEpochDay();			
		}
		System.out.println("IN FIRST : "+days);
		//if to is after + 1
		days = days + (to.isAfter(rateSegment.getStayDateTo())?1:0);
		System.out.println("CONSIDERING SKIP : "+days);
		float price = ((days==0?1:days)/rateSegment.getNights())*rateSegment.getValue();
		//return rateSegment.getValue()*(days==0?1:days);
		return price;
	}
	
	//get price in last segment
	private static float getPriceByLastSegment(LocalDate to,Rate rateSegment) {
		if(rateSegment==null)
			return 0;
		long days = to.toEpochDay()-rateSegment.getStayDateFrom().toEpochDay();
		System.out.println("IN LAST : "+days);
		float price = (days/rateSegment.getNights())*rateSegment.getValue();
		return price;
	}
	
	//get price in range
	private static float getPriceByRange(List<Rate> rateSegmentList) {
		if(rateSegmentList.size()==0)
			return 0;
		
		long price = 0;
		Iterator<Rate> itr = rateSegmentList.iterator();	
		while(itr.hasNext()){
			Rate r = itr.next();
			long days = r.getStayDateTo().toEpochDay()-r.getStayDateFrom().toEpochDay()+1;
			System.out.println("DAYS IN "+r+" SEGMENT : "+days);
			float p = (days/r.getNights())*r.getValue();
			//price += days*r.getValue();
			price += p;
		}
		
		return price;
	}
}
