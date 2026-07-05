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
@DiscriminatorValue("EBOOK")
public abstract class Ebook extends DigitalItem {

  @Column(nullable = false)
  private String author;

  @Column(unique = true, nullable = false, length = 17)
  private String isbn;

  @Column(nullable = false)
  private Integer edition;

  @Column(nullable = false)
  private String downloadLink;
}
