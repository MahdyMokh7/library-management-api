package com.mehdymokhtari.libraryapi.service.validation;

import org.springframework.stereotype.Service;

import com.mehdymokhtari.libraryapi.exception.BookNotAvailableException;
import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.repository.BookRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class BookValidationService {

  private final BookRepository bookRepository;

  public void validateCreateBook(BookRequest request) {
    if (bookRepository.existsByIsbn(request.isbn())) {
      throw new BusinessException("Book with ISBN " + request.isbn() + " already exists");
    }
  }

  public void validateUpdateBook(Long id, BookUpdateRequest request) {
    if (!bookRepository.existsByIdAndDeletedFalse(id)) {
      throw new BusinessException("Book with ID " + id + " does not exist");
    }
  }

  public void validateDeleteBook(Long id) {
    if (!bookRepository.existsByIdAndDeletedFalse(id)) {
      throw new BusinessException("Book with ID " + id + " does not exist");
    }
  }

  public Book validateAndGetBook(Long id) {
    return bookRepository
        .findByIdAndDeletedFalse(id)
        .orElseThrow(() -> new BusinessException("Book with ID " + id + " does not exist"));
  }

  public void validateBookNotBorrowed(Long id) {
    if (bookRepository.isBookWithStatus(id, BookStatus.BORROWED)) {
      throw new BookNotAvailableException("Cannot delete book that is currently borrowed");
    }
  }

  public void validateIsbnUniqueForUpdate(String isbn, Long id) {
    if (bookRepository.existsByIsbnAndIdNot(isbn, id)) {
      throw new BusinessException("Book with ISBN " + isbn + " already exists");
    }
  }
}
