package com.ykshinde.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.ykshinde.domain.Customer;
import com.ykshinde.domain.Order;

@Service
public class CustomerService {
	
	Map<String, Customer> customerMap = new HashMap<String, Customer>();
	
	Map<String, List<Order>> customerOrderMap = new HashMap<String, List<Order>>();

	public Map<String, Customer> getCustomerMap() {
		return customerMap;
	}

	public void setCustomerMap(Map<String, Customer> customerMap) {
		this.customerMap = customerMap;
	}

	public Map<String, List<Order>> getCustomerOrderMap() {
		return customerOrderMap;
	}

	public void setCustomerOrderMap(Map<String, List<Order>> customerOrderMap) {
		this.customerOrderMap = customerOrderMap;
	}
	
	
}
