package com.garycoffee.order.controller;


import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.Product;
import com.garycoffee.order.services.OrderService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("api/v1/orders")
@AllArgsConstructor
public class OrderController {

    @Autowired
    private OrderService orderService;

    @PostMapping
    public ResponseEntity<Order> createOrder(@RequestBody CreateOrderRequest req){
        Order targetOrder = orderService.createOrder(req);
        URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/orders").toUriString());
        return ResponseEntity.created(uri).body(targetOrder);
    }

    //Get All Products
    @GetMapping
    public ResponseEntity<List<Order>> fetchOrders(){

        List<Order> orderList = orderService.getAllOrder();

        return ResponseEntity.ok().body(orderList);
    }

    //Get a Product by shortName
    @GetMapping("/{id}")
    public ResponseEntity<Order> fetchOrderById(@PathVariable String id){

        Order order = orderService.getOrderById(id);

        return ResponseEntity.ok().body(order);
    }
}
