# 🔌 Adapter Design Pattern in Java – A Complete Guide

## 📚 Table of Contents

* [What is the Adapter Design Pattern?](#what-is-the-adapter-design-pattern)
* [Key Participants](#key-participants)
* [Real world Analogy](#real-world-analogy)
* [UML Diagram](#uml-diagram-text-format)
* [Java Example](#java-example)

  * [1. Target Interface](#1-target-interface)
  * [2. Adaptee Class (Existing API)](#2-adaptee-class-existing-api)
  * [3. Adapter Class](#3-adapter-class)
  * [4. Client Code](#4-client-code)
* [Use Cases in Real World Systems](#use-cases-in-real-world-systems)
* [Advantages](#advantages)
* [Disadvantages](#disadvantages)
* [When to Use and Not to Use](#when-to-use-and-not-to-use)
* [Object Adapter vs Class Adapter](#object-adapter-vs-class-adapter)
* [Comparison with Similar Patterns](#comparison-with-similar-patterns)
* [Best Practices and Pitfalls](#best-practices-and-pitfalls)
* [Alternatives](#alternatives)
* [Summary](#summary)

---

## ✅ What is the Adapter Design Pattern?

The **Adapter Pattern** is a **structural design pattern** that allows objects with incompatible interfaces to work together by converting one interface into another the client expects.

> **Intent**: Bridge the gap between a new interface and an existing (often legacy) implementation.

This pattern acts as a **wrapper** around an existing class, exposing a new interface to the client.

---

## 👥 Key Participants

| Role        | Description                                                          |
| ----------- | -------------------------------------------------------------------- |
| **Target**  | The interface expected by the client.                                |
| **Adaptee** | The existing class that needs adapting.                              |
| **Adapter** | Implements the Target interface and translates calls to the Adaptee. |
| **Client**  | Uses the Target interface to interact with the system.               |

---

## 🧠 Real world Analogy

Think about a **mobile charger adapter**. Your laptop may have a USB-C port, but your phone uses Micro-USB. The **adapter** converts the USB-C output to Micro-USB input so your phone can charge — even though the two interfaces don’t match.

---

## 📊 UML Diagram (Text Format)

```
     +--------------+
     |   Client     |
     +--------------+
           |
           v
     +--------------+       +---------------+
     |   Target     |<------+   Adapter     |
     +--------------+       +---------------+
                            | - adaptee     |
                            +---------------+
                            | +request()    |
                            +---------------+
                                    |
                                    v
                            +---------------+
                            |   Adaptee     |
                            +---------------+
                            | +specificRequest() |
                            +---------------+
```

---

## 💻 Java Example

### 📘 Scenario: You have a **legacy payment system** (`OldPaymentGateway`) and want to integrate it with your **new `PaymentProcessor` interface**.

---

### 1. Target Interface

```java
public interface PaymentProcessor {
    void pay(String amount);
}
```

---

### 2. Adaptee Class (Existing API)

```java
public class OldPaymentGateway {
    public void makePayment(String amountInRupees) {
        System.out.println("Payment made using OldPaymentGateway: ₹" + amountInRupees);
    }
}
```

---

### 3. Adapter Class

```java
public class PaymentAdapter implements PaymentProcessor {
    private OldPaymentGateway oldPaymentGateway;

    public PaymentAdapter(OldPaymentGateway oldPaymentGateway) {
        this.oldPaymentGateway = oldPaymentGateway;
    }

    @Override
    public void pay(String amount) {
        // Adapting method call
        oldPaymentGateway.makePayment(amount);
    }
}
```

---

### 4. Client Code

```java
public class Main {
    public static void main(String[] args) {
        OldPaymentGateway oldGateway = new OldPaymentGateway();
        PaymentProcessor processor = new PaymentAdapter(oldGateway);

        processor.pay("2500");
    }
}
```

**Output:**

```
Payment made using OldPaymentGateway: ₹2500
```

---

## 🛠 Use Cases in Real World Systems

* Integrating **legacy systems** with modern interfaces
* Adapting third-party APIs (e.g., wrapping Stripe's API with your own payment layer)
* Making incompatible class libraries work together
* Wrapping classes with different naming conventions or method signatures
* Supporting backward compatibility

---

## ✅ Advantages

* Promotes **code reusability** by allowing integration of old components
* Improves **interoperability** between systems
* Provides **flexibility** in evolving interfaces
* Encourages **decoupling** between client and implementation

---

## ❌ Disadvantages

* Can increase complexity with too many adapters
* Might introduce **runtime overhead** with deep adapter chains
* Sometimes hides the **true power** of the Adaptee
* **Tight coupling** to Adaptee may remain if not designed carefully

---

## 🧭 When to Use and Not to Use

### ✅ Use When:

* You want to integrate an existing class but its interface doesn’t match
* You’re using third-party or legacy code
* You’re refactoring to support a new interface gradually

### ❌ Avoid When:

* You can directly modify the existing class
* You don’t control the Adaptee and behavior is unpredictable
* Adapter logic becomes too complex — consider Facade instead

---

## 🔁 Object Adapter vs Class Adapter

| Type               | Description                                                                             |
| ------------------ | --------------------------------------------------------------------------------------- |
| **Object Adapter** | Uses **composition** (holds Adaptee instance). More flexible.                           |
| **Class Adapter**  | Uses **inheritance** (extends Adaptee). Tightly coupled, limited to single inheritance. |

> Java only supports **Object Adapters** as multiple inheritance is not allowed.

---

## 🔍 Comparison with Similar Patterns

| Pattern       | Purpose                                                |
| ------------- | ------------------------------------------------------ |
| **Adapter**   | Convert one interface to another.                      |
| **Decorator** | Add behavior dynamically without changing interface.   |
| **Facade**    | Simplify a complex subsystem with a unified interface. |
| **Proxy**     | Provide a surrogate or placeholder.                    |

---

## ⚠️ Best Practices and Pitfalls

### ✅ Best Practices:

* Name adapters clearly (e.g., `PaymentAdapter`, `LegacyToNewAdapter`)
* Make adapters **lightweight**
* Favor **object composition** over inheritance
* Use **interfaces** to abstract clients from concrete adapters

### ❌ Pitfalls:

* Don't create adapter chains (adapter over adapter)
* Avoid adapting too many unrelated interfaces — use **Facade** instead
* Don’t tightly couple your Adapter to internal logic of Adaptee

---

## 🔄 Alternatives

| Alternative               | Use When                                      |
| ------------------------- | --------------------------------------------- |
| **Wrapper/Wrapper Class** | Simple alias or name change of methods        |
| **Facade Pattern**        | When you need to simplify multiple interfaces |
| **Strategy Pattern**      | When you need interchangeable algorithms      |
| **Bridge Pattern**        | To decouple abstraction from implementation   |

---

## 📌 Summary

* The **Adapter Pattern** bridges incompatible interfaces.
* Useful for integrating **legacy** or **third-party** APIs.
* Involves **Target**, **Adaptee**, **Adapter**, and **Client**.
* Java supports **Object Adapters** (via composition).
* Be cautious of complexity and avoid overusing adapters.
* Best used when refactoring or integrating external libraries.

--

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