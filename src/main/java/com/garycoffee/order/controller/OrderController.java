package com.garycoffee.order.controller;


import com.garycoffee.order.dto.CreateOrderRequest;
import com.garycoffee.order.exception.BalanceNotEnoughException;
import com.garycoffee.order.exception.StockNotEnoughException;
import com.garycoffee.order.model.Order;
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
        try{
            Order targetOrder = orderService.createOrder(req);
            URI uri = URI.create(ServletUriComponentsBuilder.fromCurrentContextPath().path("/api/v1/orders").toUriString());
            return ResponseEntity.created(uri).body(targetOrder);
        }catch(BalanceNotEnoughException e){
            throw new BalanceNotEnoughException(e.getMessage());
        }catch (StockNotEnoughException e){
            throw new StockNotEnoughException(e.getMessage());
        }






    }

    //Get All Orders
    @GetMapping
    public ResponseEntity<List<Order>> fetchOrders(){

        List<Order> orderList = orderService.getAllOrder();

        return ResponseEntity.ok().body(orderList);
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Order>> fetchOrdersWithPage(
            @RequestParam (value = "phone", defaultValue = "", required = false) String phone,
            @RequestParam (value = "staffId", defaultValue = "0",required = false) Integer staffId,
            @RequestParam (value = "page", defaultValue = "1") Integer page
            ){

        if(!phone.isEmpty() | !staffId.equals(0)){
            if (!phone.isEmpty()) {
                Page<Order> orderList = orderService.getAllOrderWithPage(phone, page);
                return ResponseEntity.ok().body(orderList);

            }
            if (!staffId.equals(0)) {
                Page<Order> orderList = orderService.getOrderByStaffId(staffId, page);
                return ResponseEntity.ok().body(orderList);
            }
        }else {
            throw new RuntimeException("param format wrong");
        }

        return null;
    }


    //Get Order by id
    @GetMapping("/{id}")
    public ResponseEntity<Order> fetchOrderById(@PathVariable String id){

        Order order = orderService.getOrderById(id);

        return ResponseEntity.ok().body(order);
    }



}
