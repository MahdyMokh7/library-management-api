package com.mehdymokhtari.libraryapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

// Spring Creates the Implementation at Runtime
@Repository
public interface BookRepository extends JpaRepository<Book, Long>, JpaSpecificationExecutor<Book> {

  boolean existsByIsbn(String isbn);

  boolean existsByIdAndIsDeletedFalse(Long id);

  boolean existsByIdAndStatusAndIsDeletedFalse(Long id, BookStatus status);

  Optional<Book> findByIdAndIsDeletedFalse(Long id);

  @Query(
      "SELECT COUNT(b) > 0 FROM Book b WHERE b.id = :id AND b.status = :status AND b.isDeleted = false")
  boolean isBookWithStatus(@Param("id") Long id, @Param("status") BookStatus status);

  @Query(
      "SELECT COUNT(b) > 0 FROM Book b WHERE b.isbn = :isbn AND b.id != :id AND b.isDeleted = false")
  boolean existsByIsbnAndIdNot(@Param("isbn") String isbn, @Param("id") Long id);
}
