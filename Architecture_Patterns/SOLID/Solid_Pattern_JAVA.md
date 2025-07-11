
# SOLID Principles in JavaScript — A Beginner-Friendly Guide

Writing clean, maintainable, and scalable code is key to being an effective developer. The SOLID principles are five foundational object-oriented design guidelines that help you write better code. Even though JavaScript isn't strictly object-oriented, these principles still apply beautifully with classes, modules, and functions.

---

## 📘 Table of Contents

* [Single Responsibility Principle (SRP)](#single-responsibility-principle-srp)
* [Open Closed Principle (OCP)](#open-closed-principle-ocp)
* [Liskov Substitution Principle (LSP)](#liskov-substitution-principle-lsp)
* [Interface Segregation Principle (ISP)](#interface-segregation-principle-isp)
* [Dependency Inversion Principle (DIP)](#dependency-inversion-principle-dip)
* [Final Thoughts](#final-thoughts)

---

## 🔹 Single Responsibility Principle (SRP)

> A class/module should have only one reason to change.

### 💡 Real-World Analogy:

A **bank** has different departments:

* The **cashier** handles deposits/withdrawals
* The **loan officer** handles loan applications

Each department has a clear, single responsibility.

### ✅ Java Example:

```java
class BankService {
    public void depositMoney(String accountNumber, double amount) {
        // Deposit logic
    }

    public void withdrawMoney(String accountNumber, double amount) {
        // Withdrawal logic
    }
}

class LoanService {
    public void applyForLoan(String accountNumber, double amount) {
        // Loan application logic
    }
}

class NotificationService {
    public void sendNotification(String message) {
        // Notification logic
    }
}
```

📝 **Why it matters**:
Each class now has **only one responsibility**, making it easier to maintain, test, and reuse.

---

## 🔹 Open Closed Principle (OCP)

> Software entities should be **open for extension**, but **closed for modification**.

### 💡 Real-World Analogy:

A **notification system** should support Email, SMS, WhatsApp, etc. New channels can be added without rewriting old ones.

### ✅ Java Example:

```java
interface Notification {
    void send(String message);
}

class EmailNotification implements Notification {
    public void send(String message) {
        System.out.println("Sending Email: " + message);
    }
}

class MobileNotification implements Notification {
    public void send(String message) {
        System.out.println("Sending SMS: " + message);
    }
}

class NotificationService {
    public void notifyUser(Notification notification, String message) {
        notification.send(message);
    }
}
```

📝 **Why it matters**:
You can add new types of notifications like `PushNotification` without touching `NotificationService`.

---

## 🔹 Liskov Substitution Principle (LSP)

> Subtypes should be replaceable with their base types without affecting correctness.

### 💡 Real-World Analogy:

You should be able to switch between **WhatsApp**, **Instagram**, and **Facebook** for chatting, sending media, or calling — and things should still work.

### ✅ Java Example:

```java
interface SocialMedia {
    void chatWithFriends();
    void sendPhotosAndVideos();
}

interface SocialVideoCallManager {
    void groupVideoCall(String... users);
}

interface PostMediaManager {
    void publishPost(String post);
}

class Facebook implements SocialMedia, SocialVideoCallManager, PostMediaManager {
    public void chatWithFriends() { /* ... */ }
    public void sendPhotosAndVideos() { /* ... */ }
    public void groupVideoCall(String... users) { /* ... */ }
    public void publishPost(String post) { /* ... */ }
}

class Instagram implements SocialMedia, PostMediaManager {
    public void chatWithFriends() { /* ... */ }
    public void sendPhotosAndVideos() { /* ... */ }
    public void publishPost(String post) { /* ... */ }
}

class WhatsApp implements SocialMedia, SocialVideoCallManager {
    public void chatWithFriends() { /* ... */ }
    public void sendPhotosAndVideos() { /* ... */ }
    public void groupVideoCall(String... users) { /* ... */ }
}
```

📝 **Why it matters**:
Replacing `Facebook` with `Instagram` or `WhatsApp` won’t break the app logic because they honor the interfaces properly.

---

## 🔹 Interface Segregation Principle (ISP)

> Clients should not be forced to depend on methods they do not use.

### 💡 Real-World Analogy:

**Google Pay** supports scratch cards and rewards. **PhonePe** may not. Forcing both to use the same bloated interface would be bad design.

### ✅ Java Example:

```java
interface UpiPayment {
    void payMoney();
    void getScratchCard();
}

interface Rewardable {
    void redeemReward();
}

class GooglePay implements UpiPayment, Rewardable {
    public void payMoney() { /* logic */ }
    public void getScratchCard() { /* logic */ }
    public void redeemReward() { /* logic */ }
}

class PhonePe implements UpiPayment {
    public void payMoney() { /* logic */ }
    public void getScratchCard() { /* logic */ }
}
```

📝 **Why it matters**:
You split interfaces into smaller chunks so implementations stay lightweight and focused.

---

## 🔹 Dependency Inversion Principle (DIP)

> High-level modules should not depend on low-level modules. Both should depend on abstractions.

### 💡 Real-World Analogy:

A **shopping mall** accepts any card — debit or credit. It doesn’t care about implementation, just that a card can make a transaction.

### ✅ Java Example:

```java
interface BankCard {
    void doTransaction(int amount);
}

class DebitCard implements BankCard {
    public void doTransaction(int amount) {
        System.out.println("DebitCard transaction of ₹" + amount);
    }
}

class CreditCard implements BankCard {
    public void doTransaction(int amount) {
        System.out.println("CreditCard transaction of ₹" + amount);
    }
}

class ShoppingMall {
    private BankCard bankCard;

    public ShoppingMall(BankCard bankCard) {
        this.bankCard = bankCard;
    }

    public void makePayment(int amount) {
        bankCard.doTransaction(amount);
    }
}
```

📝 **Why it matters**:

* `ShoppingMall` only depends on the interface `BankCard`, not on concrete classes like `DebitCard`.
* This promotes flexibility and makes your code easier to test.

---

## 📘 Final Thoughts

| Principle | Summary                                      | Goal                        |
| --------- | -------------------------------------------- | --------------------------- |
| **SRP**   | One responsibility per module/class          | Clarity and maintainability |
| **OCP**   | Extend behavior without modifying core       | Flexibility and scalability |
| **LSP**   | Derived classes must be usable in base class | Safety and substitutability |
| **ISP**   | Interfaces should be focused                 | Minimal dependencies        |
| **DIP**   | Depend on abstraction, not concretions       | Decoupling and testability  |

---

By consistently applying the **SOLID principles**, you’ll write JavaScript code that is:

* Easier to **test**
* Safer to **extend**
* Simpler to **understand**
* Ready for **scale**

---

### 📚 Explore More:

🔗 [SystemDesignWithZeeshanAli on dev.to](https://dev.to/t/systemdesignwithzeeshanali)
📁 GitHub: [SystemDesignWithZeeshanAli Repository](https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli)

![SOLID Principles](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/kehpoavvm5b1sjf49849.png)

---

Let me know if you’d like a **JavaScript version** of all code examples too, or a **series** post format with one principle per article!
