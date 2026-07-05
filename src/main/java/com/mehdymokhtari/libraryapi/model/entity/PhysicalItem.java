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
public abstract class PhysicalItem extends LibraryItem {

  @Override
  public abstract void borrow();

  @Override
  public abstract void returnItem();

  @Override
  public abstract boolean isAvailable();

  @Override
  public abstract boolean isBorrowed();
}
