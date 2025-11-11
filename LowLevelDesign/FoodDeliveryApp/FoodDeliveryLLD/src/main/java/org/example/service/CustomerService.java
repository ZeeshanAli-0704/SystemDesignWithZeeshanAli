package org.example.service;

import org.example.model.order.Order;
import org.example.model.user.User;
import org.example.repository.CustomerRepository;

public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepo) {
        this.customerRepository = customerRepo;
    }

    public void addOrder(User customer, Order order) {
        customerRepository.addOrder(customer.getUserId(), order);
    }
}
