# System Design with Zeeshan Ali

Welcome to the **System Design with Zeeshan Ali** repository! This collection is dedicated to in-depth articles and resources on system design, covering foundational concepts, architecture patterns, optimization techniques, and real-world case studies. Each section is carefully curated to provide clear explanations, practical examples, and best practices for building scalable, reliable, and efficient systems.

* **GitHub**: [System Design with Zeeshan Ali](https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli)
* **Dev.to**: [System Design with Zeeshan Ali](https://dev.to/t/systemdesignwithzeeshanali)

---

## Folder Structure

Folders are listed below in a suggested learning order. Where a topic is a multi-part series, the parts are listed in reading order in this README. Diagrams live in a `diagrams/` sub-folder and runnable code lives in a `code/` sub-folder (or at the root of a Low Level Design project).

```
Fundamentals/                 Capacity estimation, scaling, CAP, availability, SPOFs
Networking_and_Web/           DNS, CDN, load balancers, browser/URL flow, cookies, API testing
Databases_and_Storage/        Indexing, partitioning, sharding, replication, storage, blob storage
Caching/                      Cache eviction policies, Redis
Distributed_Systems/          Consistent hashing, Bloom filters, Zookeeper, Kafka
Security/                     HTTPS, password storage
Architecture_Patterns/        Client-server, event-driven, microservices, serverless, pub/sub
Design_Patterns_and_SOLID/    SOLID + GoF design patterns (articles with Java code)
HLD_Case_Studies/             High-level design interviews & real-world architectures
Low_Level_Design/             LLD projects with runnable Java / C++ / JS code
Frontend_System_Design/       Front-end performance, JS memory, virtualized rendering
```

---

### Fundamentals

* **[Capacity Estimation](./Fundamentals/Capacity_Estimation/)**: Techniques to estimate storage, bandwidth, and traffic for scalable systems.
  * [Part 1 - Prerequisites](./Fundamentals/Capacity_Estimation/Capacity_Estimation_Prerequisites.md)
  * [Part 2 - Twitter-like Example](./Fundamentals/Capacity_Estimation/Capacity_Estimation_Twitter_Example.md)
  * [Part 3 - Instagram & Twitter Calculations](./Fundamentals/Capacity_Estimation/Capacity_Calculations_Instagram_and_Twitter.md)
* **[Horizontal vs Vertical Scaling](./Fundamentals/Scaling/Horizontal_vs_Vertical_Scaling.md)**: Trade-offs between adding more servers vs. scaling up existing servers.
* **[Scaling From Zero to Millions (High Level System Design)](./Fundamentals/Scaling_From_Zero_To_Millions/High_Level_System_Design.md)**: Evolving a single server into a system serving millions of users (with diagrams).
* **[CAP Theorem](./Fundamentals/CAP_Theorem/)**: Consistency, Availability, Partition Tolerance with trade-offs in real-world databases.
  * [Overview](./Fundamentals/CAP_Theorem/CAP_Theorem_Overview.md)
  * [Real-World Scenarios and Trade-offs](./Fundamentals/CAP_Theorem/CAP_Theorem_Real_World_Scenarios_and_Tradeoffs.md)
* **[Designing for High Availability](./Fundamentals/High_Availability/Designing_for_High_Availability.md)**: Patterns for ensuring system uptime using redundancy and failover.
* **[Single Point of Failure](./Fundamentals/Single_Point_Of_Failure/)**: Identifying and eliminating SPOFs.
  * [How to Avoid Single Points of Failure](./Fundamentals/Single_Point_Of_Failure/How_to_Avoid_Single_Points_of_Failure.md)
  * [Real-World Case Study: E-commerce](./Fundamentals/Single_Point_Of_Failure/Avoiding_SPOFs_Ecommerce_Case_Study.md)

---

### Networking and Web

* **[DNS - The Domain Name System](./Networking_and_Web/DNS/DNS.md)**: Translating human-readable domain names into IP addresses with caching and security.
* **[CDN (Content Delivery Network)](./Networking_and_Web/CDN/CDN.md)**: How CDNs distribute content geographically to reduce latency and improve performance.
* **[What Happens When You Type a URL](./Networking_and_Web/How_URL_Works_In_Browser/What_Happens_When_You_Type_a_URL.md)**: End-to-end lifecycle of a web request.
* **[Load Balancers](./Networking_and_Web/Load_Balancers/)**: Layer 4 vs Layer 7, types, and algorithms like Round Robin, Least Connections, and Hashing.
  * [Complete Guide (single article)](./Networking_and_Web/Load_Balancers/Load_Balancer_Complete_Guide.md)
  * [Part 1 - Introduction & Fundamentals](./Networking_and_Web/Load_Balancers/Load_Balancer_Introduction_and_Fundamentals.md)
  * [Part 2 - Types of Load Balancers](./Networking_and_Web/Load_Balancers/Types_of_Load_Balancers.md)
  * [Part 3 - Load Balancing Algorithms](./Networking_and_Web/Load_Balancers/Load_Balancing_Algorithms.md)
* **[Cookie Properties](./Networking_and_Web/Cookies/Cookie_Properties_Guide.md)**: Deep dive into cookie attributes (`Expires`, `SameSite`, `Secure`, ...) and cross-origin rules.
* **[Types of API Testing](./Networking_and_Web/API_Testing/Types_of_API_Testing.md)**: API testing strategies like unit, load, functional, and security tests.

---

### Databases and Storage

* **[Database Optimization](./Databases_and_Storage/Database_Optimization/)**: [Indexing](./Databases_and_Storage/Database_Optimization/Indexing.md), [Partitioning](./Databases_and_Storage/Database_Optimization/Partitioning.md), and [Sharding](./Databases_and_Storage/Database_Optimization/Sharding.md).
* **[Database Replication](./Databases_and_Storage/Database_Replication/Database_Replication.md)**: Techniques for ensuring high availability and fault tolerance via replication.
* **[Storage Concepts in System Design](./Databases_and_Storage/Storage_Concepts/Storage_Concepts_in_System_Design.md)**: Overview of file systems, databases, and storage layers in modern apps.
* **[Blob Storage](./Databases_and_Storage/Blob_Storage/Blob_Storage.md)**: Blob storage, use cases, and benefits in cloud systems.

---

### Caching

* **[Cache Eviction Policies](./Caching/Cache_Eviction_Policies/Cache_Eviction_Policies.md)**: Strategies like LRU, LFU, FIFO for managing cache space efficiently.
* **[Redis](./Caching/Redis/Redis_and_its_Role_in_System_Design.md)**: In-memory data store for caching, session management, and pub/sub.

---

### Distributed Systems

* **[Hashing and Consistent Hashing](./Distributed_Systems/Consistent_Hashing/)**: Explanation, examples, diagrams, and a [Java implementation](./Distributed_Systems/Consistent_Hashing/code/).
  * [Hashing Basics](./Distributed_Systems/Consistent_Hashing/Hashing_Basics.md)
  * [Consistent Hashing Explained with an Example](./Distributed_Systems/Consistent_Hashing/Consistent_Hashing_Explained_With_Example.md)
  * [Consistent Hashing in Java](./Distributed_Systems/Consistent_Hashing/Consistent_Hashing_Java_Implementation.md)
* **[Bloom Filters](./Distributed_Systems/Bloom_Filters/)**: Bloom filters, their problems, and Counting Bloom filters, plus a [Java implementation](./Distributed_Systems/Bloom_Filters/code/).
  * [Bloom Filters Deep Dive](./Distributed_Systems/Bloom_Filters/Bloom_Filters.md)
  * [Problems with Bloom Filters](./Distributed_Systems/Bloom_Filters/Problems_With_Bloom_Filters.md)
  * [Counting Bloom Filter](./Distributed_Systems/Bloom_Filters/Counting_Bloom_Filter.md)
* **[Apache Zookeeper](./Distributed_Systems/Apache_Zookeeper/Apache_Zookeeper.md)**: Coordination service for leader election, configuration, and synchronization.
* **[Kafka](./Distributed_Systems/Kafka/Kafka.md)**: Distributed event streaming platform for real-time data pipelines and messaging.

---

### Security

* **[HTTPS: How HTTPS Works](./Security/HTTPS/How_HTTPS_Works.md)**: SSL/TLS handshake and how HTTPS secures communication.
* **[How to Store Passwords in a Database](./Security/Password_Storage/How_To_Store_Passwords_in_Database.md)**: Best practices for secure password storage (hashing, salting, peppering).

---

### Architecture Patterns

* **[Client-Server Architecture](./Architecture_Patterns/Client_Server/Client_Server_Architecture.md)**: Client-server interactions and N-tier architecture.
* **[Event-Driven Architecture](./Architecture_Patterns/Event_Driven/Event_Driven_Architecture.md)**: Reactive systems built around events and consumers.
* **[Microservices Architecture](./Architecture_Patterns/Microservices/Microservices_Architecture.md)**: Service decomposition, pros/cons, and comparison with monoliths ([Monolith vs Microservices diagram](./Architecture_Patterns/Microservices/diagrams/Monolith_vs_Microservices.drawio)).
* **[Serverless Architecture](./Architecture_Patterns/Serverless/Serverless_Architecture.md)**: Cloud functions, pay-per-execution, and auto-scaling benefits.
* **[Pub/Sub Architecture](./Architecture_Patterns/Pub_Sub/Pub_Sub_Architecture.md)**: Publish-subscribe model for decoupled communication.

---

### Design Patterns and SOLID

* **[SOLID Principles](./Design_Patterns_and_SOLID/SOLID/SOLID_Principles.md)**: The five SOLID principles, with a [Java walkthrough](./Design_Patterns_and_SOLID/SOLID/SOLID_Principles_Java.md) and [code](./Design_Patterns_and_SOLID/SOLID/code/).
* **[Design Patterns - Introduction](./Design_Patterns_and_SOLID/Design_Patterns/Design_Patterns_Introduction.md)** · [Architecture Styles Notes](./Design_Patterns_and_SOLID/Design_Patterns/Software_Architecture_Styles_Notes.md) · [Design Patterns in JavaScript](./Design_Patterns_and_SOLID/Design_Patterns/Design_Patterns_in_JavaScript.md)
* **Creational**: [Abstract Factory](./Design_Patterns_and_SOLID/Design_Patterns/Creational/Abstract_Factory/), [Builder](./Design_Patterns_and_SOLID/Design_Patterns/Creational/Builder/), [Factory](./Design_Patterns_and_SOLID/Design_Patterns/Creational/Factory/), [Prototype](./Design_Patterns_and_SOLID/Design_Patterns/Creational/Prototype/), [Singleton](./Design_Patterns_and_SOLID/Design_Patterns/Creational/Singleton/)
* **Structural**: [Adapter](./Design_Patterns_and_SOLID/Design_Patterns/Structural/Adapter/), [Decorator](./Design_Patterns_and_SOLID/Design_Patterns/Structural/Decorator/)
* **Behavioral**: [Iterator](./Design_Patterns_and_SOLID/Design_Patterns/Behavioral/Iterator/), [Observer](./Design_Patterns_and_SOLID/Design_Patterns/Behavioral/Observer/), [Strategy](./Design_Patterns_and_SOLID/Design_Patterns/Behavioral/Strategy/)

---

### HLD Case Studies

* **[Craigslist](./HLD_Case_Studies/Craigslist/Design_Craigslist.md)**: Classified ads system design with posting, browsing, and search.
* **[Instagram](./HLD_Case_Studies/Instagram/Design_Instagram.md)**: Feeds, media storage, and scaling challenges.
* **[Twitter](./HLD_Case_Studies/Twitter/Design_Twitter.md)**: Large-scale social media design with timelines and fan-out strategies.
* **[Key-Value Store](./HLD_Case_Studies/Key_Value_Store/Design_Key_Value_Store.md)**: Data partitioning, replication, and fault tolerance.
* **[Notification / Alerting Service](./HLD_Case_Studies/Notification_Alerting_Service/Design_Notification_Alerting_Service.md)**: Real-time push notifications with retries, queues, and priorities.
* **[PasteBin](./HLD_Case_Studies/PasteBin/Design_PasteBin.md)**: Text sharing service with expiration and storage strategies.
* **[Spotify Top K Songs](./HLD_Case_Studies/Spotify_Top_K_Songs/Design_Spotify_Top_K.md)**: Ranking most-played tracks at scale.
* **[Unique ID Generator](./HLD_Case_Studies/Unique_ID_Generator/Design_Unique_ID_Generator.md)**: UUID, Snowflake IDs, and distributed ID generation techniques.
* **[URL Shortener](./HLD_Case_Studies/URL_Shortener/Design_URL_Shortener.md)**: Designing TinyURL-like systems with hashing and redirection.
* **[Web Crawler](./HLD_Case_Studies/Web_Crawler/Design_Web_Crawler.md)**: Crawling, indexing, and respecting robots.txt at scale.
* **[Rate Limiter](./HLD_Case_Studies/Rate_Limiter/)**: Token bucket, leaky bucket, and sliding window algorithms with Redis.
  * [Overview](./HLD_Case_Studies/Rate_Limiter/Rate_Limiter_Overview.md)
  * [Step 1 - Introduction & Requirements](./HLD_Case_Studies/Rate_Limiter/Introduction_and_Requirements.md)
  * [Step 2 - High Level Design](./HLD_Case_Studies/Rate_Limiter/High_Level_Design.md)
  * [Step 3 - Redis Cluster & Million Request Model](./HLD_Case_Studies/Rate_Limiter/HLD_Redis_Cluster_Million_Requests.md)
  * [Step 4 - Distributed Environment Challenges](./HLD_Case_Studies/Rate_Limiter/Distributed_Environment_Challenges.md)
  * [Step 5 - Algorithms](./HLD_Case_Studies/Rate_Limiter/Rate_Limiting_Algorithms.md)
  * [Step 6 - Sorted Sets in Redis](./HLD_Case_Studies/Rate_Limiter/Sorted_Set_in_Redis.md)
  * [Step 7 - Conclusion](./HLD_Case_Studies/Rate_Limiter/Conclusion.md)
* **[UPI Payment System](./HLD_Case_Studies/UPI_Payment_System/)**: Unified Payments Interface on NPCI rails with transactions and security.
  * [UPI System Design](./HLD_Case_Studies/UPI_Payment_System/UPI_System_Design.md)
  * [UPI Payment System Design (NPCI-style)](./HLD_Case_Studies/UPI_Payment_System/UPI_Payment_System_Design_NPCI.md)
  * [NPCI & UPI - End-to-End Design](./HLD_Case_Studies/UPI_Payment_System/NPCI_and_UPI_End_to_End_Design.md)
* **[Job Scheduler](./HLD_Case_Studies/Job_Scheduler/)**: Distributed job scheduling system using Redis.
  * [Part 1 - Concepts & HLD](./HLD_Case_Studies/Job_Scheduler/Job_Scheduling_Concepts_and_HLD.md)
  * [Part 2 - Implementation](./HLD_Case_Studies/Job_Scheduler/Job_Scheduling_Implementation_With_Redis.md)
* **[QR Code Generator](./HLD_Case_Studies/QR_Code_Generator/Design_QR_Code_Generator.md)**: *(in progress)*
* **Real-World Architectures**
  * [Flipkart - Flash Sales, Real-Time Inventory & Checkout](./HLD_Case_Studies/Real_World_Architectures/Flipkart_Flash_Sales_and_Inventory.md)
  * [Uber - Serving 40M Reads with Integrated Redis Cache](./HLD_Case_Studies/Real_World_Architectures/Uber_Redis_Cache_40M_Reads.md)

---

### Low Level Design (LLD)

* **[ATM Machine](./Low_Level_Design/ATM_Machine/)**: LLD for ATM operations (cash withdrawal, balance inquiry, etc.) using the State pattern.
* **[Food Delivery App](./Low_Level_Design/Food_Delivery_App/)**: Food ordering system in Java with in-memory repos, delivery agent auto-assign, and pending queues.
* **[Parking Lot](./Low_Level_Design/Parking_Lot/)**: Multi-floor parking lot with different spot types, entry/exit gates, and strategies.
* **[Polling System](./Low_Level_Design/Polling_System/)**: Voting system design implemented in Java, C++, and JavaScript.
* **[Ticket Booking System](./Low_Level_Design/Ticket_Booking_System/)**: Movie ticket reservation with seat locking and concurrency handling.
* **[Multithreading Basics](./Low_Level_Design/Multithreading_Basics/)**: Multi-threading and synchronization examples.

> Bloom filter, consistent hashing, and design pattern code now lives next to the matching articles in [Distributed_Systems](./Distributed_Systems/) and [Design_Patterns_and_SOLID](./Design_Patterns_and_SOLID/).

---

### Frontend System Design

* **[Front End Optimization](./Frontend_System_Design/Front_End_Optimization/Front_End_Optimization.md)**: Best practices to improve performance (lazy loading, caching, bundling).
* **[JavaScript Memory Leaks](./Frontend_System_Design/JavaScript_Memory_Management/JavaScript_Memory_Leaks.md)**: JavaScript memory handling, garbage collection, and avoiding leaks.
* **[Google Sheets - Virtualized Rendering, Windowing & Recycling](./Frontend_System_Design/Google_Sheets_Virtualized_Rendering/Google_Sheets_HLD_Virtualized_Rendering_and_Windowing.md)**: Frontend + backend HLD for a spreadsheet with millions of cells.

---
