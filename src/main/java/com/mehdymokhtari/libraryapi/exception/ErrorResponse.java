package com.mehdymokhtari.libraryapi.exception;

import java.time.LocalDateTime;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Builder;

@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public record ErrorResponse(
    int status,
    String error,
    String message,
    String path,
    LocalDateTime timestamp,
    Map<String, String> validationErrors) {

  public ErrorResponse {
    timestamp = timestamp != null ? timestamp : LocalDateTime.now();
  }

  public static ErrorResponse of(int status, String error, String message, String path) {
    return ErrorResponse.builder()
        .status(status)
        .error(error)
        .message(message)
        .path(path)
        .timestamp(LocalDateTime.now())
        .build();
  }

  public static ErrorResponse of(
      int status, String error, String message, String path, Map<String, String> validationErrors) {
    return ErrorResponse.builder()
        .status(status)
        .error(error)
        .message(message)
        .path(path)
        .timestamp(LocalDateTime.now())
        .validationErrors(validationErrors)
        .build();
  }
}
