package com.mehdymokhtari.libraryapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mehdymokhtari.libraryapi.filter.BookFilter;
import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.PagedResponse;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.service.BookService;

@WebMvcTest(BookController.class)
class BookControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookService bookService;

    private BookRequest bookRequest;
    private BookResponse bookResponse;
    private BookUpdateRequest updateRequest;

    @BeforeEach
    void setUp() {
        bookRequest = new BookRequest("Clean Code", "Robert Martin", "9780132350884", 2008);

        bookResponse = new BookResponse(
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
        // Test: POST /api/v1/books returns 201 Created with book data
        when(bookService.createBook(any(BookRequest.class))).thenReturn(bookResponse);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bookRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book created successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Clean Code"))
                .andExpect(jsonPath("$.data.author").value("Robert Martin"))
                .andExpect(jsonPath("$.data.isbn").value("9780132350884"))
                .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
    }

    @Test
    void shouldReturnBadRequestWhenCreateBookWithInvalidData() throws Exception {
        // Test: POST /api/v1/books with invalid data returns 400 Bad Request
        BookRequest invalidRequest = new BookRequest("", "", "", null);

        mockMvc.perform(post("/api/v1/books")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldGetAllBooksWithFiltersAndPagination() throws Exception {
        // Test: GET /api/v1/books with filters and pagination returns 200 OK
        PagedResponse<BookResponse> pagedResponse = new PagedResponse<>(
                List.of(bookResponse), 0, 20, 1, 1, true);

        when(bookService.getAllBooks(any(BookFilter.class), any(Pageable.class)))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/books")
                        .param("title", "Clean")
                        .param("author", "Robert")
                        .param("year", "2008")
                        .param("status", "AVAILABLE")
                        .param("page", "0")
                        .param("size", "20")
                        .param("sort", "id,asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Books retrieved successfully"))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1))
                .andExpect(jsonPath("$.data.totalElements").value(1))
                .andExpect(jsonPath("$.data.pageNumber").value(0))
                .andExpect(jsonPath("$.data.pageSize").value(20))
                .andExpect(jsonPath("$.data.last").value(true));
    }

    @Test
    void shouldGetAllBooksWithoutFilters() throws Exception {
        // Test: GET /api/v1/books without filters returns all books
        PagedResponse<BookResponse> pagedResponse = new PagedResponse<>(
                List.of(bookResponse), 0, 20, 1, 1, true);

        when(bookService.getAllBooks(any(BookFilter.class), any(Pageable.class)))
                .thenReturn(pagedResponse);

        mockMvc.perform(get("/api/v1/books"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data.content").isArray())
                .andExpect(jsonPath("$.data.content[0].id").value(1));
    }

    @Test
    void shouldGetBookByIdAndReturn200() throws Exception {
        // Test: GET /api/v1/books/{id} returns 200 OK with book data
        when(bookService.getBookById(1L)).thenReturn(bookResponse);

        mockMvc.perform(get("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book retrieved successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Clean Code"));
    }

    @Test
    void shouldReturn404WhenBookNotFound() throws Exception {
        // Test: GET /api/v1/books/{id} for non-existing book returns 404 Not Found
        when(bookService.getBookById(999L))
                .thenThrow(new com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException("Book", 999L));

        mockMvc.perform(get("/api/v1/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldUpdateBookAndReturn200() throws Exception {
        // Test: PUT /api/v1/books/{id} returns 200 OK with updated book
        BookResponse updatedResponse = new BookResponse(
                1L,
                "Clean Code Updated",
                "Robert C. Martin",
                "9780132350884",
                2009,
                BookStatus.AVAILABLE,
                null,
                null);

        when(bookService.updateBook(eq(1L), any(BookUpdateRequest.class))).thenReturn(updatedResponse);

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book updated successfully"))
                .andExpect(jsonPath("$.data.id").value(1))
                .andExpect(jsonPath("$.data.title").value("Clean Code Updated"))
                .andExpect(jsonPath("$.data.author").value("Robert C. Martin"))
                .andExpect(jsonPath("$.data.publicationYear").value(2009));
    }

    @Test
    void shouldReturn404WhenUpdatingNonExistingBook() throws Exception {
        // Test: PUT /api/v1/books/{id} for non-existing book returns 404 Not Found
        when(bookService.updateBook(eq(999L), any(BookUpdateRequest.class)))
                .thenThrow(new com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException("Book", 999L));

        mockMvc.perform(put("/api/v1/books/999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturnBadRequestWhenUpdateBookWithInvalidData() throws Exception {
        // Test: PUT /api/v1/books/{id} with invalid data returns 400 Bad Request
        BookUpdateRequest invalidRequest = new BookUpdateRequest("", "", null);

        mockMvc.perform(put("/api/v1/books/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteBookAndReturn200() throws Exception {
        // Test: DELETE /api/v1/books/{id} returns 200 OK
        doNothing().when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Book deleted successfully"));
    }

    @Test
    void shouldReturn404WhenDeletingNonExistingBook() throws Exception {
        // Test: DELETE /api/v1/books/{id} for non-existing book returns 404 Not Found
        doThrow(new com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException("Book", 999L))
                .when(bookService).deleteBook(999L);

        mockMvc.perform(delete("/api/v1/books/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenDeletingBorrowedBook() throws Exception {
        // Test: DELETE /api/v1/books/{id} for borrowed book returns 409 Conflict
        doThrow(new com.mehdymokhtari.libraryapi.exception.BusinessException("Cannot delete book that is currently borrowed"))
                .when(bookService).deleteBook(1L);

        mockMvc.perform(delete("/api/v1/books/1"))
                .andExpect(status().isConflict());
    }
}