package com.ykshinde.domain;

import java.util.ArrayList;
import java.util.List;

public class Customer {
	private String name;
	
	private String city;
	
	public Customer() {
	}
	
	public Customer(String name, String city) {
		this.name = name;
		this.city = city;
	}
	
	public void setName(String name) {
		this.name = name;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return city;
	}

	public String getName() {
		return name;
	}

}
