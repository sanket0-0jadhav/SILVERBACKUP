package com.developer.silverheavens.util;

import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import org.apache.poi.ss.formula.eval.EvaluationException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import com.developer.silverheavens.entities.Rate;

public class XlsParser {

	/*fields*/
	private List<Rate> rateDataList = new ArrayList<>();
	private List<Integer> parsingErrorsRows = new ArrayList<>();
	private MultipartFile file;

	/*GETTER*/
	public List<Rate> getRateDataList() {
		return rateDataList;
	}
	public List<Integer> getParsingErrorsRows() {
		return parsingErrorsRows;
	}
	
	/*Ctor*/
	public XlsParser(MultipartFile file) throws Exception {
		this.file=file;
		parseXls();
	}

	private void parseXls() throws IOException, EvaluationException {
		//build workbook
		Workbook workbook = new XSSFWorkbook(file.getInputStream());
		Sheet sheet = workbook.getSheetAt(0);
		
		Iterator<Row> rowIterator = sheet.iterator();
		//SKIP first as it is "ID" - "...."
		if(rowIterator.hasNext()) 
			rowIterator.next();
			
		//iterate over all rows
		while(rowIterator.hasNext()) {
			Row r = rowIterator.next();
			Rate rateFromXls = new Rate();
			LocalDate fromDate,toDate,closedDate;
			int id,night,value,bungalowId;
			
			if(r.getCell(0).getCellType()==CellType.BLANK || r.getCell(1).getCellType()==CellType.BLANK 
				|| r.getCell(2).getCellType()==CellType.BLANK || r.getCell(3).getCellType()==CellType.BLANK 
				|| r.getCell(4).getCellType()==CellType.BLANK) {
				parsingErrorsRows.add(r.getRowNum());
			}
			
			try {
				//ID	STAY_DATE_FROM	STAY_DATE_TO	NIGHTS	VALUE	BUNGALOW_ID	 CLOSED_DATE
				// 0		1				2			  3		   4		5			6
				
				id = getInteger(r.getCell(0));
				fromDate = getLocalDate(r.getCell(1));
				toDate = getLocalDate(r.getCell(2));
				night = getInteger(r.getCell(3));
				value = getInteger(r.getCell(4));
				bungalowId = getInteger(r.getCell(5));
				closedDate = getLocalDate(r.getCell(6));
	
				rateFromXls.setId(id);
				rateFromXls.setStayDateFrom(fromDate);
				rateFromXls.setStayDateTo(toDate);
				rateFromXls.setNights(night);
				rateFromXls.setValue(value);
				rateFromXls.setBungalowId(bungalowId);
				rateFromXls.setClosedDate(closedDate);
				
				rateDataList.add(rateFromXls);
			}catch(Exception ex) {
				parsingErrorsRows.add(r.getRowNum());
			}
		}
		
	}
	
	private LocalDate getLocalDate(Cell c) throws EvaluationException {
		//if cell has null written
		if(c.getCellType()==CellType.STRING && c.getStringCellValue().equals("null")) {
			return null;
		}

		Date date = c.getDateCellValue();
		@SuppressWarnings("deprecation")
		LocalDate localDate = LocalDate.of(date.getYear()+1900,date.getMonth()+1,date.getDate());
		
		return localDate;
	}
	
	private int getInteger(Cell c) {
		return (int) c.getNumericCellValue();
	}
}
