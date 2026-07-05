package com.mehdymokhtari.libraryapi.repository;

import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BorrowingRecordRepositoryTest {

    @Autowired
    private BorrowingRecordRepository borrowingRecordRepository;

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;
    private BorrowingRecord record1;
    private BorrowingRecord record2;
    private BorrowingRecord record3;

    @BeforeEach
    void setUp() {
        book1 = Book.builder()
                .title("Clean Code")
                .author("Robert Martin")
                .isbn("9780132350884")
                .publicationYear(2008)
                .status(BookStatus.BORROWED)
                .isDeleted(false)
                .build();

        book2 = Book.builder()
                .title("Effective Java")
                .author("Joshua Bloch")
                .isbn("9780134685991")
                .publicationYear(2018)
                .status(BookStatus.AVAILABLE)
                .isDeleted(false)
                .build();

        bookRepository.saveAll(List.of(book1, book2));

        record1 = BorrowingRecord.builder()
                .book(book1)
                .borrowerName("John Doe")
                .borrowedDate(LocalDate.now().minusDays(10))
                .status(BorrowingStatus.BORROWED)
                .build();

        record2 = BorrowingRecord.builder()
                .book(book1)
                .borrowerName("John Doe")
                .borrowedDate(LocalDate.now().minusDays(30))
                .returnDate(LocalDate.now().minusDays(20))
                .status(BorrowingStatus.RETURNED)
                .build();

        record3 = BorrowingRecord.builder()
                .book(book2)
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
        BorrowingRecord newRecord = BorrowingRecord.builder()
                .book(book2)
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
    void shouldFindByBookIdAndStatus() {
        // Test: Find active borrowing record by book ID and status
        Optional<BorrowingRecord> found = borrowingRecordRepository
                .findByBookIdAndStatus(book1.getId(), BorrowingStatus.BORROWED);

        assertThat(found).isPresent();
        assertThat(found.get().getBook().getId()).isEqualTo(book1.getId());
        assertThat(found.get().getBorrowerName()).isEqualTo("John Doe");
        assertThat(found.get().getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    }

    @Test
    void shouldReturnEmptyWhenNoActiveBorrowingRecord() {
        // Test: Find by book ID and status returns empty when no matching record
        Optional<BorrowingRecord> found = borrowingRecordRepository
                .findByBookIdAndStatus(book2.getId(), BorrowingStatus.BORROWED);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldFindAllByBookIdOrderedByBorrowedDateDesc() {
        // Test: Get all borrowing records for a book ordered by borrowed date descending
        List<BorrowingRecord> records = borrowingRecordRepository
                .findAllByBookIdOrderByBorrowedDateDesc(book1.getId());

        assertThat(records).hasSize(2);
        assertThat(records.get(0).getBorrowedDate()).isAfterOrEqualTo(records.get(1).getBorrowedDate());
        assertThat(records.get(0).getStatus()).isEqualTo(BorrowingStatus.BORROWED);
        assertThat(records.get(1).getStatus()).isEqualTo(BorrowingStatus.RETURNED);
    }

    @Test
    void shouldReturnEmptyListWhenNoRecordsForBook() {
        // Test: Get borrowing records for book with no records returns empty list
        Book newBook = Book.builder()
                .title("New Book")
                .author("New Author")
                .isbn("9781234567897")
                .publicationYear(2020)
                .status(BookStatus.AVAILABLE)
                .isDeleted(false)
                .build();

        bookRepository.save(newBook);
        List<BorrowingRecord> records = borrowingRecordRepository
                .findAllByBookIdOrderByBorrowedDateDesc(newBook.getId());

        assertThat(records).isEmpty();
    }

    @Test
    void shouldFindByBookIdAndStatusWithMultipleStatuses() {
        // Test: Find by book ID and different statuses
        Optional<BorrowingRecord> active = borrowingRecordRepository
                .findByBookIdAndStatus(book1.getId(), BorrowingStatus.BORROWED);

        Optional<BorrowingRecord> returned = borrowingRecordRepository
                .findByBookIdAndStatus(book1.getId(), BorrowingStatus.RETURNED);

        assertThat(active).isPresent();
        assertThat(active.get().getStatus()).isEqualTo(BorrowingStatus.BORROWED);

        assertThat(returned).isPresent();
        assertThat(returned.get().getStatus()).isEqualTo(BorrowingStatus.RETURNED);
    }

    @Test
    void shouldCheckIfBookIsCurrentlyBorrowed() {
        // Test: Check if book is currently borrowed using custom query
        boolean isBorrowed = borrowingRecordRepository.isBookCurrentlyBorrowed(book1.getId());
        boolean isNotBorrowed = borrowingRecordRepository.isBookCurrentlyBorrowed(book2.getId());

        assertThat(isBorrowed).isTrue();
        assertThat(isNotBorrowed).isFalse();
    }

    @Test
    void shouldFindAllBorrowingRecordsForBorrower() {
        // Test: Find all borrowing records for a borrower by name
        List<BorrowingRecord> records = borrowingRecordRepository
                .findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc("John");

        assertThat(records).hasSize(2);
        assertThat(records).allMatch(r -> r.getBorrowerName().contains("John"));
        assertThat(records.get(0).getBorrowedDate()).isAfterOrEqualTo(records.get(1).getBorrowedDate());
    }

    @Test
    void shouldReturnEmptyListWhenBorrowerNotFound() {
        // Test: Find records for non-existing borrower returns empty list
        List<BorrowingRecord> records = borrowingRecordRepository
                .findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc("Unknown");

        assertThat(records).isEmpty();
    }

    @Test
    void shouldFindBorrowingRecordsCaseInsensitiveByBorrowerName() {
        // Test: Find by borrower name case-insensitive
        List<BorrowingRecord> recordsLower = borrowingRecordRepository
                .findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc("john");

        List<BorrowingRecord> recordsUpper = borrowingRecordRepository
                .findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc("JOHN");

        assertThat(recordsLower).hasSize(2);
        assertThat(recordsUpper).hasSize(2);
        assertThat(recordsLower).containsExactlyInAnyOrderElementsOf(recordsUpper);
    }

    @Test
    void shouldCheckIfBorrowingRecordExistsByBookIdAndStatus() {
        // Test: Check if borrowing record exists by book ID and status
        boolean existsActive = borrowingRecordRepository
                .existsByBookIdAndStatus(book1.getId(), BorrowingStatus.BORROWED);

        boolean existsReturned = borrowingRecordRepository
                .existsByBookIdAndStatus(book1.getId(), BorrowingStatus.RETURNED);

        boolean existsForBook2 = borrowingRecordRepository
                .existsByBookIdAndStatus(book2.getId(), BorrowingStatus.RETURNED);

        assertThat(existsActive).isTrue();
        assertThat(existsReturned).isTrue();
        assertThat(existsForBook2).isTrue();
    }

    @Test
    void shouldReturnFalseWhenNoBorrowingRecordExists() {
        // Test: Check if borrowing record exists with wrong status returns false
        boolean exists = borrowingRecordRepository
                .existsByBookIdAndStatus(book2.getId(), BorrowingStatus.BORROWED);

        assertThat(exists).isFalse();
    }

    @Test
    void shouldFindAllBorrowingRecordsForBookInOrder() {
        // Test: Find all records for a book sorted by borrowed date descending
        Book newBook = Book.builder()
                .title("Spring in Action")
                .author("Craig Walls")
                .isbn("9781617294945")
                .publicationYear(2018)
                .status(BookStatus.AVAILABLE)
                .isDeleted(false)
                .build();

        bookRepository.save(newBook);

        BorrowingRecord recent = BorrowingRecord.builder()
                .book(newBook)
                .borrowerName("User 1")
                .borrowedDate(LocalDate.now())
                .status(BorrowingStatus.BORROWED)
                .build();

        BorrowingRecord older = BorrowingRecord.builder()
                .book(newBook)
                .borrowerName("User 2")
                .borrowedDate(LocalDate.now().minusDays(15))
                .returnDate(LocalDate.now().minusDays(5))
                .status(BorrowingStatus.RETURNED)
                .build();

        borrowingRecordRepository.saveAll(List.of(recent, older));

        List<BorrowingRecord> records = borrowingRecordRepository
                .findAllByBookIdOrderByBorrowedDateDesc(newBook.getId());

        assertThat(records).hasSize(2);
        assertThat(records.get(0).getBorrowedDate()).isAfter(records.get(1).getBorrowedDate());
        assertThat(records.get(0).getStatus()).isEqualTo(BorrowingStatus.BORROWED);
    }

    @Test
    void shouldSaveAndUpdateBorrowingRecordWithReturnDate() {
        // Test: Save borrowing record and update with return date
        BorrowingRecord newRecord = BorrowingRecord.builder()
                .book(book1)
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