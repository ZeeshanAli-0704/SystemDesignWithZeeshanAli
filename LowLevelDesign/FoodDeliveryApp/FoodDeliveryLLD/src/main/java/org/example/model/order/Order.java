package org.example.model.order;

import org.example.model.restaurant.Restaurant;
import org.example.model.food.FoodItem;
import org.example.model.user.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class Order {
    private final String orderId;
    private final User customer;
    private final Restaurant restaurant;
    private OrderStatus status;
    private final List<FoodItem> items;
    private final LocalDateTime timestamp;
    private String assignedAgentId;

    public Order(User customer, Restaurant restaurant, List<FoodItem> items){
        this.orderId = "ORD-" + UUID.randomUUID().toString();
        this.restaurant = restaurant;
        this.customer = customer;
        this.status = OrderStatus.PLACED;
        this.items = items;
        this.timestamp = LocalDateTime.now();
        this.assignedAgentId = null;
    }

    public String getOrderId() {
        return orderId;
    }

    public User getCustomer() {
        return customer;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public Restaurant getRestaurant() {
        return restaurant;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    };

    public String getAssignedAgentId() {
        return assignedAgentId;
    }

    public void setAssignedAgentId(String assignedAgentId) {
        this.assignedAgentId = assignedAgentId;
    }

    @Override
    public String toString() {
        return "Order{" +
                "id='" + orderId + '\'' +
                ", customer=" + customer.getUserName() +
                ", restaurant=" + restaurant.getName() +
                ", status=" + status +
                ", assignedAgentId=" + assignedAgentId +
                '}';
    }
}
