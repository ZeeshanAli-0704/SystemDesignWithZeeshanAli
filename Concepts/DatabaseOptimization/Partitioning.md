# 📑 Table of Contents – Database Optimizations: Partitioning

* [Introduction](#introduction)
* [What is Partitioning?](#what-is-partitioning)
* [Why Partition Data?](#why-partition-data)
* [Types of Partitioning](#types-of-partitioning)
  * [Range Partitioning](#range-partitioning)
  * [List Partitioning](#list-partitioning)
  * [Hash Partitioning](#hash-partitioning)
  * [Composite Partitioning](#composite-partitioning-range--hash--range--list)
* [Benefits of Partitioning](#benefits-of-partitioning)
* [Drawbacks of Partitioning](#drawbacks-of-partitioning)
* [Real World Example: Large Order History](#real-world-example-large-order-history)
* [Best Practices](#best-practices)
* [Summary](#summary)

---



# 🚀 Database Optimizations: Partitioning (From Basics to Practical Use)

As datasets grow into millions or billions of rows, even **indexes** sometimes struggle to deliver performance. Imagine having a single **giant warehouse** where every item is stored. If you need to find one box, it could still take time.

👉 This is where **Partitioning** comes in. It’s like dividing that giant warehouse into **smaller sections (partitions)** so you can look in the right section instead of the entire warehouse.

Partitioning improves query performance, scalability, and manageability of large databases. Let’s break it down step by step.

---

## 1. What is Partitioning?

Partitioning is the process of splitting a large table into **smaller, more manageable pieces** while keeping them logically as **one table** for queries.

* To the user → it still looks like one table.
* To the database → it can store and process data in **smaller chunks (partitions)**.

---

## 2. Why Partition Data?

Without partitioning, a query on a 500-million-row table requires scanning indexes or huge storage blocks.

With partitioning:

* The database narrows down to only the relevant partition.
* Maintenance operations (backup, purge, archive) become faster.
* Improves parallel processing (different partitions can be scanned simultaneously).

---

## 3. Types of Partitioning

### 🔹 3.1 Range Partitioning

Rows are divided based on a range of values in a column.
Example: Partitioning an `Orders` table by `OrderDate`.

```sql
CREATE TABLE Orders (
    OrderID INT,
    CustomerID INT,
    OrderDate DATE,
    Amount DECIMAL(10,2)
)
PARTITION BY RANGE (YEAR(OrderDate)) (
    PARTITION p2019 VALUES LESS THAN (2020),
    PARTITION p2020 VALUES LESS THAN (2021),
    PARTITION p2021 VALUES LESS THAN (2022),
    PARTITION pmax VALUES LESS THAN MAXVALUE
);
```

👉 Queries for orders in **2020** will only hit partition `p2020`.

---

### 🔹 3.2 List Partitioning

Rows are divided based on **discrete values**.
Example: Partition employees by department.

```sql
CREATE TABLE Employees (
    EmployeeID INT,
    Name VARCHAR(100),
    Department VARCHAR(50)
)
PARTITION BY LIST (Department) (
    PARTITION pHR VALUES IN ('HR'),
    PARTITION pIT VALUES IN ('IT'),
    PARTITION pFinance VALUES IN ('Finance')
);
```

---

### 🔹 3.3 Hash Partitioning

Rows are distributed across partitions using a **hash function**.
Useful when data doesn’t have a natural range or list.

```sql
CREATE TABLE Transactions (
    TransactionID INT,
    UserID INT,
    Amount DECIMAL(10,2)
)
PARTITION BY HASH(UserID)
PARTITIONS 4;
```

👉 Distributes data evenly across 4 partitions to avoid hotspots.

---

### 🔹 3.4 Composite Partitioning (Range + Hash / Range + List)

Combines multiple strategies.
Example: Range partition by year, then hash partition inside each year.

```sql
CREATE TABLE Sales (
    SaleID INT,
    Region VARCHAR(50),
    SaleDate DATE,
    Amount DECIMAL(10,2)
)
PARTITION BY RANGE (YEAR(SaleDate))
SUBPARTITION BY HASH (Region)
SUBPARTITIONS 4 (
    PARTITION p2019 VALUES LESS THAN (2020),
    PARTITION p2020 VALUES LESS THAN (2021),
    PARTITION p2021 VALUES LESS THAN (2022)
);
```

---

## 4. Benefits of Partitioning

✅ Speeds up queries (partition pruning).
✅ Parallelism — partitions scanned in parallel.
✅ Easier data management (archiving, backup, purging old data).
✅ Reduces index size (indexes per partition).

---

## 5. Drawbacks of Partitioning

❌ Increases schema complexity.
❌ Overhead in maintaining partition strategy.
❌ Not every query benefits (e.g., cross-partition queries still touch many partitions).
❌ Too many partitions = performance degradation.

---

## 6. Real World Example: Large Order History

Imagine an **e-commerce site** with billions of orders.

* Users rarely query orders older than 3 years.
* By **partitioning Orders by year**, queries for recent data are much faster.
* Old partitions can be archived or moved to cheaper storage.

Query example:

```sql
SELECT * FROM Orders WHERE OrderDate BETWEEN '2021-01-01' AND '2021-12-31';
```

👉 Database automatically scans only the **2021 partition**, not the entire table.

---

## 7. Best Practices

* Choose partition keys based on query patterns (e.g., date ranges).
* Avoid too many small partitions (can slow down optimizer).
* Combine with indexing for best performance.
* Regularly monitor partition sizes and adjust strategy.

---

## 8. Summary

* Partitioning splits a large table into smaller, logical pieces.
* Types: **Range, List, Hash, Composite**.
* Helps with performance, maintenance, and scalability.
* Must be carefully designed, or it can add complexity.

👉 Partitioning is like **dividing a warehouse into sections** so you don’t waste time searching everywhere.

---


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli