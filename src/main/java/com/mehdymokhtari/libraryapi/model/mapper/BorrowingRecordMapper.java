package com.mehdymokhtari.libraryapi.model.mapper;

import java.time.LocalDate;
import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.mehdymokhtari.libraryapi.model.dto.request.BorrowRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BorrowingRecordResponse;
import com.mehdymokhtari.libraryapi.model.entity.BorrowingRecord;
import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;
import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;

@Mapper(
    componentModel = "spring",
    imports = {LocalDate.class, BorrowingStatus.class})
public interface BorrowingRecordMapper {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "item", source = "item")
  @Mapping(target = "borrowerName", source = "request.borrowerName")
  @Mapping(target = "borrowedDate", expression = "java(LocalDate.now())")
  @Mapping(target = "returnDate", ignore = true)
  @Mapping(target = "status", constant = "BORROWED")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  BorrowingRecord toEntity(BorrowRequest request, LibraryItem item);

  @Mapping(target = "itemId", source = "item.id")
  @Mapping(target = "itemTitle", source = "item.title")
  @Mapping(target = "borrowerName", source = "borrowerName")
  @Mapping(target = "borrowedDate", source = "borrowedDate")
  @Mapping(target = "returnDate", source = "returnDate")
  @Mapping(target = "status", source = "status")
  @Mapping(target = "createdAt", source = "createdAt")
  @Mapping(target = "updatedAt", source = "updatedAt")
  BorrowingRecordResponse toResponse(BorrowingRecord entity);

  List<BorrowingRecordResponse> toResponseList(List<BorrowingRecord> entities);
}
