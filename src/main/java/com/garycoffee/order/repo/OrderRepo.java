package com.garycoffee.order.repo;

import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepo extends MongoRepository<Order, String> {


    List<Order> getOrdersByStaffId(Integer staffId);

    Page<Order> getOrdersByPhone(String phone,Pageable pageable);

    Order getOrderById(String id);
}
