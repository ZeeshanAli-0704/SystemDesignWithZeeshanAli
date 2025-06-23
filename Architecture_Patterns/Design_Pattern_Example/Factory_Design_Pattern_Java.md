
## 📚 Table of Contents

1. [What is the Factory Design Pattern?](#-what-is-the-factory-design-pattern)
2. [When to Use It?](#when-to-use-it)
3. [Real world Use Case](#real-world-use-case)
4. [Java Code Example](#java-code-example)
5. [Advantages](#advantages)
6. [Disadvantages](#disadvantages)
7. [How to Break Factory Pattern](#how-to-break-factory-pattern)
8. [When NOT to Use Factory Pattern](#when-not-to-use-factory-pattern)
9. [Alternatives to Factory Pattern](#alternatives-to-factory-pattern)
10. [Factory vs Factory Method vs Abstract Factory](#factory-vs-factory-method-vs-abstract-factory)
11. [Summary](#summary)


# Factory Design Pattern in Java: A Complete Guide

## What is the Factory Design Pattern?

The **Factory Design Pattern** is a **creational design pattern** that provides an interface for creating objects in a **superclass**, but allows **subclasses to alter the type of objects that will be created**.

> It helps in **delegating the instantiation logic to a factory class**, instead of creating objects using the `new` keyword directly in the client code.

---

## When to Use It?

* When the object creation process is **complex or repetitive**
* When the system should be **independent of how its objects are created**
* When you need to **introduce new types of products** without changing existing code
* When there are **multiple subclasses** of a class, and you need to instantiate them conditionally

---

## Real world Use Case

Suppose you’re building a **notification system** that supports multiple channels: Email, SMS, and Push.

Each notification type has a different creation logic or configuration. Instead of writing `new EmailNotification()`, `new SMSNotification()`, etc., everywhere, you can use a **NotificationFactory**.

---

## Java Code Example

### Step 1: Create a Common Interface

```java
public interface Notification {
    void notifyUser();
}
```

### Step 2: Implement Concrete Classes

```java
public class EmailNotification implements Notification {
    public void notifyUser() {
        System.out.println("Sending an Email Notification");
    }
}

public class SMSNotification implements Notification {
    public void notifyUser() {
        System.out.println("Sending an SMS Notification");
    }
}

public class PushNotification implements Notification {
    public void notifyUser() {
        System.out.println("Sending a Push Notification");
    }
}
```

### Step 3: Create Factory Class

```java
public class NotificationFactory {

    public Notification createNotification(String type) {
        if (type == null || type.isEmpty()) return null;
        switch (type.toLowerCase()) {
            case "sms":
                return new SMSNotification();
            case "email":
                return new EmailNotification();
            case "push":
                return new PushNotification();
            default:
                throw new IllegalArgumentException("Unknown notification type: " + type);
        }
    }
}
```

### Step 4: Use It in Client Code

```java
public class Main {
    public static void main(String[] args) {
        NotificationFactory factory = new NotificationFactory();
        
        Notification notification = factory.createNotification("email");
        notification.notifyUser();
    }
}
```

---

## Advantages

| Benefit           | Description                                                    |
| ----------------- | -------------------------------------------------------------- |
| 🔄 Loose Coupling | Clients depend only on interfaces, not concrete classes.       |
| ➕ Easy to Extend  | Add new product classes with minimal changes to factory.       |
| 🤐 Encapsulation  | Hides complex instantiation logic.                             |
| 🧪 Testable       | Easily mock the factory to produce fake objects in unit tests. |

---

## Disadvantages

| Drawback                          | Description                                                           |
| --------------------------------- | --------------------------------------------------------------------- |
| ❌ Code Duplication                | You might write many factory classes for different types.             |
| ❓ Type Safety                     | You often use strings or enums, which aren’t checked at compile time. |
| ⚒️ Maintenance Overhead           | As product types grow, factory switch-case can get bloated.           |
| 📦 Violates Open/Closed Principle | Adding new types may require modifying existing factory code.         |

---

## How to Break Factory Pattern

Sometimes the Factory Pattern becomes an anti-pattern when:

* You use it **just to avoid `new`**, even when simple DI would suffice.
* The factory becomes a **God class**, creating dozens of unrelated objects.
* **Too many conditionals (if/else or switch)** are used — becomes a **maintenance nightmare**.
* You start **injecting factory classes into every service**, making it harder to test and debug.

---

## When NOT to Use Factory Pattern

* When object creation is **simple and unlikely to change**
* When you can easily achieve dependency inversion using **Dependency Injection (DI)** frameworks like **Spring**
* When **subclassing isn't involved**, and the object construction is direct
* When you’re building **small scripts or throwaway code**

---

## Alternatives to Factory Pattern

| Alternative                   | When to Use                                                                                  |
| ----------------------------- | -------------------------------------------------------------------------------------------- |
| **Dependency Injection (DI)** | When using a framework like Spring, it manages the object lifecycle for you.                 |
| **Builder Pattern**           | When you have many optional parameters or configuration steps.                               |
| **Service Locator Pattern**   | When you want to look up services dynamically (though considered an anti-pattern sometimes). |
| **Factory Method Pattern**    | When subclasses decide which class to instantiate (i.e., deferred to child classes).         |

---

## Factory vs Factory Method vs Abstract Factory

| Pattern              | Key Idea                                       | Example                                             |
| -------------------- | ---------------------------------------------- | --------------------------------------------------- |
| **Factory**          | One central class creates objects              | `NotificationFactory.createNotification("sms")`     |
| **Factory Method**   | Subclasses override a method to create objects | `public abstract Notification createNotification()` |
| **Abstract Factory** | Group of factories for related families        | `UIFactory -> ButtonFactory, DialogFactory`         |

---

## Summary

* 🏗️ **Factory Pattern** abstracts the creation of objects.
* ✅ Use it when object creation logic is complex or varies.
* ❌ Avoid when overengineering small solutions or when DI suffices.
* 📈 Helps with **maintainability and scalability**, especially in large codebases.
* 🧰 Consider **Builder** or **DI** as cleaner alternatives in modern Spring-based Java applications.

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