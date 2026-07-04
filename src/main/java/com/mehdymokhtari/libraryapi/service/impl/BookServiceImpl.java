package com.mehdymokhtari.libraryapi.service.impl;

import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.PagedResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.filter.BookFilter;
import com.mehdymokhtari.libraryapi.repository.BookRepository;
import com.mehdymokhtari.libraryapi.repository.spec.BookSpecification;
import com.mehdymokhtari.libraryapi.service.BookService;
import com.mehdymokhtari.libraryapi.service.validation.BookValidationService;
import com.mehdymokhtari.libraryapi.model.mapper.BookMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper;
    private final BookValidationService validationService;

    @Override
    @Transactional
    public BookResponse createBook(BookRequest request) {
        log.debug("Creating book with ISBN: {}", request.isbn());
        validationService.validateCreateBook(request);

        Book book = bookMapper.toEntity(request);
        book.setStatus(BookStatus.AVAILABLE);
        Book saved = bookRepository.save(book);

        log.info("Book created with ID: {}", saved.getId());
        return bookMapper.toResponse(saved);
    }

    @Override
    public PagedResponse<BookResponse> getAllBooks(BookFilter filter, Pageable pageable) {
        log.debug("Fetching all books with filters: {}, pageable: {}", filter, pageable);

        Specification<Book> spec = BookSpecification.withFilters(filter);
        Page<Book> page = bookRepository.findAll(spec, pageable);

        List<BookResponse> content = bookMapper.toResponseList(page.getContent());

        return new PagedResponse<>(
                content,
                page.getNumber(),
                page.getSize(),
                page.getTotalElements(),
                page.getTotalPages(),
                page.isLast()
        );
    }

    @Override
    public BookResponse getBookById(Long id) {
        log.debug("Fetching book by ID: {}", id);
        Book book = validationService.validateAndGetBook(id);
        return bookMapper.toResponse(book);
    }

    @Override
    @Transactional
    public BookResponse updateBook(Long id, BookUpdateRequest request) {
        log.debug("Updating book with ID: {}", id);
        validationService.validateUpdateBook(id, request);

        Book book = validationService.validateAndGetBook(id);
        bookMapper.updateEntity(request, book);
        Book updated = bookRepository.save(book);

        log.info("Book updated with ID: {}", updated.getId());
        return bookMapper.toResponse(updated);
    }

    @Override
    @Transactional
    public void deleteBook(Long id) {
        log.debug("Deleting book with ID: {}", id);
        validationService.validateDeleteBook(id);
        validationService.validateBookNotBorrowed(id);

        Book book = validationService.validateAndGetBook(id);
        book.setDeleted(true);
        bookRepository.save(book);

        log.info("Book soft-deleted with ID: {}", id);
    }
}