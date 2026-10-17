package org.example.controller;

import org.example.service.PaymentService;
import org.example.user.User;

public class PaymentController {
    // Service to handle payment-related logic
    private final PaymentService paymentService;
    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }
    public void processPayment(final String bookingId, final User user) throws Exception {
        paymentService.processPayment(bookingId, user);
    }
}
