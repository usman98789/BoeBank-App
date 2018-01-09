package com.bank.databasehelper;

import com.bank.database.DatabaseInsertException;
import com.bank.database.DatabaseInserter;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException; // Not thrown
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountWithInterest;
import com.bank.generics.Roles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

public class DatabaseInsertHelper extends DatabaseInserter {
 
  /**
   * Insert a new account into account table.
   * @param name the name of the account.
   * @param balance the balance currently in account.
   * @param typeId the id of the type of the account.
   * @return accountId of inserted account, -1 otherwise
   * @throws ConnectionFailedException if something goes wrong with the database connection
   * @throws IllegalObjectTypeException if input account type not valid
   */
  public static int insertAccount(String name, BigDecimal balance, int typeId)
      throws ConnectionFailedException, IllegalObjectTypeException {
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    boolean validAccountType = false;
    // CHECK IF typeId IS IN ACCOUNTS TABLE
    for (Integer id : accountTypeIds) {
      if (typeId == id) {
        validAccountType = true;
        break;
      }
    }
    if (validAccountType) {
      int accountId;
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // round balance to 2 decimal places
      balance = balance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
      try {
        accountId = DatabaseInserter.insertAccount(name, balance, typeId, connection);
      } catch (DatabaseInsertException e) {
        accountId = -1; // -1 if insertion fails
      } finally {
        DatabaseDriverHelper.closeConnection(connection);
      }
      return accountId;
    } else {
      throw new IllegalObjectTypeException("Not a valid account type");
    }
  }
  
  /**
   * Insert an accountType into the accountType table.
   * @param name the name of the type of account.
   * @param interestRate the interest rate for this type of account.
   * @return the id of the account type, -1 if failure of some kind
   * @throws ConnectionFailedException if something goes wrong with the database connection
   * @throws IllegalAmountException if given interest rate is not in the allowed range
   * @throws IllegalObjectTypeException if the input account type is not allowed
   */
  public static int insertAccountType(String name, BigDecimal interestRate)
      throws ConnectionFailedException, IllegalAmountException, IllegalObjectTypeException {
    boolean accountTypeExists = false;
    int ret;
    
    // check account type already in database
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    String currName;
    for (Integer id : accountTypeIds) {
      try {
        currName = DatabaseSelectHelper.getAccountTypeName(id);
      } catch (ItemNotFoundException e) {
        // Item should be found, set currName to empty string though
        currName = "";
      }
      if (currName.equalsIgnoreCase(name)) {
        accountTypeExists = true;
      }
    }
    
    // Check to see if account type is allowed
    boolean validAccountType = false;
    AccountTypes[] accountTypeList = AccountTypes.values();
    for (AccountTypes accountType : accountTypeList) {
      if (accountType.name().equalsIgnoreCase(name)) {
        validAccountType = true;
      }
    }

    // check the interestRate bounds
    if (!AccountWithInterest.validInterestRate(interestRate)) {
      // If interest not in allowed range
      throw new IllegalAmountException("Interest must be between "
                  + AccountWithInterest.MIN_INTEREST.toString() + " and "
                  + AccountWithInterest.MAX_INTEREST.toString());
    } else if (accountTypeExists) {
      // If account type already in database
      return -1;
    } else if (!validAccountType) {
      // If account type is not valid
      throw new IllegalObjectTypeException("Given account type not allowed");
    } else {
      // if all requirements met, insert new user to database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        ret = DatabaseInserter.insertAccountType(name, interestRate, connection);
      } catch (DatabaseInsertException e) {
        ret = -1;
      }
      DatabaseDriverHelper.closeConnection(connection);
      return ret;
    }
  }
  
  /**
   * Use this to insert a new user into the database.
   * @param name the user's name.
   * @param age the user's age, must be positive
   * @param address the user's address, must be less than 100 characters
   * @param roleId the user's role.
   * @param password the user's password, not hashed
   * @return the account id if successful, -1 otherwise
   * @throws ConnectionFailedException if something goes wrong with the database connection
   * @throws IllegalObjectTypeException if the user's role is not valid
   * @throws IllegalInputParameterException if any input variable is invalid
   * @throws IllegalAmountException if age is not positive
   */
  public static int insertNewUser(String name, int age, String address, int roleId, String password)
      throws ConnectionFailedException, IllegalObjectTypeException,
      IllegalInputParameterException, IllegalAmountException { 
    int ret;
    List<Integer> listOfRoles = new ArrayList<Integer>();
    listOfRoles = DatabaseSelectHelper.getRoles();
    // check if role id in role table
    boolean found = listOfRoles.contains(roleId);
    
    // check address for 100 char limit
    if (address.length() > 100) {
      throw new IllegalInputParameterException("Address length too long");
    } else if (password.isEmpty()) {
      throw new IllegalInputParameterException("No password given");
    } else if (!found) {
      throw new IllegalObjectTypeException("Not a valid user role");
    } else if (age <= 0) {
      throw new IllegalAmountException("Age must be positive");
    } else {
      // if all requirements met, insert new user to database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        ret = DatabaseInserter.insertNewUser(name, age, address, roleId, password, connection);
      } catch (DatabaseInsertException e) {
        ret = -1;
      }
      DatabaseDriverHelper.closeConnection(connection);
      return ret;
    }
  }
  
  /**
   * Use this to insert new roles into the database.
   * @param role the new role to be added.
   * @return id of the role inserted, -1 if failure of some kind
   * @throws ConnectionFailedException if the connection to the database fails somehow
   * @throws IllegalObjectTypeException if the given role is not valid
   */
  public static int insertRole(String role)
      throws ConnectionFailedException, IllegalObjectTypeException {
    // check if role is in the enums
    boolean found = false;
    int ret;
    
    // check if role already in roles table
    List<Integer> roleIds = DatabaseSelectHelper.getRoles();
    String currRole;
    for (Integer roleId : roleIds) {
      try {
        currRole = DatabaseSelectHelper.getRole(roleId);
      } catch (ItemNotFoundException e) {
        currRole = ""; // Empty string if some kind of error pops up, set empty string for now
      }
      if (currRole.equalsIgnoreCase(role)) {
        found  = true;
      }
    }
    // Check if role is valid
    boolean roleIsValid = false;
    Roles[] roleList = Roles.values();
    for (Roles validRole : roleList) {
      if (validRole.name().equalsIgnoreCase(role)) {
        roleIsValid = true;
      }
    }
    // if role is already in roles table
    if (found == true) {
      // don't add
      return -1;
    } else if (!roleIsValid) {
      // If role is not valid
      throw new IllegalObjectTypeException("Invalid role");
    } else {
      // if it's not, then add to database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      try {
        ret = DatabaseInserter.insertRole(role, connection);
      } catch (DatabaseInsertException e) {
        ret = -1;
      }
      DatabaseDriverHelper.closeConnection(connection);
      return ret;
    }
  }
  
  /**
   * insert a user and account relationship.
   * @param userId the id of the user.
   * @param accountId the id of the account.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if the connection fails somehow
   * @throws ItemNotFoundException if either the user or the account are not found in the database
   */
  public static boolean insertUserAccount(int userId, int accountId)
      throws ItemNotFoundException, ConnectionFailedException {

    // Check to see if user and account exist
    // Below two calls will throw ItemNotFoundException if user or account not in database
    DatabaseSelectHelper.getUserDetails(userId);
    DatabaseSelectHelper.getAccountDetails(accountId);
    
    // Check to see if association between accounts already exists
    List<Integer> userAccounts = DatabaseSelectHelper.getAccountIds(userId);
    boolean alreadyExists = userAccounts.contains(accountId);
    
    if (alreadyExists) {
      // if association already exists
      return false;
    } else {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean successful;
      try {
        DatabaseInserter.insertUserAccount(userId, accountId, connection);
        successful = true;
      } catch (DatabaseInsertException e) {
        successful = false;
      }
      DatabaseDriverHelper.closeConnection(connection);
      return successful;
    }
  }
  
  /**
   * Puts a message in the database for the given recipient. The message must be 512 characters or
   * less. 
   * @param recipientId the id of the person who is to receive this message
   * @param message the message to leave
   * @return the id of the message, -1 if failure to insert
   * @throws IllegalInputParameterException if the message is too long
   * @throws ItemNotFoundException if the recipient is not in the database
   */
  public static int insertMessage(int recipientId, String message)
      throws IllegalInputParameterException, ItemNotFoundException, ConnectionFailedException {
    if (message.length() > 512) {
      throw new IllegalInputParameterException("Message too long");
    } else {
      // Will throw ItemNotFoundException if user not in database, ending function
      DatabaseSelectHelper.getUserDetails(recipientId);
      // If user found, continues on to attempt insertion
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      int messageId;
      try {
        messageId = DatabaseInserter.insertMessage(recipientId, message, connection);
      } catch (DatabaseInsertException e) {
        messageId = -1;  
      } finally {
        DatabaseDriverHelper.closeConnection(connection);
      }
      return messageId;
    }
  }
}
