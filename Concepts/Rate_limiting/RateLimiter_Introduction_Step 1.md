### **Table of Contents**

- [**Introduction**](#introduction)
  - [**What is a Rate Limiter?**](#what-is-a-rate-limiter)
  - [**Advantages of an API Rate Limiter**](#advantages-of-an-api-rate-limiter)
- [**Understanding the Problem and Requirements**](#understanding-the-problem-and-requirements)
  - [**Key Considerations**](#key-considerations)
- [**Step 1: Requirements**](#step-1-requirements)
    - [**Functional and Non Functional Requirements**](#functional-and-non-functional-requirements)
    - [**Functional Requirements to Design a Rate Limiter API**](#functional-requirements)
    - [**Non Functional Requirements to Design a Rate Limiter API**](#non-functional-requirements)

---

**Introduction**

### **What is a Rate Limiter?**
A rate limiter is a mechanism used within network systems to manage and control the flow of traffic sent by a client or a service. Specifically, in the context of HTTP-based communications, a rate limiter sets boundaries on how many requests a client can make to a server within a defined time frame. If a client exceeds this predetermined limit, any additional requests beyond the threshold are blocked or rejected to maintain system stability and fairness. This helps ensure that no single client or user monopolizes the system resources, allowing equitable access for all. Here are a few practical examples to illustrate how rate limiters work in real-world scenarios:
- A social media platform might restrict a user to posting no more than 2 updates per second to prevent spamming or overloading the system with rapid submissions.
- A service might limit account creation to a maximum of 10 new accounts per day from a single IP address to deter fraudulent or automated account generation.
- A gaming or rewards platform could cap the number of times a user can claim rewards to 5 times per week from the same device, ensuring fair distribution of incentives and preventing abuse.

### **Advantages of an API Rate Limiter**
API rate limiters offer several key benefits that help maintain the integrity, performance, and cost-efficiency of systems. Below, we explore these advantages in more detail:

#### **Prevent Resource Starvation**
One of the primary purposes of a rate limiter is to safeguard systems against resource starvation, which can occur due to excessive requests overwhelming the server. This is particularly critical in preventing Denial of Service (DoS) attacks, where malicious actors flood a system with requests to disrupt service. Many leading technology companies implement rate limiting as a protective measure. For instance, Twitter imposes a cap of 300 tweets per user every 3 hours to manage traffic, while Google Docs APIs set a default limit of 300 read requests per user every 60 seconds. By rejecting requests that exceed these limits, rate limiters mitigate the risk of both intentional attacks and unintentional overuse, ensuring that resources remain available for legitimate users.

#### **Reduce Costs**
Rate limiting plays a vital role in cost management, especially for organizations that rely on paid third-party APIs. By restricting the number of requests made to these external services, companies can significantly lower their operational expenses. Fewer requests mean reduced server usage on their end, which translates to fewer servers needed and more resources available for critical, high-priority APIs. For example, operations like checking credit scores, processing payments, or accessing health records often involve per-request charges from third-party providers. Limiting these calls ensures that costs are kept under control while still meeting business needs.

#### **Prevent Server Overload**
Servers can become overwhelmed by a high volume of requests, particularly when they originate from automated bots or users engaging in inappropriate behavior. A rate limiter acts as a gatekeeper, filtering out excessive or unnecessary requests to maintain optimal server performance. This helps ensure that the system remains responsive to legitimate users and avoids performance degradation or crashes due to overload.

### **Understanding the Problem and Requirements**

#### **Key Considerations**
When designing or implementing a rate limiter, several important factors must be taken into account to ensure it meets the needs of the system and its users. These considerations include:
- **Type of Rate Limiter:** The focus here is on a server-side API rate limiter, which operates at the server level to control incoming requests before they reach the application logic.
- **Throttling Criteria:** The rate limiter must be versatile enough to enforce various throttling rules based on different identifiers, such as a client’s IP address, user ID, or other relevant attributes, allowing for tailored restrictions.
- **System Scale:** Given the potential for high traffic volumes, the rate limiter must be capable of handling a large number of requests without becoming a bottleneck or failing under load.
- **Distributed Environment:** In modern systems with multiple servers or data centers, the rate limiter should function effectively in a distributed setup, ensuring consistent enforcement of limits across all nodes.
- **Implementation Approach:** The rate limiter can be deployed as a standalone service separate from the main application or embedded directly within the application code, depending on architectural preferences and requirements.
- **User Notifications:** It’s essential to provide clear feedback to users when their requests are throttled, informing them that they’ve exceeded the allowed limit and guiding them on next steps or retry timing.

### **Step 1: Requirements**

#### **Functional and Non Functional Requirements**

##### **Functional Requirements to Design a Rate Limiter API:**
These requirements outline the core capabilities and behaviors the rate limiter API must support to meet business and user needs:
- **Support for Multiple Rate-Limiting Rules:** The API should allow administrators or developers to define various rules for rate limiting, such as different thresholds for different types of users, endpoints, or time periods, ensuring flexibility in policy enforcement.
- **Customizable Responses for Exceeded Limits:** When a client surpasses the rate limit, the API should provide the ability to return tailored responses, such as specific error messages or headers indicating when they can retry, improving user experience.
- **Storage and Retrieval of Rate-Limit Data:** The API must include mechanisms to store data related to request counts and limits (e.g., per user or IP) and retrieve this information efficiently to enforce rules accurately.
- **Proper Error Handling:** The system should gracefully handle scenarios where request thresholds are exceeded, whether on a single server or across a distributed setup. Clients should receive clear, meaningful error messages to inform them of the issue and prevent confusion.

##### **Non ßs̄Functional Requirements to Design a Rate Limiter API:**
These requirements focus on the quality attributes and performance characteristics that ensure the rate limiter operates effectively in a production environment:
- **High Availability and Scalability:** The API must remain operational and responsive even during peak traffic periods, as availability is a critical factor for systems handling frequent requests. It should also scale seamlessly to accommodate growing user bases or request volumes.
- **Security Against Malicious Attacks:** The rate limiter must be designed with robust security measures to protect against abuse, such as attempts to bypass limits or exploit vulnerabilities, ensuring the integrity of the system.
- **Ease of Integration:** The API should be straightforward to incorporate into existing systems or applications, minimizing the need for extensive rework or complex configurations.
- **Low Latency:** Performance is a key concern for any system, and the rate limiter should introduce minimal delay in processing requests. Low latency ensures that the rate-limiting mechanism does not negatively impact the overall user experience or system responsiveness.