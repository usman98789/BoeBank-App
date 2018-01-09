package com.bank.databasehelper;

import com.bank.accounts.ChequingAccount;
import com.bank.accounts.Loan;
import com.bank.accounts.RestrictedSavingsAccount;
import com.bank.accounts.SavingsAccount;
import com.bank.accounts.Tfsa;
import com.bank.database.DatabaseSelector;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.generics.User;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class DatabaseSelectHelper extends DatabaseSelector {
    
  /**
   * Returns the name of the role with the given id.
   * @param id the id of the role
   * @return name of the role
   * @throws ItemNotFoundException if role cannot be found in database
   * @throws ConnectionFailedException if something goes wrong with the connection
   */
  public static String getRole(int id) throws ItemNotFoundException, ConnectionFailedException {
    String role;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      role = DatabaseSelector.getRole(id, connection);
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      // always try to close connection
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (role == null) {
      throw new ItemNotFoundException("Role not in database");
    } else {
      return role;
    }
  }
   
  /**
   * Gets a hashed version of the given users password.
   * @param userId the user to get the password of
   * @return their password - hashed
   * @throws ConnectionFailedException if something goes wrong with database connection
   * @throws ItemNotFoundException if the user and/or their password are not in the database
   */
  public static String getPassword(int userId)
      throws ItemNotFoundException, ConnectionFailedException {
    String hashPassword;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      hashPassword = DatabaseSelector.getPassword(userId, connection);
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      // always try to close connection
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (hashPassword == null) {
      throw new ItemNotFoundException("User or password not in database");
    } else {
      return hashPassword;
    }
  }

  /**
   * find all the details about a given user.
   * @param userId the id of the user.
   * @return an object representing the user given
   * @throws ItemNotFoundException if user not found in database
   * @throws ConnectionFailedException if something goes wrong with the database connection
   */
  public static User getUserDetails(int userId)
      throws ItemNotFoundException, ConnectionFailedException {
    // check if the userId is in users table
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results;
    User output = null;
    try {
      results = DatabaseSelector.getUserDetails(userId, connection);
      if (results.next()) {
        // Get information about user
        String name = results.getString("NAME");
        int age = results.getInt("AGE");
        String address = results.getString("ADDRESS");                            
        int userRoleId = results.getInt("ROLEID");
        // get role name
        String userRoleString = DatabaseSelectHelper.getRole(userRoleId);
        // Determine type of output based on role
        if (userRoleString.equalsIgnoreCase(Roles.ADMIN.name())) {
          // make admin object
          output = new Admin(userId, name, age, address);
        } else if (userRoleString.equalsIgnoreCase(Roles.CUSTOMER.name())) {
          // make customer object
          output = new Customer(userId, name, age, address);
        } else if (userRoleString.equalsIgnoreCase(Roles.TELLER.name())) {
          //make teller object
          output = new Teller(userId, name, age, address);
        }
        output.setRole(userRoleId);
      }
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (output == null) {
      throw new ItemNotFoundException("User not in database");
    } else {
      return output;
    }
  }
 
  /**
   * Return the id's of all of a user's accounts. If the given id has no accounts associated with
   * it, an empty list is returned. An invalid userId also returns an empty list, as an invalid id
   * would also not have any accounts associated with it.
   * @param userId the id of the user.
   * @return List a list containing all ids for accounts
   * @throws ConnectionFailedException if connection to database fails somehow 
   */
  public static List<Integer> getAccountIds(int userId) throws ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    List<Integer> output = new ArrayList<Integer>();
    try {
      ResultSet results = DatabaseSelector.getAccountIds(userId, connection);
      while (results.next()) {
        output.add(results.getInt("ACCOUNTID"));
      }
    } catch (SQLException e) {
      // Something went wrong with looking up the account ids, likely an invalid user id
      output = new ArrayList<Integer>();
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    return output;
  }
  
  /**
   * Returns an account object to represent the given account.
   * @param accountId the id of the account
   * @return an Account object with the details of the account
   * @throws ItemNotFoundException if account not found in database
   * @throws ConnectionFailedException if something goes wrong with the connection
   */
  public static Account getAccountDetails(int accountId)
      throws ItemNotFoundException, ConnectionFailedException {
    Account output = null;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      ResultSet results = DatabaseSelector.getAccountDetails(accountId, connection);
      if (results.next()) {
        String name = results.getString("NAME");
        BigDecimal balance = new BigDecimal(results.getString("BALANCE"));
        int accountTypeId = results.getInt("TYPE"); 
        String accountTypeName = DatabaseSelectHelper.getAccountTypeName(accountTypeId, connection);
        // Determine which type of account this is
        if (accountTypeName.equalsIgnoreCase(AccountTypes.CHEQUING.name())) {
          // create ChequingAccount object
          output = new ChequingAccount(accountId, name, balance);
        } else if (accountTypeName.equalsIgnoreCase(AccountTypes.SAVING.name())) {
          // create SavingsAccount object
          output = new SavingsAccount(accountId, name, balance);
        } else if (accountTypeName.equalsIgnoreCase(AccountTypes.TFSA.name())) {
          // create TFSA object
          output = new Tfsa(accountId, name, balance); 
        } else if (accountTypeName.equalsIgnoreCase(AccountTypes.RESTRICTEDSAVING.name())) {
          // Create restricted savings account
          output = new RestrictedSavingsAccount(accountId, name, balance);
        } else if (accountTypeName.equalsIgnoreCase(AccountTypes.LOAN.name())) {
          output = new Loan(accountId, name, balance);
        }
        output.setType(accountTypeId);
      }
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (output == null) {
      throw new ItemNotFoundException("Account not found in database");
    } else {
      return output;
    }
  }
  
  /**
   * Gets the balance of the given account.
   * @param accountId the account to check.
   * @return the balance of the account
   * @throws ItemNotFoundException if the account could not be found
   * @throws ConnectionFailedException if something goes wrong with the connection
   */
  public static BigDecimal getBalance(int accountId)
      throws ItemNotFoundException, ConnectionFailedException {
    BigDecimal balance = null;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      balance = DatabaseSelector.getBalance(accountId, connection);
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (balance == null) {
      throw new ItemNotFoundException("Account not found in database");
    } else {
      return balance;
    }
  }
 
  /**
   * Gets the interest rate for a given account type.
   * @param accountType the type for the account
   * @return the interest rate
   * @throws ItemNotFoundException if the given account type is not in the database
   * @throws ConnectionFailedException if the connection to the database fails somehow
   */
  public static BigDecimal getInterestRate(int accountType)
      throws ItemNotFoundException, ConnectionFailedException {
    BigDecimal interestRate = null;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      interestRate = DatabaseSelector.getInterestRate(accountType, connection);
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (interestRate == null) {
      throw new ItemNotFoundException("Account type not found in database");
    } else {
      return interestRate;
    }
  }
  
  /**
   * Gets the ids for available account types in the database.
   * @return a list of all the ids for account types in the database
   * @throws ConnectionFailedException if something goes wrong with the database connection
   */
  public static List<Integer> getAccountTypesIds() throws ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    ResultSet results;
    List<Integer> ids = new ArrayList<Integer>();
    try {
      results = DatabaseSelector.getAccountTypesId(connection);
      while (results.next()) {
        // Add every ID to output
        ids.add(results.getInt("ID"));
      }
    } catch (SQLException e) {
      // SQLException implies no account types found (result set empty)
      ids = new ArrayList<Integer>();
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    return ids;
  }
  
  /**
   * Gets the name of the account type with the given id.
   * @param accountTypeId the id of the account type.
   * @return The name of the account type.
   * @throws ItemNotFoundException if the given id is not in the database
   * @throws ConnectionFailedException if the connection to the database fails somehow
   */
  public static String getAccountTypeName(int accountTypeId)
      throws ItemNotFoundException, ConnectionFailedException {
    String output = null;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      output = DatabaseSelector.getAccountTypeName(accountTypeId, connection);
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (output == null) {
      throw new ItemNotFoundException("Account type not found in database");
    } else {
      return output;
    }
  }
  
  /**
   * Gets a list of all the roles stored in the database. 
   * @return all role ids stored in the database
   * @throws ConnectionFailedException if something goes wrong with the connection
   */
  public static List<Integer> getRoles() throws ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    List<Integer> ids = new ArrayList<Integer>();
    try {
      ResultSet results = DatabaseSelector.getRoles(connection);
      while (results.next()) {
        ids.add(results.getInt("ID"));
      }
    } catch (SQLException e) {
      // SQLException implies empty role table (result set empty)
      ids = new ArrayList<Integer>();
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    return ids;
  }

  /**
   * get the typeId of the account.
   * @param accountId the accounts id
   * @return the typeId
   * @throws ItemNotFoundException if the account is not found in the database
   * @throws ConnectionFailedException if something goes wrong with the database connection
   */
  public static int getAccountType(int accountId)
      throws ItemNotFoundException, ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    int accountType = -1;
    try {
      accountType = DatabaseSelector.getAccountType(accountId, connection);
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (accountType == -1) {
      // If account type not found and variable not modified
      throw new ItemNotFoundException("Account not found in database");
    } else {
      return accountType;
    }
  }
  
  /**
   * Get the role of the given user.
   * @param userId the id of the user.
   * @return the roleId for the user.
   * @throws ItemNotFoundException if the user is not found in the database
   * @throws ConnectionFailedException if something goes wrong with the database connection
   */
  public static int getUserRole(int userId)
      throws ItemNotFoundException, ConnectionFailedException {
    int roleId = -1;
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    try {
      roleId = DatabaseSelector.getUserRole(userId, connection);
    } catch (SQLException e) {
      // Error here implies database access failure of some kind
      // Query does not close preparedStatement and statement fetches a resultSet
      throw new ConnectionFailedException("Database access failure");
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (roleId == -1) {
      // If account type not found and variable not modified
      throw new ItemNotFoundException("Account not found in database");
    } else {
      return roleId;
    }
  }
  
  /**
   * Gets the message with the given id from the database.
   * @param messageId the id of the message to fetch
   * @return the contents of the message
   * @throws ItemNotFoundException if the message could not be found in the database
   * @throws ConnectionFailedException if something goes wrong with the database connection
   */
  public static String getMessage(int messageId)
      throws ItemNotFoundException, ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    String message = null;
    try {
      message = DatabaseSelector.getSpecificMessage(messageId, connection);
    } catch (SQLException e) {
      message = null;
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    if (message == null) {
      throw new ItemNotFoundException("Message not in database");
    } else {
      return message;
    }
  }
  
  /**
   * Gets all messages that have the given user as a recipient.
   * @param userId the id of the user whose messages are desired
   * @return a list of all the ids of messages available to the given user
   * @throws ConnectionFailedException if something goes wrong with the database connection
   */
  public static List<Integer> getAllMessages(int userId) throws ConnectionFailedException {
    Connection connection = DatabaseDriverHelper.connectOrCreateDataBase();
    // Get result set
    List<Integer> messageIds = new ArrayList<Integer>();
    ResultSet results;
    try {
      results = DatabaseSelector.getAllMessages(userId, connection);
      // Get all message ids
      while (results.next()) {
        messageIds.add(results.getInt("ID")); // Add ids to the output list
      }
    } catch (SQLException e) {
      // If empty result set
      messageIds = new ArrayList<Integer>(); // Return empty list
    } finally {
      DatabaseDriverHelper.closeConnection(connection);
    }
    return messageIds;
  }
  
}
