package com.garycoffee.order.services;

import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.ProductRepo;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class ProductService {

    @Autowired
    private ProductRepo productRepo;


    public Product createProduct(Product product){
        productRepo.insert(product);
        Product targetProduct = productRepo.findProductByProductName(product.getProductName());
        return targetProduct;
    }

    public Product getProductByProductName(String productName){
        Product product = productRepo.findProductByProductName(productName);
        return product;
    }


}
