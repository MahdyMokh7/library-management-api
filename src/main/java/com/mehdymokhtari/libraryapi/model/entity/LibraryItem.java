package com.mehdymokhtari.libraryapi.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.mehdymokhtari.libraryapi.model.enums.ItemType;

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
@DiscriminatorColumn(name = "item_type", discriminatorType = DiscriminatorType.STRING)
@EntityListeners(AuditingEntityListener.class)
public abstract class LibraryItem {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false)
  private String title;

  @Column(nullable = false)
  private Integer publicationYear;

  @Column(nullable = false)
  private boolean deleted;

  @Enumerated(EnumType.STRING)
  @Column(name = "item_type", insertable = false, updatable = false)
  private ItemType itemType;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;

  @Version private Long version;

  // ============================================================
  // ABSTRACT BUSINESS METHODS - Each subclass implements its own
  // ============================================================

  /**
   * Marks this item as borrowed. Physical items: status → BORROWED Digital items: can either be
   * "borrowed" via license or remain always available. For simplicity, all items implement this.
   */
  public abstract void borrow();

  /**
   * Marks this item as returned. Physical items: status → AVAILABLE Digital items: may or may not
   * need this.
   */
  public abstract void returnItem();

  /**
   * Checks if this item is available. Physical items: checks status and deleted flag. Digital
   * items: always available (true) unless deleted.
   */
  public abstract boolean isAvailable();

  /**
   * Checks if this item is borrowed. Physical items: checks status. Digital items: always false
   * (unless license tracking is implemented).
   */
  public abstract boolean isBorrowed();

  /** Common for all subclasses: soft delete */
  public void delete() {
    this.deleted = true;
  }

  /** Common for all subclasses: restore from soft delete */
  public void restore() {
    this.deleted = false;
  }

  /** Common for all subclasses: check if deleted */
  public boolean isDeleted() {
    return this.deleted;
  }
}
