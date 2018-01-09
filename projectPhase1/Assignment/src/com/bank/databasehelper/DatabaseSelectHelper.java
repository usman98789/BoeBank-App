package com.bank.databasehelper;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.bank.accounts.ChequingAccount;
import com.bank.accounts.SavingsAccount;
import com.bank.accounts.Tfsa;
import com.bank.database.DatabaseDriver;
import com.bank.database.DatabaseInserter;
import com.bank.database.DatabaseSelector;
import com.bank.generics.Account;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.generics.User;
import com.bank.users.*;

public class DatabaseSelectHelper extends DatabaseSelector {
    
  /**
   * get the role with id id.
   * @param id the id of the role
   * @return a String containing the role.
   * @throws SQLException thrown when something goes wrong with query.
   */
  public static String getRole(int id) throws SQLException  {
    //hint: You should be using these three lines in your final code

    boolean found = false;   
    // check id is in the roleId table
    List<Integer> listOfRoles = new ArrayList<Integer>();
    // get the ids of roles in the database
    listOfRoles = DatabaseSelectHelper.getRoles();
    for (int i = 0; i < listOfRoles.size(); i++) {
      if (listOfRoles.get(i) == id) {
        found = true;
        break;
      }
    }
    
    if (found == true) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      String role = DatabaseSelector.getRole(id, connection);
      connection.close();
      return role;
    } else {
      throw new SQLException("Role not found.");
    }
   
  }
   
  /**
   * get the hashed version of the password.
   * @param userId the user's id.
   * @return the hashed password to be checked against given password.
   * @throws SQLException if a database issue occurs. 
   */
  public static String getPassword(int userId) throws SQLException  {
    //hint: You should be using these three lines in your final code
    // check that userId is in the users table
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet userDetails = DatabaseSelector.getUserDetails(userId, connection);
    // check if ResultSet is empty
    // if userDetails is empty
    if (!userDetails.isBeforeFirst()) {
      connection.close();
      throw new SQLException("User not found.");
    } else {
      // return hashed password from database
      String hashPassword = DatabaseSelector.getPassword(userId, connection);
      connection.close();
      return hashPassword;
    }
    
  }
  
  /**
   * find all the details about a given user.
   * @param userId the id of the user.
   * @return a result set with the details of the user.
   * @throws SQLException thrown when something goes wrong with query.
   */
  public static User getUserDetails(int userId) throws SQLException {
    //hint: The below code should help you out a little
    // check if the userId is in users table
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getUserDetails(userId, connection);
    // check if results are empty
    if (!results.isBeforeFirst()) {
      connection.close();
      throw new SQLException("User not found.");
    } else {
      User returnUser = null;
      while (results.next()) {
        //It may help you to figure out the user's type, and make an obj based on its
        
        String name = results.getString("NAME");
        int age = results.getInt("AGE");
        String address = results.getString("ADDRESS");                            
        // get user's role id
        int userRoleId = results.getInt("ROLEID");
        // get role name
        String userRoleString = DatabaseSelectHelper.getRole(userRoleId);
        // iterate through all roles to determine which user to make        
        
        if (userRoleString.equalsIgnoreCase(Roles.ADMIN.name())) {
          // make admin object
          returnUser = new Admin(userId, name, age, address);
        } else if (userRoleString.equalsIgnoreCase(Roles.CUSTOMER.name())) {
          // make customer object
          System.out.println("made customer");
          returnUser = new Customer(userId, name, age, address);
        } else if (userRoleString.equalsIgnoreCase(Roles.TELLER.name())) {
          //make teller object
          returnUser = new Teller(userId, name, age, address);
        }
        
      }
      connection.close();
      return returnUser;
    }
  }
 
  /**
   * return the id's of all of a user's accounts.
   * @param userId the id of the user.
   * @return List a list containing all ids for accounts.
   * @throws SQLException thrown when something goes wrong with query.
   */
  public static List<Integer> getAccountIds(int userId) throws SQLException {
    //hint: The below code should help you out a little
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountIds(userId, connection);

    List<Integer> accountIds = new ArrayList<Integer>();   
    while (results.next()) {   
      accountIds.add(results.getInt("ACCOUNTID"));
    }
    // close connection
    connection.close();
    // return ids
    return accountIds;
    
  }
  
  /**
   * get the full details of an account.
   * @param accountId the id of the account
   * @return acount Account object with the details of the account.
   * @throws SQLException thrown when something goes wrong with query.
   */
  public static Account getAccountDetails(int accountId) throws SQLException {
    //hint: The below code should help you out a little
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountDetails(accountId, connection);
    // check if valid accountId
    if (!results.isBeforeFirst()) {
      connection.close();
      throw new SQLException("Account not found.");
    } else {
      Account returnAccount = null;
      while (results.next()) {
        //It may help you to figure out the account type, and make an obj based on its
        String name = results.getString("NAME");
        BigDecimal balance = new BigDecimal(results.getString("BALANCE"));
        int accountTypeId = results.getInt("TYPE"); 
        String accountTypeName = DatabaseSelectHelper.getAccountTypeName(accountTypeId, connection);
        
        // determine which type of account this is
        if (accountTypeName.equalsIgnoreCase(AccountTypes.CHEQUING.name())) {
          // create chequing object
          returnAccount = new ChequingAccount(accountId, name, balance);
        }
        else if (accountTypeName.equalsIgnoreCase(AccountTypes.SAVING.name())) {
          // create savings object
          returnAccount = new SavingsAccount(accountId, name, balance);
        } else if (accountTypeName.equalsIgnoreCase(AccountTypes.TFSA.name())) {
          // create TFSA object
          returnAccount = new Tfsa(accountId, name, balance); 
        }
      }
      connection.close();
      return returnAccount;
    }
  }
  
  /**
   * return the balance in the account.
   * @param accountId the account to check.
   * @return the balance of the account
   * @throws SQLException thrown when something goes wrong with query.
   */
  public static BigDecimal getBalance(int accountId) throws SQLException {
    //hint: The below code should help you out a little
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // check if valid accountId
    Account account = DatabaseSelectHelper.getAccountDetails(accountId);
    // if not valid accountId
    if (account == null) {
      connection.close();
      throw new SQLException("Account not found.");
    } else {
      // if valid accountId, get balance and return it
      BigDecimal balance = DatabaseSelector.getBalance(accountId, connection);
      connection.close();
      return balance;
    } 
  }
 
  /**
   * Get the interest rate for an account.
   * @param accountType the type for the account.
   * @return the interest rate.
   * @throws SQLException thrown if something goes wrong with the query.
   */
  public static BigDecimal getInterestRate(int accountType) throws SQLException {
    //hint: The below code should help you out a little
    // check if valid accountTypeId
    boolean found = false;
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    // iterate through list and see if accountType is in it
    for (int i = 0; i < accountTypeIds.size(); i++) {
      if (accountType == accountTypeIds.get(i)) {
        found = true;
        break;
      }
    }
    if (found == true) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      BigDecimal interestRate = DatabaseSelector.getInterestRate(accountType, connection);
      connection.close();
      return interestRate;
    } else {
      throw new SQLException("Account type not found.");
    }
    
  }
  
  /**
   * Return all data found within the AccountTypes table.
   * @return a list of all ids for accounts in the database.
   * @throws SQLException thrown if there is an issue.
   */
  public static List<Integer> getAccountTypesIds() throws SQLException {
    // returns a list of IDs for all account types
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getAccountTypesId(connection);
    List<Integer> ids = new ArrayList<>();
    while (results.next()) {
      ids.add(results.getInt("ID"));
    }
    connection.close();
    return ids;
  }
  
  /**
   * Return the accounttype name given an accountTypeId.
   * @param accountTypeId the id of the account type.
   * @return The name of the account type.
   * @throws SQLException thrown if something goes wrong.
   */
  public static String getAccountTypeName(int accountTypeId) throws SQLException {
    // get accountTypesIds
    List<Integer> accountTypeIds = new ArrayList<Integer>();
    accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
    boolean found = false;
    // check if valid accountTypeId
    for (int i = 0; i < accountTypeIds.size(); i++) {
      if (accountTypeIds.get(i) == accountTypeId) {
        found = true;
        break;
      }
    }
    if (found == true) {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      String accountName = DatabaseSelector.getAccountTypeName(accountTypeId, connection);
      connection.close();
      return accountName;
    } else {
      throw new SQLException("Account type not found.");
    }
  }
  
  /**
   * get all the roles.
   * @return a List containing all ids of the roles table.
   * @throws SQLException thrown if an SQLException occurs.
   */
  public static List<Integer> getRoles() throws SQLException {
    // returns a list of ID numbers for roles
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results = DatabaseSelector.getRoles(connection);
    List<Integer> ids = new ArrayList<>();
    while (results.next()) {
      ids.add(results.getInt("ID"));
    }
    connection.close();
    return ids;
  }

  /**
   * get the typeId of the account.
   * @param accountId the accounts id
   * @return the typeId
   * @throws SQLException thrown if something goes wrong.
   */
  public static int getAccountType(int accountId) throws SQLException {
    // check if accountId is a valid id in accounts table
    Account account = DatabaseSelectHelper.getAccountDetails(accountId);
    if (account == null) {
      // return -1 to signify error
      throw new SQLException("Account not found.");
    } else {
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();  
      int accountTypeId = DatabaseSelector.getAccountType(accountId, connection);
      connection.close();
      return accountTypeId;
    }  
  }
  
  /**
   * get the role of the given user.
   * @param userId the id of the user.
   * @return the roleId for the user.
   * @throws SQLException thrown if something goes wrong with the query.
   */
  public static int getUserRole(int userId) throws SQLException {
    // check if userId is in the user table
    User u = DatabaseSelectHelper.getUserDetails(userId);
    // if user is not in the users table
    if (u == null) {
      // -1 if failed
      throw new SQLException("User not found.");
    } else {
      //if user is in the users table 
      Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
      // get the user's role from database
      int userRoleId = DatabaseSelector.getUserRole(userId, connection);
      connection.close();
      return userRoleId;
    }
  }
  
}
