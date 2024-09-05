### Table of Contents

1. [System Overview](#system-overview)
2. [Requirements and Goals of the System](#requirements-and-goals-of-the-system)
   - [Functional Requirements](#functional-requirements)
   - [Non Functional Requirements](#non-functional-requirements)
3. [Storage Requirement Estimations](#storage-requirement-estimations)
   - [Post File Storage](#post-file-storage)
   - [Post Metadata Storage](#post-metadata-storage)
   - [User Data Storage](#user-data-storage)
   - [Feed Storage](#feed-storage)
   - [Caching](#caching)
   - [Server Requirements](#server-requirements)
4. [System Design](#system-design)
   - [High Level Architecture of an Instagram](#high-level-architecture-of-an-instagram)
   - [Client Layer](#client-layer)
   - [API Gateway](#api-gateway)
   - [Microservices](#microservices)
   - [Storage Layer](#storage-layer)
   - [Feed Generation](#feed-generation)
   - [Search and Discovery](#search-and-discovery)
   - [Notifications](#notifications)
   - [Content Delivery Network CDN](#content-delivery-network-cdn)
   - [Security and Privacy](#security-and-privacy)
   - [Scalability and High Availability](#scalability-and-high-availability)
   - [Monitoring and Logging](#monitoring-and-logging)
5. [Detailed Component Design Instagram](#detailed-component-design-instagram)
   - [Gateway Service Anti DDoS Layer](#gateway-service-anti-ddos-layer)
   - [Load Balancer](#load-balancer)
   - [Read and Write Servers](#read-and-write-servers)
   - [Cache](#cache)
   - [Feed Generation Service](#feed-generation-service)
   - [Notification Service](#notification-service)
   - [Post Service](#post-service)
   - [Comment Like Services](#comment-like-services)
   - [Search Service](#search-service)
   - [User Profile Service](#user-profile-service)
   - [CDN (Content Delivery Network)](#cdn-content-delivery-network)
   - [Cloud Storage](#cloud-storage)
   - [Database Replicas](#database-replicas)
   - [Kafka (Message Broker)](#kafka-message-broker)
   - [Analytics Service](#analytics-service)
   - [Redis](#redis)
   - [Monitoring Logging Tools](#monitoring-logging-tools)
   - [Asset Service](#asset-service)
   - [Detailed Overview of Tables in an Instagram](#detailed-overview-of-tables-in-an-instagram)
      - [User Table](#user-table)
      - [Post Table](#post-table)
      - [Followers Table](#followers-table)
      - [Comments Table](#comments-table)
      - [Likes Table](#likes-table)
      - [Messages Table](#messages-table)
      - [Notifications Table](#notifications-table)
      - [Feeds Table](#feeds-table)
      - [Object Storage](#object-storage)
      - [Conclusion](#conclusion)
   - [API Implementation](#api-implementation)
      - [Signup API](#signup-api)
      - [Login API](#login-api)
      - [Search User API](#search-user-api)
      - [Get User by ID API](#get-user-by-id-api)
      - [Follow User API](#follow-user-api)
      - [Add Post API](#add-post-api)
      - [Delete Post API](#delete-post-api)
      - [Get Feed API](#get-feed-api)
      - [Get User Posts API](#get-user-posts-api)
      - [Post Like API](#post-like-api)
      - [Post Unlike API](#post-unlike-api)
      - [Add Comment API](#add-comment-api)
      - [Delete Comment API](#delete-comment-api)
      - [Summary](#summary)
6. [Feed Generation](#feed-generation)
   - [Overview](#overview)
   - [Generation Approach](#generation-approach)
   - [Performance Improvements](#performance-improvements)
   - [Storage Considerations](#storage-considerations)
   - [Cache Management](#cache-management)
   - [Distribution Methods](#distribution-methods)
   - [Caching Strategy](#caching-strategy)
7. [Database Sharding](#database-sharding)
   - [Overview](#overview)
   - [Sharding Approaches](#sharding-approaches)
     - [Partitioning Based on UserID](#partitioning-based-on-userid)
     - [Partitioning Based on PhotoID](#partitioning-based-on-photoid)
8. [Feed Ranking](#feed-ranking)
   - [Ranking Features](#ranking-features)
9. [Conclusion](#conclusion)


### **System Overview**

Instagram is a widely popular social networking platform that enables users to upload, share, and interact with photos and videos. Users can follow others, like and comment on posts, and share content while accessing a personalized feed based on their interactions and follows.

### **Requirements and Goals of the System**

#### **Functional Requirements**
- **User Registration and Authentication**
  - **Sign Up**: Users can create an account using their username, email, phone number, and password.
  - **Sign In**: Users log in with their credentials.

- **Profile Management**
  - Users can create, update, and view their own and other users' profiles.

- **Photo/Video Upload**
  - Support various formats such as JPEG, PNG, and MP4, allowing users to add captions, tags, and hashtags to their posts.

- **Social Interactions**
  - Users can like, comment, share posts, follow or unfollow other users, and receive notifications for interactions.

- **Feed and Timeline**
  - A personalized feed displays recent posts from followed accounts with infinite scrolling support.

- **Search and Discovery**
  - Users can search for people, hashtags, and posts and receive recommendations for accounts to follow.

- **Direct Messaging**
  - Real-time messaging with support for media sharing between users.

- **Stories**
  - Temporary posts visible for 24 hours.

- **Push Notifications**
  - Alerts users of interactions, follows, and messages.

#### **Non Functional Requirements**
- **Scalability**: Support 1 billion users and manage 500 million daily posts.
- **Latency**: Keep feed generation latency under 200ms.
- **Availability**: Ensure high availability.
- **Reliability**: Guarantee no data loss.

### Storage Requirement Estimations
***Detailed Storage Estimation Breakdown***

#### **Post File Storage**
- **Daily Post Uploads**: 500M posts/day
- **Average Post Size**: 500 KB
  - **Daily Post File Storage**: 
    
    500 M x 500 KB = 250 TB/day
    
  - **5-Year Post File Storage**:
    
    250 TB x 365 x 5 = 456.25 PB \ (approx 500PB)
    

#### **Post Metadata Storage**
- **Average Metadata Size**: 10 KB
  - **Daily Metadata Storage**: 
    
    500 M x 10 KB = 5 GB/day
    
  - **5-Year Metadata Storage**: 
    
    5 GB x 365 x 5 = 9.13 TB \ (approx 10TB)
    

#### **User Data Storage**
- **User Base**: 1B
- **Data Size per User**: 10 KB
  - **Total User Data**:
    
    1 B x 10 KB = 10 TB
    

#### **Feed Storage**
- **Posts in Feed per User**: 50
  - **Average Post Metadata Size**: 1 KB
  - **Feed Size per User**: 
    
    50 x 1 KB = 50 KB
    
  - **Total Feed Storage for 1B Users**: 
    
    50 KB x 1 B = 50 TB
    

#### **Caching**
- **Posts Metadata Cache (80/20 Rule)**: 
    
    0.2 x 5 TB = 1 TB
    
- **Feed Cache (80/20 Rule)**: 
    
    0.2 x 50 TB = 10 TB
    

#### Server Requirements

####  **Post Add QPS**
- **Total Posts per Day**: 500M
  - **Post Add QPS**: 
    
    500M \div 86400 \ (seconds/day) = 5.79K \ (\approx 6K) \ qps
    

#### **Post View QPS**
- **Total Post Views per Day**: 5B
  - **Post View QPS**: 
    
    5B \div 86400 = 57.87K \ (\approx 60K) \ qps
    

#### **Feed Requests QPS**
- **Total Feed Requests per Day**: 1.5B
  - **Feed QPS**: 
    
    1.5B \div 86400 = 17.36K \ (\approx 17.5K) \ qps
    

#### **Total App Servers**
- **Estimated App Servers Needed**: 450 (approx)

1. **Pull-Based**: User fetches feed manually, suitable for large users.
2. **Push-Based**: Server pushes new posts to followers, ideal for users with smaller follower counts.
3. **Hybrid**: Use pull for large users and push for smaller users.

This storage and server breakdown provides a scalable approach to handling a massive user base with efficient data storage, feed generation, and cache optimization.

### **System Design**

#### High Level Architecture of an Instagram

To build a scalable, highly available, and efficient system, the architecture must involve several key components, each responsible for specific tasks.

#### **Client Layer**
   - **Mobile & Web Clients**: Users interact with the system via mobile apps (iOS, Android) or web browsers. The client-side handles uploading content, interacting with posts (likes, comments), and managing user feeds.

#### **API Gateway**
   - The API Gateway is the entry point for all requests from clients, handling routing, rate limiting, authentication, and validation.
   - **Example**: When a user uploads a photo, the gateway forwards the request to the appropriate service responsible for processing the upload.

#### **Microservices**
   - The system is broken down into multiple microservices, each responsible for different aspects:
     - **User Service**: Manages user registration, authentication, profile data, and follower relationships.
     - **Post Service**: Handles creation, deletion, and retrieval of posts, including interacting with media storage.
     - **Feed Service**: Generates and stores personalized feeds based on user activity.
     - **Search Service**: Provides search functionality for users, hashtags, and posts using search engines like Elasticsearch.
     - **Notification Service**: Manages notifications for activities like likes, comments, and follows.
     - **Messaging Service**: Enables real-time communication between users.
   - **Solution**: Each microservice can be independently scaled to handle increasing loads, for example, scaling the Post Service to handle high traffic without affecting other services.

#### **Storage Layer**
   - **Relational Databases**: Used for structured data like user information, follower relationships, and post metadata. MySQL or PostgreSQL can be employed with sharding and replication for scalability.
   - **NoSQL Databases**: Suitable for managing large-scale, distributed data such as user feeds, messages, and likes. Cassandra or MongoDB can handle this workload.
   - **Object Storage**: For media files (photos, videos), object storage services like AWS S3 can be used, paired with a CDN (Content Delivery Network) for efficient global delivery.
   - **Caching Layer**: Redis or Memcached can be used to cache frequently accessed data (e.g., popular posts) for faster access.

#### **Feed Generation**
   - **Pull-Based Approach**: The feed is generated when a user requests it, which can increase latency.
   - **Push-Based Approach**: Pre-generates feeds for users when new content is posted, reducing latency but increasing storage costs.
   - **Hybrid Approach**: A combination of both, where feeds for high-traffic users are pre-generated while others are generated on demand.

#### **Search and Discovery**
   - **Search Indexing**: Use Elasticsearch to index profiles, hashtags, and posts for fast retrieval.
   - **Recommendation Engine**: Based on user interactions, a recommendation engine suggests relevant users or content.

#### **Notifications**
   - **Event-Driven Architecture**: Notifications are generated based on events like new comments or likes.
   - **Message Broker**: A system like Apache Kafka can handle event streams and manage notifications in real-time.

#### **Content Delivery Network (CDN)**
   - Media files are cached at edge locations globally using a CDN, which reduces the load on origin servers and decreases latency for users accessing the content.

#### **Security and Privacy**
   - **Authentication**: OAuth 2.0 for secure access and JWT for session management.
   - **Encryption**: Data encryption (TLS in transit and AES-256 at rest).
   - **Rate Limiting**: To prevent abuse, rate limiting controls the number of requests a user can make.

#### **Scalability and High Availability**
   - **Horizontal Scaling**: Microservices and databases are scaled horizontally to manage high loads.
   - **Database Replication**: Ensures high availability by replicating data across regions.
   - **Auto-Scaling**: Automatically adjusts the number of microservice instances based on traffic.
   - **Load Balancers**: Distribute traffic efficiently across services.

#### **Monitoring and Logging**
   - **Monitoring**: Tools like Prometheus and Grafana track the system’s health and performance.
   - **Centralized Logging**: ELK stack (Elasticsearch, Logstash, Kibana) aggregates logs for analysis and troubleshooting.


This architecture ensures the system is scalable, resilient, and capable of handling millions of users and daily interactions while maintaining performance and availability.

### **Detailed Component Design Instagram**

The component design further details individual services and their integration with other parts of the system, focusing on modularity and scalability to ensure the platform can handle millions of concurrent users and content uploads efficiently.

[Instagram Design](https://github.com/user-attachments/assets/5e7088ff-c127-4215-af8d-78782e87fe76)


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/x7lw0ppa7u8u9e9148m7.png)

Database Table Design:

![Database Design](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/f5iq07zr93rzuqp3tozk.png)


#### **Gateway Service & Anti-DDoS Layer**
   - **Functionality**: Acts as the entry point for all requests. Provides SSL termination, rate limiting, and authentication.
   - **Anti DDoS Protection**: Filters and blocks malicious traffic.

#### **Load Balancer**
   - **Functionality**: Distributes incoming requests to different servers based on consistent Hashing
   
#### **Read and Write Servers**
   - **Read Server**: 
     - Handles requests like fetching user profiles, posts, comments, etc.
     - Interacts heavily with cache and databases to serve data.
   - **Write Server**:
     - Manages requests that modify data, such as uploading posts, likes, and comments.
     - Ensures consistency and durability by writing to the primary database.

#### **Cache**
   - **Functionality**: Reduces latency by storing frequently accessed data.
   - **Types**:
     - **User Cache**: Stores user profiles, follower lists.
     - **Feed Cache**: Stores recent posts and feeds for quick access.
   - **Integration**:
     - Connected to both Read and Write servers to ensure updated data is cached.

#### **Feed Generation Service**
   - **Functionality**: Generates user feeds by aggregating posts from users that someone follows.
   - **Details**:
     - **Algorithm**: Considers factors like recency, popularity, and user interactions.
     - **Data Source**: Pulls from the primary database and caches results for quick delivery.
     Feed Generation explain in detail below.

#### **Notification Service**
   - **Functionality**: Manages and sends notifications for likes, comments, follows, etc.
   - **Integration**:
     - Works with other services (like Post and Comment services) to trigger notifications.
     - Relies on the database and cache for real-time data.

#### **Post Service**
   - **Functionality**: Manages the lifecycle of posts, including creation, editing, and deletion.
   - **Details**:
     - **Storage**: Works with cloud storage to manage media files.
     - **Database**: Stores metadata about posts (captions, tags, etc.).

#### **Comment Like Services**
   - **Comment Service**: Handles the creation, retrieval, and deletion of comments.
   - **Like Service**: Manages the likes for posts and comments.
   - **Integration**:
     - Both services communicate with the database and cache to ensure data consistency and availability.

#### **Search Service**
   - **Functionality**: Provides search capabilities across users, posts, and hashtags.
   - **Details**:
     - **Search Engine**: Uses an indexing system (like Elasticsearch) to quickly retrieve relevant results.

#### **User Profile Service**
   - **Functionality**: Manages user profiles, including bio, profile picture, and settings.
   - **Integration**:
     - Pulls data from the database and updates it as necessary.
     - Works with the Feed and Notification services to personalize user experiences.

#### **CDN (Content Delivery Network)**
   - **Functionality**: Caches and delivers static assets like images and videos closer to the user.
   - **Integration**:
     - Cloud CDN offloads traffic from the origin servers, reducing latency.

#### **Cloud Storage**
   - **Functionality**: Stores user-generated content such as images and videos.
   - **Details**:
     - **Sharding & Replication**: Ensures data durability and availability.
     - **Integration**: The Post Service uploads media files to cloud storage, and the CDN delivers them to users.

#### **Database & Replicas**
   - **Primary Database**: Stores structured data like user profiles, posts, comments, etc.
   - **Replicas**:
     - **Read Replicas**: Handle read-heavy operations to offload the primary database.
     - **Sharding**: Considered for scaling, particularly for user and post data.

#### **Kafka (Message Broker)**
   - **Functionality**: Manages communication between different services, especially for post-processing and analytics.
   - **Details**:
     - **Post Ingestion Service**: Handles post-processing tasks like media resizing, tagging, etc.
     - **Post Processing**: Ensures tasks like media optimization are completed asynchronously.

#### **Analytics Service**
   - **Functionality**: Collects and processes data for generating insights, trending posts, and user behavior.
   - **Integration**:
     - Communicates with the database, Kafka, and Redis to store and process data.
     - **Output**: Popular posts and related metrics are stored in Redis for quick access.

#### **Redis**
   - **Functionality**: Used as an in-memory store for frequently accessed data like popular posts.
   - **Integration**:
     - Works with services like Analytics and Feed Generation to store and serve real-time data.

#### **Monitoring Logging Tools**
   - **Functionality**: Tracks system performance and logs errors or other important events.
   - **Integration**:
     - Connected to all critical components to ensure system reliability and quick troubleshooting.

#### **Asset Service**
   - **Functionality**: Manages media files and other assets.
   - **Details**:
     - **Storage**: Integrates with cloud storage solutions like S3 and a private data warehouse (e.g., Ceph).
     - **Post Service**: Works in conjunction with the post service to handle media files.

#### Detailed Overview of Tables in an Instagram
##### **User Table**
   - **Table Name:** `Users`
   - **Description:** Stores information about each user in the system.
   - **Columns:**
     - `user_id` (Primary Key): Unique identifier for each user.
     - `username`: Unique username chosen by the user.
     - `email`: User's email address.
     - `password_hash`: Hashed password for authentication.
     - `profile_picture_url`: URL to the user's profile picture stored in object storage.
     - `bio`: A short biography written by the user.
     - `created_at`: Timestamp of when the user account was created.
     - `updated_at`: Timestamp of when the user account was last updated.

---

##### **Post Table**
   - **Table Name:** `Posts`
   - **Description:** Stores information about each post created by users.
   - **Columns:**
     - `post_id` (Primary Key): Unique identifier for each post.
     - `user_id` (Foreign Key): References the user who created the post.
     - `caption`: Text caption associated with the post.
     - `media_url`: URL to the media (photo/video) stored in object storage.
     - `media_type`: Type of media (e.g., image, video).
     - `created_at`: Timestamp of when the post was created.
     - `updated_at`: Timestamp of when the post was last updated.


---

##### **Followers Table**
   - **Table Name:** `Followers`
   - **Description:** Stores the relationship between users who follow each other.
   - **Columns:**
     - `follower_id` (Foreign Key): References the user who is following.
     - `followee_id` (Foreign Key): References the user being followed.
     - `created_at`: Timestamp of when the follow relationship was created.
  

---

##### **Comments Table**
   - **Table Name:** `Comments`
   - **Description:** Stores comments made on posts by users.
   - **Columns:**
     - `comment_id` (Primary Key): Unique identifier for each comment.
     - `post_id` (Foreign Key): References the post on which the comment is made.
     - `user_id` (Foreign Key): References the user who made the comment.
     - `comment_text`: The actual comment text.
     - `created_at`: Timestamp of when the comment was created.

---

##### **Likes Table**
   - **Table Name:** `Likes`
   - **Description:** Stores likes given by users on posts.
   - **Columns:**
     - `like_id` (Primary Key): Unique identifier for each like.
     - `post_id` (Foreign Key): References the post that was liked.
     - `user_id` (Foreign Key): References the user who liked the post.
     - `created_at`: Timestamp of when the like was made.

---

##### **Messages Table**
   - **Table Name:** `Messages`
   - **Description:** Stores direct messages sent between users.
   - **Columns:**
     - `message_id` (Primary Key): Unique identifier for each message.
     - `sender_id` (Foreign Key): References the user who sent the message.
     - `receiver_id` (Foreign Key): References the user who received the message.
     - `message_text`: The actual message content.
     - `created_at`: Timestamp of when the message was sent.
  

---

##### **Notifications Table**
   - **Table Name:** `Notifications`
   - **Description:** Stores notifications sent to users.
   - **Columns:**
     - `notification_id` (Primary Key): Unique identifier for each notification.
     - `user_id` (Foreign Key): References the user receiving the notification.
     - `type`: Type of notification (e.g., new follower, like, comment).
     - `entity_id`: References the ID of the entity that triggered the notification (e.g., post_id for likes/comments).
     - `is_read`: Boolean flag indicating whether the notification has been read.
     - `created_at`: Timestamp of when the notification was created.


---

##### **Feeds Table**
   - **Table Name:** `Feeds`
   - **Description:** Stores pre-generated feeds for users.
   - **Columns:**
     - `user_id` (Foreign Key): References the user for whom the feed is generated.
     - `post_id` (Foreign Key): References the posts in the user's feed.
     - `created_at`: Timestamp of when the feed entry was created.
  


---

##### **Object Storage**
- **Object Storage Service:** (e.g., AWS S3, Google Cloud Storage)
  - **Used for:**
    - Storing large media files such as photos and videos uploaded by users.
    - Storing profile pictures and other static content.
  - **Integration with Tables:**
    - The URLs to the stored media files are stored in the `Posts` and `Users` tables under columns like `media_url` and `profile_picture_url`.
  - **Example Workflow:**
    1. A user uploads a photo.
    2. The photo is stored in the object storage (e.g., S3).
    3. The URL of the stored photo is then saved in the `media_url` column of the `Posts` table.
    4. When another user views the post, the app fetches the media URL from the `Posts` table and retrieves the photo from object storage.

---

##### **Conclusion**

- **Relational Database Tables:** Used for structured data like users, posts, comments, likes, followers, and notifications.
- **NoSQL Database:** Could be used for feeds and messages to handle high volumes and ensure low latency.
- **Object Storage:** Used for storing media files like photos and videos, with URLs referenced in the relational database.

This architecture ensures that media-heavy content is efficiently managed using object storage while the relational database handles structured data with relationships. The combination of these storage solutions enables scalability, performance, and efficient data retrieval in an Instagram-like photo-sharing service.

#### API implemenation

##### **Signup API**
**Endpoint**: `/api/signup`  
**Method**: `POST`  
**Description**: Registers a new user and stores their details in the `users` table.

```javascript
app.post('/api/signup', async (req, res) => {
    const { username, first_name, last_name, salted_password_hash, phone_number, email, bio, photo } = req.body;

    // Store profile photo in Object Storage (e.g., S3)
    const photo_url = await objectStorage.upload(photo);

    // Insert user data into the 'users' table
    const query = `INSERT INTO users (username, first_name, last_name, salted_password_hash, phone_number, email, bio, photo_url)
                   VALUES (?, ?, ?, ?, ?, ?, ?, ?)`;
    await db.execute(query, [username, first_name, last_name, salted_password_hash, phone_number, email, bio, photo_url]);

    res.status(201).json({ message: 'User created successfully' });
});
```

##### **Login API**
**Endpoint**: `/api/login`  
**Method**: `POST`  
**Description**: Authenticates a user and updates their last login time.

```javascript
app.post('/api/login', async (req, res) => {
    const { username, salted_password_hash } = req.body;

    // Verify user credentials
    const query = `SELECT * FROM users WHERE username = ? AND salted_password_hash = ?`;
    const [user] = await db.execute(query, [username, salted_password_hash]);

    if (!user) {
        return res.status(401).json({ message: 'Invalid credentials' });
    }

    // Update last login time
    const updateQuery = `UPDATE users SET last_login = NOW() WHERE id = ?`;
    await db.execute(updateQuery, [user.id]);

    // Generate auth token (e.g., JWT)
    const auth_token = generateAuthToken(user.id);

    res.status(200).json({ auth_token, user });
});
```

##### **Search User API**
**Endpoint**: `/api/search_user`  
**Method**: `GET`  
**Description**: Returns public user data for a given search string.

```javascript
app.get('/api/search_user', authenticate, async (req, res) => {
    const { search_string } = req.query;

    // Search users by username, first_name, or last_name
    const query = `SELECT id, username, first_name, last_name, bio, photo_url FROM users 
                   WHERE username LIKE ? OR first_name LIKE ? OR last_name LIKE ?`;
    const users = await db.execute(query, [`%${search_string}%`, `%${search_string}%`, `%${search_string}%`]);

    res.status(200).json(users);
});
```

##### **Get User by ID API**
**Endpoint**: `/api/get_user_by_id/:id`  
**Method**: `GET`  
**Description**: Returns public user data for a given user ID.

```javascript
app.get('/api/get_user_by_id/:id', authenticate, async (req, res) => {
    const { id } = req.params;

    // Fetch user data by ID
    const query = `SELECT id, username, first_name, last_name, bio, photo_url FROM users WHERE id = ?`;
    const [user] = await db.execute(query, [id]);

    if (!user) {
        return res.status(404).json({ message: 'User not found' });
    }

    res.status(200).json(user);
});
```

##### **Follow User API**
**Endpoint**: `/api/follow_user`  
**Method**: `POST`  
**Description**: Adds a follow relationship between the user and the target user.

```javascript
app.post('/api/follow_user', authenticate, async (req, res) => {
    const { user_id, target_user_id } = req.body;

    // Insert follow relationship into 'followers' table
    const query = `INSERT INTO followers (follower_id, followee_id) VALUES (?, ?)`;
    await db.execute(query, [user_id, target_user_id]);

    res.status(201).json({ message: 'User followed successfully' });
});
```

##### **Add Post API**
**Endpoint**: `/api/add_post`  
**Method**: `POST`  
**Description**: Uploads a file to the object storage and adds post data to the `posts` table.

```javascript
app.post('/api/add_post', authenticate, async (req, res) => {
    const { file, caption, user_id } = req.body;

    // Upload file to Object Storage
    const file_url = await objectStorage.upload(file);

    // Insert post data into 'posts' table
    const query = `INSERT INTO posts (user_id, caption, file_url) VALUES (?, ?, ?)`;
    await db.execute(query, [user_id, caption, file_url]);

    res.status(201).json({ message: 'Post created successfully' });
});
```

##### **Delete Post API**
**Endpoint**: `/api/delete_post`  
**Method**: `DELETE`  
**Description**: Soft deletes a post by marking it as deleted in the `posts` table.

```javascript
app.delete('/api/delete_post', authenticate, async (req, res) => {
    const { user_id, post_id } = req.body;

    // Soft delete the post
    const query = `UPDATE posts SET is_deleted = 1 WHERE id = ? AND user_id = ?`;
    await db.execute(query, [post_id, user_id]);

    res.status(200).json({ message: 'Post deleted successfully' });
});
```

##### **Get Feed API**
**Endpoint**: `/api/get_feed`  
**Method**: `GET`  
**Description**: Returns a list of posts from users followed by the given user.

```javascript
app.get('/api/get_feed', authenticate, async (req, res) => {
    const { user_id, count, offset, timestamp } = req.query;

    // Fetch feed posts
    const query = `SELECT posts.* FROM posts 
                   JOIN followers ON posts.user_id = followers.followee_id
                   WHERE followers.follower_id = ? AND posts.created_at > ? 
                   ORDER BY posts.created_at DESC
                   LIMIT ? OFFSET ?`;
    const posts = await db.execute(query, [user_id, timestamp, count, offset]);

    res.status(200).json(posts);
});
```

##### **Get User Posts API**
**Endpoint**: `/api/get_user_posts`  
**Method**: `GET`  
**Description**: Returns a list of posts from a specific user.

```javascript
app.get('/api/get_user_posts', authenticate, async (req, res) => {
    const { user_id, count, offset } = req.query;

    // Fetch posts by user
    const query = `SELECT * FROM posts WHERE user_id = ? AND is_deleted = 0 ORDER BY created_at DESC LIMIT ? OFFSET ?`;
    const posts = await db.execute(query, [user_id, count, offset]);

    res.status(200).json(posts);
});
```

##### **Post Like API**
**Endpoint**: `/api/post_like`  
**Method**: `POST`  
**Description**: Adds a like to a post by the given user.

```javascript
app.post('/api/post_like', authenticate, async (req, res) => {
    const { user_id, post_id } = req.body;

    // Insert like into 'likes' table
    const query = `INSERT INTO likes (user_id, post_id) VALUES (?, ?)`;
    await db.execute(query, [user_id, post_id]);

    res.status(201).json({ message: 'Post liked successfully' });
});
```

##### **Post Unlike API**
**Endpoint**: `/api/post_unlike`  
**Method**: `POST`  
**Description**: Removes a like from a post by the given user.

```javascript
app.post('/api/post_unlike', authenticate, async (req, res) => {
    const { user_id, post_id } = req.body;

    // Delete like from 'likes' table
    const query = `DELETE FROM likes WHERE user_id = ? AND post_id = ?`;
    await db.execute(query, [user_id, post_id]);

    res.status(200).json({ message: 'Post unliked successfully' });
});
```

##### **Add Comment API**
**Endpoint**: `/api/add_comment`  
**Method**: `POST`  
**Description**: Adds a comment to a post.

```javascript
app.post('/api/add_comment', authenticate, async (req, res) => {
    const { user_id, post_id, comment } = req.body;

    // Insert comment into 'comments' table
    const query = `INSERT INTO comments (user_id, post_id, comment) VALUES (?, ?, ?)`;
    await db.execute(query, [user_id, post_id, comment]);

    res.status(201).json({ message: 'Comment added successfully' });
});
```

##### **Delete Comment API**
**Endpoint**: `/api/delete_comment`  
**Method**: `DELETE`  
**Description**: Deletes a comment from a post.

```javascript
app.delete('/api/delete_comment', authenticate,

 async (req, res) => {
    const { user_id, comment_id } = req.body;

    // Delete comment from 'comments' table
    const query = `DELETE FROM comments WHERE id = ? AND user_id = ?`;
    await db.execute(query, [comment_id, user_id]);

    res.status(200).json({ message: 'Comment deleted successfully' });
});
```

##### Summary
This high-level implementation outlines how each API endpoint interacts with the underlying SQL tables. Each API performs tasks such as inserting, updating, and retrieving data from the database, as well as handling interactions with object storage for file uploads. Authentication and authorization checks are assumed to be handled by middleware (e.g., `authenticate`).

This design offers a scalable, distributed architecture that leverages caching, sharding, and message brokering to ensure high availability, low latency, and fault tolerance. Each component is modular, allowing for independent scaling and maintenance. The use of CDNs and cloud storage ensures efficient media delivery, while the robust caching mechanism guarantees a responsive user experience.


## Feed Generation

### Overview
Feed generation involves creating a personalized list of posts for users based on the users they follow and their interactions. The feed should be efficient in terms of data retrieval and should reflect the most relevant and recent posts.

### Generation Approach

**Approach 1**:
1. **Get User IDs**: Retrieve the user IDs of all users followed by the given user.
2. **Fetch Posts**: For each user ID, get all posts made after a specific timestamp.
3. **Sort Posts**: Sort these posts based on recency.
4. **Cache Top K Posts**: Store the top K posts in a cache for quick access.
5. **Return Paginated Results**: Return the next `count` number of posts after the specified `offset`. Support for infinite scrolling provided to fetch more related feed for user.

### Performance Improvements
To improve time complexity and response times:
- **Pre-generated Feeds**: Continuously generate feeds on separate servers and store them in a user feed table. This allows quick retrieval without having to compute the feed on every request.
- **Incremental Updates**: When generating a user’s feed, query the user feed table for previously generated posts and generate new posts based on the timestamp of the last fetched post.

### Storage Considerations
- **Feed Size**: If each user has an average of 50 posts in their feed, with each post being approximately 1KB, the storage size per user would be around 50KB. For 1 billion users, the total feed size would be approximately 50TB.
  
### Cache Management
- **Initial Cache Size**: Initially store 500 posts per user in memory, but adjust based on user engagement patterns (e.g., users who rarely scroll past 10 feeds may only need 100 posts cached).
- **Use of LRU Cache**: Implement a Least Recently Used (LRU) cache to evict feeds for users who haven't accessed their feeds in a while.
- **Machine Learning Optimization**: Utilize machine learning techniques to pre-generate feeds based on user login patterns, ensuring active users receive timely updates.

### Distribution Methods
- **Pull**: Users request their feeds from the server based on actions like scrolling. However, this method may result in empty responses if no new posts have been generated.
- **Push**: The server can push new posts to followers using techniques like long polling. This method is useful for active users but may overwhelm the server if a user has many followers.
- **Hybrid Approach**: Implement a hybrid model where users with a large follower base use pull methods, while less active users receive updates through push mechanisms.

### Caching Strategy
To enhance performance and reduce latency:
- Host static files on Content Delivery Networks (CDNs).
- Use distributed caches on application servers for both posts and feeds.
- Estimate cache size using the 80/20 rule, with 20% of the data generating 80% of the traffic:
  - **Posts Metadata Cache Size**: 0.2 * 5TB = 1TB.
  - **Feed Cache Size**: 0.2 * 50TB = 10TB.

## Database Sharding

### Overview
Database sharding involves partitioning data across multiple database servers to enhance performance, scalability, and manageability.

### Sharding Approaches

#### Partitioning Based on UserID
- **Method**: Use the UserID to determine the shard where a user’s data is stored (e.g., `UserID % 200`).
- **PhotoID Generation**: Append the shard number to each PhotoID for uniqueness.
- **Total Shards**: For a database shard of 4TB, with a projected total size of 712TB, create approximately 200 shards.

**Issues with this Scheme**:
- **Hot Users**: Popular users can create bottlenecks if they have many followers.
- **Non-uniform Distribution**: Some users may have significantly more data than others.
- **Shard Availability**: Storing all of a user's photos on one shard can lead to unavailability if that shard fails.

#### Partitioning Based on PhotoID
- **Method**: Generate unique PhotoIDs first and then determine the shard based on `PhotoID % 200`.
- **PhotoID Generation**: Use a dedicated database instance to generate auto-incrementing PhotoIDs.

**Challenges**:
- **Single Point of Failure**: The ID generation database can become a bottleneck. To mitigate this, implement two databases to generate even and odd PhotoIDs, distributing the load.

#### Future Growth Planning
- **Logical Partitions**: Plan for future data growth by having multiple logical partitions on a single physical database server. Migrate partitions as needed based on data load.
- **Configuration Management**: Maintain a configuration file or a separate database to map logical partitions to database servers, allowing easy migration of partitions when necessary.

## Feed Ranking

### Overview
Feed ranking is crucial for displaying the most relevant posts to users, enhancing engagement and retention.

### Ranking Features
To rank posts effectively, consider the following key features:
- **Creation Time**: More recent posts may be ranked higher.
- **Engagement Metrics**:
  - **Number of Likes**: Posts with higher likes indicate popularity.
  - **Number of Comments**: Indicates engagement and interest.
  - **Number of Shares**: Posts shared by others may be deemed more valuable.
  - **Time of Updates**: Any changes made to a post (like comments or new likes) may influence its ranking.

### Evaluation of Ranking Effectiveness
Assess the effectiveness of the ranking system by monitoring:
- **User Retention**: Track changes in user activity and engagement.
- **Advertising Revenue**: Analyze whether ranking posts higher leads to increased ad clicks and revenue.

## Conclusion
This design provides a scalable and distributed system that uses a microservices architecture to ensure resilience, high availability, and low latency. It employs caching, sharding, and message brokers to manage high user volumes and frequent interactions.


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli