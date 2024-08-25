### Table of Contents

- [Introduction](#introduction)
- [Understand the Problem and Define the Design Scope](#understand-the-problem-and-define-the-design-scope)
- [Functional Requirements (FR)](#functional-requirements-fr)
- [Non Functional Requirements (Non FR)](#non-functional-requirements-non-fr)
- [Types of Notifications](#types-of-notifications)
- [Notification Component](#notification-component)
- [Storage Estimations](#storage-estimations)
- [Total Storage Estimation](#total-storage-estimation)
- [Notification Types](#notification-types)
- [High Level Design Components](#high-level-design-components)
- [Workflow Explanation](#workflow-explanation)
- [Deep Design Concepts](#deep-design-concepts)
- [Low Level Design of API Database Diagram for Notification System](#low-level-design-of-api-database-diagram-for-notification-system)
- [Conclusion](#conclusion)
- [UseCases](#usecases)
  - [Use Case Send Push Notification for Product Availability](#use-case-send-push-notification-for-product-availability)
  - [Use Case Prioritize OTP Notifications Over Normal Notifications](#use-case-prioritize-otp-notifications-over-normal-notifications)

# Introduction

This structure provides a clear and organized approach to navigating your blog. Each section can be linked accordingly for easy access. Let me know if you need any modifications!

The Notification System is designed to alert users with crucial information, such as breaking news, product updates, events, and special offers. Notifications can be delivered via mobile push notifications, SMS messages, or emails.

# Understand the Problem and Define the Design Scope

Designing a scalable system capable of sending millions of notifications daily requires a comprehensive understanding of the notification landscape. The problem is intentionally open-ended, so it's essential to clarify requirements through thoughtful questioning.

# Functional Requirements (FR):
- **Notification Types**: The system must support push notifications, SMS messages, and emails.
- **Real-Time Delivery**: The system should operate as a soft real-time system, delivering notifications to users as quickly as possible. Minor delays are acceptable under heavy loads.
- **Device Support**: The system must support notifications on iOS devices, Android devices, and desktop/laptop computers.
- **Notification Triggers**: Notifications can be triggered by client applications or scheduled on the server-side.
- **Opt-Out Option**: Users must have the ability to opt out of receiving notifications.
- **Daily Notification Volume**: The system must handle 10 million mobile push notifications, 1 million SMS messages, and 5 million emails daily.

- **Message Prioritization**: Critical messages, such as OTPs, should be prioritized over lower-priority notifications like promotional content.

- **Plugability and Extensibility**: The system should be easy to integrate with various applications and be extendable to support additional types of notifications.

- **Rate Limiting**: Implement rate limiting to prevent users from being spammed with promotional messages and to protect external services from being overloaded.

# Non Functional Requirements (Non FR):
- **Notification Size**: Notifications should be limited to 1 MB, allowing for extensive text and a thumbnail image.

- **Scalability**: The system must be highly scalable, capable of sending millions of notifications per day.

- **Reliability**: The system must ensure reliable delivery of notifications.

- **Client Expandability**: It should be easy to add new clients to the system.

---

# Types of Notifications:
- **SMS**
- **Email**
- **Mobile Push Notifications** (iOS, Android)


# Notification Component

- **Provider**: Builds and sends notifications to the notification service.
- **Device Token**: A unique identifier used for sending push notifications.
- **Payload**: A JSON object that contains the notification's data.
- **Notification Service**: Delivers notifications to the intended clients.
- **Client**: Mobile devices, email systems, etc., that receive notifications.

---

# Storage Estimations

### **User Information**
- **Data Stored**: User ID, email, phone number, preferences, etc.
- **Average Size per Record**: 
  - User ID: 8 bytes
  - Email: 100 bytes
  - Phone Number: 15 bytes
  - Preferences and other data: 50 bytes
  - **Total per user**: ~173 bytes

- **Estimated Number of Users**: 10,000,000
- **Total Storage**: 
  ```
  10,000,000 x 173   bytes   ~ 1.73   GB 
    
  ```

### **Device Tokens**
- **Data Stored**: User ID, device token, device type, last updated timestamp.
- **Average Size per Record**:
  - User ID: 8 bytes
  - Device Token: 64 bytes
  - Device Type: 10 bytes
  - Timestamp: 8 bytes
  - **Total per token**: ~90 bytes

- **Estimated Number of Device Tokens**: 20,000,000 (assuming 2 devices per user on average)
- **Total Storage**: 
  ```
  20,000,000 x 90   bytes   ~ 1.8   GB 
  ```

### **Notification Logs**
- **Data Stored**: User ID, notification type, content, status, timestamp, etc.
- **Average Size per Record**:
  - User ID: 8 bytes
  - Notification Type: 10 bytes
  - Content (Payload): 1,024 bytes (1 KB)
  - Status: 1 byte
  - Timestamp: 8 bytes
  - **Total per log**: ~1,027 bytes

- **Estimated Number of Notifications**: 16,000,000 per day with logs retained for 30 days.
- **Total Storage**: 
  ```
  16,000,000 x 30 x 1,027   bytes   ~ 492.96   GB 
  ```

### **Notification Templates**
- **Data Stored**: Template ID, content, metadata.
- **Average Size per Record**:
  - Template ID: 8 bytes
  - Content: 2,048 bytes (2 KB)
  - Metadata: 100 bytes
  - **Total per template**: ~2,108 bytes

- **Estimated Number of Templates**: 1,000
- **Total Storage**: 
  ```
  1,000 x 2,108   bytes   ~ 2.06   MB   ~ 0.002   GB 
  ```

### **Analytics Data**
- **Data Stored**: User engagement metrics, delivery success rates, click-through rates, etc.
- **Average Size per Record**:
  - Metric Data: 100 bytes
  - Timestamp: 8 bytes
  - **Total per record**: ~108 bytes

- **Estimated Number of Records**: 16,000,000 notifications daily stored for 30 days.
- **Total Storage**: 
  ```
  16,000,000 x 30 x 108   bytes   ~ 51.84   GB 
  ```

### **Total Storage Estimation**

Summing up all components:
- **User Information**: ~1.73 GB
- **Device Tokens**: ~1.8 GB
- **Notification Logs**: ~492.96 GB
- **Notification Templates**: ~0.002 GB
- **Analytics Data**: ~51.84 GB

### **Grand Total Storage Estimate**: 
```
1.73 + 1.8 + 492.96 + 0.002 + 51.84  ~ 548.33   GB 
```

**Summary**

The grand total storage estimation of **approximately 548.33 GB** encompasses all key components of your notification system. This estimation is essential for planning the database capacity, ensuring efficient storage solutions, and anticipating growth as the user base and notification volume increase. 

---

# Notification Types

This section outlines a high-level design for a system that supports various types of notifications, including iOS push notifications, Android push notifications, SMS messages, and emails.

### Notification Types
The design should accommodate the following notification types:
- **iOS Push Notifications**: Sent through Apple Push Notification Service (APNS).
- **Android Push Notifications**: Sent through Firebase Cloud Messaging (FCM).
- **SMS Messages**: Sent through third-party services like Twilio or Nexmo.
- **Emails**: Sent through services like SendGrid or Mailchimp.

**iOS Push Notification**:
- **Provider**: Sends notification requests to Apple Push Notification Service (APNS).
- **Device Token**: A unique identifier for sending push notifications.
- **Payload**: A JSON dictionary that contains the notification's content.
- **APNS**: Apple's service for delivering push notifications to iOS devices.
- **iOS Device**: The end client receiving the notification.

**Android Push Notification**:
- Similar to iOS but uses Firebase Cloud Messaging (FCM) instead of APNS to send push notifications.

**SMS Messages**:
- **Third-Party Services**: Services like Twilio or Nexmo are used to send SMS messages to users.

**Email**:
- **Third-Party Services**: Services like SendGrid or Mailchimp are commonly used for sending emails, offering better delivery rates and analytics.

### **Contact Information Gathering:**
The system must collect device tokens, phone numbers, and email addresses when a user installs the app or signs up. This data is stored in a database for future use in sending notifications.

### **Notification Sending/Receiving Flow:**
The system should support sending notifications through third-party services such as:
- **APNS** for iOS push notifications.
- **FCM** for Android push notifications.
- **Twilio** for SMS messages.
- **SendGrid** for emails.



# High Level Design Components


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/woc4p4hljn9foh4nvmr2.png)


The high-level design includes several key components:
**Service N**: 
   - A source service (e.g., a billing service) that triggers notifications, such as reminding customers of due payments.

**Kafka**: Handles all notification requests asynchronously.

**Notification Validator / Prioritizer**: Validates incoming notifications and prioritizes them based on predefined criteria before processing.

**Notification Servers**:
   - Handles requests from various services, ensuring they are authenticated, rate-limited, and processed.

**Authentication**:
   - Ensures that requests to send notifications are from authorized sources by verifying credentials or tokens.

**Rate Limiting**:
   - Controls the frequency of notifications to prevent spamming and system overload.

**Cache**:
   - Temporarily stores frequently accessed data, reducing the load on the database.

**Database (DB)**:
   - Stores persistent data, such as device settings, user information, and notification logs.

**Device Setting & User Info**:
   - Contains user-specific settings and information that determine notification preferences.

**iOS Push Notification Module**:
   - Manages the formatting and sending of push notifications to iOS devices.

**Workers**:
   - Background processing units that handle sending notifications, logging, and retries.

**APNs (Apple Push Notification Service)**:
    - Service provided by Apple for delivering push notifications to iOS devices.

**Notification Template**:
    - Stores predefined formats for notifications to standardize content.

**Notification Log**:
    - Keeps a record of all notifications sent for tracking and auditing.

**Analytics Service**:
    - Monitors the performance and effectiveness of notifications, tracking metrics like delivery success rates and user engagement.

# Workflow Explanation




**Triggering the Notification**:
   - Service N sends a notification request to the Notification Servers.

**Authentication and Rate Limiting**:
   - The request is authenticated and then goes through a rate limit check.

**Data Retrieval and Caching**:
   - Necessary data is retrieved from the cache or database.

**Notification Preparation**:
   - Workers use templates to prepare the notification content.

**Sending the Notification**:
   - The notification is formatted and sent through APNs or the appropriate channel.

**Logging and Analytics**:
   - The notification is logged, and its status is analyzed for performance metrics.

**Retry Mechanism**:
   - If sending fails, the Workers retry to ensure delivery.

**Click Tracking**:
   - If applicable, clicks are tracked and sent to the Analytics Service to measure user engagement.

This design ensures reliable, efficient, and scalable notification delivery, addressing potential issues like single points of failure, scalability challenges, and performance bottlenecks.

# Deep Design Concepts

## **Decoupling with Message Queues**
   - **Introduction of Message Queues**: 
     - Implement message queues (e.g., RabbitMQ, Kafka) to decouple the notification generation from sending. This allows services to push notification requests into a queue without waiting for immediate delivery.
     - Consumers (e.g., notification workers) can process these messages asynchronously, which helps in scaling out processing and managing spikes in traffic effectively.

   - **Benefits**:
- Asynchronous Processing: Reduces the response time for services that generate notifications, allowing them to operate independently.
- Scalability: Workers can be scaled horizontally to handle increased loads by simply adding more instances.

- Fault Tolerance: In case of failures in sending notifications, messages remain in the queue for retrying.

## **Database and Cache Separation**
   - **Independent Database and Cache**:
     - Move the database and cache out of the notification server to dedicated services. Use a distributed cache like Redis or Memcached to store frequently accessed data.
     - Ensure the database handles user settings, notification logs, and templates, while the cache provides quick access to device tokens and user preferences.

   - **Benefits**:
     - Reduced resource contention between the notification processing and data storage layers.

     - Scalability, as the database can be independently scaled based on demand without impacting notification delivery.

## **Horizontal Scaling of Notification Servers**
   - **Multiple Notification Servers**:
     - Implement multiple instances of notification servers that can handle incoming requests from various services. Use a load balancer to distribute traffic evenly across these servers.
     - Configure automatic horizontal scaling to dynamically adjust the number of active notification servers based on system load, ensuring optimal performance during peak times.

- **Benefits**:

     - Elimination of the single point of failure (SPOF), enhancing system reliability.
     - Improved throughput, allowing the system to handle more notifications concurrently.

## **Reliability and Data Loss Prevention**
   - **Persistent Notification Storage**:
     - Store all notifications in a durable database before sending. This ensures that notifications can be retried even after failures.
     - Implement a robust retry mechanism with exponential backoff to handle transient failures while limiting repeated attempts to avoid flooding the external services.

   - **Dedupe Mechanism**:
     - Introduce a deduplication strategy that checks for duplicate notifications based on user ID, notification type, and timestamp, to minimize redundancy.

   - **Monitoring and Alerts**:
     - Monitor notification delivery success rates and trigger alerts for persistent failures. Use logging to keep track of retry attempts and outcomes for better diagnostics.

## **Notification Templates and User Preferences**
   - **Dynamic Notification Templates**:
     - Support a templating engine that allows for the dynamic generation of notifications. This could include variables for user personalization and specific content based on notification types.

   - **User Notification Settings**:
     - Allow users to manage their preferences via a user interface or API, enabling them to opt-in or opt-out of specific notification types. Store these preferences in the database, and ensure that they are respected during the notification sending process.

## **Analytics and Performance Monitoring**
   - **Analytics Service**:
     - Implement an analytics service to track the effectiveness of notifications. Key metrics include delivery rates, open rates, click-through rates, and engagement over time.
     - Use this data to optimize notification content and timing based on user behavior and preferences.

   - **Monitoring Dashboard**:
     - Create a monitoring dashboard for real-time visibility into system performance, including the number of queued notifications, processing times, and failure rates.

## **API Improvements**

**API Versioning**: Implement versioning for your APIs to ensure backward compatibility as new features are added.

**Error Handling**: Enhance error handling to provide more informative responses, which will help clients understand what went wrong and how to correct it.

**Rate Limiting**: Use API Gateway tools (e.g., AWS API Gateway) to enforce rate limiting at the API level, preventing abuse and ensuring fair use across all clients.

## **Security Considerations**

**Authentication and Authorization**: Implement OAuth2.0 or JWT for securing API endpoints to ensure only authorized services can send notifications.

**Data Encryption**: Use encryption (e.g., TLS) for data in transit and at rest to protect sensitive user information.

## **Testing and Quality Assurance**

**Load Testing**: Conduct load testing to ensure the system can handle the expected volume of notifications and identify bottlenecks.

**Automated Testing**: Implement unit and integration tests for APIs and notification sending logic to catch issues early in the development process.

### Summary of Optimizations
- **Scalability**: By decoupling components and allowing for horizontal scaling, the system can effectively manage increased workloads and adapt to varying traffic patterns.
- **Reliability**: With a focus on data persistence and retry mechanisms, the system ensures notifications are not lost and can be reliably delivered to users.
- **User-Centric Design**: Providing options for users to manage their notification preferences enhances user experience and engagement.
- **Data-Driven Decisions**: Incorporating analytics enables the continuous improvement of notification strategies based on user behavior and feedback.

These enhancements will create a robust, flexible, and efficient notification system capable of handling millions of notifications daily while ensuring reliability and user satisfaction.


# Low Level Design of API Database Diagram for Notification System

```js
// Models
const mongoose = require('mongoose');

const deviceSchema = new mongoose.Schema({
    device_id: String,
    device_type: String,
    device_token: String,
});

const userSchema = new mongoose.Schema({
    user_id: { type: String, unique: true, required: true },
    name: String,
    email: { type: String, unique: true, required: true },
    country_code: String,
    phone_number: String,
    device: deviceSchema,
    created_at: { type: Date, default: Date.now },
    last_logged_in: Date,
    notification_preferences: {
        PUSH: { type: Boolean, default: true },
        SMS: { type: Boolean, default: true },
        EMAIL: { type: Boolean, default: true }
    }
});

const templateSchema = new mongoose.Schema({
    template_id: { type: String, unique: true, required: true },
    template_content: String, // e.g., "Hello <name>, your order <order_id> is confirmed."
    notification_type: String, // PUSH, SMS, EMAIL
});

const notificationSchema = new mongoose.Schema({
    notification_id: { type: String, unique: true, required: true },
    user_id: { type: String, required: true },
    notification_type: String, // PUSH, SMS, EMAIL
    status: { type: String, default: 'pending' }, // pending, delivered, failed
    delivered_at: Date,
    created_at: { type: Date, default: Date.now }
});

module.exports = {
    User: mongoose.model('User', userSchema),
    Template: mongoose.model('Template', templateSchema),
    Notification: mongoose.model('Notification', notificationSchema),
};
```

```js

// API implementation

const { User, Template, Notification } = require('./models');

// User Registration API
app.post('/api/v1/register', async (req, res) => {
    try {
        const user = new User(req.body);
        await user.save();
        res.status(201).json({ status: 'success', message: 'User registered successfully' });
    } catch (error) {
        res.status(400).json({ status: 'error', message: error.message });
    }
});

// Send Notification API
app.post('/api/v1/sendNotification', async (req, res) => {
    try {
        const { user_id, notification_type, template_id, data } = req.body;

        // Retrieve the user from the database
        const user = await User.findOne({ user_id });
        if (!user) {
            return res.status(404).json({ status: 'error', message: 'User not found' });
        }

        // Retrieve the template from the database
        const template = await Template.findOne({ template_id, notification_type });
        if (!template) {
            return res.status(404).json({ status: 'error', message: 'Template not found' });
        }

        // Prepare the notification message by replacing placeholders with actual data
        let notificationMessage = template.template_content;
        for (const key in data) {
            notificationMessage = notificationMessage.replace(`<${key}>`, data[key]);
        }

        // Example: Send notification based on type
        if (notification_type === 'PUSH') {
            await sendPushNotification(user.device.device_token, notificationMessage);
        } else if (notification_type === 'SMS') {
            await sendSMSNotification(user.phone_number, notificationMessage);
        } else if (notification_type === 'EMAIL') {
            await sendEmailNotification(user.email, notificationMessage);
        }

        res.json({ status: 'success', message: 'Notification sent successfully' });
        // Update the notification table with the notification details
    } catch (error) {
        res.status(500).json({ status: 'error', message: error.message });
    }
});

// Function to send Push Notifications
async function sendPushNotification(deviceToken, message) {
    const payload = {
        notification: {
            title: 'New Notification',
            body: message,
        },
    };
    await admin.messaging().sendToDevice(deviceToken, payload);
}

// Function to send SMS Notifications (Placeholder)
async function sendSMSNotification(phoneNumber, message) {
    // Use Twilio or any SMS service provider to send SMS
    console.log(`Sending SMS to ${phoneNumber}: ${message}`);
}

// Function to send Email Notifications (Placeholder)
async function sendEmailNotification(email, message) {
    // Use SendGrid, Nodemailer, Mailchimp or any email service provider to send emails
    console.log(`Sending email to ${email}: ${message}`);
}

// Update Device Token API
app.put('/api/v1/updateDeviceToken', async (req, res) => {
    try {
        const { user_id, device_id, device_token } = req.body;
        const user = await User.findOneAndUpdate(
            { user_id, 'device.device_id': device_id },
            { 'device.device_token': device_token },
            { new: true }
        );
        res.json({ status: 'success', message: 'Device token updated successfully' });
    } catch (error) {
        res.status(400).json({ status: 'error', message: error.message });
    }
});

// Opt-Out API
app.post('/api/v1/optOut', async (req, res) => {
    try {
        const { user_id, notification_type } = req.body;

        // Validate input
        if (!['PUSH', 'SMS', 'EMAIL'].includes(notification_type)) {
            return res.status(400).json({ status: 'error', message: 'Invalid notification type' });
        }

        // Find the user and update their notification preferences
        const user = await User.findOneAndUpdate(
            { user_id },
            { $set: { [`notification_preferences.${notification_type}`]: false } },
            { new: true }
        );

        if (!user) {
            return res.status(404).json({ status: 'error', message: 'User not found' });
        }

        res.json({ status: 'success', message: `User opted out of ${notification_type} notifications successfully` });
    } catch (error) {
        res.status(500).json({ status: 'error', message: error.message });
    }
});

// Get Notification Status API
app.get('/api/v1/notificationStatus', async (req, res) => {
    try {
        const { notification_id } = req.query;

        // Find the notification in the database
        const notification = await Notification.findOne({ notification_id });

        if (!notification) {
            return res.status(404).json({ status: 'error', message: 'Notification not found' });
        }

        res.json({
            notification_id: notification.notification_id,
            status: notification.status,
            delivered_at: notification.delivered_at || null,
        });
    } catch (error) {
        res.status(500).json({ status: 'error', message: error.message });
    }
});

```

# **Database Schema**

## **Users Table**
This table stores user information and their notification preferences.

| Column                  | Data Type | Constraints              | Description                                      |
|-------------------------|-----------|--------------------------|--------------------------------------------------|
| `user_id`               | `VARCHAR` | `PRIMARY KEY, UNIQUE, NOT NULL` | Unique identifier for the user.                   |
| `name`                  | `VARCHAR` | `NOT NULL`               | The name of the user.                            |
| `email`                 | `VARCHAR` | `UNIQUE, NOT NULL`       | The email address of the user.                   |
| `country_code`          | `VARCHAR` |                          | The country code of the user's phone number.     |
| `phone_number`          | `VARCHAR` |                          | The phone number of the user.                    |
| `device_id`             | `VARCHAR` |                          | Unique identifier for the user's device.         |
| `device_type`           | `VARCHAR` |                          | Type of the device (e.g., Android, iOS).         |
| `device_token`          | `VARCHAR` |                          | Token used for sending push notifications.       |
| `created_at`            | `DATETIME`| `DEFAULT CURRENT_TIMESTAMP` | Timestamp when the user was created.             |
| `last_logged_in`        | `DATETIME`|                          | Timestamp when the user last logged in.          |
| `notification_preferences_PUSH` | `BOOLEAN` | `DEFAULT TRUE`       | Indicates if the user has opted in for PUSH notifications. |
| `notification_preferences_SMS`  | `BOOLEAN` | `DEFAULT TRUE`       | Indicates if the user has opted in for SMS notifications.  |
| `notification_preferences_EMAIL`| `BOOLEAN` | `DEFAULT TRUE`       | Indicates if the user has opted in for Email notifications. |

## **templates Table**
This table stores notification templates for various notification types.

| Column             | Data Type | Constraints              | Description                                      |
|--------------------|-----------|--------------------------|--------------------------------------------------|
| `template_id`      | `VARCHAR` | `PRIMARY KEY, UNIQUE, NOT NULL` | Unique identifier for the template.               |
| `template_content` | `TEXT`    | `NOT NULL`               | Template content with placeholders (e.g., `{{name}}`). |
| `notification_type`| `VARCHAR` | `NOT NULL`               | Type of the notification (e.g., PUSH, SMS, EMAIL).|

## **notifications Table**
This table logs notifications sent to users.

| Column             | Data Type | Constraints              | Description                                      |
|--------------------|-----------|--------------------------|--------------------------------------------------|
| `notification_id`  | `VARCHAR` | `PRIMARY KEY, UNIQUE, NOT NULL` | Unique identifier for the notification.          |
| `user_id`          | `VARCHAR` | `NOT NULL`               | Foreign key referencing the `users` table.       |
| `notification_type`| `VARCHAR` | `NOT NULL`               | Type of the notification (e.g., PUSH, SMS, EMAIL).|
| `status`           | `VARCHAR` | `DEFAULT 'pending'`      | Status of the notification (e.g., pending, delivered, failed). |
| `delivered_at`     | `DATETIME`|                          | Timestamp when the notification was delivered.   |
| `created_at`       | `DATETIME`| `DEFAULT CURRENT_TIMESTAMP` | Timestamp when the notification was created.     |


# Conclusion
This blog post provides a comprehensive overview of the design, storage estimates, workflow, and database schema for a notification system. With careful planning and consideration of both functional and non-functional requirements, the system can effectively serve users while maintaining scalability and reliability. Let me know if you need any more details or specific adjustments!

---

# UseCases

## **Use Case Send Push Notification for Product Availability**
To incorporate a use case for sending push notifications when a product is back in stock into your notification system, we can follow these steps:

### Use Case Description
When a user opts to receive notifications for a specific product, the system will send a push notification to the user's device when the product becomes available in stock. 

### Actors
- **User**: The individual who subscribes to receive notifications about product availability.
- **Notification System**: The system responsible for managing notifications.

### Preconditions
- The user must have registered an account in the system.
- The user must have opted in to receive push notifications.
- The product must be available in the database.

### Postconditions
- The user receives a push notification informing them that the product is back in stock.

### Example Flow
1. **User Action**: The user browses the product catalog and finds a product that is currently out of stock. They click on the “Notify Me” button for that product.
  
2. **User Opt-In**: The system records the user's request to be notified when the product is back in stock, storing the user ID, product ID, and notification type (PUSH) in the database.

3. **Stock Update**: The inventory management system updates the product's stock status, indicating that the product is now available.

4. **Notification Trigger**: The notification system checks for users who have opted in for notifications related to the updated product.

5. **Send Push Notification**:
   - The system generates a push notification message, e.g., "Great news! The product XYZ is back in stock. Click here to purchase."
   - The notification is sent to the user's device via a push notification service (e.g., Firebase Cloud Messaging, Apple Push Notification Service).

6. **User Receives Notification**: The user receives the push notification on their device.

7. **User Action (Optional)**: The user clicks on the notification, which opens the product page for them to make a purchase.

### API Endpoints

To implement this use case, you may need to define or update the following API endpoints:

1. **Opt-In for Notifications**:
   - **Endpoint**: `POST /notifications/subscribe`
   - **Request Body**:

     ```json
     {
       "userId": "user123",
       "productId": "product456",
       "notificationType": "PUSH"
     }
     ```


3. **Send Push Notification** (Triggered by the system upon stock update):
   - **Internal Process**: This can be a background job that runs after the stock update.


**Summary**
This use case allows users to be actively informed when a product they are interested in becomes available, enhancing user engagement and potentially increasing sales. The use of push notifications provides a direct and immediate way to communicate with users.

---


## Use Case Prioritize OTP Notifications Over Normal Notifications

### Use Case Description
When a user requests an OTP (for purposes such as two-factor authentication or account verification), the system will ensure that the OTP notification is sent immediately and takes precedence over any other normal notifications that may be queued for the user.

### Actors
- **User**: The individual requesting the OTP for verification or authentication.
- **Notification System**: The system responsible for managing notifications and ensuring proper prioritization.

### Preconditions
- The user must have a registered account and opted in to receive notifications (both normal and OTP).
- The user must request an OTP for authentication or verification.
- The system must have the ability to send both normal and OTP notifications.

### Postconditions
- The user receives the OTP notification promptly, regardless of any other queued notifications.

### Example Flow
1. **User Action**: The user tries to log in to their account and selects the option for two-factor authentication, prompting the system to generate an OTP.

2. **Request OTP**: The user submits their request for an OTP, triggering the following actions:
   - The system generates an OTP and stores it securely (e.g., in a database or cache) with an expiration time.
   - The system initiates the process to send the OTP notification to the user's device.

3. **Normal Notifications Queued**: At this time, there may be normal notifications queued for the user (e.g., product back-in-stock alerts or marketing messages).

4. **Priority Check**:
   - The notification system checks the type of notification being sent.
   - Since the OTP is a high-priority notification, it takes precedence over normal notifications.

5. **Send OTP Notification**:
   - The system generates a push or SMS notification message, e.g., "Your OTP is 123456. It is valid for 5 minutes."
   - The OTP notification is sent to the user's device immediately.

6. **User Receives OTP**: The user receives the OTP notification on their device.

7. **Normal Notifications Management**: Any normal notifications that were queued while the OTP was being processed can either:
   - Be sent after the OTP notification is delivered, or 
   - Be temporarily paused until the OTP is sent, depending on the implementation strategy.

8. **User Action (Using OTP)**: The user enters the received OTP into the login interface to complete the authentication process.

### API Endpoints

To implement this use case, you may need to define or update the following API endpoints:

1. **Request OTP**:
   - **Endpoint**: `POST /auth/request-otp`
   - **Request Body**:
     ```json
     {
       "userId": "user123",
       "channel": "PUSH" // or SMS
     }
     ```

2. **Send OTP Notification**:
   - **Internal Process**: The system processes the OTP request and sends the OTP notification as a high-priority message.

3. **Manage Normal Notifications**:
   - **Endpoint**: `POST /notifications/send`
   - **Request Body** (for normal notifications):
     ```json
     {
       "userId": "user123",
       "message": "Your OTP for XYZ is 12345."
     }
     ```

### Database Diagram Update

You might want to update your database diagram to reflect the handling of notification priorities:

- **Notifications Table**:
  - **notification_id** (PK)
  - **user_id** (FK)
  - **notification_type** (normal, OTP)
  - **status** (pending, sent)
  - **created_at**
  - **priority** (high, low) -- Added to handle notification Priority

**Summary**
This use case emphasizes the critical nature of OTP notifications, which often relate to security and authentication processes. By prioritizing OTP notifications, the system enhances the user experience, ensuring that important security messages are delivered promptly without being delayed by less urgent notifications. This prioritization strategy can be crucial in maintaining user trust and system security.


---

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli