# notadomain

https://github.com/user-attachments/assets/09b2efdc-05c7-41d7-a3a9-8de19283bf1d

- [Overview](#overview)
- [Technologies](#technologies)
- [Architecture & Design](#architecture--design)
- [Features](#features)
- [How to Run](#how-to-run)

## Overview

Distributed email system with multi-threaded server and JavaFX clients. Implements simplified SMTP protocol for sending, receiving, and managing emails over TCP sockets.

## Technologies

- **Java 11** | **JavaFX 17** | **Maven**
- **GSON (fx-gson 3.1.2)** - JSON serialization
- **Java Socket API** - TCP/IP communication
- **Java Concurrency** - ExecutorService, ReadWriteLock

## Architecture & Design

Classic **client-server** model over TCP (port 1999). **Client** uses **MVC** with JavaFX controllers, **SocketManager** for async communication (ExecutorService with timeouts), **Command pattern** (TypeOfRequest enum), and **Observer pattern** (JavaFX properties for reactive UI). **Server** employs **thread-per-request**: ConnectionManager accepts sockets and submits ClientTask to ExecutorService, which routes requests to appropriate Engine via **Strategy pattern**. **ReadWriteLock** per user ensures thread-safe JSON file access (multiple readers OR single writer).

**Communication:** Client sends serialized ClientRequest (TypeOfRequest, User, Email); server responds with ServerResponse (ResponseStatus, message, result). Operations: `GET_INBOX`, `GET_SEND`, `SEND_EMAIL`, `DELETE_EMAIL`, `GET_NOTIFICATION_INBOX`, `OPEN_EMAIL`.

**Storage:** User emails stored in `data/<username>.json` (GSON serialization, ReadWriteLock protected).

## Features

**Client:**
- User login and authentication
- Compose/send emails with multiple recipients and CC support
- View inbox, sent emails, and drafts
- Delete emails and mark as read/unread
- Reply and forward functionality
- Automatic inbox refresh

**Server:**
- Multi-threaded concurrent client handling
- Thread-safe JSON-based email storage
- Comprehensive logging system
- Real-time server activity monitoring
- Graceful startup/shutdown

## How to Run

**Prerequisites:** JDK 11+, Maven 3.6+, JavaFX 17

**Build & Run:**
```bash
# Server (starts on port 1999)
cd Server && mvn clean compile && mvn javafx:run

# Client (launch multiple instances)
cd Client && mvn clean compile && mvn javafx:run
```

**Configuration:** Default `localhost:1999` | Client: `client.properties` | Server: GUI settings

