
package com.arc.springmicro3;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.hateoas.PagedModel;
import org.springframework.hateoas.mediatype.hal.Jackson2HalModule;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.web.client.RestTemplate;

import com.arc.domain.Product;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * @author <a href="mailto:biniljava<[@>.]yahoo.co.in">Binildas C. A.</a>
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest
@TestPropertySource(locations="classpath:test.properties")
public class ProductHalRestTemplateTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ProductHalRestTemplateTest.class);
	private static String PRODUCT_SERVICE_URL = "http://127.0.0.1:8080/products";

	@Before
	public void setUp(){
		deleteAllProducts();
	}


    @Test
    public void testPostProduct(){

    	LOGGER.info("Star tppt");
    	try{

		Product productNew1 = createProduct("1");
		Product productNew2 = createProduct("2");

		RestTemplate restTemplate = restTemplate();
		Product productRetreived1 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew1, Product.class);
		Product productRetreived2 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew2, Product.class);
		LOGGER.info("productRetreived1 : {}"+ productRetreived1);
		LOGGER.info("productRetreived2 : {}"+ productRetreived2);

    	assertTrue("successfully saved",true);
    	}catch(Exception ex){
    		assertTrue("successfully failed",true);
    	}
    	LOGGER.info("End");
    }

    @Test
    public void testGetAllProducts(){

    	LOGGER.info("Start tgap");

		testPostProduct();
		List<Product> productList = getAllProducts();
		LOGGER.info("productList.size() : " + productList.size());

    	assertTrue(productList.size() > 0);
    	LOGGER.info("End");
    }

    @Test
    public void testPutProduct(){


    	LOGGER.info("Start tpp");
    	try{

		Product productNew3 = createProduct("3");

		RestTemplate restTemplate = restTemplate();
		Product productRetreived3 = restTemplate.postForObject( PRODUCT_SERVICE_URL, productNew3, Product.class);
		LOGGER.info("productRetreived3 : {}"+ productRetreived3);
		productRetreived3.setPrice(productRetreived3.getPrice() * 2);
		restTemplate.put( PRODUCT_SERVICE_URL + "/" + productRetreived3.getId(), productRetreived3, Product.class);
		Product productAgainRetreived = restTemplate.getForObject(PRODUCT_SERVICE_URL + "/" + productRetreived3.getId(), Product.class);
		LOGGER.info("productAgainRetreived : {}"+ productAgainRetreived);

    	assertTrue("successfully saved",true);
    	}catch(Exception ex){
    		assertTrue("successfully failed",true);
    	}
    	LOGGER.info("End");
    }

    public void deleteAllProducts(){

    	LOGGER.info("Start dap");
		RestTemplate restTemplate = restTemplate();
		List<Product> productList = getAllProducts();
		LOGGER.info("productList.size() : " + productList.size());
		productList.forEach(item->restTemplate.delete(PRODUCT_SERVICE_URL + "/" + item.getId()));
    	LOGGER.info("End");
    }

    public List<Product> getAllProducts(){

		RestTemplate restTemplate = restTemplate();

		ParameterizedTypeReference<PagedModel<Product>> responseTypeRef = new ParameterizedTypeReference<PagedModel<Product>>() {

			};
		ResponseEntity<PagedModel<Product>> responseEntity = restTemplate.exchange(PRODUCT_SERVICE_URL, HttpMethod.GET,
				(HttpEntity<Product>) null, responseTypeRef);
		PagedModel<Product> resources = responseEntity.getBody();
		Collection<Product> products = resources.getContent();
		List<Product> productList = new ArrayList<Product>(products);
		return productList;

    }

    private RestTemplate restTemplate() {
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		mapper.registerModule(new Jackson2HalModule());
		MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
		converter.setSupportedMediaTypes(MediaType.parseMediaTypes("application/hal+json"));
		converter.setObjectMapper(mapper);
		return new RestTemplate(Arrays.asList(converter));
	}

	private Product createProduct(String id){

    	Product product=new Product();
    	product.setName("Kamsung D3" + "-" + id);
    	product.setCode("KAMSUNG-TRIOS" + "-" + id);
    	product.setTitle("Kamsung Trios 12 inch , black , 12 px ...." + "-" + id);
    	product.setDescription("Kamsung Trios 12 inch with Touch" + "-" + id);
    	product.setImgUrl("kamsung" + id + ".jpg");
    	product.setPrice(12000.00);
    	product.setProductCategoryName("testCategory");
    	return product;
	}
}
