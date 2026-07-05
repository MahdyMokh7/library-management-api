package com.mehdymokhtari.libraryapi.service.validation;

import org.springframework.stereotype.Service;

import com.mehdymokhtari.libraryapi.exception.BookNotAvailableException;
import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.exception.InvalidOperationException;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;
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

  public void validateItemAvailable(LibraryItem item) {
    if (!item.isAvailable()) {
      throw new BookNotAvailableException(
          "Item with ID " + item.getId() + " is not available for borrowing");
    }
  }

  public void validateItemBorrowed(LibraryItem item) {
    if (!item.isBorrowed()) {
      throw new InvalidOperationException(
          "Item with ID " + item.getId() + " is not currently borrowed");
    }
  }

  public BorrowingRecord validateAndGetActiveBorrowing(Long itemId) {
    return borrowingRecordRepository
        .findByItemIdAndStatus(itemId, BorrowingStatus.BORROWED)
        .orElseThrow(
            () -> new BusinessException("No active borrowing record found for item ID: " + itemId));
  }

  public void validateItemNotAlreadyBorrowed(Long itemId) {
    if (borrowingRecordRepository.isItemCurrentlyBorrowed(itemId)) {
      throw new BookNotAvailableException("Item with ID " + itemId + " is already borrowed");
    }
  }
}
