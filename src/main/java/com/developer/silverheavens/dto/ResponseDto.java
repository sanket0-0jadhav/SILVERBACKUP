package com.developer.silverheavens.dto;

public class ResponseDto<T> {

	/*FIELDS*/
	private ResponseStatus status;
	private T data;
	private String error;
	
	/*GET-SET*/
	public ResponseStatus getStatus() {
		return status;
	}
	public void setStatus(ResponseStatus status) {
		this.status = status;
	}
	public T getData() {
		return data;
	}
	public void setData(T data) {
		this.data = data;
	}
	public String getError() {
		return error;
	}
	public void setError(String error) {
		this.error = error;
	}
	
	/*CTOR*/
	public ResponseDto(ResponseStatus status, T data, String error) {
		super();
		this.status = status;
		this.data = data;
		this.error = error;
	}
	public ResponseDto() {
		super();
	}
	
	
	
}
