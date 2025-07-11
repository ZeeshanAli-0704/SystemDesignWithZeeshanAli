

# 🎯 Strategy Design Pattern in Java — Explained with Real-Life Problem and Code

> “Design patterns are solutions to recurring problems in software design. Think of them as time-tested blueprints.”

In this post, we’ll demystify the **Strategy Design Pattern**, walk through a real-world use case, and build a clean Java implementation. If you’re preparing for LLD interviews, learning design patterns, or aiming to write flexible and maintainable code, this one’s for you.

---

## 📘 Table of Contents

* [What is the Strategy Design Pattern?](#what-is-the-strategy-design-pattern)
* [Real Life Analogy](#real-life-analogy)
* [When to Use Strategy Pattern](#when-to-use-strategy-pattern)
* [Example Problem Statement](#example-problem-statement)
* [Step by Step Java Implementation](#step-by-step-java-implementation)
* [UML Class Diagram](#uml-class-diagram)
* [Benefits of Strategy Pattern](#benefits-of-strategy-pattern)
* [Conclusion](#conclusion)

---

## ✅ What is the Strategy Design Pattern?

The **Strategy Pattern** is a **behavioral design pattern** that enables you to define a **family of algorithms**, encapsulate each one, and make them interchangeable. The key idea is to **delegate the behavior to different strategy classes** instead of hardcoding it.

> 🔁 Strategy pattern is all about **choosing behavior at runtime**.

---

## 🧠 Real Life Analogy

Think about a **navigation app** like Google Maps.

* You want to go from Point A to Point B.
* Based on conditions (fastest, least traffic, shortest), the **route strategy** changes.
* The app uses a **strategy (algorithm)** to calculate the best route.

These strategies are:

* 🛣️ Fastest Route
* 🚗 Shortest Distance
* 🧭 Avoid Tolls

At runtime, the user picks the strategy.

---

## 📌 When to Use Strategy Pattern

Use the Strategy pattern when:

* You have multiple algorithms for a specific task.
* You want to switch between algorithms at runtime.
* You want to reduce conditionals (`if-else` or `switch`).
* You want to follow the **Open/Closed Principle** (OCP) – Open for extension, closed for modification.

---

## 💬 Example Problem Statement

> 🧾 Design a payment system where a user can pay using **different payment methods** — Credit Card, UPI, and PayPal. The system should allow selecting or switching payment strategy **dynamically at runtime**.

---

## 🛠️ Step by Step Java Implementation

Let’s implement this in Java using the Strategy Design Pattern.

---

### Step 1: Create the Strategy Interface

```java
public interface PaymentStrategy {
    void pay(double amount);
}
```

---

### Step 2: Implement Different Strategies

```java
public class CreditCardPayment implements PaymentStrategy {
    private String cardNumber;

    public CreditCardPayment(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " using Credit Card: " + cardNumber);
    }
}
```

```java
public class UpiPayment implements PaymentStrategy {
    private String upiId;

    public UpiPayment(String upiId) {
        this.upiId = upiId;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " using UPI: " + upiId);
    }
}
```

```java
public class PayPalPayment implements PaymentStrategy {
    private String email;

    public PayPalPayment(String email) {
        this.email = email;
    }

    @Override
    public void pay(double amount) {
        System.out.println("Paid ₹" + amount + " using PayPal: " + email);
    }
}
```

---

### Step 3: Create the Context Class

```java
public class PaymentContext {
    private PaymentStrategy strategy;

    // Allow strategy to be set at runtime
    public void setPaymentStrategy(PaymentStrategy strategy) {
        this.strategy = strategy;
    }

    public void payAmount(double amount) {
        if (strategy == null) {
            throw new IllegalStateException("Payment strategy not set.");
        }
        strategy.pay(amount);
    }
}
```

---

### Step 4: Use in Main Class

```java
public class StrategyPatternDemo {
    public static void main(String[] args) {
        PaymentContext context = new PaymentContext();

        // Using Credit Card
        context.setPaymentStrategy(new CreditCardPayment("1234-5678-9012-3456"));
        context.payAmount(1500);

        // Switching to UPI
        context.setPaymentStrategy(new UpiPayment("zeeshan@upi"));
        context.payAmount(800);

        // Switching to PayPal
        context.setPaymentStrategy(new PayPalPayment("zeeshan@gmail.com"));
        context.payAmount(2000);
    }
}
```

---

## 🧭 UML Class Diagram

```plaintext
        +---------------------+
        |   PaymentStrategy   |<-- interface
        +---------------------+
                  ▲
       +----------+----------+
       |          |          |
+------------------+  +----------------+  +------------------+
| CreditCardPayment|  |   UpiPayment   |  |  PayPalPayment   |
+------------------+  +----------------+  +------------------+
       ▲
       |
+------------------+
|  PaymentContext  |
+------------------+
| - strategy       |
| +setStrategy()   |
| +payAmount()     |
+------------------+
```

---

## 🌟 Benefits of Strategy Pattern

| Benefit                  | Description                                        |
| ------------------------ | -------------------------------------------------- |
| ✅ Open/Closed Principle  | Add new strategies without modifying existing code |
| ✅ Flexibility            | Choose behavior at runtime                         |
| ✅ Eliminate Conditionals | Replaces large if-else/switch blocks               |
| ✅ Reusability            | Each strategy is a reusable, independent class     |

---

## ⚠️ Common Pitfalls

* **Too many small classes** if overused.
* **Wrong abstraction** can lead to complexity.
* **Forgetting default/null strategies** can cause runtime issues.

---

## ✅ Real-World Use Cases

* Payment Gateways
* Sorting Algorithms (e.g., Comparator)
* Compression (ZIP, RAR, GZIP)
* Authentication Strategies (OAuth, JWT, LDAP)
* Route finding in GPS apps

---

## 🔚 Conclusion

The **Strategy Pattern** is a powerful way to inject flexibility into your application by **decoupling algorithms from the context** where they’re used. It's an essential tool in your **LLD (Low-Level Design)** toolkit, especially for interview prep and scalable production systems.

---


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli


---

## 📚 Explore More Design Patterns in Java

* 🔒 [Mastering the Singleton Design Pattern in Java – A Complete Guide](https://dev.to/zeeshanali0704/mastering-the-singleton-design-pattern-in-java-a-complete-guide-13nn)
* ⚠️ [Why You Should Avoid Singleton Pattern in Modern Java Projects](https://dev.to/zeeshanali0704/why-you-should-avoid-singleton-pattern-in-modern-java-projects-3hff)
* 🏭 [Factory Design Pattern in Java – A Complete Guide](https://dev.to/zeeshanali0704/factory-design-pattern-in-java-a-complete-guide-dgj)
* 🧰 [Abstract Factory Design Pattern in Java – Complete Guide with Examples](https://dev.to/zeeshanali0704/abstract-factory-design-pattern-in-java-complete-guide-with-examples-1kld)
* 🧱 [Builder Design Pattern in Java – A Complete Guide](https://dev.to/zeeshanali0704/builder-design-pattern-in-java-a-complete-guide-2l41)
* 👀 [Observer Design Pattern in Java – Complete Guide](https://dev.to/zeeshanali0704/observer-design-pattern-in-java-complete-guide-1pe7)
* 🔌 [Adapter Design Pattern in Java – A Complete Guide](https://dev.to/zeeshanali0704/adapter-design-pattern-in-java-a-complete-guide-4aa2)
* 🔁 [Iterator Design Pattern in Java – Complete Guide](https://dev.to/zeeshanali0704/iterator-design-pattern-in-java-complete-guide-34fh)

---