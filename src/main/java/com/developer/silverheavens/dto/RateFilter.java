package com.developer.silverheavens.dto;

import java.time.LocalDate;


public class RateFilter {

	/*FIELDS*/
	private int id;
	private LocalDate stayDateFrom;
	private LocalDate stayDateTo;
	private int nights;
	private int minNights;
	private int MaxNights;
	private int value;
	private int minValue;
	private int maxValue;
	private int bungalowId;
	private LocalDate closedDate;
	private boolean closedRates;
	private boolean activeRates;
	

	/*GETTER SETTER*/
	public int getMinValue() {
		return minValue;
	}
	public void setMinValue(int minValue) {
		this.minValue = minValue;
	}
	public boolean isActiveRates() {
		return activeRates;
	}
	public void setActiveRates(boolean activeRates) {
		this.activeRates = activeRates;
	}
	public int getMaxValue() {
		return maxValue;
	}
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
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
	public int getNights() {
		return nights;
	}
	public void setNights(int nights) {
		this.nights = nights;
	}
	public int getValue() {
		return value;
	}
	public void setValue(int value) {
		this.value = value;
	}
	public int getBungalowId() {
		return bungalowId;
	}
	public void setBungalowId(int bungalowId) {
		this.bungalowId = bungalowId;
	}
	public LocalDate getClosedDate() {
		return closedDate;
	}
	public void setClosedDate(LocalDate closedDate) {
		this.closedDate = closedDate;
	}
	public boolean isClosedRates() {
		return closedRates;
	}
	public void setClosedRates(boolean closedRates) {
		this.closedRates = closedRates;
	}
	
	public int getMinNights() {
		return minNights;
	}
	public void setMinNights(int minNights) {
		this.minNights = minNights;
	}
	public int getMaxNights() {
		return MaxNights;
	}
	public void setMaxNights(int maxNights) {
		MaxNights = maxNights;
	}
	
	/*TO STRING*/
	@Override
	public String toString() {
		return "RateFilter [id=" + id + ", stayDateFrom=" + stayDateFrom + ", stayDateTo=" + stayDateTo + ", nights="
				+ nights + ", minNights=" + minNights + ", MaxNights=" + MaxNights + ", value=" + value + ", minValue="
				+ minValue + ", maxValue=" + maxValue + ", bungalowId=" + bungalowId + ", closedDate=" + closedDate
				+ ", closedRates=" + closedRates + "]";
	}
	
	
	

}
