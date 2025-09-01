package org.example.config;
import org.example.strategy.parking.NearToEntranceParkingStrategy;
import org.example.strategy.parking.ParkingStrategy;
import org.example.strategy.payment.CreditCardPayment;
import org.example.strategy.payment.PaymentStrategy;

public class ParkingLotConfiguration {

    public ParkingStrategy parkingStrategy() {
        return new NearToEntranceParkingStrategy();
    }

    public PaymentStrategy defaultPaymentStrategy() {
        return new CreditCardPayment("2134-2345-2445-4124");
    }
}
