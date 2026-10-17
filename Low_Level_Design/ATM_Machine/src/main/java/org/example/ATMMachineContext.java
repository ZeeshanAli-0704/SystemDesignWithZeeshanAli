package org.example;

import org.example.atm.ATMInventory;
import org.example.atm.Card;
import org.example.enums.TransactionType;
import org.example.service.TransactionProcessor;
import org.example.state.*;
import org.example.atm.Account;

import java.math.BigDecimal;
import java.util.Map;

public class ATMMachineContext {
    private ATMState currentState;
    private Card currentCard;
    private Account currentAccount;
    private final ATMStateFactory atmStateFactory;
    private TransactionType selectedOperation;
    private final Map<String, Account> accounts; // Simplified account storage
    private final TransactionProcessor transactionProcessor;

    public ATMMachineContext(ATMInventory atmInventory, Map<String, Account> accounts) {
        this.atmStateFactory = ATMStateFactory.getInstance();
        this.currentState = atmStateFactory.createIdleState();
        this.transactionProcessor = new TransactionProcessor(atmInventory);
        this.accounts = accounts;
        System.out.println("ATM initialized in: " + currentState.getStateName());
    };

    public Card getCurrentCard(){
        return currentCard;
    }
    public ATMStateFactory getStateFactory(){
        return atmStateFactory;
    }

    public Account getCurrentAccount() {
        return currentAccount;
    }

    public TransactionType getSelectedOperation(){
        return selectedOperation;
    };

    // Reset ATM state
    private void resetATM() {
        this.currentCard = null;
        this.currentAccount = null;
        this.selectedOperation = null;
        this.currentState = atmStateFactory.createIdleState();
    };

    // Add an account to the ATM (for demo purposes)
    public void addAccount(Account account) {
        accounts.put(account.getAccountNumber(), account);
    }

    // Get account by number
    public Account getAccount(String accountNumber) {
        return accounts.get(accountNumber);
    }

    // Return card to user
    public synchronized void returnCard() {
        if (currentState instanceof HasCardState
                || currentState instanceof SelectOperationState
                || currentState instanceof TransactionState) {
            System.out.println("Card returned to customer");
            resetATM();
        } else {
            System.out.println("No card to return in " + currentState.getStateName());
        }
    }
    // Move to Next Stage

    public void advanceState(){
        ATMState nextState = currentState.next(this);
        currentState = nextState;
        System.out.println("Current state: " + currentState.getStateName());
    }

    // Card insertion operation

    public synchronized void insertCard (Card card){
        if(currentState instanceof IdleState){
            System.out.println("Card Inserted");
            currentCard = card;
            advanceState();
        }
    }

    // PIN authentication operation

    public synchronized void enterPin(int pin){
        if(currentState instanceof HasCardState){
            if(currentCard.validatePIN(pin)){
                System.out.println("PIN authenticated successfully");
                currentAccount = accounts.get(currentCard.getAccountNumber());
                advanceState();
            }else{
                System.out.println("Invalid PIN. Please try again");
                // Could implement PIN retry logic here
            }
        } else {
            System.out.println("Cannot enter PIN in " + currentState.getStateName());
        }
    }

    // Select operation (withdrawal, balance check, etc.)
    public synchronized void selectOperation(TransactionType transactionType) {
        if (currentState instanceof SelectOperationState) {
            System.out.println("Selected operation: " + transactionType);
            this.selectedOperation = transactionType;
            advanceState();
        } else {
            System.out.println(
                    "Cannot select operation in " + currentState.getStateName());
        }
    };

    // Perform the selected transaction
    public  synchronized void performTransaction(BigDecimal amount) {
        if (currentState instanceof TransactionState) {
            try {
                if (selectedOperation == TransactionType.WITHDRAW_CASH) {
                    transactionProcessor.performWithdrawal(currentAccount, amount);
                } else if (selectedOperation == TransactionType.CHECK_BALANCE) {
                    transactionProcessor. checkBalance(currentAccount);
                }
                // Ask if user wants another transaction
                advanceState();
            } catch (Exception e) {
                System.out.println("Transaction failed: " + e.getMessage());
                // Go back to select operation state
                currentState = atmStateFactory.createSelectOperationState();
            }
        } else {
            System.out.println(
                    "Cannot perform transaction in " + currentState.getStateName());
        }
    }

}
