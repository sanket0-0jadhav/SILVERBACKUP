package com.developer.silverheavens.entities;

import java.io.Serializable;
import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "rate")
public class Rate implements Serializable,Comparable<Rate>{

	/*FIELDS*/
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "RATE_SEQ")
	@SequenceGenerator(name = "RATE_SEQ",allocationSize = 1,sequenceName = "RATE_SEQ") 
	private int id;
	
	@Column(name = "stay_date_from",nullable = false)
	private LocalDate stayDateFrom;
	
	@Column(name = "stay_date_to",nullable = false)
	private LocalDate stayDateTo;
	
	@Column(name = "nights",nullable = false)
	private int nights;
	
	@Column(name = "value",nullable = false)
	private int value;
	
	@Column(name = "bungalow_id",nullable = false)
	private int bungalowId;
	
//	@ManyToOne(cascade = CascadeType.ALL)
//	private Bungalow bungalow;
	
	@Column(name = "closed_date",nullable = true)
	private LocalDate closedDate;

	/*CTOR*/
	public Rate() {
		super();
	}
	
	/*GET-SET*/
	
	
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
	public void setNights(int night) {
		this.nights = night;
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
//	public Bungalow getBungalow() {
//		return bungalow;
//	}
//	public void setBungalow(Bungalow bungalow) {
//		this.bungalow = bungalow;
//	}
	public LocalDate getClosedDate() {
		return closedDate;
	}
	

	public void setClosedDate(LocalDate closedDate) {
		this.closedDate = closedDate;
	}
	
	@Override
	public int compareTo(Rate o1) {
		return this.getStayDateFrom().compareTo(o1.getStayDateFrom());
	}

	@Override
	public String toString() {
		return "Rate [id=" + id + ", stayDateFrom=" + stayDateFrom + ", stayDateTo=" + stayDateTo + ", nights=" + nights
				+ ", value=" + value + ", closedDate=" + closedDate + "]\n";
	}

	public Rate(int id, LocalDate stayDateFrom, LocalDate stayDateTo, int nights, int value, int bungalowId,
			LocalDate closedDate) {
		super();
		this.id = id;
		this.stayDateFrom = stayDateFrom;
		this.stayDateTo = stayDateTo;
		this.nights = nights;
		this.value = value;
		this.bungalowId = bungalowId;
		this.closedDate = closedDate;
	}
	
	@Override
	public boolean equals(Object obj) {
		Rate objRate = (Rate)obj;
		return id==objRate.getId() 
				&& nights == objRate.getNights()
				&& value == objRate.getValue()
				&& bungalowId == objRate.getBungalowId()
				&& stayDateFrom.isEqual(objRate.getStayDateFrom())
				&& stayDateTo.isEqual(objRate.getStayDateTo());
			//	&& closedDate.isEqual(objRate.getClosedDate());
	}
	
	@Override
	public int hashCode() {
		return id+nights+value+bungalowId+stayDateFrom.hashCode()+stayDateTo.hashCode();
	}
	

	

	
}
