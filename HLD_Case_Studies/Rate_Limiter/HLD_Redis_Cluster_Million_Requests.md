### **Table of Contents**

- [**Step 3: High Level Design (HLD) for a Rate Limiter with Distributed Redis**](#step-3-high-level-design)
- [**Revised High Level Design Overview**](#revised-high-level-design-overview)
- [**Estimations and Requirements Recap**](#estimations-and-requirements-recap)
- [**High Level Design Diagram Description**](#high-level-design-diagram-description)
- [**Detailed Explanation of Key Components and Design Choices**](#detailed-explanation-of-key-components-and-design-choices)
- [**Key Design Principles and Considerations for Scale**](#key-design-principles-and-considerations-for-scale)
- [**Summary of HLD with Distributed Redis**](#summary-of-hld-with-distributed-redis)

---


### **Step 3: High Level Design (HLD) for a Rate Limiter with Distributed Redis**

In this step, we will design a rate limiter system that leverages a distributed Redis setup to handle the massive scale of billions of daily requests across a user base of one billion users. The design will account for the provided estimations, storage requirements, and the need to operate across multiple availability zones (AZs) and hosts. I’ll explain each aspect of the design in detail, focusing on how the system meets the scalability, performance, and reliability requirements while using Redis as the primary storage for rate-limiting data. I’ll describe the architecture and components clearly.

---

### **Revised High Level Design Overview**
The rate limiter system is designed as a distributed service that sits between clients and backend application servers, enforcing rate limits based on recent user activity. Given the scale (billions of daily requests) and the user base (one billion users), the system must be highly scalable, fault-tolerant, and capable of low-latency operations. We will use a distributed Redis cluster as the in-memory storage for tracking request timestamps, ensuring fast access and consistency across multiple availability zones and hosts.

The architecture includes components for request processing, rate limit enforcement, distributed storage, load balancing, and monitoring, all tailored to handle the specified data volumes and short retention periods (10 seconds).

---

### **Estimations and Requirements Recap**
Before diving into the design, let’s summarize the provided estimations and constraints to ensure the system addresses them:
- **Scale:** The system must handle billions of daily requests.
- **User Base:** One billion users, with each user potentially generating up to 100 requests at any given moment.
- **Data to Store:**
  - User ID: 64 bits (8 bytes).
  - Timestamp: 64 bits (8 bytes).
  - Track up to 100 timestamps per user per service.
  - Support for 100 services.
- **Storage Calculation Per User:**
  - Per service: 100 timestamps × 8 bytes = 800 bytes.
  - Across 100 services: 800 bytes × 100 = 80,000 bytes (80 KB) per user.
- **Total Storage Estimate (Worst Case):**
  - For 1 billion users: 1,000,000,000 × 80 KB = 80 terabytes (TB) of data at peak.
- **Retention Period:** Data is retained for only 10 seconds, as decisions are based on recent activity. This significantly reduces long-term storage needs.
- **Deployment:** The service is deployed across multiple availability zones (AZs) and hosts for high availability and fault tolerance.

Given the short retention period, the actual storage footprint at any given time will be much smaller than the worst-case estimate, as old timestamps will be continuously expired or removed.

---

### **High Level Design Diagram Description**
Imagine the following textual representation of the HLD architecture:

```
[Client] ----> [Global CDN/Load Balancer] ----> [Rate Limiter Service Instances<br/>(Across Multiple AZs)]
                              |                           |
                              |                           +----> [Distributed Redis Cluster<br/>(Across AZs)]
                              |                           |
                              |                           +----> [Application Servers<br/>(Across AZs)]
                              |
                              +----> [Monitoring & Logging System]
                              |
                              +----> [Configuration Database<br/>(Rules Storage)]
```

**Key Components in the Diagram:**
1. **Client:** Represents the source of requests (users or applications) sending billions of HTTP requests daily.
2. **Global CDN/Load Balancer:** Distributes incoming traffic across multiple rate limiter service instances deployed in different availability zones to ensure even load distribution and minimize latency.
3. **Rate Limiter Service Instances (Across Multiple AZs):** Multiple instances of the rate limiter service running in different AZs and on different hosts to handle requests, enforce limits, and provide fault tolerance.
4. **Distributed Redis Cluster (Across AZs):** A distributed in-memory cache for storing rate-limiting data (timestamps per user per service) with replication and sharding for scalability and high availability.
5. **Application Servers (Across AZs):** Backend servers that process requests approved by the rate limiter, also deployed across multiple AZs for resilience.
6. **Monitoring & Logging System:** Captures metrics and logs for system health, throttled requests, and usage patterns to detect anomalies and ensure operational visibility.
7. **Configuration Database:** Stores rate-limiting rules and configurations (not in the critical path for real-time processing to avoid latency).

**Flow of a Request:**
- A client sends a request, which is routed through the global CDN or load balancer to the nearest or least-loaded rate limiter service instance based on geographic proximity or load.
- The rate limiter instance identifies the user (e.g., via user ID or IP) and checks the request history in the distributed Redis cluster.
- If the user is within the rate limit (based on timestamps in the last 10 seconds), the request is forwarded to the application servers, and a new timestamp is added to Redis.
- If the limit is exceeded, an error response (e.g., HTTP 429 Too Many Requests) is returned to the client with a retry-after header.
- Metrics and logs are sent to the monitoring system for analysis.

---

### **Detailed Explanation of Key Components and Design Choices**

#### **1. Rate Limiter Service Instances (Across Multiple AZs)**
- **Purpose:** These instances process incoming requests, apply rate-limiting logic, and decide whether to allow or block requests based on data stored in Redis.
- **Design Consideration:** Deploying across multiple AZs ensures high availability and fault tolerance. If one AZ experiences an outage, others can continue to serve requests. Each instance runs on separate hosts to avoid single points of failure.
- **Functionality:** For each request, the service extracts a client identifier (e.g., user ID), queries Redis for the user’s request history (timestamps in the last 10 seconds), and checks against the defined limit (e.g., 100 requests per 10 seconds per service). It updates Redis with new timestamps for allowed requests and returns appropriate responses for throttled requests.
- **Scalability:** Horizontal scaling is achieved by deploying thousands of rate limiter instances across AZs. The global load balancer ensures even distribution of traffic, preventing any instance from being overwhelmed.
- **Latency:** The service is designed to minimize processing overhead by relying on fast Redis queries, ensuring decisions are made in milliseconds.

#### **2. Distributed Redis Cluster (Across AZs)**
- **Purpose:** Stores rate-limiting data (timestamps per user per service) in memory for fast access and updates, critical for handling billions of requests with low latency.
- **Design Consideration:** A distributed Redis cluster is used to handle the scale of 1 billion users and up to 80 TB of data (at peak). Redis supports sharding (partitioning data across multiple nodes) and replication (maintaining copies of data for redundancy), making it ideal for distributed environments.
- **Data Structure:** For each user and service, we store a list of timestamps (up to 100) in a Redis List or Sorted Set with a key format like `user:<user_id>:service:<service_id>`. Timestamps older than 10 seconds are automatically expired using Redis’s TTL (Time-To-Live) feature or trimmed during updates.
- **Storage Management:** Given the 10-second retention period, Redis’s TTL ensures old data is automatically removed, keeping the memory footprint manageable. For 1 billion users, even if only a fraction are active simultaneously, sharding distributes the load across multiple Redis nodes.
  - **Example Storage per Active User:** 80 KB per user for 100 services.
  - **Active Users Assumption:** If 10% of users (100 million) are active at any moment, the storage need is 100M × 80 KB = 8 TB, which is feasible with a Redis cluster of sufficient nodes.
- **Deployment Across AZs:** Redis nodes are deployed across multiple AZs with replication to ensure data availability if an AZ fails. Cross-AZ replication introduces slight latency but is necessary for resilience.
- **Consistency:** Redis provides eventual consistency in a distributed setup. For rate limiting, slight inconsistencies (e.g., a user briefly exceeding limits due to replication lag) are acceptable given the short time window and non-critical nature of exact counts.
- **Scalability:** The cluster can scale by adding more shards (nodes) as the user base or request volume grows. Each shard handles a subset of user data based on a consistent hashing mechanism to distribute keys.
- **Performance:** Redis offers sub-millisecond read/write latency, critical for real-time rate-limiting decisions under high load (billions of requests daily).

#### **3. Global CDN/Load Balancer**
- **Purpose:** Routes client requests to the nearest or least-loaded rate limiter instance across AZs, reducing latency and ensuring even traffic distribution.
- **Design Consideration:** A global CDN (like Akamai or Cloudflare) or a cloud-native load balancer (like AWS Application Load Balancer with cross-region support) is used to handle the scale of billions of requests. It directs traffic based on geographic proximity, instance health, and load.
- **Functionality:** Provides a single entry point for clients, masks the complexity of multiple AZs, and ensures fault tolerance by rerouting traffic if an instance or AZ fails.
- **Scalability:** Designed to handle massive traffic spikes by leveraging global infrastructure and dynamic scaling of rate limiter instances behind it.

#### **4. Application Servers (Across AZs)**
- **Purpose:** Process requests that pass the rate limiter checks, delivering the core service functionality.
- **Design Consideration:** Deployed across multiple AZs for high availability, these servers are protected from overload by the rate limiter, ensuring they handle only approved traffic.
- **Functionality:** Receive forwarded requests from the rate limiter and respond to clients with the appropriate data or service output.

#### **5. Monitoring & Logging System**
- **Purpose:** Tracks system performance, logs throttling events, and monitors Redis cluster health to ensure operational visibility at scale.
- **Design Consideration:** Tools like Prometheus (for metrics) and ELK Stack (for logs) are deployed to handle the volume of data generated by billions of requests. Alerts are set for anomalies like sudden traffic spikes or Redis node failures.
- **Functionality:** Logs details of throttled requests (user ID, timestamp, service), monitors latency of rate limiter decisions, and tracks Redis memory usage and request rates.

#### **6. Configuration Database**
- **Purpose:** Stores rate-limiting rules (e.g., 100 requests per 10 seconds per service) and configurations for different user groups or services.
- **Design Consideration:** A separate relational or NoSQL database is used, outside the critical path of real-time request processing to avoid latency. Rules are cached in Redis or loaded into memory by rate limiter instances during initialization or updates.
- **Functionality:** Provides a centralized repository for administrators to define and update throttling policies without impacting live traffic.

---

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/ghltfsd4k16uzkebbk25.png)

### **Key Design Principles and Considerations for Scale**

1. **Handling Billions of Requests Daily:**
   - The system is designed to process high request volumes by distributing traffic across thousands of rate limiter instances and Redis nodes. Assuming 10 billion requests per day, that’s approximately 115,000 requests per second (RPS). With 1,000 rate limiter instances, each handles ~115 RPS, which is manageable.
   - Redis’s high throughput (hundreds of thousands of operations per second per node) ensures it can handle the read/write load for timestamp checks and updates, especially with sharding.

2. **Managing 1 Billion Users with Peak Activity:**
   - With 1 billion users and up to 100 requests per user at any moment, peak RPS could reach 100 billion if all users act simultaneously (unrealistic). A more realistic peak of 10% active users (100 million) yields 10 billion requests at a moment, or ~115,000 RPS over a 10-second window, aligning with daily estimates.
   - Redis sharding ensures user data is evenly distributed across nodes, preventing hotspots. For 100 million active users, 100 shards mean ~1 million users per shard, with 80 GB of data per shard (well within modern server memory limits).

3. **Short Retention Period (10 Seconds):**
   - The 10-second retention period is a critical optimization. Redis’s TTL feature automatically expires old timestamps, ensuring memory usage remains proportional to active users rather than the entire user base.
   - For each user-service pair, trimming lists to the last 100 timestamps (or last 10 seconds) during updates further reduces storage needs.

4. **Distributed Deployment Across AZs and Hosts:**
   - Rate limiter instances, Redis nodes, and application servers are deployed across multiple AZs within each region and potentially across regions for global coverage. This ensures resilience against AZ or regional outages.
   - Cross-AZ replication in Redis introduces minor latency (a few milliseconds) but is necessary for data durability. Rate limiter instances connect to the nearest Redis node to minimize network delays.
   - Running on multiple hosts within each AZ prevents hardware failures from disrupting service, with load balancers rerouting traffic as needed.

5. **Low Latency and High Performance:**
   - Redis’s in-memory nature ensures sub-millisecond latency for rate-limiting checks, critical for real-time decisions under high load.
   - Rate limiter instances are colocated with Redis nodes within the same AZ where possible to reduce network hops.
   - The global CDN/load balancer minimizes client-to-service latency by routing to the nearest AZ.

6. **Fault Tolerance and High Availability:**
   - Redis replication (master-slave or sentinel mode) ensures data availability if a node fails. Automatic failover mechanisms promote a replica to master during outages.
   - Rate limiter instances are stateless, allowing the load balancer to redirect traffic to healthy instances if one fails.
   - Cross-AZ and cross-region redundancy ensures the system remains operational during localized failures.

7. **Storage Optimization for 80 KB per User:**
   - The 80 KB per user (for 100 services) is manageable with Redis’s efficient storage and the short retention period. Only active users consume memory at any time, and expired data is automatically purged.
   - If memory constraints arise, Redis can use disk-based persistence as a fallback (though slower) or prioritize eviction of least-recently-used data.

---

### **Summary of HLD with Distributed Redis**
This high-level design for a rate limiter leverages a distributed Redis cluster to handle the scale of billions of daily requests from a user base of one billion. By deploying rate limiter instances, Redis nodes, and application servers across multiple availability zones and hosts, the system ensures high availability, fault tolerance, and low latency. Redis’s sharding and replication capabilities manage the data volume (up to 80 TB in worst case, but far less with a 10-second retention), while its TTL feature keeps memory usage proportional to active users. The global CDN/load balancer optimizes traffic distribution, and monitoring systems provide visibility into performance at scale.

This design meets the functional requirement of enforcing rate limits per user per service and non-functional requirements like scalability, availability, and low latency, making it suitable for a distributed, high-traffic environment.

If you need assistance with specific implementation details or tools for diagramming this architecture, please let me know. Remember to ensure that any third-party tools used comply with Oracle’s security and compliance guidelines.



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli