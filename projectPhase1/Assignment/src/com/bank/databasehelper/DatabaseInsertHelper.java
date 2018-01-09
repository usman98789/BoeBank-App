package com.bank.databasehelper;

import com.bank.database.DatabaseInserter;
import com.bank.generics.*;
import com.bank.users.Customer;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseInsertHelper extends DatabaseInserter {
 
  /**
   * Insert a new account into account table.
   * @param name the name of the account.
   * @param balance the balance currently in account.
   * @param typeId the id of the type of the account.
   * @return accountId of inserted account, -1 otherwise
   */
  public static int insertAccount(String name, BigDecimal balance, int typeId) throws SQLException {
    //hint: You should be using these three lines in your final code
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    boolean found = false;
    // CHECK IF typeId IS IN ACCOUNTS TABLE
    for (int i = 0; i < accountTypeIds.size(); i++) {
      if (typeId == accountTypeIds.get(i)) {
        found = true;
        break;
      }
    }
    if (found == true) {
      int accountId;
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // round balance to 2 decimal places
      balance = balance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
      accountId = DatabaseInserter.insertAccount(name, balance, typeId, connection);
      connection.close();
      return accountId;
    } else {
      return -1;
    }
    
  }
  
  /**
   * insert an accountType into the accountType table.
   * @param name the name of the type of account.
   * @param interestRate the interest rate for this type of account.
   * @return true if successful, false otherwise.
   */
  public static boolean insertAccountType(String name, BigDecimal interestRate)
      throws SQLException {
    //hint: You should be using these three lines in your final code:
    
    // set upper and lower bounds for interestRate
    BigDecimal upperBound = new BigDecimal("1.0");
    BigDecimal lowerBound = new BigDecimal("0");
    int res1 = interestRate.compareTo(upperBound);
    int res2 = interestRate.compareTo(lowerBound);
    boolean exists = false;
    boolean ret;
    
    
    // check account type already in database
    List<Integer> accountIds = new ArrayList<Integer>();
    accountIds = DatabaseSelectHelper.getAccountTypesIds();
    for (int i = 0; i < accountIds.size(); i++) {
      String tempAccountName = DatabaseSelectHelper.getAccountTypeName(accountIds.get(i));
      if (tempAccountName.equals(name)) {
        exists = true;
      }
    }
    
    // check the interestRate bounds
    if (res2 == -1 || res1 == 1 || exists == true) {
      return false;
    } else {
      // if all reqs met, insert new user to database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      ret = DatabaseInserter.insertAccountType(name, interestRate, connection);
      connection.close();
      return ret;
    }
  }
  
  /**
   * Use this to insert a new user.
   * @param name the user's name.
   * @param age the user's age.
   * @param address the user's address.
   * @param roleId the user's role.
   * @param password the user's password (not hashsed).
   * @return the account id if successful, -1 otherwise
   */
  public static int insertNewUser(String name, int age, String address, int roleId, String password)
      throws SQLException {
    //hint: You should be using these three lines in your final code:    
    int ret;
    List<Integer> listOfRoles = new ArrayList<Integer>();
    listOfRoles = DatabaseSelectHelper.getRoles();
    boolean found = false;
    // check if roleId matches id from role table    
    for (int i = 0; i < listOfRoles.size(); i++) {
      if (roleId == listOfRoles.get(i)) {
        found = true;
      }
    }
    // check address for 100 char limit
    if (address.length() > 100 || found == false) {
      return -1;
    }
    else {
      // if all reqs met, insert new user to database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      ret = DatabaseInserter.insertNewUser(name, age, address, roleId, password, connection);
      connection.close();
      return ret;
    }
   
  }
  
  /**
   * Use this to insert new roles into the database.
   * @param role the new role to be added.
   * @return true if successful, false otherwise.
   */
  public static boolean insertRole(String role) throws SQLException {
    //hint: You should be using these three lines in your final code:
    // check if role is in the enums
    boolean found = false;
    boolean res;
    
    // check if role already in roles table
    List<Integer> roleIds = new ArrayList<Integer>();
    roleIds = DatabaseSelectHelper.getRoles();
    for (int i = 0; i < roleIds.size(); i++) {
      String tempRoleName = DatabaseSelectHelper.getRole(roleIds.get(i));
      if (role.equalsIgnoreCase(tempRoleName)) {
        found = true;
      }
    }
    // if role is already in roles table
    if (found == true) {
      // don't add
      return false;
    } else {
      // if it's not, then add to database
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      res = DatabaseInserter.insertRole(role, connection);
      connection.close();
      return res;
    }
  }
  
  /**
   * insert a user and account relationship.
   * @param userId the id of the user.
   * @param accountId the id of the account.
   * @return true if successful, false otherwise.
   */
  public static boolean insertUserAccount(int userId, int accountId) throws SQLException {
    //hint: You should be using these three lines in your final code:    
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();

    // check that user Id is in the users table    
    if (DatabaseSelectHelper.getUserDetails(userId) == null) {
      connection.close();
      return false;
    } else {
      Account a = DatabaseSelectHelper.getAccountDetails(accountId);
      // check if account Id is in accounts table
      if (a != null) {
        Customer c = (Customer) DatabaseSelectHelper.getUserDetails(userId);
        // add account to the association
        c.addAccount(a);
        boolean res = DatabaseInserter.insertUserAccount(userId, accountId, connection);
        connection.close();
        return res;
      }
    }
    connection.close();
    return false;
  }
}
