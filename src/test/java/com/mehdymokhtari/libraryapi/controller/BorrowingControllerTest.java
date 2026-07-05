package com.mehdymokhtari.libraryapi.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.time.LocalDate;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import com.mehdymokhtari.libraryapi.service.BorrowingService;

@WebMvcTest(BorrowingController.class)
class BorrowingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BorrowingService borrowingService;

    private BorrowRequest borrowRequest;
    private ReturnRequest returnRequest;
    private BorrowingRecordResponse borrowingResponse;
    private BorrowingRecordResponse returnedResponse;

    @BeforeEach
    void setUp() {
        borrowRequest = new BorrowRequest(1L, "John Doe");
        returnRequest = new ReturnRequest(1L);

        borrowingResponse = new BorrowingRecordResponse(
                1L,
                1L,
                "Clean Code",
                "John Doe",
                LocalDate.now(),
                null,
                BorrowingStatus.BORROWED,
                null,
                null);

        returnedResponse = new BorrowingRecordResponse(
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
        // Test: POST /api/v1/borrowings/borrow returns 200 OK with borrowing record
        when(borrowingService.borrowItem(any(BorrowRequest.class))).thenReturn(borrowingResponse);

        mockMvc.perform(post("/api/v1/borrowings/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowRequest)))
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
        // Test: POST /api/v1/borrowings/borrow with invalid data returns 400 Bad Request
        BorrowRequest invalidRequest = new BorrowRequest(null, "");

        mockMvc.perform(post("/api/v1/borrowings/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenBorrowingNonExistingItem() throws Exception {
        // Test: POST /api/v1/borrowings/borrow for non-existing item returns 404 Not Found
        BorrowRequest invalidRequest = new BorrowRequest(999L, "John Doe");

        when(borrowingService.borrowItem(any(BorrowRequest.class)))
                .thenThrow(new com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException("LibraryItem", 999L));

        mockMvc.perform(post("/api/v1/borrowings/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenBorrowingUnavailableItem() throws Exception {
        // Test: POST /api/v1/borrowings/borrow for unavailable item returns 409 Conflict
        when(borrowingService.borrowItem(any(BorrowRequest.class)))
                .thenThrow(new com.mehdymokhtari.libraryapi.exception.BookNotAvailableException(
                        "Item with ID 1 is not available for borrowing"));

        mockMvc.perform(post("/api/v1/borrowings/borrow")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(borrowRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldReturnItemAndReturn200() throws Exception {
        // Test: POST /api/v1/borrowings/return returns 200 OK with returned record
        when(borrowingService.returnItem(any(ReturnRequest.class))).thenReturn(returnedResponse);

        mockMvc.perform(post("/api/v1/borrowings/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnRequest)))
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
        // Test: POST /api/v1/borrowings/return with invalid data returns 400 Bad Request
        ReturnRequest invalidRequest = new ReturnRequest(null);

        mockMvc.perform(post("/api/v1/borrowings/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturn404WhenReturningNonExistingItem() throws Exception {
        // Test: POST /api/v1/borrowings/return for non-existing item returns 404 Not Found
        ReturnRequest invalidRequest = new ReturnRequest(999L);

        when(borrowingService.returnItem(any(ReturnRequest.class)))
                .thenThrow(new com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException("LibraryItem", 999L));

        mockMvc.perform(post("/api/v1/borrowings/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldReturn409WhenReturningNotBorrowedItem() throws Exception {
        // Test: POST /api/v1/borrowings/return for not borrowed item returns 409 Conflict
        when(borrowingService.returnItem(any(ReturnRequest.class)))
                .thenThrow(new com.mehdymokhtari.libraryapi.exception.BusinessException(
                        "Item with ID 1 is not currently borrowed"));

        mockMvc.perform(post("/api/v1/borrowings/return")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(returnRequest)))
                .andExpect(status().isConflict());
    }

    @Test
    void shouldGetBorrowingHistoryByItemAndReturn200() throws Exception {
        // Test: GET /api/v1/borrowings/book/{bookId} returns 200 OK with history
        List<BorrowingRecordResponse> history = List.of(borrowingResponse, returnedResponse);

        when(borrowingService.getBorrowingHistoryByItem(1L)).thenReturn(history);

        mockMvc.perform(get("/api/v1/borrowings/book/1"))
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
        // Test: GET /api/v1/borrowings/book/{bookId} for non-existing item returns 404 Not Found
        when(borrowingService.getBorrowingHistoryByItem(999L))
                .thenThrow(new com.mehdymokhtari.libraryapi.exception.ResourceNotFoundException("LibraryItem", 999L));

        mockMvc.perform(get("/api/v1/borrowings/book/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void shouldGetBorrowingHistoryByBorrowerAndReturn200() throws Exception {
        // Test: GET /api/v1/borrowings/borrower/{name} returns 200 OK with history
        List<BorrowingRecordResponse> history = List.of(borrowingResponse, returnedResponse);

        when(borrowingService.getBorrowingHistoryByBorrower("John")).thenReturn(history);

        mockMvc.perform(get("/api/v1/borrowings/borrower/John"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.message").value("Borrowing history retrieved successfully"))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(2))
                .andExpect(jsonPath("$.data[0].borrowerName").value("John Doe"));
    }

    @Test
    void shouldReturnEmptyListWhenBorrowerHasNoHistory() throws Exception {
        // Test: GET /api/v1/borrowings/borrower/{name} for borrower with no history returns empty list
        when(borrowingService.getBorrowingHistoryByBorrower("Unknown")).thenReturn(List.of());

        mockMvc.perform(get("/api/v1/borrowings/borrower/Unknown"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.success").value(true))
                .andExpect(jsonPath("$.data").isArray())
                .andExpect(jsonPath("$.data.length()").value(0));
    }
}