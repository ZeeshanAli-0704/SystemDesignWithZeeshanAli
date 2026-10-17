package org.example;

public class PaymentAdapter implements NewPaymentProcessor{

    OldPaymentProcessor oldPaymentProcessor;
    private String Amount;
    public PaymentAdapter(OldPaymentProcessor oldPaymentProcessorInstance) {
        oldPaymentProcessor = oldPaymentProcessorInstance;
    }

    @Override
    public void pay(String Amount) {
        oldPaymentProcessor.makePayment( Amount);
    }
}
