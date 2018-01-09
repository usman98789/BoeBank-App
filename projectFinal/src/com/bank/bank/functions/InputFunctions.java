package com.bank.bank.functions;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class InputFunctions {

  /**
   * Returns true iff stringAge is an integer.
   * @param stringAge string to be parse into an integer
   * @return true iff stringAge in an integer, else return false
   */
  private static boolean isInteger(String stringAge) {
    try {
      Integer.parseInt(stringAge);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  /**
   * Gets an input from the user and returns it as an integer iff an integer is entered.
   * @return An integer from the user
   * @throws IOException Exception raised if error in reading input from user.
   */
  public static int getAndCheckValidInt() throws IOException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    // read the age
    String initialInput = br.readLine();
    // check if it's an integer
    boolean isValidInteger = isInteger(initialInput);
    // if input is invalid, loop until valid integer is given
    while (!isValidInteger) {
      System.out.println("Please enter a valid integer.");
      initialInput = br.readLine();
      isValidInteger = isInteger(initialInput);
    }
    // parse the valid input
    int validInteger = Integer.parseInt(initialInput);
    return validInteger;
  } 
  
  /**
   * Returns true iff stringAge is an float.
   * @param stringAge string to be parse into an float
   * @return true iff stringAge in an integer, else return false
   */
  private static boolean isFloat(String stringAge) {
    try {
      Float.parseFloat(stringAge);
      return true;
    } catch (NumberFormatException e) {
      return false;
    }
  }
  
  /**
   * Gets an input from the user and returns it as an integer iff an integer is entered.
   * @return An integer from the user
   * @throws IOException Exception raised if error in reading input from user.
   */
  public static float getAndCheckValidFloat() throws IOException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    // read the age
    String initialInput = br.readLine();
    // check if it's an integer
    boolean isValidFloat = isFloat(initialInput);
    // if input is invalid, loop until valid integer is given
    while (!isValidFloat) {
      System.out.println("Please enter a valid float.");
      initialInput = br.readLine();
      isValidFloat = isFloat(initialInput);
    }
    // parse the valid input
    float validFloat = Float.parseFloat(initialInput);
    return validFloat;
  } 
}
