# 📚 Library Management API

## 📖 Overview

A robust Library Management System built with Spring Boot and Java 22, providing comprehensive book management, borrowing, and return functionality with advanced search capabilities.

## ✨ Features

- 📋 **Book Management**: Create, read, update, and soft-delete books
- 🔍 **Advanced Search**: Filter books by title, author, publication year, and availability status
- 📄 **Pagination & Sorting**: Efficient data retrieval with customizable pagination
- 📤 **Borrowing System**: Borrow and return books with automatic status updates
- 🛡️ **Data Integrity**: Soft delete mechanism and validation rules
- ⚡ **Performance**: Optimized queries with Spring Data JPA Specifications

## 🛠️ Technology Stack

| Technology | Version |
|------------|---------|
| Java | 22 |
| Spring Boot | 3.2.x |
| Spring Data JPA | 3.2.x |
| Hibernate | 6.4.x |
| PostgreSQL | 15.x |
| Maven | 3.9.x |
| JUnit | 5.10.x |
| MapStruct | 1.5.x |
| Flyway | 9.22.x |
| SpringDoc OpenAPI | 2.3.x |

## 📋 Prerequisites

Before running the project, ensure you have the following installed:

- ☕ Java 22 or higher
- 📦 Maven 3.9+
- 🐘 PostgreSQL 15+
- 🐳 Docker
- 🔧 Git

## 🚀 Getting Started

### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/library-management-system.git
cd library-management-system
```

### 2. Configure Database

Create a PostgreSQL database:
```sql
CREATE DATABASE library_db;
```

Update `application.yml` with your credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library_db
    username: your_username
    password: your_password
```

### 3. Build the Project
```bash
mvn clean install
```

### 4. Run Database Migrations
Flyway will automatically run migrations on application startup.

### 5. Run the Application
```bash
mvn spring-boot:run
```

Or using the JAR:
```bash
java -jar target/library-management-system-1.0.0.jar
```

### 6. Access the Application
- 🌐 **API Base URL**: `http://localhost:8080/api/v1`
- 📚 **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- 📊 **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`
- 💚 **Health Check**: `http://localhost:8080/actuator/health`

## 🏗️ Project Structure

```
library-management-system/
├── src/
│   ├── main/
│   │   ├── java/com/library/management/
│   │   │   ├── config/          # Configuration classes
│   │   │   ├── controller/      # REST API controllers
│   │   │   ├── service/         # Business logic layer
│   │   │   ├── repository/      # Data access layer
│   │   │   ├── model/           # Entities & DTOs
│   │   │   ├── mapper/          # MapStruct mappers
│   │   │   ├── exception/       # Global exception handling
│   │   │   ├── validator/       # Custom validators
│   │   │   ├── filter/          # Search filters
│   │   │   └── util/            # Utility classes
│   │   └── resources/
│   │       ├── application.yml  # Main configuration
│   │       ├── db/migration/    # Flyway migrations
│   │       └── static/          # Static resources
│   └── test/
│       ├── java/                # Unit & integration tests
│       └── resources/           # Test configurations
├── .github/workflows/           # CI/CD pipelines
├── docker-compose.yml           # Docker setup
├── pom.xml                      # Maven dependencies
├── License                      # License
└── README.md                    # Readme
```

## 🔌 API Endpoints

### 📚 Book Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/books` | Create a new book |
| GET | `/api/v1/books` | Get all books (with pagination & filtering) |
| GET | `/api/v1/books/{id}` | Get book by ID |
| PUT | `/api/v1/books/{id}` | Update book details |
| DELETE | `/api/v1/books/{id}` | Soft delete a book |

### 📤 Borrowing Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/borrowings/borrow` | Borrow a book |
| POST | `/api/v1/borrowings/return` | Return a book |
| GET | `/api/v1/borrowings/book/{bookId}` | Get borrowing history by book |
| GET | `/api/v1/borrowings/borrower/{name}` | Get borrowing history by borrower |

### 🔍 Advanced Search
```http
GET /api/v1/books?title={title}&author={author}&year={year}&status={status}&page={page}&size={size}&sort={sort}
```

## 🧪 Testing

### Run Unit Tests
```bash
mvn test
```

### Run Integration Tests
```bash
mvn verify
```

### Test Coverage Report
```bash
mvn jacoco:report
```

## 🤖 CI/CD Pipeline

This project uses GitHub Actions for continuous integration:

### Automated Workflow
- ✅ **Build**: Compiles and builds the application
- ✅ **Test**: Runs all unit and integration tests
- ✅ **Code Quality**: Static code analysis with SonarCloud
- ✅ **Deployment**: Automatic deployment to staging/production

### GitHub Actions Configuration
Located in `.github/workflows/ci.yml`:
```yaml
name: CI/CD Pipeline

on:
  push:
    branches: [ main, develop ]
  pull_request:
    branches: [ main ]

jobs:
  build-and-test:
    runs-on: ubuntu-latest
    services:
      postgres:
        image: postgres:15
        env:
          POSTGRES_DB: library_db_test
          POSTGRES_USER: test_user
          POSTGRES_PASSWORD: test_password
    steps:
      - uses: actions/checkout@v4
      - name: Set up JDK 22
        uses: actions/setup-java@v3
        with:
          java-version: '22'
          distribution: 'temurin'
      - name: Cache Maven packages
        uses: actions/cache@v3
      - name: Build with Maven
        run: mvn clean compile
      - name: Run tests
        run: mvn test
```

## 🎯 Key Design Decisions

### 1. **Layered Architecture with DDD Principles**
We adopted Domain-Driven Design patterns to ensure clear separation of concerns:
- **Why**: Maintainability and scalability
- **Impact**: Each layer has a single responsibility, making the codebase easier to test and extend

### 2. **Specification Pattern for Dynamic Queries**
Implemented Spring Data JPA Specifications for flexible search:
- **Why**: Avoid complex query strings and maintain type safety
- **Impact**: Easy to add new filters without modifying existing code (Open/Closed Principle)

### 3. **Soft Delete with Status Management**
Used `is_deleted` flag instead of physical deletion:
- **Why**: Data retention, audit trail, and business requirements
- **Impact**: Historical data preserved, GDPR compliance, and recovery capabilities

### 4. **DTO Pattern with MapStruct**
Separated internal entities from external API contracts:
- **Why**: API versioning, security, and performance optimization
- **Impact**: Prevents over-exposure of internal data, better control over API responses

### 5. **Optimistic Locking for Concurrency**
Implemented `@Version` annotation on entities:
- **Why**: Prevent concurrent updates conflicts
- **Impact**: Ensures data consistency in multi-user scenarios

## 📊 Database Schema

### Books Table
```sql
CREATE TABLE books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(17) UNIQUE NOT NULL,
    publication_year INT,
    status VARCHAR(20) NOT NULL,
    is_deleted BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);
```

### Borrowing Records Table
```sql
CREATE TABLE borrowing_records (
    id BIGSERIAL PRIMARY KEY,
    book_id BIGINT NOT NULL,
    borrower_name VARCHAR(100) NOT NULL,
    borrowed_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id)
);
```

## 🔒 Security Considerations

- ✅ Input validation using Bean Validation
- ✅ SQL injection prevention through JPA/Hibernate
- ✅ Proper transaction management
- ✅ Exception handling to prevent information leakage
- ✅ Environment-specific configurations

## 📦 Deployment

### Docker Deployment
```bash
# Build Docker image
docker build -t library-management-system .

# Run with Docker Compose
docker-compose up -d
```

### Production Deployment
1. Set environment variables for production configuration
2. Use PostgreSQL with production-grade settings
3. Enable HTTPS/TLS
4. Configure proper logging and monitoring

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📝 Future Enhancements

Given more time, the following improvements would be implemented:

1. **Authentication & Authorization**: JWT-based user authentication with roles (LIBRARIAN, MEMBER)
2. **Email Notifications**: Automated reminder emails for overdue books
3. **Reports & Analytics**: Dashboard with borrowing statistics and popular books
4. **Caching**: Redis implementation for frequently accessed book data
5. **Event-Driven Architecture**: Use RabbitMQ/Kafka for async operations
6. **Reservation System**: Allow users to reserve books that are currently borrowed
7. **Internationalization**: Multi-language support for API responses
8. **Rate Limiting**: Prevent API abuse with request throttling
9. **Audit Logs**: Complete audit trail for all operations
10. **API Versioning**: Proper versioning strategy for API evolution

## 📄 License

This project is licensed under the MIT License.

## 👤 Contact

Your Name - [mh.mokhtari7@gmail.com](mailto:mh.mokhtari7@gmail.com)

Project Link: [https://github.com/MahdyMokh7/library-management-api](https://github.com/yourusername/library-management-system)
