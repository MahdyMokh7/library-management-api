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
@DiscriminatorValue("AUDIOBOOK")
public abstract class AudioBook extends DigitalItem {

  @Column(nullable = false)
  private String author;

  @Column(nullable = false)
  private String narrator;

  @Column(nullable = false)
  private Integer duration;

  @Column(unique = true, nullable = false, length = 17)
  private String isbn;
}
