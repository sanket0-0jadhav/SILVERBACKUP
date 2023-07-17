package com.developer.silverheavens.util;

import java.util.Collections;
import java.util.List;

import com.developer.silverheavens.entities.Rate;

public class RateInternalMerger {
	private List<Rate> unmergedRateList;
	
	public RateInternalMerger(List<Rate> unmergedRateList) {
		this.unmergedRateList = unmergedRateList;
		 mergeRates();
	}

	//check for any merging 
	public List<Rate> getMergedList(){
		return unmergedRateList;
	}
	private void mergeRates() {
		//SORT
		Collections.sort(unmergedRateList);
		int i=0;
		while(i<unmergedRateList.size()) {
			//variable to store current and next to check mergin
			Rate currentRateSegment;
			Rate nextRateSegment;
				
			//break look if there is no next element 
			if(i+1>=unmergedRateList.size()) {
				break;
			}
			//get reference to rates
			currentRateSegment = unmergedRateList.get(i);
			nextRateSegment = unmergedRateList.get(i+1);
			//check if merge able
			if(currentRateSegment.getStayDateTo().plusDays(1).isEqual(nextRateSegment.getStayDateFrom())
					&& currentRateSegment.getValue()==nextRateSegment.getValue()
					&& currentRateSegment.getClosedDate()==null 
					&& nextRateSegment.getClosedDate()==null) {
				
				//Update current.to date			
				/*CHANGES BEGIN*/
				currentRateSegment.setStayDateTo(nextRateSegment.getStayDateTo());
				/*CHANGES END*/
				unmergedRateList.remove(nextRateSegment);
				i=0;
			}else {
				i++;
			}
		}
		Collections.sort(unmergedRateList);
	}
	
}
