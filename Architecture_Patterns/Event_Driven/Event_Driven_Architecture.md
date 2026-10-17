# Event-Driven Architecture (EDA)

Event-Driven Architecture (EDA) is a software design pattern that enables organizations to detect significant "events" (such as transactions, site visits, shopping cart abandonment, etc.) and act on them in real-time or near real-time. This pattern replaces the traditional "request/response" architecture, where services wait for a reply before moving on to the next task. EDA operates based on events and is designed to respond to them or carry out actions in response to an event.

## Asynchronous Communication

EDA is often referred to as "asynchronous" communication, meaning that the sender and recipient do not need to wait for each other to proceed. Systems are not dependent on a single message. An example of asynchronous communication is text messaging, where you send a message without knowing if or when the recipient will respond, and you are not waiting for a response to continue your tasks.

## What is an Event?

An event is defined as a change in the state of a key business system. Examples include purchasing a product, checking in for a flight, or a bus arriving late. Events are pervasive across industries, occurring constantly. Anything that creates a message by being produced, published, detected, or consumed is considered an event.

Events are separate from messages. While the event is the occurrence, the message is the notification that relays the occurrence. In EDA, an event often triggers one or more actions or processes in response.

## How Does Event-Driven Architecture Work?

The components of EDA typically include three parts: producer, consumer, and broker/router. The broker can be optional, particularly when a single producer communicates directly with a single consumer. In enterprises, multiple sources often send various events to one or more consumers interested in some or all of those events.

### Components of EDA

1. **Event Producers (or Publishers):** 
   Producers are the sources of events; they generate events and send them to the rest of the system. A producer could be a user interface, a sensor, a service, or any other system capable of detecting or producing a state change of interest to other parts of the system. Though producers are responsible for creating events, they have no event processing functionality.

2. **Event Consumers (or Subscribers):** 
   Consumers handle the event processing tasks in an EDA. They listen on event channels and react when an event of interest is published. The reaction can take many forms, including updating a database, triggering a downstream process, or simply logging information.

3. **Event Routers (or Event Orchestrators):** 
   Routers, or orchestrators, are the brokers that decouple front-end event producers from backend event consumers. They serve as the conduits through which events are delivered, ensuring transmission without requiring a direct connection between the entities. Routers can be based on conventional components (message-oriented middleware) and be useful in a range of messaging systems (message queues, publish/subscribe models, event streams, and more).

## Benefits of Event-Driven Architecture

1. **Scale and Fail Independently:**
   By decoupling services, they only need to be aware of the event router, not each other. This ensures that services remain operational even if one service fails. The event router acts as an elastic buffer, accommodating workload surges.

2. **Develop with Agility:**
   EDA eliminates the need for custom code to poll, filter, and route events. The event router automatically filters and pushes events to consumers, removing the need for heavy coordination between producer and consumer services, thus speeding up development.

3. **Audit with Ease:**
   An event router provides a centralized location to audit applications and define policies. Policies can restrict who can publish and subscribe to the router and control access to data. Events can be encrypted both in transit and at rest.

4. **Cut Costs:**
   EDA is push-based, so events are handled on-demand, reducing costs associated with continuous polling, network bandwidth consumption, CPU utilization, idle fleet capacity, and SSL/TLS handshakes.

## When to Use Event-Driven Architecture

1. **Cross-Account, Cross-Region Data Replication:**
   EDA coordinates systems between teams operating across different regions and accounts. Using an event router to transfer data between systems allows independent development, scaling, and deployment of services.

2. **Resource State Monitoring and Alerting:**
   Instead of continuously checking resources, EDA monitors and alerts on anomalies, changes, and updates for resources such as storage buckets, database tables, serverless functions, and compute nodes.

3. **Fanout and Parallel Processing:**
   EDA enables multiple systems to operate in response to an event without custom code. The router pushes the event to systems, which can process the event in parallel for different purposes.

4. **Integration of Heterogeneous Systems:**
   EDA facilitates information sharing between systems running on different stacks without coupling. The event router ensures indirection and interoperability among systems, allowing message and data exchange while remaining agnostic.


## Event-Driven Architecture Models

### Publish/Subscribe (Pub/Sub) Model

The publish/subscribe model, commonly known as pub/sub, is a messaging pattern where senders of messages, called publishers, do not send their messages directly to specific receivers, called subscribers. Instead, messages are categorized into classes, without knowing the consumers. Subscribers express interest in one or more classes and only receive messages that are relevant, without knowing the publishers.

#### Key Characteristics:
1. **Decoupling:** Publishers and subscribers are decoupled, which allows systems to evolve independently.
2. **Scalability:** Easily scalable as the system can handle multiple publishers and subscribers without a direct link.
3. **Flexibility:** Subscribers can dynamically change their subscription without impacting publishers.

#### How it Works:
1. **Publishers:** Produce and send messages to a topic.
2. **Subscribers:** Subscribe to a topic to receive messages.
3. **Broker/Router:** Intermediary that routes messages from publishers to subscribers based on the topic.

#### Example:
In an e-commerce platform, an order management system (publisher) can send order status updates to a topic. Multiple systems (subscribers), such as inventory management and shipping, can subscribe to this topic to receive updates.

### Event Streaming

Event streaming is the practice of capturing data in real-time from event sources like databases, sensors, mobile devices, cloud services, and software applications in the form of streams of events. These streams of data are processed, stored, and analyzed continuously as they occur.

#### Key Characteristics:
1. **Real-time Processing:** Events are processed in real-time, allowing for immediate reaction to events.
2. **Scalability:** Designed to handle high volumes of data, making it suitable for big data applications.
3. **Durability:** Ensures events are stored durably so they can be replayed and processed at any time.

#### How it Works:
1. **Event Producers:** Generate streams of events.
2. **Event Stream Processor:** Processes streams in real-time, performing operations like filtering, transforming, and aggregating.
3. **Event Consumers:** Consume processed events for various applications like monitoring, alerting, and analytics.

#### Example:
A financial trading platform can use event streaming to process and analyze real-time market data. Event producers (market data sources) generate a continuous stream of price updates, which are processed by an event stream processor to detect patterns and trigger trades. Event consumers, such as trading algorithms and dashboards, then use this processed data to make decisions and display information.

By leveraging these models, event-driven architectures enable systems to be more responsive, scalable, and resilient, facilitating real-time data processing and integration across diverse applications and services.




More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli