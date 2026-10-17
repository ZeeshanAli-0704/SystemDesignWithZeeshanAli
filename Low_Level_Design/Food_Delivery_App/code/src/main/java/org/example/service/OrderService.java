package org.example.service;

import org.example.model.order.OrderStatus;
import org.example.model.restaurant.Restaurant;
import org.example.model.food.FoodItem;
import org.example.model.order.Order;
import org.example.model.user.User;
import org.example.repository.OrderRepository;

import java.util.List;

public class OrderService {
    private final OrderRepository orderRepository;
    private final RestaurantService restaurantService;
    private final CustomerService customerService;

    public OrderService(OrderRepository orderRepo, RestaurantService restaurantService, CustomerService customerService) {
        this.orderRepository = orderRepo;
        this.restaurantService = restaurantService;
        this.customerService = customerService;
    };

    public Order createOrder(User user, Restaurant restaurant, List<FoodItem> item){
        if (user == null || restaurant == null || item == null || item.isEmpty()) {
            throw new IllegalArgumentException("Invalid order request.");
        }
        Order order = new Order(user, restaurant, item);
        orderRepository.save(order);
        customerService.addOrder(order.getCustomer(), order);
        System.out.println("\n🛒 " + user.getUserName() + " placed an order at " + restaurant.getName() + " waiting for confirmation. ");

        // Delegate restaurant processing — OrderService shouldn’t care about restaurant internals
        restaurantService.handleNewOrder(order);
        return order;
    }


}
