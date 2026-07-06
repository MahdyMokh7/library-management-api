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

// MapsStruct implements the class automatically
// Separation of Concerns - Entities are for database, DTOs are for API. Mappers handle the
// conversion
@Mapper(
    componentModel = "spring",
    nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface BookMapper extends LibraryItemMapper<Book, BookResponse> {

  // converts BookRequest DTO -> Book Entity  (Create a new Book entity from API request)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "status", constant = "AVAILABLE")
  @Mapping(target = "deleted", constant = "false")
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "itemType", ignore = true)
  Book toEntity(BookRequest request);

  // converts Book Entity -> BookResponse DTO  (Convert entity to API response)
  @Override
  BookResponse toResponse(Book entity);

  // converts List<Book> Entity -> List<BookResponse> DTO  (Convert multiple entities to responses)
  @Override
  List<BookResponse> toResponseList(List<Book> entities);

  // converts BookUpdateRequest DTO → Book Entity  (Update existing entity with request data)
  @Mapping(target = "id", ignore = true)
  @Mapping(target = "isbn", ignore = true)
  @Mapping(target = "status", ignore = true)
  @Mapping(target = "deleted", ignore = true)
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "updatedAt", ignore = true)
  @Mapping(target = "version", ignore = true)
  @Mapping(target = "itemType", ignore = true)
  void updateEntity(BookUpdateRequest request, @MappingTarget Book entity);
}
