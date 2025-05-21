
## 📚 Table of Contents

1. [Understanding the Craigslist-like Classifieds Platform Design](#understanding-the-craigslist-like-classifieds-platform-design)
2. [Functional Requirements Analysis](#functional-requirements-analysis)
   2.1. [User Types](#user-types)
   2.2. [Listing Details](#listing-details)
   2.3. [Filters](#filters)
3. [Non-Functional Requirements](#non-functional-requirements)
4. [System Capacity Planning](#system-capacity-planning)
   4.1. [Key Assumptions](#key-assumptions)
   4.2. [Post Volume](#post-volume)
   4.3. [Storage Requirements](#storage-requirements)
   4.4. [Write Traffic](#write-traffic)
   4.5. [Read Traffic](#read-traffic)
   4.6. [Daily Storage Growth](#daily-storage-growth)
5. [API Design](#api-design)
   5.1. [Post Management APIs](#post-management-apis)
   5.2. [User Management APIs](#user-management-apis)
   5.3. [System API](#system-api)
6. [Database Schema](#database-schema)
   6.1. [Users Table](#users-table)
   6.2. [Posts Table](#posts-table)
   6.3. [Images Table](#images-table)
   6.4. [Reports Table](#reports-table)
7. [Storage Architecture](#storage-architecture)
8. [Image Upload Strategy](#image-upload-strategy)
   8.1. [Direct Client Upload](#direct-client-upload-recommended)
   8.2. [Upload Through Backend](#upload-through-backend)
   8.3. [Hybrid Approach](#hybrid-approach-recommended)
9. [Read and Write Flow](#read-and-write-flow)
   9.1. [Write Flow](#write-flow)
   9.2. [Read Flow](#read-flow)
10. [Geolocation Partitioning](#geolocation-partitioning)
    10.1. [Why Use Geolocation Partitioning?](#why-use-geolocation-partitioning)
    10.2. [Structure Details](#structure-details)
    10.3. [Database Sharding](#database-sharding)
    10.4. [Elasticsearch Indexing Strategy](#elasticsearch-indexing-strategy)
    10.5. [Object Storage Organization](#object-storage-organization)
    10.6. [CDN and GeoDNS Implementation](#cdn-and-geodns-implementation)
    10.7. [Request Routing Logic](#request-routing-logic)
    10.8. [Real-World Example](#real-world-example)
    10.9. [Implementation Challenges](#implementation-challenges)
11. [Search Design](#search-design)
    11.1. [Document Structure](#document-structure)
    11.2. [Query Patterns](#query-patterns)
    11.3. [Scaling Strategy](#scaling-strategy)
12. [Optional Analytics System](#optional-analytics-system)
13. [Key Design Decisions Explained](#key-design-decisions-explained)
    13.1. [Why Use a Hybrid Upload Strategy?](#why-use-a-hybrid-upload-strategy)
    13.2. [Why Geographic Partitioning?](#why-geographic-partitioning)
    13.3. [Why 7-Day Auto-Expiration?](#why-7-day-auto-expiration)
    13.4. [Why Object Storage + CDN for Images?](#why-object-storage--cdn-for-images)
14. [Summary](#summary)




# Understanding the Craigslist-like Classifieds Platform Design

The document outlines a comprehensive system design for a Craigslist-style classifieds platform that allows users to post, browse, and respond to classified listings. Let me walk through each major component in detail.

## Functional Requirements Analysis

### User Types
The system supports two primary user types:

1. **Viewers**: Users who browse the platform without posting content. They can:
   - Browse and search through listings
   - View detailed information about specific listings
   - Apply filters to narrow down search results
   - Contact sellers/posters
   - Report inappropriate content

2. **Posters**: Users who create and manage listings. They can:
   - Create, update, and delete their own listings
   - Renew posts every 7 days to keep them active
   - Search through and manage their listings
   - Upload up to 10 images per listing (1MB each)
   - Potentially upload videos (marked as an extra feature)

### Listing Details
Each listing contains:
- Title: Brief description of the item/service
- Description: Detailed information
- Price: Listed in a single currency format
- Location: Geographic information about where the item/service is available
- Photos: Up to 10 images
- Auto-deletion: Posts automatically expire after 7 days

### Filters
The system supports filtering by:
- Neighborhood: Geographic area within a city
- Price range: Minimum and maximum price values
- Item condition: Categorical value (like new, good, fair, etc.)
- The design allows for additional filters based on user/application needs

## Non-Functional Requirements

These requirements define the quality attributes of the system:

- **Scalability**: The system must support up to 10 million users per city
- **High Availability**: 99.9% uptime guarantee (equals about 8.8 hours of downtime per year)
- **Performance**: 99th percentile latency under 1 second for read/search operations
- **Security**: Authentication required for users who want to post listings

## System Capacity Planning

The capacity planning section provides detailed calculations for the expected scale:

### Key Assumptions
- 10 million users per city
- 10% (1 million) are active posters, assume rest 9M are viewers 
- Each active poster creates 10 posts per day
- Each post has 1KB of metadata plus 10 images of 1MB each
- Posts expire after 7 days

### Post Volume
- Daily: 10 million new posts per city (1M x 10 post/user)
- Total active posts at any time: 70 million (10M × 7 days) 
7 days as we are keeping post active for 7  days & will delete after 


### Storage Requirements
- Metadata: 70GB (70M posts × 1KB)
- Images: 700TB (70M posts × 10MB)
- This shows why object storage and CDN are critical architectural components

### Write Traffic
- Post creation: 116 posts/second average, 232/second at peak
- Image uploads: 580/second average, 1,160/second at peak (accounting for retries)

### Read Traffic
- Post views: 2,083/second average, 4,000/second at peak
- Image views: 20,000/second average, 40,000/second at peak

### Daily Storage Growth
- Metadata: 10GB/day
- Images: 100TB/day

## API Design

The API is RESTful and divided into three main categories:

### Post Management APIs
- `GET /post/{id}`: Retrieve a specific post
- `DELETE /post/{id}`: Remove a post
- `GET /post?search=...`: Search for posts with filters
- `POST /post`: Create a new post
- `PUT /post`: Update an existing post
- `POST /report`: Report abusive content
- `POST /contact`: Contact a poster
- `DELETE /old_posts`: System endpoint to remove expired posts

### User Management APIs
- `POST /signup`: Create a new user account
- `POST /login`: Authenticate a user
- `DELETE /user`: Delete a user account

### System API
- `GET /health`: System health check endpoint

## Database Schema

The database uses a relational model with four main tables:

### Users Table
Stores basic user information:

```sql
CREATE TABLE Users (
  id SERIAL PRIMARY KEY,
  first_name TEXT,
  last_name TEXT,
  signup_ts BIGINT
);
```

### Posts Table
Contains all listing information:

```sql
CREATE TABLE Posts (
  id SERIAL PRIMARY KEY,
  created_at BIGINT,
  poster_id INT,
  location_id INT,
  title TEXT,
  description TEXT,
  price INT,
  condition TEXT,
  country_code CHAR(2),
  state TEXT,
  city TEXT,
  street_number INT,
  street_name TEXT,
  zip_code TEXT,
  phone_number BIGINT,
  email TEXT
);
```


### Images Table
Tracks images associated with posts:

```sql
CREATE TABLE Images (
  id SERIAL PRIMARY KEY,
  ts BIGINT,
  post_id INT,
  image_address TEXT
);
```

### Reports Table
Records abuse reports:

```sql
CREATE TABLE Reports (
  id SERIAL PRIMARY KEY,
  ts BIGINT,
  post_id INT,
  user_id INT,
  abuse_type TEXT,
  message TEXT
);
```


## Storage Architecture

The system uses a multi-tiered storage approach:

1. **SQL Database**: For structured data like metadata, user information, and reports
2. **Object Storage (S3-like)**: For storing large binary files like images
3. **CDN (Content Delivery Network)**: For efficiently serving images from edge locations close to users

## Image Upload Strategy

The document presents three approaches:

### Direct Client Upload (Recommended)
1. Client sends post metadata to backend
2. Backend creates a post record and returns the post ID plus pre-signed URLs for image uploads
3. Client uploads images directly to object storage
4. Client notifies backend when uploads are complete

**Advantages**:
- Highly scalable and efficient
- Reduces backend load
- More cost-effective

**Disadvantages**:
- More complex error handling
- Risk of incomplete uploads if client disconnects

### Upload Through Backend
1. Client sends metadata and image files to backend
2. Backend stores data and handles uploading to object storage

**Advantages**:
- Better validation and control
- Simpler client implementation

**Disadvantages**:
- Creates a scalability bottleneck
- Increases backend resource requirements

### Hybrid Approach (Recommended)
Combines elements of both approaches for optimal balance of control and scalability.

## Read and Write Flow

### Write Flow
1. Client sends post metadata to the backend
2. Backend stores metadata in SQL database and returns a post ID
3. Client uploads images directly to object storage
4. Database sharding is used to handle high write volumes:
   - Multiple write databases
   - Partitioning by city ID or consistent hashing
   - Each write node has associated read replicas

### Read Flow
1. Client requests a post by ID
2. Load balancer directs request to nearest read replica
3. Backend fetches metadata from SQL database
4. Images are served from CDN/object storage

## Geolocation Partitioning

Geolocation partitioning is a critical architectural strategy for a classifieds platform that divides data along geographic boundaries. Let me break down why it's important and how each component works:

### Why Use Geolocation Partitioning?

#### Performance Benefits
- **Faster Queries**: Most users search for listings in their own city or region, so keeping related data together reduces query latency
- **Reduced Data Scope**: Limits searches to relevant geographic areas instead of scanning the entire database
- **Localized Caching**: Improves cache hit rates by focusing on locally relevant content

#### Scalability Advantages
- **Independent Scaling**: Each region can scale based on its own traffic patterns and user base
- **Fault Isolation**: Issues in one region don't affect others
- **Optimized Resource Allocation**: High-traffic cities can get more resources than low-traffic areas

### Structure Details

#### Hierarchical Organization
The system organizes data in a geographic hierarchy:
- **Country**: Top level (e.g., US, Canada, UK)
- **State/Province**: Middle level (e.g., New York, California)
- **City**: Lowest level (e.g., NYC, San Francisco)

This mirrors how users think about locations when posting or searching for items.

#### Database Sharding
- **Geographic Shards**: Each city or region gets its own database shard (or shares with similar-sized regions)
- **Consistent Hashing**: Distributes cities across shards evenly and minimizes resharding impact
- **Mapping Table**: Maintains a lookup service that maps cities to their corresponding database shards

Example:
```
Shard 1: New York City, Chicago, Los Angeles
Shard 2: Miami, Seattle, Denver
Shard 3: Boston, Washington DC, San Francisco
```

The mapping table would contain entries like:
```
"new-york-city" → Shard 1
"seattle" → Shard 2
"boston" → Shard 3
```

### Elasticsearch Indexing Strategy

#### Separate Indexes per Region
- Each city/region gets its own search index (e.g., `posts_us_ny_nyc`)
- Benefits:
  - Smaller indexes are faster to search
  - Index settings can be tuned for local language and search patterns
  - Index operations (updates, rebuilds) affect only one region

#### Geo_Point Fields
- Special Elasticsearch data type optimized for location-based queries
- Enables powerful queries like:
  - "Find all listings within 5 miles of downtown"
  - "Sort listings by distance from my current location"
  - "Show me items in this neighborhood"

#### Federated Search
- For queries that span multiple regions:
  - System sends parallel queries to relevant regional indexes
  - Results are merged, sorted, and returned to the user
  - Example: "Show me furniture listings in NYC, Boston, and Philadelphia"

### Object Storage Organization

#### Path-Based Organization
- Images stored using paths that reflect geography: `s3://images/us/ny/nyc/post123.jpg`
- Benefits:
  - Logical organization matches application structure
  - Easy to identify content by location
  - Simplifies backup and retention policies by region

Example:
```
s3://images/us/ny/nyc/post123.jpg
s3://images/us/ca/sf/post456.jpg
s3://images/ca/on/toronto/post789.jpg
```

### CDN and GeoDNS Implementation

#### CDN Edge Nodes
- Content Delivery Network caches images at edge locations worldwide
- When a user views a listing, images are served from the nearest edge server
- Benefits:
  - Reduced image load times (often 10x faster than from origin)
  - Lower origin server load
  - Better user experience especially for mobile users

#### GeoDNS Routing
- DNS system determines user's approximate location
- Routes requests to nearest server cluster
- Example:
  - User in Chicago → Midwest regional servers
  - User in Paris → European regional servers

### Request Routing Logic

#### Region Inference
The system determines which region's data to access using multiple methods:
1. **Post ID**: IDs can encode region information (e.g., `nyc-12345`)
2. **User IP**: Approximate user location from IP address
3. **Explicit Tags**: User-selected region or location preferences

#### Routing Implementation
- **API Gateway**: Routes API requests to appropriate regional services
- **Load Balancer**: Distributes traffic across servers within a region
- **Service Discovery**: Maintains registry of available services by region

### Real-World Example

When a user in San Francisco searches for "used bicycle under $200":

1. GeoDNS routes them to West Coast servers
2. System identifies SF as their location (from IP or preferences)
3. Query goes to the SF Elasticsearch index (`posts_us_ca_sf`)
4. Results include only SF listings, with data from the SF database shard
5. Images load from nearby CDN edge nodes in California

If the user expands their search to include Oakland and San Jose:
1. System performs parallel queries across all three city indexes
2. Results are merged, filtered by price, and returned to the user

### Implementation Challenges

1. **Cross-Region Searches**: Need efficient algorithms for merging results
2. **Region Mapping Maintenance**: Keeping the city→shard mapping updated
3. **Data Migration**: Moving data when resharding or rebalancing
4. **Consistency**: Ensuring consistent experience across regions

This geolocation partitioning architecture enables the classifieds platform to scale efficiently to millions of users while maintaining fast response times and a localized user experience.


## Search Design

The system uses Elasticsearch for fast, scalable search functionality:

### Document Structure
- Core fields: Post ID, title, price, description
- Location stored as geo_point for spatial queries
- Filter fields: price, condition, neighborhood

### Query Patterns
- Full-text search across title and description
- Range filtering for price
- Categorical filtering for condition
- Geospatial filtering by location

### Scaling Strategy
- One index per geographic region
- Optimized shard configuration
- Asynchronous indexing via Kafka to Elasticsearch consumers

## Optional Analytics System

For future growth, the design includes an analytics capability:
- Log collection for user actions (searches, views, reports)
- Kafka for streaming log data
- Data warehouse (Redshift or BigQuery) for storage
- Analysis for trends and abuse detection

## Key Design Decisions Explained

### Why Use a Hybrid Upload Strategy?
The hybrid approach balances control and scalability. By having the backend manage metadata but allowing direct image uploads, the system avoids becoming a bottleneck while maintaining control over the core listing data.

### Why Geographic Partitioning?
Most classified listings are location-specific, with users typically searching within their own city or region. Geographic partitioning aligns the data storage with this usage pattern, improving performance and reducing query scope.

### Why 7-Day Auto-Expiration?
This policy keeps content fresh and significantly reduces storage requirements. Without this limitation, the storage needs would grow unbounded over time.

### Why Object Storage + CDN for Images?
With 700TB of image data and 40,000 image requests per second at peak, traditional file storage would be inadequate. Object storage offers cost-effective scalability, while CDNs provide low-latency global delivery.

## Summary

This Craigslist-like system design demonstrates careful consideration of:

1. **Scale**: Supporting 10 million users per city with 10 million daily posts
2. **Performance**: Ensuring fast response times through caching, CDN, and read replicas
3. **Cost-efficiency**: Using appropriate storage tiers and auto-expiration policies
4. **Geographic organization**: Aligning system architecture with usage patterns
5. **Evolution path**: Starting simple and evolving to microservices as needed

The design balances technical sophistication with practical implementation concerns, providing a solid foundation for a large-scale classifieds platform.