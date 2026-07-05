package com.mehdymokhtari.libraryapi.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.*;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.mehdymokhtari.libraryapi.model.enums.BorrowingStatus;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@NoArgsConstructor
@SuperBuilder
@Entity
@Table(name = "borrowing_records")
@EntityListeners(AuditingEntityListener.class)
public class BorrowingRecord {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "book_id", nullable = false)
  private Book book;

  @Column(name = "borrower_name", nullable = false, length = 100)
  private String borrowerName;

  @Column(name = "borrowed_date", nullable = false)
  private LocalDate borrowedDate;

  @Column(name = "return_date")
  private LocalDate returnDate;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private BorrowingStatus status;

  @CreatedDate
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate private LocalDateTime updatedAt;

  @Version private Long version;

  public void markReturned() {
    this.returnDate = LocalDate.now();
    this.status = BorrowingStatus.RETURNED;
  }

  public boolean isActive() {
    return this.status == BorrowingStatus.BORROWED;
  }
}
