package com.bank.bank;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bank.accounts.*;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ErrorAuthenticatingException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.NotTellerIdException;
import com.bank.exceptions.UnauthorizedAccessException;
import com.bank.generics.Account;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.generics.User;
import com.bank.users.Customer;
import com.bank.users.Teller;

/**
 * Class for the tellerTerminal object, child of ATM.
 * @author Ka-Kit Jason Cheung
 *
 */
public class TellerTerminal extends Atm {
  private Teller currentUser;
  private boolean currentUserAuthenticated;
  private Customer currentCustomer;
  private boolean currentCustomerAuthenticated;
  
  /**
   * Constructor for TellerTerminal object.
   * @param tellerId id of teller operating the tellerTerminal
   * @param password password of the teller operation tellerTerminal
   * @throws SQLException Exception thrown if errors in SQL queries
   * @throws NotTellerIdException Exception thrown if role is not a teller
   * @throws ErrorAuthenticatingException Exception thrown if error authenticating user
   */
  public TellerTerminal(int tellerId, String password) throws SQLException, NotTellerIdException,
      ErrorAuthenticatingException {
    // default values -1 = no customer yet
    super(-1);
    // find the teller with tellerId
    // check ID provided is a tellerId
    User tempUser = DatabaseSelectHelper.getUserDetails(tellerId);
    // if user not found, throw exception
    if (tempUser == null) {
      throw new NotTellerIdException();
    } else {
      // get role id
      int roleId = DatabaseSelectHelper.getUserRole(tellerId);
      List<Integer> roleIds = new ArrayList<Integer>();
      roleIds = DatabaseSelectHelper.getRoles();
      int tellerRoleId = -1;
      // get the roleId of the teller
      for (int i = 0; i < roleIds.size(); i++) {
        String roleName = DatabaseSelectHelper.getRole(roleIds.get(i));
        if (roleName.equalsIgnoreCase(Roles.TELLER.toString())) {
          tellerRoleId = roleIds.get(i);
        }
      }
      // check the role Id matches with a teller
      if (roleId == tellerRoleId) {
        this.currentUser = (Teller) tempUser;
        // must authenticate the teller
        this.currentUserAuthenticated = this.currentUser.authenticate(password);
        // if wrong password, throw exception
        if (this.currentUserAuthenticated == false) {
          throw new ErrorAuthenticatingException("Incorrect password.");
        }
      } else {
        // if this is not a teller, raise NotTellerIdException
        throw new NotTellerIdException("Id is not a teller ID.");
      } 
    }
  }
  
  /**
   * Method for making new account and inserting into the database.
   * @param name name of new account
   * @param balance starting balance of new account 
   * @param type type of account
   * @return true iff account was successfully made and inserted into database
   * @throws SQLException Exception thrown if errors in SQL queries
   */
  public boolean makeNewAccount(String name, BigDecimal balance, int type) throws SQLException {
    // if both user and customer are authenticated, make a new account with given
    // information and register it to the currentCustomer
    if (this.currentCustomerAuthenticated == true && this.currentUserAuthenticated == true) {
      // insert account into database with type of typeId
      System.out.println("TypeId of account: " + type);
      int accountId = DatabaseInsertHelper.insertAccount(name, balance, type);
      System.out.println("New account's id: " + accountId);
      // error check insertAccount
      if (accountId != -1) {
        // make connection with user and account
        boolean success;
        // prompt process
        System.out.println("Adding: " + this.currentCustomer.getId() + "'s account.");
        // insert user and account relationship
        success = DatabaseInsertHelper.insertUserAccount(this.currentCustomer.getId(), accountId);
        Customer c = (Customer) DatabaseSelectHelper.getUserDetails(this.currentCustomer.getId());
        return success;
      }
    }
    return false;
  }
  
  /**
   * Setter for currentCustomer.
   * @param customer customer to be set as currentCustomer
   */
  public void setCurrentCustomer(Customer customer) {
    this.currentCustomer = customer;
    System.out.println("current customer is now " + this.currentCustomer);
  }
  
  /**
   * Method to authenticate currentCustomer with their password.
   * @param password password to be authenticated with
   * @throws SQLException Exception thrown if errors in SQL queries
   */
  public void authenticateCurrentCustomer(String password) throws SQLException {
    this.currentCustomerAuthenticated = this.currentCustomer.authenticate(password);
  }
  
  /**
   * Method to make new customer and insert into database.
   * @param name name of customer
   * @param age age of customer
   * @param address address of customer
   * @param password password of customer
   * @throws SQLException Exception thrown if errors in SQL queries
   */
  public void makeNewUser(String name, int age, String address, String password) throws 
      SQLException {
    // if teller is authenticated, make new Customer based on given information
    // check if teller is authenticated
    if (this.currentUserAuthenticated == true) {
      // get roleId of customer
      List<Integer> roleIds = new ArrayList<Integer>();
      roleIds = DatabaseSelectHelper.getRoles();
      int roleId = -1;
      for (int i = 0; i < roleIds.size(); i++) {
        int tempId = roleIds.get(i);
        // check if this id is the customer roleId
        if (DatabaseSelectHelper.getRole(tempId).equalsIgnoreCase(Roles.CUSTOMER.toString())) {
          roleId = tempId;
          break;
        }
      }
      // make a new customer and add it to the database
      int newCustId = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
      System.out.println("New user id: " + newCustId);
     
    }
  }
  
  /**
   * Method that applies interest to account with accountId
   * @param accountId account with accountId to have interest applied.
   * @throws SQLException Exception thrown if errors in SQL queries
   */
  public void giveInterest(int accountId) throws SQLException {
    // if teller, user is authenticate and account belongs to the given user, give them interest
    // check if user and teller is authenticated
    if (this.currentUserAuthenticated == true && this.currentCustomerAuthenticated == true) {
      // get all accounts associated with the currentCustomer
      List<Integer> userAccountIds = new ArrayList<Integer>();
      userAccountIds = DatabaseSelectHelper.getAccountIds(this.currentCustomer.getId());
      // check account given belongs to the user      
      if (userAccountIds.contains(accountId)) {
        // get the account type
        int accountTypeId = DatabaseSelectHelper.getAccountType(accountId);
        // error checking
        if (accountTypeId != -1) {
          // get old balance and interest rate, calculate interest
          BigDecimal oldBalance = DatabaseSelectHelper.getBalance(accountId);
          BigDecimal interestRate = DatabaseSelectHelper.getInterestRate(accountTypeId);
          BigDecimal interest = oldBalance.multiply(interestRate);
          BigDecimal newBalance = oldBalance.add(interest);
          // update the balance on the database for this account
          DatabaseUpdateHelper.updateAccountBalance(newBalance, accountId);
        }
      }
    }
  }
  
  /**
   * Method to deauthenticate customer from teller terminal.
   */
  public void deAuthenticateCustomer() {
    this.currentCustomerAuthenticated = false;
    this.currentCustomer = null;
  }
  
  
  public boolean makeDeposit(BigDecimal amount, int accountId) throws SQLException,
      IllegalAmountException, UnauthorizedAccessException {
      System.out.println("From tellerTerminal: " + this.currentCustomer);
      return super.makeDeposit(amount, accountId);
  }
  
}
