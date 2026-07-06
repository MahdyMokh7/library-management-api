package com.mehdymokhtari.libraryapi.model.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

class BookMapperTest {

  private final BookMapper mapper = Mappers.getMapper(BookMapper.class);

  @Test
  void testToEntity_SuccessAndNull() {
    // Branch 1: Null check coverage
    assertNull(mapper.toEntity(null));

    // Branch 2: Value parsing coverage
    BookRequest request = new BookRequest("Title", "Author", "12345", 2026);
    Book entity = mapper.toEntity(request);

    assertNotNull(entity);
    assertEquals("Title", entity.getTitle());
    assertEquals("Author", entity.getAuthor());
    assertEquals("12345", entity.getIsbn());
    assertEquals(2026, entity.getPublicationYear());
    assertEquals(BookStatus.AVAILABLE, entity.getStatus());
    assertFalse(entity.isDeleted());
  }

  @Test
  void testToResponse_SuccessAndNull() {
    // Branch 1: Null check coverage
    assertNull(mapper.toResponse(null));

    // Branch 2: Value transformation
    Book entity = new Book();
    entity.setId(1L);
    entity.setTitle("Title");
    entity.setStatus(BookStatus.BORROWED);

    BookResponse response = mapper.toResponse(entity);
    assertNotNull(response);
    assertEquals(1L, response.id());
    assertEquals("Title", response.title());
  }

  @Test
  void testToResponseList_SuccessNullAndEmpty() {
    // Branch 1: Null list branch
    assertNull(mapper.toResponseList(null));

    // Branch 2: Empty list branch
    List<BookResponse> emptyRes = mapper.toResponseList(Collections.emptyList());
    assertNotNull(emptyRes);
    assertTrue(emptyRes.isEmpty());

    // Branch 3: Active population iteration loop branch
    Book entity = new Book();
    entity.setId(2L);
    List<BookResponse> res = mapper.toResponseList(List.of(entity));
    assertEquals(1, res.size());
    assertEquals(2L, res.get(0).id());
  }

  @Test
  void testUpdateEntity_SuccessAndNull() {
    Book entity = new Book();
    entity.setTitle("Original Title");
    entity.setAuthor("Original Author");

    // Branch 1: Null request checks strategy behavior
    mapper.updateEntity(null, entity);
    assertEquals("Original Title", entity.getTitle());

    // Branch 2: Actual updates with NullValuePropertyMappingStrategy.IGNORE coverage
    BookUpdateRequest request = new BookUpdateRequest("Updated Title", null, 2026);
    mapper.updateEntity(request, entity);

    assertEquals("Updated Title", entity.getTitle());
    assertEquals(
        "Original Author", entity.getAuthor()); // Remained because of IGNORE strategy on null
    assertEquals(2026, entity.getPublicationYear());
  }
}
