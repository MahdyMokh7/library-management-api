package com.mehdymokhtari.libraryapi.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CustomExceptionsTest {

    @Test
    void testBookNotAvailableExceptionConstructors() {
        BookNotAvailableException ex1 = new BookNotAvailableException();
        assertEquals("Book is not available for borrowing", ex1.getMessage());

        BookNotAvailableException ex2 = new BookNotAvailableException("Custom Msg");
        assertEquals("Custom Msg", ex2.getMessage());

        BookNotAvailableException ex3 = new BookNotAvailableException(5L);
        assertEquals("Book with ID 5 is not available for borrowing", ex3.getMessage());
    }

    @Test
    void testItemNotBorrowedExceptionConstructors() {
        ItemNotBorrowedException ex1 = new ItemNotBorrowedException();
        assertEquals("Item is not currently borrowed", ex1.getMessage());

        ItemNotBorrowedException ex2 = new ItemNotBorrowedException("Custom Msg");
        assertEquals("Custom Msg", ex2.getMessage());

        ItemNotBorrowedException ex3 = new ItemNotBorrowedException(10L);
        assertEquals("Item with ID 10 is not currently borrowed", ex3.getMessage());
    }

    @Test
    void testResourceNotFoundExceptionConstructors() {
        ResourceNotFoundException ex1 = new ResourceNotFoundException();
        assertEquals("Resource not found", ex1.getMessage());

        ResourceNotFoundException ex2 = new ResourceNotFoundException("Author", 99L);
        assertEquals("Author with ID 99 not found", ex2.getMessage());
    }

    @Test
    void testErrorResponseValidationFactory() {
        ErrorResponse resp = ErrorResponse.of(400, "Bad Request", "Fail", "/test", java.util.Map.of("field", "err"));
        assertEquals(400, resp.status());
        assertNotNull(resp.validationErrors());
    }
}