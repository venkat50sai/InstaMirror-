package com.app.product.serviceImpl;

import com.app.product.dto.ProductDto;
import com.app.product.entity.Product;
import com.app.product.exception.ProductException;
import com.app.product.repository.ProductRepository;
import com.app.product.service.ProductService;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.*;

@Service
@Transactional
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepo;

    @Autowired
    private ModelMapper modelMapper;

    @Override
    public Map<String, Object> addProduct(ProductDto productDto, MultipartFile imageFile) throws IOException {
        if (imageFile != null && !imageFile.isEmpty()) {
            productDto.setImage(imageFile.getBytes());
        }
        Product product = modelMapper.map(productDto, Product.class);
        productRepo.save(product);
        return buildProductResponse("Product Added Successfully");
    }

    @Override
    public ProductDto getProductById(int id) throws ProductException {
        Product product = productRepo.findById(id).orElseThrow(()->new ProductException("No Product Found"));
        return modelMapper.map(product, ProductDto.class);
    }

    @Override
    public List<ProductDto> getProducts() {
        return productRepo.findAll()
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public List<ProductDto> batchProducts(List<Integer> productIds) {
        return productRepo.findAllById(productIds)
                .stream()
                .map(product -> modelMapper.map(product, ProductDto.class))
                .toList();
    }

    @Override
    public Map<String, Object> getCount() {
        return Map.of(
                "count",(int) productRepo.count(),
                "success", true);
    }

    @Override
    public Map<String, Object> updateProduct(int id, ProductDto productDto, MultipartFile imageFile) throws IOException {

        Product existingProduct = productRepo.findById(id)
                .orElseThrow(() -> new ProductException("No Product Found with id: " + id));

        existingProduct.setName(productDto.getName());
        existingProduct.setDescription(productDto.getDescription());
        existingProduct.setPrice(productDto.getPrice());
        existingProduct.setStock(productDto.getStock());
        if (imageFile != null && !imageFile.isEmpty()) {
            existingProduct.setImage(imageFile.getBytes());
        }
        productRepo.save(existingProduct);

        return buildProductResponse("Product updated Successfully");
    }

    @Override
    public Map<String, Object> deleteProduct(int id) throws ProductException{
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new ProductException("No Product Found with id: " + id));
        productRepo.deleteById(id);
        return buildProductResponse("Product Deleted Successfully");
    }

    private Map<String, Object> buildProductResponse(String message){
        return Map.of("message", message,
                "success", true,
                "product", getProducts()
        );
    }
}
