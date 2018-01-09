package com.bank.bank.functions;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;
import com.bank.generics.Roles;
import com.bank.generics.RolesMap;
import com.bank.generics.User;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;



/**
 * A class for storing all the tasks an admin can execute.
 */
public class AdminFunctions {

  public static void promoteTeller(int promoteTellerId)
      throws ConnectionFailedException, ItemNotFoundException {
    // create variable to hold teller role id
    int tellerRoleId = 0;
    // loop over roles and get teller role id
    for (int i = 0; i < DatabaseSelectHelper.getRoles().size(); i++) {
      if (DatabaseSelectHelper.getRole(DatabaseSelectHelper.getRoles().get(i)).equals("TELLER")) {
        tellerRoleId = DatabaseSelectHelper.getRoles().get(i);
      }
    }
    // check if given id is teller
    if (DatabaseSelectHelper.getUserRole(promoteTellerId) != tellerRoleId) {
      // id is not teller type
      System.out.println("Given Id is not Teller type");
    } else {
      List<Integer> rolesId = new ArrayList<Integer>();
      rolesId = RolesMap.getRolesMappings();
      // default value for adminRoleId
      int adminRoleId = -1;
      // get the roleId for the admin
      for (int i = 0; i < rolesId.size(); i++) {
        if (DatabaseSelectHelper.getRole(rolesId.get(i)).equalsIgnoreCase(Roles.ADMIN.toString())) {
          adminRoleId = rolesId.get(i);
        }
      }
      // update role of Teller to Admin
      DatabaseUpdateHelper.updateUserRole(adminRoleId, promoteTellerId);
      System.out.println("The Teller is now an Admin");
    }
  }

  public static void printAllBal(int id)  {
    // Loop though all user id until we find one that won't work
    boolean loopedThroughAll = false;
    User currentUser = null;
    int currentUserId = 1;
    // loops through all the user in the database
    while (!loopedThroughAll) {
      try {
        // Get role id of the current user
        // checks if the role id of the current user matches the one we're looking for
        if (DatabaseSelectHelper.getUserRole(currentUserId) == id) {
          // get the user's information to print it
          currentUser = DatabaseSelectHelper.getUserDetails(currentUserId);
          System.out.print(currentUserId + " | " + currentUser.getName() + " | ");
          List<Integer> customerAccountIds = new ArrayList<Integer>();
          // get ids belonging to currCustomerId
          customerAccountIds = DatabaseSelectHelper.getAccountIds(currentUserId);
          // print out all accounts for this customer
          BigDecimal bal = new BigDecimal(0);
          for (Integer accountId : customerAccountIds) {
            Account a = DatabaseSelectHelper.getAccountDetails(accountId);
            bal = bal.add(a.getBalance());
          }
          System.out.println(bal);
        }
      } catch (ItemNotFoundException e) {
        // If we get to an id that doesn't exist
        loopedThroughAll = true;
      } catch (ConnectionFailedException e) {
        // Exit loop if connection fails
        loopedThroughAll = true;
      } 
      currentUserId++;
    }
  }
  
  /**
   * Prints all users of a given type. Will print both their names and id.
   * @param id the id of the role or kind of users desired
   */
  public static void printUsersOfType(int id) {
    // Loop though all user id until we find one that won't work
    boolean loopedThroughAll = false;
    User currentUser = null;
    int currentUserId = 1;
    
    // loops through all the user in the database
    while (!loopedThroughAll) {
      try {
        // Get role id of the current user
        // checks if the role id of the current user matches the one we're looking for
        if (DatabaseSelectHelper.getUserRole(currentUserId) == id) {
          // get the user's information to print it
          currentUser = DatabaseSelectHelper.getUserDetails(currentUserId);
          System.out.println(currentUserId + " | " + currentUser.getName());
        }
      } catch (ItemNotFoundException e) {
        // If we get to an id that doesn't exist
        loopedThroughAll = true;
      } catch (ConnectionFailedException e) {
        // Exit loop if connection fails
        loopedThroughAll = true;
      } 
      currentUserId++;
    }
  }

  /**
   * Creates a new admin based on user input and is inserted into the database.
   * @throws IOException Exception thrown if error in reading input
   * @throws ConnectionFailedException Exception thrown if error connecting to database
   * @throws ItemNotFoundException Exception thrown if an item is not found
   * @throws IllegalObjectTypeException Exception thrown if an illegal type of object is used
   * @throws IllegalInputParameterException Exception thrown if wrong input parameter
   * @throws IllegalAmountException Exception thrown if wrong monetary amount entered
   */
  public static void makeNewAdmin()
      throws IOException, ConnectionFailedException, ItemNotFoundException,
             IllegalObjectTypeException, IllegalInputParameterException, IllegalAmountException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    // get all info required to make admin
    System.out.println("Creating a new admin...");
    System.out.println("Enter new Admin's name:");
    String name = br.readLine();
    System.out.println("Enter new Admin's age:");
    int age = InputFunctions.getAndCheckValidInt();
    System.out.println("Enter new Admin's address:");
    String address = br.readLine();
    System.out.println("Enter new Admin's password:");
    String password = br.readLine();
    // insert the user into the database
    // get admin roleId
    List<Integer> rolesId = new ArrayList<Integer>();
    rolesId = RolesMap.getRolesMappings();
    // default value for adminRoleId
    int adminRoleId = -1;
    // get the roleId for the admin
    for (int i = 0; i < rolesId.size(); i++) {
      if (DatabaseSelectHelper.getRole(rolesId.get(i)).equalsIgnoreCase(Roles.ADMIN.toString())) {
        adminRoleId = rolesId.get(i);
      }
    }
    // insert the admin into the database
    int u = DatabaseInsertHelper.insertNewUser(name, age, address, adminRoleId, password);
    // print the ID of the admin
    System.out.println("New admin id is " + u);
    // remember the last id that was created
  }
  
  /**
   * Creates a new teller based on user input and is inserted into the database.
   * @throws IOException Exception thrown if error in reading input
   * @throws ConnectionFailedException Exception thrown if error connecting to database
   * @throws ItemNotFoundException Exception thrown if an item is not found
   * @throws IllegalObjectTypeException Exception thrown if an illegal type of object is used
   * @throws IllegalInputParameterException Exception thrown if wrong input parameter
   * @throws IllegalAmountException Exception thrown if wrong monetary amount entered
   */
  public static void makeNewTeller()
      throws IOException, ConnectionFailedException, ItemNotFoundException,
             IllegalObjectTypeException, IllegalInputParameterException, IllegalAmountException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    // get all info required to make teller
    System.out.println("Creating a new teller...");
    System.out.println("Enter new Teller's name:");
    String name = br.readLine();
    System.out.println("Enter new Teller's age:");
    int age = InputFunctions.getAndCheckValidInt();
    System.out.println("Enter new Teller's address:");
    String address = br.readLine();
    System.out.println("Enter new Teller's password:");
    String password = br.readLine();
    // insert new teller into database
    // get teller roleId
    List<Integer> rolesId = new ArrayList<Integer>();
    rolesId = RolesMap.getRolesMappings();
    int tellerRoleId = -1;
    // get the roleId for the teller
    for (Integer id : rolesId) {
      if (DatabaseSelectHelper.getRole(id).equalsIgnoreCase(Roles.TELLER.toString())) {
        tellerRoleId = id;
      }
    }
    // insert the teller into the database
    int u = DatabaseInsertHelper.insertNewUser(name, age, address, tellerRoleId, password);
    // print the ID of the teller
    System.out.println("New teller id is " + u);
    // remember the newest id created
  }
  
  /**
   * Prompts the user for input for a message to leave for any bank user.
   * @throws ItemNotFoundException 
   */
  public static void leaveMessage() throws IOException, ConnectionFailedException, ItemNotFoundException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
 // check if valid recipientId
    boolean validId = false;
    int recipientId;
    do {
      System.out.println("Type in the id of the customer you wish to leave a message for");
      recipientId = InputFunctions.getAndCheckValidInt();
      User u = DatabaseSelectHelper.getUserDetails(recipientId);
      if (u instanceof User) {
        validId = true;
      } else {
        System.out.println("ID entered is not valid.");
      }
    } while(!validId);
    System.out.println("Type in the message you would like to leave for this customer");
    System.out.println("The message must be 512 characters or less");
    String message = br.readLine();
    try {
      DatabaseInsertHelper.insertMessage(recipientId, message);
    } catch (ItemNotFoundException e) {
      System.out.println("User not found in database, please try again");
    } catch (IllegalInputParameterException e) {
      System.out.println("Message too long, please try again");
    }
  }
  
  /**
   * Views any message in the database. If the message 
   * @param userId the admin's id
   * @param messageId the id of the message desired
   * @throws ConnectionFailedException if connection to database fails somehow
   */
  public static void viewMessage(int userId, int messageId) throws ConnectionFailedException {
    try {
      String message = DatabaseSelectHelper.getMessage(messageId);
      System.out.println("Message successfully retrieved, contents below:");
      System.out.println(message);
    } catch (ItemNotFoundException e) {
      System.out.println("Message not found");
    }
    if (DatabaseSelectHelper.getAllMessages(userId).contains(messageId)) {
      // If message belongs to admin
      DatabaseUpdateHelper.updateMessageStatus(messageId);
    }
  }
}
