# Designing for High Availability

High availability is a critical aspect of system design, ensuring that a system remains operational and accessible to users, even in the face of failures. It is typically expressed as a percentage of uptime over a given period. For example, a system with 99.9% availability is expected to be operational 99.9% of the time, which translates to roughly 8.76 hours of downtime per year.

## What is Availability in System Design?

Availability in system design refers to the ability of a system to remain operational and accessible to users. It is a crucial aspect of system reliability, particularly for critical systems such as online banking, e-commerce websites, and cloud computing platforms. High availability prevents financial losses, reputational damage, and user dissatisfaction by ensuring users can access the system and its services whenever needed.

Achieving high availability involves designing systems with redundancy, fault tolerance, and the ability to quickly recover from failures. Redundancy involves duplicating critical components or functions of a system to increase reliability. For example, using multiple servers in a load-balanced configuration ensures that if one server fails, others can handle the load. Fault tolerance involves designing systems with built-in mechanisms to detect, isolate, and recover from faults. For example, using error detection and correction codes in communication protocols can help detect and correct errors in data transmission.

## How is Availability Measured?

Availability is measured as the percentage of a system’s uptime in a given time period, calculated as follows:

Availability = Uptime / (Downtime + Uptime​)

### The Nine’s of Availability

Availability is often measured in terms of "nines" rather than percentages. Here’s a breakdown of different levels of availability and their corresponding downtime:

| Availability % | Downtime (Year)  | Downtime (Month) | Downtime (Week) |
|----------------|------------------|------------------|-----------------|
| 90% (one nine) | 36.53 days       | 72 hours         | 16.8 hours      |
| 99% (two nines) | 3.65 days        | 7.20 hours       | 1.68 hours      |
| 99.9% (three nines) | 8.77 hours   | 43.8 minutes     | 10.1 minutes    |
| 99.99% (four nines) | 52.6 minutes | 4.32 minutes     | 1.01 minutes    |
| 99.999% (five nines) | 5.25 minutes | 25.9 seconds    | 6.05 seconds    |
| 99.9999% (six nines) | 31.56 seconds | 2.59 seconds   | 604.8 milliseconds |
| 99.99999% (seven nines) | 3.15 seconds | 263 milliseconds | 60.5 milliseconds |
| 99.999999% (eight nines) | 315.6 milliseconds | 26.3 milliseconds | 6 milliseconds |
| 99.9999999% (nine nines) | 31.6 milliseconds | 2.6 milliseconds | 0.6 milliseconds |

## Patterns to Achieve High Availability

### Redundancy

Employing redundancy involves duplicating components (e.g., servers or storage) to ensure that another can take over seamlessly if one fails. There are two types of redundancy:

- **Passive Redundancy**: In this pattern, an active node handles all the traffic while a passive (standby) node waits to take over in case the active node fails.

Some components are active while backup components are on standby. If an active component fails, a backup takes over.

**Use Case**: Database replication where the primary database is active and a replica database is on standby.

- **Active Redundancy**:  Multiple nodes are active and handle traffic simultaneously. If one node fails, the others continue to handle the load.
Multiple components work simultaneously. If one fails, others continue to function without interruption.

**Use Case:** Load-balanced web servers where traffic is evenly distributed across multiple servers.

Let's explore three common redundancy architectures: **Hot-Cold, Hot-Warm, and Hot-Hot,** along with their pros and cons.

### Hot-Cold Architecture

In the Hot-Cold architecture, there is a primary instance that handles all client reads and writes, and a backup instance that remains idle until needed. The primary instance continuously synchronizes data to the backup instance. If the primary fails, manual intervention is required to switch clients over to the backup instance.


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/zls6y97oghw26eevjvdo.png)

**Pros**: Simple and straightforward design.
**Cons**: Resource wastage due to the idle backup instance; potential for data loss depending on the last synchronization; manual intervention needed for failover.

### Hot-Warm Architecture

The Hot-Warm architecture optimizes resource utilization by allowing clients to read from the backup instance while the primary handles all writes. If the primary fails, clients can still read from the backup instance with reduced capacity.


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/xq2z5azw8g23c385db0b.png)

**Pros**: Better resource utilization compared to Hot-Cold; reduced downtime for read operations during failover.

**Cons**: Potential for stale reads if data synchronization is not up-to-date; complexity in maintaining data consistency.

### Hot-Hot Architecture

In the Hot-Hot architecture, both instances act as primaries, handling reads and writes. This requires bidirectional state replication, which can lead to data conflicts if sequential ordering is needed.

 
![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/oq3tv66ujxkulp2waj97.png)


**Pros**: High flexibility and resource utilization; continuous availability even if one instance fails.
**Cons**: Complexity in maintaining data consistency; potential for data conflicts in scenarios requiring sequential data.

### Failure Detection and Alerting

Redundancy alone is not enough. Systems must have mechanisms to detect failures and alert administrators. Continuous monitoring and regular high-availability testing are essential to take corrective action promptly.

### Load Balancing

Load balancing distributes incoming requests across multiple servers or resources to prevent overloading any single component and improve overall system performance and fault tolerance.

### Automatic Failover

Implement mechanisms for automatic failover so that if one component fails, another takes over its function automatically without manual intervention.

### Data Replication

Replicate data across multiple locations to avoid outages and make the system resilient against disasters. Replication can be synchronous or asynchronous, depending on the requirements.

`Note: will discuss more about this topic in upcoming articles`

### Performance Optimization and Scalability

Ensure the system is designed and tuned to handle the expected load efficiently, reducing the risk of bottlenecks and failures. Design the system to scale easily by adding more resources when needed to accommodate increased demand.

### High Availability Architectures

**Microservices**: Break down applications into smaller, independent services that can be deployed and scaled independently.
**Containerization:** Use container orchestration platforms like Kubernetes to manage and scale applications automatically.
**Service Mesh:** Implement a service mesh to manage service-to-service communication, security, and monitoring.

### Disaster Recovery (DR)

Have a comprehensive plan to recover the system in case of a catastrophic event that affects the primary infrastructure.

### Monitoring and Alerting

Implement robust monitoring systems that can detect issues in real-time and notify administrators to take appropriate action promptly.

## High Availability vs. Fault Tolerance

Both high availability and fault tolerance aim to achieve high uptime but approach the problem differently.

- **High Availability**: Focuses on minimizing downtime and may use software-based approaches, making it more flexible and easier to implement.

- **Fault Tolerance**: Ensures the system continues to function normally even during failures. It often requires multiple systems running in parallel and advanced hardware to detect and manage component faults. 

Fault tolerance provides a higher level of protection against failures but can be more complex and costly to implement compared to high availability strategies.

## Conclusion

High availability is essential for systems where continuous operation is vital, and any disruption could lead to significant consequences. By employing redundancy, load balancing, automatic failover, data replication, and robust monitoring, system designers can ensure that their systems remain operational and accessible to users, even in the face of failures.


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli