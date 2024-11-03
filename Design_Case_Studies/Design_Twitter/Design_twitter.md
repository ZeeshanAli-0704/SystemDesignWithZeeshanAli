Here is a Table of Contents (TOC) with links to navigate within the document:

## **Table of Contents**

1. [What is Twitter?](#what-is-twitter)
2. [System Design Requirements for Twitter](#system-design-requirements-for-twitter)  
   - [Functional Requirements](#functional-requirements)  
   - [Non-Functional Requirements](#non-functional-requirements)  
   - [Extended Requirements](#extended-requirements)
3. [Capacity Traffic and Bandwidth Estimation](#capacity-traffic-and-bandwidth-estimation)  
   - [Traffic Estimation](#traffic-estimation)  
   - [Storage Estimation](#storage-estimation)  
   - [Bandwidth Estimation](#bandwidth)
4. [Basic Flow Diagram Description](#basic-flow-diagram-description)
5. [Low-Level Design for Twitter System Design](#low-level-design-for-twitter-system-design)  
   - [Data Storage](#data-storage)  
   - [Core Functionalities](#core-functionalities)
6. [High-Level Design: Microservices Architecture for Twitter](#high-level-design-microservices-architecture-for-twitter)  
   - [User Service](#user-service)  
   - [Newsfeed Service](#newsfeed-service)  
     - [Feed Generation](#feed-generation)  
     - [Feed Publishing](#feed-publishing)
   - [Tweet Service](#tweet-service)
   - [Retweets Service](#retweets-service)
   - [Retweets Service](#retweets-service)


Now, each section can be easily accessed by clicking the links above. Let me know if you need further adjustments!

# **Designing Twitter – A System Design Interview Question**

## **What is Twitter?**

Twitter is a social media platform where users can post or read short messages, called tweets, limited to 280 characters. It is accessible via web and mobile platforms, such as Android and iOS.

---

## **System Design Requirements for Twitter**

### **Functional Requirements:**
**Post Tweets**: Users should be able to post new tweets (text, images, videos, etc.).
**Follow Users**: Users should be able to follow other accounts.
**Newsfeed**: Users should have a newsfeed showing tweets from accounts they follow.
**Search Tweets**: Users should be able to search for tweets based on trending hashtags

###  **Non-Functional Requirements:**

1. **High Availability**: The system must be highly available with minimal latency.
2. **Eventual Consistency**: The system can tolerate eventual consistency.
3. **Scalability**: The system must efficiently scale to accommodate high traffic.
 
### **Extended Requirements:**

- **Metrics and Analytics**: For tracking user engagement and system health.
- **Retweet Functionality**: Users can share others' tweets.
- **Favorite Tweets**: Users can like tweets.

---

## **Capacity Traffic and Bandwidth Estimation**:

### **Traffic Estimation**:

- **Total Users**: 1 billion users.
- **Daily Active Users (DAU)**: 200 million.
- **Tweets per DAU**: 5 tweets per day.

Thus, there are **1 billion tweets per day** (200 million DAU * 5 tweets).

 **Media Files Estimation**:

- **Media Tweets**: 10% of the tweets contain media (images/videos). Therefore, there will be **100 million media files** per day.

 **Request Estimation**:

The **requests per second (RPS)** can be calculated based on the number of daily tweets and media file uploads.

 **Write Requests (Tweets)**:

- **Tweets per day**: 1 billion.
- **RPS for Tweets**: The system must handle around **11,574 write requests/second**.
  -  1 billion tweets/day24 hoursx 3600 seconds/hour = approx 11,574 write requests/second).

 **Write Requests (Media Files)**:

- **Media files per day**: 100 million media uploads.
- **RPS for Media Files**: The system must handle around **1,157 media write requests/second**.
  -  100 million media/day24 hoursx 3600 seconds/hour = approx 1,157 write requests/second).

Therefore, the total write RPS is:
- **Total Write RPS** = **11,574** (tweets) + **1,157** (media) = **12,731 write requests/second**.

 **Read Requests (Tweet Retrieval)**:

To estimate read operations, we consider the number of users retrieving tweets:

- **DAU**: 200 million users.
- **Tweets read per DAU**: Assume each user reads 50 tweets per day (from their feed, trending, or search).

 **Read Requests per Second**:

- **Total reads per day** = 200 million users * 50 tweets/user = 10 billion tweet reads per day.
- **RPS for reads** =  10 billion reads/day24 hoursx 3600 seconds/hour = approx 115,740 read requests/second).

Thus, the system must handle approximately **115,740 read requests per second**.


### **Storage Estimation**:

 **Tweet Storage**:

- **Tweets**: Assuming each tweet is 100 bytes, the system needs **100 GB/day**:
  -  1 billion tweets/dayx 100 bytes= 100 GB/day).

 **Media Files Storage**:

- **Media Files**: Assuming each media file is 50 KB, the system requires **5 TB/day** for media storage:
  -  100 million media files/dayx 50 KB= 5 TB/day).

 **Long-term Storage**:

- **Total storage** over 10 years would be approximately **19 PB** (assuming tweets and media are retained):
  -  (5 TB/day for media + 0.1 TB/day for tweets) x 365 days/yearx 10 years= approx 19 PB).

 **Summary**:

- **Write RPS**: ~12.7K requests/second (tweets + media).
- **Read RPS**: ~115.7K requests/second.
- **Storage**: 100 GB/day for tweets and 5 TB/day for media, leading to ~19 PB over 10 years.


### **Bandwidth**:
To estimate the **bandwidth** required for handling the system’s traffic, we need to consider both **write (upload)** and **read (download)** operations.

 **Write Bandwidth Estimation**:
This refers to the bandwidth required to handle **uploading** tweets and media files.

 **Tweets**:
- **Number of tweets/day**: 1 billion.
- **Size of each tweet**: 100 bytes (on average).
- **Total data per day for tweets**: 
  -  1 billion tweetsx 100 bytes/tweet= 100 GB/day).

- **Bandwidth required for tweets (in bytes/second)**:
  -  100 GB/day 24 hoursx 3600 seconds/hour = approx 1.16 MB/second).

 **Media Files**:
- **Number of media files/day**: 100 million.
- **Size of each media file**: 50 KB (on average).
- **Total data per day for media files**:
  -  100 million filesx 50 KB= 5 TB/day).

- **Bandwidth required for media (in bytes/second)**:
  -  5 TB/day 24 hoursx 3600 seconds/hour = approx 60 MB/second).

 **Total Write Bandwidth**:
- **Total write bandwidth** = 1.16 MB/second (tweets) + 60 MB/second (media) = **61.16 MB/second**.

This is the average bandwidth required for **writing data** to the system (uploading tweets and media).


 **Read Bandwidth Estimation**:
This refers to the bandwidth required to handle **downloading** tweets and media files.

 **Tweet Reads**:
- **Number of tweets read/day**: 10 billion.
- **Size of each tweet**: 100 bytes.
- **Total data per day for tweet reads**:
  -  10 billion tweetsx 100 bytes/tweet= 1 TB/day).

- **Bandwidth required for tweet reads (in bytes/second)**:
  -  1 TB/day24 hoursx 3600 seconds/hour = approx 12 MB/second).

 **Media Files Reads**:
- **Assume** that 50% of users (100 million DAU) download media, and each user downloads 5 media files.
- **Total media files downloaded/day**:  100 million usersx 5 files= 500 million media files).
- **Size of each media file**: 50 KB.
- **Total data per day for media downloads**:
  -  500 million filesx 50 KB= 25 TB/day).

- **Bandwidth required for media downloads (in bytes/second)**:
  -  25 TB/day24 hoursx 3600 seconds/hour = approx 300 MB/second).

 **Total Read Bandwidth**:
- **Total read bandwidth** = 12 MB/second (tweets) + 300 MB/second (media) = **312 MB/second**.

---

 **Total Bandwidth (Write + Read)**:
- **Total bandwidth** = **61.16 MB/second** (write) + **312 MB/second** (read) = **373.16 MB/second**.

This is the **average bandwidth** the system needs to handle **both read and write operations**. For practical purposes, bandwidth capacity should be higher than this estimate to accommodate peak loads and traffic spikes.

This analysis provides a clear picture of the system's capacity and storage requirements based on traffic, media usage, and long-term retention.


Add Diagam here

Detailed Flow for Twitter System Design

---

## **Basic Flow Diagram Description**

In the system design, the user experience can be broken down into key navigational elements and interactions:

**Main Navigation**:
   - When a user clicks on the **Twitter Page**, they are directed to the main interface that includes multiple sub-pages: 
     - **Home Page**: The primary feed where tweets from the accounts followed by the user appear.
     - **Search Page**: Users can search for topics, hashtags, and users.
     - **Notifications Page**: Users are notified about new followers, likes, retweets, or comments on their tweets.

**Home Page Breakdown**:
   - The **Home Page** will provide users with the ability to:
     - View the timeline of tweets from the users they follow.
     - Create new tweets via a **New Tweet Page**, which includes options to post text, images, or videos.

**Search Page**:
- Searching module where user can search for trending post via hastags etc.

**Notification Page**:
- list of all notification will be displayed
   
**Tweet Interactions**:
   - Each tweet on the timeline includes actions such as:
     - **Like** and **Dislike**.
     - **Comments** section where users can respond to tweets.
     - **Follow/Unfollow** buttons to control whether the user wants to receive updates from the tweet's author.
   
**Guest and Registered Users**:
   - **Guest Users**:
     - Can only view tweets; they have limited access and cannot post or interact.
   - **Registered Users**:
     - Can post, like, comment, follow, or unfollow other users.
     - Registered users have the ability to create new tweets and engage in social interactions within the platform.

---

## **Low-Level Design for Twitter System Design**

To design the internal components of the Twitter-like system, we'll focus on the key aspects: **Data Storage**, **Core Functionalities**.

### **Data Storage**:

**User Accounts**:
   - Each user’s data will be stored in a relational database such as **MySQL** or **PostgreSQL**.
   - Key attributes to store include:
     - **Username**: Unique identifier for the user.
     - **Email**: For login and notification purposes.
     - **Password (hashed)**: Use algorithms like bcrypt to securely store hashed passwords.
     - **Profile Picture**: Store a reference to the image file (stored externally).
     - **Bio**: A short description provided by the user.

**Tweets**:
   - Tweets are stored in a separate table to efficiently handle large volumes of user-generated content.
   - Attributes include:
     - **Tweet ID**: Unique identifier for the tweet.
     - **Content**: The text of the tweet.
     - **Author ID**: The user ID of the tweet's author.
     - **Timestamp**: Date and time when the tweet was posted.
     - **Hashtags and Mentions**: Store associations for hashtag and user mentions.
     - **Replies**: If the tweet is a reply, this will store the reference to the parent tweet.

**Follow Relationships**:
   - The **Follow/Unfollow** relationships will be stored in a dedicated table to efficiently manage and retrieve connections between users.
     - **Follower ID**: The user who follows another.
     - **Followee ID**: The user who is being followed.
     - **Timestamp**: The time when the follow action took place.

**Media Storage**:
   - Media files like images and videos uploaded by users will be stored externally in a service such as **Amazon S3** or **Google Cloud Storage**.
   - In the database, references (URLs) to these media files will be stored, linked to the respective tweets.

---

### **Core Functionalities**:

**Posting a Tweet**:
   - When a user posts a tweet:
     - **Server-side validation** checks the tweet's content, ensuring it complies with length limits (e.g., 280 characters) and handles any attached media.
     - **Store the tweet** in the **Tweets** table with all metadata (author ID, content, timestamp, media references, etc.).
     - Notify the user’s followers in **real-time** using WebSockets or push notifications.

**Timeline Generation**:
   - The timeline consists of tweets from followed users and relevant hashtags.
     - Retrieve the list of users and hashtags followed by the logged-in user.
     - **Fetch recent tweets** from these users and hashtags from the **Tweets** table.
     - Apply an **algorithm** to rank the tweets based on:
       - **Recency**: Newer tweets are given higher priority.
       - **Relevance**: Tweets with more engagement (likes, retweets, etc.) may rank higher.
     - To improve performance, timelines are **cached** in systems like **Redis**, especially for users with many followers.

**Search**:
   - The search function allows users to look up tweets, hashtags, or users.
     - Use **full-text indexing** in the database or integrate with **Elasticsearch** for optimized search.
     - Index tweets based on **keywords, hashtags**, and **mentions**.
     - Use a ranking system that considers relevance and recency to deliver meaningful search results quickly.

**Follow/Unfollow**:
   - When a user follows or unfollows another user:
     - The system updates the **Follow Relationships** table, which will allow real-time adjustments to the follower’s timeline.
     - The system may trigger notifications to inform users about new followers.
     - **Timeline updates** dynamically reflect the change in follow/unfollow actions to include or exclude the respective user’s tweets.

## High-Level Design: Microservices Architecture for Twitter

In this architecture, Twitter's services are divided into independently scalable microservices. This approach allows for horizontal scaling and easy decoupling, enabling each service to manage its data model and functionality. Let's explore each of the core services:

### **User Service**
This service manages all user-related operations such as:
- **Authentication**: Handling login, signup, and authorization.
- **Profile Management**: Enabling users to update their profile information (e.g., name, bio, profile picture).
- **User Preferences**: Managing notification settings and other user preferences.

### **Newsfeed Service**
The Newsfeed Service is responsible for generating and delivering personalized content (tweets) to users. It consists of two core processes: **feed generation** and **feed publishing**.

#### **Feed Generation**

To generate a user's feed (e.g., for User A), we perform the following steps:
1. **Retrieve User Data**: Get the IDs of all users, hashtags, and entities (topics) that User A follows.
2. **Apply Ranking Algorithm**: Rank tweets based on parameters such as relevance, recency, engagement (likes, comments, retweets), and user affinity.
3. **Paginate Results**: Return ranked tweets to the client in a paginated format to enhance performance and avoid overwhelming the user.

Generating a newsfeed is resource-intensive, particularly for users who follow many accounts. To improve performance:
- **Pre-generation**: Pre-generate the feed and store it in a cache.
- **Incremental Updates**: Periodically update the feed with newly published tweets using the ranking algorithm.

#### **Feed Publishing**

Publishing refers to pushing the generated feed to the user's timeline. This process can become heavy as users with millions of followers (e.g., celebrities) would require millions of pushes. There are three models to handle this:

**Pull Model (Fan-out on load)**:
   - Tweets are fetched from the server only when users request to see their newsfeed.
   - **Pros**: Reduces write operations on the database.
   - **Cons**: Increases read operations, and users may experience delays when fetching recent tweets.

**Push Model (Fan-out on write)**:
   - Tweets are pushed to the followers' feeds immediately when posted.
   - **Pros**: Users see updates instantly.
   - **Cons**: Increases write operations significantly, especially for users with millions of followers.
 
**Hybrid Model**:
   - This approach balances the push and pull models.
   - **Mechanism**: Use push for users with a smaller follower base and pull for users with large follower counts. For example, celebrities can use the pull model to reduce the system load.

### **Tweet Service**
This service manages tweet-related operations, including:
- **Posting**: Allows users to create and post tweets.
- **Retweeting**: Retweets are treated as new tweets with a reference to the original tweet and the retweeting user's ID.
- **Favoriting and Replying**: Handles likes and comment threads under tweets.

### **Retweets Service**
To implement retweets:
- A retweet creates a new tweet in the database.
- It links the original tweet via an enum field (e.g., `TWEET_TYPE = RETWEET`).
- This design allows easy retrieval and display of retweets.

### **Search Service**
This service enables users to search for tweets by keywords, hashtags, or topics. Since traditional databases might struggle with large-scale searches, **Elasticsearch** is employed due to its ability to handle:
- **Real-time search**: It processes massive amounts of data quickly and provides results in milliseconds.
- **Distributed architecture**: Elasticsearch distributes data across clusters for horizontal scalability.
  
### **Ranking Algorithm**
The ranking of tweets is essential to optimize the feed. Here’s an example inspired by Facebook’s EdgeRank algorithm:

 Rank = Affinity X Weight X Decay 

- **Affinity**: Measures the closeness between users (e.g., how often they interact).
- **Weight**: Assigns higher values to certain interactions (e.g., comments > likes).
- **Decay**: Penalizes older content, giving priority to fresher content.

Today, advanced machine learning models are used to consider thousands of ranking factors, improving user engagement by tailoring the feed to individual preferences.

### **Notification Service**
Notifications are crucial for user engagement. This service leverages message brokers such as **Apache Kafka** to handle dispatch requests. Notifications are sent using:
- **Firebase Cloud Messaging (FCM)**: For Android devices.
- **Apple Push Notification Service (APNS)**: For iOS devices.

The service ensures that users are alerted about new interactions such as mentions, replies, or retweets.

### **Media Service**
The Media Service handles:
- **Uploading**: Users can upload images, videos, and other media formats.
- **Storage**: Media is stored in cloud-based storage systems like AWS S3 or Google Cloud Storage, optimized for large-scale file management.

### **Analytics Service**
This service collects and analyzes data related to system performance and user behavior. It tracks metrics like:
- Tweet counts (daily/second).
- Timeline delivery performance.
- Latency in user requests (e.g., fetching tweets).

---

## **Sharding and Data Distribution**
To efficiently manage the massive data (tweets, user actions), we must distribute it across multiple machines using **sharding**. Various sharding techniques include:

**Sharding by UserID**: All user-related data is stored in one server based on their hashed UserID. 
   - **Issues**: Hot users (e.g., celebrities) could overload a single server, causing performance bottlenecks.

**Sharding by TweetID**: Tweets are distributed across multiple servers using TweetID hashing. This ensures balanced read/write loads but may require querying multiple servers for a user’s timeline.

**Sharding by Tweet Creation Time**: New tweets are stored based on their creation timestamp.
   - **Problem**: Write load becomes concentrated on servers holding the latest tweets, creating an imbalance.

**Solution**: Use a combination of TweetID and creation time. This makes querying more efficient and allows for quick retrieval of recent tweets.

---

## **Caching and Scaling**
To reduce the load on database servers, we introduce caching mechanisms:
- **Memcached**: Can store hot tweets to reduce read requests to the database.
- **Cache Replacement Policy**: Implement a Least Recently Used (LRU) policy, which discards the least recently viewed tweets first.
- **Optimized Caching**: Cache the most popular (20%) tweets or recent tweets (last 3 days) to improve performance.

---

## **Load Balancing**
Load balancers are placed at multiple layers:
1. Between clients and application servers.
2. Between application servers and database servers.
3. Between aggregation servers and cache servers.

**Round Robin** is a basic load balancing approach, while more intelligent load balancing solutions can be deployed to handle server loads dynamically.

---

## **Monitoring**
Monitoring is crucial for maintaining system health and scalability. Key metrics to track include:
- New tweets per day/second.
- Timeline delivery statistics.
- Average latency for timeline refreshes.

---

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli