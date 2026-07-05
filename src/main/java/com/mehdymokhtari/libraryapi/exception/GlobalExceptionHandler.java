package com.mehdymokhtari.libraryapi.exception;

import static org.springframework.http.HttpStatus.*;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import jakarta.validation.ConstraintViolationException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import lombok.extern.slf4j.Slf4j;

// should generate private static final logger log = from LoggerFactory
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(ResourceNotFoundException.class)
  public ResponseEntity<ErrorResponse> handleResourceNotFound(
      ResourceNotFoundException ex, WebRequest request) {
    log.error("Resource not found: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.of(
            NOT_FOUND.value(),
            NOT_FOUND.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(NOT_FOUND).body(error);
  }

  @ExceptionHandler(BookNotAvailableException.class)
  public ResponseEntity<ErrorResponse> handleBookNotAvailable(
      BookNotAvailableException ex, WebRequest request) {
    log.error("Book not available: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.of(
            CONFLICT.value(),
            CONFLICT.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(CONFLICT).body(error);
  }

  @ExceptionHandler(BusinessException.class)
  public ResponseEntity<ErrorResponse> handleBusinessException(
      BusinessException ex, WebRequest request) {
    log.error("Business exception: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.of(
            UNPROCESSABLE_ENTITY.value(),
            UNPROCESSABLE_ENTITY.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(UNPROCESSABLE_ENTITY).body(error);
  }

  @ExceptionHandler(InvalidOperationException.class)
  public ResponseEntity<ErrorResponse> handleInvalidOperation(
      InvalidOperationException ex, WebRequest request) {
    log.error("Invalid operation: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.of(
            BAD_REQUEST.value(),
            BAD_REQUEST.getReasonPhrase(),
            ex.getMessage(),
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(BAD_REQUEST).body(error);
  }

  @ExceptionHandler(ConstraintViolationException.class)
  public ResponseEntity<ErrorResponse> handleConstraintViolation(
      ConstraintViolationException ex, WebRequest request) {
    log.error("Constraint violation: {}", ex.getMessage());
    Map<String, String> errors =
        ex.getConstraintViolations().stream()
            .collect(
                Collectors.toMap(
                    violation -> violation.getPropertyPath().toString(),
                    violation -> violation.getMessage(),
                    (existing, replacement) -> existing));

    ErrorResponse error =
        ErrorResponse.of(
            BAD_REQUEST.value(),
            BAD_REQUEST.getReasonPhrase(),
            "Validation failed",
            request.getDescription(false).replace("uri=", ""),
            errors);
    return ResponseEntity.status(BAD_REQUEST).body(error);
  }

  @ExceptionHandler(MethodArgumentNotValidException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentNotValid(
      MethodArgumentNotValidException ex, WebRequest request) {
    log.error("Method argument validation failed: {}", ex.getMessage());
    Map<String, String> errors = new HashMap<>();
    ex.getBindingResult()
        .getAllErrors()
        .forEach(
            error -> {
              String fieldName =
                  error instanceof FieldError
                      ? ((FieldError) error).getField()
                      : error.getObjectName();
              errors.put(fieldName, error.getDefaultMessage());
            });

    ErrorResponse error =
        ErrorResponse.of(
            BAD_REQUEST.value(),
            BAD_REQUEST.getReasonPhrase(),
            "Validation failed",
            request.getDescription(false).replace("uri=", ""),
            errors);
    return ResponseEntity.status(BAD_REQUEST).body(error);
  }

  @ExceptionHandler(MethodArgumentTypeMismatchException.class)
  public ResponseEntity<ErrorResponse> handleMethodArgumentTypeMismatch(
      MethodArgumentTypeMismatchException ex, WebRequest request) {
    log.error("Method argument type mismatch: {}", ex.getMessage());
    String message =
        String.format("Invalid value '%s' for parameter '%s'", ex.getValue(), ex.getName());
    ErrorResponse error =
        ErrorResponse.of(
            BAD_REQUEST.value(),
            BAD_REQUEST.getReasonPhrase(),
            message,
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(BAD_REQUEST).body(error);
  }

  @ExceptionHandler(HttpMessageNotReadableException.class)
  public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(
      HttpMessageNotReadableException ex, WebRequest request) {
    log.error("Malformed JSON request: {}", ex.getMessage());
    ErrorResponse error =
        ErrorResponse.of(
            BAD_REQUEST.value(),
            BAD_REQUEST.getReasonPhrase(),
            "Malformed JSON request. Please check your request body.",
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(BAD_REQUEST).body(error);
  }

  @ExceptionHandler(DataIntegrityViolationException.class)
  public ResponseEntity<ErrorResponse> handleDataIntegrityViolation(
      DataIntegrityViolationException ex, WebRequest request) {
    log.error("Data integrity violation: {}", ex.getMessage());
    String message = "Database constraint violation";
    if (ex.getMessage() != null && ex.getMessage().contains("duplicate key")) {
      message = "Duplicate entry. Please check unique fields.";
    }

    ErrorResponse error =
        ErrorResponse.of(
            CONFLICT.value(),
            CONFLICT.getReasonPhrase(),
            message,
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(CONFLICT).body(error);
  }

  @ExceptionHandler(Exception.class)
  public ResponseEntity<ErrorResponse> handleAllUncaughtException(
      Exception ex, WebRequest request) {
    log.error("Unhandled exception: ", ex);
    ErrorResponse error =
        ErrorResponse.of(
            INTERNAL_SERVER_ERROR.value(),
            INTERNAL_SERVER_ERROR.getReasonPhrase(),
            "An unexpected error occurred. Please try again later.",
            request.getDescription(false).replace("uri=", ""));
    return ResponseEntity.status(INTERNAL_SERVER_ERROR).body(error);
  }
}
