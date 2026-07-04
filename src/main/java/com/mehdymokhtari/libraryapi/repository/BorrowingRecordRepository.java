package com.mehdymokhtari.libraryapi.repository;

import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

// Spring Creates the Implementation at Runtime
@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

    @Query("SELECT br FROM BorrowingRecord br WHERE br.book.id = :bookId AND br.status = :status")
    Optional<BorrowingRecord> findByBookIdAndStatus(@Param("bookId") Long bookId, @Param("status") BorrowingStatus status);

    List<BorrowingRecord> findByBookIdAndStatusOrderByBorrowedDateDesc(Long bookId, BorrowingStatus status);

    List<BorrowingRecord> findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(String borrowerName);

    boolean existsByBookIdAndStatus(Long bookId, BorrowingStatus status);

    @Query("SELECT COUNT(br) > 0 FROM BorrowingRecord br WHERE br.book.id = :bookId AND br.status = 'BORROWED'")
    boolean isBookCurrentlyBorrowed(@Param("bookId") Long bookId);

    @Query("SELECT br FROM BorrowingRecord br WHERE br.book.id = :bookId ORDER BY br.borrowedDate DESC")
    List<BorrowingRecord> findAllByBookIdOrderByBorrowedDateDesc(@Param("bookId") Long bookId);
}