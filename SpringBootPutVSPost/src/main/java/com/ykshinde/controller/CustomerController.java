package com.ykshinde.controller;

import java.util.Map;

import javax.annotation.Resource;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ykshinde.domain.Customer;
import com.ykshinde.service.CustomerService;

@RestController
public class CustomerController {


	@Resource
	CustomerService service;
	
    @RequestMapping(method=RequestMethod.PUT, value="/Customer/{customerId}.json")
    public ResponseEntity<Customer> addCustomer(@PathVariable("customerId") String customerId, @RequestBody Customer customer) {
        System.out.println(customer.getName() +"  "+customer.getCity());
        
        Map<String, Customer> customerMap = service.getCustomerMap();
        System.out.println("Sleeeping");        
        try {
            Thread.sleep(2700);                 
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Woke up");
        if(customerMap.containsKey(customerId)) {
        	customerMap.put(customerId, customer);
        	return new ResponseEntity<Customer>(customer, HttpStatus.OK);
        } else {
        	customerMap.put(customerId, customer);
        	return new ResponseEntity<Customer>(customer, HttpStatus.CREATED);
        }
    }
    
    @RequestMapping(method=RequestMethod.POST, value="/Customer/{customerId}.json")
    public ResponseEntity<String> postCustomer(@PathVariable("customerId") String customerId, @RequestBody Customer customer) {
        System.out.println(customer.getName() +"  "+customer.getCity());
        
        Map<String, Customer> customerMap = service.getCustomerMap();
        
        System.out.println("Sleeeping");        
        try {
            Thread.sleep(1000);                 
        } catch(InterruptedException ex) {
            Thread.currentThread().interrupt();
        }
        System.out.println("Woke up");
        
        customerMap.put(customerId, customer);
    	
        return new ResponseEntity<String>("", HttpStatus.OK); 
    }
    
    @RequestMapping(method=RequestMethod.PATCH, value="/Customer/{customerId}")
    public ResponseEntity<String> patchCustomer(@PathVariable("customerId") String customerId, @RequestBody Customer customer) {
        System.out.println(customer.getName() +"  "+customer.getCity());
        
        Map<String, Customer> customerMap = service.getCustomerMap();
        customerMap.put(customerId, customer);
    	
        return new ResponseEntity<String>("", HttpStatus.CREATED); 
    }
    
    @RequestMapping(method=RequestMethod.GET, value="/Customer/{customerId}.json")
    public Customer getCustomer(@PathVariable String customerId) {
    	System.out.println("customerId : "+customerId);
    	
    	Map<String, Customer> customerMap = service.getCustomerMap();
    	
    	return customerMap.get(customerId);
    }
    

}
