package com.ykshinde.filter;

import java.util.ArrayList;
import java.util.List;

import org.springframework.boot.context.embedded.FilterRegistrationBean;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.ShallowEtagHeaderFilter;

@Component
public class SimpleFilterConfiguration extends FilterRegistrationBean{

	public SimpleFilterConfiguration() {
		super();
		this.setFilter(new SimpleCORSFilter());
		
		//this.setFilter(new ShallowEtagHeaderFilter());
		
		List<String> urlPatterns = new ArrayList<String>();
		
		urlPatterns.add("/Customer/*");
		
		this.setUrlPatterns(urlPatterns);
	}
}
