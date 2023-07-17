package com.developer.silverheavens.entities;

import java.time.LocalDate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@Entity
@Table(name = "booking")
public class Booking {

	//fields
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "booking_seq")
	@SequenceGenerator(name = "booking_seq",allocationSize = 1,sequenceName = "booking_seq") 
	private int id;
	
	@Column(name = "bungalow_id")
	private int bungalowId;
	
	@Column(name = "nights")
	private int nights;

	@Column(name = "stay_date_from")
	private LocalDate stayDateFrom;
	
	@Column(name = "stay_date_to")
	private LocalDate stayDateTo;
	
	@Column(name = "price")
	private float price;
	
	//getter setter
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getBungalowId() {
		return bungalowId;
	}
	public int getNights() {
		return nights;
	}
	public void setNights(int nights) {
		this.nights = nights;
	}
	public void setBungalowId(int bungalowId) {
		this.bungalowId = bungalowId;
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
	
	//CTOR
	public Booking() {
		super();
	}
	@Override
	public String toString() {
		return "Booking [id=" + id + ", bunglowId=" + bungalowId + ", nights=" + nights + ", stayDateFrom="
				+ stayDateFrom + ", stayDateTo=" + stayDateTo + ", price=" + price + "]";
	}
	
	
	

	
}
