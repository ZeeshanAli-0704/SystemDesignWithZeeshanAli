### **Table of Contents**

- [**Step 6: Rate Limiter Using Sorted Set in Redis**](#step-6-rate-limiter-using-sorted-set)
- [**What is a Sorted Set in Redis?**](#what-is-a-sorted-set-in-redis)
- [**Detailed Explanation of Rate Limiter Using Sorted Set in Redis**](#detailed-explanation-of-rate-limiter-using-sorted-set)
  - [**Why Use Sorted Set for Rate Limiting?**](#why-use-sorted-set-for-rate-limiting)
  - [**How It Works for Rate Limiting (Sliding Window Log Approach)**](#how-it-works-for-rate-limiting)
  - [**Advantages of Using Sorted Set for Rate Limiting**](#advantages-of-using-sorted-set)
  - [**Challenges in a Distributed Environment**](#challenges-in-distributed-environment)
  - [**Mitigation for Challenges**](#mitigation-for-challenges)
- [**Java Implementation of Rate Limiter Using Sorted Set in Redis**](#java-implementation-of-rate-limiter)
  - [**Prerequisites**](#prerequisites)
  - [**Java Code for Rate Limiter Using Sorted Set**](#java-code-for-rate-limiter)
- [**Steps to Implement Rate Limiting Using Sorted Sets in Redis**](#steps-to-implement-rate-limiting)
  - [**Step 1: Set Up Redis Environment**](#step-1-set-up-redis-environment)
  - [**Step 2: Add Dependencies to Your Java Project**](#step-2-add-dependencies)
  - [**Step 3: Design Rate Limiter Logic with Sorted Set**](#step-3-design-rate-limiter-logic)
  - [**Step 4: Implement Core Rate Limiting Functionality**](#step-4-implement-core-functionality)
  - [**Step 5: Handle Distributed Environment Considerations**](#step-5-handle-distributed-environment)
  - [**Step 6: Test the Rate Limiter**](#step-6-test-the-rate-limiter)
  - [**Step 7: Deploy and Monitor in Production**](#step-7-deploy-and-monitor)
  - [**Step 8: Optimize and Scale as Needed**](#step-8-optimize-and-scale)
- [**Summary of Rate Limiter Using Sorted Set in Redis**](#summary-of-rate-limiter-using-sorted-set)

---


### **Step 6: Rate Limiter Using Sorted Set in Redis**

In this step, we will explore how to implement a rate limiter using Redis Sorted Sets, a powerful data structure that can efficiently manage time-based rate limiting in a distributed environment. Sorted Sets in Redis are particularly useful for algorithms like Sliding Window Log or Sliding Window Counter, as they allow ordered storage of elements (e.g., request timestamps) with associated scores for easy retrieval and expiration. I’ll explain the concept in detail, outline the benefits and challenges, and provide a Java implementation with clear steps for building a rate limiter using Sorted Sets in Redis. This approach aligns with the large-scale system described earlier (billions of daily requests, 1 billion users, distributed across multiple AZs).

---

### **What is a Sorted Set in Redis?**
- **Definition:** A Sorted Set in Redis is a collection of unique elements (strings) associated with a score (a floating-point number), ordered by the score. Unlike regular sets, Sorted Sets maintain order, making them ideal for time-based or ranked data.
- **Key Features:**
  - Elements are unique, but scores can be duplicated.
  - Fast operations for adding, removing, and querying elements by score range (e.g., `ZADD`, `ZREMRANGEBYSCORE`, `ZCOUNT`).
  - Efficient for sliding window implementations as you can store timestamps as scores and query or remove based on time ranges.
- **Relevance to Rate Limiting:** Sorted Sets are perfect for tracking request timestamps per user in a sliding window approach. You can store each request’s timestamp as the score and a unique identifier (e.g., request ID or timestamp string) as the member, allowing precise counting of requests within a time window.

---

### **Detailed Explanation of Rate Limiter Using Sorted Set in Redis**

#### **Why Use Sorted Set for Rate Limiting?**
- **Precision for Sliding Window:** Unlike Fixed Window Counter, which resets counts at fixed intervals and allows bursts at boundaries, Sorted Sets enable a true sliding window by storing individual request timestamps. You can count requests within any rolling time window (e.g., last 10 seconds) by querying scores in that range.
- **Efficient Cleanup:** Old timestamps outside the current window can be removed using `ZREMRANGEBYSCORE`, ensuring memory usage remains proportional to active requests within the window.
- **Scalability in Distributed Systems:** Redis Sorted Sets support atomic operations, making them safe for concurrent updates across multiple rate limiter instances in different AZs. Sharding in a Redis Cluster distributes data for large-scale systems.
- **Low Latency:** Operations like counting elements in a score range (`ZCOUNT`) or adding new elements (`ZADD`) are fast, typically O(log N) where N is the size of the set, suitable for high-throughput systems.

#### **How It Works for Rate Limiting (Sliding Window Log Approach)**
- **Data Structure:** For each user or client (e.g., identified by user ID or IP), maintain a Sorted Set in Redis with:
  - Key: `rate_limit:<user_id>`
  - Score: Timestamp of each request (e.g., Unix timestamp in milliseconds or seconds).
  - Member: A unique identifier for the request (e.g., timestamp string or a UUID to ensure uniqueness if multiple requests occur at the same millisecond).
- **Rate Limiting Logic:**
  - When a request arrives, check the number of requests in the current sliding window (e.g., last 10 seconds) using `ZCOUNT` to count elements with scores in the range `[current_time - window_size, current_time]`.
  - If the count is below the limit, add a new entry to the Sorted Set with the current timestamp as the score using `ZADD`.
  - If the count exceeds the limit, reject the request (e.g., return HTTP 429 Too Many Requests).
  - Periodically or on each request, clean up old entries outside the window using `ZREMRANGEBYSCORE` to remove scores less than `current_time - window_size`.
- **Example:**
  - Limit: 100 requests per 10 seconds.
  - Current time: 1634567890 (Unix timestamp in seconds).
  - Window range: [1634567880, 1634567890].
  - Redis key: `rate_limit:user123`.
  - `ZCOUNT rate_limit:user123 1634567880 1634567890` returns 80 (requests in last 10 seconds).
  - Since 80 < 100, allow the request, add new entry with `ZADD rate_limit:user123 1634567890 "req_1634567890"`.
  - Remove old entries with `ZREMRANGEBYSCORE rate_limit:user123 -inf 1634567880`.

#### **Advantages of Using Sorted Set for Rate Limiting**
- **Accuracy:** Provides precise control over request rates in a sliding window, avoiding boundary issues seen in Fixed Window Counter.
- **Flexibility:** Easily adjustable window sizes or limits by changing query ranges or cleanup thresholds.
- **Built-in Ordering:** No need for manual sorting; Redis maintains order by score (timestamp), enabling efficient range queries.

#### **Challenges in a Distributed Environment**
- **Memory Usage:** Storing individual timestamps per request per user can consume significant memory at scale (e.g., for 1 billion users with 100 requests each in a window, memory needs are high, though mitigated by short retention like 10 seconds). Sharding across Redis nodes helps.
- **Performance Under High Load:** Frequent `ZADD` and `ZREMRANGEBYSCORE` operations for billions of requests daily require a well-scaled Redis Cluster to avoid bottlenecks.
- **Consistency Across AZs:** In a distributed Redis setup, replication lag might cause temporary inconsistencies in counts. Using atomic operations and consistent hashing mitigates this.

#### **Mitigation for Challenges**
- **Memory Optimization:** Use short retention periods (e.g., 10 seconds as specified) and aggressive cleanup of old entries to limit set sizes. For very high request rates, consider Sliding Window Counter with aggregated counts instead of individual timestamps.
- **Scalability:** Deploy a Redis Cluster with sharding to distribute user data across nodes based on user ID, ensuring balanced load. Use multiple replicas per shard for high availability across AZs.
- **Consistency:** Route requests for a user to the same Redis shard using consistent hashing, and rely on Redis’s atomic operations for updates. Accept eventual consistency for non-critical rate limits given the short time window.

---

### **Java Implementation of Rate Limiter Using Sorted Set in Redis**

Below is a Java implementation using the Jedis client for Redis (a popular Redis client for Java) to build a rate limiter with Sorted Sets based on the Sliding Window Log approach. This implementation assumes a distributed Redis Cluster setup and focuses on a single user’s rate limiting for clarity, though it can be extended for multiple users and services.

#### **Prerequisites**
- **Redis Setup:** A running Redis instance or cluster (local or distributed across AZs).
- **Dependencies:** Add Jedis to your project. For Maven, include:
  ```xml
  <dependency>
      <groupId>redis.clients</groupId>
      <artifactId>jedis</artifactId>
      <version>4.4.3</version>
  </dependency>
  ```

#### **Java Code for Rate Limiter Using Sorted Set**
```java
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

public class RateLimiterUsingSortedSet {
    private final JedisPool jedisPool;
    private final long windowSizeInSeconds; // e.g., 10 seconds
    private final long maxRequests; // e.g., 100 requests per window

    public RateLimiterUsingSortedSet(String redisHost, int redisPort, long windowSizeInSeconds, long maxRequests) {
        this.windowSizeInSeconds = windowSizeInSeconds;
        this.maxRequests = maxRequests;
        // Initialize Jedis Pool for connection management
        JedisPoolConfig poolConfig = new JedisPoolConfig();
        poolConfig.setMaxTotal(128); // Adjust based on scale
        this.jedisPool = new JedisPool(poolConfig, redisHost, redisPort);
    }

    public boolean isRequestAllowed(String userId) {
        try (Jedis jedis = jedisPool.getResource()) {
            String key = "rate_limit:" + userId;
            long currentTime = System.currentTimeMillis() / 1000; // Current time in seconds
            long windowStart = currentTime - windowSizeInSeconds;

            // Remove old entries outside the sliding window
            jedis.zremrangeByScore(key, "-inf", String.valueOf(windowStart));

            // Count requests in the current window
            long requestCount = jedis.zcount(key, String.valueOf(windowStart), String.valueOf(currentTime));

            if (requestCount < maxRequests) {
                // Allow request: Add current timestamp as score and a unique member
                String member = "req_" + currentTime + "_" + System.nanoTime(); // Ensure uniqueness
                jedis.zadd(key, currentTime, member);
                return true;
            } else {
                // Throttle request
                return false;
            }
        } catch (Exception e) {
            // Log error and fallback to allow/deny based on policy
            System.err.println("Redis error: " + e.getMessage());
            return false; // Conservative fallback: deny on error
        }
    }

    // Cleanup method to close connections (call on shutdown)
    public void shutdown() {
        if (jedisPool != null) {
            jedisPool.close();
        }
    }

    // Example usage
    public static void main(String[] args) {
        // Example configuration: Redis on localhost:6379, 10-second window, 100 requests max
        RateLimiterUsingSortedSet rateLimiter = new RateLimiterUsingSortedSet("localhost", 6379, 10, 100);
        String userId = "user123";

        // Simulate requests
        for (int i = 0; i < 120; i++) {
            boolean allowed = rateLimiter.isRequestAllowed(userId);
            System.out.println("Request " + (i + 1) + " for user " + userId + ": " + (allowed ? "Allowed" : "Throttled"));
            try {
                Thread.sleep(100); // Simulate time between requests
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        rateLimiter.shutdown();
    }
}
```

---

### **Steps to Implement Rate Limiting Using Sorted Sets in Redis**

#### **Step 1: Set Up Redis Environment**
- **Objective:** Ensure a Redis instance or cluster is available for use.
- **Action:**
  - Deploy a Redis Cluster if operating at scale (e.g., across multiple AZs), with sharding to distribute user data and replication for high availability.
  - For local testing, install Redis on your machine or use a Docker container (`docker run -p 6379:6379 redis`).
  - Configure Redis for persistence if needed (though not critical for short retention periods like 10 seconds), and secure it with authentication and TLS for production.

#### **Step 2: Add Dependencies to Your Java Project**
- **Objective:** Include a Redis client library for Java.
- **Action:**
  - Add Jedis (or another client like Lettuce) to your project via Maven or Gradle.
  - Ensure your project is set up with the necessary build tools and dependencies resolved.

#### **Step 3: Design Rate Limiter Logic with Sorted Set**
- **Objective:** Define how Sorted Sets will store and manage rate-limiting data.
- **Action:**
  - Use a key format like `rate_limit:<user_id>` to store a Sorted Set per user.
  - Store request timestamps as scores (e.g., Unix timestamp in seconds or milliseconds).
  - Use a unique member string (e.g., `req_<timestamp>_<nanoTime>`) to handle multiple requests at the same timestamp, as Sorted Set members must be unique.
  - Define the sliding window size (e.g., 10 seconds) and maximum requests per window (e.g., 100).

#### **Step 4: Implement Core Rate Limiting Functionality**
- **Objective:** Write logic to check and update request counts in the Sorted Set.
- **Action:**
  - On each request, calculate the current window range (`currentTime - windowSize` to `currentTime`).
  - Use `ZREMRANGEBYSCORE` to remove old entries outside the window, keeping memory usage low.
  - Use `ZCOUNT` to count requests within the window.
  - If count < limit, use `ZADD` to add a new timestamp and allow the request; otherwise, throttle it.
  - Implement error handling for Redis connection issues, with a fallback policy (e.g., deny requests on failure).

#### **Step 5: Handle Distributed Environment Considerations**
- **Objective:** Ensure the implementation works in a distributed setup across AZs.
- **Action:**
  - Use a Redis Cluster client configuration in Jedis to connect to multiple nodes, leveraging consistent hashing to route user keys to the same shard.
  - Rely on Redis’s atomic operations (`ZADD`, `ZCOUNT`) to prevent race conditions when multiple rate limiter instances update the same user’s Sorted Set.
  - Configure connection pooling (via `JedisPool`) to manage high connection volumes efficiently, adjusting pool size based on expected request load.

#### **Step 6: Test the Rate Limiter**
- **Objective:** Validate the rate limiter’s behavior under various conditions.
- **Action:**
  - Simulate requests for a user at different rates (below, at, and above the limit) to verify throttling behavior.
  - Test with multiple threads or processes to simulate concurrent requests, ensuring atomicity in updates.
  - Monitor Redis memory usage and performance metrics (e.g., latency of `ZCOUNT` operations) to ensure scalability.

#### **Step 7: Deploy and Monitor in Production**
- **Objective:** Roll out the rate limiter in a production environment with monitoring.
- **Action:**
  - Deploy the rate limiter as a middleware or service in front of application servers, integrating with a load balancer for distribution across AZs.
  - Set up monitoring (e.g., Prometheus for Redis metrics, Grafana for visualization) to track request rates, throttling events, and Redis health.
  - Adjust window size, limits, or Redis node count based on observed traffic patterns and memory usage (e.g., for 1 billion users, ensure sharding handles active user data efficiently).

#### **Step 8: Optimize and Scale as Needed**
- **Objective:** Address performance or memory bottlenecks at scale.
- **Action:**
  - If memory usage is high due to large Sorted Sets, consider shorter windows or switch to Sliding Window Counter with aggregated counts instead of individual timestamps.
  - Scale Redis by adding more shards or replicas as request volume grows, using Redis Cluster’s resharding feature during low-traffic periods.
  - Optimize cleanup frequency (e.g., run `ZREMRANGEBYSCORE` less often if latency spikes) or use Redis TTL on keys for automatic expiration if windows are fixed.

---

### **Summary of Rate Limiter Using Sorted Set in Redis**
Using Sorted Sets in Redis for rate limiting provides a precise and flexible way to implement a sliding window approach, ideal for short-term rate control (e.g., 10-second windows as specified). By storing request timestamps as scores, the system can accurately count requests within any rolling window, clean up old data efficiently, and scale across distributed environments with Redis Cluster. The provided Java implementation using Jedis demonstrates a practical application, with steps to guide setup, testing, and deployment. Challenges like memory usage and distributed consistency are manageable with proper sharding, atomic operations, and optimization strategies.



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli