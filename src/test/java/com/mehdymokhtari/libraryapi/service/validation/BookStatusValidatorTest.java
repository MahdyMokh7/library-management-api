package com.mehdymokhtari.libraryapi.service.validation;

import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.exception.InvalidOperationException;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class BookStatusValidatorTest {

    private final BookStatusValidator validator = new BookStatusValidator();

    @Test
    void testValidateStatus_Success() {
        Book book = Mockito.mock(Book.class);
        when(book.getStatus()).thenReturn(BookStatus.AVAILABLE);

        assertDoesNotThrow(() -> validator.validateStatus(book, BookStatus.AVAILABLE));
    }

    @Test
    void testValidateStatus_ThrowsBusinessException() {
        Book book = Mockito.mock(Book.class);
        when(book.getId()).thenReturn(1L);
        when(book.getStatus()).thenReturn(BookStatus.BORROWED);

        BusinessException ex = assertThrows(BusinessException.class,
                () -> validator.validateStatus(book, BookStatus.AVAILABLE));
        assertTrue(ex.getMessage().contains("expected 'AVAILABLE'"));
    }

    @Test
    void testValidateBookCanBeBorrowed_Success() {
        Book book = Mockito.mock(Book.class);
        when(book.isAvailable()).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateBookCanBeBorrowed(book));
    }

    @Test
    void testValidateBookCanBeBorrowed_ThrowsException() {
        Book book = Mockito.mock(Book.class);
        when(book.getId()).thenReturn(1L);
        when(book.isAvailable()).thenReturn(false);

        assertThrows(BusinessException.class, () -> validator.validateBookCanBeBorrowed(book));
    }

    @Test
    void testValidateBookCanBeReturned_Success() {
        Book book = Mockito.mock(Book.class);
        when(book.isBorrowed()).thenReturn(true);

        assertDoesNotThrow(() -> validator.validateBookCanBeReturned(book));
    }

    @Test
    void testValidateBookCanBeReturned_ThrowsException() {
        Book book = Mockito.mock(Book.class);
        when(book.getId()).thenReturn(1L);
        when(book.isBorrowed()).thenReturn(false);

        assertThrows(InvalidOperationException.class, () -> validator.validateBookCanBeReturned(book));
    }
}