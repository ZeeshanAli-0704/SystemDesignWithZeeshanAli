### **Designing a Web Crawler: Detailed Design with Example and Diagram**

#### **Table of Contents**
1. [Introduction to Web Crawlers](#introduction-to-web-crawlers)
2. [Applications of Web Crawlers](#applications-of-web-crawlers)
3. [Understanding the Problem Functional and Non Functional Requirements](#understanding-the-problem-functional-and-non-functional-requirements)
4. [Capacity Estimation and Constraints](#capacity-estimation-and-constraints)
5. [High Level Design Components of a Web Crawler](#high-level-design-components-of-a-web-crawler)
6. [Web Crawler Workflow Detailed Overview](#web-crawler-workflow-detailed-overview)
7. [More Design Considerations](#more-design-considerations)
8. [Conclusion](#conclusion)
9. [FAQ's](#faqs)
    - [Load Distribution](#how-load-should-be-distributed-evenly-across-all-computers)
    - [Preventing Crawling of Duplicate URLs](#how-would-you-prevent-crawling-of-duplicate-urls)
    - [Best Storage Systems](#what-is-the-best-type-of-storage-system-to-use-for-a-web-crawler)
    - [Challenges Faced by Web Crawlers](#what-are-some-challenges-faced-by-web-crawlers)
    - [Performance Metrics](#what-performance-metrics-should-be-monitored-for-a-web-crawler)

---

### **Introduction to Web Crawlers**
A web crawler, also known as a robot or spider, is a critical tool used primarily by search engines to discover new or updated content on the web. This content can include web pages, images, videos, and PDF files. The crawler starts by collecting a few web pages, then follows the links on those pages to discover new content. The data gathered by web crawlers is typically used to create an index, enabling faster search results.

---

### **Applications of Web Crawlers**

- **Search Engine Indexing:** Web crawlers are used by search engines to gather and index web pages, enabling quick and relevant search results. For instance, Googlebot, Google's crawler, systematically explores and indexes web content.

- **Web Archiving:** Web crawlers are employed to collect and preserve web content for historical purposes. Institutions like the US Library of Congress use crawlers to archive websites, ensuring that digital information is stored for future generations.

- **Web Mining:** Financial institutions use web crawlers to download and analyze corporate documents such as shareholder meetings and annual reports. This analysis helps in extracting valuable insights that can inform investment decisions.

- **Web Monitoring:** Crawlers scan the web to identify unauthorized use of copyrighted materials or trademarks. This application is critical for detecting and addressing copyright and trademark infringements.

- **Maintaining Mirror Sites:** Web crawlers are used to create and maintain mirror sites, ensuring that popular websites have reliable backups that can be accessed if the primary site goes down.

- **Searching for Copyright Infringements:** Beyond web monitoring, specific crawlers are tasked with identifying instances of copyright infringement, helping content creators and rights holders protect their intellectual property.

---

### **Understanding the Problem Functional and Non Functional Requirements**

Before diving into the design, it's essential to clarify the system requirements and goals. Below is a breakdown of functional and non-functional requirements based on a typical scenario where the web crawler is used for search engine indexing.

#### **Functional Requirements (FRs):**
1. **Content Type Support:** The crawler should support crawling HTML pages initially, with the ability to extend to other content types like PDFs, images, etc.
2. **Content Freshness:** The system should periodically recrawl and update newly added or edited web pages.
3. **Duplicate Handling:** The crawler must detect and ignore duplicate content.
4. **Robust URL Processing:** The crawler should handle and respect the `robots.txt` file for each website, preventing the crawling of restricted pages.
5. **Content Storage:** HTML pages should be stored for up to five years.
6. **Extensible Architecture:** The system should be modular, allowing for the addition of new document types or protocols (e.g., FTP) with minimal changes.

#### **Non-Functional Requirements (NFRs):**
1. **Scalability:** The crawler should be capable of crawling billions of web pages, fetching approximately 6,200 pages per second.
2. **Efficiency:** The system should utilize parallel processing to maximize the crawling speed and efficiency.
3. **Politeness:** The crawler must adhere to a polite crawling policy, ensuring it doesn't overwhelm any web server.
4. **Robustness:** The crawler should handle malformed HTML, unresponsive servers, and other potential issues without crashing.
5. **Storage Capacity:** The system should be capable of storing up to 2.14 petabytes of data.

---

### **Capacity Estimation and Constraints**

When designing a web crawler to handle 15 billion pages in a four-week period, it's essential to estimate the required processing speed and storage capacity.

#### **1. Crawling Rate**
- **Total Pages to Crawl:** 15 billion pages.
- **Time Frame:** 4 weeks (28 days).
- **Seconds in 4 Weeks:** 4 weeks X 7 days/week X 24 hours/day X 3600 seconds/hour = 2,419,200 seconds.
- **Pages per Second:** To meet the goal, the crawler needs to fetch pages at a rate of:

    15 billion pages / 2,419,200 seconds ~ 6,200 pages/second

#### **2. Storage Requirements**
- **Average Page Size:** Assuming each HTML page is approximately 100KB.
- **Metadata Size:** An additional 500 bytes of metadata per page.
- **Total Storage for HTML Content:**

    15 billion pages X (100 KB + 500 bytes) ~ 1.5 petabytes
    
- **Storage Capacity with Overhead:** Operating with a 70% capacity model (to avoid hitting storage limits and maintain system performance):

    1.5 petabytes / 0.7 ~ 2.14 petabytes

**Conclusion:** The crawler system needs to support a sustained throughput of around 6,200 pages per second and have a storage capacity of at least 2.14 petabytes to store the crawled data efficiently.

---

## **High Level Design Components of a Web Crawler**

Designing a web crawler involves creating several components that work together to efficiently fetch, process, and store web pages. Here’s a detailed explanation of each component:

#### **1. Seed URLs**
- **Purpose:** Seed URLs serve as the starting points for the crawler's exploration of the web. They are the initial URLs that the crawler uses to begin the crawling process. The selection of the seed URL can depend on factors like geographical location, categories (entertainment, education, sports, food), content type, etc.
- **Example:** If you're crawling a specific domain, like a university’s website, the seed URL might be `www.university.edu`. The crawler begins by fetching this URL and then discovers other pages linked from it.
- **Importance:** The selection of seed URLs can significantly influence the scope and efficiency of the crawl, as they determine the initial set of pages the crawler will access.

#### **2. URL Frontier**
- **Purpose:** The URL Frontier is a central data structure that manages the list of URLs waiting to be crawled.
- **Implementation:** Typically implemented as a First-In-First-Out (FIFO) queue to maintain a breadth-first search pattern. However, it can be enhanced with additional features:
    - **Politeness Policy:** Ensures the crawler doesn't overload a single web server by spacing out requests to the same domain.
    - **Priority Queue:** Allows prioritization of certain URLs based on factors like freshness, importance, or relevance to the crawl's objectives.
- **Example:** URLs are dequeued from the URL Frontier and processed one by one. If the crawler finds new links on a page, those links are added to the URL Frontier for future crawling.

#### **3. HTML Downloader / Fetcher / Renderer**
- **Purpose:** The HTML Downloader fetches the actual web pages from the internet. It takes URLs from the URL Frontier, requests the corresponding HTML content, and makes it available for further processing.
- **Functionality:**
    - Makes HTTP/HTTPS requests to download web pages.
    - Handles redirects, timeouts, and retries to ensure successful retrieval.
- **Example:** For a given URL like `www.university.edu/department.html`, the HTML Downloader fetches the HTML content of that page.

#### **4. DNS Resolver**
- **Purpose:** Converts domain names (e.g., `www.university.edu`) into IP addresses, which are necessary to establish a connection with the web server hosting the content.
- **Functionality:**
    - Performs DNS lookups to resolve URLs into IP addresses.
    - Caches DNS responses to reduce latency and load on DNS

 servers.
- **Example:** When the crawler encounters `www.university.edu`, the DNS Resolver converts it to the IP address `192.168.1.1`, allowing the crawler to connect to the server.

#### **5. Content Parser**
- **Purpose:** The Content Parser is responsible for parsing the fetched HTML content and extracting relevant information, such as metadata, text, and links.
- **Functionality:**
    - Extracts data like page titles, meta descriptions, and internal/external links.
    - Parses HTML to identify and process different tags, such as `<a>`, `<img>`, and `<script>`.
- **Example:** For an HTML page with the structure:

```html
<html>
<head><title>Department of Computer Science</title></head>
<body>
<a href="www.university.edu/courses.html">Courses</a>
<a href="www.university.edu/research.html">Research</a>
</body>
</html>
```

The Content Parser extracts the title ("Department of Computer Science") and the links to other pages (`www.university.edu/courses.html` and `www.university.edu/research.html`).

#### **6. Content Deduplication**
- **Purpose:** Ensures that duplicate content is not processed or stored multiple times, optimizing storage use and reducing processing overhead.
- **Implementation:** A hash-based mechanism is typically used:
    - Generates a unique hash for each piece of content.
    - Compares the hash with existing ones to detect duplicates.
    - Stores only unique content, ignoring duplicates.
- **Example:** If two pages have the same content but different URLs, the deduplication component identifies the duplicate and avoids storing or processing it again.

#### **7. Content Storage**
- **Purpose:** Stores the crawled content for future use, such as indexing by a search engine or analysis by a data mining tool.
- **Implementation:**
    - **File-Based Storage:** Stores content as files on disk, organized by domain or content type.
    - **Database Storage:** Stores metadata and content in a database, enabling quick retrieval and analysis.
    - **Cloud Storage:** Stores large volumes of content on distributed cloud storage platforms like AWS S3, ensuring scalability and availability.
- **Example:** A crawled HTML page is stored with metadata indicating when it was crawled, its source URL, and the content hash.

#### **8. URL Extractor**
- **Purpose:** Identifies and extracts URLs from the HTML content of a page. These URLs are then added to the URL Frontier for future crawling.
- **Functionality:**
    - Analyzes the HTML structure to locate `<a>` tags and other elements containing URLs.
    - Normalizes extracted URLs to ensure consistency and accuracy.
- **Example:** From the following HTML snippet:

```html
<a href="www.university.edu/courses.html">Courses</a>
<a href="www.university.edu/research.html">Research</a>
```

The URL Extractor identifies and extracts `www.university.edu/courses.html` and `www.university.edu/research.html`.

#### **9. URL Filter**
- **Purpose:** Filters out irrelevant or disallowed URLs based on predefined criteria, ensuring that the crawler focuses only on useful content.
- **Functionality:**
    - **Domain Filter:** Restricts crawling to specific domains or excludes certain domains.
    - **File Type Filter:** Ignores URLs pointing to non-HTML content, like images or videos.
    - **Robots.txt Compliance:** Ensures that URLs disallowed by the `robots.txt` file are not crawled.
- **Example:** If the URL Filter is configured to exclude all non-HTML content, URLs pointing to `.jpg`, `.png`, or `.mp4` files are filtered out.

#### **10. URL Seen**
- **Purpose:** Keeps track of all URLs that have been crawled or are in the process of being crawled, preventing duplicate efforts.
- **Implementation:**
    - Maintains a database or in-memory set of seen URLs.
    - Checks each new URL against this set before adding it to the URL Frontier.
- **Example:** If `www.university.edu/courses.html` has already been crawled, the URL Seen component ensures it is not crawled again.

#### **11. Robots.txt Handler**
- **Purpose:** The Robots.txt Handler processes the `robots.txt` file of each website to determine which pages are off-limits to the crawler.
- **Functionality:**
    - Fetches and parses the `robots.txt` file for each domain.
    - Enforces the rules specified in the file, ensuring compliance with the website’s crawling policies.
- **Example:** If `www.university.edu/robots.txt` contains the following directive:

```plaintext
User-agent: *
Disallow: /private/
```

The Robots.txt Handler ensures that the crawler does not access any URLs under `www.university.edu/private/`.

---

### **Web Crawler Workflow Detailed Overview**

Below is a simplified workflow diagram to illustrate how these components interact:


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/zlb6jy1tyn4hvkpm5iry.png)

Source : Tech Dummies Narendra L / YouTube

1. **Add Seed URLs to the URL Frontier**:
   - The process begins by adding seed URLs to the URL Frontier. These initial URLs serve as the starting points for the crawling process. For example, if you’re crawling a university’s website, the homepage URL (e.g., `www.university.edu`) would be a seed URL.

2. **HTML Downloader Fetches URLs from the URL Frontier**:
   - The HTML Downloader requests a batch of URLs from the URL Frontier. These URLs are the ones that need to be fetched and processed.

3. **HTML Downloader Resolves IP Addresses via DNS Resolver**:
   - Before downloading the content, the HTML Downloader uses the DNS Resolver to convert the URLs into their corresponding IP addresses. This step is necessary to establish connections to the correct web servers.

4. **HTML Downloader Fetches and Downloads the Content**:
   - Once the IP addresses are resolved, the HTML Downloader fetches the web pages corresponding to those URLs and downloads their HTML content.

5. **Content Parser Validates and Parses HTML Pages**:
   - The downloaded HTML content is then passed to the Content Parser. This component parses the HTML to ensure it’s well-formed and extracts any relevant information. If the page is malformed, appropriate actions are taken to handle the errors.

6. **Content Deduplication Checks for Duplicate Content**:
   - After parsing and validating, the content is passed to the "Content Seen?" component. This component checks if the HTML page is already stored, indicating that the same content under a different URL has already been processed.
     - **If the content is already in storage**: The system identifies it as a duplicate, and the HTML page is discarded to avoid redundant processing.
     - **If the content is not in storage**: It means this content has not been processed before, so it proceeds to the next step.

7. **Link Extractor Extracts Links from the HTML Pages**:
   - The validated content is now sent to the Link Extractor. This component identifies and extracts all hyperlinks within the HTML pages, which may lead to other pages that need to be crawled.

8. **URL Filter Screens Out Unwanted URLs**:
   - The extracted links are passed through the URL Filter, which screens out unwanted URLs. These could include URLs pointing to non-HTML content, blacklisted sites, or any other links deemed unnecessary for the crawl.

9. **URL Seen? Component Checks for Previously Processed URLs**:
   - The filtered URLs are then checked by the "URL Seen?" component. This component verifies if a URL has already been processed and stored.
     - **If the URL has been processed before**: The system skips it, as there's no need to revisit the same URL.
     - **If the URL is new**: It’s added to the URL Frontier for future crawling.

10. **New URLs are Added to the URL Frontier**:
   - Any URLs that have not been processed before are added to the URL Frontier, completing the cycle. These URLs will be crawled in subsequent iterations, ensuring continuous and efficient crawling of new web pages.

This workflow ensures that the web crawler efficiently and systematically discovers, downloads, processes, and stores web content while avoiding redundancy and respecting the rules set by websites.

---

### **More Design Considerations**

**URL Frontier Management:**

- **Politeness:** Ensure that the crawler respects the rules of each website by mapping website hostnames to specific download threads. This allows each thread to fetch URLs from its queue at a controlled rate, avoiding overloading any single site.
  
- **Priority:** Implement a prioritization mechanism for URLs based on criteria such as PageRank, web traffic, and the frequency of content updates. This ensures that high-value pages are crawled first.

**Content Deduplication:**

- **Hashing for Duplicate Detection:** Generate checksums for each document using hashing algorithms like MD5 or SHA. These checksums are stored and used to compare future documents, allowing the system to detect and ignore duplicates effectively.

**HTML Downloader Performance Optimization:**

- **Distributed Crawling:** Improve performance by distributing the crawl jobs across multiple servers and threads, allowing for parallel processing and faster download rates.

- **DNS Caching:** Cache DNS resolutions to minimize delays caused by repeated DNS lookups, improving the efficiency of the crawler.

- **Locality Optimization:** Position crawl servers in various geographic locations to reduce latency and improve download speeds by fetching content from the closest servers.

**Robustness:**

- **Consistent Hashing:** Use consistent hashing to evenly distribute the crawling load across all servers, ensuring no single server is overwhelmed.

- **Exception Handling:** Implement comprehensive error handling mechanisms to manage network issues and other exceptions gracefully, ensuring that the crawler remains stable and continues operating smoothly.

- **Spider Trap Prevention:** Protect the crawler from spider traps by setting limits on URL length and patterns, and by implementing manual filters to avoid getting stuck in infinite loops or irrelevant content.


---

### **Conclusion**

Designing a web crawler involves a complex interplay of components and considerations. By addressing both functional and non-functional requirements and implementing a robust, scalable design, it's possible to build a crawler that efficiently discovers and processes web content. This document has provided a detailed overview of the design and implementation considerations for a web crawler, laying the groundwork for a successful project.

---

### **FAQ's**

#### **How load should be distributed evenly across all computers?**

The load can be distributed across multiple machines by partitioning the URL space, using consistent hashing, or assigning specific domains or subdomains to different crawler nodes. Each node can independently fetch and process pages, with coordination provided by a central URL Frontier or through a distributed queue system.

#### **How would you prevent crawling of duplicate URLs?**

To prevent duplicate URLs from being crawled, the system should maintain a URL Seen database that records all URLs that have been processed or are in the queue. Before adding a new URL to the queue, the system should check if it already exists in the URL Seen database.

#### **What is the best type of storage system to use for a web crawler?**

The best storage system for a web crawler depends on the volume and nature of the data. For large-scale operations, distributed file systems like Hadoop's HDFS or cloud storage solutions like Amazon S3 are ideal. These systems provide scalability, fault tolerance, and ease of access. For storing metadata and small content, NoSQL databases like MongoDB or Elasticsearch can be used.

#### **What are some challenges faced by web crawlers?**

Web crawlers face several challenges, including:

- **Dynamic Content:** Pages that change frequently or are generated dynamically can be difficult to crawl effectively.
- **Politeness:** Ensuring the crawler doesn't overwhelm servers while maintaining efficiency can be challenging.
- **Duplicate Content:** Identifying and handling duplicate content across different URLs can be complex.
- **Scaling:** As the number of pages grows, ensuring that the crawler scales effectively without compromising performance is critical.

#### **What performance metrics should be monitored for a web crawler?**

Key performance metrics to monitor include:

- **Crawling Speed:** The number of pages fetched per second.
- **Data Storage Utilization:** The amount of storage used versus available storage.
- **URL Queue Length:** The number of URLs waiting to be processed.
- **Error Rate:** The percentage of failed fetches or parsing errors.
- **Politeness Compliance:** The rate at which requests are made to the same server, ensuring that the crawler respects robots.txt directives and rate limits.

---

This document provides a comprehensive overview of the design and implementation of a web crawler, covering all the essential components, design considerations, and potential challenges. It can serve as a foundational guide for anyone looking to build or understand the workings of a web crawler.

---


Todo - Bloom Filters



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli