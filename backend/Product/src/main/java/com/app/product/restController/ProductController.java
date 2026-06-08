package com.app.product.restController;


import com.app.product.dto.ProductDto;
import com.app.product.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/product")
public class ProductController {

    @Autowired
    ProductService productServ;

    @PostMapping(value = "/addProduct", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> addProduct(
            @ModelAttribute ProductDto productDto,
            @RequestParam(value = "img", required = false) MultipartFile image
    ) throws Exception {
        Map<String, Object> res = productServ.addProduct(productDto, image);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/products")
    public ResponseEntity<ProductDto> getProductById(@RequestParam("id")int id){
        ProductDto product = productServ.getProductById(id);
        return new ResponseEntity<>(product, HttpStatus.OK);
    }

    @PutMapping("/updateProduct")
    public ResponseEntity<Map<String, Object>> updateProduct(
            @RequestParam("id") int id,
            @ModelAttribute ProductDto productDto,
            @RequestParam(value = "img", required = false) MultipartFile imageFile
    ) throws IOException { Map<String, Object> res = productServ.updateProduct(id, productDto, imageFile);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/getAllProducts")
    public ResponseEntity<List<ProductDto>> getAllProducts(){
        List<ProductDto> products = productServ.getProducts();
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @PostMapping("/batch")
    public ResponseEntity<List<ProductDto>> getProducts(@RequestBody List<Integer> productIds){
        List<ProductDto> products = productServ.batchProducts(productIds);
        return new ResponseEntity<>(products, HttpStatus.OK);
    }

    @DeleteMapping("/delete")
    public ResponseEntity<Map<String,Object>> delete(@RequestParam("id") int id)throws Exception{
        Map<String, Object> res = productServ.deleteProduct(id);
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

    @GetMapping("/count")
    public ResponseEntity<Map<String,Object>> getCount()throws Exception{
        Map<String, Object> res = productServ.getCount();
        return new ResponseEntity<>(res, HttpStatus.OK);
    }

}
