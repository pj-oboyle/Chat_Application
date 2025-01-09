# Network-Based Chat Application

This is a network-based chat application built using the Java Socket API. It supports real-time communication between clients and provides essential chat room functionalities. The application is implemented with four key classes: `Server`, `ClientThread`, `Client`, and `Addressing`.

---

## Features

### **1. Client-Side Functionalities**
- **Reconnection Handling:**
  - The client attempts to reconnect to the server at set intervals if a connection is lost.
  - If reconnection fails, the client prompts the user to either close the application or enter another port number.

- **Chat Commands:**
  1. `\q` - Exits the chat room and closes the client.
  2. `#userlist` - Displays a list of all active users in the chat room.
  3. `#*` - Sends a private message to a specified user (e.g., `#ann` sends a direct message to the user "ann").

- **Username Handling:**
  - If a username is already taken, a numerical suffix is automatically appended to the username.

- **Message Prefixing:**
  - Messages in the chat room are prefixed with the sender's username.

- **User Join/Leave Notifications:**
  - Users are notified when someone enters or leaves the chat room.

### **2. Chat Room Functionalities**
- **Multi-User Chat:**
  - The server supports multiple clients simultaneously through multithreading.

- **Private Messaging:**
  - Users can send direct messages to specific individuals in the chat room.

### **3. Server-Side Functionalities**
- **Port Logging:**
  - The server logs the port numbers of connected clients.

- **Resource Management:**
  - When a client disconnects, the server confirms the closure of the client's I/O resources in the console log.

### **4. Networking Functionalities**
- Utilizes Java's `Socket` and `ServerSocket` APIs to establish TCP connections between the client and server.
- Threads handle client connections, ensuring efficient communication for multiple users.

---

## Implementation Overview

### **Key Classes**
1. **`Server`**
   - Creates a `ServerSocket` to listen for incoming connections.
   - Uses the `accept()` method to establish a `Socket` connection with clients.
   - Handles multiple client connections via the `ClientThread` class.

2. **`ClientThread`**
   - Implements `Runnable` to handle communication with individual clients on separate threads.
   - Enables simultaneous message exchanges with multiple clients.

3. **`Client`**
   - Creates a `Socket` to connect to the server.
   - Implements `sendMessage()` to send messages and `listen()` to receive incoming messages.
   - Runs the `listen()` method in a separate thread to allow real-time message handling while awaiting user input.

4. **`Addressing`**
   - Contains shared methods for assigning IP addresses and port numbers for both client and server applications.

---

## Prerequisites
- **Java Development Kit (JDK)**: Version 8 or above.
- **Integrated Development Environment (IDE)**: Eclipse, IntelliJ IDEA, or any text editor with Java support.

---

## Installation
1. Clone the repository or download the source code.
   ```bash
   git clone https://github.com/your-username/java-socket-chat-app.git
   ```
2. Open the project in your IDE.
3. Compile the Java files:
   ```bash
   javac *.java
   ```
4. Run the server application first, then start one or more client applications.

---

## Usage

### **Server Application**
1. Run the server application.
2. Enter a valid port number when prompted.
3. The server will start listening for connections and log activity in the console.

### **Client Application**
1. Run the client application.
2. Follow the prompts:
   - Enter an IP address (default: `localhost`).
   - Enter a valid port number.
   - Input a username.
3. The client will attempt to connect to the server and display a message indicating whether the connection was successful.

---

## License
This project is licensed under the MIT License. See the [LICENSE](LICENSE) file for details.

---

## Contact
For further information, contact:
- **Name:** pj-oboyle
- **GitHub:** [pj-oboyle](https://github.com/pj-oboyle)

---

Thank you for exploring this project! If you found it useful, please give it a star on GitHub.

