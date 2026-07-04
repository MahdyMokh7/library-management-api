package com.mehdymokhtari.libraryapi.model.entity;

import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@DiscriminatorValue("BOOK")
public class Book extends LibraryItem {

    @Column(nullable = false)
    private String author;

    @Column(unique = true, nullable = false, length = 17)
    private String isbn;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    public void borrow() {
        this.status = BookStatus.BORROWED;
    }

    public void returnBook() {
        this.status = BookStatus.AVAILABLE;
    }

    public boolean isAvailable() {
        return this.status == BookStatus.AVAILABLE && !this.isDeleted();
    }

    public boolean isBorrowed() {
        return this.status == BookStatus.BORROWED && !this.isDeleted();
    }
}