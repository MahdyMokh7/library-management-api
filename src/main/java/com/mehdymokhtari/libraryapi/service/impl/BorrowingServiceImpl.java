package com.mehdymokhtari.libraryapi.service.impl;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.ReturnRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import com.mehdymokhtari.libraryapi.repository.BookRepository;
import com.mehdymokhtari.libraryapi.repository.BorrowingRecordRepository;
import com.mehdymokhtari.libraryapi.service.BorrowingService;
import com.mehdymokhtari.libraryapi.service.validation.BookValidationService;
import com.mehdymokhtari.libraryapi.model.mapper.BorrowingRecordMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BorrowingServiceImpl implements BorrowingService {

    private final BorrowingRecordRepository borrowingRecordRepository;
    private final BookRepository bookRepository;
    private final BorrowingRecordMapper borrowingRecordMapper;
    private final BookValidationService bookValidationService;

    @Override
    @Transactional
    public BorrowingRecordResponse borrowBook(BorrowRequest request) {
        log.debug("Processing borrow request for book ID: {}, borrower: {}",
                request.bookId(), request.borrowerName());

        Book book = bookValidationService.validateAndGetBook(request.bookId());

        if (!book.isAvailable()) {
            throw new BusinessException("Book with ID " + request.bookId() + " is not available for borrowing");
        }

        BorrowingRecord record = borrowingRecordMapper.toEntity(request, book);
        book.borrow();
        bookRepository.save(book);
        BorrowingRecord saved = borrowingRecordRepository.save(record);

        log.info("Book borrowed successfully. Book ID: {}, Borrower: {}",
                request.bookId(), request.borrowerName());
        return borrowingRecordMapper.toResponse(saved);
    }

    @Override
    @Transactional
    public BorrowingRecordResponse returnBook(ReturnRequest request) {
        log.debug("Processing return request for book ID: {}", request.bookId());

        Book book = bookValidationService.validateAndGetBook(request.bookId());

        if (!book.isBorrowed()) {
            throw new BusinessException("Book with ID " + request.bookId() + " is not currently borrowed");
        }

        BorrowingRecord record = borrowingRecordRepository
                .findByBookIdAndStatus(request.bookId(), BorrowingStatus.BORROWED)
                .orElseThrow(() -> new BusinessException(
                        "No active borrowing record found for book ID: " + request.bookId()
                ));

        record.markReturned();
        book.returnBook();
        bookRepository.save(book);
        BorrowingRecord saved = borrowingRecordRepository.save(record);

        log.info("Book returned successfully. Book ID: {}, Borrower: {}",
                request.bookId(), record.getBorrowerName());
        return borrowingRecordMapper.toResponse(saved);
    }

    @Override
    public List<BorrowingRecordResponse> getBorrowingHistoryByBook(Long bookId) {
        log.debug("Fetching borrowing history for book ID: {}", bookId);

        if (!bookRepository.existsByIdAndIsDeletedFalse(bookId)) {
            throw new BusinessException("Book with ID " + bookId + " does not exist");
        }

        List<BorrowingRecord> records = borrowingRecordRepository
                .findAllByBookIdOrderByBorrowedDateDesc(bookId);
        return borrowingRecordMapper.toResponseList(records);
    }

    @Override
    public List<BorrowingRecordResponse> getBorrowingHistoryByBorrower(String borrowerName) {
        log.debug("Fetching borrowing history for borrower: {}", borrowerName);

        List<BorrowingRecord> records = borrowingRecordRepository
                .findByBorrowerNameContainingIgnoreCaseOrderByBorrowedDateDesc(borrowerName);
        return borrowingRecordMapper.toResponseList(records);
    }
}