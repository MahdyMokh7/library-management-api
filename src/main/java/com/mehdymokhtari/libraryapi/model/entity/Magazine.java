package com.mehdymokhtari.libraryapi.model.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("MAGAZINE")
public abstract class Magazine extends PhysicalItem {

  // No fields - just demonstrates inheritance
}
