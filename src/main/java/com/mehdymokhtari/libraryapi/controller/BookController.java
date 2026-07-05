package com.mehdymokhtari.libraryapi.controller;

import static org.springframework.http.HttpStatus.*;

import jakarta.validation.Valid;

import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mehdymokhtari.libraryapi.filter.BookFilter;
import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.ApiResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.PagedResponse;
import com.mehdymokhtari.libraryapi.service.BookService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/books")
@RequiredArgsConstructor
@Tag(name = "Book Management", description = "Endpoints for managing books")
public class BookController {

  private final BookService bookService;

  @PostMapping
  @Operation(summary = "Create a new book")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "201",
            description = "Book created successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "ISBN already exists")
      })
  public ResponseEntity<ApiResponse<BookResponse>> createBook(
      @Valid @RequestBody BookRequest request) {
    BookResponse response = bookService.createBook(request);
    return ResponseEntity.status(CREATED)
        .body(ApiResponse.success("Book created successfully", response));
  }

  @GetMapping
  @Operation(summary = "Get all books with pagination, sorting and filtering")
  public ResponseEntity<ApiResponse<PagedResponse<BookResponse>>> getAllBooks(
      @Parameter(description = "Filter by title") @RequestParam(required = false) String title,
      @Parameter(description = "Filter by author") @RequestParam(required = false) String author,
      @Parameter(description = "Filter by publication year") @RequestParam(required = false)
          Integer year,
      @Parameter(description = "Filter by status (AVAILABLE/BORROWED)")
          @RequestParam(required = false)
          String status,
      @PageableDefault(size = 20, sort = "id", direction = Sort.Direction.ASC) Pageable pageable) {

    BookFilter filter =
        BookFilter.builder()
            .title(title)
            .author(author)
            .publicationYear(year)
            .status(
                status != null
                    ? com.mehdymokhtari.libraryapi.model.enums.BookStatus.valueOf(
                        status.toUpperCase())
                    : null)
            .build();

    PagedResponse<BookResponse> response = bookService.getAllBooks(filter, pageable);
    return ResponseEntity.ok(ApiResponse.success("Books retrieved successfully", response));
  }

  @GetMapping("/{id}")
  @Operation(summary = "Get book by ID")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Book not found")
      })
  public ResponseEntity<ApiResponse<BookResponse>> getBookById(@PathVariable Long id) {
    BookResponse response = bookService.getBookById(id);
    return ResponseEntity.ok(ApiResponse.success("Book retrieved successfully", response));
  }

  @PutMapping("/{id}")
  @Operation(summary = "Update book information")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book updated successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Book not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "400",
            description = "Invalid input")
      })
  public ResponseEntity<ApiResponse<BookResponse>> updateBook(
      @PathVariable Long id, @Valid @RequestBody BookUpdateRequest request) {
    BookResponse response = bookService.updateBook(id, request);
    return ResponseEntity.ok(ApiResponse.success("Book updated successfully", response));
  }

  @DeleteMapping("/{id}")
  @Operation(summary = "Delete a book (soft delete)")
  @ApiResponses(
      value = {
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "200",
            description = "Book deleted successfully"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "404",
            description = "Book not found"),
        @io.swagger.v3.oas.annotations.responses.ApiResponse(
            responseCode = "409",
            description = "Book is currently borrowed")
      })
  public ResponseEntity<ApiResponse<Void>> deleteBook(@PathVariable Long id) {
    bookService.deleteBook(id);
    return ResponseEntity.ok(ApiResponse.success("Book deleted successfully"));
  }
}
