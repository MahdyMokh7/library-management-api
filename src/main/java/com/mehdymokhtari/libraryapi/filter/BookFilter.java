package com.mehdymokhtari.libraryapi.filter;

import com.mehdymokhtari.libraryapi.model.enums.BookStatus;
import lombok.Builder;
import lombok.Data;

/**
 * Filter object for book search and query operations.
 *
 * <p>Encapsulates all search/filter criteria for querying books in the system.
 * Used in combination with {@link com.mehdymokhtari.libraryapi.repository.spec.BookSpecification}
 * to build dynamic WHERE clauses for database queries.</p>
 *
 * <p>This filter is typically constructed from HTTP query parameters in the controller,
 * then passed to the service layer where it's converted to a JPA Specification.</p>
 *
 * <p>Supported filters:</p>
 * <ul>
 *   <li><b>title</b> - Partial match (case-insensitive LIKE query)</li>
 *   <li><b>author</b> - Partial match (case-insensitive LIKE query)</li>
 *   <li><b>publicationYear</b> - Exact match</li>
 *   <li><b>status</b> - Exact match (AVAILABLE or BORROWED)</li>
 * </ul>
 *
 * <p>All filters are optional and can be combined. Null/empty values are ignored
 * during query building.</p>
 *
 * <p>Example usage:</p>
 * <pre>
 * BookFilter filter = BookFilter.builder()
 *     .title("Spring")
 *     .author("John")
 *     .status(BookStatus.AVAILABLE)
 *     .build();
 *
 * PagedResponse<BookResponse> books = bookService.getAllBooks(filter, pageable);
 * </pre>
 *
 */

@Data
@Builder
public class BookFilter {
    private String title;
    private String author;
    private Integer publicationYear;
    private BookStatus status;
}