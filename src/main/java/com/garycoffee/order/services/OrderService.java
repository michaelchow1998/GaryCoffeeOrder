package com.garycoffee.order.services;

import com.garycoffee.order.dto.BuyItem;
import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.dto.WebClientRequestAccount;
import com.garycoffee.order.model.Account;
import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.OrderItem;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.OrderRepo;
import com.garycoffee.order.repo.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

import java.util.*;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {

    @Autowired
    private WebClient.Builder webClientBuilder;

    @Autowired
    private OrderRepo orderRepo;

    @Autowired
    private ProductRepo productRepo;

    public Order createOrder(CreateOrderRequest createOrderRequest) {
        int totalAmount = 0;
        Order order = new Order();
        List<OrderItem> orderItemList = new ArrayList<>();

        //set up orderItemList
        for(BuyItem buyItem : createOrderRequest.getBuyItemList()){
            Product product = productRepo.findProductByShortName(buyItem.getProductShortName());

            if(product == null){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }else if(product.getStock() < buyItem.getQuantity()){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST);
            }

            product.setStock(product.getStock() - buyItem.getQuantity());
            productRepo.save(product);

            int amount = buyItem.getQuantity() * product.getPrice();
            totalAmount = totalAmount + amount;

            OrderItem orderItem = new OrderItem();
            orderItem.setProductShortName(buyItem.getProductShortName());
            orderItem.setQuantity(buyItem.getQuantity());
            orderItem.setAmount(amount);
            orderItemList.add(orderItem);
        }
        if(createOrderRequest.getIsUserBuy().equals(true)){
            String uri = "http://localhost:8070/api/v1/accounts/" +createOrderRequest.getPhone();
            Integer accountBalance = webClientBuilder.build()
                    .get()
                    .uri(uri)
                    .retrieve()
                    .bodyToMono(Integer.class)
                    .block();


            if(accountBalance>totalAmount){
                WebClientRequestAccount webClientRequestAccount = new WebClientRequestAccount();
                webClientRequestAccount.setPhone(createOrderRequest.getPhone());
                webClientRequestAccount.setAmount(totalAmount);

                webClientBuilder.build()
                        .put()
                        .uri("http://localhost:8070/api/v1/accounts/reduceBalance")
                        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                        .body(Mono.just(webClientRequestAccount), WebClientRequestAccount.class)
                        .retrieve()
                        .bodyToMono(Account.class)
                        .block();

                //set up order
                order.setPhone(createOrderRequest.getPhone());
                order.setStaffId(createOrderRequest.getStaffId());
                order.setTotalAmount(totalAmount);
                order.setCreateDate(new Date());
                order.setOrderItemList(orderItemList);
            }

        }
        log.info("save order");
        return orderRepo.insert(order);

    }


    public List<Order> getAllOrder(){
        List<Order> orderList = orderRepo.findAll();
        return orderList;
    }

    public Order getOrderById(String Id){
        Order order = orderRepo.getOrderById(Id);
        return order;
    }
}
