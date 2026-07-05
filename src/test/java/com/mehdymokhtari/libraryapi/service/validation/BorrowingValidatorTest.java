package com.mehdymokhtari.libraryapi.service.validation;

import com.mehdymokhtari.libraryapi.exception.BookNotAvailableException;
import com.mehdymokhtari.libraryapi.exception.BusinessException;
import com.mehdymokhtari.libraryapi.exception.InvalidOperationException;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import com.mehdymokhtari.libraryapi.repository.BorrowingRecordRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BorrowingValidatorTest {

    @Mock
    private BorrowingRecordRepository borrowingRecordRepository;

    @InjectMocks
    private BorrowingValidator borrowingValidator;

    @Test
    void testValidateBorrowerName_Success() {
        assertDoesNotThrow(() -> borrowingValidator.validateBorrowerName("John Doe"));
    }

    @Test
    void testValidateBorrowerName_NullOrEmpty() {
        assertThrows(BusinessException.class, () -> borrowingValidator.validateBorrowerName(null));
        assertThrows(BusinessException.class, () -> borrowingValidator.validateBorrowerName("   "));
    }

    @Test
    void testValidateBorrowerName_TooLong() {
        String longName = "a".repeat(101);
        assertThrows(BusinessException.class, () -> borrowingValidator.validateBorrowerName(longName));
    }

    @Test
    void testValidateItemAvailable_Success() {
        LibraryItem item = mock(LibraryItem.class);
        when(item.isAvailable()).thenReturn(true);
        assertDoesNotThrow(() -> borrowingValidator.validateItemAvailable(item));
    }

    @Test
    void testValidateItemAvailable_ThrowsException() {
        LibraryItem item = mock(LibraryItem.class);
        when(item.getId()).thenReturn(1L);
        when(item.isAvailable()).thenReturn(false);
        assertThrows(BookNotAvailableException.class, () -> borrowingValidator.validateItemAvailable(item));
    }

    @Test
    void testValidateItemBorrowed_Success() {
        LibraryItem item = mock(LibraryItem.class);
        when(item.isBorrowed()).thenReturn(true);
        assertDoesNotThrow(() -> borrowingValidator.validateItemBorrowed(item));
    }

    @Test
    void testValidateItemBorrowed_ThrowsException() {
        LibraryItem item = mock(LibraryItem.class);
        when(item.getId()).thenReturn(1L);
        when(item.isBorrowed()).thenReturn(false);
        assertThrows(InvalidOperationException.class, () -> borrowingValidator.validateItemBorrowed(item));
    }

    @Test
    void testValidateAndGetActiveBorrowing_Success() {
        BorrowingRecord record = new BorrowingRecord();
        when(borrowingRecordRepository.findByItemIdAndStatus(1L, BorrowingStatus.BORROWED))
                .thenReturn(Optional.of(record));

        assertEquals(record, borrowingValidator.validateAndGetActiveBorrowing(1L));
    }

    @Test
    void testValidateAndGetActiveBorrowing_ThrowsException() {
        when(borrowingRecordRepository.findByItemIdAndStatus(1L, BorrowingStatus.BORROWED))
                .thenReturn(Optional.empty());

        assertThrows(BusinessException.class, () -> borrowingValidator.validateAndGetActiveBorrowing(1L));
    }

    @Test
    void testValidateItemNotAlreadyBorrowed_Success() {
        when(borrowingRecordRepository.isItemCurrentlyBorrowed(1L)).thenReturn(false);
        assertDoesNotThrow(() -> borrowingValidator.validateItemNotAlreadyBorrowed(1L));
    }

    @Test
    void testValidateItemNotAlreadyBorrowed_ThrowsException() {
        when(borrowingRecordRepository.isItemCurrentlyBorrowed(1L)).thenReturn(true);
        assertThrows(BookNotAvailableException.class, () -> borrowingValidator.validateItemNotAlreadyBorrowed(1L));
    }
}