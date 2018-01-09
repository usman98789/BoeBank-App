package com.bank.serializer;

import android.content.Context;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;
import com.bank.generics.User;
import com.bank.users.Customer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SerializedDatabase implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  // Array list to store all the data from the database
  private ArrayList<User> databaseUsers = new ArrayList<User>();
  private ArrayList<Account> databaseAccounts = new ArrayList<Account>();

  private ArrayList<String> databasePasswords = new ArrayList<String>();

  private ArrayList<String> databaseRoleTypes = new ArrayList<String>();
  private ArrayList<String> databaseAccountTypes = new ArrayList<String>();
  private ArrayList<String> databaseMessages = new ArrayList<String>();

  // stores the id of the inserted accounts
  private ArrayList<Integer> insertedAccountIds = new ArrayList<Integer>();

  /**
   * Adds everything in the database to itself so that it can be serialized
   * @param context
   * @throws ConnectionFailedException
   */
  public SerializedDatabase(Context context) throws ConnectionFailedException, ItemNotFoundException {
    // creates the DatabaseHelper object
    DatabaseSelectHelper databaseHelper = new DatabaseSelectHelper(context);

    System.out.println("Started serialization :)");
    // adds all the components to the database
    addUsers(databaseHelper, context);
    System.out.println("Finished exporting users");
    addAccounts(databaseHelper);
    System.out.println("Finished exporting accounts");
    addRoleTypes(databaseHelper);
    System.out.println("Finished exporting roles types");
    addAccountTypes(databaseHelper);
    System.out.println("Finished exporting account types");
    addMessages(databaseHelper);
    System.out.println("Finished exporting messages");
  }

  public void addUsers(DatabaseSelectHelper databaseHelper, Context context)
      throws ConnectionFailedException, ItemNotFoundException {
    // variable to hold the id of the current user
    int currentUserId = 1;
    // variable to control looping through the database
    boolean completed = false;
    User newUser = null;

    // iterates through all the users in the database
    while (!completed) {
      // stores the user from DB to check if not the last user
      newUser = databaseHelper.getUser(currentUserId, context);
      if (newUser != null) {
        // adds the password to the list of passwords
        this.databasePasswords.add(databaseHelper.getPassword(currentUserId));
        // adds the current user to the serializable object
        this.databaseUsers.add(newUser);

        String userName = newUser.getName();
        String userAddress = newUser.getAddress();
        int userAge = newUser.getAge();
        int userRoleId = newUser.getRoleId();
        System.out.println(userName + " " + userAddress + " " + userAge + " " + userRoleId);
        System.out.println(databaseHelper.getPassword(currentUserId));

      } else {
        completed = true;
      }
      // increments the user id
      currentUserId ++;
    }
  }

  private void addAccounts(DatabaseSelectHelper databaseHelper) throws ConnectionFailedException {
    // variable to hold the id of the current account
    int currentAccountId = 1;
    // variable to control looping through the database
    boolean completed = false;
    Account newAccount = null;

    // iterates through all the users in the database
    while (!completed) {
      // stores the user from DB to check if not the last user
      newAccount = databaseHelper.getAccount(currentAccountId);
      if (newAccount != null) {
        // adds the current user to the serializable object
        this.databaseAccounts.add(newAccount);
      } else {
        completed = true;
      }

      // increments the account id
      currentAccountId ++;
    }
  }

  private void addMessages(DatabaseSelectHelper databaseHelper) {
    // variable to hold the id of the current message
    int currentMessageId = 1;
    // variable to control looping through the database
    boolean completed = false;
    String newMessage = null;

    // iterates through all the users in the database
    while (!completed) {
      // stores the user from DB to check if not the last user
      newMessage = databaseHelper.getSpecificMessage(currentMessageId);
      if (newMessage != null) {
        // adds the current message to the serializable object
        this.databaseMessages.add(newMessage);
      } else {
        completed = true;
      }
      // increments the message id
      currentMessageId ++;
    }

  }

  private void addAccountTypes(DatabaseSelectHelper databaseHelper) {
    // variable to hold the id of the current message
    int currentAccountType = 1;
    // variable to control looping through the database
    boolean completed = false;
    String newAccountType = null;

    // iterates through all the users in the database
    while (!completed) {

      // stores the account type from DB to check if not the last user
      newAccountType = databaseHelper.getAccountTypeName(currentAccountType);
      if (newAccountType != null) {
        // adds the current account type to the serializable object
        this.databaseAccountTypes.add(newAccountType);
      } else {
        completed = true;
      }
      // increments the account id
      currentAccountType ++;
    }
  }

  private void addRoleTypes(DatabaseSelectHelper databaseHelper) {
    // variable to hold the id of the current role type
    int currentRoleTypeId = 1;
    // variable to control looping through the database
    boolean completed = false;
    String newRoleType = null;

    // iterates through all the users in the database
    while (!completed) {
      // stores the role type from DB to check if not the last user
      newRoleType = databaseHelper.getRole(currentRoleTypeId);
      if (newRoleType != null) {
        // adds the current account type to the serializable object
        this.databaseRoleTypes.add(newRoleType);
      } else {
        completed = true;
      }
      // increments the role id
      currentRoleTypeId ++;
    }   
  }


  public void deserialize(Context context) throws ConnectionFailedException, IllegalObjectTypeException,
  IllegalInputParameterException, IllegalAmountException, ItemNotFoundException {
    // creates the DatabaseHelper object
    DatabaseInsertHelper databaseInsertHelper = new DatabaseInsertHelper(context);
    // creates a database helper object to get values
    DatabaseSelectHelper databaseSelectHelper = new DatabaseSelectHelper(context);
    // creates a database helper object to update values
    DatabaseUpdateHelper databaseUpdateHelper = new DatabaseUpdateHelper(context);

    // adds all the components to the database
    System.out.println("Started deserialization :)");

    extractAccounts(databaseInsertHelper);
    System.out.println("Finished importing accounts");

    extractUsers(databaseInsertHelper, databaseSelectHelper, databaseUpdateHelper);
    System.out.println("Finished importing users ");

    System.out.println("Database successfully imported :)");

  }


  /*
   * Method to extract the accounts from the imported db wrapper object 
   * @throws 
   * @throws 
   */
  private void extractAccounts(DatabaseInsertHelper databaseInsertHelper)
      throws ConnectionFailedException, IllegalObjectTypeException {
    String accountName = null;
    BigDecimal accountBalance = null;
    int accountTypeId;
    // stores the account id of the account in current DB
    int newAccountId;

    // iterate through the accounts in the imported object
    for (Account newAccount: databaseAccounts) {
      // extracts values from the imported account to insert them into the DB
      accountName = newAccount.getName();
      accountBalance = newAccount.getBalance();
      accountTypeId = newAccount.getType();
      // inserts the new account to the database
      newAccountId = (int) databaseInsertHelper.insertAccount(accountName,
          accountBalance, accountTypeId); 
      // inserts the account ID into its corresponding position in a separate array list
      this.insertedAccountIds.add(newAccountId);
    }
  }


  /*
   * Method to extract the users from the deserialized db object 
   * @throws 
   * @throws 
   */
  public void extractUsers(DatabaseInsertHelper databaseInsertHelper,
                           DatabaseSelectHelper databaseSelectHelper,
                           DatabaseUpdateHelper databaseUpdateHelper)
      throws ConnectionFailedException, IllegalObjectTypeException,
  IllegalInputParameterException, IllegalAmountException, ItemNotFoundException {
    // stores the current 
    // temporary values for each user needed to insert into DB
    String userName = null;
    String userAddress = null;
    String userPassword = null;
    int userAge;
    int userRoleId;

    int currentPosition = 0;
    // temporary value to hold the inserted user
    int insertedId;

    // iterate through the older users in the imported object
    for (User oldUser: databaseUsers) {
      // extracts values from the imported user to insert them into the current DB
      userName = oldUser.getName();
      userAddress = oldUser.getAddress();
      userAge = oldUser.getAge();
      userRoleId = oldUser.getRoleId();

      System.out.println(userName + " " + userAddress + " " + userAge + " " + userRoleId);

      // add the user to the database using the insert helper
      insertedId = (int) databaseInsertHelper.insertNewUser(userName,
          userAge, userAddress, userRoleId, "temporary");

      // gets the password stored in the array of passwords and updates the account
      userPassword = this.databasePasswords.get(currentPosition);
      databaseUpdateHelper.updateUserPassword(userPassword, insertedId);


      // binds accounts to the user if it's a customer
      if (oldUser instanceof Customer){
        bindAccounts(databaseInsertHelper, oldUser, insertedId);
      }
      // binds the messages to the user
      bindMessages(databaseInsertHelper, databaseSelectHelper, oldUser, insertedId);

      // for getting data from the arrays associated with each user
      currentPosition ++;
    }    
  }

  /**
   * Adds the accounts from the old user to the new user
   * @param oldUser <User> the user object that we get data from
   * @param insertedId <id> the id of the new user
   * @throws ItemNotFoundException when the account is not found
   * @throws ConnectionFailedException when the connection to the DB failes
   */
  private void bindAccounts (DatabaseInsertHelper databaseHelper, User oldUser, int insertedId)
      throws ItemNotFoundException, ConnectionFailedException {
    // gets the list of accounts associated with the user
    List<Account> associatedAccounts = ((Customer) oldUser).getAccounts();

    // iterates through the list of accounts from the old DB
    for (Account associatedAccount: associatedAccounts) {

      // gets the original DB id of the associated account
      int originalId = associatedAccount.getId();
      // gets the id of the account in the current database
      int currAccountId = this.insertedAccountIds.get(originalId - 1);

      // binds the accounts associated with the customer for the new DB
      databaseHelper.insertUserAccount(insertedId, currAccountId);
    }
  }

  /**
   * Adds the messages to a user in the old db
   * @param oldUser the old user object we get data from
   * @param insertedId the id of the user we send messsages to
   * @throws ConnectionFailedException when the connection to the DB fails
   * @throws IllegalInputParameterException when the message is too long
   * @throws ItemNotFoundException when the message cannot be found
   */
  private void bindMessages (DatabaseInsertHelper databaseInsertHelper,
                             DatabaseSelectHelper databaseSelectHelper,
                             User oldUser, int insertedId)
      throws ConnectionFailedException, IllegalInputParameterException, ItemNotFoundException {


    // temporary value to store the message
    String message;
    // gets the list of messages associated with the user
    List<Integer> associatedMsgs = null;

    // iterates through the messages for the user
    for (int messageID: associatedMsgs) {
      message = databaseMessages.get(messageID - 1);
      // inserts the message into the DB
      databaseInsertHelper.insertMessage(insertedId, message);
    }
  }


}
