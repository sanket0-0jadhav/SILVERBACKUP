package com.developer.silverheavens.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.developer.silverheavens.entities.Rate;

public class RateSplitter {
	/*FIELDS*/
	private Rate newRate;
	private Rate rateAffectedByNewFrom;
	private Rate rateAffectedByNewTo;
	private ArrayList<Rate> affectedSegments;
	private ArrayList<Rate> finalNewRateSegments;
	private List<Rate> inputSegment;
	
	/*CTOR*/
	public RateSplitter(Rate newRate,List<Rate> inputSegment) {
		this.newRate = newRate;
		this.inputSegment = inputSegment;
		this.affectedSegments = new ArrayList<Rate>();	 
		finalNewRateSegments = processSegments();
	}
	
	/*---give affected rate list ----*/
	public List<Rate> getAffectedRates(){
		return affectedSegments;
	}
	
	/*---give final rate list ----*/
	public ArrayList<Rate> getNewRatesList(){
		return finalNewRateSegments;
	}
	
	


	/*--METHOD--*/
	private ArrayList<Rate> processSegments() {
		//sort
		Collections.sort(inputSegment);
		
		inputSegment.forEach((r)->{
			if(isAffected(r)) {
				affectedSegments.add(r);
			}
		});
		
		//set rates affected by first and last segment
		SetRatesToSplit();
		
		//get 3 new rates 
		return getNewRatesToInsert();
		
	}
	
	//get (0,3) new segments
	private void SetRatesToSplit(){
		LocalDate newFrom = newRate.getStayDateFrom();
		LocalDate newTo = newRate.getStayDateTo();
		
		affectedSegments.forEach((r)->{
			//Rate rateUnderObservation = r;
			LocalDate fromDateUnderObservation = r.getStayDateFrom();
			LocalDate toDateUnderObservation = r.getStayDateTo();
			
			//get first affected segment
			if((newFrom.isEqual(fromDateUnderObservation) || newFrom.isAfter(fromDateUnderObservation))
				&&(newFrom.isEqual(toDateUnderObservation) || newFrom.isBefore(toDateUnderObservation))) {
				rateAffectedByNewFrom = r;
			}
			//get last affected segment
			if((newTo.isEqual(fromDateUnderObservation) || newTo.isAfter(fromDateUnderObservation))
				&&(newTo.isEqual(toDateUnderObservation) || newTo.isBefore(toDateUnderObservation))) {
				rateAffectedByNewTo = r;
			}
		});
		
	}
	
	//get new Rate Segments to insert
	private ArrayList<Rate> getNewRatesToInsert(){
		//variables
		ArrayList<Rate> result = new ArrayList<>();
		
		//make 1st segment
		if(rateAffectedByNewFrom!=null) {
			Rate firstSegment = new Rate();
			
			firstSegment.setNights(newRate.getNights());
			firstSegment.setStayDateFrom(rateAffectedByNewFrom.getStayDateFrom());
			firstSegment.setStayDateTo(newRate.getStayDateFrom().minusDays(1));
			firstSegment.setValue(rateAffectedByNewFrom.getValue());
			firstSegment.setBungalowId(newRate.getBungalowId());
			firstSegment.setClosedDate(null);
			
			//if(firstSegment.getStayDateTo().isAfter(firstSegment.getStayDateFrom())) {
			if(!firstSegment.getStayDateTo().isBefore(firstSegment.getStayDateFrom())) {
				result.add(firstSegment);				
			}
		}
		
		//last segment
		if(rateAffectedByNewTo!=null) {
			Rate lastSegment = new Rate();

			lastSegment.setNights(newRate.getNights());
			lastSegment.setStayDateFrom(newRate.getStayDateTo().plusDays(1));
			lastSegment.setStayDateTo(rateAffectedByNewTo.getStayDateTo());
			lastSegment.setValue(rateAffectedByNewTo.getValue());
			lastSegment.setBungalowId(newRate.getBungalowId());
			lastSegment.setClosedDate(null);
			
			//if(lastSegment.getStayDateTo().isAfter(lastSegment.getStayDateFrom())) {
			if(!lastSegment.getStayDateTo().isBefore(lastSegment.getStayDateFrom())) {
				result.add(lastSegment);				
			}
			
		}
		//add  mid rate
		result.add(newRate);
		return result;
	}
	
	// get segments that are affected 
 	private boolean isAffected(Rate rateSegment) {
		LocalDate newRateFrom = newRate.getStayDateFrom();
		LocalDate newRateTo = newRate.getStayDateTo();
		
		//start = oldFrom/oldTo
		if(newRateFrom.isEqual(rateSegment.getStayDateFrom()) || newRateFrom.isEqual(rateSegment.getStayDateTo())) {
			return true;
		}
		
		//start in segment 
		if(newRateFrom.isAfter(rateSegment.getStayDateFrom()) && newRateFrom.isBefore(rateSegment.getStayDateTo())) {
			return true;
		}
		
		//end = oldFrom/oldTo
		if(newRateTo.isEqual(rateSegment.getStayDateFrom()) || newRateTo.isEqual(rateSegment.getStayDateTo())) {
			return true;
		}
				
		//end in segment -
		if(newRateTo.isAfter(rateSegment.getStayDateFrom()) && newRateTo.isBefore(rateSegment.getStayDateTo())) {
			return true;
		}
		
		//check if new is whole included in old
		if(newRateFrom.isAfter(rateSegment.getStayDateFrom()) && newRateTo.isBefore(rateSegment.getStayDateTo())) {
			return true;
		}
		
		//check if old is whole included in new
		if(newRateFrom.isBefore(rateSegment.getStayDateFrom()) && newRateTo.isAfter(rateSegment.getStayDateTo())) {
			return true;
		}
		
		
		return false;
		
	}

 	
	
}
