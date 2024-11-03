I’d like to share another insightful article from ByteByteGo that provides great information and learning opportunities.
How Uber Served 40 Million Reads with Integrated Redis Cache?
There are 3 main parts of the implementation:
1. CacheFront Reads and Writes with Change Data Capture (CDC):
CacheFront is the name of Uber’s integrated caching solution, which combines Redis (for caching), Docstore (document database), and MySQL (relational database).
Instead of Uber’s micro services handling requests directly, Docstore’s query engine communicates with Redis for read requests.
For cache hits: The query engine fetches data directly from Redis, leading to quick responses.
For cache misses: The request is forwarded to the storage engine (Docstore and MySQL), which takes more time but ensures data retrieval.
For writes (data changes), Uber uses a Change Data Capture (CDC) service called Flux. Flux monitors (or tails) MySQL binlog events (logs of all changes in MySQL) and invalidates outdated data in Redis. This keeps Redis cache in sync with the most recent data in MySQL.
2. Multi-Region Cache Warming with Redis Streaming
When a region (geographically separated data center) fails over, the system may experience a flood of cache misses in the backup region, overwhelming the database with requests.
To address this, Uber uses cross-region Redis replication by tailing Redis write streams (a feature to track changes to keys in Redis). This enables cache warming in the remote region by replicating cache data.
The remote region’s consumer (process monitoring the Redis stream) sends read requests to the database to fetch the required data and populate the cache in that region.
This minimizes database overload in the event of failover and ensures data is preloaded into cache, reducing latency.
3. Redis and Docstore Sharding
Uber’s services generate an enormous number of requests, requiring both Redis and Docstore to be sharded (partitioned) across multiple instances to handle the load.
However, if one Redis cluster fails, it could lead to a hot shard in the database, where a single DB shard gets overwhelmed with requests. To avoid this, Uber implemented a different partitioning scheme between Redis and the database.
Asymmetric sharding: By using different sharding schemes for Redis and Docstore, Uber ensures that failure in a single Redis cluster doesn’t cause an overload on any particular database shard. This balances the load more effectively across their infrastructure.
Summary:
Uber’s integrated caching solution involves a combination of Redis for fast data access, Docstore for querying, and MySQL for persistent storage. With cross-region replication and advanced sharding, Uber ensures high availability and performance across multiple regions while avoiding overloads. This system allows them to efficiently serve 40 million reads with minimized latency and failover risks.
Source: ByteByteGo