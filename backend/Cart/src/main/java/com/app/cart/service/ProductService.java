package com.app.cart.service;

import com.app.cart.dto.ProductDto;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class ProductService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @CircuitBreaker(name ="productServiceCB", fallbackMethod = "getProductfromProductServiceFallBack")
    public ProductDto getProductfromProductService(Integer id){
        ProductDto product = webClientBuilder.build()
                .get()
                .uri("http://PRODUCT-SERVICE/product/products?id=" + id)
                .retrieve()
                .bodyToMono(ProductDto.class)
                .block();
        return product;
    }

    public ProductDto getProductfromProductServiceFallBack(Integer id, Throwable ex){
        return new ProductDto();
    }

}
