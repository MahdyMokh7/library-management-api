package com.mehdymokhtari.libraryapi.model.entity;

import jakarta.persistence.*;

@Entity
@DiscriminatorValue("AUDIOBOOK")
public abstract class AudioBook extends DigitalItem {
  // No fields - just demonstrates inheritance
}
