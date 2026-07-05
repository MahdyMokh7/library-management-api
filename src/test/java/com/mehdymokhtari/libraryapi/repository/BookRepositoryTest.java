package com.mehdymokhtari.libraryapi.repository;

import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.repository.spec.BookSpecification;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
class BookRepositoryTest {

    @Autowired
    private BookRepository bookRepository;

    private Book book1;
    private Book book2;
    private Book book3;

    @BeforeEach
    void setUp() {
        book1 = Book.builder()
                .title("Clean Code")
                .author("Robert Martin")
                .isbn("9780132350884")
                .publicationYear(2008)
                .status(BookStatus.AVAILABLE)
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

        book3 = Book.builder()
                .title("Spring in Action")
                .author("Craig Walls")
                .isbn("9781617294945")
                .publicationYear(2018)
                .status(BookStatus.BORROWED)
                .isDeleted(false)
                .build();

        bookRepository.saveAll(List.of(book1, book2, book3));
    }

    @Test
    void shouldSaveBookSuccessfully() {
        // Test: Save book and verify it has generated ID
        Book newBook = Book.builder()
                .title("Test Book")
                .author("Test Author")
                .isbn("9781234567897")
                .publicationYear(2020)
                .status(BookStatus.AVAILABLE)
                .isDeleted(false)
                .build();

        Book saved = bookRepository.save(newBook);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getTitle()).isEqualTo("Test Book");
        assertThat(saved.getAuthor()).isEqualTo("Test Author");
        assertThat(saved.getIsbn()).isEqualTo("9781234567897");
        assertThat(saved.getStatus()).isEqualTo(BookStatus.AVAILABLE);
        assertThat(saved.isDeleted()).isFalse();
    }

    @Test
    void shouldFindBookByIdAndIsDeletedFalse() {
        // Test: Find existing book by ID that is not deleted
        Optional<Book> found = bookRepository.findByIdAndIsDeletedFalse(book1.getId());

        assertThat(found).isPresent();
        assertThat(found.get().getTitle()).isEqualTo("Clean Code");
        assertThat(found.get().getIsbn()).isEqualTo("9780132350884");
    }

    @Test
    void shouldReturnEmptyWhenBookNotFoundOrDeleted() {
        // Test: Find non-existing book returns empty Optional
        Optional<Book> found = bookRepository.findByIdAndIsDeletedFalse(999L);

        assertThat(found).isEmpty();
    }

    @Test
    void shouldCheckIfBookExistsByIsbn() {
        // Test: Check if book exists by ISBN
        boolean exists = bookRepository.existsByIsbn("9780132350884");

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseForNonExistingIsbn() {
        // Test: Check if book exists with non-existing ISBN returns false
        boolean exists = bookRepository.existsByIsbn("9999999999999");

        assertThat(exists).isFalse();
    }

    @Test
    void shouldCheckIfBookExistsByIdAndNotDeleted() {
        // Test: Check if book exists by ID and is not deleted
        boolean exists = bookRepository.existsByIdAndIsDeletedFalse(book1.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenBookDeletedOrNotFound() {
        // Test: Check if deleted or non-existing book exists returns false
        book1.setDeleted(true);
        bookRepository.save(book1);

        boolean exists = bookRepository.existsByIdAndIsDeletedFalse(book1.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldCheckIfBookHasSpecificStatus() {
        // Test: Check if book has specific status (AVAILABLE/BORROWED)
        boolean isAvailable = bookRepository.isBookWithStatus(book1.getId(), BookStatus.AVAILABLE);
        boolean isBorrowed = bookRepository.isBookWithStatus(book3.getId(), BookStatus.BORROWED);

        assertThat(isAvailable).isTrue();
        assertThat(isBorrowed).isTrue();
    }

    @Test
    void shouldReturnFalseWhenBookDoesNotHaveSpecificStatus() {
        // Test: Check if book has wrong status returns false
        boolean isAvailable = bookRepository.isBookWithStatus(book3.getId(), BookStatus.AVAILABLE);

        assertThat(isAvailable).isFalse();
    }

    @Test
    void shouldFindAllBooksWithPagination() {
        // Test: Find all books with pagination
        Pageable pageable = PageRequest.of(0, 10);
        Page<Book> page = bookRepository.findAll(pageable);

        assertThat(page.getContent()).hasSize(3);
        assertThat(page.getTotalElements()).isEqualTo(3);
        assertThat(page.getNumber()).isEqualTo(0);
        assertThat(page.getSize()).isEqualTo(10);
    }

    @Test
    void shouldFindBooksBySpecificationWithTitleFilter() {
        // Test: Find books by title using Specification
        Specification<Book> spec = BookSpecification.hasTitle("Clean");
        List<Book> books = bookRepository.findAll(spec);

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Clean Code");
    }

    @Test
    void shouldFindBooksBySpecificationWithAuthorFilter() {
        // Test: Find books by author using Specification
        Specification<Book> spec = BookSpecification.hasAuthor("Joshua");
        List<Book> books = bookRepository.findAll(spec);

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getAuthor()).isEqualTo("Joshua Bloch");
    }

    @Test
    void shouldFindBooksBySpecificationWithMultipleFilters() {
        // Test: Find books with multiple filters (year + status)
        Specification<Book> spec = Specification
                .where(BookSpecification.hasPublicationYear(2018))
                .and(BookSpecification.hasStatus(BookStatus.AVAILABLE));

        List<Book> books = bookRepository.findAll(spec);

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void shouldFindBooksBySpecificationWithStatusFilter() {
        // Test: Find books by status using Specification
        Specification<Book> spec = BookSpecification.hasStatus(BookStatus.BORROWED);
        List<Book> books = bookRepository.findAll(spec);

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getStatus()).isEqualTo(BookStatus.BORROWED);
    }

    @Test
    void shouldFilterByTitleAndAuthorCombined() {
        // Test: Find books by title and author combined
        Specification<Book> spec = Specification
                .where(BookSpecification.hasTitle("Effective"))
                .and(BookSpecification.hasAuthor("Joshua"));

        List<Book> books = bookRepository.findAll(spec);

        assertThat(books).hasSize(1);
        assertThat(books.get(0).getTitle()).isEqualTo("Effective Java");
    }

    @Test
    void shouldCheckIfIsbnExistsForDifferentBook() {
        // Test: Check if ISBN exists for a different book (for update validation)
        boolean exists = bookRepository.existsByIsbnAndIdNot("9780132350884", book2.getId());

        assertThat(exists).isTrue();
    }

    @Test
    void shouldReturnFalseWhenIsbnNotExistsForDifferentBook() {
        // Test: Check if non-existing ISBN exists for a different book returns false
        boolean exists = bookRepository.existsByIsbnAndIdNot("9999999999999", book2.getId());

        assertThat(exists).isFalse();
    }

    @Test
    void shouldReturnBooksWithIsDeletedFalseOnly() {
        // Test: Find only books that are not deleted
        book1.setDeleted(true);
        bookRepository.save(book1);

        List<Book> books = bookRepository.findAll();

        assertThat(books).hasSize(3);
        assertThat(books.stream().filter(Book::isDeleted)).hasSize(1);
        assertThat(books.stream().filter(b -> !b.isDeleted())).hasSize(2);
    }
}