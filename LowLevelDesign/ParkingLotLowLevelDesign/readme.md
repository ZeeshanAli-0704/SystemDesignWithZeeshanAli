# 🚗 Parking Lot Management System

A **modular, object-oriented Java application** to manage multi-floor parking lots with flexible parking and payment strategies.

---

## 📑 Table of Contents

* [Overview](#overview)
* [System Design](#system-design)
* [Key Features](#key-features)
* [Code Structure](#code-structure)
* [Usage](#usage)
* [Example Use Cases](#example-use-cases)
* [Configuration](#configuration)
* [Extensibility](#extensibility)

---

## 🧾 Overview

The **Parking Lot Management System** is a Java-based application that simulates the real-world functionality of a parking lot. It supports multiple **entrances, exits, floors**, and **vehicle types**. It allocates parking spots, issues tickets, calculates parking fees based on duration and vehicle type, and supports different **payment methods**.

---

## 🏗️ System Design

The system is designed using **Object-Oriented Principles (OOP)** and is highly modular and extensible.

### Core Components:

* **`ParkingSpotManager`**: Allocates and tracks parking spots across floors.
* **`Entrance` / `ExitGate`**: Represent parking lot entry and exit points.
* **`Vehicle` / `Ticket`**: Models vehicles and their parking tickets.
* **`ParkingStrategy`**: Strategy pattern for parking spot allocation.
* **`CostComputation`**: Calculates parking fees.
* **`PaymentStrategy`**: Multiple payment options like UPI and Credit Card.

---

## 🌟 Key Features

* ✅ **Multi-floor Support**: Define multiple floors with distinct spot capacities.
* 🚙 **Vehicle Parking & Unparking**: Real-time parking operations with ticket generation.
* 💰 **Fee Calculation**: Based on vehicle type and parked duration.
* 💳 **Multiple Payment Methods**: Plug-and-play strategy for UPI, Credit Card, etc.
* ♻️ **Pluggable Parking Strategies**: Easily switch between strategies like *nearest to entrance*, etc.

---

## 📁 Code Structure

```
org.example
├── config                 // ParkingLotConfiguration
├── domain.model          // Vehicle, Ticket, etc.
├── entrance / exit       // Entry and exit gates
├── floor                 // Floor & display board logic
├── parkingspot           // Spot definitions (Mini, Compact, Large)
├── service               // Service classes (EntryExitRegistry, etc.)
├── spotmanager           // ParkingSpotManager
└── strategy
    ├── parking           // ParkingStrategy interfaces & implementations
    ├── cost              // Cost computation logic
    └── payment           // Payment strategy logic
```

---

## ▶️ Usage

To use the system:

1. **Configure the parking lot**:

   ```java
   ParkingLotConfiguration config = new ParkingLotConfiguration();

   ```

2. **Initialize manager & floors**:

   ```java
   ParkingSpotManager spotManager = new ParkingSpotManager();
   spotManager.addFloor(...); // Add floors and spots
   ```

3. **Set parking strategy**:

   ```java
   spotManager.setParkingStrategy(new NearToEntranceParkingStrategy());
   ```

4. **Setup entrances/exits**:

   ```java
   EntryExitRegistry registry = new EntryExitRegistry();
   registry.registerEntrance(...);
   registry.registerExit(...);
   ```

5. **Park & unpark vehicles**:

   ```java
   Ticket ticket = entrance.parkVehicle(vehicle);
   exit.processExit(ticket, new UpiPayment());
   ```

---

## 🧪 Example Use Cases

The `ParkingLotDemo` class demonstrates:

* Parking vehicles of different sizes: `MINI`, `COMPACT`, `LARGE`
* Unparking and fee calculation
* Retry parking after a spot is vacated

---

## ⚙️ Configuration

Use `ParkingLotConfiguration` to define floors, spot counts, supported vehicle types, or override it with your custom implementation.

---

## 🧩 Extensibility

The system supports easy plug-in of new logic via interfaces:

### ➕ Add a Parking Strategy

1. Implement the `ParkingStrategy` interface.
2. Set it using:

   ```java
   spotManager.setParkingStrategy(new YourCustomStrategy());
   ```

### ➕ Add a Cost Computation Strategy

1. Implement the `CostComputation` interface.
2. Update the resolver:

   ```java
   costComputationResolver.registerStrategy(VehicleType.LUXURY, new LuxuryCostComputation());
   ```

### ➕ Add a Payment Strategy

1. Implement the `PaymentStrategy` interface.
2. Use it when calling:

   ```java
   exit.processExit(ticket, new CryptoPayment());
   ```

---

Let me know if you'd like a **badge**, **UML diagram**, or **setup instructions** added!
