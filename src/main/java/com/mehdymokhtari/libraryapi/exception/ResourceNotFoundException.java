package com.mehdymokhtari.libraryapi.exception;

public class ResourceNotFoundException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Resource not found";

  public ResourceNotFoundException() {
    super(DEFAULT_MESSAGE);
  }

  public ResourceNotFoundException(String message) {
    super(message);
  }

  public ResourceNotFoundException(String resourceName, Object identifier) {
    super(String.format("%s with ID %s not found", resourceName, identifier));
  }
}
