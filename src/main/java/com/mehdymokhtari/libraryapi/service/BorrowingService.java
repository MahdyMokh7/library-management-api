package com.mehdymokhtari.libraryapi.service;

import java.util.List;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;

public interface BorrowingService {

  BorrowingRecordResponse borrowItem(BorrowRequest request);

  BorrowingRecordResponse returnItem(ReturnRequest request);

  List<BorrowingRecordResponse> getBorrowingHistoryByItem(Long itemId);

  List<BorrowingRecordResponse> getBorrowingHistoryByBorrower(String borrowerName);
}
