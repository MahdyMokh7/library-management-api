package com.mehdymokhtari.libraryapi.model.dto.response;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;

public record BorrowingRecordResponse(
    Long id,
    Long itemId,
    String itemTitle,
    String borrowerName,
    LocalDate borrowedDate,
    LocalDate returnDate,
    BorrowingStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
