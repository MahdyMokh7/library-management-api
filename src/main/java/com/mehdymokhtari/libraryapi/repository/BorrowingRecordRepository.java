package com.mehdymokhtari.libraryapi.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;

@Repository
public interface BorrowingRecordRepository extends JpaRepository<BorrowingRecord, Long> {

  @Query("SELECT br FROM BorrowingRecord br WHERE br.item.id = :itemId AND br.status = :status")
  Optional<BorrowingRecord> findByItemIdAndStatus(
      @Param("itemId") Long itemId, @Param("status") BorrowingStatus status);

  List<BorrowingRecord> findByItemIdAndStatusOrderByBorrowedDateDesc(
      Long itemId, BorrowingStatus status);

  List<BorrowingRecord> findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
      String borrowerName);

  boolean existsByItemIdAndStatus(Long itemId, BorrowingStatus status);

  @Query(
      "SELECT COUNT(br) > 0 FROM BorrowingRecord br WHERE br.item.id = :itemId AND br.status = 'BORROWED'")
  boolean isItemCurrentlyBorrowed(@Param("itemId") Long itemId);

  @Query(
      "SELECT br FROM BorrowingRecord br WHERE br.item.id = :itemId ORDER BY br.borrowedDate DESC")
  List<BorrowingRecord> findAllByItemIdOrderByBorrowedDateDesc(@Param("itemId") Long itemId);
}
