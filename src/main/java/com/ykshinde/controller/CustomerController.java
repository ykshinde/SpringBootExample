package com.ykshinde.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.ykshinde.domain.Customer;

@RestController
public class CustomerController {

    private static final String template = "Hello, %s!";
    private final AtomicLong counter = new AtomicLong();

    List<Customer> customerList = new ArrayList<Customer>();
    
    @RequestMapping("/getAllCustomer")
    public List<Customer> getAllCustomer() {
        
    	
    	customerList.add(new Customer("Yogesh", "Hadapsar"));
    	customerList.add(new Customer("Taks", "Sanghavi"));
    	customerList.add(new Customer("Mithun", "Aundh"));
    	customerList.add(new Customer("Yogesh", "Hadapsar"));
    	
    	
    	return customerList;
    }
    
    @RequestMapping(method=RequestMethod.PUT, value="/addCustomer")
    public List<Customer> addCustomer(@RequestBody Customer customer) {
        System.out.println(customer.getName() +"  "+customer.getCity());
    	customerList.add(customer);
    	
    	return customerList;
    }
}
