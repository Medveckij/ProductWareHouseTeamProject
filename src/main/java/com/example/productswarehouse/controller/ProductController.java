
package com.example.productswarehouse.controller;

import com.example.productswarehouse.model.Product;
import com.example.productswarehouse.repository.ProductRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "http://172.20.10.6:3000",
        allowedHeaders = "*")

public class ProductController {

    @Autowired
    private ProductRepository productRepo;
    private ObjectMapper objectMapper = new  ObjectMapper();


    @GetMapping("/getAllProduct")
    public ResponseEntity<String> getAllProducts() {
        try {

            List<Product> products = productRepo.findAll();

            String json = objectMapper.writeValueAsString(products);

            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(json);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/createNewProduct")
    public void createNewProds(@RequestBody String json){

        Product newProduct = null;
        try {
            newProduct = objectMapper.readValue(json, Product.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        productRepo.save(newProduct);

    }
    @PostMapping("/removeProduct")
    public void removeProds(@RequestBody String json){


        Product newProduct = null;
        try {
            newProduct = objectMapper.readValue(json, Product.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        productRepo.deleteById(newProduct.getId());

    }
}