package com.mehdymokhtari.libraryapi.service;

import java.util.List;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;

public interface BorrowingService {

  BorrowingRecordResponse borrowBook(BorrowRequest request);

  BorrowingRecordResponse returnBook(ReturnRequest request);

  List<BorrowingRecordResponse> getBorrowingHistoryByBook(Long bookId);

  List<BorrowingRecordResponse> getBorrowingHistoryByBorrower(String borrowerName);
}
