package com.bank.functions;

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
import com.bank.generics.Roles;
import com.bank.generics.RolesMap;
import com.bank.generics.User;
import com.bank.users.Customer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


/**
 * A class for storing all the tasks an admin can execute.
 */
public class AdminFunctions {

  public static void promoteTeller(int promoteTellerId, Context context)
      throws ConnectionFailedException, ItemNotFoundException {
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    // create variable to hold teller role id
    int tellerRoleId = 0;
    // loop over roles and get teller role id
    for (int i = 0; i < db.getRolesList().size(); i++) {
      if (db.getRole(db.getRolesList().get(i)).equals("TELLER")) {
        tellerRoleId = db.getRolesList().get(i);
      }
    }
    // check if given id is teller
    if (db.getUserRole(promoteTellerId) != tellerRoleId) {
      // id is not teller type
      System.out.println("Given Id is not Teller type");
    } else {
      List<Integer> rolesId = new ArrayList<Integer>();
      rolesId = RolesMap.getRolesMappings(context);
      // default value for adminRoleId
      int adminRoleId = -1;
      // get the roleId for the admin
      for (int i = 0; i < rolesId.size(); i++) {
        if (db.getRole(rolesId.get(i)).equalsIgnoreCase(Roles.ADMIN.toString())) {
          adminRoleId = rolesId.get(i);
        }
      }
      DatabaseUpdateHelper dbUpdate = new DatabaseUpdateHelper(context);
      // update role of Teller to Admin
      dbUpdate.updateUserRole(adminRoleId, promoteTellerId);
      dbUpdate.close();
      System.out.println("The Teller is now an Admin");
    }
    db.close();
  }

  public static int getAllBalance(int id, Context context)
      throws ConnectionFailedException, ItemNotFoundException{
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    // create list to hold users
    List<User> usersList = db.getUsersList(context);
    int balance = 0;
    // loop through all users and find the customers
    for (int i = 0; i < usersList.size(); i++) {
      // if this is a customer
      if (usersList.get(i) instanceof Customer) {
        // print out their balance
        Customer tempCustomer = (Customer) usersList.get(i);
        System.out.println("Viewing " + tempCustomer.getId() + " | " + tempCustomer.getName()
                + "'s account balances");
        // loop through all accounts for this tempCustomer
        List<Integer> userAccountIds = new ArrayList<Integer>();
        userAccountIds = db.getAccountIdsList(tempCustomer.getId());
        // print out balance for each account associated with user
        for (int j = 0; j < userAccountIds.size(); j++) {
          Account a = db.getAccount(userAccountIds.get(j));
          balance += a.getBalance().intValue();
        }
      }
    }
    db.close();
    return balance;
  }
  
  /**
   * Prints all users of a given type. Will print both their names and id.
   * @param id the id of the role or kind of users desired
   */
  public static List<User> getAllUsersOfType(int id, Context context)
      throws ConnectionFailedException, ItemNotFoundException{
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);

    // get all users from the db
    List<User> usersList = new ArrayList<User>();
    usersList = db.getUsersList(context);

    // returns the list
    return usersList;
  }

  /**
   * Creates a new admin based on user input and is inserted into the database.
   * @param name
   * @param age
   * @param address
   * @param password
   * @param context
   * @throws IOException Exception thrown if error in reading input
   * @throws ConnectionFailedException Exception thrown if error connecting to database
   * @throws ItemNotFoundException Exception thrown if an item is not found
   * @throws IllegalObjectTypeException Exception thrown if an illegal type of object is used
   * @throws IllegalInputParameterException Exception thrown if wrong input parameter
   * @throws IllegalAmountException Exception thrown if wrong monetary amount entered
   */
  public static int makeNewAdmin(String name, int age, String address, String password,
                                 Context context)
      throws IOException, ConnectionFailedException, ItemNotFoundException,
             IllegalObjectTypeException, IllegalInputParameterException, IllegalAmountException {

    DatabaseSelectHelper dbSelect = new DatabaseSelectHelper(context);
    DatabaseInsertHelper dbInsert = new DatabaseInsertHelper(context);

    // insert the user into the database
    // get admin roleId
    List<Integer> rolesId = new ArrayList<Integer>();
    rolesId = RolesMap.getRolesMappings(context);
    // default value for adminRoleId
    int adminRoleId = -1;
    // get the roleId for the admin
    for (int i = 0; i < rolesId.size(); i++) {
      if (dbSelect.getRole(rolesId.get(i)).equalsIgnoreCase(Roles.ADMIN.toString())) {
        adminRoleId = rolesId.get(i);
      }
    }
    // insert the admin into the database
    int u = (int) dbInsert.insertNewUser(name, age, address, adminRoleId, password);
    // print the ID of the admin
    // remember the last id that was created
    return u;
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
  public static void makeNewTeller(String name, int age, String address, String password,
                                   Context context)
      throws IOException, ConnectionFailedException, ItemNotFoundException,
             IllegalObjectTypeException, IllegalInputParameterException, IllegalAmountException {

    DatabaseSelectHelper dbSelect = new DatabaseSelectHelper(context);
    DatabaseInsertHelper dbInsert = new DatabaseInsertHelper(context);

    // insert new teller into database
    // get teller roleId
    List<Integer> rolesId = new ArrayList<Integer>();
    rolesId = RolesMap.getRolesMappings(context);
    int tellerRoleId = -1;
    // get the roleId for the teller
    for (Integer id : rolesId) {
      if (dbSelect.getRole(id).equalsIgnoreCase(Roles.TELLER.toString())) {
        tellerRoleId = id;
      }
    }
    // insert the teller into the database
    int u = (int) dbInsert.insertNewUser(name, age, address, tellerRoleId, password);
    // print the ID of the teller
    System.out.println("New teller id is " + u);
    // close dbs
    dbInsert.close();
    dbSelect.close();
  }
  
  /**
   * Prompts the user for input for a message to leave for any bank user.
   * @param recipientId recipient that receives msg
   * @param msg message entered to be sent
   * @param context context of the activity
   * @throws ItemNotFoundException 
   */
  public static boolean leaveMessage(int recipientId, String msg, Context context) throws IOException, ConnectionFailedException, ItemNotFoundException {
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    DatabaseInsertHelper dbInsert = new DatabaseInsertHelper(context);

    // check if valid recipientId
    User u = db.getUser(recipientId, context);
    boolean result;
    // if there's a valid user
    if (u instanceof User) {
      // if so, try to insert message
      dbInsert.insertMessage(recipientId, msg);
      result = true;
    } else {
      // if not, then false
      result = false;
    }
    db.close();
    dbInsert.close();
    return result;
  }
  
  /**
   * Returns the message with messageId
   * @param userId the admin's id
   * @param messageId the id of the message desired
   * @throws ConnectionFailedException if connection to database fails somehow
   */
  public static String viewMessage(int userId, int messageId, Context context) throws ConnectionFailedException {
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    DatabaseUpdateHelper dbUpdate = new DatabaseUpdateHelper(context);
    // try retrieving message
    String msg = null;

    msg = db.getSpecificMessage(messageId);

    if (db.getAllMessagesList(userId).contains(messageId)) {
      // If message belongs to admin
      dbUpdate.updateUserMessageState(messageId);
    }
    // close dbs
    db.close();
    dbUpdate.close();
    return msg;
  }
}
