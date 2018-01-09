package com.bank.bank;

import com.bank.accounts.RestrictedSavingsAccount;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.NotAuthenticatedException;
import com.bank.generics.Account;
import com.bank.generics.User;
import com.bank.users.Customer;

import java.math.BigDecimal;

import java.util.ArrayList;
import java.util.List;

/**
 * Class for ATM object.
 * @author Ka-Kit Jason Cheung
 *
 */
public class Atm implements FinancialAccountInterface, CustomerInterface {
  protected Customer currentCustomer;
  private boolean customerAuthenticated;
  
  /**
   * Constructor for ATM object with 2 params.
   * @param customerId customerId to be set to an ATM object
   * @param password password to be set to an ATM object
   * @throws IllegalObjectTypeException if {@code customerId} refers to a non-customer
   * @throws ItemNotFoundException if {@code customerId} refers to a user that does not exist
   * @throws ConnectionFailedException if connection to database fails somehow
   */
  public Atm(int customerId, String password)
      throws IllegalObjectTypeException, ItemNotFoundException, ConnectionFailedException {
    // Get user details from database
    User user = DatabaseSelectHelper.getUserDetails(customerId);
    // Check if user is valid customer
    if (user instanceof Customer) {
      this.customerAuthenticated = user.authenticate(password);
      this.currentCustomer = (Customer) user;
    } else {
      throw new IllegalObjectTypeException("Must input an id for a valid customer");
    }
  }
  
  /**
   * Empty constructor for use by subclasses.
   */
  protected Atm() {}
  
  @Override
  public boolean authenticateCustomer(String password)
      throws ItemNotFoundException, ConnectionFailedException {
    // method to authenticate user
    this.customerAuthenticated = this.currentCustomer.authenticate(password);
    return this.customerAuthenticated;
  }
  
  @Override
  public boolean isCustomerAuthenticated() {
    return this.customerAuthenticated;
  }
  
  @Override
  public List<Account> listAccounts() throws ConnectionFailedException, NotAuthenticatedException {
    if (!customerAuthenticated) {
      // Throw exception if customer is not authenticated
      throw new NotAuthenticatedException("Customer is not authenticated");
    }
    // get list of accounts for currentCustomer
    List<Account> accountsList = new ArrayList<Account>();
    List<Integer> accountIds = new ArrayList<Integer>();
    // get account ids for all accounts
    try {
      accountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
      // add accounts to accountsList
      for (Integer id : accountIds) {
        accountsList.add(DatabaseSelectHelper.getAccountDetails(id));
      }
    } catch (ItemNotFoundException e) {
      // If accounts not found, simply return empty list
      accountsList = new ArrayList<Account>();
    }
    // return list of all accounts for this user
    return accountsList;
  }

  @Override
  public boolean makeDeposit(BigDecimal amount, int accountId)
      throws IllegalAmountException, NotAuthenticatedException,
             ConnectionFailedException, ItemNotFoundException {
    // get the account with accountId
    Account a = DatabaseSelectHelper.getAccountDetails(accountId);

    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());

    // check if user has access or if account doesn't exist
    if (!userAccountIds.contains(accountId)) { 
      throw new NotAuthenticatedException("Account does not belong to user.");
    }
    if (a != null) {
      // throw exception (illegalAmountException) if more than 2 decimal places
      if (!this.twoDecimalPlaces(amount) || (amount.compareTo(BigDecimal.ZERO) < 0)) {
        throw new IllegalAmountException("Deposit amount must be to two decimal places");
      }
      
      // add money to the account
      BigDecimal newBalance = a.getBalance().add(amount);
      
      a.setBalance(newBalance);
      // update the database
      try {
        DatabaseUpdateHelper.updateAccountBalance(a.getBalance(), accountId);
      } catch (IllegalInputParameterException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
      return true;
    }
    // if there is no matching account
    return false;
  }
  
  protected boolean twoDecimalPlaces(BigDecimal amount) {
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
        return false;
      }
    }
    return true;
  }

  @Override
  public BigDecimal checkBalance(int accountId)
      throws NotAuthenticatedException, ConnectionFailedException, ItemNotFoundException {
    // check if user has access to account
    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
    // check if user has access or if account doesn't exist
    if (!userAccountIds.contains(accountId)) { 
      throw new NotAuthenticatedException("Account does not belong to user.");
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
  
  @Override
  public boolean makeWithdrawal(BigDecimal amount, int accountId)
      throws IllegalAmountException, InsufficientFundsException, NotAuthenticatedException,
             ItemNotFoundException, ConnectionFailedException {
    // true if withdrawal was a success, false if it wasn't
    boolean completedWithdrawal = false;
    // get the account object
    Account accountToCheck = DatabaseSelectHelper.getAccountDetails(accountId);
    // check if user has access to account
    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
    // check if user has access or if account doesn't exist
    if (!userAccountIds.contains(accountId)) { 
      throw new NotAuthenticatedException("Account does not belong to user.");
    } else if (amount.compareTo(BigDecimal.ZERO) < 0) {
      throw new IllegalAmountException();
    }
    // check if account exists
    if (accountToCheck != null) {
      // check if there are enough funds
      if (amount.compareTo(accountToCheck.getBalance()) == 1) {
        throw new InsufficientFundsException("Not enough funds in your account to withdraw.");
      } else {
        // checks if the account is restricted
        boolean restricted = accountToCheck instanceof RestrictedSavingsAccount;
        boolean isTellerTerminal = this instanceof TellerTerminal;
        // if the account is not restricted or restricted and the current user is a teller
        if (!restricted || (restricted && isTellerTerminal)) { 
          // calculate new balance and set it to the object
          BigDecimal newBalance = accountToCheck.getBalance().subtract(amount);
          // update the database
          try {
            DatabaseUpdateHelper.updateAccountBalance(newBalance, accountId);
          } catch (IllegalInputParameterException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
          }
          completedWithdrawal = true;
        }
      }
    }
    // return false if account doesn't exist
    return completedWithdrawal;
  }

  @Override
  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
  }
  
  @Override
  public Customer getCurrentCustomer() {
    return this.currentCustomer;
  }

  @Override
  public void deauthenticateCustomer() {
    // set authenticated to false and remove the current customer
    this.customerAuthenticated = false;
    this.currentCustomer = null;
  }
  
  /**
   * Returns whether or not a customer is currently signed in at this Atm.
   * @return whether or not there is currently a customer signed in at this Atm
   */
  public boolean currentCustomerPresent() {
    if (this.currentCustomer != null) {
      return true;
    } else {
      System.out.println("There is currently no customer.");
      System.out.println("Please authenticate and assign a customer first.");
      return false;
    }
  }
  
  public boolean hasAccounts() throws ConnectionFailedException {
    // get list of accounts belonging to current customer
    List<Integer> customerAccountIds = new ArrayList<Integer>();
    customerAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
    // check if list is empty
    if (customerAccountIds.size() == 0) {
      System.out.println("There are no accounts for this user.");
      System.out.println("Please make an account first.");
      // return false if current customer has no accounts
      return false;
    }
    // return true if current user has accounts
    return true;
  }
}
