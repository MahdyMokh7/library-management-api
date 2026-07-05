package com.mehdymokhtari.libraryapi.model.dto.response;

import java.time.LocalDateTime;

import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

public record BookResponse(
    Long id,
    String title,
    String author,
    String isbn,
    Integer publicationYear,
    BookStatus status,
    LocalDateTime createdAt,
    LocalDateTime updatedAt) {}
