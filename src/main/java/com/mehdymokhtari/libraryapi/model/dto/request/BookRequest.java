package com.mehdymokhtari.libraryapi.model.dto.request;

import jakarta.validation.constraints.*;

// Uses BEAN Validator (like @NotBlank, @Size, @NotNull, etc.)
public record BookRequest(
    @NotBlank(message = "Title is required")
        @Size(max = 255, message = "Title must not exceed 255 characters")
        String title,
    @NotBlank(message = "Author is required")
        @Size(max = 255, message = "Author name must not exceed 255 characters")
        String author,
    @NotBlank(message = "ISBN is required")
        @Pattern(
            regexp = "^(?=(?:\\D*\\d){10}(?:(?:\\D*\\d){3})?$)[\\d-]+$",
            message = "ISBN must be a valid format (10 or 13 digits)")
        @Size(max = 17, message = "ISBN must not exceed 17 characters")
        String isbn,
    @NotNull(message = "Publication year is required")
        @Min(value = 1450, message = "Publication year must be at least 1450")
        @Max(value = 2100, message = "Publication year cannot exceed 2100")
        Integer publicationYear) {}
