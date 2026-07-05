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
@DiscriminatorValue("MAGAZINE")
public abstract class Magazine extends PhysicalItem {

  @Column(nullable = false)
  private String issueNumber;

  @Column(nullable = false)
  private String editor;

  @Column(unique = true, nullable = false)
  private String issn;
}
