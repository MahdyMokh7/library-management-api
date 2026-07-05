package com.mehdymokhtari.libraryapi.service.validation;

import org.springframework.stereotype.Service;

import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.exception.InvalidOperationException;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

@Service
public class BookStatusValidator {

  public void validateStatus(Book book, BookStatus expectedStatus) {
    if (book.getStatus() != expectedStatus) {
      throw new BusinessException(
          "Book with ID "
              + book.getId()
              + " has status '"
              + book.getStatus()
              + "', expected '"
              + expectedStatus
              + "'");
    }
  }

  public void validateBookCanBeBorrowed(Book book) {
    if (!book.isAvailable()) {
      throw new BusinessException(
          "Book with ID " + book.getId() + " is not available for borrowing");
    }
  }

  public void validateBookCanBeReturned(Book book) {
    if (!book.isBorrowed()) {
      throw new InvalidOperationException(
          "Book with ID " + book.getId() + " is not currently borrowed");
    }
  }
}
