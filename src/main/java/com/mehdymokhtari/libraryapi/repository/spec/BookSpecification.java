package com.mehdymokhtari.libraryapi.repository.spec;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import jakarta.persistence.criteria.Predicate;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.util.StringUtils;

import com.mehdymokhtari.libraryapi.filter.BookFilter;
import com.mehdymokhtari.libraryapi.model.entity.Book;
import com.mehdymokhtari.libraryapi.model.enums.BookStatus;

public final class BookSpecification {

  private BookSpecification() {}

  public static Specification<Book> withFilters(BookFilter filter) {
    return (root, query, criteriaBuilder) -> {
      List<Predicate> predicates = new ArrayList<>();

      // Always exclude deleted books
      predicates.add(criteriaBuilder.isFalse(root.get("deleted")));

      if (filter == null) {
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
      }

      if (StringUtils.hasText(filter.getTitle())) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("title")),
                "%" + filter.getTitle().toLowerCase(Locale.ROOT) + "%"));
      }

      if (StringUtils.hasText(filter.getAuthor())) {
        predicates.add(
            criteriaBuilder.like(
                criteriaBuilder.lower(root.get("author")),
                "%" + filter.getAuthor().toLowerCase(Locale.ROOT) + "%"));
      }

      if (filter.getPublicationYear() != null) {
        predicates.add(
            criteriaBuilder.equal(root.get("publicationYear"), filter.getPublicationYear()));
      }

      if (filter.getStatus() != null) {
        predicates.add(criteriaBuilder.equal(root.get("status"), filter.getStatus()));
      }

      return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    };
  }

  public static Specification<Book> hasTitle(String title) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(title)) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("title")), "%" + title.toLowerCase(Locale.ROOT) + "%");
    };
  }

  public static Specification<Book> hasAuthor(String author) {
    return (root, query, criteriaBuilder) -> {
      if (!StringUtils.hasText(author)) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.like(
          criteriaBuilder.lower(root.get("author")), "%" + author.toLowerCase(Locale.ROOT) + "%");
    };
  }

  public static Specification<Book> hasPublicationYear(Integer year) {
    return (root, query, criteriaBuilder) -> {
      if (year == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("publicationYear"), year);
    };
  }

  public static Specification<Book> hasStatus(BookStatus status) {
    return (root, query, criteriaBuilder) -> {
      if (status == null) {
        return criteriaBuilder.conjunction();
      }
      return criteriaBuilder.equal(root.get("status"), status);
    };
  }

  public static Specification<Book> isNotDeleted() {
    return (root, query, criteriaBuilder) -> criteriaBuilder.isFalse(root.get("deleted"));
  }
}
