

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [What is the Abstract Factory Pattern?](#what-is-the-abstract-factory-pattern)
3. [Key Concepts](#key-concepts)
4. [Real-World Analogy](#real-world-analogy)
5. [Java Example: GUI Toolkit](#java-example-gui-toolkit)
5.1. [Abstract Product Interfaces](#abstract-product-interfaces) 
5.2. [Concrete Products](#concrete-products) 
5.3. [Abstract Factory](#abstract-factory) 
5.4. [Concrete Factories](#concrete-factories) 
5.5. [Client Code](#client-code) 
5.6. [Application Runner](#application-runner)
6. [When to Use Abstract Factory](#when-to-use-abstract-factory)
7. [Advantages](#advantages)
8. [Disadvantages](#disadvantages)
9. [Why Use Abstract Factory](#why-use-abstract-factory)
10. [Why Not Use It?](#why-not-use-it)
11. [Alternative Patterns](#alternative-patterns)
12. [Conclusion](#conclusion)
13. [Factory Method vs Abstract Factory Detailed Comparison](#factory-method-vs-abstract-factory-detailed-comparison)

---

# Abstract Factory Design Pattern in Java: Complete Guide with Examples

> *“Design patterns are the blueprints for building scalable and maintainable software. Among them, the Abstract Factory pattern stands out when you need to build families of related objects.”*

---

## Introduction

Software systems often need to create families of related objects. For instance, a GUI toolkit might support different themes (dark, light) or platforms (Windows, macOS). In such cases, how can you ensure that components like buttons and checkboxes match the selected theme or platform?

That’s where the **Abstract Factory** pattern shines.

---

## What is the Abstract Factory Pattern?

The **Abstract Factory Pattern** is a **creational design pattern** that provides an interface for creating **families of related or dependent objects** without specifying their concrete classes.

Think of it as:

> “A factory of factories.”

Instead of creating objects directly, the client uses a factory that produces other factories, each responsible for creating a set of related objects.

---

## Key Concepts

| Concept              | Description                                                             |
| -------------------- | ----------------------------------------------------------------------- |
| **Abstract Factory** | Declares interfaces for creating a family of related objects.           |
| **Concrete Factory** | Implements the creation of specific objects.                            |
| **Abstract Product** | Interface for a type of object.                                         |
| **Concrete Product** | Actual implementation of the product.                                   |
| **Client**           | Uses the factory to get objects but never knows their concrete classes. |

---

## Real-World Analogy

Imagine you're designing furniture for two types of houses: **Victorian** and **Modern**. You need a **Chair** and a **Table** for both styles.

You don’t want to accidentally place a Modern Chair in a Victorian room. So you create a factory for each style:

* `VictorianFurnitureFactory`: creates `VictorianChair` and `VictorianTable`
* `ModernFurnitureFactory`: creates `ModernChair` and `ModernTable`

Your client just selects the style and gets the matching furniture without worrying about the internal details.

---

## Java Example: GUI Toolkit

Let’s design a GUI framework that supports **Windows** and **macOS** platforms. Each platform has its own version of `Button` and `Checkbox`.

---

### Abstract Product Interfaces

```java
public interface Button {
    void paint();
}

public interface Checkbox {
    void paint();
}
```

---

### Concrete Products

```java
public class WindowsButton implements Button {
    public void paint() {
        System.out.println("Rendered Windows Button");
    }
}

public class MacButton implements Button {
    public void paint() {
        System.out.println("Rendered Mac Button");
    }
}

public class WindowsCheckbox implements Checkbox {
    public void paint() {
        System.out.println("Rendered Windows Checkbox");
    }
}

public class MacCheckbox implements Checkbox {
    public void paint() {
        System.out.println("Rendered Mac Checkbox");
    }
}
```

---

### Abstract Factory

```java
public interface GUIFactory {
    Button createButton();
    Checkbox createCheckbox();
}
```

---

### Concrete Factories

```java
public class WindowsFactory implements GUIFactory {
    public Button createButton() {
        return new WindowsButton();
    }

    public Checkbox createCheckbox() {
        return new WindowsCheckbox();
    }
}

public class MacFactory implements GUIFactory {
    public Button createButton() {
        return new MacButton();
    }

    public Checkbox createCheckbox() {
        return new MacCheckbox();
    }
}
```

---

### Client Code

```java
public class Application {
    private Button button;
    private Checkbox checkbox;

    public Application(GUIFactory factory) {
        button = factory.createButton();
        checkbox = factory.createCheckbox();
    }

    public void render() {
        button.paint();
        checkbox.paint();
    }
}
```

---

### Application Runner

```java
public class Demo {
    public static void main(String[] args) {
        GUIFactory factory;

        String osName = System.getProperty("os.name").toLowerCase();

        if (osName.contains("mac")) {
            factory = new MacFactory();
        } else {
            factory = new WindowsFactory();
        }

        Application app = new Application(factory);
        app.render();
    }
}
```

---

## When to Use Abstract Factory

| Use Case          | Description                                                              |
| ----------------- | ------------------------------------------------------------------------ |
| Cross-platform UI | Different UIs for different OS with consistent theme                     |
| Theming System    | Dark/Light/Custom UI themes with consistent look                         |
| Game Environments | Create consistent game components per environment (e.g., Forest, Desert) |
| Database Engines  | Abstract queries, drivers for different DBs (MySQL, Oracle, Postgres)    |

---

## Advantages

1. **Consistency Across Products**
   Ensures all related objects work well together.

2. **Encapsulation of Object Creation**
   Client is decoupled from specific classes.

3. **Scalability**
   Adding a new family of objects is easy—just create a new factory.

4. **Code Flexibility**
   Helps support different configurations (themes, OS) at runtime.

---

## Disadvantages

1. **Complex Structure**
   Involves multiple interfaces and classes, which may feel like over-engineering for small apps.

2. **Rigid Design**
   Adding a new product type requires changes in all factory classes.

3. **Too Abstract**
   Might be hard to trace instantiation if overused in large codebases.

---

## Why Use Abstract Factory

| Reason                          | Benefit                                           |
| ------------------------------- | ------------------------------------------------- |
| Decouple creation logic         | Easier testing, mocking, swapping implementations |
| Ensure compatibility            | Prevents mismatches in product families           |
| Support multiple configurations | Switch behavior based on environment              |

---

## Why Not Use It?

* You only need **one type of object**? Use **Factory Method**.
* Adding **new product types frequently**? Consider **Builder** or **Dependency Injection**.
* Small/simple system? Abstract Factory may be overkill.

---

## Alternative Patterns

| Pattern                  | When to Use                                           |
| ------------------------ | ----------------------------------------------------- |
| **Factory Method**       | Single product type, more flexibility                 |
| **Builder**              | Complex object construction                           |
| **Prototype**            | When you need to clone objects                        |
| **Service Locator**      | Central registry of services                          |
| **Dependency Injection** | Use with Spring/Guice for dynamic object provisioning |

---

## Conclusion

The **Abstract Factory Pattern** is a powerful design pattern that ensures consistency, decouples product creation, and enables scalable architectures. When used appropriately, it can significantly improve code maintainability and flexibility.

However, with great power comes great responsibility. Avoid it if your application is small or product families are unlikely to grow.

---

Great! Since you're working with both **Factory Method** and **Abstract Factory**, it's important to understand how they're **related** yet **distinct**.

---

## Factory Method vs Abstract Factory Detailed Comparison

| Feature                       | **Factory Method**                                      | **Abstract Factory**                                                        |
| ----------------------------- | ------------------------------------------------------- | --------------------------------------------------------------------------- |
| **Definition**                | Creates objects using a method in a class               | Creates **families of related objects** without specifying concrete classes |
| **Design Pattern Type**       | **Creational**                                          | **Creational**                                                              |
| **Core Intent**               | Let subclasses decide which class to instantiate        | Provide an interface for creating families of related/dependent objects     |
| **Scale**                     | Produces **one** product                                | Produces **multiple** related products                                      |
| **Hierarchy**                 | Based on inheritance (single level of factory)          | Composition over inheritance (multiple factories for related objects)       |
| **Example Analogy**           | Pizza Store that creates one type of Pizza              | UI Toolkit that creates related components like Button, TextField etc.      |
| **Common Use Case**           | When the exact class to instantiate is known at runtime | When you need consistency across related products or configurations         |
| **Extensibility**             | Easy to add new products via subclassing the factory    | Requires new factories for new product families                             |
| **Implementation Complexity** | Simpler                                                 | More complex                                                                |

---

## ✅ Factory Method – Summary

> A **single factory class** with a method that returns a **single object type**, usually using inheritance.

### 🔧 Example:

```java
public interface Employee {
    void work();
}

public class WebDeveloper implements Employee {
    public void work() {
        System.out.println("Web development work");
    }
}

public class AndroidDeveloper implements Employee {
    public void work() {
        System.out.println("Android development work");
    }
}

public class EmployeeFactory {
    public static Employee getEmployee(String type) {
        if (type.equalsIgnoreCase("WEB")) return new WebDeveloper();
        if (type.equalsIgnoreCase("ANDROID")) return new AndroidDeveloper();
        throw new IllegalArgumentException("Unknown employee type");
    }
}
```

---

## 🧱 Abstract Factory – Summary

> A **super-factory** that creates **other factories**, each responsible for a group of related products.

### 🔧 Example:

```java
// Products
interface Button { void render(); }
interface TextBox { void display(); }

// Concrete Products
class WindowsButton implements Button { public void render() { System.out.println("Windows Button"); } }
class MacButton implements Button { public void render() { System.out.println("Mac Button"); } }

class WindowsTextBox implements TextBox { public void display() { System.out.println("Windows TextBox"); } }
class MacTextBox implements TextBox { public void display() { System.out.println("Mac TextBox"); } }

// Abstract Factory
interface GUIFactory {
    Button createButton();
    TextBox createTextBox();
}

// Concrete Factories
class WindowsFactory implements GUIFactory {
    public Button createButton() { return new WindowsButton(); }
    public TextBox createTextBox() { return new WindowsTextBox(); }
}

class MacFactory implements GUIFactory {
    public Button createButton() { return new MacButton(); }
    public TextBox createTextBox() { return new MacTextBox(); }
}
```

---

## 🧠 Key Differences in Summary

| Aspect          | **Factory Method**                      | **Abstract Factory**                            |
| --------------- | --------------------------------------- | ----------------------------------------------- |
| Products        | One at a time                           | Multiple related products                       |
| Purpose         | Delegates object creation to subclasses | Encapsulates group of factories                 |
| Flexibility     | Lower – tied to inheritance             | Higher – uses composition for flexible grouping |
| Object Families | Not handled                             | Specifically made for related object families   |

---

## 📌 When to Use What?

| Scenario                                                                            | Pattern            |
| ----------------------------------------------------------------------------------- | ------------------ |
| You need a simple way to instantiate different subclasses                           | ✅ Factory Method   |
| You want to create multiple related objects together (e.g. GUI Toolkit for Mac/Win) | ✅ Abstract Factory |
| You want to enforce consistency among objects                                       | ✅ Abstract Factory |
| You're concerned only with creating one object                                      | ✅ Factory Method   |

---

## 📚 Visual Summary

```
Factory Method:
  [Creator]
      |
  +---+---+
  |       |
[WebDev] [AndroidDev]

Abstract Factory:
             [AbstractFactory]
               /          \
      [WinFactory]      [MacFactory]
       /     \           /          \
    [Button][Textbox]  [Button].  [Textbox]
```

---


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli


