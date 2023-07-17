package com.developer.silverheavens.service;


import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.developer.silverheavens.dto.RateFilter;
import com.developer.silverheavens.entities.Bungalow;
import com.developer.silverheavens.entities.Rate;
import com.developer.silverheavens.exceptions.IdNotFoundException;
import com.developer.silverheavens.exceptions.ValidationException;
import com.developer.silverheavens.exceptions.UpdateException;
import com.developer.silverheavens.exceptions.XlsCreationException;
import com.developer.silverheavens.repository.BungalowRepo;
import com.developer.silverheavens.repository.RateRepository;
import com.developer.silverheavens.specifications.BungalowSpecification;
import com.developer.silverheavens.specifications.RateSpecification;
import com.developer.silverheavens.util.RateInternalMerger;
import com.developer.silverheavens.util.RateSplitter;
import com.developer.silverheavens.util.RateUtil;
import com.developer.silverheavens.util.XlsMaker;
import com.developer.silverheavens.util.XlsParser;
import jakarta.transaction.Transactional;
import validators.CustomValidator;
import validators.ValidatorResult;

@Service
@Transactional
public class RateService {

	/*fields*/
	@Autowired
	private RateRepository rateRepo;
	@Autowired
	private BungalowRepo bungalowRepo;
	//@Autowired
	//private EntityManager entityManager;
	
	//send all rates OR active Rates
	public List<Rate> getRates(RateFilter filter,Pageable pageable) {
		
		Page<Rate> ratesDataPage = rateRepo.findAll(RateSpecification.getRatesByFilter(filter),pageable);
		List<Rate> rateList = ratesDataPage.getContent();
		return rateList;
	}
	
	//add new Rate
	public boolean addNewRate(Rate newRate,Rate oldRefForUpdate) {
		/*Validation*/
		ValidatorResult result = CustomValidator.validateNewRate(newRate);
		if(!result.isValidationPassed()) {
			throw new ValidationException(result.getMessage().toString());
		}
		
		//check if bungalow exists
		Optional<Bungalow> bungalowOptional = bungalowRepo.findOne(BungalowSpecification.withId(newRate.getBungalowId()));
		if(bungalowOptional.isEmpty())
			throw new IdNotFoundException(Bungalow.class, newRate.getBungalowId());
		
		/*VAR*/
		List<Rate> allRatesList = rateRepo.findAll(RateSpecification.getRatesForInsertion(newRate));
		//Remove if called from update
		if(oldRefForUpdate!=null)
			allRatesList.remove(oldRefForUpdate);
		
		List<Rate> affectedRatesList;			//=====> Holds ref of rates from data which will shrink/expand/close
		List<Rate> newGeneratedRatesList;		//=====> Holds ref of rates that are newly created (1/2/3)
		ArrayList<Rate> historyRateList;		//=====> Holds ref of rates created to close rates after we shrink/expand
		ArrayList<Rate> mergableRateList;		//=====> Holds ref of rates from DB which can be merged
		boolean isMerging = false;
		
		Collections.sort(allRatesList);
		//redundant insert
		if(RateUtil.isRedundant(newRate, allRatesList)) {
			return true;
		}
		
		//if no data present : Insert
		if(allRatesList.size()==0) {
			rateRepo.save(newRate);
			return true;
		}
		
		//check if merge able
		mergableRateList = RateUtil.checkIfMergeable(allRatesList, newRate);
		isMerging = mergableRateList.size()!=0;
		
		/*SPLITTING*/ 
		RateSplitter splitter = new RateSplitter(newRate,allRatesList);
		
		//get new Rates to insert
		newGeneratedRatesList = splitter.getNewRatesList();
		affectedRatesList = splitter.getAffectedRates();
		
		//internal merger
		RateInternalMerger internalMerge = new RateInternalMerger(newGeneratedRatesList);
		newGeneratedRatesList = internalMerge.getMergedList();
		
		//COREMR CASE
		if(affectedRatesList.size()==1 && newGeneratedRatesList.size()==3) {
			//SAVE TO DB
			newGeneratedRatesList.forEach((r)->{
				rateRepo.save(r);
			});
			
			//SAVE CLOSED HOSTORY
			affectedRatesList.forEach((r)->{
				r.setClosedDate(LocalDate.now());
				rateRepo.save(r);
			});
			return true;
		}
	
		//get Rates to close after shrinking/expanding
		historyRateList = RateUtil.shrinkOrExpandAffectedRates(newGeneratedRatesList,affectedRatesList);
		
		//PREPARE DATA FOR DB = close affected + add new 
		ArrayList<Rate> ratesToSaveInDb = new ArrayList<>();
		ArrayList<Rate> closedRatesToUpdateInDb = new ArrayList<>();
		
		/* NEW GENERATED - TO SAVE IN DB
		 * AFFECTEDARTELIST will have rates that are shrunk,expanded/closed
		 * */
		ratesToSaveInDb.addAll(newGeneratedRatesList);
		affectedRatesList.forEach((r)->{
			if(r.getClosedDate()==null) {
				ratesToSaveInDb.add(r);			//SHOULD update in DB because it is shrunk/expanded
			}else {
				closedRatesToUpdateInDb.add(r); //SHOULD CLOSE in DB because it did not shrunk/expanded
			}
		});
		
		//MERGE
		if(isMerging) {
			//get closed rates after merging
			closedRatesToUpdateInDb.addAll(RateUtil.MergeExternal(ratesToSaveInDb, mergableRateList));
		}
		
		/*----------------DB OPERATIONS----------------*/
		//close and update rates
		closedRatesToUpdateInDb.forEach((r)->{                          										//->>>>>>>
				System.out.println("CLOSING : "+r);
				r.setClosedDate(LocalDate.now());
				rateRepo.save(r);										
		});																
		
		//SAVE TO DB
		Iterator<Rate> dataForDbIterator = ratesToSaveInDb.iterator();
		while(dataForDbIterator.hasNext()) {
			Rate r = dataForDbIterator.next();
			rateRepo.save(r);
			System.out.println("SAVE : "+r);
			//entityManager.clear();
		}
		
		//SAVE CLOSED HOSTORY
		Iterator<Rate> historyRateListIterator = historyRateList.iterator();
		while(historyRateListIterator.hasNext()) {
			Rate r = historyRateListIterator.next();

			r.setClosedDate(LocalDate.now());
			rateRepo.save(r);
			System.out.println("ENTER CLOSED : "+r);
			//entityManager.clear();
		}
		
		
		//handle if called by update
		if(oldRefForUpdate!=null) {
			/*
			 * If called by update methods, we have ref of entry from database which is being updated.
			 * If this id which is being updated is not in closed or in new generated: then explicitly
			 * close it
			 * */
			boolean updated = false;
			updated = Stream.concat(ratesToSaveInDb.stream(), ratesToSaveInDb.stream())
					.filter((r)->r.getId()==oldRefForUpdate.getId())
					.findFirst()
					.isPresent();
		
			if(!updated) {
				oldRefForUpdate.setClosedDate(LocalDate.now());
				System.out.println("AD>>>>"+oldRefForUpdate);
				rateRepo.save(oldRefForUpdate);
				//entityManager.clear();
			}
		}
		return true;
	}
	
	//UPDATE
	public boolean updateRate(Rate updateRate) {
		/*Validation*/
		ValidatorResult result = CustomValidator.validateNewRate(updateRate);
		if(!result.isValidationPassed()) {
			throw new ValidationException(result.getMessage().toString());
		}
		
		//check if bungalow exists
		Optional<Bungalow> bungalowOptional = bungalowRepo.findOne(BungalowSpecification.withId(updateRate.getBungalowId()));
		if(bungalowOptional.isEmpty())
			throw new IdNotFoundException(Bungalow.class, updateRate.getBungalowId());
		
		/*----ID IS DELETED----*/
		//Get from Optional
		Optional<Rate> refFromDbOptions = rateRepo.findOne(RateSpecification.byRateId(updateRate.getId()));
		
		//get by 
		if(refFromDbOptions.isEmpty()) {
			throw new IdNotFoundException(Rate.class, updateRate.getId());
		}
		
		Rate refFromDb = refFromDbOptions.get();
		
		if(refFromDb.getClosedDate()!=null) {
			throw new UpdateException(updateRate.getId(),"Rate is already closed");
		}
		
		//delete from DB
		//rateRepo.delete(refFromDb);
		//entityManager.clear();
		
		updateRate.setId(refFromDb.getId());
		
		Rate clonedFromRefFromDb = new Rate();
		clonedFromRefFromDb.setBungalowId(refFromDb.getBungalowId());
		clonedFromRefFromDb.setValue(refFromDb.getValue());
		clonedFromRefFromDb.setId(refFromDb.getId());
		clonedFromRefFromDb.setNights(refFromDb.getNights());
		clonedFromRefFromDb.setStayDateFrom(refFromDb.getStayDateFrom());
		clonedFromRefFromDb.setStayDateTo(refFromDb.getStayDateTo());;
		
		return this.addNewRate(updateRate,clonedFromRefFromDb);
		
	}
	
	//DELETE
	public boolean deleteRate(int rateId) {
		//Get from Optional
		Optional<Rate> refFromDbOptions = rateRepo.findOne(RateSpecification.byRateId(rateId));
				
		//get by 
		if(refFromDbOptions.isEmpty()) {
			throw new IdNotFoundException(Rate.class, rateId);
		}
		
		Rate refFromDb = refFromDbOptions.get();
		
		if(refFromDb.getClosedDate()!=null) {
			throw new UpdateException(rateId,"Rate is already closed");
		}
		
		//delete from DB
		rateRepo.delete(refFromDb);
		//entityManager.clear();
		
		return true;
	}
	
	//EXPORT DATE
	
	/*EXPORT*/
	public Workbook exportData(RateFilter filter, Pageable pageable) {
		//get data to export
		List<Rate> dataList = getRates(filter,pageable);
		Workbook workbook;
		
		//make excel
		try {
			XlsMaker<Rate> xlsMaker = new XlsMaker<>(dataList);
			workbook = xlsMaker.getWorkbook();
		}catch(Exception ex) {
			throw new XlsCreationException(ex.getMessage());
		}
		
		System.out.println(dataList);
		//return workbook
		return workbook;
		
	}
	
	//IMPORT DATA
	public String importData(MultipartFile file) throws Exception{
		//extract rate from xls
		XlsParser parser = new XlsParser(file);
		StringBuilder errorMessage = new StringBuilder();
		
		List<Rate> ratesFromExcel = parser.getRateDataList();
		List<Integer> parsingErrorsRows = parser.getParsingErrorsRows();
		
		errorMessage.append(parsingErrorsRows.isEmpty()?"":parsingErrorsRows.toString());
		//iterate and add
		ratesFromExcel.forEach((r)->{
			try {
				//if id=0 :: NEW
				if(r.getId()==0) {
					addNewRate(r,null);
				}else {
					//if closed date == null & id !=0 :: UPDATE 
					if(r.getClosedDate()==null)
						updateRate(r);
				}
			}catch(Exception ex) {
				errorMessage.append(", For Entry {"+r+"} --> "+ex.getMessage());
			}
		});
		
		return errorMessage.toString();
	}
}
