package com.developer.silverheavens.controller;

import java.util.Date;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.Workbook;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.developer.silverheavens.dto.RateFilter;
import com.developer.silverheavens.dto.ResponseDto;
import com.developer.silverheavens.dto.ResponseStatus;
import com.developer.silverheavens.entities.Rate;
import com.developer.silverheavens.exceptions.XlsCreationException;
import com.developer.silverheavens.service.RateService;

import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/rates")
public class RateController {
	
	/*FIELDS*/
	@Autowired
	private RateService rateService;
	
	private static final Logger logger =  LogManager.getLogger("MyLoggerOne");
	
	//To get rates as per filter
	@GetMapping
	public ResponseEntity<ResponseDto<List<Rate>>> getActiveRates(RateFilter filter,Pageable pageable){
		logger.info("Get rates request : "+filter);
		List<Rate> responseRates = rateService.getRates(filter,pageable);
			
		//create response
		ResponseDto<List<Rate>> resp = new ResponseDto<List<Rate>>(ResponseStatus.SUCCESS,responseRates,null);
		return new ResponseEntity<ResponseDto<List<Rate>>>(resp,HttpStatus.OK);
	}
	
	//create and insert a new rate entry
	@PostMapping
	public ResponseEntity<ResponseDto<String>> createNewRate(@RequestBody Rate newRate){
		logger.info("Request for adding new rate : "+newRate);
		newRate.setId(0);
		
		if(rateService.addNewRate(newRate,null)) {
			logger.info("Added new rate : "+newRate);
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Created new rate!",null),HttpStatus.CREATED);
		}else {
			logger.info("Add new rate failed : "+newRate);
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.FAIL,"Error in inserting.",null),HttpStatus.OK);
		}
		
	}
	
	//update an existing rate 
	@PatchMapping
	public ResponseEntity<ResponseDto<String>> updateRate(@RequestBody Rate updateRate){
		logger.info("Request for updating rate : "+updateRate);
		if(rateService.updateRate(updateRate)) {
			logger.info("Updated : "+updateRate);
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Updated!.",null),HttpStatus.OK);
		}else{
			logger.info("Update failed : "+updateRate);
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.FAIL,null,"Error in updating."),HttpStatus.OK);
		} 
	}
	
	//delete an existing rate 
	@DeleteMapping("/{rateId}")
	public ResponseEntity<ResponseDto<String>> deleteRate(@PathVariable int rateId){
		logger.info("Request for deleting rate : "+rateId);
		if(rateService.deleteRate(rateId)) {		
			logger.info("Deleted : "+rateId);
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Deleted!.",null),HttpStatus.OK);			
		}else {
			logger.info("Delete failed : "+rateId);
			return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.FAIL,null,"Error in Deleting."),HttpStatus.OK);
		}
	}
	
	//importing data from excel and inserting in Db
	@PostMapping("/import")
	public ResponseEntity<ResponseDto<String>> importRate(@RequestParam MultipartFile file){
		logger.info("Import request : "+file.getName());
		try {
			String errorWhileImporting = rateService.importData(file);
			if(!errorWhileImporting.isEmpty()) {				
				logger.info("Import Successful : "+file.getName());
				return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Data imported! (Errors : "+errorWhileImporting+")",null),HttpStatus.CREATED);
			}else {
				logger.info("Import failed : "+file.getName());
				return new ResponseEntity<ResponseDto<String>>(new ResponseDto<String>(ResponseStatus.SUCCESS,"Data imported!",null),HttpStatus.CREATED);
			}
		}catch (Exception ex) {
			logger.info("Exception while Import : "+ex);
			throw new XlsCreationException(ex.getMessage());
		}
	}
	

	//Exporting Database data to excel
	@GetMapping("/export")
	public ResponseEntity<?> exportRate(RateFilter filter, Pageable pageable,HttpServletResponse response){
		logger.info("Export request : "+filter);
		//prepare response
		response.setContentType("application/octet-stream");
		String headerKey = "Content-Disposition";
        String headerValue = "attachment; filename=rates" + new Date() + ".xlsx";
        response.setHeader(headerKey, headerValue);
		try {
			
			Workbook workbook = rateService.exportData(filter,pageable);
			ServletOutputStream outputStream = response.getOutputStream();
	        workbook.write(outputStream);
	        workbook.close();
	        outputStream.close();
	        logger.info("Export Successfully.");
	        return new ResponseEntity<String>("Done",HttpStatus.OK);
		} catch (Exception e) {
			logger.info("Export Failed : "+e);
			throw new XlsCreationException(e.getMessage());
		}
	}
	
	
	
	
}
