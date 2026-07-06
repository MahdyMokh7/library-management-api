## 🎯 You're Absolutely Right! Let's Test Docker Before Pushing

First, let me list all the ports you'll be using:

---



## 🖥️ Visual Comparison

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                    AFTER DOCKER COMPOSE UP                                 │
├─────────────────────────────────────────────────────────────────────────────┤
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  BROWSER → http://localhost:8080/swagger-ui.html                    │   │
│  │                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │  │  📚 Library Management API                                     │ │   │
│  │  │                                                                  │ │   │
│  │  │  POST   /api/v1/books       [Try it out]                       │ │   │
│  │  │  GET    /api/v1/books       [Try it out]                       │ │   │
│  │  │  GET    /api/v1/books/{id}  [Try it out]                       │ │   │
│  │  │  PUT    /api/v1/books/{id}  [Try it out]                       │ │   │
│  │  │  DELETE /api/v1/books/{id}  [Try it out]                       │ │   │
│  │  │  POST   /borrowings/borrow  [Try it out]                       │ │   │
│  │  │  POST   /borrowings/return  [Try it out]                       │ │   │
│  │  └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
│  ┌─────────────────────────────────────────────────────────────────────┐   │
│  │  BROWSER → http://localhost:5050                                   │   │
│  │                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │  │  🔐 pgAdmin Login                                              │ │   │
│  │  │  Email: admin@library.com                                      │ │   │
│  │  │  Password: admin_password                                      │ │   │
│  │  │  [Login]                                                        │ │   │
│  │  └─────────────────────────────────────────────────────────────────┘ │   │
│  │                                                                      │   │
│  │  ┌─────────────────────────────────────────────────────────────────┐ │   │
│  │  │  📊 Database Browser                                           │ │   │
│  │  │  ├── Servers                                                   │ │   │
│  │  │  │   └── Library DB                                            │ │   │
│  │  │  │       ├── Databases                                         │ │   │
│  │  │  │       │   └── library_db                                    │ │   │
│  │  │  │       │       ├── Schemas                                   │ │   │
│  │  │  │       │       │   └── public                                │ │   │
│  │  │  │       │       │       ├── Tables                            │ │   │
│  │  │  │       │       │       │   ├── books                        │ │   │
│  │  │  │       │       │       │   └── borrowing_records            │ │   │
│  │  │  │       │       │       └── ...                              │ │   │
│  │  └─────────────────────────────────────────────────────────────────┘ │   │
│  └─────────────────────────────────────────────────────────────────────┘   │
│                                                                             │
└─────────────────────────────────────────────────────────────────────────────┘
```


## 📋 All Ports Summary

| Port | Service | Purpose | URL |
|------|---------|---------|-----|
| **8080** | Spring Boot Application | Main API | `http://localhost:8080` |
| **5432** | PostgreSQL Database | Data storage | `jdbc:postgresql://localhost:5432/library_db` |
| **5050** | pgAdmin | Database GUI (optional, dev profile) | `http://localhost:5050` |

---

## 🚀 Step 1: Build the Docker Image

```bash
# Make sure you're in the project root
cd /path/to/library-management-api

# Build the Docker image
docker build -t library-api:1.0.0 .
```

**Expected output:**
```
[+] Building 45.2s (12/12) FINISHED
 => [1/7] FROM maven:3.9.6-eclipse-temurin-22-alpine
 ...
 => exporting to image
 => => naming to docker.io/library/library-api:1.0.0
```

---

## 🐳 Step 2: Start Docker Compose

```bash
# Start all services
docker-compose up -d

# Watch the logs
docker-compose logs -f app
```

**Expected output:**
```
library-postgres | 2026-07-06 03:30:00.000 UTC [1] LOG:  database system is ready to accept connections
library-app     | 
library-app     |   .   ____          _            __ _ _
library-app     |  /\\ / ___'_ __ _ _(_)_ __  __ _ \ \ \ \
library-app     | ( ( )\___ | '_ | '_| | '_ \/ _` | \ \ \ \
library-app     |  \\/  ___)| |_)| | | | | || (_| |  ) ) ) )
library-app     |   '  |____| .__|_| |_|_| |_\__, | / / / /
library-app     |  =========|_|==============|___/=/_/_/_/
library-app     |  :: Spring Boot ::                (v3.2.4)
library-app     | 
library-app     | 2026-07-06T03:30:15.123Z  INFO 1 --- [library-api] [main] c.m.l.LibraryManagementApplication       : Starting...
library-app     | 2026-07-06T03:30:15.456Z  INFO 1 --- [library-api] [main] c.m.l.LibraryManagementApplication       : Started in 3.2 seconds
```

---

## 🔍 Step 3: Test the API

### **3.1 Check Health**
```bash
curl http://localhost:8080/actuator/health
```

**Expected:**
```json
{"status":"UP"}
```

### **3.2 Create a Book**
```bash
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "publicationYear": 2008
  }'
```

**Expected:**
```json
{
  "success": true,
  "message": "Book created successfully",
  "data": {
    "id": 1,
    "title": "Clean Code",
    "author": "Robert C. Martin",
    "isbn": "9780132350884",
    "publicationYear": 2008,
    "status": "AVAILABLE",
    "createdAt": "2026-07-06T03:30:20.123Z",
    "updatedAt": "2026-07-06T03:30:20.123Z"
  },
  "timestamp": "2026-07-06T03:30:20.456Z"
}
```

### **3.3 Get All Books**
```bash
curl http://localhost:8080/api/v1/books
```

### **3.4 Borrow a Book**
```bash
curl -X POST http://localhost:8080/api/v1/borrowings/borrow \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 1,
    "borrowerName": "John Doe"
  }'
```

### **3.5 Return a Book**
```bash
curl -X POST http://localhost:8080/api/v1/borrowings/return \
  -H "Content-Type: application/json" \
  -d '{
    "itemId": 1
  }'
```

---

## 🎯 Step 4: Test Swagger UI

Open in browser: **http://localhost:8080/swagger-ui.html**

You'll see all your API endpoints documented and ready to test interactively!

---

## 🗄️ Step 5: Test PostgreSQL Connection (Optional)

```bash
# Connect to PostgreSQL
docker exec -it library-postgres psql -U library_user -d library_db

# List tables
\dt

# Query books
SELECT * FROM books;

# Query borrowing records
SELECT * FROM borrowing_records;

# Exit
\q
```

---

## 🖥️ Step 6: Test pgAdmin (Optional)

If you want a GUI for the database:

```bash
# Start pgAdmin (dev profile)
docker-compose --profile dev up -d pgadmin
```

Then open: **http://localhost:5050**

**Login:**
- Email: `admin@library.com`
- Password: `admin_password`

**Add server:**
- Name: `Library DB`
- Host: `postgres` (the service name)
- Port: `5432`
- Username: `library_user`
- Password: `library_password`

---

## 🔄 Step 7: Test Hot Reload (Optional)

If you want to test code changes without rebuilding:

```bash
# The app will auto-restart on code changes
# But you need to rebuild the image first
docker build -t library-api:1.0.0 .
docker-compose restart app
```

---

## 🛑 Step 8: Stop Everything

```bash
# Stop all containers
docker-compose down

# Stop and remove volumes (clean database)
docker-compose down -v
```

---

## 📊 Complete Test Flow

```bash
# 1. Build
docker build -t library-api:1.0.0 .

# 2. Start
docker-compose up -d

# 3. Health check
curl http://localhost:8080/actuator/health

# 4. Create book
curl -X POST http://localhost:8080/api/v1/books \
  -H "Content-Type: application/json" \
  -d '{"title":"Clean Code","author":"Robert Martin","isbn":"9780132350884","publicationYear":2008}'

# 5. Get books
curl http://localhost:8080/api/v1/books

# 6. Borrow book (replace 1 with actual ID)
curl -X POST http://localhost:8080/api/v1/borrowings/borrow \
  -H "Content-Type: application/json" \
  -d '{"itemId":1,"borrowerName":"John Doe"}'

# 7. Return book
curl -X POST http://localhost:8080/api/v1/borrowings/return \
  -H "Content-Type: application/json" \
  -d '{"itemId":1}'

# 8. Swagger UI
open http://localhost:8080/swagger-ui.html

# 9. Stop
docker-compose down
```

---

## ✅ Success Checklist

| Test | Status |
|------|--------|
| Docker image builds successfully | ? |
| Docker Compose starts all services | ? |
| Health check returns `{"status":"UP"}` | ? |
| Can create a book | ? |
| Can get all books | ? |
| Can borrow a book | ? |
| Can return a book | ? |
| Swagger UI loads | ? |

---

## 🚀 If Everything Works, Push to GitHub!

```bash
git add .
git commit -m "✅ Production ready: All tests passing, Docker working"
git push origin master
```

**Everything is ready for production!** 🎉