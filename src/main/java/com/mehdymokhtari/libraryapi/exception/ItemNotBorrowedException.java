package com.mehdymokhtari.libraryapi.exception;

public class ItemNotBorrowedException extends RuntimeException {

  private static final String DEFAULT_MESSAGE = "Item is not currently borrowed";

  public ItemNotBorrowedException() {
    super(DEFAULT_MESSAGE);
  }

  public ItemNotBorrowedException(String message) {
    super(message);
  }

  public ItemNotBorrowedException(Long itemId) {
    super(String.format("Item with ID %d is not currently borrowed", itemId));
  }
}
