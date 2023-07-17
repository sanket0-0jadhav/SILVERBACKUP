package com.developer.silverheavens.util;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import com.developer.silverheavens.entities.Rate;

public class RateUtil {

	//check is merge able
	public static ArrayList<Rate> checkIfMergeable(List<Rate> allRatesList,Rate newRate) {
		//deciding splitting or merging
		
		ArrayList<Rate> mergableRateList = new ArrayList<>();
		
		for(Rate r : allRatesList) {
			LocalDate rFrom = r.getStayDateFrom();
			LocalDate rTo = r.getStayDateTo();
			
			if(newRate.getValue()==r.getValue()) {
				if(rTo.plusDays(1).isEqual(newRate.getStayDateFrom())){
					//return true;
					mergableRateList.add(r);
				}
				if(rFrom.minusDays(1).isEqual(newRate.getStayDateTo())){
					mergableRateList.add(r);
				}
			}
		}
		return mergableRateList;
	}

	//expand / shrink affected rates according to new rates
	public static ArrayList<Rate> shrinkOrExpandAffectedRates(List<Rate> newGeneratedRateList,List<Rate> affectedRatesList){
		ArrayList<Rate> historicalRates = new ArrayList<Rate>();
		
		for(int i=0;i<affectedRatesList.size();i++) {
			Rate affectedRate = affectedRatesList.get(i);
			boolean isAffectedRateUpdated=false;
			
			for(int j=0;j<newGeneratedRateList.size();j++) {
				Rate generatedRate = newGeneratedRateList.get(j);
				//if rate is not same skip
				if(affectedRate.getValue()!=generatedRate.getValue() || generatedRate.getId()==-1) {
					continue;
				}
				//if from is same; extends and get history rates
				if(affectedRate.getStayDateFrom().isEqual(generatedRate.getStayDateFrom())) {
					if(affectedRate.getStayDateTo().isAfter(generatedRate.getStayDateTo())) {
						//SHRINK AFFECTED
						historicalRates.add(shrinkAffected(affectedRate, generatedRate,true));
						//mark new for remove
						generatedRate.setId(-1);
						isAffectedRateUpdated=true;
					}else if(affectedRate.getStayDateTo().isBefore(generatedRate.getStayDateTo())) {
						//EXPAND affected
						expandAffected(affectedRate, generatedRate,true);
						//mark new for removal
						generatedRate.setId(-1);
						isAffectedRateUpdated=true;
					}else {
						//affectedRate.setValue(generatedRate.getValue());
						System.out.println("UNHANDLED==========================>RATEUTIL-65");
					}
				}
				//if to is same; extends and give history rates
				else if(affectedRate.getStayDateTo().isEqual(generatedRate.getStayDateTo())) {
					if(affectedRate.getStayDateFrom().isBefore(generatedRate.getStayDateFrom())) {
						//SHRINK AFFECTED
						historicalRates.add(shrinkAffected(affectedRate, generatedRate,false));
						//mark new for removal;
						generatedRate.setId(-1);
						isAffectedRateUpdated=true;
					}else if(affectedRate.getStayDateFrom().isAfter(generatedRate.getStayDateFrom())) {
						//EXPAND AFFECTED
						expandAffected(affectedRate, generatedRate,false);
						//mark new for removal;
						generatedRate.setId(-1);
						isAffectedRateUpdated=true;
					}else {
						//affectedRate.setValue(generatedRate.getValue());
						System.out.println("UNHANDLED==========================>RATEUTIL-84");
					}
				}else {
					System.out.println(">>>>> RATEUTIL-98");
				}
				
			}
			
			if(!isAffectedRateUpdated) {
				affectedRate.setClosedDate(LocalDate.now());
			}
		}
		
		//remove marked from newGenerated
		newGeneratedRateList.removeIf((r)->r.getId()==-1);
		
		return historicalRates;
	}
	
	//MERGE -- MERGE WITH OLD, REMOVE NEW
	public static ArrayList<Rate> MergeExternal(ArrayList<Rate> dataForDB,ArrayList<Rate> mergableRateList) {
		ArrayList<Rate> updateClosedDate = new ArrayList<>();
		dataForDB.addAll(mergableRateList);
		//System.out.println("DATE BF MERGE : "+dataForDB);
		Collections.sort(dataForDB);
		
		int i=0;
		
		while(i<dataForDB.size()) {
			//if next not available stop
			if(i+1>=dataForDB.size()) {
				break;
			}
			//get current and next
			Rate currentRate = dataForDB.get(i);
			Rate nextRate = dataForDB.get(i+1);
			//is rate not same skip
			if(currentRate.getValue()!=nextRate.getValue() 
					|| currentRate.getClosedDate()!=null
					|| nextRate.getClosedDate()!=null) {
				i++;
				continue;
			}
			
			//if to and from are side by side
			if(currentRate.getStayDateTo().plusDays(1).isEqual(nextRate.getStayDateFrom())) {
				//update current rate
				if(currentRate.getId()!=0) {
					//if CURRENT is not new:Update CURRENT remove NEXT 
					currentRate.setStayDateTo(nextRate.getStayDateTo());
					updateClosedDate.add(dataForDB.remove(i+1));
				}else if(nextRate.getId()!=0) {
					//if NEXT is not new:Update NEXT remove CURRENT 
					nextRate.setStayDateFrom(currentRate.getStayDateFrom());
					updateClosedDate.add(dataForDB.remove(i));
				}else {
					System.out.println("SOS : UNHANDLED COND @ RATEUTIL-153");
				}

				i=0;
			}else {
				i++;
			}
		}
		//System.out.println("DATE AF MERGE : "+dataForDB);
		updateClosedDate.removeIf((r)->r.getId()==0);
		return updateClosedDate;
	}
	
	//check if insert is redundant 
	public static boolean isRedundant(Rate newRate,List<Rate> dataFromDb) {
		LocalDate newFrom = newRate.getStayDateFrom();
		LocalDate newTo = newRate.getStayDateTo();
		
		//get filtered
		Optional<Rate> rateOptional = dataFromDb.stream()
		.filter((r)->r.getValue()==newRate.getValue())
		.filter((r)->{
			LocalDate rFrom = r.getStayDateFrom();
			LocalDate rTo = r.getStayDateTo();
			
			if(newFrom.isEqual(rFrom) && newTo.isEqual(rTo)) {
				return true;
			}else if((newFrom.isAfter(rFrom) || newFrom.isEqual(rFrom)) && (newTo.isBefore(rTo) || newTo.isEqual(rTo))) {
				return true;
			}
			return false;})
		.findFirst();
		
		//if any matches, redundant
		return rateOptional.isPresent();
	}
	
	//shrink
	private static Rate shrinkAffected(Rate affected,Rate generated,boolean isFromSame) {
		Rate historyRate = new Rate();
		//populate history
		historyRate.setBungalowId(generated.getBungalowId());
		historyRate.setClosedDate(null);
		historyRate.setNights(generated.getNights());
		//decide how to modify 
		if(isFromSame) {
			historyRate.setStayDateFrom(generated.getStayDateTo().plusDays(1));
			historyRate.setStayDateTo(affected.getStayDateTo());
		}else {
			historyRate.setStayDateFrom(affected.getStayDateFrom());
			historyRate.setStayDateTo(generated.getStayDateFrom().minusDays(1));
		}
		historyRate.setValue(affected.getValue());
		
		//update affected
		if(isFromSame) {
			affected.setStayDateTo(generated.getStayDateTo());
		}else {
			affected.setStayDateFrom(generated.getStayDateFrom());
			
		}
		//System.out.println("After shrink "+affected+"X"+generated);
		return historyRate;
	}
	
	//expand
	private static void expandAffected(Rate affected,Rate generated,boolean isFromSame) {
		if(isFromSame) {
			affected.setStayDateTo(generated.getStayDateTo());
		}else {
			affected.setStayDateFrom(generated.getStayDateFrom());
		}
	}
	
}
