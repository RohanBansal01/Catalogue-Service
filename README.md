<div align="center">
  <h1>🛍️ Catalogue Service API</h1>
  <p><strong>A production-ready e-commerce catalogue management microservice</strong></p>
</div>

<p align="center">
  <a href="https://spring.io/projects/spring-boot">
    <img src="https://img.shields.io/badge/Spring%20Boot-3.5.6-brightgreen" alt="Spring Boot" />
  </a>
  &nbsp;
  <a href="https://www.oracle.com/java/technologies/javase/jdk21-archive-downloads.html">
    <img src="https://img.shields.io/badge/Java-21-blue" alt="Java" />
  </a>
  &nbsp;
  <a href="https://www.mysql.com/">
    <img src="https://img.shields.io/badge/MySQL-8.x-orange" alt="MySQL" />
  </a>
  &nbsp;
  <a href="https://springdoc.org/">
    <img src="https://img.shields.io/badge/OpenAPI-3.0-green" alt="OpenAPI" />
  </a>
  &nbsp;
  <a href="LICENSE">
    <img src="https://img.shields.io/badge/License-MIT-yellow" alt="License: MIT" />
  </a>
</p>

---

## 📋 Table of Contents

- [Overview](#-overview)
- [Features](#-features)
- [Tech Stack](#️-tech-stack)
- [Architecture](#-architecture)
- [Quick Start](#-quick-start)
- [API Documentation](#-api-documentation)
- [Database Schema](#-database-schema)
- [Testing](#-testing)
- [Configuration](#-configuration)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)

---

## 📘 Overview

The **Catalogue Service** is a comprehensive e-commerce catalogue management system built with modern Java enterprise patterns. It provides RESTful APIs for managing product catalogues with proper separation of concerns, transactional integrity, and production-ready error handling.

**Core Capabilities:**
- 📂 **Category Management** - Hierarchical product categorization
- 📦 **Product Catalog** - Full product lifecycle management
- 📊 **Inventory Tracking** - Stock management with reservation system
- 💰 **Pricing Engine** - Multi-currency pricing with historical tracking
- 📥 **Bulk Operations** - Efficient bulk import with validation and error reporting

---

## ✨ Features

### 🏗️ **Enterprise Architecture**
- **Clean Architecture** with proper layer separation
- **Domain-Driven Design (DDD)** patterns with aggregate roots
- **Transactional Management** with proper rollback semantics
- **Global Exception Handling** with structured error responses
- **DTO Pattern** for clean API contracts

### 🔒 **Data Integrity**
- **ACID Transactions** with Spring `@Transactional`
- **Database Constraints** with proper exception handling
- **Idempotent Operations** for bulk imports
- **Validation Framework** using Jakarta Bean Validation

### 📚 **API Excellence**
- **OpenAPI 3.0 Documentation** with Swagger UI
- **RESTful Design** following HTTP standards
- **Comprehensive Error Handling** with proper HTTP status codes
- **Input Validation** with meaningful error messages

### 🚀 **Developer Experience**
- **Hot Reload** with Spring DevTools
- **Comprehensive Logging** with SLF4J
- **Clean Code** with Lombok boilerplate reduction
- **Maven Build** with dependency management

---

## ⚙️ Tech Stack

| Layer | Technology | Version |
|-------|------------|---------|
| **Framework** | Spring Boot | 3.5.6 |
| **Language** | Java | 21 (LTS) |
| **Database** | MySQL | 8.x |
| **ORM** | Spring Data JPA | Hibernate 6.x |
| **Validation** | Jakarta Bean Validation | 3.0 |
| **API Docs** | Springdoc OpenAPI | 2.8.12 |
| **Logging** | SLF4J + Logback | - |
| **Utilities** | Lombok | - |
| **Build** | Maven | 3.8+ |

---

## 🏛️ Architecture

### **Layered Architecture**
```
┌─────────────────────────────────────┐
│           Controllers               │ ← REST APIs
├─────────────────────────────────────┤
│             Services                │ ← Business Logic
├─────────────────────────────────────┤
│           Repositories              │ ← Data Access
├─────────────────────────────────────┤
│             Models                  │ ← JPA Entities
├─────────────────────────────────────┤
│               DTOs                  │ ← Request/Response
└─────────────────────────────────────┘
```

### **Domain-Driven Design**
- **Aggregate Roots**: `Category`, `Product`
- **Value Objects**: `ProductInventory`, `ProductPrice`
- **Domain Events**: Activation/Deactivation
- **Factory Methods**: Entity creation with validation

### **Transaction Strategy**
- **Class-level `@Transactional`** for consistency
- **Read-only optimizations** for query methods
- **Exception handling** with automatic rollback
- **Propagation settings** for bulk operations

---

## 🚀 Quick Start

### **Prerequisites**
- Java 21 or higher
- Maven 3.8 or higher
- MySQL 8.0 or higher

### **Installation**

1. **Clone the repository**
   ```bash
   git clone https://github.com/RohanBansal01/Catalogue-Service.git
   cd Catalogue-Service
   ```

2. **Configure Database**
   ```properties
   # src/main/resources/application.properties
   spring.datasource.url=jdbc:mysql://localhost:3306/catalogue_db
   spring.datasource.username=your_username
   spring.datasource.password=your_password
   ```

3. **Build and Run**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

4. **Access the Application**
   - **API Base URL**: `http://localhost:8080`
   - **Swagger UI**: `http://localhost:8080/swagger-ui/index.html`
   - **Health Check**: `http://localhost:8080/actuator/health`

---

## 📚 API Documentation

### **Base URL**
```
http://localhost:8080
```

### **Authentication**
*Currently no authentication. Spring Security integration planned for future releases.*

### **Core Endpoints**

#### 📂 **Categories**
```http
POST   /api/categories              # Create category
PUT    /api/categories/{id}          # Update category
POST   /api/categories/{id}/activate   # Activate category
POST   /api/categories/{id}/deactivate # Deactivate category
GET    /api/categories/{id}          # Get category by ID
GET    /api/categories               # Get all active categories
```

#### 📦 **Products**
```http
POST   /products                     # Create product
PUT    /products/{id}                 # Update product
POST   /products/{id}/activate       # Activate product
POST   /products/{id}/deactivate     # Deactivate product
GET    /products/{id}                # Get product by ID
GET    /products                     # Get all active products
GET    /products/category/{categoryId} # Get products by category
```

#### 📊 **Inventory**
```http
POST   /inventory                    # Create inventory
POST   /inventory/{productId}/reserve?quantity=10  # Reserve stock
POST   /inventory/{productId}/release?quantity=5   # Release stock
POST   /inventory/{productId}/clear    # Clear reservations
GET    /inventory/{productId}        # Get inventory
```

#### 💰 **Pricing**
```http
POST   /prices                       # Create price
POST   /prices/{priceId}/change?amount=199.99  # Change price
POST   /prices/{priceId}/expire      # Expire price
GET    /prices/{priceId}             # Get price by ID
GET    /prices/product/{productId}   # Get active prices by product
```

#### 📥 **Bulk Import**
```http
POST   /bulk/import-json             # Import JSON data
POST   /bulk/import-file             # Import file upload
```

### **API Documentation**
- **Interactive Docs**: [Swagger UI](http://localhost:8080/swagger-ui/index.html)
- **OpenAPI Spec**: [OpenAPI JSON](http://localhost:8080/v3/api-docs)

---

## 🗄️ Database Schema

### **Entity Relationships**
```
Category (1) ←→ (N) Product (1) ←→ (N) ProductPrice
                          ↓
                   ProductInventory
```

### **Key Tables**
- **categories** - Product categories
- **products** - Product master data
- **product_inventory** - Stock management
- **product_price** - Pricing with history

### **Schema Management**
- **Development**: `ddl-auto=update` (auto-schema generation)
- **Production**: Recommend Flyway/Liquibase migrations
- **Constraints**: Proper foreign keys and unique constraints

---

## 🧪 Testing

### **Test Strategy**
The project follows a comprehensive testing approach:

#### **1. Unit Tests**
- Service layer business logic
- Entity domain behavior
- Utility methods

#### **2. Integration Tests**
- Repository layer with test database
- Controller endpoints with `@WebMvcTest`
- Transaction boundaries

#### **3. API Testing**
- Contract testing with OpenAPI specs
- Error scenario validation
- Performance testing

### **Running Tests**
```bash
# Run all tests
mvn test

# Run with coverage
mvn clean test jacoco:report

# Run specific test class
mvn test -Dtest=ProductServiceTest
```

### **Test Coverage**
- **Target**: 80%+ code coverage
- **Current**: Add more tests to improve coverage
- **Tools**: JUnit 5, Mockito, TestContainers

---

## ⚙️ Configuration

### **Application Properties**
```properties
# Application
spring.application.name=catalogue-service

# Database
spring.datasource.url=jdbc:mysql://localhost:3306/catalogue_db
spring.datasource.username=${DB_USERNAME:root}
spring.datasource.password=${DB_PASSWORD:password}
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

# JPA
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.MySQLDialect

# OpenAPI
springdoc.api-docs.path=/v3/api-docs
springdoc.swagger-ui.path=/swagger-ui.html

# Actuator
management.endpoints.web.exposure.include=health,info
```

### **Environment Variables**
```bash
export DB_USERNAME=your_username
export DB_PASSWORD=your_password
export SPRING_PROFILES_ACTIVE=prod
```

### **Profiles**
- **Default**: Development configuration
- **Production**: Production-ready settings (planned)

---

## 🚀 Deployment

### **Local Development**
```bash
mvn spring-boot:run
```

### **Docker (Planned)**
```dockerfile
# Dockerfile coming soon
FROM openjdk:21-jre-slim
COPY target/catalogue-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/app.jar"]
```

### **Production Considerations**
- **Database**: Use connection pooling (HikariCP)
- **Security**: Add Spring Security + JWT
- **Monitoring**: Add Prometheus + Grafana
- **Scaling**: Kubernetes deployment ready
- **Caching**: Redis integration planned

---

## 📈 Performance & Scalability

### **Current Optimizations**
- **Database Connection Pooling** via HikariCP
- **Read-only Transactions** for query optimization
- **Lazy Loading** for JPA relationships
- **DTO Projection** to reduce payload size

### **Future Enhancements**
- **Redis Caching** for frequently accessed data
- **Database Indexing** strategy
- **Pagination** for large result sets
- **Async Processing** for bulk operations

---

## 🔧 Development Guidelines

### **Code Style**
- Follow **Clean Code** principles
- Use **Lombok** for boilerplate reduction
- Implement **DDD** patterns consistently
- Maintain **comprehensive JavaDoc** documentation

### **Git Workflow**
```bash
# Feature branch workflow
git checkout -b feature/new-feature
git commit -m "feat: add new feature"
git push origin feature/new-feature
# Create Pull Request
```

### **Commit Messages**
- `feat:` New features
- `fix:` Bug fixes
- `docs:` Documentation updates
- `refactor:` Code refactoring
- `test:` Test additions/updates

---

## 🤝 Contributing

1. **Fork** the repository
2. **Create** a feature branch (`git checkout -b feature/amazing-feature`)
3. **Commit** your changes (`git commit -m 'Add amazing feature'`)
4. **Push** to the branch (`git push origin feature/amazing-feature`)
5. **Open** a Pull Request

### **Development Setup**
```bash
git clone https://github.com/RohanBansal01/Catalogue-Service.git
cd Catalogue-Service
mvn clean install
mvn spring-boot:run
```

---

## 📊 Project Status

### **Current Version**: 0.0.1-SNAPSHOT

### **Implemented Features**
- ✅ Category Management
- ✅ Product CRUD Operations
- ✅ Inventory Management
- ✅ Pricing System
- ✅ Bulk Import Functionality
- ✅ Global Exception Handling
- ✅ OpenAPI Documentation

### **In Progress**
- 🔄 Unit Test Coverage Improvement
- 🔄 Integration Test Suite
- 🔄 Performance Optimization

### **Planned Features**
- 📋 Spring Security + JWT Authentication
- 📋 Docker + Docker Compose Setup
- 📋 Kubernetes Deployment
- 📋 Redis Caching Layer
- 📋 Audit Logging System
- 📋 API Rate Limiting
- 📋 Database Migration Scripts

---

## 🐛 Troubleshooting

### **Common Issues**

#### **Database Connection Issues**
```bash
# Check MySQL service
sudo systemctl status mysql

# Verify connection
mysql -u username -p -h localhost
```

#### **Port Conflicts**
```bash
# Check port usage
netstat -tulpn | grep :8080

# Change port in application.properties
server.port=8081
```

#### **Build Issues**
```bash
# Clean build
mvn clean install -DskipTests

# Clear Maven cache
mvn dependency:purge-local-repository
```

### **Logging Configuration**
```properties
# Enable debug logging
logging.level.com.solveda.catalogueservice=DEBUG

# SQL logging
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql.BasicBinder=TRACE
```

---

## 📞 Support

### **Getting Help**
- **Documentation**: Check this README and API docs
- **Issues**: [GitHub Issues](https://github.com/RohanBansal01/Catalogue-Service/issues)
- **Email**: rohanbansalcse@gmail.com

### **Community**
- **Contributions**: Welcome via Pull Requests
- **Discussions**: GitHub Discussions (coming soon)
- **Feedback**: Always appreciated

---

## 👨‍💻 Author

**Rohan Bansal**
- 📧 Email: [rohanbansalcse@gmail.com](mailto:rohanbansalcse@gmail.com)
- 🐙 GitHub: [@RohanBansal01](https://github.com/RohanBansal01)
- 💼 LinkedIn: [Rohan Bansal](https://linkedin.com/in/rohan-bansal)

---

## 📄 License

This project is licensed under the **MIT License** - see the [LICENSE](LICENSE) file for details.

```
MIT License

Copyright (c) 2024 Rohan Bansal

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.
```

---

## 🙏 Acknowledgments

- **Spring Boot Team** for the excellent framework
- **MySQL Community** for the robust database
- **OpenAPI Community** for API documentation standards
- **Lombok Team** for reducing boilerplate code

---

> **🚀 Production-grade APIs are built with clean code, strong testing, and clear contracts.**

---

<div align="center">
  <p><strong>⭐ If this project helped you, please give it a star!</strong></p>
  <p><em>Built with ❤️ by Rohan Bansal</em></p>
</div>
