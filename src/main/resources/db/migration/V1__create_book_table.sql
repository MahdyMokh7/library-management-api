-- Create books table
CREATE TABLE IF NOT EXISTS books (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    publication_year INT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    item_type VARCHAR(20) DEFAULT 'BOOK',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,

    -- PhysicalItem fields
    status VARCHAR(20) NOT NULL,

    -- Book specific fields
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(17) NOT NULL UNIQUE,
    edition INT NOT NULL,
    publisher VARCHAR(255)
);

-- ============================================================
-- INDEXES (Only for frequently queried fields)
-- ============================================================

-- 1. ISBN lookup - Very frequent (find by ISBN)
CREATE UNIQUE INDEX idx_books_isbn ON books(isbn);

-- 2. Title search - Frequent (search by title)
CREATE INDEX idx_books_title ON books(title);

-- 3. Author search - Frequent (search by author)
CREATE INDEX idx_books_author ON books(author);

-- 4. Status filter - Frequent (AVAILABLE/BORROWED)
CREATE INDEX idx_books_status ON books(status);

-- 5. Partial index for deleted = false (most queries exclude deleted)
CREATE INDEX idx_books_not_deleted ON books(id) WHERE deleted = false;

-- 6. Composite index for common filter combinations
CREATE INDEX idx_books_status_deleted ON books(status, deleted);