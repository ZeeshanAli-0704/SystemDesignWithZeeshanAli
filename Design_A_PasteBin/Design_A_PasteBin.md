### Designing a Pastebin-Like Web Service

#### Overview

**Pastebin** is a service that allows users to store plain text and generate unique URLs for easy sharing. Users can paste text, which then gets stored on the server, and receive a URL through which others can access the content. Such services are often used for sharing code snippets, configurations, logs, or any plain text.

**Similar Services**: pastebin.com, pasted.co, chopapp.com  
**Difficulty Level**: Easy

### Requirements and Goals of the System

#### Functional Requirements:

1. **Paste Creation**: Users should be able to paste their text and receive a unique URL for access.
2. **Text Only**: The service will only support plain text.
3. **Expiration**: Pastes will expire after a certain period, either default or user-specified.
4. **Custom Aliases**: Users should be able to choose custom URLs for their pastes.

#### Non-Functional Requirements:

1. **Reliability**: The system must be reliable, ensuring no data loss.
2. **Availability**: The service must be highly available, so users can always access their pastes.
3. **Low Latency**: Users should be able to access their pastes quickly.
4. **Security**: URLs should not be easily guessable or predictable.

#### Extended Requirements:

1. **Analytics**: Track how often a paste is accessed.
2. **API Access**: Provide REST APIs for third-party services to create, retrieve, or delete pastes.

### Design Considerations

- **Text Size Limit**: To prevent abuse, we can impose a maximum paste size of 10MB.
- **Custom URL Limits**: Enforcing size limits on custom URLs ensures a consistent and manageable database.

### Capacity Estimation and Constraints

The system is expected to be read-heavy, with more read requests than paste creation. A 5:1 ratio between read and write operations is assumed.

#### Traffic Estimates:

- **New Pastes**: 1 million per day
- **Reads**: 5 million per day

**New Pastes per second**:  

```
1M X 24 X 3600  = 12 pastes/sec
```

**Paste Reads per second**:  

```
5M X 24 X 3600  = 58 reads/sec
```

### Storage Estimates:

- **Average Paste Size**: 10KB
- **Daily Storage**: 1 million pastes × 10KB = 10GB/day

- **Yearly Storage**: 10GB/day × 365 days ≈ 3.65TB/year

- **Storage for 10 Years**: 3.65TB/year × 10 years = 36.5TB

#### Key Generation and Storage:

### Storage and Bandwidth Estimates:

- With 1 million pastes daily, there will be 3.6 billion pastes over 10 years.

#### Unique Key Generation and Storage:

- **Base64 Encoding**: Six-letter strings using base64 encoding can generate about 68.7 billion unique strings.
- **Storage for Keys**:  
  3.6 billion pastes × 6 bytes = 22GB

- **Adjusted Storage Needs**:  
  To avoid full capacity, we add 30% more storage:
  36.5TB × 1.3 ≈ 47.45TB

#### Bandwidth Estimates:

- **Write Requests**: 12 pastes per second
  - **Ingress**:  
    12 pastes × 10KB = 120KB/s

- **Read Requests**: 58 reads per second
  - **Egress**:  
    58 reads × 10KB = 0.6MB/s

#### Memory Estimates:

To improve read performance, caching frequently accessed pastes is important. Assuming the 80-20 rule:

- **Cache Storage**:  
  20% of 5 million pastes × 10KB = 10GB

### System APIs

#### Create Paste API:

- **Endpoint**: `/createPaste`
- **Method**: `POST`
- **Parameters**:
  - `api_dev_key` (string): Developer's API key.
  - `paste_data` (string): Textual data of the paste.
  - `custom_url` (string): Optional custom URL.
  - `user_name` (string): Optional username for URL generation.
  - `paste_name` (string): Optional name of the paste.
  - `expire_date` (string): Optional expiration date.
- **Returns**: A URL for accessing the paste or an error code.

#### Retrieve Paste API:

- **Endpoint**: `/retrievePaste`
- **Method**: `GET`
- **Parameters**:
  - `api_paste_key` (string): The unique key for the paste.
- **Returns**: The paste’s content or an error code.

#### Delete Paste API:

- **Endpoint**: `/deletePaste`
- **Method**: `DELETE`
- **Parameters**:
  - `api_paste_key` (string): The unique key for the paste.
- **Returns**: `true` for success, `false` for failure.

### Database Design


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/1fl5evjerwu23ujt3s46.png)

#### Database Schema:

1. **Pastes Table**:
   - `URLHash`: Unique hash for the URL.
   - `ContentKey`: Key pointing to the paste's content.
   - `CreationDate`: Timestamp of creation.
   - `ExpirationDate`: Timestamp when the paste should expire.
   - `UserName`: (Optional) Username associated with the paste.

2. **Users Table**:
   - `UserID`: Unique identifier for the user.
   - `UserName`: Username chosen by the user.
   - `Pastes`: List of pastes created by the user.

### High-Level Design

The system consists of an application layer and a datastore layer:


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/43wvof1bmy4ujcsnp03d.png)

1. **Application Layer**: Handles read/write requests and communicates with the datastore. It also handles key generation, either dynamically or through a Key Generation Service (KGS).

2. **Datastore Layer**:
   - **Metadata Storage**: Stores paste metadata (e.g., URLHash, ContentKey) in a relational or distributed key-value store.
   - **Object Storage**: Stores the actual content, such as Amazon S3.

#### Key Generation Service (KGS):

A dedicated service generates unique keys and stores them in a key-DB, preventing key duplication.

#### Caching:

A cache layer stores frequently accessed pastes to reduce latency and load on the database.

### Component Design

#### Write Requests:

1. **Key Generation**: Generate a random six-letter key.
2. **Database Insertion**: Store the paste content and associated metadata in the database.
3. **Handle Duplicates**: If a key collision occurs, regenerate the key and retry.

#### Read Requests:

1. **Retrieve Paste**: Query the datastore using the paste key.
2. **Return Content**: Return the content if found; otherwise, return an error.

### Advanced Topics

#### Data Partitioning and Replication:

Data partitioning can be based on key hashing, and replication ensures high availability and fault tolerance.

#### Purging and Cleanup:

Expired pastes should be purged regularly to free up storage. A background service can handle this.

#### Cache and Load Balancer:

A caching layer reduces database load by storing frequently accessed pastes. A load balancer distributes incoming requests across application servers.

#### Security and Permissions:

- **Access Control**: Secure the API with developer keys and OAuth tokens.
- **Encryption**: Use HTTPS for all communications to prevent data breaches.
- **Rate Limiting**: Implement rate limiting to prevent abuse.

This detailed design outlines the architecture and operational considerations for building a scalable, reliable, and performant Pastebin-like service.


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli