package com.bank.bank;

import android.content.Context;

import com.bank.accounts.RestrictedSavingsAccount;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
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
  public Atm(int customerId, String password, Context context)
      throws IllegalObjectTypeException, ItemNotFoundException, ConnectionFailedException {
        // Create a db object
        DatabaseSelectHelper db = new DatabaseSelectHelper(context);
        // Get user details from database
        User user = db.getUser(customerId, context);
        // Check if user is valid customer
        if (user instanceof Customer) {
          this.customerAuthenticated = user.authenticate(password, context);
          this.currentCustomer = (Customer) user;
          db.close();
        } else {
          db.close();
      throw new IllegalObjectTypeException("Must input an id for a valid customer");
    }
  }
  
  /**
   * Empty constructor for use by subclasses.
   */
  protected Atm() {}
  
  @Override
  public boolean authenticateCustomer(String password, Context context)
      throws ItemNotFoundException, ConnectionFailedException {
    // method to authenticate user
    this.customerAuthenticated = this.currentCustomer.authenticate(password, context);
    return this.customerAuthenticated;
  }
  
  @Override
  public boolean isCustomerAuthenticated() {
    return this.customerAuthenticated;
  }
  
  @Override
  public List<Account> listAccounts(Context context) throws ConnectionFailedException, NotAuthenticatedException {
    // instantiate db object
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);

    if (!customerAuthenticated) {
      // Throw exception if customer is not authenticated
      throw new NotAuthenticatedException("Customer is not authenticated");
    }
    // get list of accounts for currentCustomer
    List<Account> accountsList = new ArrayList<Account>();
    List<Integer> accountIds = new ArrayList<Integer>();
    // get account ids for all accounts
      accountIds = db.getAccountIdsList(this.currentCustomer.getId());
      // add accounts to accountsList
      for (Integer id : accountIds) {
        accountsList.add(db.getAccount(id));
      }
    db.close();
    // return list of all accounts for this user
    return accountsList;
  }

  @Override
  public boolean makeDeposit(BigDecimal amount, int accountId, Context context)
      throws IllegalAmountException, NotAuthenticatedException,
             ConnectionFailedException, ItemNotFoundException {

    // instantiate db object
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);

    // get the account with accountId
    Account a = db.getAccount(accountId);

    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = db.getAccountIdsList(this.currentCustomer.getId());

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
      // instantiate db object for updating
      DatabaseUpdateHelper dbUpdate = new DatabaseUpdateHelper(context);
      // update the database
      try {
        dbUpdate.updateAccountBalance(a.getBalance(), accountId);
      } finally {
        dbUpdate.close();
      }
      return true;
    }
    db.close();
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
  public BigDecimal checkBalance(int accountId, Context context)
      throws NotAuthenticatedException, ConnectionFailedException, ItemNotFoundException {
    // instantiate object for db
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    // check if user has access to account
    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = db.getAccountIdsList(this.currentCustomer.getId());
    // check if user has access or if account doesn't exist
    if (!userAccountIds.contains(accountId)) { 
      throw new NotAuthenticatedException("Account does not belong to user.");
    }
    // get the balance of account with accoundId
    Account a = db.getAccount(accountId);
    // check if account exists
    if (a != null) {
      db.close();
      return a.getBalance();
    }
    db.close();
    // if account doesn't exist
    return null;
  }
  
  @Override
  public boolean makeWithdrawal(BigDecimal amount, int accountId, Context context)
      throws IllegalAmountException, InsufficientFundsException, NotAuthenticatedException,
          ItemNotFoundException, ConnectionFailedException {

    // instantiate db object
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);

    // true if withdrawal was a success, false if it wasn't
    boolean completedWithdrawal = false;
    // get the account object
    Account accountToCheck = db.getAccount(accountId);
    // check if user has access to account
    List<Integer> userAccountIds = new ArrayList<Integer>();
    userAccountIds = db.getAccountIdsList(this.currentCustomer.getId());
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
          DatabaseUpdateHelper dbUpdate = new DatabaseUpdateHelper(context);
          try {
            dbUpdate.updateAccountBalance(newBalance, accountId);
          } finally {
            dbUpdate.close();
          }
          completedWithdrawal = true;
        }
      }
    }
    db.close();
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
  
  public boolean hasAccounts(Context context) throws ConnectionFailedException {
    // instantiate object for db
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    // get list of accounts belonging to current customer
    List<Integer> customerAccountIds = new ArrayList<Integer>();
    customerAccountIds = db.getAccountIdsList(this.currentCustomer.getId());
    // check if list is empty
    if (customerAccountIds.size() == 0) {
      System.out.println("There are no accounts for this user.");
      System.out.println("Please make an account first.");
      // return false if current customer has no accounts
      db.close();
      return false;
    }
    db.close();
    // return true if current user has accounts
    return true;
  }
}
