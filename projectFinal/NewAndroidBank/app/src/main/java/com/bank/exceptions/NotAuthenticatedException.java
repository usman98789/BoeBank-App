package com.bank.exceptions;

/**
 * An exception thrown when an action is attempted but the user has not been authorised or been
 * granted access to do said action. 
 * 
 * @author Austin Seto
 *
 */
public class NotAuthenticatedException extends Exception {

  private static final long serialVersionUID = 1L;

  public NotAuthenticatedException() {
    super();
  }

  public NotAuthenticatedException(String message) {
    super(message);
  }
}
