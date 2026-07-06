package com.mehdymokhtari.libraryapi.model.mapper;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;

class BorrowingRecordMapperTest {

  private final BorrowingRecordMapper mapper = Mappers.getMapper(BorrowingRecordMapper.class);

  // Helper method matching the (Long itemId, String borrowerName) record constructor
  private BorrowRequest createBorrowRequest(String borrowerName) {
    return new BorrowRequest(1L, borrowerName);
  }

  @Test
  void testToEntity_AllNullBranches() {
    assertNull(mapper.toEntity(null, null));

    BorrowRequest requestOnly = createBorrowRequest("Only Request");
    BorrowingRecord entityFromRequestOnly = mapper.toEntity(requestOnly, null);
    assertNotNull(entityFromRequestOnly);
    assertEquals("Only Request", entityFromRequestOnly.getBorrowerName());
    assertNull(entityFromRequestOnly.getItem());

    Book bookOnly = new Book();
    bookOnly.setId(99L);
    BorrowingRecord entityFromBookOnly = mapper.toEntity(null, bookOnly);
    assertNotNull(entityFromBookOnly);
    assertNull(entityFromBookOnly.getBorrowerName());
    assertEquals(99L, entityFromBookOnly.getItem().getId());
  }

  @Test
  void testToEntity_FullSuccess() {
    BorrowRequest request = createBorrowRequest("Alice");
    Book book = new Book();
    book.setId(5L);
    book.setTitle("Book Title");

    BorrowingRecord entity = mapper.toEntity(request, book);
    assertNotNull(entity);
    assertEquals("Alice", entity.getBorrowerName());
    assertEquals(book, entity.getItem());
    assertEquals(LocalDate.now(), entity.getBorrowedDate());
    assertEquals(BorrowingStatus.BORROWED, entity.getStatus());
  }

  @Test
  void testToResponse_SuccessAndNull() {
    assertNull(mapper.toResponse(null));

    BorrowingRecord entityWithoutItem = new BorrowingRecord();
    entityWithoutItem.setId(10L);
    entityWithoutItem.setBorrowerName("Bob");
    entityWithoutItem.setStatus(BorrowingStatus.BORROWED);
    entityWithoutItem.setItem(null);

    BorrowingRecordResponse responseWithoutItem = mapper.toResponse(entityWithoutItem);
    assertNotNull(responseWithoutItem);
    assertNull(responseWithoutItem.itemId());
    assertNull(responseWithoutItem.itemTitle());

    Book book = new Book();
    book.setId(50L);
    book.setTitle("Java Basics");

    BorrowingRecord entityWithItem = new BorrowingRecord();
    entityWithItem.setId(10L);
    entityWithItem.setBorrowerName("Bob");
    entityWithItem.setStatus(BorrowingStatus.BORROWED);
    entityWithItem.setItem(book);

    BorrowingRecordResponse responseWithItem = mapper.toResponse(entityWithItem);
    assertNotNull(responseWithItem);
    assertEquals(50L, responseWithItem.itemId());
    assertEquals("Java Basics", responseWithItem.itemTitle());
    assertEquals("Bob", responseWithItem.borrowerName());
  }

  @Test
  void testToResponseList_SuccessNullAndEmpty() {
    assertNull(mapper.toResponseList(null));

    List<BorrowingRecordResponse> emptyList = mapper.toResponseList(Collections.emptyList());
    assertNotNull(emptyList);
    assertTrue(emptyList.isEmpty());

    BorrowingRecord entity = new BorrowingRecord();
    entity.setBorrowerName("Charlie");

    List<BorrowingRecordResponse> populatedList = mapper.toResponseList(List.of(entity));
    assertNotNull(populatedList);
    assertEquals(1, populatedList.size());
    assertEquals("Charlie", populatedList.get(0).borrowerName());
  }
}
