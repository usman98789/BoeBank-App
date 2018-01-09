package com.bank.serializer;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;
import com.bank.generics.User;
import com.bank.users.Customer;

public class SerializedDatabase implements Serializable {
  /**
   * 
   */
  private static final long serialVersionUID = 1L;
  // Array list to store all the data from the database
  private ArrayList <User> databaseUsers = new ArrayList <User> ();
  private ArrayList <Account> databaseAccounts = new ArrayList <Account> ();

  private ArrayList <String> databasePasswords = new ArrayList <String> ();

  private ArrayList <String> databaseRoleTypes = new ArrayList <String> ();
  private ArrayList <String> databaseAccountTypes = new ArrayList <String> ();
  private ArrayList <String> databaseMessages = new ArrayList <String> ();

  // stores the id of the inserted accounts
  private ArrayList <Integer> insertedAccountIds = new ArrayList <Integer> ();
  
  public SerializedDatabase() throws ConnectionFailedException {
    System.out.println("Started serialization :)");
    // adds all the components to the database
    addUsers();
    System.out.println("Finished exporting users");
    addAccounts();
    System.out.println("Finished exporting accounts");
    addRoleTypes();
    System.out.println("Finished exporting roles types");
    addAccountTypes();
    System.out.println("Finished exporting account types");
    addMessages();
    System.out.println("Finished exporting messages");
  }

  public void addUsers() throws ConnectionFailedException {
    // variable to hold the id of the current user
    int currentUserId = 1;
    // variable to control looping through the database
    boolean completed = false;
    User newUser = null;

    // iterates through all the users in the database
    while (!completed) {
      try {
        // stores the user from DB to check if not the last user
        newUser = DatabaseSelectHelper.getUserDetails(currentUserId);
        if (newUser != null) {
          // adds the password to the list of passwords
          this.databasePasswords.add(DatabaseSelectHelper.getPassword(currentUserId));
          // adds the current user to the serializable object
          this.databaseUsers.add(newUser);

          String userName = newUser.getName();
          String userAddress = newUser.getAddress();
          int userAge = newUser.getAge();
          int userRoleId = newUser.getRoleId();
          System.out.println(userName + " " + userAddress + " " + userAge + " " + userRoleId);
          System.out.println(DatabaseSelectHelper.getPassword(currentUserId));

        } else {
          completed = true;
        }

      } catch (ItemNotFoundException e) {
        // after the last item in the database is added 
        completed = true;   
      }
      // increments the user id
      currentUserId ++;
    }
  }

  private void addAccounts() throws ConnectionFailedException {
    // variable to hold the id of the current account
    int currentAccountId = 1;
    // variable to control looping through the database
    boolean completed = false;
    Account newAccount = null;

    // iterates through all the users in the database
    while (!completed) {
      try {
        // stores the user from DB to check if not the last user
        newAccount = DatabaseSelectHelper.getAccountDetails(currentAccountId);
        if (newAccount != null) {
          // adds the current user to the serializable object
          this.databaseAccounts.add(newAccount);
        } else {
          completed = true;
        }
      } catch (ItemNotFoundException e) {
        // after the last item in the database is added 
        completed = true;

      }
      // increments the account id
      currentAccountId ++;
    }
  }

  private void addMessages() {
    // variable to hold the id of the current message
    int currentMessageId = 1;
    // variable to control looping through the database
    boolean completed = false;
    String newMessage = null;

    // iterates through all the users in the database
    while (!completed) {
      try {
        // stores the user from DB to check if not the last user
        newMessage = DatabaseSelectHelper.getMessage(currentMessageId);
        if (newMessage != null) {
          // adds the current message to the serializable object
          this.databaseMessages.add(newMessage);
        } else {
          completed = true;
        }
      } catch (ItemNotFoundException | ConnectionFailedException e) {
        // after the last item in the database is added 
        completed = true;

      }
      // increments the message id
      currentMessageId ++;
    }

  }

  private void addAccountTypes() {
    // variable to hold the id of the current message
    int currentAccountType = 1;
    // variable to control looping through the database
    boolean completed = false;
    String newAccountType = null;

    // iterates through all the users in the database
    while (!completed) {
      try {
        // stores the account type from DB to check if not the last user
        newAccountType = DatabaseSelectHelper.getAccountTypeName(currentAccountType);
        if (newAccountType != null) {
          // adds the current account type to the serializable object
          this.databaseAccountTypes.add(newAccountType);
        } else {
          completed = true;
        }
      } catch (ItemNotFoundException | ConnectionFailedException e) {
        // after the last item in the database is added 
        completed = true;
      }
      // increments the account id
      currentAccountType ++;
    }
  }

  private void addRoleTypes() {
    // variable to hold the id of the current role type
    int currentRoleTypeId = 1;
    // variable to control looping through the database
    boolean completed = false;
    String newRoleType = null;

    // iterates through all the users in the database
    while (!completed) {
      try {
        // stores the role type from DB to check if not the last user
        newRoleType = DatabaseSelectHelper.getRole(currentRoleTypeId);
        if (newRoleType != null) {
          // adds the current account type to the serializable object
          this.databaseRoleTypes.add(newRoleType);
        } else {
          completed = true;
        }
      } catch (ItemNotFoundException | ConnectionFailedException e) {
        // after the last item in the database is added 
        completed = true;
      }
      // increments the role id
      currentRoleTypeId ++;
    }   
  }


  public void deserialize() throws ConnectionFailedException, IllegalObjectTypeException, 
  IllegalInputParameterException, IllegalAmountException, ItemNotFoundException {

    // adds all the components to the database

    System.out.println("Finished importing users");
    extractAccounts();

    System.out.println("Started deserialization :)");
    extractUsers();


    //    System.out.println("Finished importing accounts");
    //    extractRoleTypes();
    //    System.out.println("Finished importing roles types");
    //    extractAccountTypes();
    //    System.out.println("Finished importing account types");
    //    extractMessages();
    //    System.out.println("Finished importing messages");
  }


  /*
   * Method to extract the accounts from the imported db wrapper object 
   * @throws 
   * @throws 
   */
  private void extractAccounts() throws ConnectionFailedException, IllegalObjectTypeException {
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
      newAccountId = DatabaseInsertHelper.insertAccount(accountName, 
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
  public void extractUsers() throws ConnectionFailedException, IllegalObjectTypeException, 
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
      insertedId = DatabaseInsertHelper.insertNewUser(userName, 
          userAge, userAddress, userRoleId, "temporary");

      // gets the password stored in the array of passwords and updates the account
      userPassword = this.databasePasswords.get(currentPosition);  
      DatabaseUpdateHelper.updateUserPassword(insertedId, userPassword);


      // binds accounts to the user if it's a customer
      if (oldUser instanceof Customer){
        bindAccounts(oldUser, insertedId);
      }
      // binds the messages to the user
      bindMessages(oldUser, insertedId);

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
  private void bindAccounts (User oldUser, int insertedId) 
      throws ItemNotFoundException, ConnectionFailedException {
    // gets the list of accounts associated with the user
    List <Account> associatedAccounts = ((Customer) oldUser).getAccounts();

    // iterates through the list of accounts from the old DB
    for (Account associatedAccount: associatedAccounts) {

      // gets the original DB id of the associated account
      int originalId = associatedAccount.getId();
      // gets the id of the account in the current database
      int currAccountId = this.insertedAccountIds.get(originalId - 1);

      // binds the accounts associated with the customer for the new DB
      DatabaseInsertHelper.insertUserAccount(insertedId, currAccountId);
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
  private void bindMessages (User oldUser, int insertedId) throws ConnectionFailedException, 
  IllegalInputParameterException, ItemNotFoundException {
    // temporary value to store the message
    String message;
    // gets the list of messages associated with the user
    List<Integer> associatedMsgs = DatabaseSelectHelper.getAllMessages(insertedId);

    // iterates through the messages for the user
    for (int messageID: associatedMsgs) {
      message = databaseMessages.get(messageID - 1);
      // inserts the message into the DB
      DatabaseInsertHelper.insertMessage(insertedId, message); 
    }
  }


}
