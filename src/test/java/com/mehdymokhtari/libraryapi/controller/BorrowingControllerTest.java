package com.mehdymokhtari.libraryapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import com.mehdymokhtari.libraryapi.exception.BookNotAvailableException;
import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException;
import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;

class BorrowingControllerTest extends BaseControllerTest {

  private BorrowRequest borrowRequest;
  private ReturnRequest returnRequest;
  private BorrowingRecordResponse borrowingResponse;
  private BorrowingRecordResponse returnedResponse;

  @BeforeEach
  void setUp() {
    borrowRequest = new BorrowRequest(1L, "John Doe");
    returnRequest = new ReturnRequest(1L);

    borrowingResponse =
        new BorrowingRecordResponse(
            1L,
            1L,
            "Clean Code",
            "John Doe",
            LocalDate.now(),
            null,
            BorrowingStatus.BORROWED,
            null,
            null);

    returnedResponse =
        new BorrowingRecordResponse(
            1L,
            1L,
            "Clean Code",
            "John Doe",
            LocalDate.now().minusDays(5),
            LocalDate.now(),
            BorrowingStatus.RETURNED,
            null,
            null);
  }

  @Test
  void shouldBorrowItemAndReturn200() throws Exception {
    // Arrange
    when(borrowingService.borrowItem(any(BorrowRequest.class))).thenReturn(borrowingResponse);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(borrowRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Book borrowed successfully"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.itemId").value(1))
        .andExpect(jsonPath("$.data.itemTitle").value("Clean Code"))
        .andExpect(jsonPath("$.data.borrowerName").value("John Doe"))
        .andExpect(jsonPath("$.data.status").value("BORROWED"));
  }

  @Test
  void shouldReturn400WhenBorrowRequestHasInvalidData() throws Exception {
    // Arrange
    BorrowRequest invalidRequest = new BorrowRequest(null, "");

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn404WhenBorrowingNonExistingItem() throws Exception {
    // Arrange
    BorrowRequest invalidRequest = new BorrowRequest(999L, "John Doe");

    when(borrowingService.borrowItem(any(BorrowRequest.class)))
        .thenThrow(new ResourceNotFoundException("LibraryItem", 999L));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn409WhenBorrowingUnavailableItem() throws Exception {
    // Arrange
    when(borrowingService.borrowItem(any(BorrowRequest.class)))
        .thenThrow(new BookNotAvailableException("Item with ID 1 is not available for borrowing"));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/borrow")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(borrowRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldReturnItemAndReturn200() throws Exception {
    // Arrange
    when(borrowingService.returnItem(any(ReturnRequest.class))).thenReturn(returnedResponse);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(returnRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Book returned successfully"))
        .andExpect(jsonPath("$.data.id").value(1))
        .andExpect(jsonPath("$.data.itemId").value(1))
        .andExpect(jsonPath("$.data.borrowerName").value("John Doe"))
        .andExpect(jsonPath("$.data.status").value("RETURNED"))
        .andExpect(jsonPath("$.data.returnDate").exists());
  }

  @Test
  void shouldReturn400WhenReturnRequestHasInvalidData() throws Exception {
    // Arrange
    ReturnRequest invalidRequest = new ReturnRequest(null);

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
        .andExpect(status().isBadRequest());
  }

  @Test
  void shouldReturn404WhenReturningNonExistingItem() throws Exception {
    // Arrange
    ReturnRequest invalidRequest = new ReturnRequest(999L);

    when(borrowingService.returnItem(any(ReturnRequest.class)))
        .thenThrow(new ResourceNotFoundException("LibraryItem", 999L));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(invalidRequest)))
        .andExpect(status().isNotFound());
  }

  @Test
  void shouldReturn409WhenReturningNotBorrowedItem() throws Exception {
    // Arrange
    when(borrowingService.returnItem(any(ReturnRequest.class)))
        .thenThrow(new BusinessException("Item with ID 1 is not currently borrowed"));

    // Act & Assert
    mockMvc
        .perform(
            post("/api/v1/borrowings/return")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(returnRequest)))
        .andExpect(status().isConflict());
  }

  @Test
  void shouldGetBorrowingHistoryByItemAndReturn200() throws Exception {
    // Arrange
    List<BorrowingRecordResponse> history = List.of(borrowingResponse, returnedResponse);

    when(borrowingService.getBorrowingHistoryByItem(1L)).thenReturn(history);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/borrowings/book/1"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Borrowing history retrieved successfully"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].itemId").value(1))
        .andExpect(jsonPath("$.data[0].status").value("BORROWED"))
        .andExpect(jsonPath("$.data[1].status").value("RETURNED"));
  }

  @Test
  void shouldReturn404WhenGettingHistoryForNonExistingItem() throws Exception {
    // Arrange
    when(borrowingService.getBorrowingHistoryByItem(999L))
        .thenThrow(new ResourceNotFoundException("LibraryItem", 999L));

    // Act & Assert
    mockMvc.perform(get("/api/v1/borrowings/book/999")).andExpect(status().isNotFound());
  }

  @Test
  void shouldGetBorrowingHistoryByBorrowerAndReturn200() throws Exception {
    // Arrange
    List<BorrowingRecordResponse> history = List.of(borrowingResponse, returnedResponse);

    when(borrowingService.getBorrowingHistoryByBorrower("John")).thenReturn(history);

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/borrowings/borrower/John"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.message").value("Borrowing history retrieved successfully"))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(2))
        .andExpect(jsonPath("$.data[0].borrowerName").value("John Doe"));
  }

  @Test
  void shouldReturnEmptyListWhenBorrowerHasNoHistory() throws Exception {
    // Arrange
    when(borrowingService.getBorrowingHistoryByBorrower("Unknown")).thenReturn(List.of());

    // Act & Assert
    mockMvc
        .perform(get("/api/v1/borrowings/borrower/Unknown"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.success").value(true))
        .andExpect(jsonPath("$.data").isArray())
        .andExpect(jsonPath("$.data.length()").value(0));
  }
}
