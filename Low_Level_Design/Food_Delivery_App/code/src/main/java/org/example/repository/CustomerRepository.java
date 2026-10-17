package org.example.repository;

import org.example.model.order.Order;

public interface CustomerRepository {
    void  addOrder(String customerId, Order order);
}
