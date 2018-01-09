package com.bank.exceptions;

public class ConnectionFailedException extends Exception {
  
  /**
   * This is the serialVersionUID for the exception.
   */
  private static final long serialVersionUID = 1L;

  /**
   * Constructor to call constructor of the super class.
   */
  public ConnectionFailedException() {
    // call parent constructor
    super();
  }

  /**
   * Constructor to call a variation of the super class.
   * 
   * @param input string input passed to super class constructor.
   */
  public ConnectionFailedException(String input) {
    // call parent constructor
    super(input);
  }
}
