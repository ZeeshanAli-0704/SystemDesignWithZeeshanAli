Part 1: [Pre-requisites : How I Calculate Capacity for Systems Design - 1](https://dev.to/zeeshanali0704/how-i-calculate-capacity-for-systems-design-1-25ob)

### Capacity Estimation Process Example: Twitter-like Application

**1. Traffic Estimation**

- **Monthly Active Users (MAU):** 500 million
- **Daily Active Users (DAU):** 100 million (assuming 1/5 of MAUs are active daily)

**2. Read and Write Requests**

To estimate read and write requests, assume:
- Each active user tweets 2 times per day.
- Each user reads 100 tweets per day (including their feed, replies, and notifications).

**Write Requests:**
- Each tweet is a write request.
- Additional write requests for likes, retweets, and replies. Assume 2 additional write requests per tweet.

**Calculations:**

- **Daily Write Requests:**
  - Daily Write Requests = DAU × (Average Tweets per User + Additional Writes per Tweet)
  - Daily Write Requests = 100 million × (2 + 2) = 400 million requests/day

- **Daily Read Requests:**
  - Daily Read Requests = DAU × (Average Reads per User)
  - Daily Read Requests = 100 million × 100 = 10 billion requests/day

- **Requests per Second (RPS):**
  - There are 86,400 seconds in a day but we will consider 100,000 (100K) for simplifying calculations.
  - Write RPS = Daily Write Requests / 100,000
  - Write RPS = 400 million / 100K ≈ 4,000 writes per second
  - Read RPS = Daily Read Requests / 100,000
  - Read RPS = 10 billion / 100K ≈ 100,000 reads per second

**3. Storage Requirements**

Assume:
- Average size of a tweet: 300 bytes
- Retention period: 5 years (5 × 365 days)
- Additional storage for metadata (likes, retweets, etc.): 3 times the tweet size

**Calculations:**

- **Daily Storage for Tweets:**
  - Daily Storage = Daily Write Requests × Average Tweet Size × 4
  - Daily Storage = 400 million × 300 × 4 ≈  400 Million × 1200Bytes  ~ 480GB/Day ~ 500 GB/day


- **Annual Storage:**
  - Annual Storage = Daily Storage × 365
  - Annual Storage = 500 GB × 365 ≈ 182 TB

**Basic Formula**

If we assume a write request size of x KB and y million users:
` x KB × y Million users = xy GB;`
For example:
If each user writes 100 KB per day and there are 200 million users:
Storage per day = 100 KB × 200 M = 20000 KB X M ~ 20000 GB ~ 20 TB

**4. Bandwidth Requirements**

Assume:
- Average tweet size: 1,200 bytes (including metadata)
- Write RPS ≈ 4,000
- Read RPS ≈ 100,000
- Average read size: 500 bytes

**Calculations:**

- **Write Bandwidth:**
  - Write Bandwidth (Mbps) = Write RPS × Size per Write
  - Write Bandwidth = 4,000 × 1,200 ≈ 4.8 MB/second

- **Read Bandwidth:**
  - Read Bandwidth (Mbps) = Read RPS × Size per Read
  - Read Bandwidth = 100,000 × 500 ≈ 50 MB/second

- **Total Bandwidth:**
  - Total Bandwidth = Write Bandwidth + Read Bandwidth
  - Total Bandwidth ≈ 4.8 + 50 ≈ 54.8 MB/second


**5. RAM / Cache Estimation**

Assume:
- Cache the last 5 posts per user
- Average size of each post: 500 bytes

**Calculations:**

- **Cache Requirement:**
  - 1 post = 500 bytes, so 5 posts = 2,500 bytes ≈ 3 KB
  - Total cache for 100 million daily active users:
  - Total Cache = DAU × Cache Size per User
  - Total Cache = 100 million × 3 KB ≈ 300 GB

- **Number of Machines Required:**
  - Assume each machine has 10 GB cache storage.
  - Number of Machines = Total Cache / Cache per Machine
  - Number of Machines = 300 GB / 10 GB ≈ 30 machines

**6. Latency Estimation**

Assume:
- Latency per request: 500 ms
- 1 second to serve 2 requests
- Write RPS ≈ 4,000
- Assume each server serves 100 requests/second.

**Calculations:**

- **Number of Servers Required:**
  - Number of Servers = Write RPS / Requests per Server
  - Number of Servers = 4,000 / 100 ≈ 40 servers

By following these calculations, you can estimate the capacity needed to handle traffic, storage, bandwidth, cache, and latency for your system.


Part 1: [Pre-requisites : How I Calculate Capacity for Systems Design - 1](https://dev.to/zeeshanali0704/how-i-calculate-capacity-for-systems-design-1-25ob)

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
