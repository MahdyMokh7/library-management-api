package com.mehdymokhtari.libraryapi.model.dto.response;

import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import java.time.LocalDateTime;

public record BookResponse(
        Long id,
        String title,
        String author,
        String isbn,
        Integer publicationYear,
        BookStatus status,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {}