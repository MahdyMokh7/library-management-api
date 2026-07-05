package com.mehdymokhtari.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import com.mehdymokhtari.libraryapi.model.mapper.BorrowingRecordMapper;
import com.mehdymokhtari.libraryapi.repository.BookRepository;
import com.mehdymokhtari.libraryapi.repository.BorrowingRecordRepository;
import com.mehdymokhtari.libraryapi.service.impl.BorrowingServiceImpl;
import com.mehdymokhtari.libraryapi.service.validation.BookStatusValidator;
import com.mehdymokhtari.libraryapi.service.validation.BookValidationService;
import com.mehdymokhtari.libraryapi.service.validation.BorrowingValidator;

@ExtendWith(MockitoExtension.class)
class BorrowingServiceTest {

  @Mock private BorrowingRecordRepository borrowingRecordRepository;

  @Mock private BookRepository bookRepository;

  @Mock private BorrowingRecordMapper borrowingRecordMapper;

  @Mock private BookValidationService bookValidationService;

  @Mock private BorrowingValidator borrowingValidator;

  @Mock private BookStatusValidator bookStatusValidator;

  @InjectMocks private BorrowingServiceImpl borrowingService;

  private Book book;
  private BorrowingRecord borrowingRecord;
  private BorrowRequest borrowRequest;
  private ReturnRequest returnRequest;
  private BorrowingRecordResponse borrowingResponse;

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
            .isDeleted(false)
            .build();

    borrowingRecord =
        BorrowingRecord.builder()
            .id(1L)
            .book(book)
            .borrowerName("John Doe")
            .borrowedDate(LocalDate.now())
            .status(BorrowingStatus.BORROWED)
            .build();

    borrowRequest = new BorrowRequest(1L, "John Doe");
    returnRequest = new ReturnRequest(1L);

    borrowingResponse =
        new BorrowingRecordResponse(
            1L,
            1L,
            "Clean Code",
            "John Doe",
            LocalDate.now(),
            null,
            BorrowingStatus.BORROWED,
            null,
            null);
  }

  @Test
  void shouldBorrowBookSuccessfully() {
    // Test: Borrow available book successfully
    when(bookValidationService.validateAndGetBook(1L)).thenReturn(book);
    doNothing().when(borrowingValidator).validateBorrowerName("John Doe");
    doNothing().when(bookStatusValidator).validateBookCanBeBorrowed(book);
    when(borrowingRecordMapper.toEntity(borrowRequest, book)).thenReturn(borrowingRecord);
    when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(borrowingRecord);
    when(bookRepository.save(any(Book.class))).thenReturn(book);
    when(borrowingRecordMapper.toResponse(any(BorrowingRecord.class)))
        .thenReturn(borrowingResponse);

    BorrowingRecordResponse result = borrowingService.borrowBook(borrowRequest);

    assertThat(result).isNotNull();
    assertThat(result.bookId()).isEqualTo(1L);
    assertThat(result.borrowerName()).isEqualTo("John Doe");
    assertThat(result.status()).isEqualTo(BorrowingStatus.BORROWED);
    verify(book).borrow();
    verify(bookRepository).save(book);
    verify(borrowingRecordRepository).save(borrowingRecord);
  }

  @Test
  void shouldThrowExceptionWhenBorrowingUnavailableBook() {
    // Test: Borrow unavailable book throws exception
    book.setStatus(BookStatus.BORROWED);
    when(bookValidationService.validateAndGetBook(1L)).thenReturn(book);
    doThrow(new BusinessException("Book with ID 1 is not available for borrowing"))
        .when(bookStatusValidator)
        .validateBookCanBeBorrowed(book);

    assertThatThrownBy(() -> borrowingService.borrowBook(borrowRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ID 1 is not available for borrowing");
  }

  @Test
  void shouldThrowExceptionWhenBookNotFoundForBorrowing() {
    // Test: Borrow non-existing book throws exception
    when(bookValidationService.validateAndGetBook(999L))
        .thenThrow(new BusinessException("Book with ID 999 does not exist"));

    BorrowRequest invalidRequest = new BorrowRequest(999L, "John Doe");

    assertThatThrownBy(() -> borrowingService.borrowBook(invalidRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ID 999 does not exist");
  }

  @Test
  void shouldReturnBookSuccessfully() {
    // Test: Return borrowed book successfully
    book.setStatus(BookStatus.BORROWED);
    borrowingRecord.setStatus(BorrowingStatus.BORROWED);

    BorrowingRecord returnedRecord =
        BorrowingRecord.builder()
            .id(1L)
            .book(book)
            .borrowerName("John Doe")
            .borrowedDate(LocalDate.now().minusDays(5))
            .returnDate(LocalDate.now())
            .status(BorrowingStatus.RETURNED)
            .build();

    BorrowingRecordResponse returnedResponse =
        new BorrowingRecordResponse(
            1L,
            1L,
            "Clean Code",
            "John Doe",
            LocalDate.now().minusDays(5),
            LocalDate.now(),
            BorrowingStatus.RETURNED,
            null,
            null);

    when(bookValidationService.validateAndGetBook(1L)).thenReturn(book);
    doNothing().when(bookStatusValidator).validateBookCanBeReturned(book);
    when(borrowingValidator.validateAndGetActiveBorrowing(1L)).thenReturn(borrowingRecord);
    when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(returnedRecord);
    when(bookRepository.save(any(Book.class))).thenReturn(book);
    when(borrowingRecordMapper.toResponse(any(BorrowingRecord.class))).thenReturn(returnedResponse);

    BorrowingRecordResponse result = borrowingService.returnBook(returnRequest);

    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(BorrowingStatus.RETURNED);
    assertThat(result.returnDate()).isNotNull();
    verify(book).returnBook();
    verify(bookRepository).save(book);
    verify(borrowingRecordRepository).save(any(BorrowingRecord.class));
  }

  @Test
  void shouldThrowExceptionWhenReturningNotBorrowedBook() {
    // Test: Return book that is not borrowed throws exception
    book.setStatus(BookStatus.AVAILABLE);
    when(bookValidationService.validateAndGetBook(1L)).thenReturn(book);
    doThrow(new BusinessException("Book with ID 1 is not currently borrowed"))
        .when(bookStatusValidator)
        .validateBookCanBeReturned(book);

    assertThatThrownBy(() -> borrowingService.returnBook(returnRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ID 1 is not currently borrowed");
  }

  @Test
  void shouldThrowExceptionWhenNoActiveBorrowingRecordForReturn() {
    // Test: Return book with no active borrowing record throws exception
    book.setStatus(BookStatus.BORROWED);
    when(bookValidationService.validateAndGetBook(1L)).thenReturn(book);
    doNothing().when(bookStatusValidator).validateBookCanBeReturned(book);
    when(borrowingValidator.validateAndGetActiveBorrowing(1L))
        .thenThrow(new BusinessException("No active borrowing record found for book ID: 1"));

    assertThatThrownBy(() -> borrowingService.returnBook(returnRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("No active borrowing record found for book ID: 1");
  }

  @Test
  void shouldGetBorrowingHistoryByBook() {
    // Test: Get borrowing history for a book
    List<BorrowingRecord> records = List.of(borrowingRecord);
    List<BorrowingRecordResponse> responses = List.of(borrowingResponse);

    when(bookRepository.existsByIdAndIsDeletedFalse(1L)).thenReturn(true);
    when(borrowingRecordRepository.findAllByBookIdOrderByBorrowedDateDesc(1L)).thenReturn(records);
    when(borrowingRecordMapper.toResponseList(records)).thenReturn(responses);

    List<BorrowingRecordResponse> result = borrowingService.getBorrowingHistoryByBook(1L);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).bookId()).isEqualTo(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingHistoryForNonExistingBook() {
    // Test: Get borrowing history for non-existing book throws exception
    when(bookRepository.existsByIdAndIsDeletedFalse(999L)).thenReturn(false);

    assertThatThrownBy(() -> borrowingService.getBorrowingHistoryByBook(999L))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Book with ID 999 does not exist");
  }

  @Test
  void shouldGetBorrowingHistoryByBorrower() {
    // Test: Get borrowing history by borrower name
    List<BorrowingRecord> records = List.of(borrowingRecord);
    List<BorrowingRecordResponse> responses = List.of(borrowingResponse);

    when(borrowingRecordRepository.findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
            "John"))
        .thenReturn(records);
    when(borrowingRecordMapper.toResponseList(records)).thenReturn(responses);

    List<BorrowingRecordResponse> result = borrowingService.getBorrowingHistoryByBorrower("John");

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).borrowerName()).isEqualTo("John Doe");
  }

  @Test
  void shouldReturnEmptyListWhenBorrowerHasNoHistory() {
    // Test: Get borrowing history for borrower with no records returns empty list
    when(borrowingRecordRepository.findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
            "Unknown"))
        .thenReturn(List.of());
    when(borrowingRecordMapper.toResponseList(anyList())).thenReturn(List.of());

    List<BorrowingRecordResponse> result =
        borrowingService.getBorrowingHistoryByBorrower("Unknown");

    assertThat(result).isNotNull();
    assertThat(result).isEmpty();
  }
}
