package com.mehdymokhtari.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.test.context.ActiveProfiles;

import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.filter.BookFilter;
import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.PagedResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.model.mapper.BookMapper;
import com.mehdymokhtari.libraryapi.repository.BookRepository;
import com.mehdymokhtari.libraryapi.service.impl.BookServiceImpl;
import com.mehdymokhtari.libraryapi.service.validation.BookValidationService;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BookServiceTest {

  @Mock private BookRepository bookRepository;

  @Mock private BookMapper bookMapper;

  @Mock private BookValidationService validationService;

  @InjectMocks private BookServiceImpl bookService;

  private Book book;
  private BookRequest bookRequest;
  private BookResponse bookResponse;
  private BookUpdateRequest updateRequest;

  @BeforeEach
  void setUp() {
    book =
        Book.builder()
            .id(1L)
            .title("Clean Code")
            .author("Robert Martin")
            .isbn("9780132350884")
            .publicationYear(2008)
            .status(BookStatus.AVAILABLE)
            .deleted(false)
            .build();

    bookRequest = new BookRequest("Clean Code", "Robert Martin", "9780132350884", 2008);

    bookResponse =
        new BookResponse(
            1L,
            "Clean Code",
            "Robert Martin",
            "9780132350884",
            2008,
            BookStatus.AVAILABLE,
            null,
            null);

    updateRequest = new BookUpdateRequest("Clean Code Updated", "Robert C. Martin", 2009);
  }

  @Test
  void shouldCreateBookSuccessfully() {
    // Test: Create book with valid data
    when(bookMapper.toEntity(bookRequest)).thenReturn(book);
    when(bookRepository.save(any(Book.class))).thenReturn(book);
    when(bookMapper.toResponse(any(Book.class))).thenReturn(bookResponse);

    BookResponse result = bookService.createBook(bookRequest);

    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("Clean Code");
    verify(validationService).validateCreateBook(bookRequest);
    verify(bookRepository).save(any(Book.class));
  }

  @Test
  void shouldThrowExceptionWhenCreatingBookWithDuplicateIsbn() {
    // Test: Create book with duplicate ISBN throws exception
    doThrow(new BusinessException("Book with ISBN 9780132350884 already exists"))
        .when(validationService)
        .validateCreateBook(bookRequest);

    assertThatThrownBy(() -> bookService.createBook(bookRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ISBN 9780132350884 already exists");
  }

  @Test
  void shouldGetBookByIdSuccessfully() {
    // Test: Get existing book by ID
    when(validationService.validateAndGetBook(1L)).thenReturn(book);
    when(bookMapper.toResponse(book)).thenReturn(bookResponse);

    BookResponse result = bookService.getBookById(1L);

    assertThat(result).isNotNull();
    assertThat(result.id()).isEqualTo(1L);
    assertThat(result.title()).isEqualTo("Clean Code");
  }

  @Test
  void shouldThrowExceptionWhenBookNotFound() {
    // Test: Get non-existing book throws exception
    when(validationService.validateAndGetBook(999L))
        .thenThrow(new BusinessException("Book with ID 999 does not exist"));

    assertThatThrownBy(() -> bookService.getBookById(999L))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ID 999 does not exist");
  }

  @Test
  void shouldGetAllBooksWithFiltersAndPagination() {
    // Test: Get all books with filters and pagination
    Pageable pageable = PageRequest.of(0, 10);
    Page<Book> bookPage = new PageImpl<>(List.of(book));
    BookFilter filter = BookFilter.builder().title("Clean").status(BookStatus.AVAILABLE).build();

    when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(bookPage);
    when(bookMapper.toResponseList(anyList())).thenReturn(List.of(bookResponse));

    PagedResponse<BookResponse> result = bookService.getAllBooks(filter, pageable);

    assertThat(result).isNotNull();
    assertThat(result.content()).hasSize(1);
    assertThat(result.totalElements()).isEqualTo(1);
    assertThat(result.pageNumber()).isEqualTo(0);
    assertThat(result.pageSize()).isEqualTo(10);
    assertThat(result.last()).isTrue();
  }

  @Test
  void shouldGetAllBooksWithNoFilters() {
    // Test: Get all books without any filters
    Pageable pageable = PageRequest.of(0, 10);
    Page<Book> bookPage = new PageImpl<>(List.of(book));

    when(bookRepository.findAll(any(Specification.class), any(Pageable.class)))
        .thenReturn(bookPage);
    when(bookMapper.toResponseList(anyList())).thenReturn(List.of(bookResponse));

    PagedResponse<BookResponse> result = bookService.getAllBooks(null, pageable);

    assertThat(result).isNotNull();
    assertThat(result.content()).hasSize(1);
  }

  @Test
  void shouldUpdateBookSuccessfully() {
    // Test: Update existing book
    Book updatedBook =
        Book.builder()
            .id(1L)
            .title("Clean Code Updated")
            .author("Robert C. Martin")
            .isbn("9780132350884")
            .publicationYear(2009)
            .status(BookStatus.AVAILABLE)
            .deleted(false)
            .build();

    BookResponse updatedResponse =
        new BookResponse(
            1L,
            "Clean Code Updated",
            "Robert C. Martin",
            "9780132350884",
            2009,
            BookStatus.AVAILABLE,
            null,
            null);

    when(validationService.validateAndGetBook(1L)).thenReturn(book);
    doNothing().when(validationService).validateUpdateBook(1L, updateRequest);
    when(bookRepository.save(any(Book.class))).thenReturn(updatedBook);
    when(bookMapper.toResponse(any(Book.class))).thenReturn(updatedResponse);

    BookResponse result = bookService.updateBook(1L, updateRequest);

    assertThat(result).isNotNull();
    assertThat(result.title()).isEqualTo("Clean Code Updated");
    assertThat(result.author()).isEqualTo("Robert C. Martin");
    assertThat(result.publicationYear()).isEqualTo(2009);
  }

  @Test
  void shouldThrowExceptionWhenUpdatingNonExistingBook() {
    // Test: Update non-existing book throws exception
    doThrow(new BusinessException("Book with ID 999 does not exist"))
        .when(validationService)
        .validateUpdateBook(999L, updateRequest);

    assertThatThrownBy(() -> bookService.updateBook(999L, updateRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ID 999 does not exist");
  }

  @Test
  void shouldDeleteBookSuccessfully() {
    // Test: Soft delete existing available book
    when(validationService.validateAndGetBook(1L)).thenReturn(book);
    doNothing().when(validationService).validateDeleteBook(1L);
    doNothing().when(validationService).validateBookNotBorrowed(1L);

    bookService.deleteBook(1L);

    verify(bookRepository).save(book);
    assertThat(book.isDeleted()).isTrue();
  }

  @Test
  void shouldThrowExceptionWhenDeletingBorrowedBook() {
    // Test: Delete borrowed book throws exception
    doThrow(new BusinessException("Cannot delete book that is currently borrowed"))
        .when(validationService)
        .validateBookNotBorrowed(1L);

    assertThatThrownBy(() -> bookService.deleteBook(1L))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Cannot delete book that is currently borrowed");
  }

  @Test
  void shouldThrowExceptionWhenDeletingNonExistingBook() {
    // Test: Delete non-existing book throws exception
    doThrow(new BusinessException("Book with ID 999 does not exist"))
        .when(validationService)
        .validateDeleteBook(999L);

    assertThatThrownBy(() -> bookService.deleteBook(999L))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ID 999 does not exist");
  }
}
