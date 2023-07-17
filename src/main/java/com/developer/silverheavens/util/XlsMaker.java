package com.developer.silverheavens.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDate;
import java.util.List;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CreationHelper;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class XlsMaker<T> {
	//fields
	List<T> dataList;
	Workbook workbook;
	Class<? extends Object> paramClass;
	
	//getter setter
	public Workbook getWorkbook() {
		return workbook;
	}
	
	/*CTOR*/
	public XlsMaker(List<T> data) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		dataList = data;
		if(data==null || data.isEmpty()) {
			throw new RuntimeException("DATA CANNOT BE NULL OR EMPTY");
		}
		paramClass =  data.get(0).getClass();
		generateXml();
	}

	/*METHOD*/
	private void generateXml() throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		//create work book
		workbook = new XSSFWorkbook();
		Sheet sheet = workbook.createSheet();
		
		//set headers
		setHeaderRow(sheet);
		
		populateData(sheet);

	}
	
	//set row titles
	private void setHeaderRow(Sheet sheet) {
		sheet.setDefaultColumnWidth(12);
		//create header row
		Row headerRow =  sheet.createRow(0);
		
		//iterate over fields and get name
		Field fields[] = paramClass.getDeclaredFields();
		for(int i=0;i<fields.length;i++) {
			Cell c = headerRow.createCell(i);
			c.setCellValue(fields[i].getName());
		}
	}
	
	//set data
	private void populateData(Sheet sheet) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, SecurityException {
		int rowCounter=1;
		for(T r:dataList){
			Row dataRow =  sheet.createRow(rowCounter);
			Field fields[] = paramClass.getDeclaredFields();
			for(int i=0;i<fields.length;i++) {
				//get field name
				String fieldName = fields[i].getName();
				//make getter
				String methodName = "get"+fieldName.substring(0, 1).toUpperCase()+fieldName.substring(1);
				//make getter method 
				Method getter = r.getClass().getDeclaredMethod(methodName);
				//call getter method
				Object valueObject = getter.invoke(r);
				
				Cell c = dataRow.createCell(i);
				if(valueObject==null) {
					c.setCellValue("null");
				}else if(valueObject instanceof Integer) {
					c.setCellValue(Integer.parseInt(valueObject.toString()));
				}else if(valueObject instanceof String) {
					c.setCellValue(valueObject.toString());
				}else if(valueObject instanceof LocalDate) {
					LocalDate ld = (LocalDate)(valueObject);
					CreationHelper createHelper = workbook.getCreationHelper();
					CellStyle cellStyle     = workbook.createCellStyle();
					cellStyle.setDataFormat(createHelper.createDataFormat().getFormat("yyyy-MM-dd")); 
					c.setCellStyle(cellStyle);
					c.setCellValue(ld);
				}else {
					throw new RuntimeException("AT XLS MAKER 94");
				}
				
//				String value = "";
//				if(valueObject==null) {
//					value="null";
//				}else {
//					value = valueObject.toString();
//				}
//				dataRow.createCell(i).setCellValue(value);
			}
			//create next row
			rowCounter++;
		};
	}
}
