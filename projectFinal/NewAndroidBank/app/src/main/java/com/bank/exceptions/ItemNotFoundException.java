package com.bank.exceptions;

/**
 * An exception thrown when an item cannot be found in the database.
 * @author Austin Seto
 *
 */
public class ItemNotFoundException extends Exception {

  private static final long serialVersionUID = 1L;

  public ItemNotFoundException() {
    super();
  }

  public ItemNotFoundException(String message) {
    super(message);
  }
}
