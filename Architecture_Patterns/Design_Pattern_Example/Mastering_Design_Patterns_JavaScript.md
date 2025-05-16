
Design patterns are proven solutions to common software design problems. Whether you're building frontends or architecting backend systems, understanding these patterns can make your code more modular, flexible, and maintainable. In this blog, we’ll explore **six powerful design patterns** using **JavaScript**, each with simple explanations and code examples.

---

## Table of Contents

1. [Singleton Pattern](#1-singleton-pattern)
2. [Factory Method Pattern](#2-factory-method-pattern)
3. [Abstract Factory Pattern](#3-abstract-factory-pattern)
4. [Observer Pattern](#4-observer-pattern)
5. [Decorator Pattern](#5-decorator-pattern)
6. [Facade Pattern](#6-facade-pattern)

---

## 1. Singleton Pattern

> Ensure a class has only one instance and provide a global access point.

### Use Case

Useful when only one object should control shared resources, like configuration or logging.

### JavaScript Example

```js
class Singleton {
  constructor() {
    if (Singleton.instance) return Singleton.instance;
    this.config = {};
    Singleton.instance = this;
  }

  setConfig(key, value) {
    this.config[key] = value;
  }

  getConfig(key) {
    return this.config[key];
  }
}

const a = new Singleton();
a.setConfig("theme", "dark");
const b = new Singleton();
console.log(b.getConfig("theme")); // "dark"
```

---

## 2. Factory Method Pattern

> Define an interface for creating objects, but let the subclass or logic decide which class to instantiate.

### Use Case

Use this when creating different types of similar objects based on a condition.

### JavaScript Example

```js
class Developer {
  constructor(name) {
    this.name = name;
  }
  work() {
    console.log(`${this.name} is writing code`);
  }
}

class Tester {
  constructor(name) {
    this.name = name;
  }
  work() {
    console.log(`${this.name} is testing code`);
  }
}

class EmployeeFactory {
  static create(type, name) {
    if (type === "developer") return new Developer(name);
    if (type === "tester") return new Tester(name);
  }
}

const emp = EmployeeFactory.create("developer", "Zeeshan");
emp.work(); // Zeeshan is writing code
```

---

## 3. Abstract Factory Pattern

> Create families of related objects without specifying their concrete classes.

### Use Case

Ensures consistency between related products (e.g., UI components for themes).

### JavaScript Example

```js
class LightButton {
  render() {
    console.log("Rendering light button");
  }
}
class DarkButton {
  render() {
    console.log("Rendering dark button");
  }
}

class LightTextbox {
  render() {
    console.log("Rendering light textbox");
  }
}
class DarkTextbox {
  render() {
    console.log("Rendering dark textbox");
  }
}

class LightUIFactory {
  createButton() { return new LightButton(); }
  createTextbox() { return new LightTextbox(); }
}

class DarkUIFactory {
  createButton() { return new DarkButton(); }
  createTextbox() { return new DarkTextbox(); }
}

const factory = new DarkUIFactory();
factory.createButton().render();  // Rendering dark button
factory.createTextbox().render(); // Rendering dark textbox
```

---

## 4. Observer Pattern

> Notify multiple observers about state changes in another object.

### Use Case

Ideal for pub-sub models, event systems, or reactive UIs.

### JavaScript Example

```js
class Subject {
  constructor() {
    this.observers = [];
  }
  subscribe(observer) {
    this.observers.push(observer);
  }
  unsubscribe(observer) {
    this.observers = this.observers.filter(fn => fn !== observer);
  }
  notify(data) {
    this.observers.forEach(observer => observer(data));
  }
}

const news = new Subject();

function subscriber1(msg) { console.log("Sub1:", msg); }
function subscriber2(msg) { console.log("Sub2:", msg); }

news.subscribe(subscriber1);
news.subscribe(subscriber2);

news.notify("New blog published!");
```

---

## 5. Decorator Pattern

> Dynamically add functionality to objects without changing their structure.

### Use Case

Useful when you want to avoid subclass explosion and prefer flexible feature composition.

### JavaScript Example

```js
class Coffee {
  cost() { return 5; }
}

function withMilk(coffee) {
  const cost = coffee.cost();
  coffee.cost = () => cost + 1.5;
  return coffee;
}

function withSugar(coffee) {
  const cost = coffee.cost();
  coffee.cost = () => cost + 0.5;
  return coffee;
}

let myCoffee = new Coffee();
myCoffee = withMilk(myCoffee);
myCoffee = withSugar(myCoffee);
console.log(myCoffee.cost()); // 7.0
```

---

## 6. Facade Pattern

> Provide a simple interface to a complex subsystem.

### Use Case

Used to hide complex logic or dependencies behind a simple method call.

### JavaScript Example

```js
class CPU {
  start() { console.log("CPU started"); }
}

class Memory {
  load() { console.log("Memory loaded"); }
}

class HardDrive {
  read() { console.log("Hard drive read"); }
}

class Computer {
  constructor() {
    this.cpu = new CPU();
    this.memory = new Memory();
    this.hardDrive = new HardDrive();
  }
  start() {
    this.cpu.start();
    this.memory.load();
    this.hardDrive.read();
    console.log("Computer started successfully!");
  }
}

const pc = new Computer();
pc.start();
```

---

## Final Thoughts

Design patterns are not about memorizing definitions — they're about recognizing **recurring problems** and applying **battle-tested solutions**. Mastering these patterns in JavaScript makes your applications easier to scale and maintain.



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli