package com.developer.silverheavens.exceptions;

@SuppressWarnings("serial")
public class XlsCreationException extends RuntimeException {
	public XlsCreationException(String message) {
		super("Cannot parse data : "+message);
	}
}
