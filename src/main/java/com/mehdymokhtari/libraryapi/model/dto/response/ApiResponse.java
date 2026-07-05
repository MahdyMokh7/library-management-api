package com.mehdymokhtari.libraryapi.model.dto.response;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public record ApiResponse<T>(boolean success, String message, T data, LocalDateTime timestamp) {
  public ApiResponse(boolean success, String message, T data) {
    this(success, message, data, LocalDateTime.now());
  }

  public ApiResponse(boolean success, String message) {
    this(success, message, null, LocalDateTime.now());
  }

  public static <T> ApiResponse<T> success(T data) {
    return new ApiResponse<>(true, "Operation completed successfully", data);
  }

  public static <T> ApiResponse<T> success(String message, T data) {
    return new ApiResponse<>(true, message, data);
  }

  public static <T> ApiResponse<T> success(String message) {
    return new ApiResponse<>(true, message);
  }

  public static <T> ApiResponse<T> error(String message) {
    return new ApiResponse<>(false, message);
  }
}
