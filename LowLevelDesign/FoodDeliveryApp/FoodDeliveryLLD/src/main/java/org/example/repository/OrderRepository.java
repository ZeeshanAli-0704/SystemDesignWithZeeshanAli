package org.example.repository;

import org.example.model.order.Order;
import org.example.model.order.OrderStatus;

import java.util.List;

public interface OrderRepository {
    void save(Order order);
    Order findById(String orderId);
    List<Order> findByRestaurant(String restaurantId);
    List<Order> findByCustomer(String customerId);
    void updateStatus(String orderId, String restaurantId, String customerId, OrderStatus newStatus);

}
