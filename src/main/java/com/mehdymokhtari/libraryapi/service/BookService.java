package com.mehdymokhtari.libraryapi.service;

import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.PagedResponse;
import com.mehdymokhtari.libraryapi.filter.BookFilter;
import org.springframework.data.domain.Pageable;

public interface BookService {

    BookResponse createBook(BookRequest request);

    PagedResponse<BookResponse> getAllBooks(BookFilter filter, Pageable pageable);

    BookResponse getBookById(Long id);

    BookResponse updateBook(Long id, BookUpdateRequest request);

    void deleteBook(Long id);
}