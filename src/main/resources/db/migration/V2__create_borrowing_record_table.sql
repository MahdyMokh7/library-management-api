-- Create borrowing records table
CREATE TABLE IF NOT EXISTS borrowing_records (
    id BIGSERIAL PRIMARY KEY,
    item_id BIGINT NOT NULL,
    borrower_name VARCHAR(100) NOT NULL,
    borrowed_date DATE NOT NULL,
    return_date DATE,
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    version BIGINT DEFAULT 0,

    CONSTRAINT fk_borrowing_record_item FOREIGN KEY (item_id) REFERENCES books(id)
);

-- ============================================================
-- INDEXES (Only for frequently queried fields)
-- ============================================================

-- 1. Composite index for active borrowing check (Most frequent query)
CREATE INDEX idx_borrowing_item_status ON borrowing_records(item_id, status);

-- 2. Borrower name search - Frequent (history by borrower)
CREATE INDEX idx_borrowing_borrower ON borrowing_records(borrower_name);

-- 3. Partial index for active borrowings only
CREATE INDEX idx_borrowing_active ON borrowing_records(item_id) WHERE status = 'BORROWED';

-- 4. Date range queries - for reports/analytics
CREATE INDEX idx_borrowing_borrowed_date ON borrowing_records(borrowed_date);