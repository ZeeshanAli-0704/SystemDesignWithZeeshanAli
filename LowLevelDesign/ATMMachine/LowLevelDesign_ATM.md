## Table of Contents

1. [Problem Overview and System Context](#problem-overview-and-system-context)
2. [Requirements](#requirements)
   * [Functional Requirements](#functional-requirements)
   * [Non-Functional Requirements](#non-functional-requirements)
3. [Domain Model & State Pattern](#domain-model--state-pattern)
   * [State Diagram](#state-diagram-textual-representation)
4. [Key Domain Classes with Code](#key-domain-classes-with-code)
   * [Account](#account)
   * [Card](#card)
   * [CashType (Enum)](#cashtype-enum)
   * [TransactionType (Enum)](#transactiontype-enum)
   * [ATMInventory](#atminventory)
5. [ATM State Pattern Implementation](#atm-state-pattern-implementation)
   * [State Interface](#state-interface)
   * [IdleState, HasCardState, SelectOperationState, TransactionState](#example-states-idlestate-hascardstate-selectoperationstate-transactionstate)
6. [ATMMachineContext Orchestration and PIN Retry Logic](#atmmachinecontext-orchestration--pin-retry-logic)
7. [ATMStateFactory](#atmstatefactory)
8. [TransactionProcessor: Two-Phase Dispense Example](#transactionprocessor-two-phase-dispense-example)
9. [Demo Run (Main Method)](#demo-run-main-method)
10. [Testing, Observability, and Next Steps](#testing-observability-and-next-steps)
11. [Error Handling and Recovery](#error-handling-and-recovery)
12. [Concurrency and Thread Safety](#concurrency-and-thread-safety)
13. [Security Considerations](#security-considerations)
14. [UML Style Views (Textual)](#uml-style-views-textual)
15. [Conclusion](#14-conclusion)

---

### Building a Robust ATM Simulator in Java Using the State Pattern

*This article explains how to architect, design, and implement a realistic ATM system in Java following the State Pattern. We will highlight interface and class responsibilities, transaction rules, extensibility, error handling, and concurrency—alongside practical code and UML-like diagrams.*

---

#### Problem Overview and System Context
An Automated Teller Machine (ATM) enables customers to perform banking functions such as withdrawing cash and checking balances by interacting with:
- **Users** (customers, technicians)
- **Bank backend** (accounts/limits)
- **ATM hardware** (cash, sensors)

In this scenario, we simulate a **single ATM** without real I/O or external connections. All I/O and persistence are mocked or simplified for clarity and educational purposes.

---

#### Requirements
**Functional Requirements:**
- Insert card and enter PIN; eject card
- Withdraw cash and check balance
- Manage cash denominations (inventory)
- Handle errors (insufficient funds, invalid PIN, etc.)
- Log all actions

**Non-Functional Requirements:**
- Ensure transaction atomicity
- Implement PIN retry policy
- Ensure thread-safety for cash and account updates
- Design clear, testable modules

---

#### Domain Model & State Pattern
We’ll use the **State Pattern** to model the ATM's UI and operational workflow. Key classes include:
- `ATMMachineContext`: Coordinates states, accounts, and ATM logic
- `ATMState`: Interface for states (`IdleState`, `HasCardState`, `SelectOperationState`, `TransactionState`)
- `Account`, `Card`: Represent customer’s account and card
- `ATMInventory`: Manages available cash by denomination
- `TransactionProcessor`: Handles banking logic

**State Diagram (Textual Representation):**
```
[Idle] --insertCard--> [HasCard]
[HasCard] --PIN ok--> [SelectOperation]
[SelectOperation] --choose--> [Transaction]
[Transaction] --done--> [SelectOperation]
[Any] --eject/cancel--> [Idle]
```

---

#### Key Domain Classes with Code
Below are the core classes with their implementations.

##### Account
Represents a customer’s bank account with synchronized methods for thread-safe balance updates.
```java
import java.math.BigDecimal;

public class Account {
    private final String accountNumber, accountName;
    private BigDecimal balance;
    public Account(String accNum, String name, BigDecimal start) {
        this.accountNumber = accNum;
        this.accountName = name;
        this.balance = start;
    }
    public synchronized BigDecimal getBalance() { return balance; }
    public synchronized boolean withdraw(BigDecimal amount) {
        if (balance.compareTo(amount) >= 0) {
            balance = balance.subtract(amount);
            return true;
        }
        return false;
    }
    public synchronized void deposit(BigDecimal amount) {
        balance = balance.add(amount);
    }
    public String getAccountNumber() { return accountNumber; }
}
```

##### Card
Represents a customer’s card with PIN validation.
```java
public class Card {
    private final String cardNumber;
    private final int pin;
    private final String accountNumber;
    public Card(String cardNum, int pin, String accNum) {
        this.cardNumber = cardNum;
        this.pin = pin;
        this.accountNumber = accNum;
    }
    public boolean validatePIN(int enteredPin) { return pin == enteredPin; }
    public String getAccountNumber() { return accountNumber; }
}
```

##### CashType (Enum)
Defines cash denominations for the ATM inventory.
```java
import java.math.BigDecimal;

public enum CashType {
    BILL_100(new BigDecimal(100)),
    BILL_50(new BigDecimal(50)),
    BILL_20(new BigDecimal(20)),
    BILL_10(new BigDecimal(10)),
    BILL_5(new BigDecimal(5)),
    BILL_1(new BigDecimal(1));
    private final BigDecimal value;
    CashType(BigDecimal value) { this.value = value; }
    public BigDecimal getValue() { return value; }
}
```

##### TransactionType (Enum)

```Java
package org.example.enums;

public enum TransactionType {
    WITHDRAW_CASH,
    CHECK_BALANCE
}
```

##### ATMInventory
Manages cash inventory with a two-phase dispense process for atomicity.
```java
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class ATMInventory {
    private final Map<CashType, Integer> cashInventory = new EnumMap<>(CashType.class);
    public ATMInventory() { initialize(); }
    private void initialize() {
        for (CashType c : CashType.values()) { cashInventory.put(c, 20); }
    }
    public synchronized BigDecimal getTotalCash() {
        return cashInventory.entrySet().stream()
            .map(e -> e.getKey().getValue().multiply(BigDecimal.valueOf(e.getValue())))
            .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
    // Plan (does not mutate state)
    public synchronized Map<CashType,Integer> planDispense(BigDecimal amount) {
        List<CashType> denoms = Arrays.stream(CashType.values())
            .sorted(Comparator.comparing(CashType::getValue).reversed())
            .toList();
        BigDecimal remaining = amount;
        Map<CashType,Integer> plan = new EnumMap<>(CashType.class);
        for (CashType d : denoms) {
            int avail = cashInventory.getOrDefault(d, 0);
            int take = remaining.divide(d.getValue(), 0, RoundingMode.DOWN).intValue();
            take = Math.min(take, avail);
            if (take > 0) {
                plan.put(d, take);
                remaining = remaining.subtract(d.getValue().multiply(BigDecimal.valueOf(take)));
            }
        }
        return (remaining.compareTo(BigDecimal.ZERO) == 0) ? plan : null;
    }
    // Apply (atomic, with checks)
    public synchronized boolean applyDispense(Map<CashType,Integer> plan) {
        if (plan == null) return false;
        for (Map.Entry<CashType, Integer> e : plan.entrySet()) {
            if (cashInventory.getOrDefault(e.getKey(), 0) < e.getValue())
                return false;
        }
        plan.forEach((k,v) -> cashInventory.put(k, cashInventory.get(k) - v));
        return true;
    }
    public synchronized void addCash(CashType cashType, int count) {
        cashInventory.put(cashType, cashInventory.getOrDefault(cashType, 0) + count);
    }
}
```

---

#### ATM State Pattern Implementation
The State Pattern is used to manage the ATM’s workflow through different states.

##### State Interface
Defines the contract for all state implementations.
```java
public interface ATMState {
    String getStateName();
    ATMState next(ATMMachineContext context);
}
```

##### Example States: IdleState, HasCardState, SelectOperationState, TransactionState
These classes represent different states of the ATM.
```java
public class IdleState implements ATMState {
    public IdleState() { System.out.println("Idle: Insert your card."); }
    public String getStateName() { return "Idle"; }
    public ATMState next(ATMMachineContext ctx) {
        return (ctx.getCurrentCard() != null) ? new HasCardState() : this;
    }
}

public class HasCardState implements ATMState {
    public HasCardState() { System.out.println("HasCard: Enter your PIN."); }
    public String getStateName() { return "HasCard"; }
    public ATMState next(ATMMachineContext ctx) {
        return (ctx.getCurrentAccount() != null) ? new SelectOperationState() : this;
    }
}

public class SelectOperationState implements ATMState {
    public SelectOperationState() {
        System.out.println("ATM is in Select Operation State - Please select an operation");
        System.out.println("1. Withdraw Cash");
        System.out.println("2. Check Balance");
    }
    @Override
    public String getStateName() {
        return "SelectOperationState";
    }
    @Override
    public ATMState next(ATMMachineContext context) {
        if (context.getCurrentCard() == null) {
            return context.getStateFactory().createIdleState();
        }
        if (context.getSelectedOperation() != null) {
            return context.getStateFactory().createTransactionState();
        }
        return this;
    }
}

public class TransactionState implements ATMState {
    public TransactionState() {
    }
    @Override
    public String getStateName() {
        return "Transaction State";
    }
    @Override
    public ATMState next(ATMMachineContext context) {
        if (context.getCurrentCard() == null) {
            return context.getStateFactory().createIdleState();
        }
        // After transaction completion, go back to select operation
        return context.getStateFactory().createSelectOperationState();
    }
}
```

---

#### ATMMachineContext Orchestration and PIN Retry Logic
This class manages the state transitions and core ATM operations.
```java
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
    }
    public Card getCurrentCard() { return currentCard; }
    public ATMStateFactory getStateFactory() { return atmStateFactory; }
    public Account getCurrentAccount() { return currentAccount; }
    public TransactionType getSelectedOperation() { return selectedOperation; }
    // Reset ATM state
    private void resetATM() {
        this.currentCard = null;
        this.currentAccount = null;
        this.selectedOperation = null;
        this.currentState = atmStateFactory.createIdleState();
    }
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
    public void advanceState() {
        ATMState nextState = currentState.next(this);
        currentState = nextState;
        System.out.println("Current state: " + currentState.getStateName());
    }
    // Card insertion operation
    public synchronized void insertCard(Card card) {
        if (currentState instanceof IdleState) {
            System.out.println("Card Inserted");
            currentCard = card;
            advanceState();
        }
    }
    // PIN authentication operation
    public synchronized void enterPin(int pin) {
        if (currentState instanceof HasCardState) {
            if (currentCard.validatePIN(pin)) {
                System.out.println("PIN authenticated successfully");
                currentAccount = accounts.get(currentCard.getAccountNumber());
                advanceState();
            } else {
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
            System.out.println("Cannot select operation in " + currentState.getStateName());
        }
    }
    // Perform the selected transaction
    public synchronized void performTransaction(BigDecimal amount) {
        if (currentState instanceof TransactionState) {
            try {
                if (selectedOperation == TransactionType.WITHDRAW_CASH) {
                    transactionProcessor.performWithdrawal(currentAccount, amount);
                } else if (selectedOperation == TransactionType.CHECK_BALANCE) {
                    transactionProcessor.checkBalance(currentAccount);
                }
                // Ask if user wants another transaction
                advanceState();
            } catch (Exception e) {
                System.out.println("Transaction failed: " + e.getMessage());
                // Go back to select operation state
                currentState = atmStateFactory.createSelectOperationState();
            }
        } else {
            System.out.println("Cannot perform transaction in " + currentState.getStateName());
        }
    }
}
```

#### ATMStateFactory
```Java

public class ATMStateFactory {
    private static ATMStateFactory instance = null;

    public static ATMStateFactory getInstance(){
        if(instance == null){
            instance= new ATMStateFactory();
        }
        return instance;
    };

    public ATMState createIdleState() {
        return new IdleState();
    }
    public ATMState createHasCardState(){
        return new HasCardState();
    }

    public ATMState createSelectOperationState(){
        return new SelectOperationState();
    }

    public ATMState createTransactionState(){
        return new TransactionState();
    }

}

```

---

#### TransactionProcessor: Two-Phase Dispense Example
Handles transaction logic with rollback mechanisms for failed operations.
```java
import java.math.BigDecimal;
import java.util.Map;

public class TransactionProcessor {
    private final ATMInventory inventory;
    public TransactionProcessor(ATMInventory inventory) { this.inventory = inventory; }
    public void performWithdrawal(Account acc, BigDecimal amount) throws Exception {
        if (acc == null) throw new IllegalArgumentException("No account loaded!");
        if (amount.compareTo(BigDecimal.ZERO) <= 0)
            throw new IllegalArgumentException("Withdrawal amount must be positive.");
        if (!acc.withdraw(amount))
            throw new Exception("Insufficient account funds.");
        Map<CashType, Integer> plan = inventory.planDispense(amount);
        if (plan == null) {
            acc.deposit(amount); // rollback
            throw new Exception("Cannot dispense exact amount; suggest alternative.");
        }
        if (!inventory.applyDispense(plan)) {
            acc.deposit(amount); // rollback
            throw new Exception("ATM cash issue, transaction canceled.");
        }
        System.out.println("Dispense: " + plan);
    }
    public void checkBalance(Account acc) {
        if (acc == null) throw new IllegalArgumentException("No account loaded!");
        System.out.println("Balance: " + acc.getBalance());
    }
}
```

---

#### Demo Run (Main Method)
A sample run to demonstrate the ATM workflow.
```java
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

public class ATMDemo {
    public static void main(String[] args) {
        ATMInventory inv = new ATMInventory();
        Map<String, Account> accounts = new HashMap<>();
        accounts.put("111", new Account("111", "Jane", new BigDecimal("500.00")));
        Card janeCard = new Card("9999", 4321, "111");
        ATMMachineContext atm = new ATMMachineContext(inv, accounts);
        atm.insertCard(janeCard);
        atm.enterPin(4321);
        atm.selectOperation(TransactionType.WITHDRAW_CASH);
        atm.performTransaction(new BigDecimal("140"));
        atm.selectOperation(TransactionType.CHECK_BALANCE);
        atm.performTransaction(BigDecimal.ZERO);
        atm.insertCard(janeCard); // Fail: already in non-idle
        atm.selectOperation(TransactionType.WITHDRAW_CASH); // Eject card to reset for next
    }
}
```

**Sample Output:**
```
Idle: Insert your card.
Current state: HasCard
HasCard: Enter your PIN.
Current state: SelectOperationState
Dispense: {BILL_100=1, BILL_20=2}
Balance: 360.00
```

---

#### Testing, Observability, and Next Steps
- **Unit Tests**: Test each state, transition, and `ATMInventory` capabilities.
- **Integration Tests**: Validate full customer sessions.
- **Logging**: Add correlation/session IDs and audit logs for traceability.
- **Extensibility**: Support new features by adding new states, enums, or classes.

---

#### Error Handling and Recovery
- **Invalid PIN**: Increment retry counter; after limit, block session or eject card.
- **Insufficient Funds**: Display message; remain in `SelectOperationState`.
- **Insufficient ATM Cash**: Suggest smaller amounts.
- **Hardware Failure**: Initiate reconciliation workflow for disputes or auto-reversals.
- **Unexpected Exception**: Log with correlation ID; transition to safe state; eject card if possible.

---

####  Concurrency and Thread Safety
- Assume **one session per ATM** but guard shared data.
- `ATMInventory` and `Account` operations are **synchronized** for atomic updates.
- Use `BigDecimal` for precision in financial calculations.
- For multi-ATM scenarios, implement **optimistic concurrency** or transactional APIs.

---

####  Security Considerations
- **PIN Handling**: Avoid plaintext storage; use salted hashes in real systems.
- **Retries**: Limit PIN attempts (e.g., 3) before blocking.
- **Masking**: Hide sensitive data in logs/UI (e.g., `****3456`).
- **Transport**: Use TLS for communication with bank systems.
- **Tamper Detection**: Log physical access or firmware changes.

---

#### UML Style Views (Textual)
**Class Diagram (Text):**
```
ATMMachineContext
  - currentState: ATMState
  - currentCard: Card
  - currentAccount: Account
  - selectedOperation: TransactionType
  - atmInventory: ATMInventory
  - transactionProcessor: TransactionProcessor
ATMState <interface>
  + getStateName(): String
  + next(context): ATMState
IdleState, HasCardState, SelectOperationState, TransactionState : ATMState
Account
  - accountNumber: String
  - accountName: String
  - balance: BigDecimal
  + depositAmount(amount)
  + withdraw(amount): boolean
Card
  - cardNumber: String
  - pin: int
  - accountNumber: String
ATMInventory
  - cashInventory: Map<CashType,int>
  + getTotalCash(): int
  + hasSufficientCash(amount): boolean
  + dispenseCash(amount): Map<CashType,int> | null
TransactionProcessor
  + performWithdrawal(Account,double)
  + checkBalance(Account)
```

**State Diagram (Text):**
```
[Idle] --insertCard--> [HasCard]
[HasCard] --enterPin(valid)--> [SelectOperation]
[HasCard] --enterPin(invalid)--> [HasCard] (retries--)
[SelectOperation] --select(WITHDRAW or CHECK_BALANCE)--> [Transaction]
[Transaction] --success/fail--> [SelectOperation]
Any State --cancel/timeout--> [Idle]
```

---

#### 14. Conclusion
Using the State Pattern and separating class responsibilities, we’ve built a maintainable and robust ATM simulator that can evolve with real-world banking needs. Concurrency, error handling, and auditability are integrated, reflecting best practices in banking systems.  
*Next Steps: Localize messages, add session timeouts, support deposits, and persist transaction logs.*


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
