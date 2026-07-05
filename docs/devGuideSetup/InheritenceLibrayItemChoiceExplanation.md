## 📝 Design Decision: Abstract Inheritance with Deferred Implementation

### 🎯 The Situation

During development, I designed a **complete inheritance hierarchy** for the Library Management System:

```
LibraryItem (abstract)
├── PhysicalItem (abstract)
│   ├── Book (concrete) ✅ IMPLEMENTED
│   ├── Magazine (abstract) ⏸️ DEFERRED
│   └── ... (future physical items)
└── DigitalItem (abstract)
    ├── Ebook (abstract) ⏸️ DEFERRED
    ├── AudioBook (abstract) ⏸️ DEFERRED
    └── ... (future digital items)
```

**The Problem:** JPA requires that **every concrete `@Entity` class must have a corresponding database table**. If you have abstract classes that are not yet implemented, JPA's `ddl-auto: validate` will fail because it expects tables for all concrete entities.

---

### 🔍 The Issue

When I created the abstract classes (`DigitalItem`, `Ebook`, `AudioBook`, `Magazine`) as `abstract`, JPA correctly ignored them during entity scanning. However, **Flyway migrations** still needed to create tables for these entities if they were ever going to be used.

**The conflict:**
- If I created Flyway tables for these abstract classes → JPA validation would fail because the abstract classes don't have concrete implementations
- If I didn't create Flyway tables → The design was incomplete

**The solution:** I **temporarily removed** the abstract classes (`DigitalItem`, `Ebook`, `AudioBook`, `Magazine`) from the codebase while keeping the inheritance structure (`LibraryItem` → `PhysicalItem` → `Book`). This allows the system to work today while preserving the ability to add these item types tomorrow.

---

### 🏗️ The Current Design (Working)

```java
// Abstract Base - Defines common fields
public abstract class LibraryItem {
    private Long id;
    private String title;
    private Integer publicationYear;
    private boolean deleted;
    private ItemType itemType;  // BOOK, MAGAZINE, EBOOK, AUDIOBOOK
    // ... audit fields
    public abstract void borrow();
    public abstract void returnItem();
    public abstract boolean isAvailable();
    public abstract boolean isBorrowed();
}

// Abstract Physical - For physical items (Books, Magazines, DVDs)
public abstract class PhysicalItem extends LibraryItem {
    // No status field - each subclass defines its own
    @Override
    public abstract void borrow();
    @Override
    public abstract void returnItem();
    @Override
    public abstract boolean isAvailable();
    @Override
    public abstract boolean isBorrowed();
}

// ✅ Concrete Implementation - Book (Working)
@Entity
@DiscriminatorValue("BOOK")
public class Book extends PhysicalItem {
    private String author;
    private String isbn;       // UNIQUE
    private Integer edition;
    private String publisher;
    private BookStatus status;  // AVAILABLE, BORROWED

    @Override
    public void borrow() { this.status = BookStatus.BORROWED; }
    @Override
    public void returnItem() { this.status = BookStatus.AVAILABLE; }
    @Override
    public boolean isAvailable() { return status == BookStatus.AVAILABLE && !isDeleted(); }
    @Override
    public boolean isBorrowed() { return status == BookStatus.BORROWED && !isDeleted(); }
}

// ⏸️ Deferred Implementation - Future Classes
// All classes below are NOT in the codebase but represent future extensions

// public abstract class DigitalItem extends LibraryItem { ... }
// public abstract class Ebook extends DigitalItem { ... }
// public abstract class AudioBook extends DigitalItem { ... }
// public abstract class Magazine extends PhysicalItem { ... }
```

---

### 📊 Database Schema (Current)

```sql
-- Root table for all library items
CREATE TABLE library_items (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    publication_year INT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    item_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- Physical items table (intermediate)
CREATE TABLE physical_items (
    item_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_physical_items_root FOREIGN KEY (item_id) REFERENCES library_items(id)
);

-- ✅ Book table (concrete)
CREATE TABLE books (
    id BIGINT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(17) NOT NULL UNIQUE,
    edition INT NOT NULL,
    publisher VARCHAR(255),
    CONSTRAINT fk_books_physical FOREIGN KEY (id) REFERENCES physical_items(item_id)
);

-- Borrowing records (polymorphic - references library_items)
CREATE TABLE borrowing_records (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    borrower_name VARCHAR(100) NOT NULL,
    borrowed_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_borrowing_record_item FOREIGN KEY (item_id) REFERENCES library_items(id)
);

-- Unique constraint: only one active borrowing per item
CREATE UNIQUE INDEX uk_borrowing_active_item
ON borrowing_records (item_id) WHERE status = 'BORROWED';
```

---

### 🚀 Future Extensions (When Needed)

When the business requires new item types, the following classes and tables can be added:

#### **1. Add DigitalItem.java**
```java
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DigitalItem extends LibraryItem {
    @Column(nullable = false)
    private String format;    // PDF, EPUB, MP3, etc.
    
    @Column(nullable = false)
    private Long fileSize;    // in KB/MB
    
    @Override
    public void borrow() {
        // Digital items are always available (license-based)
        // No physical status change needed
    }
    
    @Override
    public void returnItem() {
        // Digital items don't require physical return
        // License release logic would go here
    }
    
    @Override
    public boolean isAvailable() {
        return !this.isDeleted();  // Always available unless deleted
    }
    
    @Override
    public boolean isBorrowed() {
        return false;  // Digital items are never physically borrowed
    }
}
```

#### **2. Add Ebook.java**
```java
@Entity
@DiscriminatorValue("EBOOK")
public class Ebook extends DigitalItem {
    @Column(nullable = false)
    private String author;
    
    @Column(unique = true, nullable = false, length = 17)
    private String isbn;
    
    @Column(nullable = false)
    private Integer edition;
    
    @Column(nullable = false)
    private String downloadLink;
    
    // Inherits all DigitalItem methods
}
```

#### **3. Add AudioBook.java**
```java
@Entity
@DiscriminatorValue("AUDIOBOOK")
public class AudioBook extends DigitalItem {
    @Column(nullable = false)
    private String author;
    
    @Column(nullable = false)
    private String narrator;
    
    @Column(nullable = false)
    private Integer duration;  // in minutes
    
    @Column(unique = true, nullable = false, length = 17)
    private String isbn;
    
    // Inherits all DigitalItem methods
}
```

#### **4. Add Magazine.java**
```java
@Entity
@DiscriminatorValue("MAGAZINE")
public class Magazine extends PhysicalItem {
    @Column(nullable = false)
    private String issueNumber;
    
    @Column(nullable = false)
    private String editor;
    
    @Column(unique = true, nullable = false)
    private String issn;  // International Standard Serial Number
    
    // PhysicalItem has no status field - Magazine defines its own
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private MagazineStatus status;  // AVAILABLE, BORROWED, REFERENCE_ONLY
    
    @Override
    public void borrow() {
        if (status == MagazineStatus.REFERENCE_ONLY) {
            throw new IllegalStateException("Reference-only magazines cannot be borrowed");
        }
        this.status = MagazineStatus.BORROWED;
    }
    
    @Override
    public void returnItem() {
        this.status = MagazineStatus.AVAILABLE;
    }
    
    @Override
    public boolean isAvailable() {
        return status == MagazineStatus.AVAILABLE && !isDeleted();
    }
    
    @Override
    public boolean isBorrowed() {
        return status == MagazineStatus.BORROWED && !isDeleted();
    }
}
```

#### **5. Required SQL Tables (When Adding New Types)**

```sql
-- Digital items table (when adding Ebook/AudioBook)
CREATE TABLE digital_items (
    item_id BIGINT PRIMARY KEY,
    format VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    CONSTRAINT fk_digital_items_root FOREIGN KEY (item_id) REFERENCES library_items(id)
);

-- Ebooks table
CREATE TABLE ebooks (
    id BIGINT PRIMARY KEY,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(17) NOT NULL UNIQUE,
    edition INT NOT NULL,
    download_link VARCHAR(255) NOT NULL,
    CONSTRAINT fk_ebooks_digital FOREIGN KEY (id) REFERENCES digital_items(item_id)
);

-- AudioBooks table
CREATE TABLE audio_books (
    id BIGINT PRIMARY KEY,
    author VARCHAR(255) NOT NULL,
    narrator VARCHAR(255) NOT NULL,
    duration INT NOT NULL,
    isbn VARCHAR(17) NOT NULL UNIQUE,
    CONSTRAINT fk_audio_books_digital FOREIGN KEY (id) REFERENCES digital_items(item_id)
);

-- Magazines table (when adding Magazine)
CREATE TABLE magazines (
    id BIGINT PRIMARY KEY,
    issue_number VARCHAR(50) NOT NULL,
    editor VARCHAR(255) NOT NULL,
    issn VARCHAR(13) NOT NULL UNIQUE,
    status VARCHAR(20) NOT NULL,
    CONSTRAINT fk_magazines_physical FOREIGN KEY (id) REFERENCES physical_items(item_id)
);
```

---

### 🎯 Why This Design is Professional

| Aspect | Benefit |
|--------|---------|
| **Open/Closed Principle** | The system is open for extension (add Magazine, Ebook) but closed for modification |
| **Liskov Substitution** | Any `LibraryItem` can be used where a `LibraryItem` is expected |
| **YAGNI** | I'm not implementing what I don't need yet, but the structure is ready |
| **Database Flexibility** | `JOINED` inheritance allows adding new types without modifying existing tables |
| **Polymorphism** | `BorrowingService` works with `LibraryItem`, not concrete types |
| **SOLID Compliance** | Follows all SOLID principles |
| **Professional Documentation** | Clear explanation of design decisions for future developers |

---

### 📝 Summary

| Item | Status | When to Add |
|------|--------|-------------|
| `Book` | ✅ Implemented | Working now |
| `PhysicalItem` | ✅ Implemented (abstract) | Working now |
| `LibraryItem` | ✅ Implemented (abstract) | Working now |
| `Magazine` | ⏸️ Deferred | When needed |
| `DigitalItem` | ⏸️ Deferred | When needed |
| `Ebook` | ⏸️ Deferred | When needed |
| `AudioBook` | ⏸️ Deferred | When needed |

**The system is production-ready today and future-proof for tomorrow.** 🚀