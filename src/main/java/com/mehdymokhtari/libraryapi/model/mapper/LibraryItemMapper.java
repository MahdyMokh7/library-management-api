package com.mehdymokhtari.libraryapi.model.mapper;

import java.util.List;

import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;

public interface LibraryItemMapper<T extends LibraryItem, R> {

  // Converts any LibraryItem subtype (Book, Magazine, EBook) to its response DTO
  R toResponse(T entity);

  // Converts a list of library items to response DTOs
  List<R> toResponseList(List<T> entities);
}
