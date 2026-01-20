<div align="center">
  <h1>🛍️ Catalogue Service API</h1>
</div>
<p align="center">
  <a href="https://spring.io/projects/spring-boot">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.3.x-brightgreen" alt="Spring Boot" />
  </a>
  &nbsp;
  <a href="https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html">
    <img src="https://img.shields.io/badge/Java-21-blue" alt="Java" />
  </a>
  &nbsp;
  <a href="https://www.mysql.com/">
    <img src="https://img.shields.io/badge/MySQL-8.0.44-orange" alt="MySQL" />
  </a>
  &nbsp;
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License: MIT" />
  </a>
</p>



---

## 📘 Overview

The **Catalogue Service** manages:

* 📂 Categories
* 📦 Products
* 📊 Inventory
* 💰 Pricing
* 📥 Bulk Import (JSON + File Upload)

Designed using **clean architecture**, **DTO-based request/response**, **service layer separation**, and **QA-ready API structure**.

---

## ⚙️ Tech Stack

| Layer      | Technology                      |
| ---------- | ------------------------------- |
| Framework  | Spring Boot 3.3.x               |
| Language   | Java 21 (LTS)                   |
| ORM        | Spring Data JPA (Hibernate 6.x) |
| Database   | MySQL 8.x                       |
| Validation | Jakarta Bean Validation         |
| API Docs   | Springdoc OpenAPI (Swagger UI)  |
| Logging    | SLF4J + Logback                 |
| Utilities  | Lombok                          |
| Build Tool | Maven                           |

---

## 🧱 Architecture Overview

**Layered Architecture**

* **Controller Layer** → REST APIs
* **Service Layer** → Business logic
* **Repository Layer** → Database operations
* **Model Layer** → JPA entities
* **DTO Layer** → Request/Response mapping
* **Exception Layer** → Centralized exception handling

---

## 📁 Project Structure

```bash
src/main/java/com/solveda/catalogueservice
│
├── controller/          # REST controllers
├── service/             # Business logic interfaces
│   └── impl/            # Service implementations
├── repository/          # JPA repositories
├── model/               # JPA entities
├── dto/                 # Request/Response DTOs
├── exception/           # Global exception handling
└── CatalogueServiceApplication.java
```

---

## 🔄 Request Flow

1. Client → Controller
2. Controller → Service
3. Service → Repository
4. Repository → Database
5. Database → Entity
6. Entity → DTO
7. DTO → HTTP Response
8. Errors → GlobalExceptionHandler

---

# 📚 API Endpoints (Controller Aligned)

---

# 📥 Bulk Import APIs

### Bulk Import (JSON)

```
POST /bulk/import-json
```

### Bulk Import (File Upload)

```
POST /bulk/import-file
```

---

# 📂 Category APIs

### Create Category

```
POST /api/categories
```

### Update Category

```
PUT /api/categories/{id}
```

### Activate Category

```
POST /api/categories/{id}/activate
```

### Deactivate Category

```
POST /api/categories/{id}/deactivate
```

### Get Category By ID

```
GET /api/categories/{id}
```

### Get All Active Categories

```
GET /api/categories
```

---

# 📦 Product APIs

### Create Product

```
POST /products
```

### Update Product

```
PUT /products/{id}
```

### Activate Product

```
POST /products/{id}/activate
```

### Deactivate Product

```
POST /products/{id}/deactivate
```

### Get Product By ID

```
GET /products/{id}
```

### Get All Active Products

```
GET /products
```

### Get Products By Category

```
GET /products/category/{categoryId}
```

---

# 📊 Inventory APIs

### Create Inventory

```
POST /inventory
```

### Reserve Stock

```
POST /inventory/{productId}/reserve?quantity=10
```

### Release Stock

```
POST /inventory/{productId}/release?quantity=5
```

### Clear Reservations

```
POST /inventory/{productId}/clear
```

### Get Inventory

```
GET /inventory/{productId}
```

---

# 💰 Price APIs

### Create Price

```
POST /prices
```

### Change Price

```
POST /prices/{priceId}/change?amount=199.99
```

### Expire Price

```
POST /prices/{priceId}/expire
```

### Get Price By ID

```
GET /prices/{priceId}
```

### Get Active Prices By Product

```
GET /prices/product/{productId}
```

---

# 🧪 API Testing Strategy (QA Ready)

👉 **Every API must follow this testing template**

```
# API Name: ________________________
# Endpoint:  ________________________
# Method:    ________________________
```

---

## 1. Functional Tests (Happy Path)

* [ ] Valid request returns correct success response
* [ ] Optional fields handled correctly
* [ ] Response structure matches DTO
* [ ] Correct status code (200 / 201 / 204)
* [ ] Database entry created/updated correctly

---

## 2. Input Validation Tests

* [ ] Missing required fields
* [ ] Empty string ("")
* [ ] Wrong data types
* [ ] Field length overflow
* [ ] Invalid formats
* [ ] Null values
* [ ] Extra unknown fields

---

## 3. Negative / Error Handling Tests

* [ ] Duplicate entry → 409
* [ ] Invalid JSON → 400
* [ ] Resource not found → 404
* [ ] Business rule violation
* [ ] Database constraint failure

> 🔐 Auth tests (401 / 403) will be added after Spring Security implementation

---

## 4. Edge / Extreme Cases

* [ ] Boundary values
* [ ] Special characters
* [ ] Whitespaces-only
* [ ] Large payload
* [ ] High-frequency requests

---

## 5. Integration Checks

* [ ] Category → Product relation valid
* [ ] Product → Inventory relation valid
* [ ] Product → Price relation valid
* [ ] Activation / Deactivation propagation
* [ ] Status consistency
* [ ] Response contract match

---

## 6. Post-Testing Confirmation

* [ ] Bug fixed and re-tested
* [ ] Test cases added to Postman
* [ ] Backend ready for QA / UAT

---

# ⚙️ Configuration

`application.properties`

```properties
spring.application.name=catalogue-service

spring.datasource.url=jdbc:mysql://localhost:3306/<db_name>
spring.datasource.username=<username>
spring.datasource.password=<password>

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

springdoc.swagger-ui.path=/swagger-ui.html
```

---

# ▶️ Run Locally

```bash
git clone https://github.com/your-username/catalogue-service.git
cd catalogue-service
mvn clean install
mvn spring-boot:run
```

* Base URL → `http://localhost:8080`
* Swagger UI → `http://localhost:8080/swagger-ui/index.html`

---

# 🚀 Future Enhancements

* 🔐 Spring Security + JWT
* 🐳 Docker + Docker Compose
* ☸️ Kubernetes deployment
* 📦 API Gateway integration
* 📊 Pagination & filtering
* ⚡ Redis caching
* 📜 Audit logs
* 📈 Monitoring (Prometheus + Grafana)

---

# 👨‍💻 Author

**Rohan Bansal**
📧 [rohanbansalcse@gmail.com](mailto:rohanbansalcse@gmail.com)

---

# 🪪 License

MIT License

> *Production-grade APIs are built with clean code, strong testing, and clear contracts.*

---
