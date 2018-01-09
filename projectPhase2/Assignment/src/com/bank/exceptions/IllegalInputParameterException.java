package com.bank.exceptions;

/**
 * An exception to be throw if a user inputs any kind of illegal parameters.
 * @author Austin Seto
 *
 */
public class IllegalInputParameterException extends Exception {

  private static final long serialVersionUID = 1L;

  public IllegalInputParameterException() {
    super();
  }

  public IllegalInputParameterException(String message) {
    super(message);
  }
}
