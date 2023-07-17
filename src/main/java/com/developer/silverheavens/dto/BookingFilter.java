package com.developer.silverheavens.dto;

import java.time.LocalDate;


public class BookingFilter {
	private int id;
	private int bungalowId;
	private int nights;
	private int minNights;
	private int maxNights;
	private LocalDate stayDateFrom;
	private LocalDate stayDateTo;
	private float price;
	private float minPrice;
	private float maxPrice;
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBungalowId() {
		return bungalowId;
	}
	public void setBungalowId(int bungalowId) {
		this.bungalowId = bungalowId;
	}
	public int getNights() {
		return nights;
	}
	public void setNights(int nights) {
		this.nights = nights;
	}
	public int getMinNights() {
		return minNights;
	}
	public void setMinNights(int minNights) {
		this.minNights = minNights;
	}
	public int getMaxNights() {
		return maxNights;
	}
	public void setMaxNights(int maxNights) {
		this.maxNights = maxNights;
	}
	public LocalDate getStayDateFrom() {
		return stayDateFrom;
	}
	public void setStayDateFrom(LocalDate stayDateFrom) {
		this.stayDateFrom = stayDateFrom;
	}
	public LocalDate getStayDateTo() {
		return stayDateTo;
	}
	public void setStayDateTo(LocalDate stayDateTo) {
		this.stayDateTo = stayDateTo;
	}
	public float getPrice() {
		return price;
	}
	public void setPrice(float price) {
		this.price = price;
	}
	public float getMinPrice() {
		return minPrice;
	}
	public void setMinPrice(float minPrice) {
		this.minPrice = minPrice;
	}
	public float getMaxPrice() {
		return maxPrice;
	}
	public void setMaxPrice(float maxPrice) {
		this.maxPrice = maxPrice;
	}
	@Override
	public String toString() {
		return "BookingFilter [id=" + id + ", bungalowId=" + bungalowId + ", nights=" + nights + ", minNights="
				+ minNights + ", maxNights=" + maxNights + ", stayDateFrom=" + stayDateFrom + ", stayDateTo="
				+ stayDateTo + ", price=" + price + ", minPrice=" + minPrice + ", maxPrice=" + maxPrice + "]";
	}
	
	
}
