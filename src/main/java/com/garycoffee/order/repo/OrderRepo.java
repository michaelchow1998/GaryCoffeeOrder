package com.garycoffee.order.repo;

import com.garycoffee.order.model.Order;
import com.garycoffee.order.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface OrderRepo extends MongoRepository<Order, String> {

    List<Order> getOrdersByUserId(Integer userId);

    List<Order> getOrdersByStaffId(Integer staffId);

    Order getOrderById(String id);
}
