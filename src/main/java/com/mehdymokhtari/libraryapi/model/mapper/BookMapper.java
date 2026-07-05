package com.mehdymokhtari.libraryapi.model.mapper;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import com.mehdymokhtari.libraryapi.model.dto.request.BookRequest;
import com.mehdymokhtari.libraryapi.model.dto.request.BookUpdateRequest;
import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.entity.Book;

@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookMapper extends LibraryItemMapper<Book, BookResponse> {

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "AVAILABLE")
  @Mapping(target = "isDeleted", constant = "false")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "itemType", ignore = true)
  Book toEntity(BookRequest request);

  BookResponse toResponse(Book entity);

  List<BookResponse> toResponseList(List<Book> entities);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isbn", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "isDeleted", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "itemType", ignore = true)
  void updateEntity(BookUpdateRequest request, @MappingTarget Book entity);
}
