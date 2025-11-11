package org.example.repository.implementations;

import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.repository.OrderRepository;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class OrderRepositoryImpl implements OrderRepository {

    private final Map<String, Order> orderMap = new ConcurrentHashMap<>();

    @Override
    public void save(Order order) {
        orderMap.put(order.getOrderId(), order);
    }

    @Override
    public Order findById(String orderId) {
        return orderMap.get(orderId);
    }

    @Override
    public List<Order> findByRestaurant(String restaurantId) {
        return orderMap.values().stream()
                .filter(o -> o.getRestaurant().getId().equals(restaurantId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Order> findByCustomer(String customerId) {
        return orderMap.values().stream()
                .filter(o -> o.getCustomer().getUserId().equals(customerId))
                .collect(Collectors.toList());
    }

    @Override
    public void updateStatus(String orderId, String restaurantId, String customerId, OrderStatus newStatus) {
        Order order = orderMap.get(orderId);
        if (order != null) {
            order.setStatus(newStatus);
        }
    }
}
