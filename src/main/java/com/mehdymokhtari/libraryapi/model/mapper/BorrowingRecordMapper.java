package com.mehdymokhtari.libraryapi.model.mapper;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.time.LocalDate;
import java.util.List;

@Mapper(componentModel = "spring", imports = {LocalDate.class, BorrowingStatus.class})
public interface BorrowingRecordMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "book", source = "book")
    @Mapping(target = "borrowerName", source = "request.borrowerName")
    @Mapping(target = "borrowedDate", expression = "java(LocalDate.now())")
    @Mapping(target = "returnDate", ignore = true)
    @Mapping(target = "status", constant = "BORROWED")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "version", ignore = true)
    BorrowingRecord toEntity(BorrowRequest request, Book book);

    @Mapping(target = "bookId", source = "book.id")
    @Mapping(target = "bookTitle", source = "book.title")
    @Mapping(target = "borrowerName", source = "borrowerName")
    @Mapping(target = "borrowedDate", source = "borrowedDate")
    @Mapping(target = "returnDate", source = "returnDate")
    @Mapping(target = "status", source = "status")
    @Mapping(target = "createdAt", source = "createdAt")
    @Mapping(target = "updatedAt", source = "updatedAt")
    BorrowingRecordResponse toResponse(BorrowingRecord entity);

    List<BorrowingRecordResponse> toResponseList(List<BorrowingRecord> entities);
}