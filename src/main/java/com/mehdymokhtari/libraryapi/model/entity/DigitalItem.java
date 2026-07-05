package com.mehdymokhtari.libraryapi.model.entity;

import jakarta.persistence.*;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Inheritance(strategy = InheritanceType.JOINED)
public abstract class DigitalItem extends LibraryItem {

  @Column(nullable = false)
  private String format;

  @Column(nullable = false)
  private Long fileSize;

  @Override
  public void borrow() {
    // No-op for digital items
  }

  @Override
  public void returnItem() {
    // No-op for digital items
  }

  @Override
  public boolean isAvailable() {
    return !this.isDeleted();
  }

  @Override
  public boolean isBorrowed() {
    return false;
  }
}
