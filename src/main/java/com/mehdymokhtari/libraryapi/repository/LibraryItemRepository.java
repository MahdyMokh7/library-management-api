package com.mehdymokhtari.libraryapi.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.mehdymokhtari.libraryapi.model.entity.LibraryItem;

@Repository
public interface LibraryItemRepository
    extends JpaRepository<LibraryItem, Long>, JpaSpecificationExecutor<LibraryItem> {

  boolean existsByIdAndDeletedFalse(Long id);

  Optional<LibraryItem> findByIdAndDeletedFalse(Long id);
}
