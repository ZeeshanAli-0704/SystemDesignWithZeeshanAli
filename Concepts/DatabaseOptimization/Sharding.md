
# 📑 Table of Contents – Database Optimizations: Sharding

* [Introduction](#introduction)
* [What is Sharding?](#what-is-sharding)
* [Why Shard a Database?](#why-shard-a-database)
* [Sharding vs Partitioning](#sharding-vs-partitioning)
* [Types of Sharding](#types-of-sharding)
  * [Horizontal Sharding (Row-Based)](#horizontal-sharding-row-based)
  * [Vertical Sharding (Feature-Based)](#vertical-sharding-feature-based)
  * [Directory Based Sharding](#directory-based-sharding)
  * [Hash Based Sharding](#hash-based-sharding)
  * [Geo Sharding](#geo-sharding)
* [Benefits of Sharding](#benefits-of-sharding)
* [Challenges of Sharding](#challenges-of-sharding)
* [Real World Example: Social Media Platform](#real-world-example-social-media-platform)
* [Best Practices](#best-practices)
* [Summary](#summary)

---

# 🚀 Database Optimizations: Sharding (From Basics to Types and Real-World Use)


## What is Sharding?

Sharding is the process of **splitting a large database into multiple smaller databases (shards)**, each hosted on a separate server.

* To the application: looks like one logical database.
* Behind the scenes: data is distributed across shards.

---

## Why Shard a Database?

* **Performance bottleneck**: A single server struggles with billions of rows.
* **Scalability**: Adding more machines instead of overloading one.
* **High availability**: Failure of one shard doesn’t take down the whole system.
* **Geo-distribution**: Keep data closer to users (e.g., EU users in EU shard).

---

## Sharding vs Partitioning

* **Partitioning**: Splitting a table into smaller chunks **inside the same database**.
* **Sharding**: Splitting data across **multiple databases/servers**.

👉 You can think of **partitioning as organizing one warehouse**, while **sharding is opening multiple warehouses in different cities**.

---

## Types of Sharding

###  Horizontal Sharding (Row-Based)

Data is split by **rows** across shards.

Example: Users with ID `1–1M` go to Shard 1, `1M–2M` go to Shard 2.

```sql
-- Shard 1 (users 1–1M)
SELECT * FROM Users WHERE UserID BETWEEN 1 AND 1000000;

-- Shard 2 (users 1M–2M)
SELECT * FROM Users WHERE UserID BETWEEN 1000001 AND 2000000;
```

---

### Vertical Sharding (Feature-Based)

Data is split by **tables or features**.

Example:

* Shard 1 → `Users`, `Profiles`
* Shard 2 → `Orders`, `Payments`

---

### Directory-Based Sharding

A **lookup service (shard map)** decides which shard to query.

* Pros: Flexible
* Cons: Adds dependency on the directory service

---

### Hash-Based Sharding

Rows are distributed using a **hash function** on a column (e.g., `UserID % 4`).

```sql
Shard = UserID % 4;
```

* Pros: Distributes load evenly.
* Cons: Harder to re-shard if you add more servers.

---

### Geo Sharding

Data is sharded by **geography** (e.g., Asia users in Asia shard).

* Reduces latency.
* Helps with **data compliance** (GDPR, data residency).

---

## Benefits of Sharding

✅ Scales beyond a single machine.
✅ Faster queries (smaller dataset per shard).
✅ Supports geo-distribution.
✅ Fault isolation (shard outage doesn’t kill entire DB).

---

## Challenges of Sharding

❌ Complex to implement (routing, rebalancing).
❌ Cross-shard queries are slow and complicated.
❌ Schema changes must be applied across all shards.
❌ Data rebalancing when adding shards can be costly.

---

## Real-World Example: Social Media Platform

A **social network with 1B+ users** cannot store all user data on one server.

Solution:

* Shard users by `UserID % N`.
* Queries for one user (profile, posts) only hit one shard.
* Global analytics (like “top 10 trending posts”) may still require aggregation across shards.

---

## Best Practices

* Pick shard key carefully (`UserID`, `Region`, etc.).
* Avoid cross-shard joins when possible.
* Use middleware (like **Vitess**, **Citus**, or custom shard routers).
* Plan for resharding from day one.

---

## Summary

* Sharding distributes data across multiple databases/servers.
* Types include **Horizontal, Vertical, Directory-Based, Hash-Based, Geo Sharding**.
* Solves scalability but introduces complexity.

👉 Think of sharding as **opening multiple restaurants** instead of trying to fit everyone in one giant dining hall.


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
