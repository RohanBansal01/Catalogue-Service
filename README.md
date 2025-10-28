<div align="center">
  <h1>🛍️ Catalogue Service API</h1>
</div>

<p align="center">
  <img src="https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen" alt="Spring Boot" /> &nbsp;&nbsp;
  <img src="https://img.shields.io/badge/Java-21-blue" alt="Java" /> &nbsp;&nbsp;
  <img src="https://img.shields.io/badge/MySQL-8.0.44-orange" alt="MySQL" /> &nbsp;&nbsp;
  <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License: MIT" />
</p>

> A clean and modular **Spring Boot microservice** for managing product catalogues — including **categories** and **products** — with standardized API responses, JPA integration, and centralized exception handling.

---

## 📑 Table of Contents

1. [Overview](#-overview)
2. [Tech Stack](#-tech-stack)
3. [Architecture Overview](#-architecture-overview)
   * [Layer Responsibilities](#layer-responsibilities)
4. [Folder Structure](#-folder-structure)
5. [End-to-End Request Flow](#-end-to-end-request-flow)
   * [Step-by-Step Flow](#-step-by-step-flow)
   * [Summary Table](#-summary-table)
6. [Entity Relationship](#-entity-relationship)
7. [API Endpoints](#-api-endpoints)
   * [Category APIs](#-category-apis)
   * [Product APIs](#-product-apis)
8. [Sample Payloads](#-sample-payloads)
9. [Standardized Response Format](#-standardized-response-format)
10. [Exception Handling](#-exception-handling)
11. [Configuration](#-configuration)
12. [How to Run Locally](#-how-to-run-locally)
13. [Example Responses](#-example-responses)
14. [Future Enhancements](#-future-enhancements)
15. [Author](#-author)
16. [License](#-license)


---
## 📘 Overview

The **Catalogue Service** provides a set of RESTful APIs to manage **categories** and **products**.  
It’s designed using **industry-standard best practices**, ensuring scalability, clean separation of concerns, and consistent response structures.

You can use it:
- As a **standalone backend** for catalogue management, or  
- As part of a **larger e-commerce microservice ecosystem**

---

## ⚙️ Tech Stack




| **Layer** | **Technology** |
|------------|----------------|
| Framework | Spring Boot 3.3.x (built on Spring Framework 6.2.x) |
| Language | Java 21.0.8 2025-07-15 LTS |
| ORM | Spring Data JPA (Hibernate ORM 6.x) |
| Database | MySQL Community Server 8.0.44 (GPL) |
| Validation | Jakarta Bean Validation 3.0 |
| API Docs | Springdoc OpenAPI 2.8.12 (Swagger UI) |
| Logging | SLF4J + Logback |
| Utilities | Lombok |
| Build Tool | Apache Maven|

---

## 🧱 Architecture Overview

The project follows a **multi-layered architecture** to promote modularity and maintainability:


<img width="844" height="304" alt="Architecture Flow" src="https://github.com/user-attachments/assets/0425003f-d7a0-486a-bf72-02949be0d1b4" />

```


```
---
### **Layer Responsibilities**

- **Controller:** Handles HTTP requests and builds structured responses  
- **Service:** Implements core business logic  
- **Repository:** Manages database interactions via JPA  
- **Model:** Defines database entities  
- **Payload:** Defines standardized API response & error formats  
- **Exception:** Centralized exception handling for clean error reporting  

---

## 📁 Folder Structure

```bash
src/main/java/com/solveda/catalogueservice/
│
├── controller/          # REST controllers
├── service/             # Business logic layer
│   └── impl/            # Concrete service implementations
├── repository/          # Data access layer (JPA repositories)
├── model/               # Entities (Category, Product)
├── payload/             # Response & error structures
├── exception/           # Custom exceptions + global handler
└── CatalogueServiceApplication.java

```
---


## 🔄 End-to-End Request Flow

This section explains how a request travels through the **Catalogue Service** —  
from the client → all backend layers → to the database → and back as a structured JSON response.

---

### 🧭 Step-by-Step Flow

```
Client (Frontend / Postman / API Gateway)
│
▼
───────────────────────────────────────────────
1️⃣ HTTP Request (e.g., GET /api/products/1)
───────────────────────────────────────────────
│
▼
┌───────────────────────────────┐
│     DispatcherServlet         │
│ (Spring Boot Front Controller)│
│ Receives the request and maps │
│ it to the correct controller  │
└───────────────────────────────┘
│
▼
───────────────────────────────────────────────
2️⃣ Controller Layer
───────────────────────────────────────────────
```
📦 **Package:** `controller`

* Contains `@RestController` classes (e.g. `ProductController`)
* Validates input payloads
* Calls corresponding Service layer
* Wraps responses using `ResponseStructure<T>`

Example:
`ProductController → getProductById(Long id)`
```bash
│
▼
```
```
───────────────────────────────────────────────
3️⃣ Service Layer
───────────────────────────────────────────────
```
📦 **Package:** `service`

* Contains business logic
* Validates data consistency
* Converts entities to DTOs
* Handles domain rules
* Interacts with repository

Example:
`ProductService → fetches product by ID`
If not found → throws `ProductNotFoundException`
```bash
│
▼
```
```
───────────────────────────────────────────────
4️⃣ Repository Layer
───────────────────────────────────────────────
```
📦 **Package:** `repository`

* Extends `JpaRepository`
* Handles DB operations
* Uses Spring Data JPA
* Talks to MySQL through Hibernate ORM

Example:
`ProductRepository.findById(id)`
```bash
│
▼
```
```
───────────────────────────────────────────────
5️⃣ Model Layer
───────────────────────────────────────────────
```
📦 **Package:** `model`

* Defines database entities (`Category`, `Product`)
* Mapped with JPA annotations
* Represents actual DB tables

Example:
`Product entity ↔ product table`
`Category entity ↔ category table`
```bash
│
▼
```
```
───────────────────────────────────────────────
6️⃣ Data Fetched & Transformed
───────────────────────────────────────────────
```
📦 **Service Layer**

* Receives Entity object from Repository
* Maps Entity → Response DTO
* Removes unnecessary fields
* Prepares frontend-ready data

Example:
`Product Entity → ProductResponse DTO`
(Only id, name, price, category shown)
```bash
│
▼
```
```
───────────────────────────────────────────────
7️⃣ Payload Layer
───────────────────────────────────────────────
```
📦 **Package:** `payload`

* Defines reusable structures like `ResponseStructure<T>` and `ErrorStructure`
* Ensures consistent API responses across endpoints

Example:

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "name": "Smartphone",
    "price": 49999,
    "category": "Electronics"
  },
  "error": null
}
```

```bash
    │
    ▼
```
```
───────────────────────────────────────────────
8️⃣ Exception Layer (if any error occurs)
───────────────────────────────────────────────
```
📦 **Package:** `exception`

* Contains custom exceptions (`ProductNotFoundException`)
* GlobalExceptionHandler catches all runtime errors
* Converts them into standardized error responses

Example:

```json
{
  "status": "error",
  "data": null,
  "error": {
    "errorCode": "PRODUCT_NOT_FOUND",
    "errorMessage": "Product with ID 101 not found",
    "errorSource": "ProductService"
  }
}
```

```
    │
    ▼
```
```
───────────────────────────────────────────────
9️⃣ Response Sent Back to Client
───────────────────────────────────────────────
```
Client receives a clean, structured JSON
ready to be rendered on the frontend.




---

### 🧩 Summary Table

| Stage | Package | Purpose |
|--------|----------|----------|
| 1️⃣ | **controller** | Handles HTTP requests, builds responses |
| 2️⃣ | **service** | Contains business logic, validation, mapping |
| 3️⃣ | **repository** | Interacts with DB using JPA |
| 4️⃣ | **model** | Defines database entities |
| 5️⃣ | **payload** | Standard response & error wrappers |
| 6️⃣ | **exception** | Handles and formats all application errors |

---

---

## 🧩 Entity Relationship

**Category ↔ Product → One-to-Many Relationship**

- A **Category** can contain multiple **Products**
- Each **Product** belongs to exactly one **Category**

---

## 📚 API Endpoints

### 🔹 Category APIs

| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/categories` | Create a new category |
| `GET` | `/api/categories` | Get all categories |
| `GET` | `/api/categories/{id}` | Get category by ID |
| `GET` | `/api/categories/title/{title}` | Get category by title |
| `PUT` | `/api/categories/{id}` | Update existing category |
| `DELETE` | `/api/categories/{id}` | Delete category by ID |

---

### 🔹 Product APIs

| Method | Endpoint | Description |
|--------|-----------|-------------|
| `POST` | `/api/products` | Create a new product |
| `GET` | `/api/products` | Get all products |
| `GET` | `/api/products/{id}` | Get product by ID |
| `GET` | `/api/products/sku/{sku}` | Get product by SKU |
| `PUT` | `/api/products/{id}` | Update existing product |
| `DELETE` | `/api/products/{id}` | Delete product by ID |

---

## 🧾 Sample Payloads

### ➕ Create Category
```json
{
  "title": "Electronics",
  "description": "All electronic gadgets and accessories"
}
````

### ➕ Create Product

```json
{
  "name": "Smartphone",
  "description": "Android 14, 128GB storage",
  "price": 49999,
  "stockQuantity": 50,
  "sku": "ELEC-001",
  "active": true,
  "category": { "id": 1 }
}
```

---

## 🧱 Standardized Response Format

All API responses follow a unified structure using `ResponseStructure<T>`.

### ✅ Success Example

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "title": "Electronics",
    "description": "All electronic gadgets and accessories"
  },
  "error": null
}
```

### ❌ Error Example

```json
{
  "status": "error",
  "data": null,
  "error": {
    "errorCode": "PRODUCT_NOT_FOUND",
    "errorMessage": "Product with ID 101 not found",
    "errorSource": "ProductService"
  }
}
```

---

## 🚨 Exception Handling

Centralized exception handling ensures consistent and readable error messages.
All exceptions are managed via `GlobalExceptionHandler`.

| **Exception Class**          | **Description**                            |
| ---------------------------- | ------------------------------------------ |
| `CategoryNotFoundException`  | Thrown when a category does not exist      |
| `ProductNotFoundException`   | Thrown when a product does not exist       |
| `InvalidCategoryException`   | Thrown for invalid category input          |
| `InvalidProductException`    | Thrown for invalid product input           |
| `DatabaseOperationException` | Handles database-level failures            |
| `GlobalExceptionHandler`     | Converts all exceptions into API responses |

---

## ⚙️ Configuration

**`src/main/resources/application.properties`**

```properties
spring.application.name=catalogue-service

# Database Configuration
spring.datasource.url=jdbc:mysql://localhost:3306/catalogueservicedb?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=root

# JPA / Hibernate
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQL8Dialect

# Swagger Documentation
springdoc.swagger-ui.path=/swagger-ui.html
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true

# Logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## 🧭 How to Run Locally

### 1️⃣ Clone the Repository

```bash
git clone https://github.com/your-username/catalogue-service.git
cd catalogue-service
```

### 2️⃣ Configure Database

Make sure MySQL is running locally and update credentials in `application.properties`.

### 3️⃣ Build and Run

```bash
mvn clean install
mvn spring-boot:run
```
💻 Alternatively, open the project in IntelliJ IDEA, navigate to
CatalogueServiceApplication.java, and simply click ▶ Run to start the application.

### 4️⃣ Access the Application

* Base URL: [http://localhost:8080/api](http://localhost:8080/api)
* Swagger UI: [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)


---

## 🧪 Example Responses

### ✅ Get Category by ID

**Request:**

```
GET /api/categories/1
```

**Response:**

```json
{
  "status": "success",
  "data": {
    "id": 1,
    "title": "Electronics",
    "description": "All electronic gadgets and accessories"
  },
  "error": null
}
```

### ❌ Product Not Found

**Request:**

```
GET /api/products/999
```

**Response:**

```json
{
  "status": "error",
  "data": null,
  "error": {
    "errorCode": "PRODUCT_NOT_FOUND",
    "errorMessage": "Product with ID 999 not found",
    "errorSource": "ProductService"
  }
}
```

---

## 🚀 Future Enhancements

* ⬜ JWT-based authentication & role-based access control
* ⬜ Docker & containerized deployment
* ⬜ Pagination & filtering for product APIs
* ⬜ Redis caching for frequently accessed data
* ⬜ Integration with API Gateway / Config Server

---

## 👨‍💻 Author

**Rohan Bansal**
📧 **[rohanbansalcse@gmail.com](mailto:rohanbansalcse@gmail.com)**


---

## 🪪 License

This project is licensed under the **MIT License**.
You are free to use, modify, and distribute with proper attribution.



> 💡 *“Clean code and predictable APIs are the foundation of scalable systems.”*





