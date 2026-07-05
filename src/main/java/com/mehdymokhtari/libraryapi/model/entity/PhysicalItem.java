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
@Table(name = "physical_items")
@Inheritance(strategy = InheritanceType.JOINED)
@PrimaryKeyJoinColumn(name = "item_id")
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
