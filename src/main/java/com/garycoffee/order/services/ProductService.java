package com.garycoffee.order.services;

import com.garycoffee.order.dto.RequestUpdateList;
import com.garycoffee.order.dto.RequestUpdateProduct;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepo productRepo;


    public Product createProduct(Product product){
        productRepo.insert(product);
        Product targetProduct = productRepo.findProductByShortName(product.getShortName());
        return targetProduct;
    }



    public Product getProductByShortName(String shortName){
        Product product = productRepo.findProductByShortName(shortName);
        return product;
    }

    public List<Product> getAllProduct(){
        List<Product> productList = productRepo.findAll();
        return productList;
    }

    public void updateAllProducts(RequestUpdateList requestUpdateList){
        for(RequestUpdateProduct product : requestUpdateList.getRequestUpdateProductList()){
            Product targetProduct = productRepo.findProductByShortName(product.getShortName());
            if(product.getPrice() !=null){
                targetProduct.setPrice(product.getPrice());
            }
            if(product.getStock() !=null){
                Integer currentStock = targetProduct.getStock();
                targetProduct.setStock(currentStock+product.getStock());
            }
            productRepo.save(targetProduct);
            log.info("{} updated",targetProduct.getShortName());
        }
    }

    public void deleteProduct(String shortName){
            Product product = productRepo.findProductByShortName(shortName);
            log.info("product: {} deleted", product.getShortName());
            productRepo.delete(product);

        }

    }

