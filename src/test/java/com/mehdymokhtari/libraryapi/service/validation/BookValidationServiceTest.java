package com.mehdymokhtari.libraryapi.service.validation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.mehdymokhtari.libraryapi.exception.BookNotAvailableException;
import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException;
import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.repository.BookRepository;

@ExtendWith(MockitoExtension.class)
class BookValidationServiceTest {

  @Mock private BookRepository bookRepository;

  @InjectMocks private BookValidationService validationService;

  @Test
  void testValidateCreateBook_Success() {
    BookRequest request = new BookRequest("Title", "Author", "1234567890", 2023);
    when(bookRepository.existsByIsbn("1234567890")).thenReturn(false);

    assertDoesNotThrow(() -> validationService.validateCreateBook(request));
  }

  @Test
  void testValidateCreateBook_ThrowsException() {
    BookRequest request = new BookRequest("Title", "Author", "1234567890", 2023);
    when(bookRepository.existsByIsbn("1234567890")).thenReturn(true);

    assertThrows(BusinessException.class, () -> validationService.validateCreateBook(request));
  }

  @Test
  void testValidateUpdateBook_Success() {
    when(bookRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);
    assertDoesNotThrow(
        () -> validationService.validateUpdateBook(1L, mock(BookUpdateRequest.class)));
  }

  @Test
  void testValidateUpdateBook_ThrowsException() {
    when(bookRepository.existsByIdAndDeletedFalse(1L)).thenReturn(false);
    assertThrows(
        ResourceNotFoundException.class,
        () -> validationService.validateUpdateBook(1L, mock(BookUpdateRequest.class)));
  }

  @Test
  void testValidateDeleteBook_Success() {
    when(bookRepository.existsByIdAndDeletedFalse(1L)).thenReturn(true);
    assertDoesNotThrow(() -> validationService.validateDeleteBook(1L));
  }

  @Test
  void testValidateDeleteBook_ThrowsException() {
    when(bookRepository.existsByIdAndDeletedFalse(1L)).thenReturn(false);
    assertThrows(ResourceNotFoundException.class, () -> validationService.validateDeleteBook(1L));
  }

  @Test
  void testValidateAndGetBook_Success() {
    Book book = new Book();
    when(bookRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.of(book));

    assertEquals(book, validationService.validateAndGetBook(1L));
  }

  @Test
  void testValidateAndGetBook_ThrowsException() {
    when(bookRepository.findByIdAndDeletedFalse(1L)).thenReturn(Optional.empty());
    assertThrows(ResourceNotFoundException.class, () -> validationService.validateAndGetBook(1L));
  }

  @Test
  void testValidateBookNotBorrowed_Success() {
    when(bookRepository.isBookWithStatus(1L, BookStatus.BORROWED)).thenReturn(false);
    assertDoesNotThrow(() -> validationService.validateBookNotBorrowed(1L));
  }

  @Test
  void testValidateBookNotBorrowed_ThrowsException() {
    when(bookRepository.isBookWithStatus(1L, BookStatus.BORROWED)).thenReturn(true);
    assertThrows(
        BookNotAvailableException.class, () -> validationService.validateBookNotBorrowed(1L));
  }

  @Test
  void testValidateIsbnUniqueForUpdate_Success() {
    when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(false);
    assertDoesNotThrow(() -> validationService.validateIsbnUniqueForUpdate("123", 1L));
  }

  @Test
  void testValidateIsbnUniqueForUpdate_ThrowsException() {
    when(bookRepository.existsByIsbnAndIdNot("123", 1L)).thenReturn(true);
    assertThrows(
        BusinessException.class, () -> validationService.validateIsbnUniqueForUpdate("123", 1L));
  }
}
