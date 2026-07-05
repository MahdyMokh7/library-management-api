-- Ensure only one active borrowing per item
CREATE UNIQUE INDEX uk_borrowing_active_item
ON borrowing_records (item_id)
WHERE status = 'BORROWED';