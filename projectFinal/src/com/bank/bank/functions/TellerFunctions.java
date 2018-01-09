package com.bank.bank.functions;

import com.bank.bank.TellerTerminal;
import com.bank.bank.functions.InputFunctions;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.NotAuthenticatedException;
import com.bank.generics.User;
import com.bank.users.Customer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * A class for storing all the actions that can be performed through a teller interface.
 * 
 * @author Austin Seto
 */
public class TellerFunctions {
  
  private static final String BAD_CREDENTIALS_MESSAGE = "Invalid credentials, please try again";
  private static final String CONNECT_FAIL_MESSAGE = "Connection to bank database failed";
  
  /**
   * Attempts to authenticate a customer with information input by user.
   * @param terminal the terminal that is being used
   * @throws IOException if an I/O error occurs
   */
  public static void authenticateCustomer(TellerTerminal terminal) throws IOException {
    InputStreamReader in = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(in);
    User inputCustomer = null;
    // Obtain user input
    System.out.print("Input customer ID: ");
    int id = InputFunctions.getAndCheckValidInt();
    System.out.print("Input customer password: ");
    String password = br.readLine();
    br.close();
    in.close();
    boolean successful = false;
    // New line to separate text on screen
    System.out.print("\n");
    try {
      inputCustomer = DatabaseSelectHelper.getUserDetails(id);
      if (inputCustomer instanceof Customer) {
        terminal.setCurrentCustomer((Customer)inputCustomer);
        // See if authentication successful
        successful = terminal.authenticateCustomer(password);
        // Print message based on success of authentication
        if (successful) {
          System.out.println("Authentication successful!");
        } else {
          System.out.println(BAD_CREDENTIALS_MESSAGE);
        }
      } else {
        // If input id does not correspond to a customer, bad credentials
        System.out.println(BAD_CREDENTIALS_MESSAGE);
      }
    } catch (ItemNotFoundException e) {
      // If customer could not be retrieved, bad credentials
      System.out.println(BAD_CREDENTIALS_MESSAGE);
    } catch (ConnectionFailedException e) {
      System.out.println(CONNECT_FAIL_MESSAGE);
    }
  }
  
  /**
   * Attempts to create a new customer.
   * @param terminal the teller terminal this is being done through
   * @throws IOException if an I/O error occurs
   */
  public static void newCustomer(TellerTerminal terminal) throws IOException {
    if (terminal.isTellerAuthenticated()) {
      InputStreamReader in = new InputStreamReader(System.in);
      BufferedReader br = new BufferedReader(in);
      System.out.print("Enter new customer's name: ");
      String name = br.readLine();
      System.out.print("Enter " + name + "'s age: ");
      int age = InputFunctions.getAndCheckValidInt();
      System.out.print("Enter " + name + "'s address: ");
      String address = br.readLine();
      System.out.print("Enter the password desired: ");
      String password = br.readLine();
      try {
        terminal.makeNewUser(name, age, address, password);
      } catch (NotAuthenticatedException e) {
        System.out.println("Teller not authenticated");
      } catch (ConnectionFailedException e) {
        System.out.println(CONNECT_FAIL_MESSAGE);
      } catch (IllegalInputParameterException e) {
        if (password.length() == 0) {
          System.out.println("Password required");
        }
        if (address.length() >= 100) {
          System.out.println("Address too long");
        }
      } catch (IllegalAmountException e) {
        System.out.println("Age must be positive");
      }
    } else {
      System.out.println("Teller not authenticated");
    }
  }
}
