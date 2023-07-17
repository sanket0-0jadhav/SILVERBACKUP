package com.developer.silverheavens.exceptions;

@SuppressWarnings("serial")
public class IdNotFoundException extends RuntimeException {

	public IdNotFoundException(Class<?> entityClass,int id) {
		super("ID {"+id+"} NOT FOUND FOR : "+entityClass.getSimpleName());
	}
	
}
