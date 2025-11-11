# Table of Contents

1. [Introduction](#introduction)
2. [Why Do Algorithms Matter?](#why-do-algorithms-matter)
3. [Static Load Balancing Algorithms](#1-static-load-balancing-algorithms)

   * [Round Robin](#11-round-robin)
   * [Weighted Round Robin](#12-weighted-round-robin)
   * [IP Hashing](#13-ip-hashing)
4. [Dynamic Load Balancing Algorithms](#2-dynamic-load-balancing-algorithms)

   * [Least Connections](#21-least-connections)
   * [Weighted Least Connections](#22-weighted-least-connections)
   * [Least Response Time](#23-least-response-time)
   * [Resource Based (Custom Metrics)](#24-resource-based-custom-metrics)
5. [Advanced Algorithms](#3-advanced-algorithms)

   * [Consistent Hashing](#31-consistent-hashing)
   * [Random with Two Choices](#32-random-with-two-choices)
   * [Hybrid Algorithms](#33-hybrid-algorithms)
6. [Comparison Table](#4-comparison-table)
7. [Best Practices](#5-best-practices)
8. [Conclusion](#conclusion)

---

Do you want me to now **expand this TOC into the full blog article** (with details, examples, and diagram references), similar to how we did Part 2?



# Load Balancing Series – Part 3: Load Balancing Algorithms

## Introduction

In [Part 1](#) we introduced **load balancing fundamentals**.
In [Part 2](#) we discussed the **different types of load balancers**.

Now, in **Part 3**, we’ll explore **how load balancers actually decide where to send each incoming request**—through **load balancing algorithms**.

Think of a load balancer as a **dispatcher at a busy airport taxi stand**:
* Passengers (requests) keep arriving.
* The dispatcher must decide **which taxi (server) each passenger should go to**.
* The decision strategy is the **load balancing algorithm**.

---

## Why Do Algorithms Matter?

* Ensure **fair distribution** of requests.
* Prevent overloading a single server.
* Reduce latency by routing smartly.
* Improve fault tolerance by adapting to server availability.
* Optimize costs and performance.

---

## Static Load Balancing Algorithms

These follow **predefined rules** and do not consider current server health or load.

### Round Robin

* Requests are distributed sequentially: Server 1 → Server 2 → Server 3 → Server 1 …
* Simple and effective when all servers are identical in capacity.

**Example:**

* 3 servers: A, B, C.
* Incoming 6 requests → \[A, B, C, A, B, C].

**Use Case:**

* Good for **stateless applications** where each request is similar in cost.

**Diagram Reference:**
A circular arrow pointing to servers in order.

---

### Weighted Round Robin

* Extends Round Robin by assigning **weights** to servers.
* Servers with higher capacity get more requests.

**Example:**

* Server A (weight 3), Server B (weight 1).
* Incoming 4 requests → \[A, A, A, B].

**Use Case:**

* Environments where servers have **different CPU/RAM capacities**.

---

###  IP Hashing

* Uses a hash function on the client’s **IP address** to decide which server handles the request.
* Ensures that the **same client always connects to the same server** (session persistence).

**Example:**

* User 192.168.1.10 always routed to Server A.
* User 192.168.1.20 always routed to Server B.

**Use Case:**

* Shopping carts, login sessions, multiplayer gaming servers.

---

## Dynamic Load Balancing Algorithms

These consider **real-time server load** before making routing decisions.

### Least Connections

* Request is sent to the server with the **fewest active connections**.
* Works well when requests have varying processing times.

**Example:**

* Server A: 10 active connections.
* Server B: 4 active connections.
* New request → goes to Server B.

**Use Case:**

* Long-lived connections (e.g., streaming, database queries).

---

### Weighted Least Connections

* Like Least Connections, but takes **server capacity** into account.
* Higher-capacity servers can handle more connections.

**Example:**

* Server A (weight 3) can handle 300 connections.
* Server B (weight 1) can handle 100 connections.
* Algorithm ensures Server A gets 3x more requests than Server B.

---

### Least Response Time

* Requests are sent to the server with the **lowest response time + fewest active connections**.
* Balances both **speed** and **load**.

**Use Case:**

* API gateways, high-traffic e-commerce platforms.

---

### Resource-Based (Custom Metrics)

* Balancing decisions are based on metrics like **CPU usage, memory consumption, queue length**.
* Requires health checks and monitoring integration.

**Use Case:**

* AI/ML workloads, data-intensive applications.

---

## Advanced Algorithms

### Consistent Hashing

* Requests are distributed based on a **hash of the client or request data**.
* Ensures that when servers are added/removed, only a minimal set of requests are remapped.

**Example:**

* Used in distributed caching systems like **Memcached, Cassandra, and Kafka partitioning**.

**Use Case:**

* CDN routing, distributed databases, cache clusters.

---

### Random with Two Choices

* Picks **two random servers** and assigns the request to the one with fewer connections.
* Surprisingly effective and reduces imbalance.

**Use Case:**

* High-throughput systems where fairness is critical.

---

### Hybrid Algorithms

* Modern load balancers often combine strategies.
* Example: **Weighted Least Connections + Consistent Hashing** for APIs with sticky sessions.

---

##  Comparison Table

| Algorithm             | Type     | Awareness    | Use Case               | Pros               | Cons                       |
| --------------------- | -------- | ------------ | ---------------------- | ------------------ | -------------------------- |
| Round Robin           | Static   | No           | Stateless apps         | Simple             | Doesn’t handle uneven load |
| Weighted Round Robin  | Static   | Partial      | Mixed server sizes     | Fairer             | Needs manual weights       |
| IP Hashing            | Static   | Client-based | Session persistence    | Sticky sessions    | Hard to rebalance          |
| Least Connections     | Dynamic  | Yes          | Streaming, DB          | Adaptive           | Extra overhead             |
| Weighted Least Conn.  | Dynamic  | Yes          | Uneven server capacity | Balanced           | Complex setup              |
| Least Response Time   | Dynamic  | Yes          | APIs, e-commerce       | Optimized          | Needs monitoring           |
| Consistent Hashing    | Advanced | Partial      | Caching, partitioning  | Minimal disruption | More complex               |
| Random w/ Two Choices | Advanced | Partial      | High throughput        | Reduces hotspots   | Not deterministic          |

---

## Best Practices

* **Stateless apps** → Round Robin or Weighted Round Robin.
* **Long-lived sessions** → IP Hashing or Consistent Hashing.
* **Variable workloads** → Least Connections or Least Response Time.
* **High-performance caching/distributed systems** → Consistent Hashing.
* **Cloud-native apps** → Hybrid (mix of response time + weights).

---

## Conclusion

Load balancing algorithms are the **brains behind traffic distribution**. Choosing the right algorithm depends on:

* **Workload type** (stateless vs. stateful).
* **Server heterogeneity** (equal vs. unequal capacity).
* **Performance goals** (latency vs. throughput).

In **Part 4**, we’ll explore **Load Balancer Architectures** (Single-tier, Multi-tier, Global, Anycast, CDN-integrated).

---


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli