package com.mehdymokhtari.libraryapi.service.validation;

import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.exception.BusinessException;
import org.springframework.stereotype.Service;

@Service
public class BookStatusValidator {

    public void validateBookNotDeleted(Book book) {
        if (book.isDeleted()) {
            throw new BusinessException("Book with ID " + book.getId() + " has been deleted");
        }
    }

    public void validateBookStatus(Book book, BookStatus expectedStatus) {
        if (book.getStatus() != expectedStatus) {
            throw new BusinessException(
                    "Book with ID " + book.getId() + " has status '" + book.getStatus() +
                            "', expected '" + expectedStatus + "'"
            );
        }
    }

    public void validateBookCanBeBorrowed(Book book) {
        validateBookNotDeleted(book);
        validateBookStatus(book, BookStatus.AVAILABLE);
    }

    public void validateBookCanBeReturned(Book book) {
        validateBookNotDeleted(book);
        validateBookStatus(book, BookStatus.BORROWED);
    }

    public void validateBookCanBeDeleted(Book book) {
        validateBookNotDeleted(book);
        if (book.isBorrowed()) {
            throw new BusinessException("Cannot delete book that is currently borrowed");
        }
    }
}