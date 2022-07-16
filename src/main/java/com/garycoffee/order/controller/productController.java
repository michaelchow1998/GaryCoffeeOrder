package com.garycoffee.order.controller;

import com.garycoffee.order.dto.RequestUpdateList;
import com.garycoffee.order.dto.RequestUpdateProduct;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.ProductRepo;
import com.garycoffee.order.services.ProductService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/products")
@AllArgsConstructor
public class productController {

    @Autowired
    private ProductService productService;

    //Create Product
    @PostMapping
    public ResponseEntity<Product> createProduct(@RequestBody Product product){
        Product targetProduct = productService.createProduct(product);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/products").toUriString());
        return ResponseEntity.created(uri).body(targetProduct);
    }

    //Get a Product by shortName
    @GetMapping("/{shortName}")
    public ResponseEntity<Product> fetchProductByShortName(@PathVariable String shortName){

        Product product = productService.getProductByShortName(shortName);

        return ResponseEntity.ok().body(product);
    }


    //Get All Products
    @GetMapping
    public ResponseEntity<List<Product>> fetchProducts(){

        List<Product> productList = productService.getAllProduct();

        return ResponseEntity.ok().body(productList);
    }


    //Update a List of product
    @PutMapping
    public ResponseEntity<List<Product>> refillProduct(
            @RequestBody RequestUpdateList requestUpdateList
    ){
        productService.updateAllProducts(requestUpdateList);
        List<Product> productList = productService.getAllProduct();
        return ResponseEntity.ok().body(productList);
    }

}
