package org.example.atm;

import java.math.BigDecimal;

public class Account {
    private String accountNumber;
    private String accountName;
    private BigDecimal balance;

    public Account(String accountNumber, String accountName, BigDecimal balance) {
        this.accountNumber = accountNumber;
        this.accountName = accountName;
        this.balance = balance;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public synchronized BigDecimal getBalance() {
        return balance;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }

    public synchronized void depositAmount(BigDecimal amount){
        balance = balance.add(amount);
    }

    public synchronized boolean withdraw(BigDecimal amount){
        if(balance.compareTo(amount) >= 0){
            balance = balance.subtract(amount);
            return true;
        }
        return false;
    }
}
