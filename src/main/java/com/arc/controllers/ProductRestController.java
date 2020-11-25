package com.arc.controllers;


import com.arc.domain.ProductRepository;
import com.arc.domain.Product;

import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.linkTo;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.methodOn;

import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.http.MediaType;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.Link;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.http.HttpStatus;

import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;
import java.util.Optional;
import java.util.List;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@RestController
//@RequestMapping("/products")
public class ProductRestController {

	@Autowired
	private ProductRepository productRepository;

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductRestController.class);

    //------------------- Retrieve a Product --------------------------------------------------------
    @RequestMapping(value = "/products/{id}", method = RequestMethod.GET, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<EntityModel<Product>> getProduct(@PathVariable("id") String id) {

    	LOGGER.info("Start");
    	LOGGER.debug("Fetching Product with id: {}", id);

    	Optional<Product> product = productRepository.findById(id);
    	Product myProduct = product.get();
        if (product == null) {
    		LOGGER.debug("Product with id: {} not found", id);
            return new ResponseEntity<EntityModel<Product>>(HttpStatus.NOT_FOUND);
        }
        
        //EntityModel<Product> productRes = EntityModel.of(myProduct);
        
        EntityModel<Product> productRes =
        		new EntityModel<Product>(myProduct, linkTo(methodOn(ProductRestController.class).getProduct(myProduct.getId())).withSelfRel());
    	LOGGER.info("Ending");
        return new ResponseEntity<EntityModel<Product>>(productRes, HttpStatus.OK);
    }

    //------------------- Create a Product --------------------------------------------------------
    @RequestMapping(value = "/products", method = RequestMethod.POST)
    public ResponseEntity<EntityModel<Product>> postProduct(@RequestBody Product product,    UriComponentsBuilder ucBuilder) {

    	LOGGER.info("Start");
    	LOGGER.debug("Creating Product with code: {}", product.getCode());

        List<Product> products = productRepository.findByCode(product.getCode());
        if (products.size() > 0) {
    		LOGGER.debug("A Product with code {} already exist", product.getCode());
            return new ResponseEntity<EntityModel<Product>>(HttpStatus.CONFLICT);
        }

        Product newProduct = productRepository.save(product);

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(ucBuilder.path("/products/{id}").buildAndExpand(product.getId()).toUri());
        EntityModel<Product> productRes =new EntityModel<Product>(newProduct, linkTo(methodOn(ProductRestController.class).getProduct(newProduct.getId())).withSelfRel());
    	LOGGER.info("Ending");
        return new ResponseEntity<EntityModel<Product>>(productRes, headers, HttpStatus.OK);
    }

    //------------------- Update a Product --------------------------------------------------------
    @RequestMapping(value = "/products/{id}", method = RequestMethod.PUT)
    public ResponseEntity<EntityModel<Product>> updateProduct(@PathVariable("id") String id, @RequestBody Product product) {

    	LOGGER.info("Start");
    	LOGGER.debug("Updating Product with id: {}", id);

        Optional<Product> productX = productRepository.findById(id);
        Product currentProduct = productX.get();
        

        if (currentProduct == null) {
    		LOGGER.debug("Product with id: {} not found", id);
            return new ResponseEntity<EntityModel<Product>>(HttpStatus.NOT_FOUND);
        }

        currentProduct.setName(product.getName());
        currentProduct.setCode(product.getCode());
        currentProduct.setTitle(product.getTitle());
        currentProduct.setDescription(product.getDescription());
        currentProduct.setImgUrl(product.getImgUrl());
        currentProduct.setPrice(product.getPrice());
        currentProduct.setProductCategoryName(product.getProductCategoryName());

        Product newProduct = productRepository.save(currentProduct);

        EntityModel<Product> productRes =new EntityModel<Product>(newProduct, linkTo(methodOn(ProductRestController.class).getProduct(newProduct.getId())).withSelfRel());
    	LOGGER.info("Ending");
        return new ResponseEntity<EntityModel<Product>>(productRes, HttpStatus.OK);
    }

    //------------------- Retreive all Products --------------------------------------------------------
    @RequestMapping(value = "/products", method = RequestMethod.GET ,produces = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<CollectionModel<EntityModel<Product>>> getAllProducts() {

    	LOGGER.info("Start");
        List<Product> products = productRepository.findAll();
        Link links[]={linkTo(methodOn(ProductRestController.class).getAllProducts()).withSelfRel(),linkTo(methodOn(ProductRestController.class).getAllProducts()).withRel("getAllProducts")};
        if(products.isEmpty()){
    		LOGGER.debug("No products retreived from repository");
            return new ResponseEntity<CollectionModel<EntityModel<Product>>>(HttpStatus.NOT_FOUND);
        }
        List<EntityModel<Product>> list=new ArrayList<EntityModel<Product>> ();
        for(Product product:products){
        	list.add(new EntityModel<Product>(product, linkTo(methodOn(ProductRestController.class).getProduct(product.getId())).withSelfRel()));
        }
        CollectionModel<EntityModel<Product>> productRes=new CollectionModel<EntityModel<Product>>(list, links) ;
    	LOGGER.info("Ending");
        return new ResponseEntity<CollectionModel<EntityModel<Product>>>(productRes, HttpStatus.OK);
    }

    //------------------- Delete a Product --------------------------------------------------------
    @RequestMapping(value = "/products/{id}", method = RequestMethod.DELETE)
    public ResponseEntity<Product> deleteProduct(@PathVariable("id") String id) {

    	LOGGER.info("Start");
    	LOGGER.debug("Fetching & Deleting Product with id: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product == null) {
    		LOGGER.debug("Product with id: {} not found, hence not deleted", id);
            return new ResponseEntity<Product>(HttpStatus.NOT_FOUND);
        }

        productRepository.deleteById(id);
    	LOGGER.info("Ending");
        return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
    }

    //------------------- Delete All Products --------------------------------------------------------
    @RequestMapping(value = "/products", method = RequestMethod.DELETE)
    public ResponseEntity<Product> deleteAllProducts() {

    	LOGGER.info("Start");
        long count = productRepository.count();
        LOGGER.debug("Deleting {} products", count);
        productRepository.deleteAll();
    	LOGGER.info("Ending");
        return new ResponseEntity<Product>(HttpStatus.NO_CONTENT);
    }
}
