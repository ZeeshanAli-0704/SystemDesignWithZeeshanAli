
# System Design with Zeeshan Ali

Welcome to the **System Design with Zeeshan Ali** repository! This collection is dedicated to in-depth articles and resources on system design, covering foundational concepts, architecture patterns, optimization techniques, and real-world case studies. Each section is carefully curated to provide clear explanations, practical examples, and best practices for building scalable, reliable, and efficient systems.

* **GitHub**: [System Design with Zeeshan Ali](https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli)
* **Dev.to**: [System Design with Zeeshan Ali](https://dev.to/t/systemdesignwithzeeshanali)

---

## Folder Structure

### System Design Concepts

* **[Apache Zookeeper](./Concepts/Apache_Zookeeper/Apache_Zookeeper.md)**: Coordination service for distributed systems, used for leader election, configuration, and synchronization.
* **[Blob Storage](./Concepts/BlobStorage/)**: Explanation of Blob Storage, use cases, and benefits in cloud systems.
* **[Bloom Filters](./Concepts/BloomFilters/)**: Deep dive into Bloom Filters, Counting Bloom filters, and Cuckoo filters with practical examples.
* **[Cache Eviction Policies](./Concepts/Cache_Eviction_Policies/cache-eviction-policies-system-design.md)**: Strategies like LRU, LFU, FIFO for managing cache space efficiently.
* **[CAP Theorem](./Concepts/CAP_Theorem/)**: Principles of Consistency, Availability, Partition Tolerance with trade-offs in real-world databases.
* **[Capacity Estimations](./Concepts/Capacity_Estimations/Capacity%20Estimation%20Details.md)**: Techniques to estimate storage, bandwidth, and traffic for scalable systems.
* **[CDNs (Content Delivery Networks)](./Network_and_Distributed_Systems/CDN/CDN.md)**: How CDNs distribute content geographically to reduce latency and improve performance.
* **[Client-Server Architecture Pattern](./Architecture_Patterns/Client_Server_Architecture_Pattern//Client%20Server%20Architecture%20Pattern.md)**: Explains client-server interactions and their role in networked applications.
* **[DNS - The Domain Name System](./Network_and_Distributed_Systems/DNS/DNS.md)**: Translating human-readable domain names into IP addresses with caching and security.
* **[Database Replication System Design](./Concepts/Database_Replication_System_Design/Database_Replication_in_System_Design.md)**: Techniques for ensuring high availability and fault tolerance via replication.
* **[Hashing and Consistent Hashing](./Concepts/Hashing_Consistent_Hashing/)**: Includes explanation, examples, and Java implementation for load balancing and distributed storage.
* **[Horizontal vs Vertical Scaling](./Concepts/Horizontal-vs-VerticalScaling/)**: Trade-offs between adding more servers vs. scaling up existing servers.
* **[How to Store Passwords in a Database](./Security_and_Best_Practices/How_To_Store_Password_in_Database/How_To_Store_Password_in_Database.md)**: Best practices for secure password storage (hashing, salting, peppering).
* **[HTTPS: How HTTPS Works - The HTTP Handshake](./Security_and_Best_Practices//Https_How_Https_Works/Https_How_Https_Works.md)**: Explains SSL/TLS handshake and how HTTPS secures communication.
* **[Kafka](./Concepts/Kafka/Kafka.md)**: Distributed event streaming platform for real-time data pipelines and messaging.
* **[Load Balancers](./Concepts/Load_Balancers/Load_Balancer.md)**: Layer 4 vs Layer 7, algorithms like Round Robin, Least Connections, and Hashing.
* **[Memory Management](./Concepts/Memory_Management/Memory_Management.md)**: Covers JavaScript memory handling, garbage collection, and optimization.
* **[Redis](./Concepts/Redis_and_its_role_in_System_Design/Redis_and_its_role_in_System_Design.md)**: In-memory data store for caching, session management, and pub/sub.
* **[Storage Concepts in System Design](./Concepts/Storage_Concepts_in_System_Design/Storage_Concepts_in_System_Design.md)**: Overview of file systems, databases, and storage layers in modern apps.
* **[Types Api Testing](./Concepts/Types_of_Api_Testing/Types_of_Api_Testing.md)**: Explains API testing strategies like unit, load, functional, and security tests.

---

### System Design Case Studies

* **[Design\_Craigslist](./Design_Case_Studies/Design_Craigslist/Design_Craigslist.md)**: Classified ads system design with posting, browsing, and search.
* **[Design High Availability System](./Design_Case_Studies/Design_High_Availability_System/Design_High_Availability_System.md)**: Patterns for ensuring system uptime using redundancy and failover.
* **[Design Instagram System](./Design_Case_Studies/Design_Instagram_System/Design_instagram.md)**: Covers feeds, media storage, and scaling challenges.
* **[Design Key-Value Store](./Design_Case_Studies/Design_Key_Value_Store.md/)**: Covers data partitioning, replication, and fault tolerance.
* **[Design Notification Alerting Service](./Design_Case_Studies/Design_notification_alerting_service/)**: Real-time push notifications with retries, queues, and priorities.
* **[Design PasteBin](./Design_Case_Studies/Design_PasteBin/Design_PasteBin.md)**: Text sharing service with expiration and storage strategies.
* **[Design Spotify Top K](./Design_Case_Studies/Design_Spotify_Top_K/Design_Spotify_Top_K.md)**: Ranking and recommendation systems for most-played tracks.
* **[Design Twitter](./Design_Case_Studies/Design_Twitter/Design_twitter.md)**: Large-scale social media design with timelines and fan-out strategies.
* **[Design Unique ID Generation](./Design_Case_Studies/Design_Unique_ID_Generation/Design_Unique_ID_Generation.md)**: UUID, Snowflake IDs, and distributed ID generation techniques.
* **[Design URL Shortener](./Design_Case_Studies/Design_URL_Shortening/URL_Shortening.md)**: Designing TinyURL-like systems with hashing and redirection.
* **[Design Web Crawler](./Design_Case_Studies/Designing_Web_Crawler/Designing_a_Web_Crawler.md)**: Crawling, indexing, and respecting robots.txt at scale.
* **[Polling System](./Design_Case_Studies/PollingSystem/)**: Vote collection, real-time tallying, and fraud prevention strategies.
* **[Rate Limiter](./Design_Case_Studies/Rate_Limiter/Rate_Limiter.md)**: Token bucket, leaky bucket, and sliding window algorithms.
* **[URL Working in Browser](./Network_and_Distributed_Systems/URL_Working_In_Browser/When%20you%20type%20a%20URL%20into%20a%20browser%20and%20press%20Enter.md)**: End-to-end lifecycle of a web request.

---

### Architecture Patterns

* **[Client Server Architecture Pattern](./Architecture_Patterns/Client_Server_Architecture_Pattern/Client%20Server%20Architecture%20Pattern.md)**: Basic 2-tier pattern for request/response systems.
* **[Design Pattern Example](./Architecture_Patterns/Design_Pattern_Example/Mastering_Design_Patterns_JavaScript.md)**: Classic design patterns with JS examples (Factory, Builder, Observer, etc.).
* **[Event-Driven Architecture Pattern](./Architecture_Patterns/Event_Driven_Architecture_Pattern/Event_Driven_Architecture_Pattern.md)**: Reactive systems built around events and consumers.
* **[Microservice Architecture](./Architecture_Patterns/Micro_Service_Architecture/Micro_Service.md)**: Service decomposition, pros/cons, and comparison with monoliths. Comparison of **Monolith vs Microservices** (./Architecture\_Patterns/Micro\_Service\_Architecture/Mono-vs-Micro)
* **[Serverless Architecture Pattern](./Architecture_Patterns/Serverless_Architecture_Pattern/Serverless_Architecture_Pattern.md)**: Cloud functions, pay-per-execution, and auto-scaling benefits.
* **[What is Pub/Sub Architecture](./Architecture_Patterns/What_is_Pub_Sub_Architecture/What_is_Pub_Sub_Architecture.md)**: Publish-subscribe model for decoupled communication.

---

### Low Level Design (LLD)

* **[ATM Machine](./LowLevelDesign/ATMMachine/)**: LLD for ATM operations (cash withdrawal, balance inquiry, etc.).
* **[Bloom Filters Implementation](./LowLevelDesign/BloomFilters/)**: Java/JS implementations of Bloom, Counting Bloom, and Cuckoo filters.
* **[Consistent Hashing Implementation](./LowLevelDesign/ConsistentHashingImpl/)**: Practical code example of consistent hashing in Java.
* **[Design Patterns](./LowLevelDesign/DesignPatterns/)**: LLD examples of popular design patterns.
* **[Parking Lot System](./LowLevelDesign/ParkingLotLowLevelDesign/)**: Multi-floor parking lot with different spot types, entry/exit gates, and strategies.
* **[Polling System](./LowLevelDesign/PollingSystem/)**: CRUD-based voting system design.
* **[Threading Examples](./LowLevelDesign/Thread/)**: Multi-threading and synchronization examples.
* **[Ticket Booking System](./LowLevelDesign/TicketBookingSystem/)**: Reservation, availability check, and concurrency handling.
* **[UPI System](./LowLevelDesign/UPI/)**: Unified Payment Interface system design with transactions and security.

---

### Optimization and Security

* **[Front End Optimization](./Optimization/Front_End_Optimization/Front_End_Optimization.md)**: Best practices to improve performance (lazy loading, caching, bundling).

---
