package org.example.atm;

public class Card {
    private String cardNumber;
    private int pin;
    private String accountNumber;

    public Card(String cardNumber, int pin, String accountNumber) {
        this.cardNumber = cardNumber;
        this.pin = pin;
        this.accountNumber = accountNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public int getPin() {
        return pin;
    };

    public void setPin(int pin) {
        this.pin = pin;
    };

    public String getAccountNumber() {
        return accountNumber;
    };

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    };

    public boolean validatePIN(int enteredPin){
        return pin == enteredPin;
    };
}
