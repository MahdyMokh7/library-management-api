package com.mehdymokhtari.libraryapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Size;

// Uses BEAN Validator (like @NotBlank, @Size, @NotNull, etc.)
public record BookUpdateRequest(
    @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,
    @NotBlank(message = "Author is required")
        @Size(max = 255, message = "Author name must not exceed 255 characters")
        String author,
    @NotNull(message = "Publication year is required")
        @PastOrPresent(message = "Publication year cannot be in the future")
        Integer publicationYear) {}
