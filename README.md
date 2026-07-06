# 📚 Library Management API

## 📖 Overview

A robust Library Management System built with Spring Boot and Java 22, providing comprehensive book management, borrowing, and return functionality with advanced search capabilities. This project demonstrates professional-grade development practices with containerization, CI/CD, and comprehensive testing.

## ✨ Features

- 📋 **Book Management**: Create, read, update, and soft-delete books
- 🔍 **Advanced Search**: Filter books by title, author, publication year, and availability status
- 📄 **Pagination & Sorting**: Efficient data retrieval with customizable pagination
- 📤 **Borrowing System**: Borrow and return books with automatic status updates
- 🛡️ **Data Integrity**: Soft delete mechanism and validation rules
- ⚡ **Performance**: Optimized queries with Spring Data JPA Specifications
- 🐳 **Containerized**: Docker support for consistent deployment
- 🔄 **CI/CD Ready**: GitHub Actions pipeline for automated builds and tests

---

## 🛠️ Technologies Used

| Technology | Version     | Purpose |
|------------|-------------|---------|
| **Java** | 22          | Programming language |
| **Spring Boot** | 3.2.4       | Application framework |
| **Spring Data JPA** | 3.2.4       | Data access abstraction |
| **Hibernate** | 6.4.4       | ORM implementation |
| **PostgreSQL** | 15          | Production database |
| **Flyway** | 9.22.3      | Database migrations |
| **MapStruct** | 1.5.5.Final | DTO/Entity mapping |
| **Lombok** | 1.18.30     | Boilerplate reduction |
| **SpringDoc OpenAPI** | 2.4.0       | API documentation |
| **Maven** | 4.0.0       | Build tool |
| **JUnit 5** | 5.10.2      | Unit testing |
| **Mockito** | 5.10.0      | Mocking framework |
| **Testcontainers** | 1.19.7      | Integration testing |
| **JaCoCo** | 0.8.12      | Code coverage |
| **PMD** | 3.26.0      | Static code analysis |
| **Spotless** | 2.41.0      | Code formatting |
| **Docker** | Latest      | Containerization |
| **GitHub Actions** | -           | CI/CD pipeline |

---

## 📋 Prerequisites

Before running the project, ensure you have the following installed:

- ☕ Java 22 or higher
- 📦 Maven 4.0+
- 🐘 PostgreSQL 15+ (for local development)
- 🐳 Docker and Docker Compose (for containerized deployment)
- 🔧 Git
- 💻 IDE (IntelliJ IDEA recommended)

---

## 🚀 Quick Start



### Default (Recommended)

The project includes an automation script that handles everything from formatting to deployment.

**First time only (if on Unix/Linux-based system):**
```bash
chmod +x start.sh
```

**Run the full stack:**
```bash
./start.sh run
```

This single command:
- ✅ Applies code formatting (Spotless)
- ✅ Runs all tests (unit + integration)
- ✅ Runs coverage tests (Jacoco)
- ✅ Static code analysis (PMD)
- ✅ Builds the Docker image with your current version
- ✅ Starts PostgreSQL + the application

**Reset everything:**
```bash
./start.sh clean
```
Stops all containers and removes database volumes.

---

### Access the API

| Service | URL |
|---------|-----|
| 🌐 **API Base** | `http://localhost:8080/api/v1` |
| 📚 **Swagger UI** | `http://localhost:8080/swagger-ui/index.html` |
| 💚 **Health Check** | `http://localhost:8080/actuator/health` |

---

### Manual Setup (Alternative)

If you prefer running without the automation script:

```bash
# 1. Build the project
mvn clean package -DskipTests

# 2. Start PostgreSQL
docker-compose up -d postgres

# 3. Run the application
mvn spring-boot:run
```

**Or using the JAR:**
```bash
java -jar target/libraryapi.jar
```

---

## 🏗️ Project Structure

```
library-management-api/
├── src/
│   ├── main/
│   │   ├── java/com/mehdymokhtari/libraryapi/
│   │   │   ├── config/              # Configuration classes
│   │   │   ├── controller/          # REST API controllers
│   │   │   ├── service/             # Business logic layer
│   │   │   │   ├── impl/            # Service implementations
│   │   │   │   └── validation/      # Business validators
│   │   │   ├── repository/          # Data access layer
│   │   │   │   └── spec/            # JPA Specifications
│   │   │   ├── model/               # Entities & DTOs
│   │   │   │   ├── entity/          # JPA entities
│   │   │   │   ├── dto/             # Data Transfer Objects
│   │   │   │   │   ├── request/     # Request DTOs
│   │   │   │   │   └── response/    # Response DTOs
│   │   │   │   ├── enums/           # Enumerations
│   │   │   │   └── mapper/          # MapStruct mappers
│   │   │   ├── exception/           # Global exception handling
│   │   │   ├── filter/              # Search filters
│   │   │   └── util/                # Utility classes
│   │   └── resources/
│   │       ├── application.yml      # Main configuration
│   │       ├── application-docker.yml # Docker-specific config
│   │       ├── db/
│   │       │   └── migration/        # Flyway migrations
│   │       └── static/              # Static resources
│   └── test/
│       ├── java/com/mehdymokhtari/libraryapi/
│       │   ├── controller/          # Controller unit tests
│       │   ├── service/             # Service unit tests
│       │   ├── utils/               # Utils unit tests
│       │   ├── exception/           # Excpetion unit tests
│       │   ├── filter/              # Filter unit tests
│       │   ├── model/mapper/        # mapper unit tests
│       │   ├── repository/          # Repository integration tests
│       │   └── integration/         # End-to-end integration tests
│       └── resources/
│           └── application-test.yml # Test configuration
├── docs/
│   ├── Project Documentation.pdf    # Official project specification
│   ├── Report.pdf                   # Project Report
│   └── devGuideSetup/               # Developer setup guide files
├── scripts/
│   └── start.sh                     # Application start script
├── .github/
│   └── workflows/
│       └── ci.yml                   # CI/CD pipeline
├── Dockerfile                       # Docker image definition
├── docker-compose.yml               # Docker services orchestration
├── .dockerignore                    # Docker ignore file
├── .gitignore                       # Git ignore file
├── pom.xml                          # Maven dependencies
├── LICENSE                          # License file
└── README.md                        # Project documentation
```

---

## 🔌 API Endpoints

### 📚 Book Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/books` | Create a new book |
| GET | `/api/v1/books` | Get all books (with pagination & filtering) |
| GET | `/api/v1/books/{id}` | Get book by ID |
| PUT | `/api/v1/books/{id}` | Update book details (ISBN cannot be changed) |
| DELETE | `/api/v1/books/{id}` | Soft delete a book (only if not borrowed) |

### 📤 Borrowing Management

| Method | Endpoint | Description |
|--------|----------|-------------|
| POST | `/api/v1/borrowings/borrow` | Borrow a book (only if available) |
| POST | `/api/v1/borrowings/return` | Return a book (only if borrowed) |
| GET | `/api/v1/borrowings/book/{bookId}` | Get borrowing history by book |
| GET | `/api/v1/borrowings/borrower/{name}` | Get borrowing history by borrower |

### 🔍 Advanced Search

```http
GET /api/v1/books?title={title}&author={author}&year={year}&status={status}&page={page}&size={size}&sort={sort}
```

**Example:**
```http
GET /api/v1/books?title=Spring&author=John&page=0&size=10&sort=title,asc
```

---

## 🧪 Quality Assurance & DevOps

| Aspect | Implementation |
|--------|---------------|
| **Testing** | Unit tests with JUnit 5 and Mockito for service/controller layers. Integration tests using Testcontainers with real PostgreSQL. End-to-end tests covering full API flows. Maintains 80%+ code coverage verified by JaCoCo. |
| **Code Quality** | Google Java Format enforced by Spotless. Static code analysis with PMD to catch bugs, dead code, and performance issues. Clean Code practices with meaningful naming and proper separation of concerns. |
| **Security** | Bean Validation for input validation. SQL injection prevention via JPA/Hibernate. Environment-specific configurations (dev, test, docker). CORS configuration for cross-origin requests. Soft delete for data retention. Actuator endpoints for health monitoring. |
| **CI/CD** | GitHub Actions pipeline on every push to master: Maven build → Unit tests → Docker image build → Integration tests with Docker Compose → JaCoCo coverage report → PMD static analysis → Spotless formatting check. |
| **Deployment** | Containerized with Docker using multi-stage builds for optimized image size. Ready for cloud deployment on AWS ECS, Azure AKS, or Google GKE. |

---

## 🎯 Key Design Decisions

<!-- This section will be completed with 3 important design choices -->

---

## 🚀 Performance Optimizations

- ✅ Connection pooling with HikariCP
- ✅ Lazy loading for entity relationships
- ✅ Query optimization with Spring Data JPA
- ✅ Pagination to prevent large data transfers
- ✅ Batch processing for bulk operations
- ✅ Indexed database columns for performance

---

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
11. **GraphQL Support**: Alternative query interface for flexibility
12. **WebSocket Notifications**: Real-time updates for book availability
13. **Bulk Operations**: Import/export books via CSV/Excel
14. **Search Optimization**: Elasticsearch integration for advanced search

---

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---

## 👤 Contact

**Mehdy Mokhtari**
- 📧 Email: mh.mokhtari7@gmail.com
- 🐙 GitHub: [@MahdyMokh7](https://github.com/MahdyMokh7)
- 🔗 LinkedIn: [Mehdy Mokhtari](https://linkedin.com/in/mehdymokhtari)

Project Link: [https://github.com/MahdyMokh7/library-management-api](https://github.com/MahdyMokh7/library-management-api)

---

**Made with ❤️ by Mehdy Mokhtari**