package org.example.service;

import org.example.model.restaurant.Restaurant;
import org.example.model.order.Order;
import org.example.model.order.OrderStatus;
import org.example.repository.OrderRepository;
import org.example.repository.RestaurantRepository;

import java.util.Objects;

public class RestaurantService {

    private final RestaurantRepository restaurantRepository;
    private final OrderRepository orderRepository;
    private final NotificationService notificationService;

    public RestaurantService(RestaurantRepository restaurantRepository, OrderRepository orderRepository, NotificationService notificationService){
        this.restaurantRepository=restaurantRepository;
        this.orderRepository = orderRepository;
        this.notificationService = notificationService;
    }

    public void handleNewOrder(Order order) {
        Restaurant restaurant = order.getRestaurant();

        long active = restaurantRepository.findById(restaurant.getId()).stream()
                .map(orderRepository::findById)
                .filter(Objects::nonNull)
                .map(Order::getStatus)
                .filter(s -> s == OrderStatus.PLACED || s == OrderStatus.ACCEPTED || s == OrderStatus.IN_PROGRESS)
                .count();
        // Logic to decide acceptance based on load, etc.
        boolean canAccept = active < 5;
        if (canAccept) {
            order.setStatus(OrderStatus.ACCEPTED);
            restaurantRepository.addOrder(restaurant.getId(), order.getOrderId());
            System.out.println("✅ " + restaurant.getName() + " accepted the order.");
        } else {
            order.setStatus(OrderStatus.REJECTED);
            System.out.println("❌ " + restaurant.getName() + " rejected the order due to load.");
        }
        orderRepository.save(order);
        notificationService.notifyCustomer(order);// sample condition
    };

    // Cancel invoked by customer
    public boolean cancelByCustomer(String orderId) {
        Order order = orderRepository.findById(orderId);
        if (order == null) return false;

        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.REJECTED
                || order.getStatus() == OrderStatus.CANCELED) {
            System.out.println("⚠️ Cannot cancel order " + orderId + " in status " + order.getStatus());
            return false;
        }

        if (order.getStatus() == OrderStatus.PLACED || order.getStatus() == OrderStatus.ACCEPTED) {
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
            notificationService.notifyRestaurantCancellation(order, "customer");
            System.out.println("🛑 Order " + orderId + " canceled by customer.");
            return true;
        }

        System.out.println("⚠️ Customer not allowed to cancel in status " + order.getStatus());
        return false;
    }

    // Cancel invoked by restaurant
    public boolean cancelByRestaurant(String orderId, String reason) {
        Order order = orderRepository.findById(orderId);
        if (order == null) return false;

        if (order.getStatus() == OrderStatus.DELIVERED
                || order.getStatus() == OrderStatus.REJECTED
                || order.getStatus() == OrderStatus.CANCELED) {
            System.out.println("⚠️ Cannot cancel order " + orderId + " in status " + order.getStatus());
            return false;
        }

        if (order.getStatus() == OrderStatus.PLACED
                || order.getStatus() == OrderStatus.ACCEPTED
                || order.getStatus() == OrderStatus.IN_PROGRESS) {
            order.setStatus(OrderStatus.CANCELED);
            orderRepository.save(order);
            notificationService.notifyCustomer(order);
            System.out.println("🛑 Order " + orderId + " canceled by restaurant. Reason: " + reason);
            return true;
        }

        return false;
    }
}
