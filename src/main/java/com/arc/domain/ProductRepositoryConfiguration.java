package com.arc.domain;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurerAdapter;

@Configuration
public class ProductRepositoryConfiguration extends RepositoryRestConfigurerAdapter {
	// we need this class to make sure we 
	// get the id col from the database
	
    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config) {
        config.exposeIdsFor(Product.class);
    }
}