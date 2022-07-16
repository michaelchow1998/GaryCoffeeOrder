package com.garycoffee.order.services;

import com.garycoffee.order.dto.BuyItem;
import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.OrderItem;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.repo.OrderRepo;
import com.garycoffee.order.repo.ProductRepo;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
@Slf4j
public class OrderService {

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

        //set up order
        order.setUserId(createOrderRequest.getUserId());
        order.setStaffId(createOrderRequest.getStaffId());
        order.setTotalAmount(totalAmount);
        order.setCreateDate(new Date());
        order.setOrderItemList(orderItemList);

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
