# 🏗️ Builder Design Pattern in Java – A Complete Guide

## 📚 Table of Contents

1. [Introduction](#introduction)
2. [Problem Statement](#problem-statement)
3. [What is the Builder Pattern?](#what-is-the-builder-pattern)
4. [Structure and UML](#structure-and-uml)
5. [Real world Analogy](#real-world-analogy)
6. [Builder Pattern in Java – Code Example](#builder-pattern-in-java--code-example)
7. [Advantages](#advantages)
8. [Disadvantages](#disadvantages)
9. [When to Use](#when-to-use)
10. [When Not to Use](#when-not-to-use)
11. [Alternatives](#alternatives)
12. [Common Pitfalls](#common-pitfalls)
13. [Conclusion](#conclusion)

---

## 🧩 Introduction

The Builder Design Pattern is a **creational pattern** used to construct **complex objects step-by-step**, especially when the object has many optional or configurable parameters. It helps in creating immutable objects in a clean and readable way.

---

## 🚧 Problem Statement

Imagine you're creating a `User` object with many fields like:

```java
User user = new User("John", "Doe", 30, "john@example.com", "123 Street", "Engineer", false, "India");
```

With this constructor, it's hard to:

* Know what each parameter means.
* Set only the required fields and ignore optional ones.
* Prevent errors in parameter ordering.

This is where the **Builder Pattern** shines.

---

## 🏗️ What is the Builder Pattern?

> The Builder Pattern separates the construction of a complex object from its representation, allowing the same construction process to create different representations.

In simple terms, it allows building an object **step-by-step** using **method chaining** while keeping the object **immutable** once built.

---

## 🔧 Structure and UML

**Participants:**

* **Product** – The object that is being built.
* **Builder** – Abstract interface defining the building steps.
* **ConcreteBuilder** – Implements the Builder steps.
* **Director (optional)** – Manages the construction process.

```text
Client --> Director --> Builder --> ConcreteBuilder --> Product
```

---

## 🍔 Real world Analogy

Imagine ordering a burger:

* You want a Veg burger 🍔 with cheese 🧀 but no lettuce 🥬.
* A **Builder** lets you construct it step-by-step:

  ```java
  Burger burger = new Burger.Builder()
                     .addBun()
                     .addPatty("Veg")
                     .addCheese()
                     .build();
  ```

Each step configures a part of the final product.

---

## 💻 Builder Pattern in Java – Code Example

### 1. Traditional Class with Many Parameters (Problematic)

```java
public class User {
    public User(String firstName, String lastName, int age, String email, String address, String occupation) {
        // constructor with 6+ params
    }
}
```

### 2. Builder Implementation

#### ✅ Product Class (`User`)

```java
public class User {
    // required fields
    private final String firstName;
    private final String lastName;

    // optional fields
    private final int age;
    private final String email;
    private final String address;

    private User(Builder builder) {
        this.firstName = builder.firstName;
        this.lastName = builder.lastName;
        this.age = builder.age;
        this.email = builder.email;
        this.address = builder.address;
    }

    public static class Builder {
        private final String firstName;
        private final String lastName;
        private int age;
        private String email;
        private String address;

        public Builder(String firstName, String lastName) {
            this.firstName = firstName;
            this.lastName = lastName;
        }

        public Builder age(int age) {
            this.age = age;
            return this;
        }

        public Builder email(String email) {
            this.email = email;
            return this;
        }

        public Builder address(String address) {
            this.address = address;
            return this;
        }

        public User build() {
            return new User(this);
        }
    }
}
```

### ✅ Client Usage

```java
User user = new User.Builder("John", "Doe")
                   .age(30)
                   .email("john@example.com")
                   .build();
```

---

## ✅ Advantages

| Benefit                            | Description                                                       |
| ---------------------------------- | ----------------------------------------------------------------- |
| 🧼 **Readable & Clean Code**       | Easy to understand which field is being set.                      |
| 📦 **Handles Optional Parameters** | Only set what you need.                                           |
| 🔐 **Immutable Objects**           | Builder typically returns fully initialized, immutable objects.   |
| ⚙️ **Flexible Construction**       | You can reuse the same builder to construct different variants.   |
| 🧪 **Improved Testability**        | Easier to mock and build test objects with only necessary fields. |

---

## ❌ Disadvantages

| Drawback                           | Description                                                    |
| ---------------------------------- | -------------------------------------------------------------- |
| 🔁 **Boilerplate Code**            | You may need to duplicate fields in builder class.             |
| 📈 **Overkill for Simple Objects** | If object has 2–3 fields, Builder adds unnecessary complexity. |
| 🔄 **Mutation Risk**               | If not implemented correctly, can expose mutable state.        |
| 📦 **Code Bloat**                  | For every class, you may end up with another inner class.      |

---

## 🕐 When to Use

Use Builder Pattern when:

* Object has **many fields**, some of which are **optional**.
* You want to **avoid telescoping constructors** (constructors with many parameters).
* You want to create **immutable objects** with flexible construction.
* You have a class where certain combinations of fields are **conditionally dependent**.

---

## 🚫 When Not to Use

Avoid Builder Pattern when:

* The object has **very few fields** (e.g., 2–3).
* You don’t need to **reuse building logic**.
* You’re okay with using setters or telescoping constructors for basic models.

---

## 🔁 Alternatives

| Alternative                     | Use When                                                                         |
| ------------------------------- | -------------------------------------------------------------------------------- |
| ✅ **Telescoping Constructors**  | Few parameters and simple construction.                                          |
| ✅ **JavaBeans (Setters)**       | You need mutability and simple POJOs.                                            |
| ✅ **Factory Pattern**           | Object creation is based on **logic or parameters**, not configuration chaining. |
| ✅ **Lombok @Builder**           | You want Builder with less boilerplate.                                          |
| ✅ **Record Classes (Java 16+)** | For immutable data-holding objects with minimal logic.                           |

---

## 🧨 Common Pitfalls

| Pitfall                                         | Fix                                                           |
| ----------------------------------------------- | ------------------------------------------------------------- |
| ❗ Forgetting `build()` method                   | Ensure your `Builder` class always terminates with `build()`. |
| ❗ Mutable shared builder instance               | Avoid reusing builders across threads unless it’s stateless.  |
| ❗ Public setters in final class                 | Prefer private constructors and builder usage only.           |
| ❗ Misaligned fields between Builder and Product | Keep builder and product fields in sync.                      |

---

## 🏁 Conclusion

The **Builder Design Pattern** is a powerful tool for creating complex, readable, and maintainable object construction logic in Java. It's a go-to pattern for domain models, DTOs, configuration objects, and anywhere flexibility in creation is important.

> 💡 Use the Builder Pattern for clarity, safety, and scalability—especially when constructors just don’t cut it.


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