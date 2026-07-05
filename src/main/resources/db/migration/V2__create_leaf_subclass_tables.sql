-- Concrete Leaf Table: books
CREATE TABLE IF NOT EXISTS books (
    id BIGINT PRIMARY KEY,
    status VARCHAR(20) NOT NULL,
    author VARCHAR(255) NOT NULL,
    isbn VARCHAR(17) NOT NULL UNIQUE,
    edition INT NOT NULL,
    publisher VARCHAR(255),
    CONSTRAINT fk_books_physical FOREIGN KEY (id) REFERENCES physical_items(item_id) ON DELETE CASCADE
);

-- Concrete Leaf Table: audio_books
CREATE TABLE IF NOT EXISTS audio_books (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_audio_books_digital FOREIGN KEY (id) REFERENCES digital_items(id) ON DELETE CASCADE
);

-- Concrete Leaf Table: ebooks
CREATE TABLE IF NOT EXISTS ebooks (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_ebooks_digital FOREIGN KEY (id) REFERENCES digital_items(id) ON DELETE CASCADE
);

-- Concrete Leaf Table: magazines
CREATE TABLE IF NOT EXISTS magazines (
    id BIGINT PRIMARY KEY,
    CONSTRAINT fk_magazines_physical FOREIGN KEY (id) REFERENCES physical_items(item_id) ON DELETE CASCADE
);

-- Concrete Leaf Performance Indexes
CREATE UNIQUE INDEX idx_books_isbn ON books(isbn);
CREATE INDEX idx_books_author ON books(author);
CREATE INDEX idx_books_status ON books(status);