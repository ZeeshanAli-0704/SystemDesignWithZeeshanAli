### What is DNS?

The Domain Name System (DNS) is the phonebook of the Internet. Humans access information online through domain names, like nytimes.com or espn.com. Web browsers interact through Internet Protocol (IP) addresses. DNS translates domain names to IP addresses so browsers can load Internet resources.

---

### How Does DNS Work?

The process of DNS resolution involves converting a hostname (such as www.example.com) into a computer-friendly IP address (such as 192.168.1.1). An IP address is given to each device on the Internet, and that address is necessary to find the appropriate Internet device - like a street address is used to find a particular home. When a user wants to load a webpage, a translation must occur between what a user types into their web browser (example.com) and the machine-friendly address necessary to locate the example.com webpage.

### 4 DNS Servers Involved in DNS Resolution

1. **DNS Resolver (Recursive Resolver)**
    - The DNS resolver is the first point of contact for the user. When you type a URL into your browser, the request goes to the DNS resolver. It is typically managed by your ISP (Internet Service Provider) or a third-party provider like Google DNS or Cloudflare DNS.
    - The resolver's job is to take the domain name you entered and find the corresponding IP address. It starts this process by querying other DNS servers in a hierarchical manner.

2. **Root Name Server**
    - After receiving the query from the resolver, the next step is to contact one of the root name servers. There are 13 sets of root name servers worldwide, operated by various organizations.
    - The root name server doesn't have the specific IP address for the domain but directs the resolver to the appropriate Top-Level Domain (TLD) name server based on the TLD (like .com, .org, .net).

3. **TLD Name Server**
    - The TLD name server handles the top-level domains. For example, if you are looking up www.example.com, the resolver will contact the TLD server for .com domains.
    - The TLD server doesn’t have the full IP address but can direct the resolver to the authoritative name server for the specific domain.

4. **Authoritative Name Server**
    - The authoritative name server is the final step in the DNS query process. This server holds the actual DNS records for the domain, including the specific IP address of the website.
    - When the resolver contacts the authoritative name server for www.example.com, it retrieves the IP address and sends it back to the user’s browser, which can then load the webpage.

---

### Steps in a DNS Lookup

For most situations, DNS is concerned with a domain name being translated into the appropriate IP address. To learn how this process works, it helps to follow the path of a DNS lookup as it travels from a web browser, through the DNS lookup process, and back again. Let's take a look at the steps.

Note: Often DNS lookup information will be cached either locally inside the querying computer or remotely in the DNS infrastructure. There are typically 8 steps in a DNS lookup. When DNS information is cached, steps are skipped from the DNS lookup process which makes it quicker. The example below outlines all 8 steps when nothing is cached.

1. A user types ‘example.com’ into a web browser and the query travels into the Internet and is received by a DNS recursive resolver.
2. The resolver then queries a DNS root nameserver (.).
3. The root server then responds to the resolver with the address of a Top Level Domain (TLD) DNS server (such as .com or .net), which stores the information for its domains. When searching for example.com, our request is pointed toward the .com TLD.
4. The resolver then makes a request to the .com TLD.
5. The TLD server then responds with the IP address of the domain’s nameserver, example.com.
6. Lastly, the recursive resolver sends a query to the domain’s nameserver.
7. The IP address for example.com is then returned to the resolver from the nameserver.
8. The DNS resolver then responds to the web browser with the IP address of the domain requested initially.

Once the 8 steps of the DNS lookup have returned the IP address for example.com, the browser is able to make the request for the web page:
- The browser makes an HTTP request to the IP address.
- The server at that IP returns the webpage to be rendered in the browser (step 10).

---

### What is a DNS Resolver?

The DNS resolver is the first stop in the DNS lookup, and it is responsible for dealing with the client that made the initial request. The resolver starts the sequence of queries that ultimately leads to a URL being translated into the necessary IP address.

Note: A typical uncached DNS lookup will involve both recursive and iterative queries.

### Types of DNS Queries

In a typical DNS lookup three types of queries occur. By using a combination of these queries, an optimized process for DNS resolution can result in a reduction of distance traveled. In an ideal situation cached record data will be available, allowing a DNS name server to return a non-recursive query.

1. **Recursive query**: 
    - In a recursive query, a DNS client requires that a DNS server (typically a DNS recursive resolver) will respond to the client with either the requested resource record or an error message if the resolver can't find the record.

2. **Iterative query**: 
    - In this situation, the DNS client will allow a DNS server to return the best answer it can. If the queried DNS server does not have a match for the query name, it will return a referral to a DNS server authoritative for a lower level of the domain namespace. The DNS client will then make a query to the referral address. This process continues with additional DNS servers down the query chain until either an error or timeout occurs.

3. **Non-recursive query**: 
    - Typically this will occur when a DNS resolver client queries a DNS server for a record that it has access to either because it's authoritative for the record or the record exists inside of its cache. Typically, a DNS server will cache DNS records to prevent additional bandwidth consumption and load on upstream servers.

---

### What is DNS Caching? Where Does DNS Caching Occur?

The purpose of caching is to temporarily store data in a location that results in improvements in performance and reliability for data requests. DNS caching involves storing data closer to the requesting client so that the DNS query can be resolved earlier and additional queries further down the DNS lookup chain can be avoided, thereby improving load times and reducing bandwidth/CPU consumption. DNS data can be cached in a variety of locations, each of which will store DNS records for a set amount of time determined by a time-to-live (TTL).

#### Browser DNS Caching

Modern web browsers are designed by default to cache DNS records for a set amount of time. The purpose here is obvious; the closer the DNS caching occurs to the web browser, the fewer processing steps must be taken in order to check the cache and make the correct requests to an IP address. When a request is made for a DNS record, the browser cache is the first location checked for the requested record.

#### Operating System (OS) Level DNS Caching

The operating system level DNS resolver is the second and last local stop before a DNS query leaves your machine. The process inside your operating system that is designed to handle this query is commonly called a “stub resolver” or DNS client. When a stub resolver gets a request from an application, it first checks its own cache to see if it has the record. If it does not, it then sends a DNS query (with a recursive flag set), outside the local network to a DNS recursive resolver inside the Internet service provider (ISP).

When the recursive resolver inside the ISP receives a DNS query, like all previous steps, it will also check to see if the requested host-to-IP-address translation is already stored inside its local persistence layer.

The recursive resolver also has additional functionality depending on the types of records it has in its cache:
- If the resolver does not have the A records, but does have the NS records for the authoritative nameservers, it will query those name servers directly, bypassing several steps in the DNS query. This shortcut prevents lookups from the root and .com nameservers (in our search for example.com) and helps the resolution of the DNS query occur more quickly.
- If the resolver does not have the NS records, it will send a query to the TLD servers (.com in our case), skipping the root server.
- In the unlikely event that the resolver does not have records pointing to the TLD servers, it will then query the root servers. This event typically occurs after a DNS cache has been purged.

---


## DNS Zone Files and Resource Records

### Overview

The DNS (Domain Name System) relies on zone files and various record types to facilitate the resolution process. Zone files are text-based and contain mappings and information about a domain within a DNS zone.

Each line in a zone file specifies a DNS resource record, which provides a specific piece of information about a domain. These resource records are crucial for converting domain names into actionable data that directs users to the correct servers when a query is submitted.

### Structure of DNS Zone Files

DNS zone files begin with two mandatory records:
1. **Global Time to Live (TTL):** Indicates how long records should be stored in the local DNS cache.
2. **Start of Authority (SOA) Record:** Specifies the primary authoritative name server for the DNS zone.

Following these, a zone file can include several other record types:

- **A Records:** Map to IPv4 addresses.
- **AAAA Records:** Map to IPv6 addresses.
- **Mail Exchanger (MX) Records:** Specify an SMTP email server for a domain.
- **Canonical Name (CNAME) Records:** Redirect hostnames from an alias to another domain (the “canonical domain”).
- **Name Server (NS) Records:** Indicate the authoritative name server for a domain.
- **Pointer (PTR) Records:** Specify a reverse DNS lookup, mapping IP addresses back to domain names.
- **Text (TXT) Records:** Include various text information, often used for email authentication via the sender policy framework (SPF).

---

## Public vs. Private DNS Services

### Public DNS

Public DNS refers to the resolver side of DNS, involving recursive servers that query authoritative name servers to connect users to websites. These servers are accessible to any user on the internet. Companies like Cloudflare (1.1.1.1), Quad9, and OpenDNS provide these services free of charge. Public DNS servers are maintained by the organizations that run them, and users have no control over their operation, policies, or configuration.

### Private DNS

Private DNS refers to the authoritative side of DNS within a private network. Organizations set up private DNS servers to provide DNS lookup for internal resources. These servers reside behind a firewall and hold records of internal sites, restricting access to authorized users, devices, and networks. Unlike public DNS, private DNS allows organizations to control their DNS servers, customize DNS records, apply internal naming schemes, and enforce security policies. Organizations are responsible for maintaining the infrastructure, whether hosted on-premises or through cloud services.

---

## DNS Security Risks

Even modern DNS servers are susceptible to cybersecurity issues. Attacks can target either the authoritative or the recursive side of DNS. Common attacks include:

### DNS Spoofing (Cache Poisoning)
Attackers insert false address records into a DNS resolver's cache, causing it to return incorrect IP addresses and redirect users to malicious sites. This can compromise sensitive data and facilitate phishing attacks and malware distribution.

### DNS Amplification Attacks
A type of distributed denial-of-service (DDoS) attack where small queries sent to a DNS server have the return address spoofed to the victim's IP address. The DNS server responds with much larger replies, overwhelming the victim's resources and potentially bringing down the application.

### DNS Tunneling
Encapsulates non-DNS traffic within DNS queries and responses to bypass security measures. Attackers use DNS tunnels to relay malware commands or exfiltrate data from a compromised network, often encoding the payload within DNS queries and responses to evade detection.

### Domain Hijacking
Occurs when an attacker gains unauthorized access to a domain registrar account and changes the domain's registration details. This enables attackers to redirect traffic to malicious servers, intercept emails, and take control of the user's online identity.

### Subdomain Takeover
Neglected DNS entries for subdomains pointing to decommissioned services can be claimed by attackers. If a service (like a cloud host) is decommissioned but the DNS entry remains, an attacker can set up a malicious site or service in its place.



---

More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli