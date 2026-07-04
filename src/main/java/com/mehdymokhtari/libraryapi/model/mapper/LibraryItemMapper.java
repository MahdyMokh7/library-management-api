package com.mehdymokhtari.libraryapi.model.mapper;

import com.mehdymokhtari.libraryapi.model.dto.response.BookResponse;
import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;

import java.util.List;

public interface LibraryItemMapper<T extends LibraryItem, R> {

    R toResponse(T entity);

    List<R> toResponseList(List<T> entities);
}