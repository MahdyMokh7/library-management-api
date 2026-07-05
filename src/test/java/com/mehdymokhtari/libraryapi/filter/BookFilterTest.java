package com.mehdymokhtari.libraryapi.filter;

import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class BookFilterTest {

    @Test
    void testLombokGettersSettersAndBuilder() {
        BookFilter filter = BookFilter.builder()
                .title("Clean Code")
                .author("Robert Martin")
                .publicationYear(2008)
                .status(BookStatus.AVAILABLE)
                .build();

        assertEquals("Clean Code", filter.getTitle());
        assertEquals("Robert Martin", filter.getAuthor());
        assertEquals(2008, filter.getPublicationYear());
        assertEquals(BookStatus.AVAILABLE, filter.getStatus());

        filter.setTitle("Refactoring");
        assertEquals("Refactoring", filter.getTitle());

        assertNotNull(filter.toString());
        assertEquals(filter, BookFilter.builder().title("Refactoring").author("Robert Martin").publicationYear(2008).status(BookStatus.AVAILABLE).build());
    }
}