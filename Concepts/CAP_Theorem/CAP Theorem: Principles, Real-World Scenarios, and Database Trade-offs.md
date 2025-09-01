## Table of Contents

1. [What is the CAP Theorem?](#what-is-the-cap-theorem)
2. [The Three Combinations: CA, CP, and AP in Theory and Practice](#the-three-combinations-ca-cp-and-ap-in-theory-and-practice)
3. [Mapping Real Databases to CAP](#mapping-real-databases-to-cap)
4. [Real World Use Cases and CAP Trade-offs](#real-world-use-cases-and-cap-trade-offs)
   * [Ticket Booking Systems](#1-ticket-booking-systems-eg-concert-movie-airline)
   * [Parking Lot Management Systems](#2-parking-lot-management-systems)
   * [Food Delivery Order Management](#3-food-delivery-order-management-ubereats-doordash-etc)
5. [Subsystems: Tailoring CAP to Different Needs](#subsystems-tailoring-cap-to-different-needs)
6. [Special Note: The PACELC Model](#special-note-the-pacelc-model)
7. [Common Questions When Choosing a Database for Your Use Case](#common-questions-when-choosing-a-database-for-your-use-case)
8. [CAP Myths and Misconceptions](#cap-myths-and-misconceptions)
9. [Full Table: Database CAP Summary](#full-table-database-cap-summary)
10. [Conclusion: What Should You Choose?](#conclusion-what-should-you-choose)


# Understanding the CAP Theorem: Principles, Real World Scenarios, and Database Trade-offs

In today's world, most data-driven systems are distributed: they span data centers, cloud regions, and multiple devices or sites. This distributed nature introduces new challenges, especially around reliability, scalability, and user expectations for instant access and accurate results.

One foundational concept for designing distributed data systems is the **CAP theorem**—also known as Brewer’s theorem. In this blog, we’ll do a deep dive into what the CAP theorem is, why it matters, and how it directly impacts Real World applications like ticket booking, parking management, and food order systems. We’ll finish with a comparison of common databases mapped to CAP trade-offs, plus actionable design recommendations.

---

## What is the CAP Theorem?

The **CAP theorem** asserts that any distributed data system can at most deliver two out of the following three guarantees at a single moment:

1. **Consistency (C):** Every read receives the most recent (or error). All nodes see the same data at the same time—no anomalies.
2. **Availability (A):** Every request receives a (non-error) response, regardless of individual node failures.
3. **Partition Tolerance (P):** The system continues to operate despite arbitrary network partitions that split nodes into isolated groups.

**Key Point:** Partition tolerance is non-negotiable for distributed systems: network failures will happen. Thus, engineers must decide—*during a partition*—whether to prioritize **Consistency** (return an error or block until up-to-date) or **Availability** (serve a possibly outdated value, but never block).

---
[🔼 Back to Top](#table-of-contents)

## The Three Combinations: CA, CP, and AP in Theory and Practice

| Combo | What It Means | Typical When... |
|-------|---------------|----------------|
| **CA (Consistency + Availability)** | System works as expected as long as there is *no partition*. Not realistic for large-scale distributed deployments: a single partition will bring it down or risk inconsistency. | Classic single-site, non-distributed, or LAN-only databases |
| **CP (Consistency + Partition Tolerance)** | System remains correct even when parts of the network are isolated—but might have to sacrifice availability, i.e., some operations fail or are delayed until partition resolves. | Airline booking, banking transactions |
| **AP (Availability + Partition Tolerance)** | System remains available (responds to every request) even when partitioned, but might return slightly out-of-date or inconsistent data. | Social media timelines, some sensor networks, parking status apps |

**Takeaway:** In a true partition, you must choose between answering with possibly outdated data (*AP*) or blocking/denying until consistency can be assured (*CP*).

---
[🔼 Back to Top](#table-of-contents)

## Mapping Real Databases to CAP

Let’s map common distributed and cloud databases to their typical CAP behavior:

| Database                   | Default Tradeoff | Description/Behavior                                                                        |
|----------------------------|------------------|---------------------------------------------------------------------------------------------|
| **Cassandra**              | AP               | Highly available, partition-tolerant, defaults to eventual consistency but is tunable.      |
| **MongoDB**                | AP, tunable CP   | Defaults to AP, can be configured for more consistent reads/writes.                         |
| **PostgreSQL (clustered)** | CP               | Strong consistency, partitions cause some unavailability.                                   |
| **CockroachDB**            | CP               | Globally consistent; may block writes during partition to avoid anomalies.                  |
| **DynamoDB**               | AP               | Prioritizes availability and partition-tolerance; offers "eventual" or "strong" consistency.|
| **HBase**                  | CP               | Sacrifices some availability in case of network split—emphasizes consistency.               |
| **Redis Cluster/Sentinel** | AP, tunable CP   | Default is AP; can be configured for greater consistency at expense of performance.         |
| **MySQL Cluster**          | CA (single node), CP (cluster) | Single instance: CA; cluster: CP, blocks/reroutes during partitions.         |

---
[🔼 Back to Top](#table-of-contents)

## Real World Use Cases and CAP Trade-offs

Let’s take three widely relatable systems and see how they respond to the CAP theorem:

### 1. Ticket Booking Systems (e.g., concert, movie, airline)

#### How It Works:
- Distributed nodes (websites, kiosks) access a central pool of tickets/seats.
- Payment and reservation must be atomic (no double-booking or overselling).

#### CAP Analysis:
- **Consistency:** CRITICAL. Selling the same seat twice is catastrophic.
- **Availability:** Some downtime or "please try again later" messages acceptable during contention.
- **Partition Tolerance:** Required—branches, partners, or online points can lose connectivity.

**Typical Tradeoff:** **CP**  
- During partition, system blocks new bookings rather than risk overbooking.
- May let *read-only* queries through, but not allow new writing/reserving.

#### Backend Approach:
- Use an ACID-compliant, distributed SQL database (PostgreSQL cluster, CockroachDB).
- May implement leader election, pessimistic or optimistic locking.

#### Real World Example:
- During a network split, attempt to reserve a seat gets blocked/error rather than risk a double booking.
- Analytics/reporting/replica subsystems may use an eventual consistency/“AP” approach.

---

[🔼 Back to Top](#table-of-contents)
### 2. Parking Lot Management Systems

#### How It Works:
- IoT devices and gate sensors record entrances/exits.
- Users reserve and check space availability via web or mobile.

#### CAP Analysis:
- **Consistency:** Somewhat needed for *online reservations* to avoid double-booking a spot.
- **Availability:** Critical at on-site gates—cars must always be able to enter/exit.
- **Partition Tolerance:** Imperative—network issues at garages are common.

**Typical Tradeoff:** **Hybrid CP (on bookings) and AP (at gates)**
- Online booking system (web/mobile): Consistency prioritized; block overbooking.
- Gate devices: Must let cars in/out regardless; can cache and reconcile later (eventual consistency).

#### Backend Approach:
- Strongly consistent transactional DB for online bookings.
- Gate edge devices use local cache/logs, sync back to main once reconnected (with built-in conflict resolution).

#### Real World Example:
- 2 users reserve the last spot at the same time during a partition: both get confirmation. On healing, system reconciles—contacts one user to apologize/offer alternate space.

---
[🔼 Back to Top](#table-of-contents)
### 3. Food Delivery Order Management (UberEats, DoorDash, etc.)
[🔼 Back to Top](#table-of-contents)
#### How It Works:
- Orders placed by users, picked up by couriers, and prepared by restaurants.
- Multiple real-time state transitions, GPS tracking, etc.

#### CAP Analysis:
- **Consistency:** Desired for payment/order state, but minor delays (e.g., live location) are usually tolerable.
- **Availability:** Crucial for competitive UX—order intake and tracking must be always on.
- **Partition Tolerance:** Unavoidable—mobile devices, branches go on/offline.

**Typical Tradeoff:** **AP** (Availability + Partition Tolerance), with CP for payment/checkout
- High availability and partition-tolerance for order and status tracking.
- Payment module may use stronger transactional consistency (CP).

#### Backend Approach:
- Main order log and status: AP-oriented DB (DynamoDB, Cassandra).
- Payment subcomponent: transactional, ACID-compliant DB.

#### Real World Example:
- System may temporarily list a courier as “en route” when they’ve arrived at restaurant (minor lag). All orders are accepted; payment status is never ambiguous.

---

## Subsystems: Tailoring CAP to Different Needs

Large systems mix and match CAP strategies for different needs:

| Subsystem         | Main Priority   | CAP Focus | DB Example                    |
|-------------------|----------------|-----------|-------------------------------|
| Ticket booking    | Consistency    | CP        | CockroachDB, PostgreSQL Citus |
| Parking booking   | Consistency    | CP        | MySQL Cluster, HBase          |
| Gate operation    | Availability   | AP        | Cassandra, Redis              |
| Food Orders (log) | Availability   | AP        | DynamoDB, Cassandra           |
| Food Payment      | Consistency    | CP        | Oracle, PostgreSQL            |

[🔼 Back to Top](#table-of-contents)

## Special Note: The PACELC Model

The **PACELC theorem** extends CAP by noting:  
- If there is a Partition (P), you must choose Availability (A) or Consistency (C);  
- Else (E), you must choose Latency (L) or Consistency (C).

**In Plain English:** Even when no partition exists, systems often trade latency against perfect consistency.

---

[🔼 Back to Top](#table-of-contents)

## Common Questions When Choosing a Database for Your Use Case

- **Must you absolutely avoid any stale data?** (Favor CP)
- **Would a small delay or error message during a split be OK?** (Favor CP)
- **Must your app never be down for writes/reads?** (Favor AP)
- **Can users tolerate a few incorrect counts for a short while if recovery is graceful?** (Favor AP)

---
[🔼 Back to Top](#table-of-contents)

## CAP Myths and Misconceptions

- **"You can only pick two":** Actually, all three can be satisfied when the network is healthy—the problem only arises during partitions.
- **"SQL is CP and NoSQL is AP":** Many modern NoSQL DBs can be configured for strong consistency, and RDBMS scales can add availability.

---

## Full Table: Database CAP Summary

| Database    | Consistency | Availability | Partition Tolerance | Default Tradeoff     |
|-------------|:-----------:|:------------:|:------------------:|---------------------|
| Cassandra   | Tunable (Default Weak) | High | High               | AP                  |
| MongoDB     | Configurable           | High | High               | AP/CP               |
| MySQL       | Strong (single server) | High | Low  (single node) | CA (single) / CP    |
| PostgreSQL  | Strong                 | High | High (clustered)   | CP                  |
| HBase       | Strong                 | Varies | High             | CP                  |
| DynamoDB    | Eventual/Strong        | High | High               | AP                  |
| CockroachDB | Strong                 | Varies | High             | CP                  |

---

## Conclusion: What Should You Choose?

- **Transactional, unique-resource systems (tickets, banking):** Go for **CP**. Never risk double-booking or monetary mistakes, even if this means offering users a "system busy" page in rare disruptive scenarios.
- **Mass user, stateless or soft-state systems (parking status, social feeds):** Go for **AP**. Persistent uptime for reads/writes, with quick healing of minor inconsistencies when partitions resolve.
- **CA** systems work only in non-distributed (single node) settings. Not suitable for modern, scalable apps needing fault-tolerance.

**Remember:** Partition Tolerance is a must-have in distributed systems. The main decision is whether, in the rare event of a partition, you prioritize immediately up-to-date data (CP), or you prioritize always responding (AP).

[🔼 Back to Top](#table-of-contents)


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli