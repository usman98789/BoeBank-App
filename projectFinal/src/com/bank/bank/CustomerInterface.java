package com.bank.bank;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.NotAuthenticatedException;
import com.bank.generics.Account;
import com.bank.users.Customer;

import java.util.List;

/**
 * Interface used for customer information.
 * @author jasoncheung, Austin Seto
 *
 */
public interface CustomerInterface {

  /**
   * Attempts to authenticate the customer currently using this terminal.
   * @param password the password to authenticate the customer with
   * @return whether or not customer was authenticated successfully
   * @throws ItemNotFoundException if the customer is not in the database
   * @throws ConnectionFailedException if the connection to the database fails
   */
  public boolean authenticateCustomer(String password)
      throws ItemNotFoundException, ConnectionFailedException;
  
  /**
   * Changes the customer using this terminal.
   * @param customer the new customer using this interface
   */
  public void setCurrentCustomer(Customer customer);
  
  public Customer getCurrentCustomer();
  
  /**
   * Deauthenticates the customer currently using this terminal, removing them from it.
   */
  public void deauthenticateCustomer();
  
  public boolean isCustomerAuthenticated();
  
  /**
   * Gets a list of all the accounts owned by the customer currently using this interface.
   * @return a list of all the customer's accounts
   * @throws ConnectionFailedException if the connection to the database fails somehow
   * @throws NotAuthenticatedException if the customer is not authenticated yet
   */
  public List<Account> listAccounts() throws ConnectionFailedException, NotAuthenticatedException;
}
