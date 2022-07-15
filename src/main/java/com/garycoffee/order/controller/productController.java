package com.garycoffee.order.controller;

import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.ProductRepo;
import com.garycoffee.order.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("api/v1/products")
@AllArgsConstructor
public class productController {

    @Autowired
    private ProductService productService;

    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        Product targetProduct = productService.createProduct(product);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/products").toUriString());
        return ResponseEntity.created(uri).body(targetProduct);
    }



    @GetMapping("/{productName}")
    public ResponseEntity<Product> fetchProductByProductName(@PathVariable String productName){

        Product product = productService.getProductByProductName(productName);

        return ResponseEntity.ok().body(product);
    }




}
