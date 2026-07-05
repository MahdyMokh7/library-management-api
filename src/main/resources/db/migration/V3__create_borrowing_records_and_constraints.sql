CREATE TABLE IF NOT EXISTS borrowing_records (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL, -- Pointing to root library_items table now!
    borrower_name VARCHAR(100) NOT NULL,
    borrowed_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,
    CONSTRAINT fk_borrowing_record_item FOREIGN KEY (item_id) REFERENCES library_items(id) ON DELETE CASCADE
);

-- Business Constraint: Ensure only one active borrowing per library item globally
CREATE UNIQUE INDEX uk_borrowing_active_item
ON borrowing_records (item_id)
WHERE status = 'BORROWED';

-- Analytical/Operational Indexes
CREATE INDEX idx_borrowing_item_status ON borrowing_records(item_id, status);
CREATE INDEX idx_borrowing_borrower ON borrowing_records(borrower_name);