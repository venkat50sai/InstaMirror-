package com.app.product.service;


import com.app.product.dto.ProductDto;
import com.app.product.exception.ProductException;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public interface ProductService {
    Map<String, Object> addProduct(ProductDto productDto, MultipartFile imageFile) throws IOException;
    ProductDto getProductById(int id) throws ProductException;
    List<ProductDto> getProducts();
    List<ProductDto> batchProducts(List<Integer> productIds);
    Map<String, Object>  updateProduct(int id, ProductDto productDto, MultipartFile imageFile) throws IOException;
    Map<String, Object>  deleteProduct(int id) throws ProductException;
    Map<String, Object> getCount();
}
