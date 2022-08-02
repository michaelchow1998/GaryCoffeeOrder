package com.garycoffee.order.services;

import com.garycoffee.order.WebClientRequest.ProductLogWebClientRequest;
import com.garycoffee.order.WebClientRequest.dto.RequestLogProduct;
import com.garycoffee.order.WebClientRequest.dto.TransactionType;
import com.garycoffee.order.dto.RequestUpdateList;
import com.garycoffee.order.dto.RequestUpdateProduct;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class ProductService {

    @Autowired
    private ProductRepo productRepo;

    @Resource
    private ProductLogWebClientRequest productLogWebClientRequest;

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

            //log ProductLog
            RequestLogProduct logReq = new RequestLogProduct();
            logReq.setStaffId(null);
            logReq.setProductShortName(product.getShortName());
            logReq.setTransactionType(TransactionType.Increase);
            logReq.setAmount(product.getStock());

            String logMessage = productLogWebClientRequest.createProductLog(logReq);
            log.info(logMessage);

        }

    }

    public void updateProduct(String shortName,RequestUpdateProduct requestUpdateProduct){

            Product targetProduct = productRepo.findProductByShortName(shortName);
            if(requestUpdateProduct.getPrice() !=null){
                targetProduct.setPrice(requestUpdateProduct.getPrice());
            }
            if(requestUpdateProduct.getStock() !=null){
                Integer currentStock = targetProduct.getStock();
                targetProduct.setStock(currentStock+requestUpdateProduct.getStock());
            }
            productRepo.save(targetProduct);

            //log ProductLog
            RequestLogProduct logReq = new RequestLogProduct();
            logReq.setStaffId(null);
            logReq.setProductShortName(requestUpdateProduct.getShortName());
            logReq.setTransactionType(TransactionType.Increase);
            logReq.setAmount(requestUpdateProduct.getStock());

            String logMessage = productLogWebClientRequest.createProductLog(logReq);
            log.info(logMessage);


    }

    public void deleteProduct(String shortName){
            Product product = productRepo.findProductByShortName(shortName);
            log.info("product: {} deleted", product.getShortName());
            productRepo.delete(product);

        }

    }

