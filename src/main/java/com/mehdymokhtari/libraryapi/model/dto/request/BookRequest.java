package com.mehdymokhtari.libraryapi.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

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
        @PastOrPresent(message = "Publication year cannot be in the future")
        Integer publicationYear) {}
