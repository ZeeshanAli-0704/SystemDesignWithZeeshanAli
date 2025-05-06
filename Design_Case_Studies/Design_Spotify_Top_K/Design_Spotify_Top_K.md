### **Table of Contents**
1. [System Overview](#system-overview)  
2. [Key Components](#key-components)  
3. [High-Level Architecture](#high-level-architecture)  
4. [Detailed Flow](#detailed-flow)   
5. [Flow Diagram](#flow-diagram)  
6. [Optimizations](#optimizations)   
7. [Scaling for Global Use](#scaling-for-global-use)  
8. [Example API Request](#example-api-request)  

---

### **System Overview**
We need a scalable system to identify the **Top K Heavy Hitters** (most-viewed or most-played posts/songs) from a **continuous stream of data**. This system must:
- **Efficiently aggregate and rank data** by views.
- Support **regional and global Top K** queries.
- Provide high **availability, low latency, and scalability**.

---

### **Key Components**
1. **Database**: 
   - **Cassandra/MongoDB**: Optimized for writes, distributed, and highly available.
2. **Stream Processing**:
   - **Apache Kafka**: Handles the real-time stream of view data.
   - **Stream Processor**: Aggregates views and calculates the top K.
3. **Cache**:
   - **Redis**: Caches the most recent Top K results for quick access.
4. **API Layer**:
   - Exposes endpoints for querying Top K posts (regionally and globally).
5. **Global Aggregator**:
   - Aggregates regional data for global Top K computation.

---

### **High-Level Architecture**


---

### **Detailed Flow**

#### **1. Data Ingestion**
- **Scenario**: Each view event streams into the system.
- **Components Involved**:
  - **Kafka**:
    - Handles the continuous stream of data.
    - Each event contains: `post_id`, `region_id`, and `timestamp`.
  - **Producers**:
    - The frontend (Spotify clients) sends view data via Kafka Producers.
  - **Partitioning**:
    - Kafka topics are partitioned by region to enable parallel processing.

#### **2. Stream Processing**
- **Goal**: Aggregate view counts for each post.
- **Components Involved**:
  - **Stream Processor** (Apache Flink/Samza/Spark Streaming):
    - Aggregates views using **windowing** (time buckets).
    - Example: Count views per `post_id` for 1-minute windows.
    - Updates **Cassandra** in real-time.
  - **Intermediate Results**:
    - Stores partial aggregates (per post, per region) in Cassandra.

#### **3. Database Storage**
- **Cassandra/MongoDB** Schema:
  - **Posts Table**:
    - `post_id` (Primary Key)
    - `content`, `author`, `timestamp`, `view_count`.
  - **Views Table**:
    - `post_id` (Partition Key)
    - `region_id`, `timestamp`, `view_count`.
  - **Regions Table**:
    - `region_id`, `region_name`.

#### **4. Caching**
- **Purpose**: Reduce database load for repeated queries.
- **Implementation**:
  - Cache the top K posts for each region (1-minute TTL).
  - Use **Redis Sorted Sets** for efficient ranking:
    - Key: `region:<region_id>:top_k`
    - Sorted by `view_count`.

#### **5. Regional Aggregation**
- **Goal**: Calculate regional Top K posts.
- **Steps**:
  - Query Cassandra for the latest `view_count` of all posts in the region.
  - Sort posts in descending order by `view_count`.
  - Store Top K results in Redis.

#### **6. Global Aggregation**
- **Goal**: Calculate global Top K posts.
- **Steps**:
  - Periodically fetch the regional Top K posts.
  - Aggregate them using a **global aggregator**.
  - Calculate global Top K using **min-heaps** or Redis Sorted Sets.
  - Store results in a **global cache**.

#### **7. API Layer**
- **Endpoint**: `GET /top-posts`
  - **Query Parameters**:
    - `region`: Filter by region.
    - `timeframe`: Filter by timeframe.
    - `k`: Number of posts to retrieve.
  - **Flow**:
    - Query Redis for cached results.
    - If a cache miss occurs, query Cassandra for real-time computation.

---

### **Flow Diagram**

```plaintext
1. User Interaction
   └──> API Service (`GET /top-posts?region=<region>&k=<value>`)
        ├──> Cache (Redis)
        │     ├──> Hit: Return Top K Results
        │     └──> Miss: Query Cassandra
        └──> Cassandra (Region/Global Tables)
              ├──> Stream Processor Updates
              └──> Kafka Streams (Real-time Views)
```

---

### **Optimizations**

#### **1. Rate Limiting**
- Use **API Gateway** (e.g., Kong, AWS API Gateway) to throttle requests.
- Apply per-user rate limits.

#### **2. Caching**
- Use **Redis** for:
  - Regional Top K: `region:<region_id>:top_k`
  - Global Top K: `global:top_k`.

#### **3. Partitioning**
- Partition Cassandra tables by:
  - **Region**: Distribute data geographically.
  - **Timeframe**: Separate tables for hourly/daily/weekly data.

#### **4. Fault Tolerance**
- **Kafka**: Retains unprocessed events for retries.
- **Cassandra**: Replicates data across nodes.

---

### **Scaling for Global Use**
1. **Distributed Databases**:
   - Use region-specific Cassandra clusters.
2. **Message Streaming**:
   - Use **Kafka MirrorMaker** to replicate streams across regions.
3. **Global Aggregator**:
   - Use Apache Flink for cross-region data aggregation.

---

### **Example API Request**

**Request**:  
```http
GET /top-posts?region=NA&timeframe=24h&k=10
```

**Response**:  
```json
{
  "region": "NA",
  "timeframe": "24h",
  "top_posts": [
    {
      "post_id": "123",
      "author": "Artist1",
      "view_count": 120000,
      "content": "Post Content 1"
    },
    {
      "post_id": "456",
      "author": "Artist2",
      "view_count": 110000,
      "content": "Post Content 2"
    }
  ]
}
```

---
Notes:
Windowing (time buckets) is separate design pattern. If you can explain them in details that will be gr8 in interview.

[rate-limiting-global-tumbling-window-and-sliding-window](https://dev.to/zeeshanali0704/rate-limiting-global-tumbling-window-and-sliding-window-3d2o)


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
