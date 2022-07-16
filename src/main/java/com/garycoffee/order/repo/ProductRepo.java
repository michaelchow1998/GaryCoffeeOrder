package com.garycoffee.order.repo;

import com.garycoffee.order.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ProductRepo
        extends MongoRepository<Product, String> {



    Product findProductByShortName(String shortName);
}
