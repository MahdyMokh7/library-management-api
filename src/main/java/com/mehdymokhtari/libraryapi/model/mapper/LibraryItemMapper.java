package com.mehdymokhtari.libraryapi.model.mapper;

import java.util.List;

import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;

public interface LibraryItemMapper<T extends LibraryItem, R> {

  R toResponse(T entity);

  List<R> toResponseList(List<T> entities);
}
