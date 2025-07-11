
# 🍕 Decorator Design Pattern in Java — Explained with Pizza & Toppings

Writing clean, extensible, and maintainable object-oriented code is a challenge many developers face. One elegant solution for adding new behaviors without modifying existing code is the **Decorator Design Pattern**.

In this blog, we'll dive deep into the Decorator pattern using one of the most delicious real-world analogies: **Pizza and Toppings**!

---

## 📘 Table of Contents

* [What is the Decorator Pattern?](#what-is-the-decorator-pattern)
* [Real World Analogy](#real-world-analogy)
* [Problem Statement](#problem-statement)
* [Decorator Pattern Structure](#decorator-pattern-structure)
* [Java Implementation (Pizza Example)](#java-implementation-pizza-example)

  * Step 1: [Component Interface](#component-interface)
  * Step 2: [Concrete Component](#concrete-component)
  * Step 3: [Abstract Decorator](#abstract-decorator)
  * Step 4: [Concrete Decorators](#concrete-decorators)
  * Step 5: [Client Code](#client-code)
* [How the Decorator Pattern Works (Step by Step)](#how-the-decorator-pattern-works-step-by-step)
* [Advantages](#advantages)
* [Disadvantages](#disadvantages)
* [When to Use It](#when-to-use-it)
* [Real World Examples in Java API](#real-world-examples-in-java-api)
* [Conclusion](#conclusion)


---

## 🧠 What is the Decorator Pattern?

> The **Decorator Design Pattern** is a structural design pattern that lets you **dynamically attach new behaviors** or responsibilities to an object at **runtime** without altering its structure.

This is achieved by wrapping the original object inside a new object (called a **decorator**) that adds the new behavior.

✅ It follows the **Open/Closed Principle** — you can extend functionality without modifying existing code.

---

## 🍴 Real World Analogy

Imagine you run a pizza shop.

You sell a **base pizza**, and customers can choose to add **toppings** like:

* Extra cheese
* Olives
* Mushrooms
* Jalapeños

Creating a separate class for each topping combination like `PizzaWithCheeseAndOlives` would be chaotic. Instead, what if you could **dynamically wrap** a pizza with different toppings?

That’s exactly what the Decorator Pattern allows you to do.

---

## 🚨 Problem Statement

Without decorators, you might end up with many subclasses like:

```java
class PizzaWithCheese {}
class PizzaWithCheeseAndOlives {}
class PizzaWithMushroomsAndCheeseAndOlives {}
// ... and so on
```

This quickly becomes **unmanageable** as combinations grow.

---

## 🧱 Decorator Pattern Structure

| Role              | Responsibility                        | Example                         |
| ----------------- | ------------------------------------- | ------------------------------- |
| Component         | Interface or abstract class           | `Pizza`                         |
| ConcreteComponent | Real object implementing Component    | `MargheritaPizza`               |
| Decorator         | Abstract class implementing Component | `PizzaDecorator`                |
| ConcreteDecorator | Adds additional behavior              | `CheeseTopping`, `OliveTopping` |

---

## ☕ Java Implementation (Pizza Example)

### 1️⃣ Component Interface

```java
public interface Pizza {
    String getDescription();
    double getCost();
}
```

---

### 2️⃣ Concrete Component

```java
public class MargheritaPizza implements Pizza {
    @Override
    public String getDescription() {
        return "Margherita Pizza";
    }

    @Override
    public double getCost() {
        return 200.0;
    }
}
```

---

### 3️⃣ Abstract Decorator

```java
public abstract class PizzaDecorator implements Pizza {
    protected Pizza pizza;

    public PizzaDecorator(Pizza pizza) {
        this.pizza = pizza;
    }

    public String getDescription() {
        return pizza.getDescription();
    }

    public double getCost() {
        return pizza.getCost();
    }
}
```

---

### 4️⃣ Concrete Decorators

```java
public class CheeseTopping extends PizzaDecorator {
    public CheeseTopping(Pizza pizza) {
        super(pizza);
    }

    public String getDescription() {
        return pizza.getDescription() + ", Extra Cheese";
    }

    public double getCost() {
        return pizza.getCost() + 50.0;
    }
}
```

```java
public class OliveTopping extends PizzaDecorator {
    public OliveTopping(Pizza pizza) {
        super(pizza);
    }

    public String getDescription() {
        return pizza.getDescription() + ", Olives";
    }

    public double getCost() {
        return pizza.getCost() + 30.0;
    }
}
```

---

### 5️⃣ Client Code

```java
public class PizzaShop {
    public static void main(String[] args) {
        Pizza pizza = new MargheritaPizza();
        System.out.println(pizza.getDescription() + " => ₹" + pizza.getCost());

        pizza = new CheeseTopping(pizza);
        pizza = new OliveTopping(pizza);

        System.out.println(pizza.getDescription() + " => ₹" + pizza.getCost());
    }
}
```

**Output:**

```
Margherita Pizza => ₹200.0
Margherita Pizza, Extra Cheese, Olives => ₹280.0
```

---

## 🔍 How the Decorator Pattern Works (Step by Step)

```java
Pizza pizza = new OliveTopping(
                  new CheeseTopping(
                      new MargheritaPizza()));
```

**Call Flow for `getDescription()`**:

* `OliveTopping.getDescription()` → calls

  * `CheeseTopping.getDescription()` → calls

    * `MargheritaPizza.getDescription()` → `"Margherita Pizza"`
  * adds `", Extra Cheese"`
* adds `", Olives"`

Final: `"Margherita Pizza, Extra Cheese, Olives"`

**Call Flow for `getCost()`**:

* Base Pizza = `200.0`
* Cheese = `+50.0`
* Olives = `+30.0`
* Final = `₹280.0`

✅ The decorators **wrap** and **extend** the behavior dynamically.

---

## ✅ Advantages

* **Open/Closed Principle**: Add behavior without modifying existing code
* **Flexible**: Combine decorators in any order
* **Avoids subclass explosion**
* **Runtime customization**: Change behavior during execution

---

## ❌ Disadvantages

* Many small classes for each decorator
* Debugging becomes harder with deep nesting
* Order of decorators matters

---

## 🛠 When to Use It

Use the Decorator Pattern when:

* You need to add **optional features or behaviors**
* You want to avoid large class hierarchies
* You need **dynamic behavior composition** at runtime

---

## 🧪 Real World Examples in Java API

* `java.io.InputStream`, `OutputStream`, `BufferedReader`, etc.
* `java.util.Collections.unmodifiableList()` — wraps a list to prevent mutation

---

## 🧁 Conclusion

The **Decorator Design Pattern** is a flexible and powerful tool that helps you extend behavior without changing existing code. By using composition over inheritance, you keep your code modular, extensible, and maintainable.

**Key Takeaway**:

> Decorators are like toppings on a pizza — each one adds flavor (behavior) while wrapping the same base pizza!

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
* 🔌 [Strategy Design Pattern in Java – A Complete Guide](https://dev.to/zeeshanali0704/strategy-design-pattern-in-java-a-complete-guide-3hjn)
---