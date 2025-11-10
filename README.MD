# 🛒 Collectible Marketplace – Java Spark Web Application

## 📖 Overview
This project implements an online marketplace for selling collectible items, developed using **Java** and the **Spark framework**.  

The application allows users to register, manage their profiles, list collectible items, make and receive offers, and view real-time price updates through **WebSockets**.  
All backend routes, validation, and data access operations are implemented using modular controllers and DAO patterns.

---

## ⚙️ Main Features
- **User Management:** Create, update, and delete user accounts through RESTful endpoints.
- **Item Management:** Add, edit, and delete collectible items with detailed descriptions.
- **Offer System:** Submit and manage offers on items using form-based interactions.
- **Real-Time Updates:** Integrated WebSocket functionality for live price and offer updates.
- **Filtering & Search:** Dynamic filtering of collectibles by price, category, or keyword.
- **Error Handling:** Unified response models for success and error states.
- **Validation Layer:** Input validation to ensure clean and secure operations.

---

## 🧩 System Architecture

### Layers and Components
| Layer | Description | Main Files |
|-------|--------------|-------------|
| **Controllers** | Define endpoints for API and WebSocket communication. Handle routing, request parsing, and responses. | `UserApiController.java`, `ItemApiController.java`, `OfferApiController.java`, `AuctionViewController.java`, `OfferWebSocket.java` |
| **Data Access (DAO)** | Handles database operations (CRUD) using JDBI. | `UserDao.java`, `ItemDao.java`, `OfferDao.java` |
| **Models** | Define entities used across the app (User, Item, Offer) and response wrappers. | `User.java`, `Item.java`, `Offer.java`, `ErrorResponse.java`, `SuccessResponse.java`, `UserItemsResponse.java` |
| **Validation** | Manages item input validation and field rules. | `ItemValidator.java` |
| **Utilities** | Provides helper functions such as database seeding. | `DatabaseSeeder.java` |
| **Application Entry Point** | Configures routes, initializes DAOs, and starts the Spark server. | `Main.java` |

---

## 🧠 Technologies Used
- **Java 17+**
- **Spark Framework** – lightweight web framework for Java.
- **Maven** – dependency and build management.
- **Gson** – JSON serialization/deserialization.
- **JDBI** – SQL and data access layer.
- **WebSocket (Jetty)** – real-time event communication.
- **Mustache Templates** – frontend view rendering.
- **Bootstrap & Vanilla JS** – user interface and interactivity.

---

## 🧾 API Endpoints

### 👤 User Routes
| Method   | Endpoint        | Description        |
|----------|-----------------|--------------------|
| `GET`    | `apiapi/users`  | Retrieve all users |
| `POST`   | `api/users`     | Create a new user  |
| `PUT`    | `api/users/:id` | Update user by ID  |
| `DELETE` | `api/users/:id` | Delete user by ID  |

### 📦 Item Routes
| Method   | Endpoint        | Description                |
|----------|-----------------|----------------------------|
| `GET`    | `api/items`     | Retrieve all items         |
| `POST`   | `api/items`     | Add a new collectible item |
| `PUT`    | `api/items/:id` | Update item information    |
| `DELETE` | `api/items/:id` | Remove item by ID          |

### 💰 Offer Routes
| Method   | Endpoint         | Description          |
|----------|------------------|----------------------|
| `GET`    | `api/offers`     | Retrieve all offers  |
| `POST`   | `api/offers`     | Submit a new offer   |
| `PUT`    | `api/offers/:id` | Update offer details |
| `DELETE` | `api/offers/:id` | Delete offer by ID   |

### 🔄 WebSocket
| Event     | Description                                                             |
|-----------|-------------------------------------------------------------------------|
| `connect` | Establish connection between client and server                          |
| `message` | Broadcasts real-time updates (offers and prices) to all active sessions |
| `close`   | Disconnects user session gracefully                                     |

---

## 🧰 Setup & Installation

### Prerequisites
- Java JDK 17+
- Maven 3.8+
- MariaDB or MySQL server installed and running

### Steps
1. **Clone the repository:**
   ```bash
   git clone https://github.com/Emisario-py/Challenge-6-Java-Spark-for-web-app.git
   cd Challenge-6-Java-Spark-for-web-app
   cd App-Spark
2. **Build and Run the project**
    ```bash
   mvn clean compile
   mvn exec:java
3. **Access the app**
    ```bash
   http://localhost:4567

### Development work flow
| Stage        | Description                                                | Deliverable                    |
|--------------|------------------------------------------------------------|--------------------------------|
| **Sprint 1** | API service for users and items (CRUD endpoints).          | Working REST API               |
| **Sprint 2** | Functional templates, offer form, and validation handling. | Interactive front-end pages    |
| **Sprint 3** | Filtering and real-time updates via WebSockets.            | Dynamic, real-time marketplace |

### Author

Emiliano Osuna