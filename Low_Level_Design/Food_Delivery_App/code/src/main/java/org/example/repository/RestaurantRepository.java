package org.example.repository;

import org.example.model.order.Order;
import org.example.model.order.OrderStatus;

import java.util.List;

public interface RestaurantRepository {
    Boolean addOrder(String restaurantId, String Id);
    // Returns all orders (history) for a restaurant
    List<String> findById(String restaurantId);
}
