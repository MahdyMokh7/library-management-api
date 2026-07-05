package com.mehdymokhtari.libraryapi.model.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("EBOOK")
public abstract class Ebook extends DigitalItem {

  // No fields - just demonstrates inheritance
}
