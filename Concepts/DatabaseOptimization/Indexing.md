# 📑 Table of Contents – Database Optimizations: Indexing

* [Introduction](#introduction)
* [What is Indexing?](#what-is-indexing)
* [How Queries Work Without Index](#how-queries-work-without-index)
* [How Indexing Works](#how-indexing-works)
* [Types of Indexes](#types-of-indexes)
  * [Single Column Index](#single-column-index)
  * [Composite (MultiColumn) Index](#composite-multicolumn-index)
  * [Unique Index](#unique-index)
  * [Full Text Index](#full-text-index)
  * [Clustered vs Non Clustered Index](#clustered-vs-non-clustered-index)
* [When Indexing Helps](#when-indexing-helps)
* [When Indexing Hurts](#when-indexing-hurts)
* [Real World Example: E commerce Search](#real-world-example-e-commerce-search)
* [Best Practices](#best-practices)
* [Summary](#summary)

---

# 🚀 Database Optimizations: Indexing 

Databases are the backbone of modern applications, powering everything from e-commerce websites to banking systems. But as the volume of data grows, retrieving records efficiently becomes challenging. One of the most fundamental optimization techniques is **Indexing**.

This article will walk you through indexing step by step — starting with naive concepts and gradually moving toward real-world usage with SQL examples.

---

## What is Indexing?

Think of a database as a giant **book** and each row as a **page**. If you want to find a word in the book without an index, you’d have to scan **page by page** — this is called a **full table scan**.

But if the book has an **index at the back**, you can jump directly to the page number.
👉 Similarly, a **database index** is a **data structure** that helps the database quickly locate rows matching a query condition.

---

## How Queries Work Without Index

Let’s say we have a table of employees:

```sql
CREATE TABLE Employees (
    EmployeeID INT PRIMARY KEY,
    Name VARCHAR(100),
    Department VARCHAR(50),
    Salary INT
);

INSERT INTO Employees VALUES
(1, 'Alice', 'HR', 50000),
(2, 'Bob', 'IT', 60000),
(3, 'Charlie', 'Finance', 55000),
(4, 'David', 'IT', 65000),
(5, 'Eva', 'HR', 52000);
```

Now, if we run:

```sql
SELECT * FROM Employees WHERE Department = 'IT';
```

Without an index, the database will:

1. Scan each row in `Employees`.
2. Compare the `Department` column with `"IT"`.
3. Return matching rows.

If `Employees` has **10 million rows**, this is slow!

---

## How Indexing Works

By creating an index on the `Department` column:

```sql
CREATE INDEX idx_department ON Employees(Department);
```

Now, the database creates a **separate structure** (often a **B-Tree** or **Hash Map**) that maps each department to the row locations.

When we query for `"IT"`, the DB jumps directly to the index → fetches rows → skips scanning unnecessary rows.

---

## Types of Indexes (With Examples)

### Single Column Index

Indexes created on **one column**.

```sql
CREATE INDEX idx_salary ON Employees(Salary);
```

Used for:

```sql
SELECT * FROM Employees WHERE Salary > 60000;
```

---

### Composite (MultiColumn) Index

Indexes created on **multiple columns**.

```sql
CREATE INDEX idx_dept_salary ON Employees(Department, Salary);
```

This helps queries like:

```sql
SELECT * FROM Employees WHERE Department = 'IT' AND Salary > 60000;
```

⚠️ **Rule:** Order matters!

* Index `(Department, Salary)` works for queries filtering by `Department` or both.
* But if you only filter by `Salary`, the index may not be used effectively.

---

### Unique Index

Ensures all values in a column are unique.

```sql
CREATE UNIQUE INDEX idx_email ON Employees(Name);
```

If you try to insert a duplicate value, it fails.

---

### Full Text Index

Used for searching large text fields.

```sql
CREATE FULLTEXT INDEX idx_name ON Employees(Name);
```

Query:

```sql
SELECT * FROM Employees WHERE MATCH(Name) AGAINST('Alice');
```

---

### Clustered vs Non Clustered Index

* **Clustered Index**: Rearranges the actual table rows to match the index order. (Only **one** per table, usually the primary key).
* **Non-Clustered Index**: Creates a separate index structure that points to the row location.

Example in SQL Server:

```sql
-- Clustered Index on EmployeeID
CREATE CLUSTERED INDEX idx_empid ON Employees(EmployeeID);

-- Non-Clustered Index on Department
CREATE NONCLUSTERED INDEX idx_dept ON Employees(Department);
```

---

## When Indexing Helps

✅ Fast searches with `WHERE` clauses
✅ Speeding up `JOIN` operations
✅ Efficient sorting with `ORDER BY`
✅ Quick lookups for unique constraints

---

## When Indexing Hurts

❌ Takes extra disk space
❌ Slows down `INSERT`, `UPDATE`, `DELETE` (because indexes must also be updated)
❌ Too many indexes → query optimizer confusion

---

## Real World Example: E commerce Search

Imagine an **Orders** table with millions of rows:

```sql
CREATE TABLE Orders (
    OrderID INT PRIMARY KEY,
    CustomerID INT,
    ProductID INT,
    OrderDate DATE,
    Status VARCHAR(20)
);
```

Query:

```sql
SELECT * FROM Orders WHERE CustomerID = 101 AND Status = 'Shipped';
```

👉 Without indexes: **full table scan**.
👉 With composite index:

```sql
CREATE INDEX idx_customer_status ON Orders(CustomerID, Status);
```

Now, results are instant.

---

## Best Practices

* Index **columns frequently used in WHERE, JOIN, ORDER BY, GROUP BY**.
* Don’t index small tables (full scan is faster).
* Avoid indexing columns with **high update frequency**.
* Monitor with `EXPLAIN` (MySQL/PostgreSQL) or `EXPLAIN PLAN` (Oracle) to see if indexes are used.

---

## Summary

* Indexing improves query performance by avoiding full table scans.
* Different types of indexes (single-column, composite, clustered, full-text) suit different needs.
* Over-indexing can hurt performance, so balance is key.

👉 **Think of indexing like a library catalog — it’s not free to maintain, but it saves you tons of time when searching.**

---

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli

