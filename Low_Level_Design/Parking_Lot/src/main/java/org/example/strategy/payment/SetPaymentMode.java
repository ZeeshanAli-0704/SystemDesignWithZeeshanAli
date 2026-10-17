package org.example.strategy.payment;

public class SetPaymentMode {

    private PaymentStrategy selectedPaymentMode;

    public SetPaymentMode(PaymentStrategy strategy){
        selectedPaymentMode = strategy;
    }

    public String payAmount(double amount){
        if(selectedPaymentMode != null){
           boolean status =  selectedPaymentMode.payAmount(amount);
           if(status){
               return "Payment Completed";
           }else{
               return "Payment Failed";
           }
        }
        return null;
    }
}
