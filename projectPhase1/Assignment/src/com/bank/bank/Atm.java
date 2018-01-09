package com.bank.bank;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.generics.Account;
import com.bank.users.*;
import com.bank.exceptions.*;

import java.math.BigDecimal;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for ATM object.
 * @author Ka-Kit Jason Cheung
 *
 */
public class Atm {
  private Customer currentCustomer;
  private boolean authenticated;
  
  /**
   * Constructor for ATM object with 2 params.
   * @param customerId customerId to be set to an ATM object
   * @param password password to be set to an ATM object
   * @throws SQLException Exception thrown if error during SQL queries
   * @throws UnauthorizedAccessException Exception thrown if role other than customer accesses ATM.
   */
  public Atm(int customerId, String password) throws SQLException, UnauthorizedAccessException {
    // get account from database
    Customer tempCustomer = (Customer) DatabaseSelectHelper.getUserDetails(customerId); 
    // authenticate account first,
    this.authenticated = tempCustomer.authenticate(password);
    // then load account
    if (this.authenticated == true) {
      this.currentCustomer = tempCustomer;
    }
  }
  
  /**
   * Constructor for ATM with 1 param.
   * @param customerId customerId to be set to an ATM object
   * @throws SQLException Exception thrown if error during SQL queries
   */
  public Atm(int customerId) throws SQLException {
    if (customerId != -1) {
      // loads the account without authentication
      Customer c = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
      if (c != null) {
        this.currentCustomer = c;
      } else {
        throw new SQLException("Customer not found.");
      }
    } 
  }
  
  /**
   * Method to authenticate the user given userId and password.
   * @param userId userId to identify user
   * @param password password to authenticate with
   * @return true iff authentication succeeds, else return false
   * @throws SQLException Exception thrown if error during SQL queries
   */
  public boolean authenticate(int userId, String password) throws SQLException {
    // method to authenticate user
    Customer tempCustomer = (Customer) DatabaseSelectHelper.getUserDetails(userId); 
    this.authenticated = tempCustomer.authenticate(password);
    return this.authenticated;
  }
  
  /**
   * Returns list Accounts associated with the current customer.
   * @return List of Account objects
   * @throws SQLException Exception thrown if error during SQL queries
   */
  public List<Account> listAccounts() throws SQLException {
    // get list of accounts for currentCustomer
    List<Account> accountsList = new ArrayList<Account>();
    List<Integer> accountIds = new ArrayList<Integer>();
    // get account ids for all accounts
    accountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
    // add accounts to accountsList
    for (int i = 0; i < accountIds.size(); i++) {
      accountsList.add(DatabaseSelectHelper.getAccountDetails(accountIds.get(i)));
    }
    // return list of all accounts for this user
    return accountsList;
  }
  
  /**
   * Performs a deposit on account with accountId.
   * @param amount Amount to be deposited
   * @param accountId Account with accountId where money will be deposited.
   * @return true iff successful deposit, else return false
   * @throws SQLException Exception thrown if error during SQL queries
   * @throws IllegalAmountException Exception thrown if improper amount entered to deposit
   * @throws UnauthorizedAccessException If any role but customer attempts to access this function
   */
  public boolean makeDeposit(BigDecimal amount, int accountId) throws SQLException,
      IllegalAmountException, UnauthorizedAccessException {
    // get the account with accountId
    System.out.println("From the ATM: " + this.currentCustomer);
    Account a = DatabaseSelectHelper.getAccountDetails(accountId);

    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());

    // check if user has access or if account doesn't exist
    if (!userAccountIds.contains(accountId)) { 
      throw new UnauthorizedAccessException("Account does not belong to user.");
    }
    if (a != null) {
      // throw exception (illegalAmountException) if more than 2 decimal places
      
      String stringAmount = amount.stripTrailingZeros().toPlainString();
      int idx = stringAmount.indexOf('.');
      int numDecimals;
      // if no decimal places
      if (idx < 0) {
        numDecimals = -1;
      } else {
        numDecimals = stringAmount.length() - idx - 1;
        // check if numDecimals > 2
        if (numDecimals > 2) {
          throw new IllegalAmountException("Illegal amount entered.");
        }
      }
      // add money to the account
      BigDecimal newBalance = a.getBalance().add(amount);
      
      a.setBalance(newBalance);
      // update the database
      DatabaseUpdateHelper.updateAccountBalance(a.getBalance(), accountId);
      return true;
    }
    // if there is no matching account
    return false;
  }
  
  /**
   * Method to check the balance of account with accoundId id.
   * @param accountId account with accountId id.
   * @return balance the balance of the account
   * @throws SQLException Exception thrown if error during SQL queries
   * @throws UnauthorizedAccessException If any role but customer attempts to access this function
   */
  public BigDecimal checkBalance(int accountId) throws SQLException, UnauthorizedAccessException {
    // check if user has access to account
    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
    // check if user has access or if account doesn't exist
    if (!userAccountIds.contains(accountId)) { 
      throw new UnauthorizedAccessException("Account does not belong to user.");
    }
    // get the balance of account with accoundId
    Account a = DatabaseSelectHelper.getAccountDetails(accountId);
    // check if account exists
    if (a != null) {
      return a.getBalance();
    }
    // if account doesn't exist
    return null;
  }
  
  /**
   * Method that withdraws amount from account with accountId id.
   * @param amount amount to be withdrawn
   * @param accountId account with accountId to withdraw amount from
   * @return success true iff withdraw was successful
   * @throws SQLException Exception thrown if error during SQL queries
   * @throws InsuffiecintFundsException Exception thrown if not enough funds to withdraw from
   * @throws UnauthorizedAccessException If any role but customer attempts to access this function
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId) throws SQLException,
      InsuffiecintFundsException, UnauthorizedAccessException {
    // true if withdrawal was a success, false if it wasn't
    // get the account
    Account a = DatabaseSelectHelper.getAccountDetails(accountId);
    // check if user has access to account
    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
    // check if user has access or if account doesn't exist
    if (!userAccountIds.contains(accountId)) { 
      throw new UnauthorizedAccessException("Account does not belong to user.");
    }
    // check if account exists
    if (a != null) {
      // check if there are enough funds
      if (amount.compareTo(a.getBalance()) == 1) {
        throw new InsuffiecintFundsException("Not enough funds in your account to withdrawal.");
      } else {
        // calculate new balance and set it to the object
        BigDecimal newBalance = a.getBalance().subtract(amount);
        // update the database
        DatabaseUpdateHelper.updateAccountBalance(newBalance, accountId);
        return true;
      }
    }
    // return false if account doesn't exist
    return false;
  }
  
}
