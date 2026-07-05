package com.mehdymokhtari.libraryapi.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException;
import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import com.mehdymokhtari.libraryapi.model.mapper.BorrowingRecordMapper;
import com.mehdymokhtari.libraryapi.repository.BorrowingRecordRepository;
import com.mehdymokhtari.libraryapi.repository.LibraryItemRepository;
import com.mehdymokhtari.libraryapi.service.impl.BorrowingServiceImpl;
import com.mehdymokhtari.libraryapi.service.validation.BorrowingValidator;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
class BorrowingServiceTest {

  @Mock private BorrowingRecordRepository borrowingRecordRepository;

  @Mock private LibraryItemRepository libraryItemRepository;

  @Mock private BorrowingRecordMapper borrowingRecordMapper;

  @Mock private BorrowingValidator borrowingValidator;

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
            .deleted(false)
            .build();

    borrowingRecord =
        BorrowingRecord.builder()
            .id(1L)
            .item(book)
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
  void shouldBorrowItemSuccessfully() {
    // Test: Borrow available item successfully
    when(libraryItemRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(book));
    when(borrowingRecordMapper.toEntity(borrowRequest, book)).thenReturn(borrowingRecord);
    when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(borrowingRecord);
    when(borrowingRecordMapper.toResponse(any(BorrowingRecord.class)))
        .thenReturn(borrowingResponse);

    BorrowingRecordResponse result = borrowingService.borrowItem(borrowRequest);

    assertThat(result).isNotNull();
    assertThat(result.itemId()).isEqualTo(1L);
    assertThat(result.borrowerName()).isEqualTo("John Doe");
    assertThat(result.status()).isEqualTo(BorrowingStatus.BORROWED);

    verify(libraryItemRepository).save(book);
    verify(borrowingRecordRepository).save(borrowingRecord);
  }

  @Test
  void shouldThrowExceptionWhenBorrowingUnavailableItem() {
    // Test: Borrow unavailable item throws exception
    book.setStatus(BookStatus.BORROWED);
    when(libraryItemRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(book));

    assertThatThrownBy(() -> borrowingService.borrowItem(borrowRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Item with ID 1 is not available for borrowing");
  }

  @Test
  void shouldThrowExceptionWhenItemNotFoundForBorrowing() {
    // Test: Borrow non-existing item throws exception
    when(libraryItemRepository.findByIdAndDeletedFalse(999L)).thenReturn(Optional.empty());

    BorrowRequest invalidRequest = new BorrowRequest(999L, "John Doe");

    assertThatThrownBy(() -> borrowingService.borrowItem(invalidRequest))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("LibraryItem with ID 999 not found");
  }

  @Test
  void shouldReturnItemSuccessfully() {
    // Test: Return borrowed item successfully
    book.setStatus(BookStatus.BORROWED);
    borrowingRecord.setStatus(BorrowingStatus.BORROWED);

    BorrowingRecord returnedRecord =
        BorrowingRecord.builder()
            .id(1L)
            .item(book)
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

    when(libraryItemRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(book));
    // CRITICAL: Mock the REPOSITORY, not the validator
    when(borrowingRecordRepository.findByItemIdAndStatus(1L, BorrowingStatus.BORROWED))
        .thenReturn(Optional.of(borrowingRecord));
    when(borrowingRecordRepository.save(any(BorrowingRecord.class))).thenReturn(returnedRecord);
    when(borrowingRecordMapper.toResponse(any(BorrowingRecord.class))).thenReturn(returnedResponse);

    BorrowingRecordResponse result = borrowingService.returnItem(returnRequest);

    assertThat(result).isNotNull();
    assertThat(result.status()).isEqualTo(BorrowingStatus.RETURNED);
    assertThat(result.returnDate()).isNotNull();

    verify(libraryItemRepository).save(book);
    verify(borrowingRecordRepository).save(any(BorrowingRecord.class));
  }

  @Test
  void shouldThrowExceptionWhenReturningNotBorrowedItem() {
    // Test: Return item that is not borrowed throws exception
    book.setStatus(BookStatus.AVAILABLE);
    when(libraryItemRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(book));

    assertThatThrownBy(() -> borrowingService.returnItem(returnRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("Item with ID 1 is not currently borrowed");
  }

  @Test
  void shouldThrowExceptionWhenNoActiveBorrowingRecordForReturn() {
    // Test: Return item with no active borrowing record throws exception
    book.setStatus(BookStatus.BORROWED);
    when(libraryItemRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(book));
    // Mock the repository to return empty (no active record)
    when(borrowingRecordRepository.findByItemIdAndStatus(1L, BorrowingStatus.BORROWED))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> borrowingService.returnItem(returnRequest))
        .isInstanceOf(BusinessException.class)
        .hasMessage("No active borrowing record found for item ID: 1");
  }

  @Test
  void shouldGetBorrowingHistoryByItem() {
    // Test: Get borrowing history for an item
    List<BorrowingRecord> records = List.of(borrowingRecord);
    List<BorrowingRecordResponse> responses = List.of(borrowingResponse);

    when(libraryItemRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);
    when(borrowingRecordRepository.findAllByItemIdOrderByBorrowedDateDesc(1L)).thenReturn(records);
    when(borrowingRecordMapper.toResponseList(records)).thenReturn(responses);

    List<BorrowingRecordResponse> result = borrowingService.getBorrowingHistoryByItem(1L);

    assertThat(result).isNotNull();
    assertThat(result).hasSize(1);
    assertThat(result.get(0).itemId()).isEqualTo(1L);
  }

  @Test
  void shouldThrowExceptionWhenGettingHistoryForNonExistingItem() {
    // Test: Get borrowing history for non-existing item throws exception
    when(libraryItemRepository.existsByIdAndDeletedFalse(999L)).thenReturn(false);

    assertThatThrownBy(() -> borrowingService.getBorrowingHistoryByItem(999L))
        .isInstanceOf(ResourceNotFoundException.class)
        .hasMessage("LibraryItem with ID 999 not found");
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
