package com.mehdymokhtari.libraryapi.repository;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;

import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;

@DataJpaTest
@ActiveProfiles("test")
class BorrowingRecordRepositoryTest {

  @Autowired private BorrowingRecordRepository borrowingRecordRepository;

  @Autowired private BookRepository bookRepository;

  private Book book1;
  private Book book2;
  private BorrowingRecord record1;
  private BorrowingRecord record2;
  private BorrowingRecord record3;

  @BeforeEach
  void setUp() {
    book1 =
        Book.builder()
            .title("Clean Code")
            .author("Robert Martin")
            .isbn("9780132350884")
            .publicationYear(2008)
            .status(BookStatus.BORROWED)
            .deleted(false)
            .edition(1)
            .publisher("Prentice Hall")
            .build();

    book2 =
        Book.builder()
            .title("Effective Java")
            .author("Joshua Bloch")
            .isbn("9780134685991")
            .publicationYear(2018)
            .status(BookStatus.AVAILABLE)
            .deleted(false)
            .edition(4)
            .publisher("Addison-Wesley")
            .build();

    bookRepository.saveAll(List.of(book1, book2));

    record1 =
        BorrowingRecord.builder()
            .item(book1)
            .borrowerName("John Doe")
            .borrowedDate(LocalDate.now().minusDays(10))
            .status(BorrowingStatus.BORROWED)
            .build();

    record2 =
        BorrowingRecord.builder()
            .item(book1)
            .borrowerName("John Doe")
            .borrowedDate(LocalDate.now().minusDays(30))
            .returnDate(LocalDate.now().minusDays(20))
            .status(BorrowingStatus.RETURNED)
            .build();

    record3 =
        BorrowingRecord.builder()
            .item(book2)
            .borrowerName("Jane Smith")
            .borrowedDate(LocalDate.now().minusDays(5))
            .returnDate(LocalDate.now())
            .status(BorrowingStatus.RETURNED)
            .build();

    borrowingRecordRepository.saveAll(List.of(record1, record2, record3));
  }

  @Test
  void shouldSaveBorrowingRecordSuccessfully() {
    // Test: Save borrowing record and verify it has generated ID
    BorrowingRecord newRecord =
        BorrowingRecord.builder()
            .item(book2)
            .borrowerName("New User")
            .borrowedDate(LocalDate.now())
            .status(BorrowingStatus.BORROWED)
            .build();

    BorrowingRecord saved = borrowingRecordRepository.save(newRecord);

    assertThat(saved.getId()).isNotNull();
    assertThat(saved.getBorrowerName()).isEqualTo("New User");
    assertThat(saved.getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    assertThat(saved.getBorrowedDate()).isEqualTo(LocalDate.now());
  }

  @Test
  void shouldFindByItemIdAndStatus() {
    // Test: Find active borrowing record by item ID and status
    Optional<BorrowingRecord> found =
        borrowingRecordRepository.findByItemIdAndStatus(book1.getId(), BorrowingStatus.BORROWED);

    assertThat(found).isPresent();
    assertThat(found.get().getItem().getId()).isEqualTo(book1.getId());
    assertThat(found.get().getBorrowerName()).isEqualTo("John Doe");
    assertThat(found.get().getStatus()).isEqualTo(BorrowingStatus.BORROWED);
  }

  @Test
  void shouldReturnEmptyWhenNoActiveBorrowingRecord() {
    // Test: Find by item ID and status returns empty when no matching record
    Optional<BorrowingRecord> found =
        borrowingRecordRepository.findByItemIdAndStatus(book2.getId(), BorrowingStatus.BORROWED);

    assertThat(found).isEmpty();
  }

  @Test
  void shouldFindAllByItemIdOrderedByBorrowedDateDesc() {
    // Test: Get all borrowing records for an item ordered by borrowed date descending
    List<BorrowingRecord> records =
        borrowingRecordRepository.findAllByItemIdOrderByBorrowedDateDesc(book1.getId());

    assertThat(records).hasSize(2);
    assertThat(records.get(0).getBorrowedDate()).isAfterOrEqualTo(records.get(1).getBorrowedDate());
    assertThat(records.get(0).getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    assertThat(records.get(1).getStatus()).isEqualTo(BorrowingStatus.RETURNED);
  }

  @Test
  void shouldReturnEmptyListWhenNoRecordsForItem() {
    // Test: Get borrowing records for item with no records returns empty list
    Book newBook =
        Book.builder()
            .title("New Book")
            .author("New Author")
            .isbn("9781234567897")
            .publicationYear(2020)
            .status(BookStatus.AVAILABLE)
            .deleted(false)
            .edition(1)
            .publisher("Test Publisher")
            .build();

    bookRepository.save(newBook);
    List<BorrowingRecord> records =
        borrowingRecordRepository.findAllByItemIdOrderByBorrowedDateDesc(newBook.getId());

    assertThat(records).isEmpty();
  }

  @Test
  void shouldFindByItemIdAndStatusWithMultipleStatuses() {
    // Test: Find by item ID and different statuses
    Optional<BorrowingRecord> active =
        borrowingRecordRepository.findByItemIdAndStatus(book1.getId(), BorrowingStatus.BORROWED);

    Optional<BorrowingRecord> returned =
        borrowingRecordRepository.findByItemIdAndStatus(book1.getId(), BorrowingStatus.RETURNED);

    assertThat(active).isPresent();
    assertThat(active.get().getStatus()).isEqualTo(BorrowingStatus.BORROWED);

    assertThat(returned).isPresent();
    assertThat(returned.get().getStatus()).isEqualTo(BorrowingStatus.RETURNED);
  }

  @Test
  void shouldCheckIfItemIsCurrentlyBorrowed() {
    // Test: Check if item is currently borrowed using custom query
    boolean isBorrowed = borrowingRecordRepository.isItemCurrentlyBorrowed(book1.getId());
    boolean isNotBorrowed = borrowingRecordRepository.isItemCurrentlyBorrowed(book2.getId());

    assertThat(isBorrowed).isTrue();
    assertThat(isNotBorrowed).isFalse();
  }

  @Test
  void shouldFindAllBorrowingRecordsForBorrower() {
    // Test: Find all borrowing records for a borrower by name
    List<BorrowingRecord> records =
        borrowingRecordRepository.findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
            "John");

    assertThat(records).hasSize(2);
    assertThat(records).allMatch(r -> r.getBorrowerName().contains("John"));
    assertThat(records.get(0).getBorrowedDate()).isAfterOrEqualTo(records.get(1).getBorrowedDate());
  }

  @Test
  void shouldReturnEmptyListWhenBorrowerNotFound() {
    // Test: Find records for non-existing borrower returns empty list
    List<BorrowingRecord> records =
        borrowingRecordRepository.findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
            "Unknown");

    assertThat(records).isEmpty();
  }

  @Test
  void shouldFindBorrowingRecordsCaseInsensitiveByBorrowerName() {
    // Test: Find by borrower name case-insensitive
    List<BorrowingRecord> recordsLower =
        borrowingRecordRepository.findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
            "john");

    List<BorrowingRecord> recordsUpper =
        borrowingRecordRepository.findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
            "JOHN");

    assertThat(recordsLower).hasSize(2);
    assertThat(recordsUpper).hasSize(2);
    assertThat(recordsLower).containsExactlyInAnyOrderElementsOf(recordsUpper);
  }

  @Test
  void shouldCheckIfBorrowingRecordExistsByItemIdAndStatus() {
    // Test: Check if borrowing record exists by item ID and status
    boolean existsActive =
        borrowingRecordRepository.existsByItemIdAndStatus(book1.getId(), BorrowingStatus.BORROWED);

    boolean existsReturned =
        borrowingRecordRepository.existsByItemIdAndStatus(book1.getId(), BorrowingStatus.RETURNED);

    boolean existsForBook2 =
        borrowingRecordRepository.existsByItemIdAndStatus(book2.getId(), BorrowingStatus.RETURNED);

    assertThat(existsActive).isTrue();
    assertThat(existsReturned).isTrue();
    assertThat(existsForBook2).isTrue();
  }

  @Test
  void shouldReturnFalseWhenNoBorrowingRecordExists() {
    // Test: Check if borrowing record exists with wrong status returns false
    boolean exists =
        borrowingRecordRepository.existsByItemIdAndStatus(book2.getId(), BorrowingStatus.BORROWED);

    assertThat(exists).isFalse();
  }

  @Test
  void shouldFindAllBorrowingRecordsForItemInOrder() {
    // Test: Find all records for an item sorted by borrowed date descending
    Book newBook =
        Book.builder()
            .title("Spring in Action")
            .author("Craig Walls")
            .isbn("9781617294945")
            .publicationYear(2018)
            .status(BookStatus.AVAILABLE)
            .deleted(false)
            .edition(6)
            .publisher("Manning")
            .build();

    bookRepository.save(newBook);

    BorrowingRecord recent =
        BorrowingRecord.builder()
            .item(newBook)
            .borrowerName("User 1")
            .borrowedDate(LocalDate.now())
            .status(BorrowingStatus.BORROWED)
            .build();

    BorrowingRecord older =
        BorrowingRecord.builder()
            .item(newBook)
            .borrowerName("User 2")
            .borrowedDate(LocalDate.now().minusDays(15))
            .returnDate(LocalDate.now().minusDays(5))
            .status(BorrowingStatus.RETURNED)
            .build();

    borrowingRecordRepository.saveAll(List.of(recent, older));

    List<BorrowingRecord> records =
        borrowingRecordRepository.findAllByItemIdOrderByBorrowedDateDesc(newBook.getId());

    assertThat(records).hasSize(2);
    assertThat(records.get(0).getBorrowedDate()).isAfter(records.get(1).getBorrowedDate());
    assertThat(records.get(0).getStatus()).isEqualTo(BorrowingStatus.BORROWED);
  }

  @Test
  void shouldSaveAndUpdateBorrowingRecordWithReturnDate() {
    // Test: Save borrowing record and update with return date
    BorrowingRecord newRecord =
        BorrowingRecord.builder()
            .item(book1)
            .borrowerName("Test User")
            .borrowedDate(LocalDate.now().minusDays(3))
            .status(BorrowingStatus.BORROWED)
            .build();

    BorrowingRecord saved = borrowingRecordRepository.save(newRecord);
    saved.markReturned();
    BorrowingRecord updated = borrowingRecordRepository.save(saved);

    assertThat(updated.getReturnDate()).isNotNull();
    assertThat(updated.getStatus()).isEqualTo(BorrowingStatus.RETURNED);
  }
}
