-- 1. Create the Root Parent Table
CREATE TABLE IF NOT EXISTS library_items (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    publication_year INT NOT NULL,
    deleted BOOLEAN DEFAULT FALSE,
    item_type VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0
);

-- 2. Create the Intermediate Physical Class Table
CREATE TABLE IF NOT EXISTS physical_items (
    item_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_physical_items_root FOREIGN KEY (item_id) REFERENCES library_items(id) ON DELETE CASCADE
);

-- 3. Create the Intermediate Digital Class Table

-- Base Strategy Performance Optimization Indexes (KEEP THEM HERE)
CREATE INDEX idx_library_items_title ON library_items(title);
CREATE INDEX idx_library_items_not_deleted ON library_items(id) WHERE deleted = false;