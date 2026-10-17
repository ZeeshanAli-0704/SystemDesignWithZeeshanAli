

# Client-Server Design Pattern


![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/27cyzf74jdy3xjackqqk.png)



This illustrates the client-server architecture. In this approach, clients, like your device, request services or resources from servers, such as the system at a restaurant. Servers handle these requests and respond with the required data, resulting in a dynamic and effective interaction. A variety of online services, including websites and emails, are supported by this architecture, enabling seamless resource sharing and communication between clients and servers through networks like the Internet.

## Understanding the Client-Server Model

### What Is Client-Server Architecture?

A client-server architecture is a system that hosts, provides, and manages most of the resources and services that the client requests. This approach, also known as the network computing model or client-server network, involves the delivery of all requests and services across a network.

The client-server pattern has two major entities: a server and multiple clients. The server has resources (data, files, or services) that a client requests. The server then processes the request and responds accordingly.

### Advantages

- **Centralized Management**: Servers can centrally manage resources, data, and security policies, simplifying maintenance.
- **Scalability**: Servers can be scaled up to handle increased client requests.
- **Security**: Security measures such as access controls and data encryption can be better implemented due to centralized controls.

## How the Client-Server Model Works

### Client-Server Communication Process

To understand how clients and servers communicate, we need a basic understanding of the following subjects:

- **Requests**: Requests are sent from the client to the server to inform it of events, such as a user wishing to log in using their credentials or to ask the server for data, such as files.
- **Response**: A server’s response to a client request is sent to the client as a message. This might, for instance, be the outcome of an authentication.
- **Service**: A particular task the server makes available to the client for use, such as downloading an image.

### Role of Protocols in Communication

TCP/IP is the protocol suite most often used by clients to connect to servers. TCP, being a connection-oriented protocol, establishes and maintains connections until the application programs at either end have exchanged messages. TCP protocols facilitate the following:

- Determining the packetization of application data.
- Sending packets to the network layer and receiving packets from it.
- Overseeing traffic flow management.
- Retransmitting dropped or jumbled packets as needed.
- Acknowledging each and every packet that enters the network.

TCP covers portions of Layer 4 (the transport layer) and Layer 5 (the session layer) in the OSI model (Open Systems Interconnection communication model).

## Explaining Client-Server Architecture

### Types of Client-Server Architectures Patterns

#### 1-Tier Structure

In a 1-tier architecture, the user interface, business logic, and data logic are all on the same system. Because the client and server are on the same system, the environment is straightforward and inexpensive, but the variation in the data necessitates repetitive effort. These systems keep their data in a shared driver or a local file. Examples of 1-tier applications include MP3 players and MS Office files.

#### 2-Tier Structure

In a 2-tier architecture, the application logic is either buried within the user interface on the client or within the database on the server (or both). The user system interface is usually located in the user’s desktop environment, and the database management services are usually in a server that is a more powerful machine serving many clients.

- **Fat Client-Thin Server Architecture**: If both application logic and user interface sit at the client end.
- **Thin Client-Fat Server Architecture**: If both application logic and database management services sit at the server end.

A 2-tier architecture is typically used in online ticket reservation systems.

#### 3-Tier Structure

A middleware is a component of the 3-tier architecture that facilitates communication between the client and the server. Despite being pricey, it is quite simple to use. The middleware enhances flexibility and performance. The data logic and business logic are stored there. The 3-tier architecture’s three layers are:

- **Client Tier**: Presentation Layer
- **Business Tier**: Application Layer
- **Data Tier**: Database Layer

In almost all cases, a 3-tier design is used in online applications.

#### N-Tier Structure (Multi-Tier Application)

An N-tier architecture is a multilayered client-server architecture in which the presentation, processing, and data functions are divided into logically and physically separate tiers. Being physically separated means that each of these functions executes on different physical machines, sometimes in different geographical locations.

N-tier architecture divides an application into logical layers and physical tiers. Layers are a way to separate responsibilities and manage dependencies. Each layer has a specific responsibility. A higher layer can use services in a lower layer, but not the other way around.



![Image description](https://dev-to-uploads.s3.amazonaws.com/uploads/articles/6y04cru5br6vxqihs4ln.png)


More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli


[systemdesignwithzeeshanali](https://dev.to/t/systemdesignwithzeeshanali)

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli