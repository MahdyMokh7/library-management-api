package com.mehdymokhtari.libraryapi.filter;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

class BookFilterTest {

  @Test
  void testLombokGettersSettersAndBuilder() {
    BookFilter filter =
        BookFilter.builder()
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
  }

  @Test
  void testLombokEqualsAndHashCodeBranches() {
    BookFilter filter1 =
        BookFilter.builder()
            .title("A")
            .author("B")
            .publicationYear(2000)
            .status(BookStatus.AVAILABLE)
            .build();
    BookFilter filter2 =
        BookFilter.builder()
            .title("A")
            .author("B")
            .publicationYear(2000)
            .status(BookStatus.AVAILABLE)
            .build();
    BookFilter filter3 =
        BookFilter.builder()
            .title("Diff")
            .author("B")
            .publicationYear(2000)
            .status(BookStatus.AVAILABLE)
            .build();

    // Reflexive, Symmetric, Null, and Class type check branches
    assertEquals(filter1, filter1);
    assertEquals(filter1, filter2);
    assertNotEquals(filter1, filter3);
    assertNotEquals(filter1, null);
    assertNotEquals(filter1, "Not A BookFilter Object");

    // HashCode branches
    assertEquals(filter1.hashCode(), filter2.hashCode());
    assertNotEquals(filter1.hashCode(), filter3.hashCode());

    // Test with null fields inside the model to catch null-safe comparison branches
    BookFilter emptyFilter1 = BookFilter.builder().build();
    BookFilter emptyFilter2 = BookFilter.builder().build();
    assertEquals(emptyFilter1, emptyFilter2);
    assertNotEquals(emptyFilter1, filter1);
    assertEquals(emptyFilter1.hashCode(), emptyFilter2.hashCode());
  }
}
