package org.example.repository.implementations;

import org.example.model.order.Order;
import org.example.repository.CustomerRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CustomerRepositoryImpl implements CustomerRepository {

    Map<String, List<String>> customerOrderMap = new ConcurrentHashMap<>();

    @Override
    public void addOrder(String customerId, Order order) {
        customerOrderMap.computeIfAbsent(customerId, k -> new ArrayList<>()).add(order.getOrderId());
    }
}
