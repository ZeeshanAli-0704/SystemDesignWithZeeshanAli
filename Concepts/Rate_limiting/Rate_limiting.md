Rate limiting is a critical aspect of system design to ensure fair usage, prevent abuse, and maintain system stability. It controls how many requests a user or a client can make in a given timeframe. The three most common rate-limiting strategies are **Global**, **Tumbling Window**, and **Sliding Window**.

Below, I will explain each with its use cases, advantages, disadvantages, and examples.

---

### **1. Global Rate Limiting**
- **Definition**: Limits the total number of requests across all users or clients globally within a given time window.
- **Key Characteristics**: 
  - Applies to the entire system, not individual users.
  - Focuses on protecting the backend infrastructure from overload.

#### **Example**:
If the global rate limit is set to **10,000 requests per second**, it means that the system will reject any requests after the 10,000th request in that second.

#### **Use Case**:
- APIs or services that experience high traffic and need to prevent total overload.
- Protecting expensive or rate-sensitive backend operations, such as database queries or third-party API calls.

#### **Advantages**:
- Simple to implement.
- Prevents system-wide resource exhaustion.

#### **Disadvantages**:
- Does not differentiate between clients. A single client can exhaust the entire limit, affecting others.

---

### **2. Tumbling Window Rate Limiting**
- **Definition**: Divides time into fixed, non-overlapping intervals (windows) and counts the requests within each window.
- **Key Characteristics**:
  - Resets the counter at the start of each new window.
  - Requests are limited to a specific count within each time window.

#### **Example**:
A tumbling window with a **1-minute duration** allows **100 requests per minute** per user. The count resets at the start of the next minute.

#### **Use Case**:
- Scenarios where strict boundaries for request limits are acceptable.
- Ideal for simpler rate-limiting requirements.

#### **Advantages**:
- Easy to implement and understand.
- Ensures fairness within each time window.

#### **Disadvantages**:
- Can lead to a "burst" problem: Users can send requests at the end of one window and the beginning of the next window, effectively doubling the allowed rate momentarily.

---

### **3. Sliding Window Rate Limiting**
- **Definition**: Keeps a rolling record of requests and checks how many have been made in the last fixed duration from the current time.
- **Key Characteristics**:
  - Offers a more accurate and consistent rate-limiting mechanism.
  - Avoids the "burst" problem of tumbling windows.

#### **Example**:
If the sliding window is **1 minute** and the limit is **100 requests per minute**, the system will count requests over the past 60 seconds from the current timestamp, irrespective of window boundaries.

#### **Use Case**:
- APIs requiring smoother and more consistent rate limiting.
- Preventing abuse without allowing short-term bursts.

#### **Advantages**:
- Prevents bursty traffic patterns.
- Provides better fairness across users.

#### **Disadvantages**:
- More complex to implement than tumbling windows.
- Requires maintaining a time-based log of requests (e.g., timestamps in a queue or array).

---

### **How to Implement Rate Limiting**

#### **Data Structures**
- **Fixed Counter (Global and Tumbling)**:
  Use a simple counter variable for requests within a time window.
- **Sliding Window (Efficient)**:
  Use a **Deque (Double-Ended Queue)** or a **Priority Queue** to store timestamps of requests.

---

#### **Implementation Examples**

Here are implementations of **Global**, **Tumbling Window**, and **Sliding Window** rate-limiting strategies in **JavaScript**.

---

### **1. Global Rate Limiting (Tumbling Window)**
This implementation tracks the total number of requests across the system within a fixed time window.

```javascript
class GlobalRateLimiter {
  constructor(limit, windowSize) {
    this.limit = limit; // Max requests allowed
    this.windowSize = windowSize * 1000; // Window size in milliseconds
    this.startTime = Date.now();
    this.requestCount = 0;
  }

  allowRequest() {
    const currentTime = Date.now();

    // Reset the counter if the window has passed
    if (currentTime - this.startTime >= this.windowSize) {
      this.startTime = currentTime;
      this.requestCount = 0;
    }

    // Check if within limit
    if (this.requestCount < this.limit) {
      this.requestCount++;
      return true; // Request allowed
    }
    return false; // Request denied
  }
}

// Example usage
const globalLimiter = new GlobalRateLimiter(100, 10); // 100 requests per 10 seconds

setInterval(() => {
  if (globalLimiter.allowRequest()) {
    console.log("Request allowed");
  } else {
    console.log("Rate limit exceeded");
  }
}, 100); // Simulate a request every 100ms
```

---

### **2. Tumbling Window Rate Limiting (Per User)**
This implementation uses a map to track the request count for each user within their tumbling window.

```javascript
class TumblingWindowRateLimiter {
  constructor(limit, windowSize) {
    this.limit = limit; // Max requests per user
    this.windowSize = windowSize * 1000; // Window size in milliseconds
    this.userRequests = new Map(); // Store user request data
  }

  allowRequest(userId) {
    const currentTime = Date.now();

    if (!this.userRequests.has(userId)) {
      this.userRequests.set(userId, { count: 1, startTime: currentTime });
      return true;
    }

    const userData = this.userRequests.get(userId);

    // Reset the user's window if the time has passed
    if (currentTime - userData.startTime >= this.windowSize) {
      this.userRequests.set(userId, { count: 1, startTime: currentTime });
      return true;
    }

    // Check if the user is within their limit
    if (userData.count < this.limit) {
      userData.count++;
      return true;
    }

    return false; // User has exceeded their limit
  }
}

// Example usage
const userLimiter = new TumblingWindowRateLimiter(5, 10); // 5 requests per 10 seconds per user

for (let i = 0; i < 7; i++) {
  const result = userLimiter.allowRequest("user1");
  console.log(`Request ${i + 1}:`, result ? "Allowed" : "Blocked");
}
```

---

### **3. Sliding Window Rate Limiting (Per User)**
This implementation uses a `Map` and a queue (array) to store timestamps for each user's requests.

```javascript
class SlidingWindowRateLimiter {
  constructor(limit, windowSize) {
    this.limit = limit; // Max requests per user
    this.windowSize = windowSize * 1000; // Window size in milliseconds
    this.userRequests = new Map(); // Store user requests as timestamps
  }

  allowRequest(userId) {
    const currentTime = Date.now();

    if (!this.userRequests.has(userId)) {
      this.userRequests.set(userId, []);
    }

    const requestQueue = this.userRequests.get(userId);

    // Remove timestamps outside the window
    while (requestQueue.length > 0 && requestQueue[0] <= currentTime - this.windowSize) {
      requestQueue.shift();
    }

    // Check if the user is within their limit
    if (requestQueue.length < this.limit) {
      requestQueue.push(currentTime);
      return true; // Request allowed
    }

    return false; // Request denied
  }
}

// Example usage
const slidingLimiter = new SlidingWindowRateLimiter(5, 10); // 5 requests per 10 seconds per user

setInterval(() => {
  const result = slidingLimiter.allowRequest("user1");
  console.log("Request:", result ? "Allowed" : "Blocked");
}, 1500); // Simulate a request every 1.5 seconds
```

---

### **Comparison of the Implementations**
- **Global Rate Limiter**: Simple but applies to all users collectively.

- **Tumbling Window Rate Limiter**: Tracks each user independently with strict boundaries.

- **Sliding Window Rate Limiter**: Smooth and accurate for high-traffic systems, but requires maintaining timestamps.

---


### **Comparison Table**

| **Strategy**      | **Granularity**        | **Accuracy**    | **Use Case**                          | **Complexity**       |
|--------------------|------------------------|-----------------|---------------------------------------|----------------------|
| Global             | System-wide           | Low             | Protect infrastructure from overload  | Low                 |
| Tumbling Window    | Per time window       | Moderate        | Enforcing strict limits per window    | Low                 |
| Sliding Window     | Rolling time interval | High            | Smoother and consistent rate limiting | Moderate to High    |

---


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli