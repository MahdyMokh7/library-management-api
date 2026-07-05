package com.mehdymokhtari.libraryapi.model.dto.request;

import jakarta.validation.constraints.*;

// Uses BEAN Validator (like @NotBlank, @Size, @NotNull, etc.)
public record BookUpdateRequest(
    @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,
    @NotBlank(message = "Author is required")
        @Size(max = 255, message = "Author name must not exceed 255 characters")
        String author,
    @NotNull(message = "Publication year is required")
        @Min(value = 1450, message = "Publication year must be at least 1450")
        @Max(value = 2100, message = "Publication year cannot exceed 2100")
        Integer publicationYear) {}
