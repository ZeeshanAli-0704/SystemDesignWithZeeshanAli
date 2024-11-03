### Table of Contents

1. [Introduction to UPI System Design](#introduction-to-upi-system-design)
2. [NPCI](#npci)
3. [PSP (Payment Service Providers)](#psp-payment-service-providers)
4. [Key Components](#key-components)
5. [Core Functionalities](#core-functionalities)
6. [Use Cases](#use-cases)
7. [Functional Requirements for UPI System Design](#functional-requirements-for-upi-system-design)
8. [Non-Functional Requirements for UPI System Design](#non-functional-requirements-for-upi-system-design)
9. [Capacity Estimation for UPI System Design](#capacity-estimation-for-upi-system-design)
10. [High Level Architecture](#high-level-architecture)
11. [Transaction Flow Explanation](#transaction-flow-explanation)
12. [Key Features in the HLD](#key-features-in-the-hld)
13. [Low Level Architecture Components](#low-level-architecture-components)
14. [Security Measures](#security-measures)
15. [Challenges to Consider](#challenges-to-consider)

### Unified Payments Interface (UPI) System Design

Designing a Unified Payments Interface (UPI) system involves creating a robust and secure architecture that enables real-time inter-bank transactions. UPI, managed by the National Payments Corporation of India (NPCI), is a platform that allows seamless fund transfers between bank accounts through mobile devices.

NPCI

NPCI is the orchestrater which defines the rules and regulations, defines the responsibilities of each entity, manages transaction processing etc. NPCI provides the ecosystem for routing, processing and settlement services to members participating in UPI. NPCI also performs approval of participating PSPs, conducting audits of the PSPs, report generation etc.

PSP

PSP (Payment service Provider) is one of the entities in UPI ecosystem whose responsibility is to provide users with a frontend application which can be used for generating the VPAs and carrying out transactions.

---

### Key Components

User Interfaces:
   - UPI apps are developed by banks and third-party service providers. These mobile applications allow users to initiate and manage transactions, view history, and interact with payment services.
   
Central UPI Switch:
   - Managed by NPCI, the central switch acts as the intermediary that routes transaction requests between banks, ensuring proper authentication, authorization, and secure fund transfer.

Banking Systems:
   - Each participating bank’s backend system interacts with the UPI switch to process and approve transactions. These systems ensure that money is transferred from the sender’s account to the receiver’s account in real time.
   
Third-Party Service Providers:
   - Apps developed by non-banking entities that facilitate UPI payments (e.g., Google Pay, PhonePe) are integrated with the NPCI and banks, providing users with easy access to UPI functionalities.

VPA
 - A Virtual Payment Address (VPA) is a unique identifier that helps UPI to track a person’s account. It acts as an ID independent of your bank account number and other details. VPA can be used to make and request payments through a UPI-enabled app. You need not fill in your bank account details repeatedly for making multiple payments.

---

### Core Functionalities

User Registration and Authentication:
   - Users register on the platform by linking their bank accounts using their mobile numbers. Multi-factor authentication (e.g., mobile number OTP, MPIN) ensures that only authorized users can access their accounts.

Payment Initiation and Authorization:
   - Payments are initiated using a UPI ID or Virtual Payment Address (VPA), a unique identifier linked to the user’s bank account. Users can also transfer funds using the receiver’s mobile number or by scanning QR codes.
   
Inter-Bank Transaction Processing:
   - Once a payment is authorized, the UPI switch routes the transaction request to the receiver’s bank for approval and settlement. The entire process happens in real time, ensuring seamless and instant transfers.

Real-Time Settlement:
   - UPI enables immediate settlement of funds between banks, reducing the complexity and delays associated with traditional banking transactions.

---

### Use Cases

- Peer-to-Peer (P2P) Transfers:
  Users can instantly send money to each other using UPI IDs, mobile numbers, or contact lists. Transactions can be done by simply selecting the receiver from the phone book or entering the UPI ID.
  
- Peer-to-Merchant (P2M) Payments:
  UPI is also widely used for making purchases, whether online or in physical stores. Users can scan QR codes or use merchant-specific VPAs to complete payments. This makes it convenient for businesses to accept digital payments.

- Bill Payments and Recharges:
  UPI enables users to pay utility bills, recharge their mobile phones, or make other recurring payments directly from their bank accounts with just a few clicks.

---

### Functional Requirements for UPI System Design

User Registration and Authentication
   - Users should be able to register and create a UPI account by providing necessary details (e.g., phone number, bank account).
   - Provide secure authentication mechanisms such as PIN-based authentication and biometric verification (fingerprint, face ID) to ensure only authorized access to the UPI account.

Bank Account Linking
   - Enable users to link one or more bank accounts to their UPI profile.
   - Facilitate the management of linked accounts, allowing users to add, remove, or update account details easily.
   - Ensure that bank account verification is secure and user-friendly.

Payment Address Management
   - Allow users to create and manage multiple Virtual Payment Addresses (VPAs).
   - Provide options for setting a primary VPA and associating specific bank accounts with each VPA for simplified transactions.

Money Transfer
   - Support person-to-person (P2P) payments, allowing users to send money to any UPI-enabled user.
   - Support person-to-merchant (P2M) payments, enabling users to make payments at merchants (physical or online stores).
   - Enable users to schedule future payments and set up recurring payments for regular expenses such as bills or subscriptions.

Transaction History
   - Provide users with a detailed and accessible transaction history, including transaction status (e.g., success, failure), timestamps, and details of the sender/receiver.
   - Allow users to filter and search through their transaction history for easy management.

Notifications
   - Send real-time notifications (via SMS, email, or in-app) for all transactions, including payment initiation, success, failure, and any payment requests.
   - Provide users with timely updates about payment statuses and changes to their linked accounts.

Payment Requests
   - Allow users to request payments from other UPI users by specifying the amount and a note for the request.
   - Provide mechanisms to track pending and completed payment requests.

QR Code Payments
   - Enable users to generate QR codes for receiving payments.
   - Support scanning of QR codes for quick and efficient payments at physical stores, online websites, or peer-to-peer transactions.

Bill Payments
   - Integrate with utility and service providers to allow users to pay bills (e.g., electricity, water, mobile recharges) directly from their UPI account.
   - Support one-time and recurring bill payments, providing users with options to automate recurring payments.

---

### Non-Functional Requirements for UPI System Design

Scalability
   - The system should handle millions of users and support high transaction volumes, especially during peak hours.
   - Implement horizontal scaling capabilities to add more servers as demand increases, ensuring that system performance remains unaffected by the growth in users and transactions.

Performance
   - Ensure low latency for both user interactions and transaction processing to provide a smooth, real-time experience.
   - Aim for transaction completion times of less than a few seconds, ensuring minimal delay between payment initiation and confirmation.
   
Reliability
   - Ensure the system operates with high availability, maintaining uptime even in the event of failures or server downtime.
   - Implement failover mechanisms and redundancy to ensure that any service interruptions are handled without affecting user transactions.
   
Security
   - Implement end-to-end encryption to protect sensitive user data and ensure secure transmission of payment information.
   - Ensure the system complies with industry security standards such as PCI-DSS and RBI guidelines.
   - Utilize multi-factor authentication and fraud detection mechanisms to protect user accounts from unauthorized access and fraudulent transactions.

Consistency
   - Ensure that all transactions are processed consistently to avoid discrepancies, double payments, or partial transactions.
   - Implement a mechanism to guarantee eventual consistency across distributed systems, ensuring the accuracy of transaction records across multiple services.

Compliance
   - Adhere to all relevant regulatory requirements and guidelines set by financial authorities such as the RBI.
   - Ensure the system complies with data privacy regulations, including ensuring that personal and financial data is handled with the utmost care and confidentiality.
   
Usability
   - Design a user-friendly interface that caters to users with varying levels of technical expertise, ensuring ease of use and accessibility.
   - Optimize the user experience by offering clear instructions, simple navigation, and seamless interactions, whether through a mobile app or website. 

Accessibility
   - Ensure that the UPI system is accessible to all users, including those with disabilities.
   - Support multiple languages and provide features that accommodate users with varying technical skills, ensuring inclusive usage of the platform.

--- 


### Capacity Estimation for UPI System Design

#### Envelope Calculations

| Metric                    | Value                                   |
|-------------------------------|---------------------------------------------|
| Total Transactions (June 2024) | 13.89 Billion                              |
| Total Transaction Amount   | ₹20.27 Lakh Crore                           |
| Daily Average Transactions | 463 Million                                 |
| Estimated Annual Transactions | 166.68 Billion (13.89 Billion × 12)        |
| Peak Daily Transactions    | 601.9 Million (463 Million × 1.3)           |
| Transactions Per Hour      | 25.08 Million (601.9 Million ÷ 24)          |
| Transactions Per Second (TPS) | 6,967 (601.9 Million ÷ 86,400)              |

---

#### Capacity Estimations

| Metric                            | Value                                             |
|---------------------------------------|-------------------------------------------------------|
| Sample Payload Size               | ~373 bytes                                            |
| Daily Storage Requirement         | 172.7 GB (463 Million transactions × 373 bytes)       |
| Monthly Storage Requirement       | 5.18 TB (172.7 GB/day × 30 days)                      |
| Annual Storage Requirement        | 63.04 TB (172.7 GB/day × 365 days)                    |

---

#### Network Bandwidth Calculation

| Metric                            | Value                                             |
|---------------------------------------|-------------------------------------------------------|
| API Call Size                     | 2 KB (including headers)                              |
| API Calls per Transaction         | 3 (initiate, process, notify)                         |
| Total API Calls per Day           | 1.389 Billion (463 Million transactions × 3)          |
| Data Transfer per Day             | 2.778 TB (1.389 Billion API calls × 2 KB)             |
| Peak API Calls per Day            | 1.8057 Billion (601.9 Million transactions × 3)       |
| Peak Data Transfer per Day        | 3.61 TB (1.8057 Billion API calls × 2 KB)             |

---

#### Server Requirements

| Metric                            | Value                                             |
|---------------------------------------|-------------------------------------------------------|
| Assumed Request Processing Time   | 50 ms                                                 |
| Peak TPS                          | 6,967                                                 |
| Throughput (Requests per Second)  | 348,350 RPS (6,967 TPS × 50)                          |
| API Gateway Servers Required      | 348 servers (348,350 RPS ÷ 1,000 RPS/server)          |
| Backend Servers Required          | 1,742 servers (348,350 RPS ÷ 200 RPS/server)          |

---

#### High Level Architecture

User Interface (Mobile/Web Apps):
   - Customers (Payers) initiate UPI transactions through mobile applications or web interfaces provided by banks or third-party payment service providers (PSPs) like Google Pay, PhonePe, etc.
   - Payees (Receivers) also receive payments via these apps.

Payment Service Providers (PSPs):
   - Each Customer's and Payee's PSP is responsible for facilitating the payment by connecting the user to the UPI system.
   - PSPs also provide the interface for managing Virtual Payment Addresses (VPAs).

UPI Centralized Switch (NPCI):
   - The NPCI UPI Switch is the central service that processes transaction requests, routing them between the payer’s and payee’s banks.
   - Acts as the hub for real-time payment settlement and transaction management.
   - Facilitates security, encryption, and authentication of the transaction, ensuring safe communication.

Banking Backend Systems:
   - Remitter/Issuer Bank: The payer’s bank verifies account details, checks for available funds, and processes the debit request.
   - Beneficiary Bank: The payee’s bank processes the credit request once funds are available.
   - Both banks interact with the NPCI UPI Switch for real-time communication and settlement.

Security and Authentication:
   - Encryption Protocols: Transactions are secured using encryption mechanisms like TLS (Transport Layer Security), AES (Advanced Encryption Standard), and PKI (Public Key Infrastructure).
   - Authentication: Customers authenticate payments using MPIN (Mobile Personal Identification Number), biometrics, or two-factor authentication (2FA).
   - Device Binding: The UPI app is bound to specific user devices for added security.

VPA Management Service:
   - This service manages the creation and validation of Virtual Payment Addresses (VPA), allowing customers to perform transactions without sharing their bank account details.

QR Code Generator/Scanner Service:
   - The QR Code Service generates and scans UPI QR codes, which customers can use to initiate payments.

---


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/13c6mzc89ibvsx8x7qa8.png)

### Transaction Flow Explanation:

Step 1: VPA Creation
   - The customer creates a Virtual Payment Address (VPA) using their PSP’s mobile app.
   - The request is sent to the VPA Management Service, which verifies and registers the VPA, responding with a success message.

Step 2: Initiating Payment
   - The customer initiates a payment by scanning a QR code using the QR Code Scanner or entering the payee’s VPA.
   - The PSP sends a payment request to the NPCI UPI Network to start the process.

Step 3: Payment Authorization
   - The customer authorizes the payment through their PSP app using MPIN, biometric verification, or 2FA.
   - The Payer PSP forwards the authorization to the NPCI UPI Switch for processing.

Step 4: Payment Processing
   - The NPCI Switch sends a debit request to the Remitter/Issuer Bank (payer’s bank), where the bank verifies the available balance.
   - The Remitter Bank responds to the NPCI with the result of the debit request.
   - Upon successful debit, the NPCI sends a credit request to the Beneficiary Bank (payee’s bank), which credits the payee’s account.

Step 5: Payee Details Validation
   - The NPCI Switch requests the payee details from the Payee PSP to ensure the payee’s identity and details are accurate.
   - The Payee PSP validates the details and confirms them with the NPCI network.

Step 6: Transaction Completion
   - Once the transaction is completed, the NPCI sends a debit response to the Payer PSP, confirming the transaction status.
   - The Payer PSP notifies the customer about the successful transaction.
   - Similarly, the Payee PSP notifies the payee about the received payment.

---

### Key Features in the HLD:

- Real-Time Processing: UPI transactions are processed in real time, ensuring instant transfer of funds.
- Scalability: The system is designed to scale horizontally with load balancers and microservices. This allows each component to scale independently as transaction volumes grow.
- Fault Tolerance: By using retry mechanisms and circuit breakers, the system is robust against failures and ensures that transactions are retried in case of temporary errors.
- Asynchronous Messaging: Services like message queues (e.g., RabbitMQ, Kafka) are used for handling notifications, transaction updates, and asynchronous tasks.

---

### Low Level Architecture Components:

User Interfaces (Mobile Applications, USSD, Web, etc.):
   - Mobile App (Customer/Payee): Users interact with UPI through mobile apps, web apps, or USSD (*99#) to initiate transactions, check balances, and manage accounts.
   - Internet Banking (Customer/Payee): Web-based access to UPI functionalities, often integrated with internet banking systems.
   - Third-Party Applications (e.g., Google Pay, PhonePe): Apps use UPI APIs to allow users to initiate or collect payments.
   - USSD Code (*99#): Allows users to perform banking transactions using basic mobile phones without internet access.

Microservices Layer (Internal Components of UPI System):
   - User Service:
     - Handles registration and login requests.
     - Manages user details and device information for UPI authentication.
     - Endpoints:
       - `POST /register`: Register a new user with details like mobile number, MPIN.
       - `POST /login`: Authenticate a user.
   - Bank Service:
     - Facilitates linking of bank accounts and retrieves account balances.
     - Endpoints:
       - `POST /link-account`: Link a bank account to a UPI VPA.
       - `GET /balance`: Retrieve the balance of a linked account.
   - Transaction Service:
     - Manages real-time transaction requests, including money transfers and UPI collect requests.
     - Endpoints:
       - `POST /transfer`: Transfer money between accounts.
       - `POST /collect`: Collect payment requests.
       - `GET /history`: Retrieve transaction history for the user.
   - Notification Service:
     - Sends notifications for completed transactions or failed attempts.
     - Endpoints:
       - `POST /notify`: Notify users of transaction status.
   - VPA Management Service:
     - Manages the creation and maintenance of Virtual Payment Addresses (VPAs).
     - Endpoints:
       - `POST /create-vpa`: Create a new VPA for a user.
       - `GET /vpa-details`: Fetch VPA details for validation.

NPCI UPI Switch:
   - Centralized system that acts as a transaction switch.
   - Responsible for routing requests between banks, validating transactions, and facilitating real-time settlement.
   - Connects with the payer's bank (Remitter/Issuer) and payee’s bank (Beneficiary) for processing.
   - Uses secure communication protocols (TLS/SSL, PKI) to ensure transaction safety.

Banking Systems:
   - Remitter Bank: The payer’s bank that validates user credentials, checks available balance, and processes debit transactions.
   - Beneficiary Bank: The payee’s bank that processes credit transactions.
   - The bank’s standard interface communicates with the NPCI UPI Switch to settle funds.

Security Modules:
   - Encryption Service: All transactions are encrypted using secure encryption mechanisms like AES, ensuring sensitive data is protected.
   - Authentication Service: Manages authentication using MPIN, biometrics, or 2FA for ensuring that only authorized users can initiate transactions.
   - Fraud Detection Module: Monitors transaction patterns to detect suspicious activities and flag fraudulent transactions.

Central Repository:
   - Stores transaction history, user account details, and audit logs.
   - Provides data to the UPI system for transaction validation and status checks.

External Payment Systems Integration:
   - IMPS (Immediate Payment Service): Used for instant fund transfers within banks.
   - AEPS (Aadhaar Enabled Payment System): Allows payments using Aadhaar-linked bank accounts.
   - RuPay: A card payment network integrated for UPI-linked transactions.

---

### **Security Measures**

**Encryption**:
   - All transaction data is encrypted to ensure secure communication between users, banks, and the UPI switch, protecting sensitive information.

**Multi-Factor Authentication (MFA)**:
   - UPI uses multi-factor authentication to verify user identities. This includes device binding, OTP-based verification, and MPIN for added security, minimizing the risk of fraud and unauthorized transactions.

**Compliance with Regulatory Standards**:
   - UPI follows strict regulatory and security standards mandated by the Reserve Bank of India (RBI) to ensure the safety and integrity of the payment ecosystem.



### **Challenges to Consider:**
- **High Availability & Fault Tolerance:** UPI is a critical service, so the system needs to be highly available (99.999% uptime) and handle failures gracefully.
- **Scalability:** The system should be able to handle millions of transactions per second, especially during peak times.
- **Security:** Ensure robust encryption, tokenization, and authentication mechanisms to prevent fraud.
- **Real-Time Settlement:** Instantaneous fund movement between banks must be supported to ensure user satisfaction.



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli
