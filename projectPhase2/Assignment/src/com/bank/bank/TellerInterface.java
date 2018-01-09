package com.bank.bank;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.NotAuthenticatedException;
import com.bank.users.Teller;

/**
 * Interface for teller information.
 * @author jasoncheung, Austin Seto
 *
 */
public interface TellerInterface {
  
  /**
   * Attempts to authenticate the teller currently in this terminal.
   * @param password the password to try to authenticate the teller with
   * @return whether or not the teller was authenticated
   * @throws ItemNotFoundException if the teller in this terminal does not exist in the database
   * @throws ConnectionFailedException if connection to the database fails
   */
  public boolean authenticateTeller(String password)
      throws ItemNotFoundException, ConnectionFailedException;
  
  /**
   * Changes the teller using this interface.
   * @param teller the new teller for this terminal
   */
  public void setCurrentTeller(Teller teller);
  
  public Teller getCurrentTeller();
  
  /**
   * Deauthenticates the current teller, removing them from this terminal.
   */
  public void deauthenticateTeller();
  
  public boolean isTellerAuthenticated();
  
  /**
   * Has the teller create a new customer if they are authenticated.
   * @param name the name of the new customer
   * @param age the age of the new customer (must be positive)
   * @param address the address of the new customer (must be under 100 characters)
   * @param password the password for the customer (cannot be empty)
   * @throws ConnectionFailedException if connection to the database fails somehow
   * @throws IllegalInputParameterException if any of the input strings are invalid
   * @throws IllegalAmountException if age is not a positive integer
   * @throws NotAuthenticatedException if the teller using this terminal is not authenticated
   */
  public void makeNewUser(String name, int age, String address, String password)
      throws ConnectionFailedException, IllegalInputParameterException,
             IllegalAmountException, NotAuthenticatedException;
  
}
