package com.developer.silverheavens;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.mockito.Mockito;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import com.developer.silverheavens.entities.Rate;
import com.developer.silverheavens.repository.RateRepository;
import com.developer.silverheavens.specifications.BungalowSpecification;
import com.developer.silverheavens.specifications.RateSpecification;
import com.developer.silverheavens.util.RateUtil;

import validators.CustomValidator;
import validators.ValidatorResult;

@SpringBootTest
public class RateServiceTest {
	
	@MockBean
	public RateRepository rateRepoMock;
	
//	public void prepMockData() {
//		//Prepare mock
//		Mockito.when(rateRepoMock.findAll()).thenReturn(getAllMockRates());
//		Mockito.when(rateRepoMock.findAll(RateSpecification.closedIsNull())).thenReturn(getActiveMockRates());
//		//rateRepo.findOne(RateSpecification.byRateId(rateId));
//		//rateRepo.findAll(RateSpecification.byBungalowId(bungalowId));
//		//rateRepo.findOne(RateSpecification.byRateId(rateId));
//	}
	
	@DisplayName("Check If Mergable")
	@Test
	public void testCheckIfMergeable() {
		Rate r1 =  new Rate(1,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 02, 29),1,3000,100,null);
		Rate r2 =  new Rate(2,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 03, 31),1,4000,100,null);
		Rate r3 =  new Rate(3,LocalDate.of(2024, 04, 01),LocalDate.of(2024, 04, 30),1,3000,100,null);
		Rate r4 =  new Rate(4,LocalDate.of(2024, 05, 01),LocalDate.of(2024, 05, 31),1,5000,100,null);
		Rate r5 =  new Rate(5,LocalDate.of(2024, 06, 01),LocalDate.of(2024, 06, 30),1,3000,100,null);
		
		List<Rate> allRates = List.of(r1,r2,r3,r4,r5);
		
		Rate newRate = new Rate(0,LocalDate.of(2023, 12, 15),LocalDate.of(2023, 12, 31),1,3000,100,null);
		assertEquals(List.of(r1),RateUtil.checkIfMergeable(allRates,newRate));
		
		newRate = new Rate(0,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 03, 31),1,3000,100,null);
		assertEquals(List.of(r1,r3),RateUtil.checkIfMergeable(allRates,newRate));
		
		newRate = new Rate(0,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 05, 15),1,3000,100,null);
		assertEquals(List.of(r1),RateUtil.checkIfMergeable(allRates,newRate));
		
		newRate = new Rate(0,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 05, 31),1,3000,100,null);
		assertEquals(List.of(r1,r5),RateUtil.checkIfMergeable(allRates,newRate));
		
		newRate = new Rate(0,LocalDate.of(2024, 06, 01),LocalDate.of(2024, 06, 30),1,5000,100,null);
		assertEquals(List.of(r4),RateUtil.checkIfMergeable(allRates,newRate));
	}
	
	@DisplayName("Shrink / expand")
	@Test
	public void testProcessAffectedRates() {
		//List<Rate> newGeneratedRateList   -- -- --   List<Rate> affectedRatesList
		Rate r1 =  new Rate(1,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 02, 29),1,3000,100,null);
		
		Rate nr1 =  new Rate(0,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 02, 15),1,3000,100,null);

		List<Rate> affectedRateList = new ArrayList<>();
		affectedRateList.add(r1);
		List<Rate> newGeneratedList = new ArrayList<>();
		affectedRateList.add(nr1);
		
		Assertions.assertEquals(new ArrayList<>(), 
				RateUtil.shrinkOrExpandAffectedRates(newGeneratedList, affectedRateList));
	}
	
	@Test
	public void testMergeExternal() {
		Rate r1 =  new Rate(1,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 02, 29),1,3000,100,null);
	
		Rate nr1 = new Rate(0,LocalDate.of(2023, 12, 15),LocalDate.of(2023, 12, 31),1,3000,100,null);

		ArrayList<Rate> mergable = new ArrayList<>(List.of(r1));
		ArrayList<Rate> dataForDb = new ArrayList<>(List.of(nr1));
		
		assertEquals(new ArrayList<Rate>(), RateUtil.MergeExternal(dataForDb, mergable));
	}
	
	@DisplayName("Test If Redundent")
	@Test
	public void testIsRedundant() {
		Rate r1 =  new Rate(1,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 02, 29),1,3000,100,null);
		Rate r2 =  new Rate(2,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 03, 31),1,4000,100,null);
		Rate r3 =  new Rate(3,LocalDate.of(2024, 04, 01),LocalDate.of(2024, 04, 30),1,3000,100,null);
		Rate r4 =  new Rate(4,LocalDate.of(2024, 05, 01),LocalDate.of(2024, 05, 31),1,5000,100,null);
		Rate r5 =  new Rate(5,LocalDate.of(2024, 06, 01),LocalDate.of(2024, 06, 30),1,3000,100,null);
		
		List<Rate> dataList = List.of(r1,r2,r3,r4,r5);
		
		Rate nr1 = new Rate(0,LocalDate.of(2024, 01, 05),LocalDate.of(2024, 02, 25),1,3000,100,null);
		Rate nr2 = new Rate(0,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 03, 31),1,4000,100,null);
		Rate nr3 = new Rate(0,LocalDate.of(2024, 04, 01),LocalDate.of(2024, 04, 01),1,3000,100,null);
		Rate nr4 = new Rate(0,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 03, 31),1,3000,100,null);
		Rate nr5 = new Rate(0,LocalDate.of(2024, 06, 01),LocalDate.of(2024, 06, 30),1,5000,100,null);
		
		assertTrue(RateUtil.isRedundant(nr1,dataList));
		assertTrue(RateUtil.isRedundant(nr2,dataList));
		assertTrue(RateUtil.isRedundant(nr3,dataList));
		assertFalse(RateUtil.isRedundant(nr4,dataList));
		assertFalse(RateUtil.isRedundant(nr5,dataList));
		
		
		
	}
	
	
	@DisplayName("ValidationTest")
	@Test
	public void testIsValid() {
		Rate r1 = new Rate(0,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 12, 31),1,3000,100,null);
		Rate r2 = new Rate(0,LocalDate.of(2024, 03, 01),LocalDate.of(2024, 04, 30),1,3000,100,null);
		Rate r3 = new Rate(0,LocalDate.of(2024, 01, 01),LocalDate.of(2023, 12, 31),1,3000,100,null);
		Rate r4 = new Rate(0,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 12, 31),0,3000,100,null);
		Rate r5 = new Rate(0,LocalDate.of(2024, 01, 01),LocalDate.of(2024, 12, 31),1,0,100,null);
		
		assertTrue(CustomValidator.validateNewRate(r1).isValidationPassed());
		assertTrue(CustomValidator.validateNewRate(r2).isValidationPassed());
		assertFalse(CustomValidator.validateNewRate(r3).isValidationPassed());
		assertFalse(CustomValidator.validateNewRate(r4).isValidationPassed());
		assertFalse(CustomValidator.validateNewRate(r5).isValidationPassed());
	}
	
//	@Test
//	public void test() {
//		
//		
//		Assertions.assertNotNull(rateRepoMock.findAll());
//		
//	}
	
	
	
//	///////////////
//	private List<Rate> getAllMockRates(){
//		Rate r0 = new Rate();
//		r0.setId(1);
//		r0.setBungalowId(100);
//		r0.setClosedDate(LocalDate.of(2024, 01, 15));
//		r0.setNights(1);
//		r0.setStayDateFrom(LocalDate.of(2024, 01, 01));
//		r0.setStayDateTo(LocalDate.of(2024, 12, 31));
//		r0.setValue(3000);
//		Rate r1 = new Rate();
//		r1.setId(2);
//		r1.setBungalowId(100);
//		r1.setClosedDate(null);
//		r1.setNights(1);
//		r1.setStayDateFrom(LocalDate.of(2024, 01, 01));
//		r1.setStayDateTo(LocalDate.of(2024, 02, 29));
//		r1.setValue(3000);
//		Rate r2 = new Rate();
//		r2.setId(3);
//		r2.setBungalowId(100);
//		r2.setClosedDate(null);
//		r2.setNights(1);
//		r2.setStayDateFrom(LocalDate.of(2024, 3, 01));
//		r2.setStayDateTo(LocalDate.of(2024, 3, 31));
//		r2.setValue(4000);
//		Rate r3 = new Rate();
//		r3.setId(4);
//		r3.setBungalowId(100);
//		r3.setClosedDate(null);
//		r3.setNights(1);
//		r3.setStayDateFrom(LocalDate.of(2024, 04, 01));
//		r3.setStayDateTo(LocalDate.of(2024, 04, 30));
//		r3.setValue(3000);
//		Rate r4 = new Rate();
//		r4.setId(5);
//		r4.setBungalowId(100);
//		r4.setClosedDate(null);
//		r4.setNights(1);
//		r4.setStayDateFrom(LocalDate.of(2024, 05, 01));
//		r4.setStayDateTo(LocalDate.of(2024, 05, 31));
//		r4.setValue(5000);
//		return List.of(r0,r1,r2,r3,r4);
//	}
	
//	private List<Rate> getActiveMockRates(){
//		Rate r1 = new Rate();
//		r1.setId(1);
//		r1.setBungalowId(100);
//		r1.setClosedDate(null);
//		r1.setNights(1);
//		r1.setStayDateFrom(LocalDate.of(2024, 01, 01));
//		r1.setStayDateTo(LocalDate.of(2024, 02, 29));
//		r1.setValue(3000);
//		Rate r2 = new Rate();
//		r2.setId(1);
//		r2.setBungalowId(100);
//		r2.setClosedDate(null);
//		r2.setNights(1);
//		r2.setStayDateFrom(LocalDate.of(2024, 3, 01));
//		r2.setStayDateTo(LocalDate.of(2024, 3, 31));
//		r2.setValue(4000);
//		Rate r3 = new Rate();
//		r3.setId(1);
//		r3.setBungalowId(100);
//		r3.setClosedDate(null);
//		r3.setNights(1);
//		r3.setStayDateFrom(LocalDate.of(2024, 04, 01));
//		r3.setStayDateTo(LocalDate.of(2024, 04, 30));
//		r3.setValue(3000);
//		Rate r4 = new Rate();
//		r4.setId(1);
//		r4.setBungalowId(100);
//		r4.setClosedDate(null);
//		r4.setNights(1);
//		r4.setStayDateFrom(LocalDate.of(2024, 05, 01));
//		r4.setStayDateTo(LocalDate.of(2024, 05, 31));
//		r4.setValue(5000);
//		return List.of(r1,r2,r3,r4);
//	}
}
