package com.developer.silverheavens.exceptions;

@SuppressWarnings("serial")
public class UpdateException extends RuntimeException {
	public UpdateException(int id,String message) {
		super("Cannot Update {"+id+"} : "+message);
	}
}
