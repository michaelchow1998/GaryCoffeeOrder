package com.garycoffee.order.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.services.OrderService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor
@Slf4j
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest req){
        Order targetOrder = orderService.createOrder(req);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/orders").toUriString());
        return ResponseEntity.created(uri).body(targetOrder);
    }

    //Get All Orders
    @GetMapping
    public ResponseEntity<List<Order>> fetchOrders(){

        List<Order> orderList = orderService.getAllOrder();

        return ResponseEntity.ok().body(orderList);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Order>> fetchOrdersWithPage(
            @RequestParam (value = "phone") String phone,
            @RequestParam (value = "page", defaultValue = "1") Integer page

            ) throws JsonProcessingException {

        Page<Order> orderList = orderService.getAllOrderWithPage(phone,page);

        return ResponseEntity.ok().body(orderList);
    }

    //Get Order by id
    @GetMapping("/{id}")
    public ResponseEntity<Order> fetchOrderById(@PathVariable String id){

        Order order = orderService.getOrderById(id);

        return ResponseEntity.ok().body(order);
    }



}
