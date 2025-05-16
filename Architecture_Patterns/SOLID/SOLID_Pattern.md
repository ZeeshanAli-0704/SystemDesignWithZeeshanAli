## Table of Contents

- [Single Responsibility Principle (SRP)](#single-responsibility-principle-srp)
- [Open Closed Principle (OCP)](#open-closed-principle-ocp)
- [Liskov Substitution Principle (LSP)](#liskov-substitution-principle-lsp)
- [Interface Segregation Principle (ISP)](#interface-segregation-principle-isp)
- [Dependency Inversion Principle (DIP)](#dependency-inversion-principle-dip)
- [Final Thoughts](#final-thoughts)


# SOLID Principles in JavaScript — A Beginner-Friendly Guide

Writing clean, maintainable, and scalable code is key to being an effective developer. The SOLID principles are five object-oriented design guidelines that help achieve exactly that. In this post, we’ll explore each SOLID principle with simple, real-world JavaScript examples.

---

## 🔹 Single Responsibility Principle (SRP)

> A class or module should have only one reason to change.

### ❌ Bad Example

```js
class UserManager {
  createUser(user) {
    // Create user logic
  }

  saveUserToDB(user) {
    // Save to DB logic
  }

  sendWelcomeEmail(user) {
    // Email logic
  }
}
```

### ✅ Good Example

```js
class UserService {
  createUser(user) {
    // Create user
  }
}

class UserRepository {
  save(user) {
    // Save to DB
  }
}

class EmailService {
  sendWelcomeEmail(user) {
    // Send email
  }
}
```

Each class has one clear responsibility.

---

## 🔹 Open Closed Principle (OCP)

> Software entities should be open for extension but closed for modification.

### ❌ Bad Example

```js
function getDiscount(product) {
  if (product.type === "gold") {
    return product.price * 0.8;
  } else if (product.type === "silver") {
    return product.price * 0.9;
  }
}
```

### ✅ Good Example

```js
class Product {
  constructor(price) {
    this.price = price;
  }

  getDiscount() {
    return this.price;
  }
}

class GoldProduct extends Product {
  getDiscount() {
    return this.price * 0.8;
  }
}

class SilverProduct extends Product {
  getDiscount() {
    return this.price * 0.9;
  }
}
```

Easily extend functionality without changing existing code.

---

## 🔹 Liskov Substitution Principle (LSP)

> Subclasses should be substitutable for their base classes.

### ❌ Bad Example

```js
class Bird {
  fly() {
    console.log("Flying");
  }
}

class Ostrich extends Bird {
  fly() {
    throw new Error("Ostriches can't fly");
  }
}
```

### ✅ Good Example

```js
class Bird {}

class FlyingBird extends Bird {
  fly() {
    console.log("Flying");
  }
}

class Ostrich extends Bird {
  // No fly method
}
```

Now each subclass only provides behavior that makes sense for it.

---

## 🔹 Interface Segregation Principle (ISP)

> Clients should not be forced to depend on interfaces they do not use.

### ❌ Bad Example

```js
class Machine {
  print() {}
  scan() {}
  fax() {}
}

class OldPrinter extends Machine {
  print() {
    console.log("Printing...");
  }
  scan() {
    throw new Error("Not supported");
  }
  fax() {
    throw new Error("Not supported");
  }
}
```

### ✅ Good Example (Composition)

```js
class Printer {
  print() {
    console.log("Printing...");
  }
}

class Scanner {
  scan() {
    console.log("Scanning...");
  }
}

class MultiFunctionMachine {
  constructor(printer, scanner) {
    this.printer = printer;
    this.scanner = scanner;
  }

  print() {
    this.printer.print();
  }

  scan() {
    this.scanner.scan();
  }
}
```

Each class has focused responsibilities, and we compose them as needed.

---

## 🔹 Dependency Inversion Principle (DIP)

> High-level modules should not depend on low-level modules. Both should depend on abstractions.

### ✅ Good Example

```js
class Database {
  connect() {
    throw new Error("This method should be overridden.");
  }
}

class MySQLDatabase extends Database {
  connect() {
    console.log("Connected to MySQL");
  }
}

class App {
  constructor(database) {
    this.db = database;
  }

  init() {
    this.db.connect();
  }
}

const db = new MySQLDatabase();
const app = new App(db);
app.init(); // Connected to MySQL
```

This allows us to easily switch to another database or mock one in tests.

---

## Final Thoughts

| Principle | Summary                                      |
| --------- | -------------------------------------------- |
| SRP       | One responsibility per class/module          |
| OCP       | Extend, don’t modify existing code           |
| LSP       | Subclasses must behave like parents          |
| ISP       | Don’t force unused behaviors                 |
| DIP       | Depend on abstractions, not concrete classes |

By applying these principles, you can build software that is flexible, testable, and easier to maintain. Happy coding! 🚀




![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/kehpoavvm5b1sjf49849.png)


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli