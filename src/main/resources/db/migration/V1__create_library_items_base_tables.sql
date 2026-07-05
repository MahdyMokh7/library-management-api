-- 1. Root Table
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

-- 2. Intermediate Physical Class Table
CREATE TABLE IF NOT EXISTS physical_items (
    item_id BIGINT PRIMARY KEY,
    CONSTRAINT fk_physical_items_root FOREIGN KEY (item_id) REFERENCES library_items(id) ON DELETE CASCADE
);

-- 3. Intermediate Digital Class Table
CREATE TABLE IF NOT EXISTS digital_items (
    id BIGINT PRIMARY KEY, -- Matches Java side strategy naming
    format VARCHAR(255) NOT NULL,
    file_size BIGINT NOT NULL,
    CONSTRAINT fk_digital_items_root FOREIGN KEY (id) REFERENCES library_items(id) ON DELETE CASCADE
);

-- Indexing for frequent root lookups
CREATE INDEX idx_library_items_title ON library_items(title);
CREATE INDEX idx_library_items_not_deleted ON library_items(id) WHERE deleted = false;