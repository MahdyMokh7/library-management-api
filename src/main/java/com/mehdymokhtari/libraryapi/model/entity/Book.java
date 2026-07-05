package com.mehdymokhtari.libraryapi.model.entity;

import jakarta.persistence.*;

import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "books")
@DiscriminatorValue("BOOK")
public class Book extends PhysicalItem {

  @Column(nullable = false)
  private String author;

  @Column(unique = true, nullable = false, length = 17)
  private String isbn;

  @Column(nullable = false)
  private Integer edition;

  private String publisher;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BookStatus status;

  @Override
  public void borrow() {
    this.status = BookStatus.BORROWED;
  }

  @Override
  public void returnItem() {
    this.status = BookStatus.AVAILABLE;
  }

  @Override
  public boolean isAvailable() {
    return this.status == BookStatus.AVAILABLE && !this.isDeleted();
  }

  @Override
  public boolean isBorrowed() {
    return this.status == BookStatus.BORROWED && !this.isDeleted();
  }
}
