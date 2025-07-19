package org.example.strategy.payment;

public class CreditCardPayment  implements PaymentStrategy{
    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public boolean payAmount(double amount) {
        System.out.println("Paid ₹" + amount + " using Credit Card: " + cardNumber);
        return true;
    }
}
