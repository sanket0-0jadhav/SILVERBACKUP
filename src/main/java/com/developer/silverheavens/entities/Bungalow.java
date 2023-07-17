package com.developer.silverheavens.entities;

import java.io.Serializable;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.SequenceGenerator;
import jakarta.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "bungalow_details")
public class Bungalow implements Serializable{

	/*FIELDS*/
	@Id
	@Column(name = "id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE,generator = "BUNGALOW_DETAILS_SEQ")
	@SequenceGenerator(name = "BUNGALOW_DETAILS_SEQ",allocationSize = 1,sequenceName = "BUNGALOW_DETAILS_SEQ") 
	private int id;
	
	@Column(name = "bungalow_name", length = 25,nullable = false)
	private String name;
	
	@Column(name = "bungalow_type", length = 25,nullable = false)
	private String bungalowType;
	
	/*CTOR*/
	public Bungalow() {
		super();
	}
	
	/*GET-SET*/
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBungalowType() {
		return bungalowType;
	}
	public void setBungalowType(String bungalowType) {
		this.bungalowType = bungalowType;
	}
	
}
