When you type a URL into a browser and press Enter, several steps occur to retrieve and display the webpage:

Let's understand what is URL first 

A URL (Uniform Resource Locator) is the address used to access resources on the internet. It consists of several parts, each with a specific function. Here’s a breakdown of its components:

1. **Scheme**: This indicates the protocol used to access the resource. Common schemes include:
   - `http`: Hypertext Transfer Protocol
   - `https`: Secure Hypertext Transfer Protocol
   - `ftp`: File Transfer Protocol
   - `mailto`: Email address
   - Example: `https://`

2. **Host**: The domain name or IP address of the server where the resource is located. This part is necessary to locate the server on the internet.
   - Example: `www.example.com`

3. **Port** (optional): Specifies the port number on the server to connect to. If omitted, default ports are used (e.g., 80 for HTTP, 443 for HTTPS).
   - Example: `:443`

4. **Path**: The specific location of the resource on the server. This often corresponds to a file or directory on the server.
   - Example: `/path/to/resource`

5. **Query String** (optional): Provides additional parameters for the resource, usually in key-value pairs. It begins with a `?` and separates parameters with `&`.
   - Example: `?key1=value1&key2=value2`

6. **Fragment** (optional): A section within the resource, indicated by a `#`. It’s often used to direct the browser to a specific part of a webpage.
   - Example: `#section2`

Putting it all together, a complete URL might look like this:
```
https://www.example.com:443/path/to/resource?key1=value1&key2=value2#section2
```

### Example Breakdown:
- **Scheme**: `https`
- **Host**: `www.example.com`
- **Port**: `443`
- **Path**: `/path/to/resource`
- **Query String**: `?key1=value1&key2=value2`
- **Fragment**: `#section2`

Each part plays a crucial role in ensuring that the browser can locate, request, and display the correct resource on the internet.

## How it works

**DNS Lookup**: 
The browser contacts a Domain Name System (DNS) server to translate the human-readable URL (e.g., www.example.com) into an IP address, which is necessary for locating the server hosting the website.

When performing a DNS lookup, the process typically checks for cached information before querying an external DNS resolver. 

Browser Cache: The browser first checks its own cache.

Operating System Cache: If the browser cache does not contain the necessary information, the operating system's DNS cache is checked next.
 
 **DNS Resolver** (ISP or Configured DNS Server): If the requested DNS information is not found in the browser cache, operating system cache, or local hosts file, the request is sent to a DNS resolver. This is usually provided by the Internet Service Provider (ISP) or a configured third-party DNS service (such as Google Public DNS or OpenDNS).


**TCP/IP Connection**: The browser initiates a Transmission Control Protocol (TCP) connection with the server using the IP address. This involves a handshake process to establish a reliable connection.

**HTTP Request**: Once the connection is established, the browser sends an HTTP request to the server, typically a GET request asking for the webpage.

**Server Response**: The server processes the request and sends back an HTTP response, which includes the status code (e.g., 200 OK), headers, and the requested content (e.g., HTML, CSS, JavaScript).

**Rendering**: The browser receives the content and begins rendering the webpage. This involves parsing the HTML, constructing the Document Object Model (DOM), and loading any linked resources (CSS files, JavaScript files, images, etc.).

**Executing Scripts**: The browser executes any JavaScript code, which may further modify the DOM and request additional resources from the server.

**Displaying Content**: Finally, the browser displays the fully rendered webpage to the user.

Throughout this process, the browser might also handle cookies, manage security aspects like HTTPS encryption, and employ caching mechanisms to speed up subsequent visits to the same website.****


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/h2mmrruu9rk5xvaj9h9p.png)


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/qcchzjg2nsexqpr9y2bq.png)





Get all article related to system design 
Hastag: SystemDesignWithZeeshanAli


