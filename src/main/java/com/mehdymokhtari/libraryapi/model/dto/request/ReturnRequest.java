package com.mehdymokhtari.libraryapi.model.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public record ReturnRequest(
        @NotNull(message = "Book ID is required")
        @Positive(message = "Book ID must be a positive number")
        Long bookId
) {}