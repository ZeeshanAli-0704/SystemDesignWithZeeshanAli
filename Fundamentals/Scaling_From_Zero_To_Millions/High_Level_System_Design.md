# High-Level System Design:

Creating a system capable of supporting millions of users involves a complex, iterative process that requires ongoing refinement and improvement. In this article we will discuss about all key components of a system. By the end, you will have a foundational understanding of system design and the various components involved.

Let's begin with:

### Server Setup - Just simple server

We start with a straightforward setup where all components operate on a single server. The diagram below shows a single server configuration, where the web application, database, cache, and other elements are all hosted on one server, consider we have everything at server side machine


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/r7lo6jjnr3zytble4mll.jpg)


Points as mentioned below:

1. Users access websites via domain names like yoursite.com, then the Domain Name System (DNS) service offered by third-party providers will called first to get IP address.

2. An Internet Protocol (IP) address is provided to the browser. In this instance, the IP address 10.123.23.214 is returned.

3. After obtaining the IP address, Hypertext Transfer Protocol (HTTP) requests are sent directly to your web server.

4. The web server then returns HTML / Css / javascript pages or JSON responses for rendering.


A web application uses server-side languages like Node / Python / Java for business logic and data storage may be any SQL or No-SQL, along with client-side languages such as HTML and JavaScript for presentation. 

In contrast, a mobile application communicates with the web server using the HTTP protocol and typically employs JSON as the API response format due to its simplicity in data transfer.

Example of JSON:

```
{
  "userId": 1,
  "id": 1,
  "title": "delectus aut autem",
  "completed": false
}

```

### Database

As the user base grows, a single server becomes insufficient, necessitating the use of multiple servers: one to handle web/mobile traffic and another for the database. This separate web /mobile traffic and database servers allows each to be scaled independently.
By doing this way be will be able to divide traffic effectivly & can handle more traffic


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/eqlm3u29c8843vc5uzoz.jpg)



**Vertical Scaling vs. Horizontal Scaling**

- **Vertical Scaling (Scale-Up):** Involves adding more resources (CPU, RAM) to a single server. While simpler, it has limitations:
  - A single server can only be upgraded to a certain extent.
  - Lack of failover and redundancy; if the server fails, the entire application goes down.

- **Horizontal Scaling (Scale-Out):** Involves adding more servers to handle increased load, making it more suitable for large-scale applications due to the inherent limitations of vertical scaling.

In a simple design, users connect directly to the web server, which poses risks:
- If the web server goes offline, users cannot access the site.
- High traffic can overwhelm the server, causing slow responses or connection failures.

A load balancer is an effective solution to manage these issues, distributing incoming traffic across multiple servers to ensure reliability and performance.


### Choosing Between Relational and Non-Relational Databases

When selecting a database for your application, you have two primary options: traditional relational databases (SQL) and non-relational databases (NoSQL). Let's explore their differences to help you make an informed decision.

#### Relational Databases (SQL)

**Characteristics:**
- **Structured Data:** Relational databases are ideal for structured data with predefined schemas. Data is organized into tables with rows and columns.
- **ACID Compliance:** They support ACID (Atomicity, Consistency, Isolation, Durability) transactions, ensuring reliable and consistent transactions.
- **SQL Language:** Data is queried and manipulated using SQL (Structured Query Language), which is powerful for complex queries and joins.
- **Strong Data Integrity:** Enforce data integrity through constraints, foreign keys, and transactions.
- **Vertical Scalability:** Typically scaled by upgrading the hardware (CPU, RAM, storage) of the existing server.

**Use Cases:**
- Financial systems
- Inventory management
- Customer Relationship Management (CRM) systems
- Applications requiring complex queries and transactions

**Popular Relational Databases:**
- MySQL
- PostgreSQL
- Microsoft SQL Server
- Oracle Database

#### Non-Relational Databases (NoSQL)

**Characteristics:**
- **Flexible Schemas:** NoSQL databases are designed for unstructured or semi-structured data and allow for flexible schemas.
- **Scalability:** They excel in horizontal scalability, making it easier to distribute data across multiple servers.
- **Variety of Data Models:** NoSQL databases come in various types, including document stores, key-value stores, wide-column stores, and graph databases.
- **Eventual Consistency:** Often provide eventual consistency rather than strong consistency, which can improve performance in distributed systems.
- **High Performance:** Optimized for high performance and large-scale data storage.

**Use Cases:**
- Real-time analytics
- Content management systems
- Internet of Things (IoT) applications
- Social networks
- Big data applications

**Popular Non-Relational Databases:**
- MongoDB (Document Store)
- Cassandra (Wide-Column Store)
- Redis (Key-Value Store)
- Neo4j (Graph Database)
- Amazon DynamoDB (Key-Value Store)

### Making the Decision

**Consider the following factors when choosing a database:**

1. **Data Structure:** If your data is highly structured and requires complex relationships and transactions, a relational database is likely the best choice. If your data is unstructured or semi-structured and you need flexibility, consider a NoSQL database.
   
2. **Scalability Requirements:** For applications that require horizontal scalability and need to handle large volumes of data and high throughput, NoSQL databases are typically more suitable. Relational databases can also scale, but they usually require more complex setups for horizontal scaling.

3. **Consistency vs. Performance:** Relational databases provide strong consistency, which is essential for applications where data accuracy and integrity are critical. NoSQL databases often offer eventual consistency, which can enhance performance and availability in distributed systems.

4. **Query Complexity:** If your application needs complex querying capabilities, including joins and aggregations, relational databases are well-suited for these tasks. NoSQL databases can perform these operations but may require additional effort to implement and optimize.

5. **Development Speed:** NoSQL databases allow for rapid development and iteration due to their flexible schemas. This can be advantageous in agile development environments or when dealing with evolving data models.

### Hybrid Approaches

In some cases, a hybrid approach can be beneficial, where both relational and non-relational databases are used in the same application to leverage the strengths of each type. For example, you might use a relational database for transactional data and a NoSQL database for storing large volumes of unstructured data or for real-time analytics.

By understanding the differences between relational and non-relational databases and considering your application's specific needs, you can make a well-informed decision that best supports your data storage and retrieval requirements.	

### Load Balancer


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/rcobu84botd34vd0m088.jpg)

A load balancer is a crucial component in a distributed system, ensuring that incoming traffic is distributed evenly across multiple web servers. This setup enhances performance, scalability, and availability.

#### How It Works

1. **Traffic Distribution:**
   - Users connect to the load balancer's public IP.
   - The load balancer forwards requests to web servers using private IPs, which are not accessible over the internet, enhancing security.

2. **Failover Protection:**
   - If a web server (e.g., Server 1) goes offline, the load balancer reroutes traffic to another server (e.g., Server 2), ensuring the website remains operational.
   - New healthy web servers can be added to the pool to distribute the load effectively.

3. **Scalability:**
   - As web traffic increases, the load balancer can distribute requests to additional servers, preventing any single server from becoming a bottleneck.

#### Benefits

- **Enhanced Security:** By using private IPs for inter-server communication, the system becomes more secure, preventing direct access to the web servers.
- **Improved Availability:** The system remains operational even if one server fails, as the load balancer can reroute traffic to other available servers.
- **Scalability:** The system can handle increased traffic by adding more web servers to the pool, which the load balancer can distribute traffic to efficiently.

#### Next Steps

While the load balancer improves the web tier, the data tier with a single database still lacks redundancy and failover mechanisms. To address these issues, database replication is essential for ensuring data reliability and availability.


### Database Replication


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/i0gxgnsjmuf25829gsve.jpg)

Database replication is a fundamental feature in database management systems, facilitating high availability and data redundancy through a master-slave relationship. Here's an overview of how it works and its benefits:

#### Overview

Database replication typically involves a master database that handles write operations and multiple slave databases that replicate data from the master and handle read operations. This architecture ensures that data-modifying commands such as insert, delete, or update are directed to the master database. Given that most applications require more read operations than writes, this setup optimizes performance and reliability.

#### Advantages of Database Replication

- **Improved Performance:** By segregating write operations to the master database and distributing read operations across slave databases, the system can handle more concurrent queries, thereby enhancing overall performance.

- **Reliability:** Replicating data across multiple locations safeguards against data loss during disasters or server failures, ensuring data preservation and system reliability.

- **High Availability:** With data replicated across different servers, the system remains operational even if one database server becomes unavailable. This redundancy allows continued access to data from other available servers.

#### Operational Scenarios

In the event of a database server failure or maintenance, the replication setup ensures continuity:

- **Single Slave Database Offline:** If a single slave database goes offline, read operations can temporarily shift to the master database or other available slave databases until the issue is resolved and the offline database is replaced.

- **Master Database Offline:** Should the master database fail, a designated slave database can be promoted to act as the new master. This promotion enables continued data operations while a new slave database is prepared for replication. Complexities in this scenario, such as ensuring data consistency and synchronization, require careful management and potentially the use of advanced replication techniques like multi-master setups.

#### Connection Flow

Here's how the system handles user requests and data operations:

- **User Connection:** Users access the system through the load balancer's public IP address obtained via DNS resolution.

- **Load Balancer Connection:** User requests are routed through the load balancer to available web servers.

- **Data Access:** Web servers retrieve user data primarily from slave databases, distributing read queries across multiple replicas for optimized performance.

- **Data Modification:** Write, update, and delete operations are directed to the master database, ensuring data consistency and integrity across the system.

By implementing database replication, organizations can achieve robust data management strategies that enhance performance, reliability, and availability across their applications and services.




### Cache

A cache is a temporary storage area that holds frequently accessed data or the results of expensive operations in memory, facilitating faster responses to subsequent requests. As depicted in Figure 1-6, each time a web page loads, multiple database queries may be triggered to fetch data. These repeated queries can significantly impact application performance, which is where a cache proves beneficial.

#### Cache Tier


The cache tier acts as a high-speed data storage layer situated between the application and the database. It offers several advantages including enhanced system performance, reduced database workload, and the ability to independently scale the cache tier.

When a web server receives a request, it first checks if the required data is available in the cache. If the data is cached, it is swiftly retrieved and returned to the client. If not, the server retrieves the data from the database, stores it in the cache for future requests, and then sends it to the client. This approach is known as a read-through cache. Different caching strategies exist depending on factors such as data type, size, and access patterns, each designed to optimize performance.

#### Considerations for Using Cache

Here are critical considerations when implementing a cache system:

- **Appropriate Use of Cache**: Cache is ideal for storing data that is read frequently but modified infrequently. Since cache data resides in volatile memory, it's unsuitable for persisting critical data. Persistent data should be stored in durable data stores to prevent loss upon cache server restarts.
  
- **Expiration Policy**: Implementing an expiration policy is essential to manage cache freshness. Expired data is automatically removed from the cache, preventing stale data issues. Balancing the expiration period is crucial; too short a period increases database load, while too long risks serving outdated information.

- **Consistency Management**: Maintaining data consistency between the cache and the underlying data store is crucial. Inconsistencies may arise due to asynchronous updates between the two. This challenge becomes more pronounced in distributed environments spanning multiple regions. Refer to resources like Facebook's "Scaling Memcache at Facebook" for insights into managing distributed cache consistency.

- **Failover and Redundancy**: Single cache servers pose a single point of failure risk. To mitigate this, deploy multiple cache servers across different data centers. Overprovisioning memory capacity provides a cushion against unexpected spikes in usage.

- **Eviction Policies**: As the cache reaches its capacity, new entries may displace older ones through eviction policies. Common eviction strategies include Least Recently Used (LRU), Least Frequently Used (LFU), and First In First Out (FIFO), chosen based on specific use case requirements.

By implementing a well-designed cache strategy, applications can achieve significant performance improvements and scalability while effectively managing data access and system reliability.

### Content Delivery Network (CDN)

A Content Delivery Network (CDN) is like having multiple storage centers spread around the world to deliver web content faster. It's especially useful for static things like images, videos, and stylesheets that don't change often. Here’s how it works:


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/elrlkwnlzlb4443al9m2.jpg)

#### How CDN Works


1. **User Request**: When a user visits a website, their browser requests files like images or scripts.
   
2. **CDN Check**: The CDN checks if it already has the requested file stored nearby in its servers. If not, it fetches it from the main server where the website is hosted.

3. **Fetching from Origin**: The CDN gets the file from the main server, which could be a web server or cloud storage like Amazon S3.

4. **Local Delivery**: Once the CDN gets the file, it keeps a copy in its nearby servers. The next time someone else requests the same file, the CDN can deliver it quickly from its local cache.

### Considerations for Using a CDN

Using a CDN has several benefits and considerations:

- **Speed**: Users get content faster because it’s delivered from a server closer to them, reducing load times.

- **Cost**: CDNs charge based on how much data is transferred. It's cost-effective for widely accessed content but may not be worth it for things rarely used.

- **Cache Timing**: Setting how long files stay cached (TTL) is crucial. Too long and outdated content might be served; too short and the server gets overloaded with requests.

- **Backup Plan**: Have a plan in case the CDN goes down. Websites should be able to switch back to serving content directly from the main server temporarily.

- **Updating Content**: If you need to change or remove cached files before they expire, use tools provided by the CDN provider or change the file names.

**CDN helps in**

1. **Faster Delivery**: All static files (like images and stylesheets) are served quickly from the CDN, improving website speed and user experience.

2. **Database Relief**: By storing often-used data in the CDN, it reduces the strain on the main database server, making the website more scalable and responsive.

Integrating a CDN makes your website faster and more reliable globally, ensuring users get a smoother experience regardless of their location.


### Stateless Web Tier

When scaling the web tier horizontally, it's essential to move state data (like user session information) out of the web tier. A good practice is to store session data in persistent storage such as a relational database or NoSQL database. This allows each web server in the cluster to access state data from the databases, resulting in a stateless web tier.



### Stateful Architecture

Stateful and stateless servers have key differences. A stateful server retains client data (state) across requests, while a stateless server does not. HTTPs is Stateless Protocal.

In this stateful setup, user A’s session data and profile image are stored on Server 1. To authenticate User A, HTTP requests must be routed to Server 1. If a request goes to another server, such as Server 2, authentication would fail because Server 2 lacks User A’s session data. Similarly, all requests from User B must go to Server 2, and requests from User C to Server 3.

The drawback is that every request from the same client must be routed to the same server, often managed through sticky sessions in load balancers. However, this adds overhead and makes it harder to add or remove servers and handle server failures.

### Stateless Architecture


In a stateless setup, HTTP requests from users can be sent to any web server, which fetches state data from a shared data store. The state data is kept out of the web servers, making the system simpler, more robust, and scalable.


### Updated Design with Stateless Web Tier

The session data is moved out of the web tier and stored in a persistent data store such as a relational database, Memcached/Redis, or NoSQL database. NoSQL is often chosen for its scalability. Autoscaling allows adding or removing web servers based on traffic load. With state data removed from web servers, autoscaling the web tier becomes straightforward.

As your website grows and attracts a significant international user base, supporting multiple data centers becomes crucial to improve availability and user experience.


### Data Centers


Example setup with two data centers. Under normal operation, users are geo-routed to the closest data center, splitting traffic between, for example, US-East and US-West. GeoDNS is a service that resolves domain names to IP addresses based on the user's location.

In case of a data center outage, all traffic is redirected to a healthy data center. Figure 1-16 demonstrates this scenario, where data center 2 (US-West) is offline, and 100% of the traffic is routed to data center 1 (US-East).


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/9gxh4qs267bhgpibkm7c.jpg)

### Technical Challenges in Multi-Data Center Setup

- **Traffic Redirection**: Effective tools are needed to direct traffic to the correct data center. GeoDNS can direct traffic to the nearest data center based on user location.

- **Data Synchronization**: Users in different regions may use different local databases or caches. During failover, traffic might be routed to a data center without the necessary data. Replicating data across multiple data centers is a common strategy, as shown in Netflix's implementation of asynchronous multi-data center replication.

- **Testing and Deployment**: It's crucial to test your website/application at different locations in a multi-data center setup. Automated deployment tools ensure consistency across all data centers.

To further scale the system, decoupling different components so they can be scaled independently is necessary. Many real-world distributed systems use messaging queues to solve this problem.



### Message Queue

A message queue acts as a durable component stored in memory, designed to facilitate asynchronous communication. It functions as a buffer, handling the distribution of asynchronous requests within a system. Here's how it operates:


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/0k901l70vgeb2myxxoz6.jpeg)

#### How Message Queue Works


1. **Producers and Consumers**: Input services, known as producers or publishers, generate messages and send them to the message queue. On the other end, consumers or subscribers connect to the queue to process these messages and execute corresponding actions.

2. **Decoupling for Scalability**: Message queues enable loose coupling between components, which is crucial for building scalable and reliable applications. Producers can send messages even if consumers are offline, and consumers can retrieve and process messages independently of producer availability.

Consider an application handling photo customization tasks like cropping and blurring, which require time-intensive processing. In Figure 1-18, web servers publish these jobs to a message queue. Dedicated photo processing workers then retrieve and process these tasks asynchronously from the queue. This setup allows for independent scaling of producers and consumers:


### Logging, Metrics, Automation

For smaller websites operating on a limited number of servers, logging, metrics, and automation tools offer added benefits without being critical. However, as your site expands to serve a larger business, investing in these tools becomes imperative.

- **Logging**: Monitoring error logs is essential for promptly identifying and addressing system issues. Logs can be monitored per server or aggregated into a centralized service for easier management.

- **Metrics**: Gathering diverse metrics provides insights into business performance and system health. Key metrics include host-level data such as CPU usage and memory, aggregated metrics for database and cache performance, and business metrics like daily active users and revenue.

- **Automation**: In complex systems, automation tools enhance efficiency by streamlining tasks such as continuous integration (CI). CI ensures that each code change undergoes automated testing, facilitating early issue detection. Automated processes for build, testing, and deployment further boost developer productivity.

### Integrating Message Queues and Tools


Using of Message Queue ensures system design, focusing on scalability and resilience.

1. **Message Queue Integration**: By incorporating a message queue, the system achieves greater resilience and flexibility. This setup allows components to operate independently, enhancing overall system reliability.

2. **Logging, Monitoring, Metrics, and Automation**: Essential tools are integrated to support system growth and ensure operational efficiency. These tools provide comprehensive insights and facilitate proactive management of system performance and reliability.

As data volume increases daily, scaling the data tier becomes essential to manage growing demands on the system.


### Database Scaling

Scaling a database involves increasing its capacity to handle larger volumes of data and higher user traffic. There are two primary approaches to database scaling: vertical scaling (scaling up) and horizontal scaling (scaling out).

#### Vertical Scaling

Vertical scaling entails upgrading the hardware resources of a single server to enhance its performance and capacity. This approach involves:

- **Increasing Hardware Resources**: Adding more powerful components such as CPUs, RAM, and disks to an existing server. For instance, platforms like Amazon RDS offer database servers with up to 24 TB of RAM, capable of managing extensive data loads.

- **Single Server Limitations**: Despite its power, vertical scaling is constrained by hardware limits. If a single server cannot handle the workload due to size or performance constraints, scaling out becomes necessary.

- **Single Point of Failure**: Relying on a single server increases the risk of downtime and data loss if the server fails.
- **High Cost**: Powerful servers are costly both in terms of initial investment and ongoing maintenance.

#### Horizontal Scaling

Horizontal scaling involves distributing the database workload across multiple servers, often referred to as sharding. Key aspects of horizontal scaling include:

- **Sharding**: Dividing a large database into smaller, more manageable parts called shards. Each shard contains a subset of the data but maintains the same schema structure.

- **Sharding Example**: User data is distributed across shards based on a hashing function like id % 4, ensuring even data distribution.

- **Choosing a Sharding Key**: The sharding key, such as "id" in Figure determines how data is partitioned across shards. A well-chosen sharding key facilitates efficient data retrieval and modification by directing queries to the appropriate shard.


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/8vhkv395wld1jb7qt130.jpg)
  
#### Challenges of Horizontal Scaling

Horizontal scaling introduces several challenges:

- **Resharding**: As data grows, individual shards may reach capacity or experience uneven data distribution. Resharding involves redistributing data across shards and updating sharding configurations. Techniques like consistent hashing help manage this process.

- **Celebrity Problem**: High-profile users or hotspots can overwhelm specific shards with excessive data access, leading to performance bottlenecks. Partitioning shards or dedicating specific shards to high-traffic entities can alleviate this issue.

- **Join Operations and Denormalization**: Performing join operations across shards is complex and can lead to performance degradation. Denormalizing data structures to reduce dependencies and optimize query performance within each shard is a common workaround.

Database sharding is implemented to accommodate increasing data traffic while offloading non-relational functionalities to a NoSQL data store. This strategy helps mitigate database overload and enhances system scalability and performance. For further exploration of NoSQL use cases, refer to the referenced article.


### Summary

Scaling a system is iterative. The techniques learned in this chapter provide a foundation for tackling new challenges and scaling beyond millions of users. Key strategies include:

- Keep the web tier stateless
- Build redundancy at every tier
- Cache data extensively
- Support multiple data centers
- Host static assets in a CDN
- Scale the data tier through sharding
- Split tiers into individual services
- Monitor the system and use automation tools


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
