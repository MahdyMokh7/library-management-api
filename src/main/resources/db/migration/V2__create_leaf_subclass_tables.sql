-- 1. Concrete Leaf Table: books
CREATE TABLE IF NOT EXISTS books (
    item_id BIGINT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(17) NOT NULL UNIQUE,
    edition INT,
    publisher VARCHAR(255),
    CONSTRAINT fk_books_physical FOREIGN KEY (item_id) REFERENCES physical_items(item_id) ON DELETE CASCADE
);

-- 2. Concrete Leaf Table: audio_books

-- 3. Concrete Leaf Table: ebooks

-- 4. Concrete Leaf Table: magazines

-- Subclass Domain Query Indexes
CREATE UNIQUE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_status ON books(status);