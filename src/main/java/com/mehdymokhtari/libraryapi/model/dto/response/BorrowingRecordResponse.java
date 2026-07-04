package com.mehdymokhtari.libraryapi.model.dto.response;

import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record BorrowingRecordResponse(
        Long id,
        Long bookId,
        String bookTitle,
        String borrowerName,
        LocalDate borrowedDate,
        LocalDate returnDate,
        BorrowingStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}