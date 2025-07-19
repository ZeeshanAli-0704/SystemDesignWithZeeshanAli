package org.example.strategy.payment;

public class UpiPayment implements PaymentStrategy{
    private String upiID;

    public UpiPayment(String upiID) {
        this.upiID = upiID;
    }

    @Override
    public boolean payAmount(double amount) {
        System.out.println("Paid ₹" + amount + " using UPI: " + upiID);
        return true;
    }
}
