package org.example.service;


import org.example.model.order.Order;

public class NotificationService {

    public NotificationService() {
    }

    public void notifyCustomer(Order order) {
        String msg = switch (order.getStatus()) {
            case ACCEPTED -> "🎉 Your order has been accepted!";
            case REJECTED -> "😞 Sorry, your order was rejected.";
            case IN_PROGRESS -> "🍳 Your order is being prepared.";
            case DELIVERED -> "🚗 Your order has been delivered!";
            case ASSIGNED -> "🚗 Your order has been assigned to Delivery Agent";
            case OUT_FOR_DELIVERY -> "🚗 Your order out for delivery!";
            default -> "📦 Order placed successfully!";
        };
        System.out.println("📩 Notification to " + order.getCustomer().getUserName() +
                ": " + msg + " (Order ID: " + order.getOrderId() + ")");
    };

    public void notifyRestaurantCancellation(Order order, String actor) {
        System.out.println("📢 Notify restaurant (" + order.getRestaurant().getName()
                + "): Order " + order.getOrderId() + " canceled by " + actor + ".");
    }
}
