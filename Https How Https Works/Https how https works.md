# What is HTTPS?

HTTPS (HyperText Transfer Protocol Secure) is an extension of HTTP and uses TLS (Transport Layer Security) to encrypt data between the client and server. The HTTPS handshake is a critical part of this process, ensuring that the communication is secure.

### Working of HTTPS Handshake

HTTPS (HyperText Transfer Protocol Secure) is an extension of HTTP and uses TLS (Transport Layer Security) to encrypt data between the client and server. The HTTPS handshake is a critical part of this process, ensuring that the communication is secure.

#### Protocol Used:
- **TLS (Transport Layer Security):** This protocol ensures data privacy and integrity between the client and server.

#### Steps of the HTTPS Handshake:

1. **TCP Handshake:**
   - **TCP SYN:** The client sends a TCP SYN packet to the server to initiate a TCP connection.
   - **TCP SYN + ACK:** The server responds with a TCP SYN-ACK packet, acknowledging the connection request.
   - **TCP ACK:** The client sends a TCP ACK packet to acknowledge the server's response, completing the TCP handshake.

2. **Client Hello:**
   - The client sends a "Client Hello" message to the server. This message includes:
     - Supported TLS versions.
     - Cipher suites (encryption algorithms).
     - A randomly generated number.

3. **Server Hello:**
   - The server responds with a "Server Hello" message, which includes:
     - Selected TLS version.
     - Selected cipher suite.
     - A randomly generated number.

4. **Server Certificate:**
   - The server sends its certificate to the client. This certificate contains the server's public key and is signed by a trusted CA.

5. **Server Key Exchange (if necessary):**
   - Depending on the cipher suite, the server may send additional key exchange information.

6. **Server Hello Done:**
   - The server signals that it has finished its initial handshake messages.

7. **Client Key Exchange:**
   - The client generates a pre-master secret key and encrypts it using the server's public key, then sends it to the server.

8. **Generate Session Keys:**
   - Both the client and the server use the pre-master secret and the random numbers to generate the session keys, which are symmetric keys used for encrypting the data during the session.

9. **Change Cipher Spec (Client):**
   - The client sends a "Change Cipher Spec" message to indicate that subsequent messages will be encrypted with the session key.

10. **Client Finished:**
    - The client sends a "Finished" message, encrypted with the session key, to indicate that the client part of the handshake is complete.

11. **Change Cipher Spec (Server):**
    - The server sends a "Change Cipher Spec" message to indicate that subsequent messages will be encrypted with the session key.

12. **Server Finished:**
    - The server sends a "Finished" message, encrypted with the session key, to indicate that the server part of the handshake is complete.

Once the handshake is complete, the client and server use the session keys to encrypt and decrypt the data they exchange.

#### Advantages of Using HTTPS:

1. **Encryption:** Data exchanged between the client and server is encrypted, protecting it from eavesdroppers and attackers.
2. **Data Integrity:** Ensures that data cannot be modified or corrupted during transfer without being detected.
3. **Authentication:** Verifies that the client is communicating with the legitimate server, preventing man-in-the-middle attacks.
4. **Trust:** Increases user trust in the website, as modern browsers display a padlock icon for HTTPS connections, indicating that the connection is secure.

### Diagram:

Below is a simplified diagram illustrating the HTTPS handshake process:

```
 Client                                 Server

   |                                      |
   |-------- TCP SYN--------------------->|
   |                                      |
   |<--------- TCP SYN + ACK -------------|
   |                                      |
   |-------- TCP ACK -------------------->|
   |                                      |
   |                                      |
   |                                      |
   |-------- Client Hello --------------->|
   |                                      |
   |<--------- Server Hello --------------|
   |                                      |
   |<-------- Server Certificate (KEY)----|
   |<------ Server Key Exchange (if) -----|
   |<-------- Server Hello Done ----------|
   |                                      |
   |                                      |
   |--------- Client Key Exchange ------->|
   |                                      |
   |------ Change Cipher Spec ----------->|
   |--------- Client Finished ----------->|
   |                                      |
   |<------ Change Cipher Spec -----------|
   |<--------- Server Finished -----------|
   |                                      |
   |                                      |
   |------ Encrypted Data --------------->|
   |<--------- Encrypted Data ------------|
   |                                      |



```

#### Detailed Steps:

1. **TCP Handshake:**
   - Client initiates the connection with TCP SYN.
   - Server responds with TCP SYN-ACK.
   - Client acknowledges with TCP ACK.

2. **Client Hello:**
   - Client sends supported TLS versions, cipher suites, and a random number.

3. **Server Hello:**
   - Server responds with selected TLS version, cipher suite, and a random number.

4. **Server Certificate:**
   - Server sends its digital certificate for authentication.

5. **Server Key Exchange (if necessary):**
   - Server may send additional key exchange information.

6. **Server Hello Done:**
   - Server indicates the end of its initial messages.

7. **Client Key Exchange:**
   - Client sends the pre-master secret encrypted with the server's public key.

8. **Change Cipher Spec (Client):**
   - Client signals the start of encrypted communication.

9. **Client Finished:**
   - Encrypted message indicating client is ready.

10. **Change Cipher Spec (Server):**
    - Server signals the start of encrypted communication.

11. **Server Finished:**
    - Encrypted message indicating server is ready.

12. **Encrypted Data:**
    - Secure data exchange begins with encrypted data packets.

This detailed process ensures a secure, encrypted connection between the client and server, protecting data integrity and privacy.



More Details:

Get all articles related to system design 
Hastag: SystemDesignWithZeeshanAli

Git: https://github.com/ZeeshanAli-0704/SystemDesignWithZeeshanAli

Dev.to Article : https://dev.to/zeeshanali0704/https-how-https-works-handshake-1mjo