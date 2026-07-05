package com.mehdymokhtari.libraryapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

public record BorrowRequest(
    @NotNull(message = "Item ID is required")
        @Positive(message = "Item ID must be a positive number")
        Long itemId,
    @NotBlank(message = "Borrower name is required")
        @Size(max = 100, message = "Borrower name must not exceed 100 characters")
        String borrowerName) {}
