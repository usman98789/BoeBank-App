package com.bank.databasehelper;

import com.bank.database.DatabaseUpdater;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException; // Not thrown
import com.bank.generics.Account;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesMap;
import com.bank.generics.AccountWithInterest;
import com.bank.generics.Roles;
import com.bank.users.Customer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


public class DatabaseUpdateHelper extends DatabaseUpdater {
  
  private static final BigDecimal MIN_SAVINGS_BALANCE = new BigDecimal(1000); 
  
  /**
   * Update the role name of a given role in the role table.
   * @param name the new name of the role.
   * @param id the current ID of the role.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if the connection fails somehow
   */
  public static boolean updateRoleName(String name, int id) throws ConnectionFailedException {
    // check if account name is valid
    boolean valid = false;
    Roles[] validRoles = Roles.values();
    for (Roles roleName : validRoles) {
      if (roleName.name().equalsIgnoreCase(name)) {
        valid = true;
      }
    }
    // Check to ensure we aren't duplicating any names
    // Checks to see if account name already exists in database
    String currRoleName;
    List<Integer> typesInDatabase = DatabaseSelectHelper.getRoles();
    for (Integer typeId : typesInDatabase) {
      if (typeId != id) {
        // If not looking at id we are changing, check this id name
        try {
          currRoleName = DatabaseSelectHelper.getRole(typeId);
        } catch (ItemNotFoundException e) {
          currRoleName = ""; // Should not encounter this exception, empty string just in case
        }
        if (currRoleName.equalsIgnoreCase(name)) {
          // If it is in the database, return false
          return false;
        }
      }
    }

    if (valid) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateRoleName(name, id, connection);
      DatabaseDriverHelper.closeConnection(connection);
      return complete;
    } else {
      return false;
    }
  }
  
  /**
   * Use this to update the user's name.
   * @param name the new name
   * @param id the current id
   * @return true if it works, false otherwise.
   * @throws ConnectionFailedException if the connection to the database fails somehow
   */
  public static boolean updateUserName(String name, int id) throws ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateUserName(name, id, connection);
    DatabaseDriverHelper.closeConnection(connection);
    return complete;
  }
  
  /**
   * Use this to update the user's age.
   * @param age the new age.
   * @param id the current id
   * @return true if it succeeds, false otherwise.
   */
  public static boolean updateUserAge(int age, int id)
      throws ConnectionFailedException, IllegalAmountException {
    if (age > 0) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateUserAge(age, id, connection);
      DatabaseDriverHelper.closeConnection(connection);
      return complete;
    } else {
      throw new IllegalAmountException("Age must be a positive number");
    }
  }
  
  /**
   * update the role of the user.
   * @param roleId the new role.
   * @param id the current id.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if something goes wrong with the connection
   */
  public static boolean updateUserRole(int roleId, int id) throws ConnectionFailedException {
    // see if roleId matches an Id in roles table
    List<Integer> roleIds = new ArrayList<Integer>();
    roleIds = DatabaseSelectHelper.getRoles();
    // if roleId exists in roles table
    if (roleIds.contains(id)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateUserRole(roleId, id, connection);
      DatabaseDriverHelper.closeConnection(connection);
      return complete;
    } else {
      return false;
    }
  }
  
  /**
   * Use this to update user's address.
   * @param address the new address.
   * @param id the current id.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if connection to database fails somehow
   */
  public static boolean updateUserAddress(String address, int id)
      throws ConnectionFailedException, IllegalInputParameterException {
    // check if address is longer than 100 chars
    if (address.length() > 100) {
      throw new IllegalInputParameterException("Address is too long");
    } else {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateUserAddress(address, id, connection);
      DatabaseDriverHelper.closeConnection(connection);
      return complete;
    }
  }

  /**
   * update the name of the account.
   * @param name the new name for the account.
   * @param id the id of the account to be changed.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if the connection to the database fails somehow
   */
  public static boolean updateAccountName(String name, int id) throws ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateAccountName(name, id, connection);
    DatabaseDriverHelper.closeConnection(connection);
    return complete;
  }
  
 
  /**
   * Updates the account balance. If a savings account has its balance updated to below $1000, it
   * will be converted to a chequing account.
   * @param balance the new balance for the account.
   * @param id the id of the account.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if the connection to the database fails somehow
   * @throws IllegalInputParameterException exception thrown if illegal params passed
   */
  public static boolean updateAccountBalance(BigDecimal balance, int id)
      throws ConnectionFailedException, IllegalInputParameterException {
    // round balance to 2 decimal places
    balance = balance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    if (balance.compareTo(MIN_SAVINGS_BALANCE) <= 0) {
      // If balance is less than 1000, check the account type
      int savingsType = AccountTypesMap.getAccountTypeID("SAVING");
      try {
        if (DatabaseSelectHelper.getAccountType(id) == savingsType) {
          // If the account is a savings account, convert to a chequing account
          DatabaseUpdateHelper.updateAccountType(AccountTypesMap.getAccountTypeID("CHEQUING"), id);
          // notify all customers associated with this account of the change via message
          int currCustomerId = 1;
          boolean loopedThroughAll = false;
          
          // loop through all users
          while (!loopedThroughAll) {
            try {
              // check if user is a customer
              if (DatabaseSelectHelper.getUserDetails(currCustomerId) instanceof Customer) {
                // check if this user has the account with accountId
                List<Integer> customerAccs = new ArrayList<Integer>();
                customerAccs = DatabaseSelectHelper.getAccountIds(currCustomerId);
                // check if user has account with id
                if (customerAccs.contains(id)) {
                  // add message to the user
                 String msg = "Your savings account has become a chequing account.";
                 DatabaseInsertHelper.insertMessage(currCustomerId, msg); 
                }
              } else if (DatabaseSelectHelper.getUserDetails(currCustomerId) == null) {
                // if we get null, then we finished looping
                loopedThroughAll = true;
              }
            } catch (ItemNotFoundException e) {
              loopedThroughAll = true;
            } catch (ConnectionFailedException e) {
              loopedThroughAll = true;
            }
            // go to the next customer
            currCustomerId++;
          }
        }
      } catch (ItemNotFoundException e) {
        return false; // Return false if account not found in database
      }
    }
    boolean complete = DatabaseUpdater.updateAccountBalance(balance, id, connection);
    DatabaseDriverHelper.closeConnection(connection);
    return complete;
  }
  
 
  /**
   * Updates the type of the given account.
   * @param typeId the new type for the account. 
   * @param id the id of the account to be updated.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if the connection to the database fails somehow
   */
  public static boolean updateAccountType(int typeId, int id) throws ConnectionFailedException {
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    // check if account typeId is in AccountTypes table
    if (accountTypeIds.contains(typeId)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateAccountType(typeId, id, connection);
      DatabaseDriverHelper.closeConnection(connection);
      return complete;
    } else {
      return false;
    }
  }
  
  /**
   * Update the name of an accountType.
   * @param name the new name to be given.
   * @param id the id of the accountType.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if the connection to the database fails somehow
   * @throws IllegalObjectTypeException if the given name is not allowed for an account type
   */
  public static boolean updateAccountTypeName(String name, int id)
      throws ConnectionFailedException, IllegalObjectTypeException {
    // check if account name is valid
    boolean valid = false;
    AccountTypes[] validAccountTypes = AccountTypes.values();
    for (AccountTypes type : validAccountTypes) {
      if (type.name().equalsIgnoreCase(name)) {
        valid = true;
      }
    }
    // Checks to see if account name already exists in database
    String currAccountType;
    List<Integer> typesInDatabase = DatabaseSelectHelper.getAccountTypesIds();
    for (Integer typeId : typesInDatabase) {
      if (typeId != id) {
        // If not looking at id we are changing, check this id name
        try {
          currAccountType = DatabaseSelectHelper.getAccountTypeName(typeId);
        } catch (ItemNotFoundException e) {
          currAccountType = ""; // Should not encounter this exception, empty string just in case
        }
        if (currAccountType.equalsIgnoreCase(name)) {
          // If it is in the database, return false
          return false;
        }
      }
    }
    
    // See if account is valid
    List<Integer> accountIds = new ArrayList<Integer>();
    accountIds = DatabaseSelectHelper.getAccountTypesIds();
    boolean found = accountIds.contains(id);
    
    if (!valid) {
      // if account type name is not valid
      throw new IllegalObjectTypeException("Given account type name is not valid");
    } else if (found == true) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateAccountTypeName(name, id, connection);
      DatabaseDriverHelper.closeConnection(connection);
      return complete;
    } else {
      return false;
    }
    
  }
  
  /**
   * Update the interest rate for this account type.
   * @param interestRate the interest rate to be updated to.
   * @param id the id of the accountType.
   * @return true if successful, false otherwise.
   * @throws ConnectionFailedException if the connection to the database fails somehow
   * @throws IllegalAmountException if interest value is invalid
   */
  public static boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id)
      throws ConnectionFailedException, IllegalAmountException {
    boolean found = false;
    // check if account type exists
    List<Integer> accountIds = new ArrayList<Integer>();
    accountIds = DatabaseSelectHelper.getAccountTypesIds();
    found = accountIds.contains(id);
    
    if (!AccountWithInterest.validInterestRate(interestRate)) {
      // If interest rate is invalid
      throw new IllegalAmountException("Interest must be between "
                 + AccountWithInterest.MIN_INTEREST.toString() + " and "
                 + AccountWithInterest.MAX_INTEREST.toString());
    } else if (found) {
      // If account exists and interest is valid, update it
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete;
      complete = DatabaseUpdater.updateAccountTypeInterestRate(interestRate, id, connection);
      DatabaseDriverHelper.closeConnection(connection);
      return complete;
    } else {
      return false;
    }
  }
  
  /**
   * Updates the hashed version of the password in the database
   * @param userId <integer> the id of the user
   * @param userId <String> the hashed version of the new password
   * @return whether or not update was successful
   * @throws ConnectionFailedException if connection to the database fails somehow
   */
  public static boolean updateUserPassword(int userId, String hashedPassword) 
      throws ConnectionFailedException {
    boolean successful = false;
    
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    successful = DatabaseUpdater.updateUserPassword(hashedPassword, userId, connection);
    DatabaseDriverHelper.closeConnection(connection);
    
    return successful;
  }
  
  /**
   * Sets the status of the given message to 'viewed'.
   * @param messageId the message that was viewed
   * @return whether or not update was successful
   * @throws ConnectionFailedException if connection to the database fails somehow
   */
  public static boolean updateMessageStatus(int messageId) throws ConnectionFailedException {
    boolean successful = false;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    successful = DatabaseUpdater.updateUserMessageState(messageId, connection);
    DatabaseDriverHelper.closeConnection(connection);
    return successful;
  }
}
