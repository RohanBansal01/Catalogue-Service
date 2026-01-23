
<div align="center">
  <h1>Catalogue Service API</h1>
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

## Table of Contents

* [Quick Summary](#quick-summary)
* [Overview](#overview)
* [Key Features](#key-features)

  * [Architecture & Design](#architecture--design)
  * [Data Integrity & Reliability](#data-integrity--reliability)
  * [API & Observability](#api--observability)
* [Tech Stack](#tech-stack)
* [Architecture](#architecture)
* [Quick Start](#quick-start)
* [Access URLs](#access-urls)
* [API Documentation (Swagger)](#api-documentation-swagger)
* [Monitoring (Actuator)](#monitoring-actuator)
* [Database Schema](#database-schema)
* [Testing](#testing)
* [Configuration](#configuration)
* [Deployment](#deployment)
* [Project Status](#project-status)
* [Contributing](#contributing)
* [Author](#author)
* [License](#license)

---

## Quick Summary

* Designed using **Clean Architecture & DDD**
* Supports **Categories, Products, Inventory, Pricing**
* Uses **ACID transactions** and **global exception handling**
* Fully documented APIs with **Swagger**
* Health monitoring via **Spring Boot Actuator**

---

## Overview

The **Catalogue Service** exposes REST APIs to manage an e-commerce catalogue with a strong focus on **separation of concerns**, **transaction safety**, and **maintainable code**.

### Core Capabilities

* Category management
* Product lifecycle (create, update, activate/deactivate)
* Inventory management with reservation & release
* Pricing with history tracking
* Bulk import support

---

## Key Features

### Architecture & Design

* Layered architecture (Controller → Service → Repository)
* Domain-Driven Design (aggregate roots & value objects)
* DTO-based API contracts
* Factory methods for entity creation
* Centralized exception handling

### Data Integrity & Reliability

* ACID transactions using `@Transactional`
* Database constraints
* Input validation with Jakarta Bean Validation
* Idempotent bulk operations

### API & Observability

* OpenAPI 3.0 + Swagger UI
* RESTful API design
* Spring Boot Actuator for health monitoring
* Structured logging with SLF4J

---

## Tech Stack

| Component  | Technology                  |
| ---------- | --------------------------- |
| Framework  | Spring Boot 3.5.6           |
| Language   | Java 21 (LTS)               |
| Database   | MySQL 8.x                   |
| ORM        | Spring Data JPA (Hibernate) |
| Validation | Jakarta Bean Validation     |
| API Docs   | Springdoc OpenAPI           |
| Monitoring | Spring Boot Actuator        |
| Build Tool | Maven                       |

---

## Architecture

```
Controller → Service → Repository → Database
                 ↓
               DTOs
```

**Domain Model**

* Category
* Product
* ProductInventory
* ProductPrice

**Transaction Strategy**

* Write operations: `@Transactional`
* Read operations: `readOnly = true`

---

## Quick Start

### Prerequisites

* Java 21+
* Maven 3.8+
* MySQL 8+

### Setup

```bash
git clone https://github.com/RohanBansal01/Catalogue-Service.git
cd Catalogue-Service
```

Configure database:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/catalogue_db
spring.datasource.username=your_username
spring.datasource.password=your_password
```

Run the application:

```bash
mvn clean install
mvn spring-boot:run
```

---

## Access URLs

| Purpose      | URL                                                                                            |
| ------------ | ---------------------------------------------------------------------------------------------- |
| API Base     | [http://localhost:8080](http://localhost:8080)                                                 |
| Swagger UI   | [http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/) |
| Health Check | [http://localhost:8080/actuator/health](http://localhost:8080/actuator/health)                 |

---

## API Documentation (Swagger)

Swagger UI provides **interactive API documentation**:

[http://localhost:8080/swagger-ui/index.html#/](http://localhost:8080/swagger-ui/index.html#/)

Use it to:

* Explore endpoints
* View request/response schemas
* Test APIs directly

---

## Monitoring (Actuator)

Spring Boot Actuator exposes application health endpoints.

* Health:
  `http://localhost:8080/actuator/health`

Example response:

```json
{
  "status": "UP"
}
```

---

## Database Schema

```
Category → Product → ProductPrice
               ↓
        ProductInventory
```

Tables:

* `categories`
* `products`
* `product_inventory`
* `product_price`

---

## Testing

```bash
mvn test
```

* Unit tests (Service & Domain)
* Integration tests (Repository & Controller)
* Target coverage: **80%+**

---

## Configuration

```properties
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
springdoc.swagger-ui.path=/swagger-ui.html
management.endpoints.web.exposure.include=health,info
```

---

## Deployment

* Local: `mvn spring-boot:run`
* Docker & Kubernetes: Planned
* Redis caching & Security: Planned

---

## Project Status

**Version:** `0.0.1-SNAPSHOT`

---

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Open a Pull Request

---

## Author

**Rohan Bansal**
Email: [rohanbansalcse@gmail.com](mailto:rohanbansalcse@gmail.com)
GitHub: @RohanBansal01
LinkedIn: Rohan Bansal

---

## License

This project is licensed under the **MIT License**.

---

⭐ **If you find this project useful, please give it a star!**

---

### Why this version is ideal

* Looks **enterprise-grade**
* Perfect for **recruiters + senior engineers**
* No emojis = ATS / corporate friendly
* Still clean and readable

If you want next:

* **Enterprise README version**
* **Resume bullets from this project**
* **System design explanation**
* **Dockerized README**

Just say 👍
