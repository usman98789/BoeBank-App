package com.bank.exceptions;

/**
 * An exception thrown when a type that is not allowed is passed into a function.
 * @author Austin Seto
 *
 */
public class IllegalObjectTypeException extends Exception {

  private static final long serialVersionUID = 1L;

  public IllegalObjectTypeException() {
    super();
  }

  public IllegalObjectTypeException(String message) {
    super(message);
  }
}
