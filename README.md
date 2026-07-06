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
| **PMD** | 7.7.0       | Static code analysis |
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
chmod +x scripts/start.sh
```

**Run the full stack:**
```bash
./scripts/start.sh run
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
./scripts/start.sh clean
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

### 1. Abstract Inheritance with Deferred Implementation

**Decision:** I designed a complete inheritance hierarchy (`LibraryItem` → `PhysicalItem` → `Book`) with abstract intermediate classes, implementing only `Book` while keeping `Magazine`, `Ebook`, and `AudioBook` as abstract placeholders.

**Why I Chose This:** During development, I anticipated that a real library system would need to support multiple media types. Rather than building everything upfront (over-engineering), I created the structure to easily add new types later. The `JOINED` inheritance strategy ensures each new type gets its own table while sharing common fields through the root `library_items` table. This follows the Open/Closed Principle—new types can be added without touching existing `Book` or borrowing logic.

---

### 2. One Borrowing Record Per Transaction

**Decision:** Each borrowing transaction creates a single record that tracks the entire lifecycle (borrow → return), rather than creating separate borrow and return records.

**Why I Chose This:** When building the borrowing system, I realized that tracking one record per transaction is simpler and more performant. A book is borrowed (status: `BORROWED`), and when returned, the same record is updated (status: `RETURNED`). This keeps queries fast (`SELECT * FROM borrowing_records WHERE item_id = ? AND status = 'BORROWED'`), handles partial returns naturally, and provides a complete audit trail—exactly how real library systems work.

---

### 3. Book as Individual Instance (Not Copies)

**Decision:** Each book is stored as a separate record with its own `ISBN` and `status`, rather than using a "copies" counter.

**Why I Chose This:** The specification required tracking each book individually with its own status. During implementation, I considered a "copies" field with `availableCopies` counters but quickly realized it would complicate borrow/return logic, lose individual copy history, and fail to handle scenarios where copies have unique conditions or borrowing histories. Storing each book as an instance keeps the design simple, accurate, and aligned with the document's requirements.

---

### 4. Flyway + JPA Hybrid (Schema Management)

**Decision:** Used Flyway for schema creation and versioning, with JPA's `ddl-auto: validate` for runtime ORM operations.

**Why I Chose This:** I wanted a production-safe approach to database management. Flyway gives me version-controlled SQL migrations stored in `src/main/resources/db/migration/`, providing an audit trail of every schema change. JPA's `ddl-auto: validate` ensures my entities match the database without risking unexpected `ALTER TABLE` commands. This hybrid approach is industry standard—Flyway handles "what the schema looks like" (DDL), while JPA/Hibernate handles "how to map objects to tables" (ORM). I wrote the SQL migrations myself, giving full control over indexes, constraints, and data integrity.

---

### 5. Two Controllers Instead of One

**Decision:** Separated API endpoints into `BookController` (book management) and `BorrowingController` (borrowing/return operations).

**Why I Chose This:** While building the API, I realized that book management (CRUD) and borrowing operations are distinct business capabilities with different validation rules. Having two controllers keeps the code focused and maintainable—changes to borrowing logic don't affect book endpoints. Testing is also simpler with smaller, focused test classes. Swagger groups endpoints by domain, making the API documentation cleaner and more intuitive for consumers.

---

### 6. PostgreSQL over MySQL

**Decision:** PostgreSQL was chosen as the production database.

**Why I Chose This:** I chose PostgreSQL for its advanced features, strong ACID compliance, and rich extension ecosystem. While MySQL is a valid choice, PostgreSQL's superior JSONB support, full-text search capabilities, and proven reliability at scale made it the better foundation for a system that will evolve over time. Flyway makes the database choice transparent, but PostgreSQL provides the strongest foundation for future enhancements like geospatial library branches or advanced search.

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

### 1. Authentication & Authorization
- JWT-based user authentication with role-based access control (LIBRARIAN, MEMBER)
- Secure endpoint protection and method-level security using Spring Security

### 2. Reports & Analytics Dashboard
- Interactive dashboard showing borrowing statistics, popular books, and user activity trends
- Exportable reports (PDF, Excel) for administrative insights and decision-making

### 3. Email Notifications
- Automated reminder emails for overdue books before and after due dates
- Confirmation emails for successful borrowings, returns, and reservations

### 4. Caching Layer with Redis
- Implement Redis caching for frequently accessed book data and search results
- Reduce database load and improve API response times for high-traffic endpoints

### 5. Reservation System
- Allow users to reserve books that are currently borrowed

### 6. Edge Cases & Exception Handling Improvements
- Graceful handling of concurrent borrowing conflicts with retry mechanisms
- Improved validation for edge cases (e.g., borrowing on same day as return, maximum borrow limits)
- Comprehensive error codes and user-friendly error messages for all scenarios

### 7. CI/CD Pipeline Enhancements
- Separate workflows for `develop` and `main` branches with appropriate test stages
- **Develop branch**: Run unit tests, integration tests, and code quality checks on every push
- **Main branch**: Full test suite (unit + integration + E2E), security scans, and production Docker image builds

### 8. Expanded Inheritance Hierarchy
Complete the inheritance design for all library item types:

```
LibraryItem (abstract)
├── PhysicalItem (abstract)
│   ├── Book (concrete) ✅ Currently implemented
│   └── Magazine (concrete)
└── DigitalItem (abstract)
    ├── Ebook (concrete) with author, ISBN, edition, file format, download link
    └── AudioBook (concrete) with author, narrator, duration, ISBN, format
```

### 9. Better Documentation

---

**Note:** These enhancements are prioritized to improve system scalability, maintainability, and user experience. The authentication, analytics, and deployment improvements would provide immediate value, while the inheritance expansion and edge case handling ensure long-term system robustness.

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

