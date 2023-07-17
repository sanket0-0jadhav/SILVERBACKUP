package com.developer.silverheavens.controller;


import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.developer.silverheavens.dto.ResponseDto;
import com.developer.silverheavens.dto.ResponseStatus;

@ControllerAdvice
public class UniversalExceptionhandler {
	
	//UNCAUGHT EXCEPTION
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ResponseDto<String>> exception(Exception ex){
		ResponseDto<String> resp = new ResponseDto<String>(ResponseStatus.FAIL, null,ex.getMessage());
		return new ResponseEntity<ResponseDto<String>>(resp,HttpStatus.BAD_REQUEST);
	}
}
