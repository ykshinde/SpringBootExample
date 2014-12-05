package com.ykshinde.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ykshinde.domain.CustomerOrders;
import com.ykshinde.domain.Order;
import com.ykshinde.service.CustomerService;

@RestController
public class OrderController {

	@Resource
	CustomerService service;

       
    @RequestMapping(method=RequestMethod.POST, value="/Order/{customerId}")
    public ResponseEntity<String> postCustomer(@PathVariable("customerId") String customerId, @RequestBody Order order) {

    	Map<String, List<Order>> customerOrderMap = service.getCustomerOrderMap();
    	
    	List<Order> orderList = customerOrderMap.get(customerId);
    	if(orderList == null) {
    		orderList = new ArrayList<Order>();
    		customerOrderMap.put(customerId, orderList);
    	} 
    	
    	orderList.add(order);
    	
        return new ResponseEntity<String>("Added", HttpStatus.CREATED); 
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/CustomerOrder/{customerId}")
    public CustomerOrders getCustomerOrders(@PathVariable("customerId") String customerId) {

    	Map<String, List<Order>> customerOrderMap = service.getCustomerOrderMap();
    	
    	List<Order> orderList = customerOrderMap.get(customerId);
    	
    	CustomerOrders customerOrders = new CustomerOrders();
    	
    	customerOrders.setCustomerOrders(orderList);
    	
        return customerOrders; 
    }
   
}
