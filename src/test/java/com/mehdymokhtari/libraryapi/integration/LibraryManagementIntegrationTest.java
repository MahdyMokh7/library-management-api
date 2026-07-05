package com.mehdymokhtari.libraryapi.integration;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.ApiResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.repository.BookRepository;
import com.mehdymokhtari.libraryapi.repository.BorrowingRecordRepository;

// end-to-end test (SpringBootTest - we used the Flyway too to init the DB schema)
@SpringBootTest
@AutoConfigureMockMvc
@Testcontainers
@ActiveProfiles("test")
class LibraryManagementIntegrationTest {

  @Container
  static PostgreSQLContainer<?> postgres =
      new PostgreSQLContainer<>("postgres:15-alpine")
          .withDatabaseName("library_db_test")
          .withUsername("test_user")
          .withPassword("test_password");

  @DynamicPropertySource
  static void properties(DynamicPropertyRegistry registry) {
    registry.add("spring.datasource.url", postgres::getJdbcUrl);
    registry.add("spring.datasource.username", postgres::getUsername);
    registry.add("spring.datasource.password", postgres::getPassword);
    registry.add("spring.flyway.enabled", () -> "true");
    registry.add("spring.jpa.hibernate.ddl-auto", () -> "validate");
  }

  @Autowired private MockMvc mockMvc;

  @Autowired private ObjectMapper objectMapper;

  @Autowired private BookRepository bookRepository;

  @Autowired private BorrowingRecordRepository borrowingRecordRepository;

  private BookRequest bookRequest;

  @BeforeEach
  void setUp() {
    borrowingRecordRepository.deleteAll();
    bookRepository.deleteAll();

    bookRequest = new BookRequest("Clean Code", "Robert Martin", "9780132350884", 2008);
  }

  @Test
  void shouldCreateAndRetrieveBookEndToEnd() throws Exception {
    // Test: End-to-end - Create book, then retrieve it by ID
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.success").value(true))
            .andExpect(jsonPath("$.data.id").exists())
            .andExpect(jsonPath("$.data.title").value("Clean Code"))
            .andExpect(jsonPath("$.data.isbn").value("9780132350884"))
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    mockMvc
        .perform(get("/api/v1/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(bookId))
        .andExpect(jsonPath("$.data.title").value("Clean Code"))
        .andExpect(jsonPath("$.data.author").value("Robert Martin"))
        .andExpect(jsonPath("$.data.isbn").value("9780132350884"))
        .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
  }

  @Test
  void shouldCreateAndUpdateBookEndToEnd() throws Exception {
    // Test: End-to-end - Create book, then update it
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    BookRequest updateRequest =
        new BookRequest("Clean Code Updated", "Robert C. Martin", "9780132350884", 2009);

    mockMvc
        .perform(
            put("/api/v1/books/{id}", bookId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.id").value(bookId))
        .andExpect(jsonPath("$.data.title").value("Clean Code Updated"))
        .andExpect(jsonPath("$.data.author").value("Robert C. Martin"))
        .andExpect(jsonPath("$.data.publicationYear").value(2009))
        .andExpect(jsonPath("$.data.isbn").value("9780132350884"));
  }

  @Test
  void shouldCreateAndDeleteBookEndToEnd() throws Exception {
    // Test: End-to-end - Create book, then soft delete it
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    mockMvc
        .perform(delete("/api/v1/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Book deleted successfully"));

    // Verify book is soft deleted (not found)
    mockMvc.perform(get("/api/v1/books/{id}", bookId)).andExpect(status().isNotFound());
  }

  @Test
  void shouldBorrowAndReturnBookEndToEnd() throws Exception {
    // Test: End-to-end - Create book, borrow it, then return it
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    // Borrow the book
    BorrowRequest borrowRequest = new BorrowRequest(bookId, "John Doe");

    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Book borrowed successfully"))
        .andExpect(jsonPath("$.data.itemId").value(bookId))
        .andExpect(jsonPath("$.data.borrowerName").value("John Doe"))
        .andExpect(jsonPath("$.data.status").value("BORROWED"));

    // Verify book status changed to BORROWED
    mockMvc
        .perform(get("/api/v1/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("BORROWED"));

    // Return the book
    ReturnRequest returnRequest = new ReturnRequest(bookId);

    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(returnRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Book returned successfully"))
        .andExpect(jsonPath("$.data.itemId").value(bookId))
        .andExpect(jsonPath("$.data.status").value("RETURNED"))
        .andExpect(jsonPath("$.data.returnDate").exists());

    // Verify book status changed back to AVAILABLE
    mockMvc
        .perform(get("/api/v1/books/{id}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.data.status").value("AVAILABLE"));
  }

  @Test
  void shouldGetBorrowingHistoryEndToEnd() throws Exception {
    // Test: End-to-end - Create book, borrow it, return it, then get history
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    // Borrow the book
    BorrowRequest borrowRequest = new BorrowRequest(bookId, "John Doe");
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
        .andExpect(status().isOk());

    // Return the book
    ReturnRequest returnRequest = new ReturnRequest(bookId);
    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(returnRequest)))
        .andExpect(status().isOk());

    // Get borrowing history
    mockMvc
        .perform(get("/api/v1/borrowings/book/{bookId}", bookId))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].status").value("RETURNED"))
        .andExpect(jsonPath("$.data[0].borrowerName").value("John Doe"))
        .andExpect(jsonPath("$.data[1].status").value("BORROWED"))
        .andExpect(jsonPath("$.data[1].borrowerName").value("John Doe"));
  }

  @Test
  void shouldGetAllBooksWithFiltersAndPaginationEndToEnd() throws Exception {
    // Test: End-to-end - Create multiple books, then filter and paginate
    BookRequest bookRequest2 =
        new BookRequest("Effective Java", "Joshua Bloch", "9780134685991", 2018);

    BookRequest bookRequest3 =
        new BookRequest("Spring in Action", "Craig Walls", "9781617294945", 2018);

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest2)))
        .andExpect(status().isCreated());

    mockMvc
        .perform(
            post("/api/v1/books")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(bookRequest3)))
        .andExpect(status().isCreated());

    // Get all books with pagination
    mockMvc
        .perform(
            get("/api/v1/books").param("page", "0").param("size", "10").param("sort", "id,asc"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.totalElements").value(3))
        .andExpect(jsonPath("$.data.pageNumber").value(0))
        .andExpect(jsonPath("$.data.pageSize").value(10));

    // Filter by year
    mockMvc
        .perform(get("/api/v1/books").param("year", "2018"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.totalElements").value(2));

    // Filter by title
    mockMvc
        .perform(get("/api/v1/books").param("title", "Clean"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data.content").isArray())
        .andExpect(jsonPath("$.data.totalElements").value(1))
        .andExpect(jsonPath("$.data.content[0].title").value("Clean Code"));
  }

  @Test
  void shouldGetBorrowingHistoryByBorrowerNameEndToEnd() throws Exception {
    // Test: End-to-end - Create book, borrow by borrower, then get history by borrower name
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    BorrowRequest borrowRequest = new BorrowRequest(bookId, "John Doe");
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
        .andExpect(status().isOk());

    ReturnRequest returnRequest = new ReturnRequest(bookId);
    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(returnRequest)))
        .andExpect(status().isOk());

    // Get history by borrower name
    mockMvc
        .perform(get("/api/v1/borrowings/borrower/{name}", "John"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].borrowerName").value("John Doe"))
        .andExpect(jsonPath("$.data[0].itemTitle").value("Clean Code"));
  }

  @Test
  void shouldThrowExceptionWhenBorrowingAlreadyBorrowedBookEndToEnd() throws Exception {
    // Test: End-to-end - Create book, borrow it, then try to borrow again (should fail)
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    BorrowRequest borrowRequest = new BorrowRequest(bookId, "John Doe");
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
        .andExpect(status().isOk());

    // Try to borrow the same book again
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(
            jsonPath("$.message")
                .value("Item with ID " + bookId + " is not available for borrowing"));
  }

  @Test
  void shouldThrowExceptionWhenReturningNotBorrowedBookEndToEnd() throws Exception {
    // Test: End-to-end - Create book, try to return it without borrowing (should fail)
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    ReturnRequest returnRequest = new ReturnRequest(bookId);

    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(returnRequest)))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(
            jsonPath("$.message").value("Item with ID " + bookId + " is not currently borrowed"));
  }

  @Test
  void shouldThrowExceptionWhenDeletingBorrowedBookEndToEnd() throws Exception {
    // Test: End-to-end - Create book, borrow it, then try to delete it (should fail)
    MvcResult createResult =
        mockMvc
            .perform(
                post("/api/v1/books")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(bookRequest)))
            .andExpect(status().isCreated())
            .andReturn();

    String createResponse = createResult.getResponse().getContentAsString();
    ApiResponse<BookResponse> apiResponse =
        objectMapper.readValue(
            createResponse,
            objectMapper
                .getTypeFactory()
                .constructParametricType(ApiResponse.class, BookResponse.class));
    Long bookId = apiResponse.data().id();

    BorrowRequest borrowRequest = new BorrowRequest(bookId, "John Doe");
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(borrowRequest)))
        .andExpect(status().isOk());

    mockMvc
        .perform(delete("/api/v1/books/{id}", bookId))
        .andExpect(status().isConflict())
        .andExpect(jsonPath("$.error").value("Conflict"))
        .andExpect(jsonPath("$.message").value("Cannot delete book that is currently borrowed"));
  }
}
