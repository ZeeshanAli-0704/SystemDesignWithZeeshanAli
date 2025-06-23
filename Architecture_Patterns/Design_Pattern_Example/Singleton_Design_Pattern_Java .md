## 📚 Table of Contents

1. [What is Singleton Design Pattern?](#1-what-is-singleton-design-pattern)
2. [Why Use Singleton?](#2-why-use-singleton)
3. [Requirements of a Singleton Class](#3-requirements-of-a-singleton-class)
4. [Implementation Types](#4-implementation-types)
   * [A Eager Initialization](#a-eager-initialization)
   * [B Lazy Initialization](#b-lazy-initialization)
5. [Thread Safety in Singleton](#5-thread-safety-in-singleton)
   * [A Synchronized Method](#a-synchronized-method)
   * [B Double Checked Locking](#b-double-checked-locking)
6. [Real World Use Cases](#6-real-world-use-cases)
7. [Comparison Table](#7-comparison-table)
8. [Advantages](#8-advantages-of-singleton-pattern)
9. [Disadvantages](#9-disadvantages-of-singleton-pattern)
10. [Notes & Best Practices](#10-notes--best-practices)
11. [Quick Test](#11-quick-test)
12. [Conclusion](#12-conclusion)

# 🎯 **Singleton Design Pattern in Java**

> *“Ensure a class has only one instance and provide a global point of access to it.”*

---

## 🔍 1. What is Singleton Design Pattern?

The **Singleton Pattern** is a **creational design pattern** that ensures a class is instantiated **only once** during the application's lifecycle and provides **global access** to that instance.

---

## 📌 2. Why Use Singleton?

* 🔄 **Consistency**: All parts of the application use the same instance.
* 💾 **Memory Efficient**: Avoids creating multiple objects.
* 🔐 **Control Access**: Especially for shared resources like databases, loggers, caches, etc.

---

## 🧱 3. Basic Requirements of a Singleton Class

1. **Private Constructor**: Prevents instantiation from outside the class.
2. **Static Reference**: Holds the single instance of the class.
3. **Public Static Method**: Returns the singleton instance.

---

## ⚙️ 4. Implementation Types

### ✅ A Eager Initialization

> The instance is created when the class is loaded (whether you use it or not).

```java
public class EagerSingleton {
    private static final EagerSingleton instance = new EagerSingleton();

    private EagerSingleton() {}

    public static EagerSingleton getInstance() {
        return instance;
    }
}
```

#### ✅ **Advantages**

* Thread-safe by default (because of class loading).
* Simple to implement.

#### ❌ **Disadvantages**

* Instance is created even if not used.
* Not ideal when object creation is resource-heavy.

---

### ✅ B Lazy Initialization

> The instance is created only when needed.

```java
public class LazySingleton {
    private static LazySingleton instance;

    private LazySingleton() {}

    public static LazySingleton getInstance() {
        if (instance == null) {
            instance = new LazySingleton();
        }
        return instance;
    }
}
```

#### ✅ **Advantages**

* Saves memory if instance is never needed.

#### ❌ **Disadvantages**

* **Not thread-safe.** In a multithreaded environment, two threads may create different instances.

---

## 🧵 5. Thread Safety in Singleton

### 🚫 Problem:

Two threads might simultaneously enter the `getInstance()` method and create **multiple instances** — breaking the singleton guarantee.

### 🔒 Solutions:

---

### ✅ A Synchronized Method

```java
public class ThreadSafeSingleton {
    private static ThreadSafeSingleton instance;

    private ThreadSafeSingleton() {}

    public static synchronized ThreadSafeSingleton getInstance() {
        if (instance == null) {
            instance = new ThreadSafeSingleton();
        }
        return instance;
    }
}
```

* ✅ Thread-safe
* ❌ Slower performance due to method-level lock

---

### ✅ B Double Checked Locking

```java
public class DoubleCheckedSingleton {
    private static volatile DoubleCheckedSingleton instance;

    private DoubleCheckedSingleton() {}

    public static DoubleCheckedSingleton getInstance() {
        if (instance == null) {
            synchronized (DoubleCheckedSingleton.class) {
                if (instance == null) {
                    instance = new DoubleCheckedSingleton();
                }
            }
        }
        return instance;
    }
}
```

* ✅ Lazy loaded
* ✅ Thread-safe
* ✅ High performance
* 🔒 Requires `volatile` to prevent instruction reordering

---

## ✅ 6. Real World Use Cases

| Use Case        | Why Singleton?                                     |
| --------------- | -------------------------------------------------- |
| JDBC Connection | Single DB connection manager to avoid new creation |
| Logger Utility  | Central logging system for the entire application  |
| Configuration   | Global configuration manager instance              |
| Cache Manager   | Central cache layer shared across app modules      |
| File System     | Access to shared file resources                    |

---

## 📊 7. Comparison Table

| Approach                | Lazy? | Thread-Safe? | Performance | Use Case         |
| ----------------------- | ----- | ------------ | ----------- | ---------------- |
| Eager Initialization    | ❌     | ✅            | Fast        | Lightweight obj  |
| Lazy Initialization     | ✅     | ❌            | Poor (MT)   | Simple apps      |
| Synchronized Method     | ✅     | ✅            | Slow        | Small-scale apps |
| Double-Checked Locking  | ✅     | ✅            | Good        | Web/Server apps  |

---

## ✅ 8. Advantages of Singleton Pattern

| 👍 Pros                                              |
| ---------------------------------------------------- |
| Ensures a single instance across app lifecycle       |
| Saves memory by preventing redundant object creation |
| Globally accessible instance (controlled access)     |
| Great for shared resources (DB, Logger, Configs)     |

---

## ❌ 9. Disadvantages of Singleton Pattern

| 👎 Cons                                                    |
| ---------------------------------------------------------- |
| Difficult to test (mocking is tricky)                      |
| Introduces global state (violates OOP encapsulation)       |
| Hidden dependencies across classes                         |
| Thread safety can be hard to ensure (if not properly done) |

---

## 📝 10. Notes & Best Practices

* Always prefer `Bill Pugh Singleton` or `Enum Singleton` for thread safety & performance.
* Avoid excessive global access. Use dependency injection where possible.
* Singleton is an **anti-pattern** in some contexts (because it can hide dependencies).
* Use `volatile` with double-checked locking to ensure visibility across threads.
* Prefer stateless singletons to avoid concurrency issues.

---

## 🧪 11. Quick Test

```java
public class SingletonTest {
    public static void main(String[] args) {
        Runnable task = () -> {
            DoubleCheckedSingleton obj = DoubleCheckedSingleton.getInstance();
            System.out.println(Thread.currentThread().getName() + ": " + obj.hashCode());
        };

        Thread t1 = new Thread(task);
        Thread t2 = new Thread(task);
        t1.start(); t2.start();
    }
}
```

You should see the **same hashCode** from both threads.

---

## ✅ Conclusion

The Singleton Pattern is **simple**, **powerful**, and **commonly used**, but requires **careful handling in multithreaded environments**. If you're designing a service or resource that should only exist once, Singleton is your go-to pattern — just be mindful of its drawbacks and test carefully.

---



# 🚫 **Why *Not* to Use Singleton Pattern in Modern Projects**


## 📚 Table of Contents

1. [🚧 Challenges of Using Singleton Pattern in Modern Projects](#challenges-of-using-singleton-pattern-in-modern-projects)
   1.1 [Global State & Hidden Dependencies](#1-global-state--hidden-dependencies)
   1.2 [Testing Difficulty (Unit Tests)](#2-testing-difficulty-unit-tests)
   1.3 [Concurrency Issues](#3-concurrency-issues)
   1.4 [Violation of SOLID Principles](#4-violation-of-solid-principles)
   1.5 [Difficult Lifecycle Management](#5-difficult-lifecycle-management)
   1.6 [Serialization Cloning Issues](#6-serialization-cloning-issues)
   1.7 [Hard to Scale in Distributed Systems](#7-hard-to-scale-in-distributed-systems)

2. [❌ Why *NOT* to Use Singleton Pattern](#why-not-to-use-singleton-pattern)

3. [✅ When Singleton *Can Be Used* in Production (Safely)](#when-singleton-can-be-used-in-production-safely)

4. [✅ Modern Alternatives](#modern-alternatives)

5. [📌 Summary](#summary)

6. [✅ Final Verdict for Modern Projects](#final-verdict-for-modern-projects)


---

## 🚧 Challenges of Using Singleton Pattern in Modern Projects

### 1. **Global State & Hidden Dependencies**

* Singleton introduces **global state**.
* Makes it harder to track **where and how the instance is being mutated**.
* Breaks the **principle of explicit dependencies** (e.g., via constructor or DI).

> ❌ Leads to tightly coupled code and makes refactoring harder.

---

### 2. **Testing Difficulty (Unit Tests)**

* Singletons are hard to **mock or isolate** during unit testing.
* Cannot easily inject a **fake/mock implementation** of the singleton.

> ❌ Makes TDD (Test Driven Development) painful.

---

### 3. **Concurrency Issues**

* In multi-threaded environments, **improper singleton implementations** can result in:

  * Multiple instances
  * Race conditions
  * Deadlocks (if synchronized badly)

> ❌ Especially risky in frameworks that manage thread pools (e.g., Spring, web servers)

---

### 4. **Violation of SOLID Principles**

* Violates **Single Responsibility Principle (SRP)** by managing its own lifecycle.
* Violates **Dependency Inversion Principle (DIP)** by being directly referenced instead of being injected.

> ❌ Harder to maintain in layered or microservice architectures.

---

### 5. **Difficult Lifecycle Management**

* Most singleton implementations **live until application termination**.
* In frameworks like Spring, this could cause **memory leaks**, especially if holding large data structures or listeners.

---

### 6. **Serialization Cloning Issues**

* Singleton instances can be **duplicated unintentionally** during:

  * Object deserialization
  * Manual cloning via reflection

> ❌ Can defeat the singleton guarantee silently.

---

### 7. **Hard to Scale in Distributed Systems**

* Singleton pattern is **inherently in-memory and JVM-local**.
* Useless in **distributed or clustered systems** unless explicitly replicated/shared.

> ❌ You’ll need stateless services or centralized stores (like Redis or databases) instead.

---

## ❌ Why *NOT* to Use Singleton Pattern

| Reason                            | Explanation                                                              |
| --------------------------------- | ------------------------------------------------------------------------ |
| 🔒 Tightly coupled code           | Everything references the same instance directly                         |
| 🧪 Hard to test                   | Can’t easily mock or replace during unit testing                         |
| 🌐 Not cloud/distributed friendly | Singleton is local to the JVM, not across services                       |
| 🔁 Hidden logic                   | You can’t tell what depends on the singleton without digging into code   |
| 🧠 Difficult lifecycle management | You don’t control when it's destroyed or garbage collected               |
| ⚠️ Threading risk                 | Improper sync leads to bugs in multi-threaded apps                       |
| 🔁 Hard to reset                  | Can’t reinitialize without restarting app, which breaks dynamic reconfig |

---

## ✅ When Singleton *Can Be Used* in Production (Safely)

Use it **only** when these conditions apply:

| Condition                         | Explanation                                               |
| --------------------------------- | --------------------------------------------------------- |
| 💼 **Stateless or Read-Only**     | E.g., config reader, logger, or utility classes           |
| 🔐 **Thread-safe**                | Implemented with synchronization or via Enum/Bill Pugh    |
| ⚙️ **Used in Controlled Context** | Internal frameworks, toolkits, libraries, or SDKs         |
| 🧪 **Properly Testable**          | Mocked via interface or factory method                    |
| 🌱 **Managed by Framework**       | Spring, Guice, etc., handle lifecycle as a singleton bean |
| 🔁 **Limited Scope**              | Used only in a few places — not across the whole app      |

---

## ✅ Modern Alternatives

| Alternative                             | Use Case                                            |
| --------------------------------------- | --------------------------------------------------- |
| **Dependency Injection (DI)**           | Preferred for configurable & testable objects       |
| **Factory + Caching**                   | Lazily instantiate & cache instances (more control) |
| **Spring's `@Component` or `@Service`** | Scoped Singleton managed by container               |
| **Application-level Context Objects**   | E.g., holding user/session info across app layers   |
| **Static Holder with DI Support**       | Combine Singleton's intent with flexibility of DI   |

---

## 📌 Summary

| 🔴 Avoid Singleton When | 🟢 Use Singleton When                  |
| ----------------------- | -------------------------------------- |
| You need testability    | Class is stateless or read-only        |
| Your app is distributed | Shared, cached object across app       |
| You use DI frameworks   | Singleton is container-managed         |
| You want modularity     | Only one instance should exist         |
| Global state is risky   | E.g., logging, metrics, config readers |


🧠 Think of it Like This:

Singleton = “I’ll make sure there's only one of me, and I’ll give myself to anyone who asks.”


DI = “I don’t care who creates me, just hand me my dependencies when I’m needed.”


---

## ✅ Final Verdict for Modern Projects

* ❌ **Avoid writing your own Singleton unless you must.**
* ✅ Prefer **DI frameworks (like Spring)** to manage Singleton scope.
* ✅ If using Singleton, use **Bill Pugh or Enum** pattern for safety.
* ✅ Always **analyze testability, maintainability, and scalability** before applying Singleton.

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