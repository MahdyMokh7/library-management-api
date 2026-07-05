package com.mehdymokhtari.libraryapi.service.impl;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.mehdymokhtari.libraryapi.exception.*;
import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import com.mehdymokhtari.libraryapi.model.mapper.BorrowingRecordMapper;
import com.mehdymokhtari.libraryapi.repository.BorrowingRecordRepository;
import com.mehdymokhtari.libraryapi.repository.LibraryItemRepository;
import com.mehdymokhtari.libraryapi.service.BorrowingService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BorrowingServiceImpl implements BorrowingService {

  private final BorrowingRecordRepository borrowingRecordRepository;
  private final LibraryItemRepository libraryItemRepository;
  private final BorrowingRecordMapper borrowingRecordMapper;

  @Override
  @Transactional
  public BorrowingRecordResponse borrowItem(BorrowRequest request) {
    log.debug(
        "Processing borrow request for item ID: {}, borrower: {}",
        request.itemId(),
        request.borrowerName());

    LibraryItem item =
        libraryItemRepository
            .findByIdAndDeletedFalse(request.itemId())
            .orElseThrow(() -> new ResourceNotFoundException("LibraryItem", request.itemId()));

    if (!item.isAvailable()) {
      throw new BookNotAvailableException(
          "Item with ID " + request.itemId() + " is not available for borrowing");
    }

    item.borrow();
    libraryItemRepository.save(item);

    BorrowingRecord record = borrowingRecordMapper.toEntity(request, item);
    BorrowingRecord saved = borrowingRecordRepository.save(record);

    log.info(
        "Item borrowed successfully. Item ID: {}, Borrower: {}",
        request.itemId(),
        request.borrowerName());
    return borrowingRecordMapper.toResponse(saved);
  }

  @Override
  @Transactional
  public BorrowingRecordResponse returnItem(ReturnRequest request) {
    log.debug("Processing return request for item ID: {}", request.itemId());

    LibraryItem item =
        libraryItemRepository
            .findByIdAndDeletedFalse(request.itemId())
            .orElseThrow(() -> new ResourceNotFoundException("LibraryItem", request.itemId()));

    if (!item.isBorrowed()) {
      throw new ItemNotBorrowedException(request.itemId());
    }

    BorrowingRecord record =
        borrowingRecordRepository
            .findByItemIdAndStatus(request.itemId(), BorrowingStatus.BORROWED)
            .orElseThrow(
                () ->
                    new BusinessException(
                        "No active borrowing record found for item ID: " + request.itemId()));

    record.markReturned();
    item.returnItem();
    libraryItemRepository.save(item);
    BorrowingRecord saved = borrowingRecordRepository.save(record);

    log.info(
        "Item returned successfully. Item ID: {}, Borrower: {}",
        request.itemId(),
        record.getBorrowerName());
    return borrowingRecordMapper.toResponse(saved);
  }

  @Override
  public List<BorrowingRecordResponse> getBorrowingHistoryByItem(Long itemId) {
    log.debug("Fetching borrowing history for item ID: {}", itemId);

    if (!libraryItemRepository.existsByIdAndDeletedFalse(itemId)) {
      throw new ResourceNotFoundException("LibraryItem", itemId);
    }

    List<BorrowingRecord> records =
        borrowingRecordRepository.findAllByItemIdOrderByBorrowedDateDesc(itemId);
    return borrowingRecordMapper.toResponseList(records);
  }

  @Override
  public List<BorrowingRecordResponse> getBorrowingHistoryByBorrower(String borrowerName) {
    log.debug("Fetching borrowing history for borrower: {}", borrowerName);

    List<BorrowingRecord> records =
        borrowingRecordRepository.findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(
            borrowerName);
    return borrowingRecordMapper.toResponseList(records);
  }
}
