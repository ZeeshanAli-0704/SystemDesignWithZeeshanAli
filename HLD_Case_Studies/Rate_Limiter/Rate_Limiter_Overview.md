# Designing a Rate Limiter

## What is a Rate Limiter?

In a network system, a rate limiter controls the rate of traffic sent by a client or service. In the context of HTTP, a rate limiter restricts the number of client requests that can be sent over a specified period. If the number of API requests exceeds the threshold defined by the rate limiter, the excess calls are blocked. Here are some examples:

- A user can write no more than 2 posts per second.
- A maximum of 10 accounts can be created per day from the same IP address.
- Rewards can be claimed no more than 5 times per week from the same device.

## Advantages of an API Rate Limiter

### Prevent Resource Starvation
Rate limiters help prevent resource starvation caused by Denial of Service (DoS) attacks. Many large tech companies enforce some form of rate limiting. For example, Twitter limits the number of tweets to 300 per 3 hours, and Google Docs APIs have a default limit of 300 requests per user per 60 seconds for read requests. By blocking excess calls, a rate limiter can prevent DoS attacks, whether intentional or unintentional.

### Reduce Costs
Limiting excess requests reduces the number of servers needed and allocates more resources to high-priority APIs. This is crucial for companies using paid third-party APIs, where each call incurs a cost. For instance, checking credit, making payments, or retrieving health records often involves per-call charges, making it essential to limit the number of calls to reduce costs.

### Prevent Server Overload
To reduce server load, a rate limiter filters out excess requests caused by bots or user misbehavior, ensuring that the servers are not overwhelmed.

## Understanding the Problem and Requirements

### Key Considerations
- **Type of Rate Limiter:** We will focus on a server-side API rate limiter.
- **Throttling Criteria:** The rate limiter should be flexible enough to support different sets of throttle rules, such as IP address or user ID.
- **System Scale:** The system must handle a large number of requests.
- **Distributed Environment:** The rate limiter should work in a distributed environment.
- **Implementation:** It can be a separate service or integrated into the application code.
- **User Notifications:** Users should be informed when their requests are throttled.

## Step 1: Requirements

### Functional and Non-Functional Requirements
#### Functional requirements to Design a Rate Limiter API:
- The API should allow the definition of multiple rate-limiting rules.
- The API should provide the ability to customize the response to clients when rate limits are exceeded.
- The API should allow for the storage and retrieval of rate-limit data.
- The API should be implemented with proper error handling as in when the threshold limit of requests are crossed for a single server or across different combinations, the client should get a proper error message.

#### Non-functional requirements to Design a Rate Limiter API:
- The API should be highly available and scalable. Availability is the main pillar in the case of request fetching APIs.
- The API should be secure and protected against malicious attacks.
- The API should be easy to integrate with existing systems.
- There should be low latency provided by the rate limiter to the system, as performance is one of the key factors in the case of any system.


## Step 2: High-Level Design (HLD)

### Placement of the Rate Limiter
Instead of implementing the rate limiter on the client or server side, we can create a rate limiter middleware that throttles requests to APIs.


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/php6rwjd2k97aqjg26et.png)


### Example Scenario
Assume our API allows 2 requests per second. If a client sends 3 requests within a second, the first two requests are routed to API servers, but the third request is throttled by the middleware and returns an HTTP status code 429, indicating too many requests.


``` 
HTTP 429 "Too Many Requests" — The user has sent too many requests in a certain amount of time. This response status code tells the user their request has been rate limited.
```

### API Gateway
In a microservices architecture, rate limiting is often implemented within a component called an API gateway. This is a fully managed service that supports rate limiting, SSL termination, authentication, IP whitelisting, and servicing static content.

### Implementation Considerations
- **Technology Stack:** Ensure the current technology stack, including the programming language and cache service, supports efficient rate limiting.
- **Rate Limiting Algorithm:** Choose an algorithm that fits business needs. Server-side implementation provides full control, but third-party gateways might have limitations.
- **Microservice Architecture:** If using an API gateway for authentication and other services, adding a rate limiter there can be beneficial.
- **Engineering Resources:** Building a rate-limiting service requires time and resources. If limited, consider using a commercial API gateway.

## High-Level Architecture
Rate limiting algorithms are simple at a high level. A counter keeps track of the number of requests from the same user or IP address. If the counter exceeds the limit, the request is disallowed. 

### Storage of Counters
Using a database is not ideal due to the slowness of disk access. In-memory cache (e.g., Redis) is preferred for its speed and support for time-based expiration. Redis offers commands like INCR (increment the counter) and EXPIRE (set a timeout for the counter).

### Request Flow
1. The client sends a request to the rate-limiting middleware.
2. The middleware fetches the counter from Redis and checks if the limit is reached.
3. If the limit is reached, the request is rejected.
4. If the limit is not reached, the request is sent to API servers, the counter is incremented, and the updated value is saved back to Redis.

## Step 3: Design Deep Dive

### Rate Limiting Rules
Rate limiting rules define the conditions under which requests are throttled. These rules are generally written in configuration files and stored on disk. For example, allowing a maximum of 5 marketing messages per day or restricting logins to 5 times per minute.

### Handling Exceeded Limits
When a request exceeds the rate limit, the API returns an HTTP response code 429. Depending on the use case, rate-limited requests may be enqueued for later processing.

### Rate Limiter Headers
Clients can be informed about rate limits via HTTP response headers:
- **X-Ratelimit-Remaining:** The remaining number of allowed requests.
- **X-Ratelimit-Limit:** The maximum number of requests allowed per time window.
- **X-Ratelimit-Retry-After:** The number of seconds to wait before making another request.

### Detailed Design

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/ghltfsd4k16uzkebbk25.png)

1. Rules are stored on the disk and frequently pulled by workers into the cache.
2. A client request goes to the rate limiter middleware.
3. The middleware loads rules from the cache and fetches counters from Redis.
4. Depending on the counter value:
   - If the request is not rate limited, it is forwarded to API servers.
   - If the request is rate limited, an HTTP 429 error is returned, and the request may be dropped or queued.

---


### Distributed Environment Challenges
#### Race Condition
Race conditions occur when multiple threads read and write shared data concurrently. For example, two requests reading the counter value before either writes it back can lead to an incorrect count. Solutions include using locks or Redis features like Lua scripts and sorted sets.

#### Synchronization
In a distributed environment, synchronization ensures that multiple rate limiter servers have consistent data. Sticky sessions or centralized data stores like Redis can help maintain synchronization.

## Performance Optimization
### Multi-Data Center Setup
To reduce latency for users far from the data center, use edge servers geographically distributed around the world. Cloud service providers like Cloudflare have many edge servers to route traffic to the nearest one.

### Eventual Consistency
Synchronize data with an eventual consistency model to handle large-scale systems efficiently.

---

Rate limiting is a technique used to control the rate at which requests are allowed in a system. Let's dive into the details of each algorithm with examples:

### Token Bucket

**Description:**
- A token bucket algorithm has a bucket that holds a certain number of tokens.
- Tokens are added to the bucket at a fixed rate.
- Each incoming request consumes a token.
- If there are no tokens available, the request is denied.
- This allows bursts of requests up to the bucket size while maintaining a steady request rate over time.

**Example:**
- Imagine a bucket that can hold 10 tokens, and 1 token is added every second.
- If the system receives 10 requests at the beginning, all will be served.
- After the bucket is empty, new tokens will be added at a rate of 1 per second.
- If a request comes in when there are no tokens, it will be denied until a new token is added.

**Pros:**
- Allows bursts of traffic.
- Simple to implement.

**Cons:**
- Potential for bursts to overload the system if the bucket size is too large.

### Leaking Bucket

**Description:**
- This algorithm uses a fixed-size bucket where requests are added.
- Requests leak out of the bucket at a fixed rate.
- If the bucket is full, incoming requests are denied.
- Ensures a steady flow of requests, preventing sudden bursts.

**Example:**
- A bucket with a capacity of 10 requests leaks requests at a rate of 1 per second.
- If 10 requests arrive immediately, they fill the bucket.
- The system processes these requests at a rate of 1 per second.
- Additional incoming requests are denied until space is available.

**Pros:**
- Smoothens traffic flow.
- Prevents system overload from bursts.

**Cons:**
- May deny legitimate burst traffic.

### Fixed Window Counter

**Description:**
- Divides time into fixed windows (e.g., 1 minute).
- Counts the number of requests within each window.
- If the count exceeds a predefined limit, requests are denied until the next window.

**Example:**
- A rate limit of 100 requests per minute.
- If 100 requests arrive in the first minute, further requests are denied until the start of the next minute.
- At the beginning of each minute, the counter resets.

**Pros:**
- Simple and easy to implement.
- Effective for evenly distributed traffic.

**Cons:**
- Boundary problem: spikes at window boundaries can exceed the rate limit.

### Sliding Window Log

**Description:**
- Tracks request timestamps in a log.
- Counts requests within a sliding time window (e.g., 1 minute).
- Provides a more accurate count compared to fixed windows.

**Example:**
- A limit of 100 requests per minute.
- Requests and their timestamps are logged.
- For each incoming request, the system checks the number of requests in the last 60 seconds.
- If the count is within the limit, the request is allowed; otherwise, it is denied.

**Pros:**
- Smooth rate limiting without boundary issues.
- More accurate than fixed windows.

**Cons:**
- Requires more memory to store request logs.
- Potentially higher computational cost.

### Sliding Window Counter

**Description:**
- Combines fixed window counter and sliding window log.
- Divides time into small sub-windows and maintains a count of requests in each sub-window.
- Computes the total count in the current window by summing sub-window counts.

**Example:**
- A limit of 100 requests per minute with sub-windows of 10 seconds.
- Requests are counted in each 10-second sub-window.
- For each incoming request, the system sums the counts of the last 6 sub-windows.
- If the total is within the limit, the request is allowed; otherwise, it is denied.

**Pros:**
- Balances accuracy and simplicity.
- Avoids boundary issues of fixed windows.

**Cons:**
- Requires careful configuration of sub-window size.
- Slightly more complex than fixed window counters.

Each algorithm has its own use cases and trade-offs, and the choice depends on the specific requirements of the system being designed.

---


## Rate Limiter Using Sorted Set in Redis
Certainly! Implementing a rate limiter using a Sorted Set in Redis is a robust method, especially in a distributed network. Here’s an in-depth explanation of how it works, along with an example:

### How Sorted Sets in Redis Work for Rate Limiting

A Sorted Set in Redis is a collection of unique elements, each associated with a score. The elements are sorted by their scores, which can be any floating-point value. This sorting allows for efficient range queries, which is crucial for implementing a rate limiter.

### Key Concepts

1. **Elements**: Represent the client’s requests.
2. **Scores**: Represent the timestamps of the requests.

### Steps to Implement Rate Limiting Using Sorted Sets

1. **Initialize the Sorted Set**: For each client (identified by a unique key like IP address or user ID), maintain a Sorted Set in Redis where the elements are request identifiers (could be a unique request ID or just a count) and the scores are the timestamps of the requests.

2. **Adding a Request**: When a client makes a request, add an entry to the Sorted Set with the current timestamp as the score.

3. **Trimming Old Requests**: Periodically or on each request, remove entries from the Sorted Set that fall outside the allowed time window.

4. **Counting Requests**: Count the number of elements in the Sorted Set to check if it exceeds the allowed limit.

5. **Enforcing Limits**: If the count exceeds the limit, deny the request. If not, process the request and add the new entry to the set.

### Example

Let's say we want to limit a user to 100 requests per hour.

#### Step-by-Step Implementation

```js
const Redis = require('ioredis');
const redis = new Redis();

async function handleRequest(clientId, limit, window) {
  const currentTimestamp = Math.floor(Date.now() / 1000);
  const windowStart = currentTimestamp - window;

  // Define the key for the sorted set
  const key = `rate:${clientId}`;

  // Start a transaction (multi)
  const pipeline = redis.multi();

  // Add the current request
  pipeline.zadd(key, currentTimestamp, currentTimestamp);

  // Remove requests older than the window
  pipeline.zremrangebyscore(key, 0, windowStart);

  // Get the count of requests in the current window
  pipeline.zcount(key, windowStart, currentTimestamp);

  // Execute the pipeline
  const results = await pipeline.exec();

  // results[2][1] contains the count of requests in the current window
  const requestCount = results[2][1];

  if (requestCount > limit) {
    return false;  // Rate limit exceeded
  } else {
    return true;   // Request allowed
  }
}

// Example usage
const clientId = "user123";
const limit = 100;  // Maximum 100 requests
const window = 3600;  // Time window of 1 hour

handleRequest(clientId, limit, window).then(allowed => {
  if (allowed) {
    console.log("Request allowed");
  } else {
    console.log("Rate limit exceeded");
  }
}).catch(error => {
  console.error("Error handling request:", error);
});

```

### Handling Concurrency

In a distributed environment, multiple servers might handle requests for the same client concurrently. Redis handles the atomicity of commands, ensuring that even with concurrent access, the operations (like adding a request or trimming old requests) are performed safely.

### Benefits of Using Sorted Sets in Redis

- **Efficiency**: Sorted sets in Redis are optimized for quick insertion, deletion, and range queries, which are essential for rate limiting.
- **Atomic Operations**: Redis supports atomic operations, ensuring that concurrent modifications are handled safely.
- **Scalability**: Redis is designed to handle high-throughput scenarios, making it suitable for distributed environments.

By leveraging Redis's sorted sets and atomic operations, you can build a robust and scalable rate limiter that effectively controls the rate of client requests in a distributed network.

## Conclusion
Designing an effective rate limiter involves understanding requirements, choosing the right algorithms, and considering performance and synchronization challenges in a distributed environment. Using tools like Redis can simplify implementation and ensure high performance.

---

### Q&A -  Rate Limiter System Design

**Q: How would you handle distributed rate limiting?**
A: By using a common read/write cache (e.g., Redis) for consistency or a different read/write cache setup to improve read performance and handle scalability with coordination for writes.

**Q: What is the difference between a stateful and stateless rate limiter?**
A: A stateful rate limiter maintains state (counters, timestamps) across requests, ensuring accurate limits. A stateless rate limiter does not maintain state, often using algorithms or cryptographic techniques to enforce limits without storing data between requests.

**Q: How will you make the rate limiter distributed in system design?**
A: Implement the rate limiter using a distributed caching system like Redis or Memcached to store counters and timestamps. Ensure all nodes in the system interact with the same distributed cache to maintain consistency.

**Q: What challenges do you foresee if a distributed rate limiter is implemented?**
A: Challenges include ensuring data consistency across nodes, handling cache synchronization, managing race conditions, dealing with network partitions, and preventing single points of failure in the distributed cache.

**Q: How would you handle failure when the rate limiter service goes down?**
A: Implement fallback mechanisms such as default allowing or denying requests, using secondary caches, or employing circuit breakers to handle temporary failures and prevent cascading issues.

**Q: How can Redis be used in rate limiting?**
A: Redis can be used to store rate limit counters and timestamps with operations like INCR, EXPIRE, and LUA scripts to perform atomic updates and checks, ensuring accurate rate limiting across distributed systems.

**Q: How can you inform a user or service about their rate limit status?**
A: By including rate limit status in the HTTP response headers (e.g., X-RateLimit-Limit, X-RateLimit-Remaining, X-RateLimit-Reset) or through custom API responses detailing the current rate limit status.

**Q: What are the issues with using sticky sessions in rate limiting?**
A: Sticky sessions can lead to uneven load distribution, session affinity issues, and complications in scaling out as the rate limiting state is tied to a specific server, reducing the benefits of a distributed system.

**Q: Why is an IP address preferred over any other parameter to limit users?**
A: IP addresses are readily available in network requests and provide a straightforward way to identify and limit clients. However, they can be spoofed or shared by multiple users, leading to potential inaccuracies.

**Q: Why is it preferred to implement rate limiting on the server side rather than the client side?**
A: Server-side implementation ensures control over enforcement, prevents clients from bypassing limits, and centralizes management and monitoring, providing a more secure and reliable rate limiting mechanism.

---

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli