package com.mehdymokhtari.libraryapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;

import com.mehdymokhtari.libraryapi.exception.BookNotAvailableException;
import com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException;
import com.mehdymokhtari.libraryapi.filter.BookFilter;
import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.PagedResponse;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

class BookControllerTest extends BaseControllerTest {

  private BookRequest bookRequest;
  private BookResponse bookResponse;
  private BookUpdateRequest updateRequest;

  @BeforeEach
  void setUp() {
    bookRequest = new BookRequest("Clean Code", "Robert Martin", "9780132350884", 2008);

    bookResponse =
        new BookResponse(
            1L,
            "Clean Code",
            "Robert Martin",
            "9780132350884",
            2008,
            BookStatus.AVAILABLE,
            null,
            null);

    updateRequest = new BookUpdateRequest("Clean Code Updated", "Robert C. Martin", 2009);
  }

  @Test
  void shouldCreateBookAndReturn201() throws Exception {
    when(bookService.createBook(any(BookRequest.class))).thenReturn(bookResponse);

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(bookRequest)))
        .andExpect(status().isCreated())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Book created successfully"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.title").value("Clean Code"))
        .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
  }

  @Test
  void shouldReturnBadRequestWhenCreateBookWithInvalidData() throws Exception {
    BookRequest invalidRequest = new BookRequest("", "", "", null);

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldGetAllBooksWithFiltersAndPagination() throws Exception {
    PagedResponse<BookResponse> pagedResponse =
        new PagedResponse<>(List.of(bookResponse), 0, 20, 1, 1, true);

    when(bookService.getAllBooks(any(BookFilter.class), any(Pageable.class)))
        .thenReturn(pagedResponse);

    mockMvc
        .perform(
            get("/api/v1/books")
                .param("title", "Clean")
                .param("author", "Robert")
                .param("year", "2008")
                .param("status", "AVAILABLE")
                .param("page", "0")
                .param("size", "20")
                .param("sort", "id,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.totalElements").value(1));
  }

  @Test
  void shouldGetAllBooksWithoutFilters() throws Exception {
    PagedResponse<BookResponse> pagedResponse =
        new PagedResponse<>(List.of(bookResponse), 0, 20, 1, 1, true);

    when(bookService.getAllBooks(any(BookFilter.class), any(Pageable.class)))
        .thenReturn(pagedResponse);

    mockMvc
        .perform(get("/api/v1/books"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content[0].id").value(1));
  }

  @Test
  void shouldGetBookByIdAndReturn200() throws Exception {
    when(bookService.getBookById(1L)).thenReturn(bookResponse);

    mockMvc
        .perform(get("/api/v1/books/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.title").value("Clean Code"));
  }

  @Test
  void shouldReturn404WhenBookNotFound() throws Exception {
    when(bookService.getBookById(999L)).thenThrow(new ResourceNotFoundException("Book", 999L));

    mockMvc.perform(get("/api/v1/books/999")).andExpect(status().isNotFound());
  }

  @Test
  void shouldUpdateBookAndReturn200() throws Exception {
    BookResponse updatedResponse =
        new BookResponse(
            1L,
            "Clean Code Updated",
            "Robert C. Martin",
            "9780132350884",
            2009,
            BookStatus.AVAILABLE,
            null,
            null);

    when(bookService.updateBook(eq(1L), any(BookUpdateRequest.class))).thenReturn(updatedResponse);

    mockMvc
        .perform(
            put("/api/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.title").value("Clean Code Updated"))
        .andExpect(jsonPath("$.data.publicationYear").value(2009));
  }

  @Test
  void shouldReturn404WhenUpdatingNonExistingBook() throws Exception {
    when(bookService.updateBook(eq(999L), any(BookUpdateRequest.class)))
        .thenThrow(new ResourceNotFoundException("Book", 999L));

    mockMvc
        .perform(
            put("/api/v1/books/999")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(updateRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturnBadRequestWhenUpdateBookWithInvalidData() throws Exception {
    BookUpdateRequest invalidRequest = new BookUpdateRequest("", "", null);

    mockMvc
        .perform(
            put("/api/v1/books/1")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldDeleteBookAndReturn200() throws Exception {
    doNothing().when(bookService).deleteBook(1L);

    mockMvc
        .perform(delete("/api/v1/books/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Book deleted successfully"));
  }

  @Test
  void shouldReturn404WhenDeletingNonExistingBook() throws Exception {
    doThrow(new ResourceNotFoundException("Book", 999L)).when(bookService).deleteBook(999L);

    mockMvc.perform(delete("/api/v1/books/999")).andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn409WhenDeletingBorrowedBook() throws Exception {
    doThrow(new BookNotAvailableException("Cannot delete book that is currently borrowed"))
        .when(bookService)
        .deleteBook(1L);

    mockMvc.perform(delete("/api/v1/books/1")).andExpect(status().isConflict());
  }
}
