package com.mehdymokhtari.libraryapi.exception;

public class BookNotAvailableException extends RuntimeException {

    private static final String DEFAULT_MESSAGE = "Book is not available for borrowing";

    public BookNotAvailableException() {
        super(DEFAULT_MESSAGE);
    }

    public BookNotAvailableException(String message) {
        super(message);
    }

    public BookNotAvailableException(Long bookId) {
        super(String.format("Book with ID %s is not available for borrowing", bookId));
    }
}