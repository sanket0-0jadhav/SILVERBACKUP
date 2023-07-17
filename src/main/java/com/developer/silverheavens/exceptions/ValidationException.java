package com.developer.silverheavens.exceptions;

@SuppressWarnings("serial")
public class ValidationException extends RuntimeException{
	public ValidationException(String message) {
		super("Validation failed. Reason : "+message);
	}
}
