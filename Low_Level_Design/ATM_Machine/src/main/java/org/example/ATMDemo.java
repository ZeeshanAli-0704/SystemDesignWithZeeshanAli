package org.example;

import org.example.atm.ATMInventory;
import org.example.atm.Account;
import org.example.atm.Card;
import org.example.enums.TransactionType;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ATMDemo {
    public static void main(String[] args) {

        ATMInventory inventory = new ATMInventory();
        Map<String, Account> accounts = new HashMap<>();
        // Create and initialize ATM
        ATMMachineContext atm = new ATMMachineContext(inventory, accounts);

        // Add sample accounts
        atm.addAccount(new Account("123456", "A", new BigDecimal(1000.0)));
        atm.addAccount(new Account("654321", "B", new BigDecimal(500.0)));

        try {
            // Sample workflow
            System.out.println("=== Starting ATM Demo ===");

            // Insert card
            atm.insertCard(new Card("123456", 1234, "654321"));

            // Enter PIN
            atm.enterPin(1234);

            // Select operation
            atm.selectOperation(TransactionType.WITHDRAW_CASH);

            // Perform transaction
            atm.performTransaction(new BigDecimal(100.0));

            // Select another operation
            atm.selectOperation(TransactionType.CHECK_BALANCE);

            // Perform balance check
            atm.performTransaction(new BigDecimal(0.0));

            // Return card
            atm.returnCard();

            System.out.println("=== ATM Demo Completed ===");

        } catch (Exception e) {
            System.out.println("Error: " + e.getMessage());
        }
    }
}
