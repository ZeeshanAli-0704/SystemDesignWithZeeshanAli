package org.example;

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class Main {

    public static void main(String[] args) {

        OldPaymentProcessor oldPaymentProcessor = new OldPaymentProcessor();

        PaymentAdapter paymentAdapter= new PaymentAdapter(oldPaymentProcessor);

        paymentAdapter.pay("1000");

    }
}