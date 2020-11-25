package com.arc;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.arc.domain.Product;
import com.arc.domain.ProductCategory;
import com.arc.domain.ProductCategoryRepository;
import com.arc.domain.ProductRepository;


/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@Component
public class InitializationComponent {

	private final Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private ProductCategoryRepository productCategoryRepository;

	@Autowired
	private ProductRepository productRepository;



	@PostConstruct
    private void init(){

		logger.info("Start");

		//Deleting all existing data on start.....

		productCategoryRepository.deleteAll();
		productRepository.deleteAll();

		ProductCategory productCategory=new ProductCategory();
    	productCategory.setName("Mobile");
    	productCategory.setDescription("Mobile phones");
    	productCategory.setTitle("Mobiles and Tablet");
    	productCategory.setImgUrl("mobile.jpg");
    	productCategoryRepository.save(productCategory);

    	Product product=new Product();
    	product.setName("Kamsung D3");
    	product.setCode("KAMSUNG-TRIOS");
    	product.setTitle("Kamsung Trios 12 inch , black , 12 px ....");
    	product.setDescription("Kamsung Trios 12 inch with Touch");
    	product.setImgUrl("kamsung.jpg");
    	product.setPrice(12000.00);
    	product.setProductCategoryName(productCategory.getName());
    	productRepository.save(product);

    	product=new Product();
    	product.setName("Lokia Pomia");
    	product.setCode("LOKIA-POMIA");
    	product.setTitle("Lokia 12 inch , white , 14px ....");
    	product.setDescription("Lokia Pomia 10 inch with NFC");
    	product.setImgUrl("lokia.jpg");
    	product.setPrice(9000.00);
    	product.setProductCategoryName(productCategory.getName());
    	productRepository.save(product);

    	//TODO: Add rest of products and catagories...........
		logger.info("End");
    }
}
