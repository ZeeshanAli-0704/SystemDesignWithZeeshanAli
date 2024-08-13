**Designing a URL Shortening Service like TinyURL**

**Introduction:**

URL shortening is a technique used to create shorter aliases for long URLs, making them easier to share, manage, and analyze. This service, often seen in tools like TinyURL, converts lengthy web addresses into compact links, enabling more efficient sharing across various platforms. Shortened URLs are particularly useful for social media, printed materials, and mobile messaging, where character count and readability are crucial.

**Why Do We Need URL Shortening?**

1. **Space Efficiency:** Short URLs are easier to display, print, and share, especially in contexts with character limits, such as Twitter or SMS messages.

2. **User Experience:** Shorter URLs are less prone to typing errors and are easier for users to remember and input.

3. **Analytics and Tracking:** URL shortening services often provide insights into link performance, such as click rates and geographical data.

4. **Branding and Customization:** Companies can use shortened URLs to maintain brand identity and allow users to customize links for easier recall and sharing.

If you're unfamiliar with services like TinyURL, try creating a shortened URL to explore its features.

### **System Requirements**

#### **Functional Requirements**

1. **Short Link Generation:** The service should generate a unique short link for each provided URL.
2. **Redirection:** When a short link is accessed, it should redirect the user to the original long URL.
3. **Custom Alias:** Users should have the option to choose a custom alias for their shortened URLs.
4. **Link Expiration:** Links should have a default expiration time, with an option for users to set a custom expiration period.

#### **Non-Functional Requirements**

1. **High Availability:** The service must be highly available, ensuring that URL redirections are always functional.
2. **Low Latency:** Redirection should occur in real-time with minimal delays.
3. **Security:** Shortened links should be unpredictable and secure from guessing.

#### **Extended Requirements**

1. **Analytics:** The system should provide detailed analytics, such as the number of redirections per URL.
2. **API Access:** The service should offer REST APIs for third-party integration.

### **Capacity Estimation and Constraints**

#### **Traffic Estimation**

The system is expected to be read-heavy, with significantly more redirections than new URL shortenings. Let's assume a 100:1 ratio between reads (redirections) and writes (new shortenings).

- **New URL Shortenings:** 500 million per month.
- **Read/Write Ratio:** 100:1
- **Redirections per Month:** 50 billion (100 * 500M)

**Queries Per Second (QPS):**
- **New URLs Per Second:** 500M / (30 * 24 * 3600) ≈ 200 URLs/s
- **Redirections Per Second:** 100 * 200 URLs/s = 20K/s

#### **Storage Estimation**

- **URL Storage:** Assuming 500 million new URLs per month, and storing each for 5 years:
  - **Total URLs:** 500M * 12 months * 5 years = 30 billion URLs
  - **Storage Requirement:** With each URL requiring ~500 bytes, the total storage needed is:
    - 30B URLs * 500 bytes = 15 TB

**Bandwidth Estimation:**
- **Write Requests:** 200 URLs/s * 500 bytes = 100 KB/s incoming data.
- **Read Requests:** 20K URLs/s * 500 bytes = 10 MB/s outgoing data.

#### **Memory Estimation**

To optimize performance, caching frequently accessed URLs is crucial. Assuming the 80-20 rule (80% of traffic comes from 20% of URLs):
- **Total Daily Requests:** 20K requests/s * 3600 s/h * 24 h = 1.7 billion requests/day
- **Cache Memory Requirement:** Caching 20% of daily traffic requires:
  - 0.2 * 1.7B * 500 bytes ≈ 170 GB of memory

### **System APIs**

The service will provide APIs for creating and managing URLs:

1. **Create URL API:**
   - **Endpoint:** `createURL(api_dev_key, original_url, custom_alias, user_name, expire_date)`
   - **Parameters:**
     - `api_dev_key`: API key for user authentication.
     - `original_url`: The URL to be shortened.
     - `custom_alias`: Optional custom alias.
     - `user_name`: Optional user identifier.
     - `expire_date`: Optional expiration date.
   - **Returns:** Shortened URL or error code.

2. **Delete URL API:**
   - **Endpoint:** `deleteURL(api_dev_key, url_key)`
   - **Parameters:**
     - `api_dev_key`: API key for user authentication.
     - `url_key`: The shortened URL key to be deleted.
   - **Returns:** Success or error code.

### **Abuse Prevention**

To prevent malicious use, the service should implement rate limiting per API key, restricting the number of URL creations and redirections allowed per time period. This can be achieved through a combination of throttling mechanisms and monitoring.

### **Database Design**

#### **Data Storage:**
- **Table 1:** Stores URL mappings with fields for original URL, shortened key, custom alias, and expiration time.
- **Table 2:** Stores user information associated with each URL.

![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/pjob90kdhcu66sl4x4s3.png)

Given the large scale and need for quick lookups, a NoSQL key-value store like DynamoDB, Cassandra, or Riak is ideal for this system.

### **System Design and Algorithms for Key Generation in URL Shortening Service**

When designing a URL shortening service, it's crucial to implement a reliable and efficient key generation strategy. The goal is to generate unique, short keys that map long URLs to their shortened counterparts. Here are various techniques that can be used:

---

#### **Technique 1: MD5 Hashing with Truncation**
- **Description**: MD5 hashing provides a 128-bit hash output, which is then truncated to create a shorter key. For example, the first 43 bits can be used to generate a 7-character Base62-encoded key.
- **Example**: Hash the URL `https://example.com/long-url` to get `5d41402abc4b2a76b9719d911017c592`, and then truncate it to `5d4140`. This truncated hash is then encoded in Base62 to create a short URL.
- **Issues**: Truncating the hash increases the likelihood of collisions, which requires careful management and collision resolution.
- **Advantages**: The hashing process is straightforward and provides a deterministic way to generate keys.
- **Disadvantages**: Collisions are a significant concern, and the need to truncate the hash increases this risk.

---




#### **Technique 2: Hash-Based Key Generation**
- **Description**: This approach involves creating a hash of the original URL using cryptographic hash functions like MD5 or SHA256. The hash is then encoded using a scheme like Base64 to generate a short key.
- **Example**: Suppose you want to shorten `https://example.com/long-url`. Hashing this URL with MD5 might produce `5d41402abc4b2a76b9719d911017c592`. To create a short key, you can take the first few characters (e.g., `5d4140`) and encode it using Base62 or Base64.
- **Collision Handling**: Since different URLs could theoretically produce the same hash (collision), it’s essential to check whether the key already exists in the database. If a collision is detected, the hash can be modified (e.g., appending random characters) and rechecked.
- **Advantages**: Hash-based methods are simple and can be implemented quickly. They also provide a uniform distribution of keys.
- **Disadvantages**: Handling collisions can be challenging, especially at scale. Additionally, the length of the hash might need truncation, which increases the risk of collisions.

---


#### **Technique 3: Random Number Generator**
- **Description**: A random number is generated and then encoded (e.g., using Base64) to create a short URL. This random key is checked against the database to ensure it hasn’t been used before.
- **Example**: A randomly generated number like `1234567` can be encoded to produce a key like `1J7G6L`. The system then checks if this key already exists in the database before assigning it to a URL.
- **Issues**: The main concern is the possibility of generating duplicate keys, especially under high concurrency, leading to potential collisions.
- **Advantages**: This method is simple and fast.
- **Disadvantages**: The risk of collision is high, particularly as the number of generated keys increases. This requires frequent checks and potentially multiple key generations to avoid duplication.

---

#### **Technique 4: Counter-Based Key Generation**
- **Description**: A counter is incremented for each new URL, and the counter value is encoded to create the short key. This approach ensures that each key is unique and in sequential order.
- **Example**: If the counter is currently at `1000001`, the system might encode this value using Base62 to get a key like `B2Fb6`.
- **Issues**: As the counter increases, the length of the encoded key might grow, leading to longer URLs. Additionally, if using multiple counters (e.g., for distributed systems), synchronization issues could arise.
- **Advantages**: It’s easy to implement and guarantees uniqueness without collisions.
- **Disadvantages**: The counter might grow large over time, resulting in longer keys. In distributed systems, managing and synchronizing multiple counters adds complexity.

---

#### **Technique 5: Zookeeper Coordination**
- **Description**: Zookeeper is used to manage and coordinate counter values among multiple servers, ensuring that each server generates unique keys without overlap.
- **Implementation**: Each server is assigned a range of counter values (e.g., Server A handles `1000000-2000000`, Server B handles `2000001-3000000`). Zookeeper ensures that no two servers use the same range simultaneously.
- **Example**: Server A might generate keys from its range and encode them to produce keys like `C1Xb2Y`. If Server B attempts to generate a key, it will do so from a different range, preventing duplication.
- **Advantages**: This method is highly reliable and scales well across distributed systems.
- **Disadvantages**: Implementing and maintaining a Zookeeper-based solution adds complexity, especially in large-scale systems.

---


#### **Technique 6: Key Generation Service (KGS)**
- **Description**: A separate service pre-generates a large pool of unique keys and stores them in a database (key-DB). When a URL needs to be shortened, the service assigns one of these pre-generated keys.

- **Key Storage:** For current example with 68.7 billion unique 6-character keys, storing them requires approximately 412 GB.

- **Example**: The KGS might generate and store keys like `A1B2C3`, `D4E5F6`, and `G7H8I9`. When a URL shortening request is made, the system fetches an unused key from this pool, assigns it to the URL, and marks it as used.
- **Concurrency Handling**: Since multiple servers may access the key-DB simultaneously, it's crucial to ensure that a key is marked as used immediately upon retrieval to prevent duplication. This can be managed using transactions or locking mechanisms.
- **Advantages**: This approach eliminates the need to compute a key on the fly and ensures no collisions since keys are pre-generated and checked for uniqueness.
- **Disadvantages**: It requires substantial storage, especially for a large number of keys, and the complexity of managing the key pool increases with scale.

---


### Best Option: 

#### Key Generation Service (KGS)

For most large-scale and performance-critical applications, Key Generation Service (KGS) is the best option. It provides fast key retrieval with guaranteed uniqueness, making it highly suitable for high-traffic services where scalability is crucial. The additional complexity in managing pre-generated keys is offset by the benefits of avoiding real-time collision checks and ensuring performance consistency.

Zookeeper Coordination is another strong candidate, particularly if you're dealing with a highly distributed system, but it adds more operational complexity compared to KGS.

For smaller or simpler systems, where storage and complexity are bigger concerns, Counter-Based Key Generation or Hash-Based Key Generation might be more appropriate due to their simplicity.


Each technique for key generation in a URL shortening service has its strengths and weaknesses. The choice of technique depends on the specific requirements of the system, such as the expected traffic volume, tolerance for collisions, and the complexity of implementation. For large-scale systems with high concurrency, techniques like the Key Generation Service (KGS) or Zookeeper Coordination might be more suitable, while simpler systems could benefit from methods like counter-based or hash-based key generation.

### **Data Partitioning and Replication**

To scale the database:
- **Range-Based Partitioning:** Store URLs based on the first letter of the key. However, this can lead to unbalanced partitions.
- **Hash-Based Partitioning:** Distribute URLs based on the hash of the key, ensuring even distribution across partitions.

### **Caching**

To reduce database load, frequently accessed URLs should be cached:
- **Cache Size:** Start with 20% of daily traffic (170 GB), adjustable based on usage patterns.
- **Eviction Policy:** Implement a Least Recently Used (LRU) policy to manage cache entries.
- **Replication:** Use multiple cache replicas to balance load and increase availability.

### **Load Balancing**

Load balancers should be implemented at three levels:
1. **Between Clients and Application Servers:** Distribute incoming requests evenly.
2. **Between Application Servers and Database Servers:** Manage load between database partitions.
3. **Between Application Servers and Cache Servers:** Distribute cache queries.

### **Purging and Database Cleanup**

Expired links should be removed from the system to free up space:
- **Lazy Cleanup:** Remove links only when accessed or periodically during low traffic.
- **Default Expiration:** Set a default expiration time (e.g., 2 years) for each link.
- **Reusing Keys:** After deleting an expired link, return the key to the key pool for reuse.

### **Security and Permissions**

To manage access to URLs:
- **Private URLs:** Allow users to create private URLs, accessible only to specific users.
- **Permission Management:** Store access permissions in a separate table, keyed by the shortened URL.

### **Common Problems and Solutions**

1. **Custom URL Collisions:** Implement checks to ensure that custom URLs are unique.
2. **Unexpected Load:** Scale the system dynamically to handle increased traffic.
3. **Storage Management:** Regularly clean up expired and unused URLs to manage database size.

This detailed system design outlines the components and considerations necessary for building a scalable, reliable, and secure URL shortening service like TinyURL.

---



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli