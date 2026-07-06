package com.mehdymokhtari.libraryapi.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.ApiResponse;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.service.BorrowingService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/borrowings")
@RequiredArgsConstructor
@Tag(
    name = "Borrowing Management",
    description = "Endpoints for managing item borrowing and returns")
public class BorrowingController {

  private final BorrowingService borrowingService;

  @PostMapping("/borrow")
  @Operation(summary = "Borrow a book")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Book borrowed successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "400",
        description = "Invalid input"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Book not found"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Book is not available")
  })
  public ResponseEntity<ApiResponse<BorrowingRecordResponse>> borrowItem(
      @Valid @RequestBody BorrowRequest request) {
    BorrowingRecordResponse response = borrowingService.borrowItem(request);
    return ResponseEntity.ok(ApiResponse.success("Book borrowed successfully", response));
  }

  @PostMapping("/return")
  @Operation(summary = "Return a book")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "Book returned successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Book not found"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "409",
        description = "Book is not currently borrowed")
  })
  public ResponseEntity<ApiResponse<BorrowingRecordResponse>> returnItem(
      @Valid @RequestBody ReturnRequest request) {
    BorrowingRecordResponse response = borrowingService.returnItem(request);
    return ResponseEntity.ok(ApiResponse.success("Book returned successfully", response));
  }

  @GetMapping("/book/{bookId}")
  @Operation(summary = "Get borrowing history by book ID")
  @ApiResponses({
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "200",
        description = "History retrieved successfully"),
    @io.swagger.v3.oas.annotations.responses.ApiResponse(
        responseCode = "404",
        description = "Book not found")
  })
  public ResponseEntity<ApiResponse<List<BorrowingRecordResponse>>> getBorrowingHistoryByBook(
      @PathVariable Long bookId) {
    List<BorrowingRecordResponse> response = borrowingService.getBorrowingHistoryByItem(bookId);
    return ResponseEntity.ok(
        ApiResponse.success("Borrowing history retrieved successfully", response));
  }

  @GetMapping("/borrower/{name}")
  @Operation(summary = "Get borrowing history by borrower name")
  public ResponseEntity<ApiResponse<List<BorrowingRecordResponse>>> getBorrowingHistoryByBorrower(
      @PathVariable String name) {
    List<BorrowingRecordResponse> response = borrowingService.getBorrowingHistoryByBorrower(name);
    return ResponseEntity.ok(
        ApiResponse.success("Borrowing history retrieved successfully", response));
  }
}
