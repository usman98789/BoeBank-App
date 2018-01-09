package com.bank.bank;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.NotAuthenticatedException;
import com.bank.generics.Roles;
import com.bank.generics.User;
import com.bank.users.Teller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Class for the tellerTerminal object, child of ATM.
 * @author Ka-Kit Jason Cheung
 *
 */
public class TellerTerminal extends Atm implements TellerInterface {
  private Teller currentTeller;
  private boolean currentTellerAuthenticated;
  
  /**
   * Constructor for TellerTerminal object.
   * @param tellerId id of teller operating the tellerTerminal
   * @param password password of the teller operation tellerTerminal
   * @throws IllegalObjectTypeException if {@code tellerId} does not correspond to a teller
   * @throws ItemNotFoundException if {@code tellerId} does not correspond to a user in the database
   */
  public TellerTerminal(int tellerId, String password)
      throws IllegalObjectTypeException, ItemNotFoundException, ConnectionFailedException {
    User user = DatabaseSelectHelper.getUserDetails(tellerId);
    // Check if user is a valid Teller
    if (user instanceof Teller) {
      this.currentTellerAuthenticated = user.authenticate(password);
      this.currentTeller = (Teller) user;
    } else {
      throw new IllegalObjectTypeException("Must input id for valid Teller");
    }
  }  
  
  /**
   * Method for making new account and inserting into the database.
   * @param name name of new account
   * @param balance starting balance of new account 
   * @param type type of account
   * @return true iff account was successfully made and inserted into database
   * @throws ConnectionFailedException if connection to the database fails somehow
   * @throws IllegalObjectTypeException if input account type is not valid
   * @throws NotAuthenticatedException if either user or teller not authenticated
   * @throws ItemNotFoundException if user not in database
   */
  public boolean makeNewAccount(String name, BigDecimal balance, int type)
      throws ConnectionFailedException, IllegalObjectTypeException,
             NotAuthenticatedException, ItemNotFoundException {    
    // if both user and customer are authenticated, make a new account with given
    // information and register it to the currentCustomer
    
    if (this.isCustomerAuthenticated() && this.isTellerAuthenticated()) {
      // insert account into database with type of typeId
      System.out.println("TypeId of account: " + type);
      int accountId = DatabaseInsertHelper.insertAccount(name, balance, type);
      System.out.println("New account's id: " + accountId);
      // error check insertAccount
      if (accountId != -1) {
        // make connection with user and account
        boolean success;
        // prompt process
        System.out.println("Creating " + this.getCurrentCustomer().getName() + "'s new account.");
        // insert user and account relationship
        success = DatabaseInsertHelper.insertUserAccount(
                  this.getCurrentCustomer().getId(), accountId);
        return success;
      }
    } else {
      throw new NotAuthenticatedException("Both user and teller must be authenticated");
    }
    return false;
  }

  @Override
  public void makeNewUser(String name, int age, String address, String password)
      throws ConnectionFailedException, IllegalInputParameterException,
             IllegalAmountException, NotAuthenticatedException {
    // if teller is authenticated, make new Customer based on given information
    // check if teller is authenticated
    if (this.currentTellerAuthenticated == true) {
      // get roleId of customer
      List<Integer> roleIds = new ArrayList<Integer>();
      roleIds = DatabaseSelectHelper.getRoles();
      int roleId = -1;
      try {
        for (Integer id : roleIds) {
          // check if this id is the customer roleId
          if (DatabaseSelectHelper.getRole(id).equalsIgnoreCase(Roles.CUSTOMER.toString())) {
            roleId = id;
            break;
          }
        }
      } catch (ItemNotFoundException e) {
        // Do not expect an exception here
      }
      // make a new customer and add it to the database
      int newCustId = -1;
      try {
        newCustId = DatabaseInsertHelper.insertNewUser(name, age, address, roleId, password);
        System.out.println("New customer's id: " + newCustId);
      } catch (IllegalObjectTypeException e) {
        // Should only get here if customer role does not exist in database
        System.out.println("Customer role not created in database");
      }
    } else {
      throw new NotAuthenticatedException("Teller not authenticated");
    }
  }
  
  /**
   * Method that applies interest to account with accountId
   * @param accountId account with accountId to have interest applied.
   * @throws ConnectionFailedException if the connection to the database fails somehow
   * @throws ItemNotFoundException if given account does not exist in the database
   * @throws NotAuthenticatedException if either the teller or customer are not authenticated or
   *     if the customer does not own the input account
   * @throws IllegalInputParameterException 
   */
  public void giveInterest(int accountId)
      throws ItemNotFoundException, ConnectionFailedException, NotAuthenticatedException, IllegalInputParameterException {
    // if teller, user is authenticate and account belongs to the given user, give them interest
    // check if user and teller is authenticated
    if (this.isTellerAuthenticated() && this.isCustomerAuthenticated()) {
      // get all accounts associated with the currentCustomer
      List<Integer> userAccountIds = new ArrayList<Integer>();
      userAccountIds = DatabaseSelectHelper.getAccountIds(this.getCurrentCustomer().getId());
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
      } else {
        throw new NotAuthenticatedException("Customer does not own this account");
      }
    } else {
      throw new NotAuthenticatedException("Both user and teller must be authenticated");
    }
  }

  // METHODS TO IMPLEMENT FROM INTERFACES
  @Override
  public boolean authenticateTeller(String password)
      throws ItemNotFoundException, ConnectionFailedException {
    // authenticate this teller
    this.currentTellerAuthenticated = this.currentTeller.authenticate(password);
    return this.currentTellerAuthenticated;
  }

  @Override
  public void setCurrentTeller(Teller teller) {
    // sets the teller as the current teller
    this.currentTeller = teller;
  }

  @Override
  public void deauthenticateTeller() {
    // remove the current teller and set authentication to false
    this.currentTeller = null;
    this.currentTellerAuthenticated = false;
  }

  @Override
  public Teller getCurrentTeller() {
    return this.currentTeller;
  }

  @Override
  public boolean isTellerAuthenticated() {
    return this.currentTellerAuthenticated;
  }
  
  // METHODS FOR PHASE 3: UPDATING CURR CUSTOMER'S INFORMATION
  // CHANGE NAME, AGE, ADDRESS, 
  /**
   * Update the name for the current customer iff the teller and customer are authenticated.
   * @param newName new name to be updated to
   * @return true iff successful, else false
   * @throws ConnectionFailedException exception thrown if error connecting to database
   */
  public boolean updateCustomerName(String newName) throws ConnectionFailedException {
    // ensure customer and teller are authenticated
    if (this.currentTellerAuthenticated && this.isCustomerAuthenticated()) {
      // update the object's name
      this.currentCustomer.setName(newName);
      // update the database value
      DatabaseUpdateHelper.updateUserName(newName, this.currentCustomer.getId());
      return true;
    }
    return false;
  }
  
  /**
   * Update the age for the current customer iff the teller and customer are authenticated.
   * @param newAge new age to be updated to
   * @return true iff successful, else false
   * @throws ConnectionFailedException exception thrown if error connecting to database
   * @throws IllegalAmountException exception thrown if illegal number entered
   */
  public boolean updateCustomerAge(int newAge) throws ConnectionFailedException, IllegalAmountException {
    // ensure customer and teller are authenticated
    if (this.currentTellerAuthenticated && this.isCustomerAuthenticated()) {
      // update the object's age
      this.currentCustomer.setAge(newAge);
      // update the database value
      DatabaseUpdateHelper.updateUserAge(newAge, this.currentCustomer.getId());
      return true;
    }
    return false;
  }
  
  /**
   * Update the address for the current customer iff the teller and customer are authenticated.
   * @param newAddress new address to be updated to
   * @return true iff successful, else false 
   * @throws ConnectionFailedException exception thrown if error connecting to database
   * @throws IllegalInputParameterException exception thrown if parameter passed is illegal
   */
  public boolean updateCustomerAddres(String newAddress) throws ConnectionFailedException, IllegalInputParameterException {
    // ensure customer and teller are authenticated
    if (this.currentTellerAuthenticated && this.isCustomerAuthenticated()) {
      // update object's address
      this.currentCustomer.setAddress(newAddress);
      // update database value
      DatabaseUpdateHelper.updateUserAddress(newAddress, this.currentCustomer.getId());
      return true;
    }
    return false;
  }
}
