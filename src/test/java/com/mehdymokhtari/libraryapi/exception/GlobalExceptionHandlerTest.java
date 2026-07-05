package com.mehdymokhtari.libraryapi.exception;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class GlobalExceptionHandlerTest {

    private final GlobalExceptionHandler handler = new GlobalExceptionHandler();
    private final WebRequest request = Mockito.mock(WebRequest.class);

    private void mockUri() {
        when(request.getDescription(false)).thenReturn("uri=/api/test");
    }

    @Test
    void handleResourceNotFoundTest() {
        mockUri();
        ResourceNotFoundException ex = new ResourceNotFoundException("Error raw");
        ResponseEntity<ErrorResponse> res = handler.handleResourceNotFound(ex, request);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    void handleBookNotAvailableTest() {
        mockUri();
        BookNotAvailableException ex = new BookNotAvailableException();
        ResponseEntity<ErrorResponse> res = handler.handleBookNotAvailable(ex, request);
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
    }

    @Test
    void handleBusinessExceptionTest() {
        mockUri();
        BusinessException ex = new BusinessException("Business issue");
        ResponseEntity<ErrorResponse> res = handler.handleBusinessException(ex, request);
        assertEquals(HttpStatus.UNPROCESSABLE_ENTITY, res.getStatusCode());
    }

    @Test
    void handleInvalidOperationTest() {
        mockUri();
        InvalidOperationException ex = new InvalidOperationException("Invalid action");
        ResponseEntity<ErrorResponse> res = handler.handleInvalidOperation(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void handleMethodArgumentTypeMismatchTest() {
        mockUri();
        MethodArgumentTypeMismatchException ex = Mockito.mock(MethodArgumentTypeMismatchException.class);
        when(ex.getValue()).thenReturn("invalid-id");
        when(ex.getName()).thenReturn("id");

        ResponseEntity<ErrorResponse> res = handler.handleMethodArgumentTypeMismatch(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void handleHttpMessageNotReadableTest() {
        mockUri();
        HttpMessageNotReadableException ex = Mockito.mock(HttpMessageNotReadableException.class);
        ResponseEntity<ErrorResponse> res = handler.handleHttpMessageNotReadable(ex, request);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    void handleDataIntegrityViolationTest() {
        mockUri();
        DataIntegrityViolationException ex = new DataIntegrityViolationException("duplicate key violation context");
        ResponseEntity<ErrorResponse> res = handler.handleDataIntegrityViolation(ex, request);
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
        assertEquals("Duplicate entry. Please check unique fields.", res.getBody().message());
    }

    @Test
    void handleDataIntegrityViolationGenericTest() {
        mockUri();
        DataIntegrityViolationException ex = new DataIntegrityViolationException("generic foreign key issue");
        ResponseEntity<ErrorResponse> res = handler.handleDataIntegrityViolation(ex, request);
        assertEquals("Database constraint violation", res.getBody().message());
    }

    @Test
    void handleItemNotBorrowedExceptionTest() {
        mockUri();
        ItemNotBorrowedException ex = new ItemNotBorrowedException();
        ResponseEntity<ErrorResponse> res = handler.handleItemNotBorrowed(ex, request);
        assertEquals(HttpStatus.CONFLICT, res.getStatusCode());
    }

    @Test
    void handleAllUncaughtExceptionTest() {
        mockUri();
        Exception ex = new Exception("Fatal programmatic issue");
        ResponseEntity<ErrorResponse> res = handler.handleAllUncaughtException(ex, request);
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, res.getStatusCode());
    }
}