package org.example;

import org.example.atm.ATMInventory;
import org.example.atm.Account;
import org.example.atm.Card;
import org.example.enums.TransactionType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ATMDemoThread {

    public static void main(String[] args) {
        ATMInventory inventory = new ATMInventory();
        Map<String, Account> accounts = new HashMap<>();

        // Add sample accounts
        accounts.put("123456", new Account("123456", "Alice", new BigDecimal("1000.00")));
        accounts.put("654321", new Account("654321", "Bob", new BigDecimal("500.00")));
        accounts.put("987654", new Account("987654", "Carol", new BigDecimal("750.00")));

        // Build cards
        Card card1 = new Card("111111", 1234, "123456"); // Alice's card
        Card card2 = new Card("222222", 2345, "654321"); // Bob's card
        Card card3 = new Card("333333", 3456, "987654"); // Carol's card

        // Define a Runnable to represent a customer session
        class ATMCustomerSession implements Runnable {
            private final ATMMachineContext atm;
            private final Card card;
            private final int pin;
            private final BigDecimal withdrawAmount;

            public ATMCustomerSession(ATMMachineContext atm, Card card, int pin, BigDecimal withdrawAmount) {
                this.atm = atm;
                this.card = card;
                this.pin = pin;
                this.withdrawAmount = withdrawAmount;
            }

            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " started session");
                    atm.insertCard(card);
                    atm.enterPin(pin);
                    atm.selectOperation(TransactionType.WITHDRAW_CASH);
                    atm.performTransaction(withdrawAmount);
                    atm.selectOperation(TransactionType.CHECK_BALANCE);
                    atm.performTransaction(BigDecimal.ZERO);
                    atm.returnCard();
                    System.out.println(Thread.currentThread().getName() + " session completed\n");
                } catch (Exception e) {
                    System.err.println("Error in " + Thread.currentThread().getName() + ": " + e.getMessage());
                }
            }
        }

        // Start concurrent customer sessions (each with its own ATM machine)
        Thread t1 = new Thread(new ATMCustomerSession(new ATMMachineContext(inventory, accounts), card1, 1234, new BigDecimal("100.00")), "AliceThread");
        Thread t2 = new Thread(new ATMCustomerSession(new ATMMachineContext(inventory, accounts), card2, 2345, new BigDecimal("150.00")), "BobThread");
        Thread t3 = new Thread(new ATMCustomerSession(new ATMMachineContext(inventory, accounts), card3, 3456, new BigDecimal("50.00")), "CarolThread");

        t1.start();
        t2.start();
        t3.start();

        // Optional: Wait for all threads to finish
        try {
            t1.join();
            t2.join();
            t3.join();
        } catch (InterruptedException e) {
            System.err.println("Demo interrupted");
        }

        System.out.println("=== All sessions completed ===");
        System.out.println("Final ATM balance: " + inventory.getTotalCash());
        accounts.values().forEach(acc -> {
            System.out.println(acc.getAccountName() + " final balance: " + acc.getBalance());
        });
    }
}
