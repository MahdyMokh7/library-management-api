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

## 🛠️ Technology Stack

| Technology | Version |
|------------|---------|
| Java | 22 |
| Spring Boot | 3.2.4 |
| Spring Data JPA | 3.2.4 |
| Hibernate | 6.4.4 |
| PostgreSQL | 15.x |
| Maven | 3.9.x |
| JUnit | 5.10.2 |
| MapStruct | 1.5.5.Final |
| Flyway | 9.22.3 |
| SpringDoc OpenAPI | 2.4.0 |
| Docker | Latest |
| Testcontainers | 1.19.7 |

## 📋 Prerequisites

Before running the project, ensure you have the following installed:

- ☕ Java 22 or higher
- 📦 Maven 3.9+
- 🐘 PostgreSQL 15+ (for local development)
- 🐳 Docker and Docker Compose (for containerized deployment)
- 🔧 Git
- 💻 IDE (IntelliJ IDEA)

## 🚀 Getting Started

### Option 1: Docker (Recommended for Production)

**This is the preferred method** as it ensures consistency across environments.

```bash
# 1. Clone the repository
git clone https://github.com/yourusername/library-management-system.git
cd library-management-system

# 2. Build the Docker image
docker build -t library-api:1.0.0 .

# 3. Start all services (PostgreSQL + Application)
docker-compose up -d

# 4. Wait for services to start (approx 30 seconds)
docker-compose logs -f app

# 5. Verify the application is running
curl http://localhost:8080/actuator/health
```

**Docker will handle:**
- ✅ PostgreSQL database setup
- ✅ Database migrations (Flyway)
- ✅ Application deployment
- ✅ Network configuration

### Option 2: Local Development (Maven)

**Use this for active development** with faster feedback loops.

#### 1. Clone the Repository
```bash
git clone https://github.com/yourusername/library-management-system.git
cd library-management-system
```

#### 2. Configure Database

Create a PostgreSQL database:
```sql
CREATE DATABASE library_db;
CREATE USER library_user WITH PASSWORD 'library_password';
GRANT ALL PRIVILEGES ON DATABASE library_db TO library_user;
```

Update `application.yml` with your credentials:
```yaml
spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/library_db
    username: library_user
    password: library_password
```

#### 3. Build the Project
```bash
mvn clean install
```

#### 4. Run Database Migrations
Flyway will automatically run migrations on application startup.

#### 5. Run the Application
```bash
mvn spring-boot:run
```

Or using the JAR:
```bash
java -jar target/libraryapi.jar
```

### Option 3: Quick Development (Docker for DB only)

**Use this to develop with Docker database but Maven for the app.**

```bash
# Start only PostgreSQL in Docker
docker-compose up -d postgres

# Run the application with Maven
mvn spring-boot:run
```

### Access the Application

- 🌐 **API Base URL**: `http://localhost:8080/api/v1`
- 📚 **Swagger UI**: `http://localhost:8080/swagger-ui.html`
- 📊 **OpenAPI Docs**: `http://localhost:8080/v3/api-docs`
- 💚 **Health Check**: `http://localhost:8080/actuator/health`

## 🏗️ Project Structure

```
library-management-system/
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
│   │   │   │   ├── enums/           # Enumerations
│   │   │   │   └── mapper/          # MapStruct mappers
│   │   │   ├── exception/           # Global exception handling
│   │   │   ├── validator/           # Custom validators
│   │   │   ├── filter/              # Search filters
│   │   │   └── util/                # Utility classes
│   │   └── resources/
│   │       ├── application.yml      # Main configuration
│   │       ├── application-docker.yml # Docker-specific config
│   │       ├── db/migration/        # Flyway migrations
│   │       └── static/              # Static resources
│   └── test/
│       ├── java/                    # Unit & integration tests
│       └── resources/               # Test configurations
├── .github/workflows/               # CI/CD pipelines
├── Dockerfile                       # Docker image definition
├── docker-compose.yml               # Docker services orchestration
├── docker-compose.prod.yml          # Production compose overrides
├── .dockerignore                    # Docker ignore file
├── pom.xml                          # Maven dependencies
├── LICENSE                          # License file
└── README.md                        # This file
```

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

## 🧪 Testing

### Run Unit Tests
```bash
# Run all unit tests
mvn test

# Run specific test class
mvn test -Dtest=BookServiceTest
```

### Run Integration Tests
```bash
# Run all integration tests
mvn verify

# Run specific integration test
mvn verify -Dtest=BookControllerIntegrationTest
```

### Run Tests in Docker Container
```bash
# Run tests inside the container
docker-compose run --rm app mvn test

# Run integration tests in container
docker-compose run --rm app mvn verify
```

### Test Coverage Report
```bash
# Generate JaCoCo coverage report
mvn jacoco:report

# View report
open target/site/jacoco/index.html
```

## 🤖 CI/CD Pipeline

This project uses GitHub Actions for continuous integration and delivery.

### Automated Workflow

The pipeline runs on every push and pull request:

1. ✅ **Build**: Compiles and builds the application with Maven
2. ✅ **Unit Tests**: Runs all unit tests
3. ✅ **Docker Build**: Builds the Docker image
4. ✅ **Integration Tests**: Runs tests against Docker containers
5. ✅ **Code Quality**: Static code analysis with JaCoCo
6. ✅ **Deployment**: Automatic deployment to staging (on main branch)

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
        options: >-
          --health-cmd pg_isready
          --health-interval 10s
          --health-timeout 5s
          --health-retries 5
    steps:
      - uses: actions/checkout@v4
      
      - name: Set up JDK 22
        uses: actions/setup-java@v4
        with:
          java-version: '22'
          distribution: 'temurin'
      
      - name: Cache Maven packages
        uses: actions/cache@v3
        with:
          path: ~/.m2
          key: ${{ runner.os }}-m2-${{ hashFiles('**/pom.xml') }}
          restore-keys: ${{ runner.os }}-m2
      
      - name: Build with Maven
        run: mvn clean package -DskipTests
      
      - name: Build Docker image
        run: docker build -t library-api:latest .
      
      - name: Start Docker Compose services
        run: docker-compose up -d
      
      - name: Wait for services
        run: sleep 30
      
      - name: Run integration tests
        run: docker-compose run --rm app mvn verify
      
      - name: Generate coverage report
        run: docker-compose run --rm app mvn jacoco:report
      
      - name: Check application health
        run: curl -f http://localhost:8080/actuator/health
      
      - name: Push Docker image to registry
        if: github.ref == 'refs/heads/main'
        uses: docker/login-action@v3
        with:
          username: ${{ secrets.DOCKER_USERNAME }}
          password: ${{ secrets.DOCKER_PASSWORD }}
      
      - name: Push to Docker Hub
        if: github.ref == 'refs/heads/main'
        run: docker push library-api:latest
```

## 🎯 Key Design Decisions

### 1. **Layered Architecture with DDD Principles**
We adopted Domain-Driven Design patterns to ensure clear separation of concerns:
- **Why**: Maintainability and scalability
- **Impact**: Each layer has a single responsibility, making the codebase easier to test and extend
- **SOLID**: Single Responsibility Principle applied throughout

### 2. **Specification Pattern for Dynamic Queries**
Implemented Spring Data JPA Specifications for flexible search:
- **Why**: Avoid complex query strings and maintain type safety
- **Impact**: Easy to add new filters without modifying existing code (Open/Closed Principle)
- **Benefit**: Clean, reusable, and testable query logic

### 3. **Soft Delete with Status Management**
Used `is_deleted` flag instead of physical deletion:
- **Why**: Data retention, audit trail, and business requirements
- **Impact**: Historical data preserved, GDPR compliance, and recovery capabilities
- **Benefit**: No data loss, ability to track deletion history

### 4. **DTO Pattern with MapStruct**
Separated internal entities from external API contracts:
- **Why**: API versioning, security, and performance optimization
- **Impact**: Prevents over-exposure of internal data, better control over API responses
- **Benefit**: Compile-time mapping with MapStruct (no reflection overhead)

### 5. **Optimistic Locking for Concurrency**
Implemented `@Version` annotation on entities:
- **Why**: Prevent concurrent updates conflicts
- **Impact**: Ensures data consistency in multi-user scenarios
- **Benefit**: Automatic retry mechanism for concurrent operations

### 6. **Containerization with Docker**
- **Why**: Consistent environments, easy deployment, scalability
- **Impact**: Production-ready application that runs anywhere
- **Benefit**: Reduced "works on my machine" problems

### 7. **Comprehensive Exception Handling**
Global exception handler with structured error responses:
- **Why**: Consistent error handling across the application
- **Impact**: Better client experience, proper HTTP status codes
- **Benefit**: Centralized error logging and monitoring

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

CREATE INDEX idx_books_title ON books(title);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_status ON books(status);
CREATE INDEX idx_books_is_deleted ON books(is_deleted);
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
    CONSTRAINT fk_book FOREIGN KEY (book_id) REFERENCES books(id),
    CONSTRAINT unique_borrowing UNIQUE (book_id, status) 
        WHERE status = 'BORROWED'
);

CREATE INDEX idx_borrowing_book ON borrowing_records(book_id);
CREATE INDEX idx_borrowing_borrower ON borrowing_records(borrower_name);
CREATE INDEX idx_borrowing_status ON borrowing_records(status);
```

## 🔒 Security Considerations

- ✅ Input validation using Bean Validation
- ✅ SQL injection prevention through JPA/Hibernate
- ✅ Proper transaction management with `@Transactional`
- ✅ Exception handling to prevent information leakage
- ✅ Environment-specific configurations (dev, prod, docker)
- ✅ CORS configuration for cross-origin requests
- ✅ Actuator endpoints secured (production)
- ✅ Soft delete for data retention

## 🐳 Docker Commands Reference

### Basic Commands
```bash
# Build the image
docker build -t library-api:1.0.0 .

# Run all services
docker-compose up -d

# View logs
docker-compose logs -f app

# Stop all services
docker-compose down

# Stop and remove volumes (clean database)
docker-compose down -v

# Rebuild and start
docker-compose up -d --build
```

### Advanced Commands
```bash
# Run with specific profile
docker-compose --profile dev up

# Run specific service only
docker-compose up -d postgres

# Execute command in container
docker exec -it library-app /bin/sh

# View container statistics
docker stats library-app

# Clean unused Docker resources
docker system prune -a
```

### Health Checks
```bash
# Check application health
curl http://localhost:8080/actuator/health

# Check database connection
docker exec -it library-postgres psql -U library_user -d library_db

# Check container logs
docker-compose logs --tail=100 app
```

## 📦 Production Deployment

### With Docker Compose
```bash
# Use production configuration
docker-compose -f docker-compose.yml -f docker-compose.prod.yml up -d
```

### With Kubernetes (Future Enhancement)
```bash
# Apply Kubernetes manifests
kubectl apply -f k8s/deployment.yaml
kubectl apply -f k8s/service.yaml
```

### Cloud Deployment Options
- ☁️ **AWS ECS**: Deploy Docker containers to AWS
- ☁️ **Azure AKS**: Kubernetes on Azure
- ☁️ **Google GKE**: Kubernetes on GCP
- ☁️ **Heroku**: Simple container deployment

## 🚀 Performance Optimizations

- ✅ Connection pooling with HikariCP
- ✅ Lazy loading for entity relationships
- ✅ Query optimization with Spring Data JPA
- ✅ Pagination to prevent large data transfers
- ✅ Caching (planned: Redis implementation)
- ✅ Batch processing for bulk operations
- ✅ Indexed database columns for performance

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

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add some amazing feature'`)
4. Ensure code passes all tests (`mvn verify`)
5. Push to the branch (`git push origin feature/amazing-feature`)
6. Open a Pull Request

### Code Standards
- ✅ Follow Google Java Format (enforced by Spotless)
- ✅ Write comprehensive unit tests
- ✅ Maintain minimum 80% code coverage
- ✅ Document all public APIs with Javadoc
- ✅ Use meaningful variable and method names

## 📄 License

This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

## 👤 Contact

**Mehdy Mokhtari**
- 📧 Email: mh.mokhtari7@gmail.com
- 🐙 GitHub: [@MahdyMokh7](https://github.com/MahdyMokh7)
- 🔗 LinkedIn: [Mehdy Mokhtari](https://linkedin.com/in/mehdymokhtari)

Project Link: [https://github.com/MahdyMokh7/library-management-api](https://github.com/MahdyMokh7/library-management-api)
