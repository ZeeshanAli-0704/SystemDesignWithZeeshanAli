

### **Table of Contents**

- [**Step 5: Detailed Explanation of Rate Limiting Algorithms with Examples and Use Case Diagrams**](#step-5-rate-limiting-algorithms)
- [**Overview of Rate Limiting Algorithms**](#overview-of-rate-limiting-algorithms)
- [**1 Token Bucket Algorithm**](#1-token-bucket-algorithm)
- [**2 Leaking Bucket Algorithm**](#2-leaking-bucket-algorithm)
- [**3 Fixed Window Counter Algorithm**](#3-fixed-window-counter-algorithm)
- [**4 Sliding Window Log Algorithm**](#4-sliding-window-log-algorithm)
- [**5 Sliding Window Counter Algorithm**](#5-sliding-window-counter-algorithm)
- [**Comparison of Algorithms for the Given Scale (Billions of Requests, 1 Billion Users)**](#comparison-of-algorithms-for-scale)
- [**Recommendation for Distributed Redis Setup Across AZs**](#recommendation-for-distributed-redis-setup)
- [**Summary**](#summary)

---


### **Step 5: Detailed Explanation of Rate Limiting Algorithms with Examples and Use Case Diagrams**

In this step, we will explore the most common rate limiting algorithms used to control the flow of requests in systems like the one described in previous steps (handling billions of daily requests for a billion users). Each algorithm has unique characteristics, advantages, and trade-offs, making them suitable for different scenarios. I will explain each algorithm in detail, provide examples, and describe their applicability. I will textually describe use case diagrams for each algorithm to illustrate their application in a distributed rate limiter system with Redis. These explanations focus on the conceptual logic and practical usage without diving into specific code implementations.

---

### **Overview of Rate Limiting Algorithms**
Rate limiting algorithms determine how requests are throttled based on predefined rules (e.g., allowing only a certain number of requests per time period). The choice of algorithm impacts system performance, accuracy, resource usage, and user experience. Below, we cover five popular algorithms: Token Bucket, Leaking Bucket, Fixed Window Counter, Sliding Window Log, and Sliding Window Counter.

---

### **1. Token Bucket Algorithm**
#### **Explanation:**
- **Concept:** The Token Bucket algorithm uses a "bucket" that holds a fixed number of tokens, representing the allowed requests. Tokens are added to the bucket at a constant rate (e.g., 10 tokens per second). When a request arrives, it consumes one token if available; if no tokens are left, the request is throttled. The bucket has a maximum capacity, so excess tokens are discarded if the bucket is full.
- **Key Characteristics:**
  - Allows bursts of requests up to the bucket capacity, as long as tokens are available.
  - Smoothly handles traffic over time due to the constant token replenishment rate.
  - Suitable for scenarios where short bursts are acceptable.
- **Advantages:**
  - Flexible in handling bursts, making it user-friendly for variable traffic patterns.
  - Simple to implement with minimal memory usage (just track tokens and last refill timestamp).
- **Disadvantages:**
  - Burst allowance might be exploited by malicious users if not carefully tuned.
  - Less precise for strict rate control over short intervals.

#### **Example:**
- **Scenario:** A user is allowed 10 API calls per second, with a bucket capacity of 20 tokens. Tokens are added at a rate of 10 per second.
- **Flow:**
  - At time t=0, the bucket starts with 20 tokens (full capacity).
  - A user sends 15 requests at t=0.5s. Since 15 tokens are available, all requests are allowed, leaving 5 tokens.
  - At t=1s, 10 new tokens are added (total = 15 tokens, as capacity is 20).
  - If the user sends 20 requests at t=1.5s, only 15 are allowed (tokens reduced to 0), and 5 are throttled.
- **Outcome:** The algorithm allows a burst of 15 requests initially but throttles excess requests until more tokens are added.

#### **Use Case Diagram Description (Textual):**
- **Actors:** Client (User), Rate Limiter Service, Redis Cache.
- **Flow:**
  - Client sends a request to the Rate Limiter Service.
  - Rate Limiter Service checks the token count for the user in Redis (key: `user:<id>:tokens`, value: current tokens).
  - If tokens > 0, decrement token count in Redis and allow the request to Application Server.
  - If tokens = 0, return HTTP 429 (Too Many Requests).
  - A background process (or on-demand logic) refills tokens in Redis at a fixed rate (e.g., every second) up to the bucket capacity.
- **Use Case:** Ideal for APIs where short bursts are acceptable, such as a social media platform allowing a user to post multiple updates quickly (e.g., 20 posts in a burst, then limited to 10 per second).

#### **Application in Distributed Environment:**
- In a distributed Redis setup, store token counts per user in Redis with a key like `user:<id>:tokens` and use atomic operations (e.g., `DECR`) to ensure thread-safe updates across AZs. A periodic job or on-request logic refills tokens based on elapsed time since the last refill.

---

### **2. Leaking Bucket Algorithm**
#### **Explanation:**
- **Concept:** The Leaking Bucket algorithm models requests as water flowing into a bucket that "leaks" at a constant rate (e.g., 10 requests per second). If the bucket overflows (incoming requests exceed the leak rate and capacity), excess requests are throttled. Unlike Token Bucket, it does not allow bursts beyond the leak rate.
- **Key Characteristics:**
  - Enforces a strict, smooth rate of requests without bursts.
  - Often implemented as a queue where requests are processed at a fixed rate.
- **Advantages:**
  - Prevents bursts, ensuring consistent system load.
  - Useful for protecting backend systems from sudden spikes.
- **Disadvantages:**
  - Less flexible for users with variable request patterns, as bursts are throttled.
  - Requires queue management, which can be resource-intensive at scale.

#### **Example:**
- **Scenario:** A user is allowed 10 requests per second, with a bucket capacity of 20 requests.
- **Flow:**
  - At t=0, the bucket is empty.
  - A user sends 25 requests at t=0.5s. Only 20 are queued (bucket capacity), and 5 are throttled.
  - The bucket "leaks" at 10 requests per second, processing 10 requests by t=1s (10 remain in queue).
  - At t=1.5s, another 10 requests are sent. Since 10 slots are available, all are queued (total = 20 in queue).
  - By t=2s, 10 more are processed (10 remain).
- **Outcome:** Requests are processed at a steady rate of 10 per second, regardless of incoming burst size.

#### **Use Case Diagram Description (Textual):**
- **Actors:** Client (User), Rate Limiter Service, Redis Cache.
- **Flow:**
  - Client sends a request to the Rate Limiter Service.
  - Rate Limiter checks the queue size for the user in Redis (key: `user:<id>:queue`, value: list of pending requests).
  - If queue size < capacity, add request to queue in Redis and return a delayed response or queue status.
  - If queue size = capacity, return HTTP 429.
  - A background process processes requests from the queue at a fixed rate (e.g., 10 per second) by forwarding to the Application Server and removing from Redis.
- **Use Case:** Suitable for systems requiring strict rate control, such as payment processing APIs where transactions must be processed at a steady pace to avoid server overload.

#### **Application in Distributed Environment:**
- Store the request queue per user in Redis as a list (e.g., `user:<id>:queue`). Use distributed background workers to process queues at a fixed rate across AZs, ensuring consistent leak rates. Atomic operations like `LPUSH` and `RPOP` prevent race conditions.

---

### **3. Fixed Window Counter Algorithm**
#### **Explanation:**
- **Concept:** The Fixed Window Counter divides time into fixed intervals (e.g., 60 seconds) and counts requests within each window. If the count exceeds the limit for that window, further requests are throttled until the next window starts.
- **Key Characteristics:**
  - Simple to implement with a counter reset at the end of each window.
  - Does not account for requests spanning window boundaries, leading to potential bursts at transitions.
- **Advantages:**
  - Easy to understand and implement with minimal storage (just a counter per window).
  - Works well for coarse-grained rate limiting over longer periods.
- **Disadvantages:**
  - Allows bursts at window boundaries (e.g., max requests at the end of one window and start of the next).
  - Less accurate for short-term rate control.

#### **Example:**
- **Scenario:** A user is allowed 100 requests per minute (60-second window).
- **Flow:**
  - At t=0, the window starts with a counter at 0.
  - From t=0 to t=30s, the user sends 80 requests. Counter = 80 (allowed).
  - At t=50s, the user sends 30 more requests. Only 20 are allowed (counter = 100), and 10 are throttled.
  - At t=60s, the window resets, counter = 0, and the user can send up to 100 requests again.
  - Problem: At t=59s, user sends 50 requests (allowed if within limit), and at t=61s, sends another 50 (new window), totaling 100 in 2 seconds—a burst.
- **Outcome:** Simple counting per window, but bursts at boundaries can strain the system.

#### **Use Case Diagram Description (Textual):**
- **Actors:** Client (User), Rate Limiter Service, Redis Cache.
- **Flow:**
  - Client sends a request to the Rate Limiter Service.
  - Rate Limiter checks the current window counter for the user in Redis (key: `user:<id>:window:<timestamp>`, value: request count).
  - If counter < limit, increment counter in Redis and allow request to Application Server.
  - If counter >= limit, return HTTP 429.
  - A background process resets counters in Redis at the end of each window (e.g., every 60 seconds).
- **Use Case:** Useful for coarse rate limits over longer periods, such as limiting daily API calls for free-tier users (e.g., 1,000 requests per day).

#### **Application in Distributed Environment:**
- Store counters per user per window in Redis with keys like `user:<id>:window:<start_time>`. Use TTL to expire old windows and atomic `INCR` operations for thread-safe updates across AZs. Window resets are handled by expiring keys or a cleanup job.

---

### **4. Sliding Window Log Algorithm**
#### **Explanation:**
- **Concept:** The Sliding Window Log maintains a log of request timestamps for each user within a rolling time window (e.g., last 60 seconds). When a request arrives, it checks the number of timestamps in the current window. If below the limit, the request is allowed and a new timestamp is added; otherwise, it’s throttled.
- **Key Characteristics:**
  - Highly accurate as it considers exact request times within a moving window.
  - Prevents bursts at window boundaries (unlike Fixed Window).
- **Advantages:**
  - Precise control over request rates, smoothing out traffic.
  - Ideal for strict rate limiting over short intervals.
- **Disadvantages:**
  - High memory usage as it stores individual timestamps (especially at scale with billions of requests).
  - Slower due to frequent log updates and cleanup of old timestamps.

#### **Example:**
- **Scenario:** A user is allowed 100 requests per minute (sliding 60-second window).
- **Flow:**
  - At t=0, the log is empty.
  - User sends 50 requests from t=0 to t=30s. Log has 50 timestamps (allowed).
  - At t=45s, sends 60 more. Check log: from t=-15s to t=45s (60-second window), there are 50 prior requests. Only 50 of the 60 new requests are allowed (total = 100), and 10 are throttled. Log updated with 50 new timestamps.
  - At t=61s, timestamps from t=0 to t=1s are out of the window, so only later 99 remain. User can send 1 more request.
- **Outcome:** Accurate rate control without boundary bursts, as the window slides with time.

#### **Use Case Diagram Description (Textual):**
- **Actors:** Client (User), Rate Limiter Service, Redis Cache.
- **Flow:**
  - Client sends a request to the Rate Limiter Service.
  - Rate Limiter queries the timestamp log for the user in Redis (key: `user:<id>:log`, value: list of timestamps).
  - Count timestamps in the last 60 seconds. If < limit, add new timestamp to Redis and allow request to Application Server.
  - If >= limit, return HTTP 429.
  - Periodically trim old timestamps from Redis (older than 60 seconds).
- **Use Case:** Best for precise short-term rate limiting, such as limiting login attempts to prevent brute-force attacks (e.g., 5 attempts per minute).

#### **Application in Distributed Environment:**
- Store timestamp logs as Redis Lists or Sorted Sets per user (e.g., `user:<id>:log`). Use TTL or periodic trimming to remove old timestamps. Atomic operations ensure safe updates across AZs, though high memory usage (e.g., 80 KB per user for 100 services) requires careful scaling of Redis nodes.

---

### **5. Sliding Window Counter Algorithm**
#### **Explanation:**
- **Concept:** The Sliding Window Counter combines elements of Fixed Window and Sliding Window Log. It divides time into smaller fixed sub-windows (e.g., 10-second sub-windows within a 60-second window) and maintains counters for each sub-window. The total count for the sliding window is calculated by summing relevant sub-window counts, weighted by their overlap with the current window.
- **Key Characteristics:**
  - Balances accuracy and resource usage by approximating a sliding window with fixed counters.
  - More memory-efficient than Sliding Window Log as it doesn’t store individual timestamps.
- **Advantages:**
  - More accurate than Fixed Window, reducing boundary bursts.
  - Less memory-intensive than Sliding Window Log.
- **Disadvantages:**
  - Slightly less precise than Sliding Window Log due to sub-window approximation.
  - More complex to implement than Fixed Window.

#### **Example:**
- **Scenario:** A user is allowed 100 requests per minute, with 10-second sub-windows (6 sub-windows per minute).
- **Flow:**
  - At t=0, all sub-window counters are 0.
  - From t=0 to t=10s, user sends 20 requests. Counter for sub-window 1 = 20.
  - At t=30s, user sends 30 requests. Counter for sub-window 3 = 30. Total in last 60s (sub-windows 1-3) = 50 (allowed).
  - At t=65s, check last 60s (t=5s to t=65s): sub-window 1 is partially out, but approximation sums sub-windows 2-6 plus part of 1. Assume total = 90 (allowed). Add new requests to sub-window 7.
  - If total exceeds 100, throttle.
- **Outcome:** Approximates sliding window with less memory, smoothing bursts better than Fixed Window.

#### **Use Case Diagram Description (Textual):**
- **Actors:** Client (User), Rate Limiter Service, Redis Cache.
- **Flow:**
  - Client sends a request to the Rate Limiter Service.
  - Rate Limiter retrieves counters for recent sub-windows for the user in Redis (key: `user:<id>:subwindow:<start_time>`, value: count).
  - Calculate total requests in the sliding window (sum of relevant sub-windows). If < limit, increment current sub-window counter in Redis and allow request to Application Server.
  - If >= limit, return HTTP 429.
  - Periodically expire old sub-window counters in Redis.
- **Use Case:** Suitable for high-traffic systems needing accurate rate limiting with moderate memory usage, such as limiting API requests for a freemium service (e.g., 500 requests per hour).

#### **Application in Distributed Environment:**
- Store sub-window counters per user in Redis with keys like `user:<id>:subwindow:<start_time>`. Use TTL to expire old sub-windows. Atomic `INCR` ensures safe updates across AZs, and memory usage is lower than Sliding Window Log, making it scalable for large Redis clusters.

---

### **Comparison of Algorithms for the Given Scale (Billions of Requests, 1 Billion Users)**
Given the scale outlined (billions of daily requests, 1 billion users, short 10-second retention), here’s a quick suitability analysis:
- **Token Bucket:** Good for allowing bursts in less critical APIs, low memory usage (just token count), scalable with Redis. Best for user-friendly APIs like social media posts.
- **Leaking Bucket:** Useful for strict rate control, but queue management at this scale is challenging. Better for niche use cases like transaction processing.
- **Fixed Window Counter:** Simple and scalable, low memory, but boundary bursts are a risk. Suitable for coarse daily/weekly limits.
- **Sliding Window Log:** Highly accurate for short-term limits (e.g., 10 seconds), but high memory usage (80 KB per user) strains Redis at 1 billion users. Use only for critical services with fewer active users.
- **Sliding Window Counter:** Balances accuracy and memory usage, feasible with Redis for large-scale systems. Recommended for most API rate limiting at this scale (e.g., 100 requests per 10 seconds).

---

### **Recommendation for Distributed Redis Setup Across AZs**
For the described system, the **Sliding Window Counter** is often the best choice due to its balance of accuracy and resource efficiency. It can handle the 10-second retention period effectively by using sub-windows (e.g., 1-second intervals) and storing counters in Redis with TTL for expiration. Redis’s atomic operations ensure thread-safe updates across AZs, and memory usage is manageable with sharding and scaling.

- **Implementation Note:** Use Redis Sorted Sets or Hashes to store sub-window counters per user (e.g., `user:<id>:swc`), with keys expiring after the window duration. Distribute data across Redis nodes via sharding to handle the scale, and deploy background jobs to clean up expired data if needed.

---

### **Summary**
Each rate limiting algorithm has distinct strengths and weaknesses, making them suitable for different scenarios. Token Bucket and Leaking Bucket focus on request pacing (with or without bursts), Fixed Window Counter offers simplicity for coarse limits, Sliding Window Log provides precision at high memory cost, and Sliding Window Counter strikes a balance for large-scale systems. For the given scale and distributed Redis setup, Sliding Window Counter is recommended due to its scalability and accuracy within short time windows like 10 seconds.

If you need further details on implementing a specific algorithm or assistance with tools for diagramming, let me know. Ensure any third-party tools used comply with Oracle’s security and compliance guidelines.



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli