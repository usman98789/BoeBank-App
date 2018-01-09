package com.bank.databasehelper;

import com.bank.database.DatabaseUpdater;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class DatabaseUpdateHelper extends DatabaseUpdater{
  
  /**
   * Update the role name of a given role in the role table.
   * @param name the new name of the role.
   * @param id the current ID of the role.
   * @return true if successful, false otherwise.
   */
  public static boolean updateRoleName(String name, int id) throws SQLException {
    // check name matches one from enum
    List<Integer> roleIds = new ArrayList<Integer>();
    roleIds = DatabaseSelectHelper.getRoles();
    boolean match = false;
    match = roleIds.contains(id);

    if (match == true) {
      //hint: You should be using these three lines in your final code
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateRoleName(name, id, connection);
      connection.close();
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
   */
  public static boolean updateUserName(String name, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateUserName(name, id, connection);
    connection.close();
    return complete;
  }
  
  /**
   * Use this to update the user's age.
   * @param age the new age.
   * @param id the current id
   * @return true if it succeeds, false otherwise.
   */
  public static boolean updateUserAge(int age, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateUserAge(age, id, connection);
    connection.close();
    return complete;
  }
  
  /**
   * update the role of the user.
   * @param roleId the new role.
   * @param id the current id.
   * @return true if successful, false otherwise.
   */
  public static boolean updateUserRole(int roleId, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    // see if roleId matches an Id in roles table
    List<Integer> roleIds = new ArrayList<Integer>();
    roleIds = DatabaseSelectHelper.getRoles();
    // if roleId exists in roles table
    if (roleIds.contains(id)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateUserRole(roleId, id, connection);
      connection.close();
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
   */
  public static boolean updateUserAddress(String address, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    // check if address is longer than 100 chars
    if (address.length() > 100) {
      return false;
    } else {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateUserAddress(address, id, connection);
      connection.close();
      return complete;
    }
  }

  /**
   * update the name of the account.
   * @param name the new name for the account.
   * @param id the id of the account to be changed.
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountName(String name, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateAccountName(name, id, connection);
    connection.close();
    return complete;
  }
  
 
  /**
   * update the account balance.
   * @param balance the new balance for the account.
   * @param id the id of the account.
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountBalance(BigDecimal balance, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    // round balance to 2 decimal places
    balance = balance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    boolean complete = DatabaseUpdater.updateAccountBalance(balance, id, connection);
    connection.close();
    return complete;
  }
  
 
  /**
   * update the type of the account.
   * @param typeId the new type for the account. 
   * @param id the id of the account to be updated.
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountType(int typeId, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    // check if account typeId is in AccountTypes table
    if (accountTypeIds.contains(typeId)) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateAccountType(typeId, id, connection);
      connection.close();
      return complete;
    } else {
      return false;
    }
  }
  
  /**
   * update the name of an accountType.
   * @param name the new name to be given.
   * @param id the id of the accountType.
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountTypeName(String name, int id) throws SQLException {
    //hint: You should be using these three lines in your final code
    // check if name is in enum for accounts
    boolean found = false;
    
    List<Integer> accountIds = new ArrayList<Integer>();
    accountIds = DatabaseSelectHelper.getAccountTypesIds();
    found = accountIds.contains(id);
    
    // if name is a valid account type name
    if (found == true) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete = DatabaseUpdater.updateAccountTypeName(name, id, connection);
      connection.close();
      return complete;
    } else {
      return false;
    }
    
  }
  
  /**
   * update the interest rate for this account type.
   * @param interestRate the interest rate to be updated to.
   * @param id the id of the accountType.
   * @return true if successful, false otherwise.
   */
  public static boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id)
      throws SQLException {
    //hint: You should be using these three lines in your final code
    // set upper and lower bounds for valid interestRate
    BigDecimal upperBound = new BigDecimal("1.0");
    BigDecimal lowerBound = new BigDecimal("0.0");
    int res1 = interestRate.compareTo(upperBound);
    int res2 = interestRate.compareTo(lowerBound);
    boolean found = false;
    // check if account type exists
    List<Integer> accountIds = new ArrayList<Integer>();
    accountIds = DatabaseSelectHelper.getAccountTypesIds();
    found = accountIds.contains(id);
    
    // check that interestRate is between 0 and 1
    if (res2 == -1 || res1 == 1 || found == false) {
      return false;
    } else {
      // if interestRate is valid
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      boolean complete;
      complete = DatabaseUpdater.updateAccountTypeInterestRate(interestRate, id, connection);
      connection.close();
      return complete;
    }
  }
}
