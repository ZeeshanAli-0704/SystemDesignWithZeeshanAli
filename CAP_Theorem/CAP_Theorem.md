### CAP Theorem: An Overview

The CAP theorem, also known as Brewer's theorem, is a fundamental principle in distributed systems that describes the trade-offs between three core properties:

1. **Consistency (C)**
2. **Availability (A)**
3. **Partition Tolerance (P)**

### Core Properties

1. **Consistency (C):**
   - Every read from the system returns the most recent write. This means that all nodes in the system see the same data at the same time.

2. **Availability (A):**
   - Every request (read or write) receives a response, regardless of whether it is successful or fails. This ensures that the system is operational and responsive.

3. **Partition Tolerance (P):**
   - The system continues to operate even if there are network partitions that prevent some nodes from communicating with others. This means the system can handle communication breakdowns between nodes.

### How It Works

The CAP theorem states that in a distributed system, it is impossible to simultaneously achieve all three properties. You can only have at most two of the three properties at any given time:

- **CA (Consistency + Availability):** These systems do not handle partitions well. If a network partition occurs, the system might become unavailable to ensure consistency.

- **CP (Consistency + Partition Tolerance):** These systems can handle partitions but might not be available during the partition to ensure consistency.

- **AP (Availability + Partition Tolerance):** These systems remain available even during partitions but might not be consistent.

### Significance in System Design

The CAP theorem is crucial in system design because it helps engineers understand the trade-offs they need to make when building distributed systems. Depending on the requirements of the application, designers choose which properties to prioritize.

### Why We Need CAP Theorem

The CAP theorem is important because it provides a framework for understanding the limitations and trade-offs of distributed systems. In real-world scenarios, network partitions are inevitable, and understanding CAP helps in designing systems that can handle such scenarios gracefully.

### Example: Distributed Database Systems

Let's consider a distributed database like Cassandra:

- **Consistency (C):** In Cassandra, you can configure the consistency level. For example, you can set it to ensure that a write is only considered successful if it is written to all nodes (or a majority).
- **Availability (A):** Cassandra is designed to be highly available. It uses replication and is designed to always accept reads and writes.
- **Partition Tolerance (P):** Cassandra is partition tolerant. It can handle network partitions and continues to operate by ensuring that data is eventually consistent.

#### Scenario 1: CA (Consistency + Availability)
- **Example:** Traditional relational databases like MySQL in a master-slave setup.
- **Trade-off:** They might become unavailable during network partitions to maintain consistency.

#### Scenario 2: CP (Consistency + Partition Tolerance)
- **Example:** MongoDB can be configured for CP by ensuring data consistency across replicas.
- **Trade-off:** It might not be available for some operations during partitions.

#### Scenario 3: AP (Availability + Partition Tolerance)
- **Example:** Cassandra prioritizes availability and partition tolerance.
- **Trade-off:** It provides eventual consistency, which means reads might not always return the most recent write immediately.


Lets take another example:

### CAP Theorem in the Context of a Banking System

Let's consider a banking system with multiple ATMs that are part of a distributed system. We'll examine how the CAP theorem applies when two ATMs need to sync data (such as account balances) in the presence of network partitions.

#### Scenario: Two ATMs and a Network Partition

Imagine there are two ATMs, ATM1 and ATM2, connected to a central database server. A network partition occurs, isolating ATM1 from ATM2 and the central server.


#### Trade-offs in the Banking System

Given the constraints of the CAP theorem, the banking system must make trade-offs:

- **CA (Consistency + Availability):**
  - **Behavior:** The system prioritizes ensuring every transaction is consistent and available.
  - **Example:** During a network partition, the ATMs may become unavailable to prevent inconsistencies. Customers might not be able to complete transactions until the partition is resolved.
  - **Implication:** This approach ensures accuracy but can lead to poor customer experience due to unavailability.

- **CP (Consistency + Partition Tolerance):**
  - **Behavior:** The system prioritizes consistency and can handle partitions but sacrifices availability.
  - **Example:** During a network partition, the ATMs might stop accepting transactions until they can sync with the central server, ensuring all transactions are consistent.
  - **Implication:** This approach maintains data accuracy but leads to service interruptions.

- **AP (Availability + Partition Tolerance):**
  - **Behavior:** The system prioritizes availability and partition tolerance but sacrifices immediate consistency.
  - **Example:** During a network partition, both ATMs continue to accept transactions. ATM1 might allow a withdrawal even if it cannot immediately sync with ATM2, leading to temporary inconsistencies (e.g., potential overdrafts).
  - **Implication:** This approach ensures the ATMs remain operational, but the balance might not be immediately accurate across both ATMs. The system resolves inconsistencies once the network partition is resolved, achieving eventual consistency.

### Example Walkthrough

#### Initial State:
- Customer A has $500 in their account.

#### During Network Partition:
1. **ATM1:** Customer A withdraws $100.
   - ATM1 shows a balance of $400.
   - ATM2, still isolated, shows a balance of $500.

2. **ATM2:** Customer A withdraws $200.
   - ATM2 shows a balance of $300.
   - ATM1, still isolated, shows a balance of $400.

#### After Network Partition Resolution:
- The system must reconcile the differences:
  - Final balance needs to reflect both transactions: $500 - $100 - $200 = $200.


### CAP Theorem in the Context of a Social Media Platform

Let's consider a social media platform like Twitter or Facebook, where users can post updates, and these updates should be reflected in their friends' or followers' feeds. We'll examine how the CAP theorem applies when two users' data needs to sync in the presence of network partitions.

#### Scenario: User Posts and Network Partition

Imagine two users, User A and User B, who are friends on the platform. User A posts an update, and due to a network partition, User B's view of the updates is temporarily affected.

#### Trade-offs in the Social Media Platform

Given the constraints of the CAP theorem, the social media platform must make trade-offs:

- **CA (Consistency + Availability):**
  - **Behavior:** The system prioritizes ensuring every interaction is consistent and available.
  - **Example:** During a network partition, the platform might become unavailable to prevent inconsistencies. Users might not be able to post updates until the partition is resolved.
  - **Implication:** This approach ensures accuracy but can lead to poor user experience due to unavailability.

- **CP (Consistency + Partition Tolerance):**
  - **Behavior:** The system prioritizes consistency and can handle partitions but sacrifices availability.
  - **Example:** During a network partition, the platform might stop accepting new posts or interactions until it can sync with all servers, ensuring all data is consistent.
  - **Implication:** This approach maintains data accuracy but leads to service interruptions.

- **AP (Availability + Partition Tolerance):**
  - **Behavior:** The system prioritizes availability and partition tolerance but sacrifices immediate consistency.
  - **Example:** During a network partition, both User A and User B continue to use the platform. User B might not see User A's latest update immediately, leading to temporary inconsistencies.
  - **Implication:** This approach ensures the platform remains operational, but updates might not be immediately consistent across all users. The system resolves inconsistencies once the network partition is resolved, achieving eventual consistency.

### Example Walkthrough

#### Initial State:
- User A and User B are friends.
- User A has posted a previous update that User B can see.

#### During Network Partition:
1. **User A:** Posts a new update.
   - The update is visible to User A and users on the same partition as User A.
   - User B, who is on a different partition, does not see the update immediately.

2. **User B:** Likes a post from another friend.
   - The like is visible to User B and users on the same partition as User B.
   - User A, who is on a different partition, does not see the like immediately.

#### After Network Partition Resolution:
- The system must reconcile the differences:
  - User A's new update is propagated to User B's feed.
  - User B's like is propagated to User A's view of the post.


### Conclusion

In a distributed system, the CAP theorem helps in understanding the trade-offs between consistency, availability, and partition tolerance. By prioritizing one or two of these properties, the system can be designed to meet specific requirements and handle real-world challenges such as network partitions. The choice of which properties to prioritize depends on the business goals and customer expectations.

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli

