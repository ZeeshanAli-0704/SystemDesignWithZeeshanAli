package org.example.service;

import org.example.atm.ATMInventory;
import org.example.atm.Account;
import org.example.enums.CashType;

import java.math.BigDecimal;
import java.util.Map;

public class TransactionProcessor {

    private final ATMInventory atmInventory;


    public TransactionProcessor(ATMInventory atmInventory) {
        this.atmInventory = atmInventory;
    }

    public void checkBalance(Account account) {
        if (account == null) {
            throw new IllegalArgumentException("No account linked to transaction");
        }
        System.out.println("Your current balance is: $" + account.getBalance());
    }


    public void performWithdrawal(Account account, BigDecimal amount) throws Exception {
        if (account == null) {
            throw new IllegalArgumentException("No account linked to transaction");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Withdrawal amount must be positive");
        }

        // Check if user has sufficient balance
        if (!account.withdraw(amount)) {
            throw new Exception("Insufficient funds in account");
        }

        // Check if ATM has sufficient cash
        if (!atmInventory.hasSufficientCash(amount)) {
            account.depositAmount(amount); // Rollback
            throw new Exception("Insufficient cash in ATM");
        }

        Map<CashType, Integer> dispensedCash = atmInventory.dispenseCash(amount);
        if (dispensedCash == null) {
            account.depositAmount(amount); // Rollback
            throw new Exception("Unable to dispense exact amount");
        }

        System.out.println("Transaction successful. Please collect your cash:");
        for (Map.Entry<CashType, Integer> entry : dispensedCash.entrySet()) {
            System.out.println(entry.getValue() + " x $" + entry.getKey().value);
        }
    }

    public void performDeposit(Account account, BigDecimal amount, CashType cashType, int count) {
        if (account == null) {
            throw new IllegalArgumentException("No account linked to transaction");
        }
        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Deposit amount must be positive");
        }

        account.depositAmount(amount);
        atmInventory.addCash(cashType, count);
        System.out.println("Deposit successful. New balance: $" + account.getBalance());
    }

}
