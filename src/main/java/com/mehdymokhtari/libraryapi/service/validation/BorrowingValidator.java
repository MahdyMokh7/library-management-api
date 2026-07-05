package com.mehdymokhtari.libraryapi.service.validation;

import org.springframework.stereotype.Service;

import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import com.mehdymokhtari.libraryapi.repository.BorrowingRecordRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BorrowingValidator {

  private final BorrowingRecordRepository borrowingRecordRepository;

  public void validateBorrowerName(String borrowerName) {
    if (borrowerName == null || borrowerName.trim().isEmpty()) {
      throw new BusinessException("Borrower name is required");
    }
    if (borrowerName.length() > 100) {
      throw new BusinessException("Borrower name must not exceed 100 characters");
    }
  }

  public void validateBookAvailable(Book book) {
    if (!book.isAvailable()) {
      throw new BusinessException(
          "Book with ID " + book.getId() + " is not available for borrowing");
    }
  }

  public void validateBookBorrowed(Book book) {
    if (!book.isBorrowed()) {
      throw new BusinessException("Book with ID " + book.getId() + " is not currently borrowed");
    }
  }

  public BorrowingRecord validateAndGetActiveBorrowing(Long bookId) {
    return borrowingRecordRepository
        .findByBookIdAndStatus(bookId, BorrowingStatus.BORROWED)
        .orElseThrow(
            () -> new BusinessException("No active borrowing record found for book ID: " + bookId));
  }

  public void validateBookNotAlreadyBorrowed(Long bookId) {
    if (borrowingRecordRepository.isBookCurrentlyBorrowed(bookId)) {
      throw new BusinessException("Book with ID " + bookId + " is already borrowed");
    }
  }
}
