package com.bank.bank;

import com.bank.database.DatabaseDriver;
import com.bank.database.InitializeDatabase;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ErrorAuthenticatingException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsuffiecintFundsException;
import com.bank.exceptions.NotTellerIdException;
import com.bank.exceptions.UnauthorizedAccessException;
import com.bank.generics.Account;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.generics.User;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;
import com.bank.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


public class Bank {
  /**
   * This is the atmInterface method that provides all the functionality for the ATM interface.
   * 
   * @throws IOException Exception thrown if error in getting inputs.
   * @throws SQLException Exception thrown if error in SQL queries.
   * @throws UnauthorizedAccessException Exception thrown if incorrect role accessing functionality.
   */
  private static void atmInterface() throws IOException, SQLException, UnauthorizedAccessException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    boolean authenticated = false;
    System.out.println("ATM Interface");
    // loop until the user is authenticated
    int userId = -1;
    String password = null;
    // loop until authenticated
    while (authenticated != true) {
      // get user id and password
      System.out.println("Enter your ID: ");
      userId = Integer.parseInt(br.readLine());
      System.out.println("Enter your password: ");
      password = br.readLine();
      // check if user is a customer
      int roleId = DatabaseSelectHelper.getUserRole(userId);
      if (!DatabaseSelectHelper.getRole(roleId).equalsIgnoreCase(Roles.CUSTOMER.toString())) {
        throw new UnauthorizedAccessException("You must be a customer to use this.");
      }
      Customer c = (Customer) DatabaseSelectHelper.getUserDetails(userId);
      // check if customer exists
      if (c == null) {
        throw new UnauthorizedAccessException("Customer does not exist with this ID.");
      }
      // attempt to authenticate customer
      authenticated = c.authenticate(password);
    }
    
    // create atm object
    Atm atmSession = new Atm(userId, password);
    int selection = -1;
    // loop until exit code 5 is given
    while (selection != 5) {
      // print ATM menu
      System.out.println("1. List Accounts and balances");
      System.out.println("2. Make Deposit");
      System.out.println("3. Check Balance");
      System.out.println("4. Make withdrawal");
      System.out.println("5. Exit");
      System.out.println("Please make a selection: ");
      // get user selection
      selection = Integer.parseInt(br.readLine());
      // if user chooses 1
      if (selection == 1) {
        System.out.println("Showing balances for all accounts: ");
        // get all accounts for the customer
        List<Account> accounts = new ArrayList<Account>();
        accounts = atmSession.listAccounts();
        // print out all account names and balances
        for (int i = 0; i < accounts.size(); i++) {
          System.out.println("Account name: " + accounts.get(i).getName());
          System.out.println("Balance: " + accounts.get(i).getBalance());
          System.out.println();
        }
      }
      // if user chooses 2
      else if (selection == 2) {
        // make a deposit
        System.out.println("Make a deposit");
        // print accounts to let user decide which account to deposit in
        printUserInfo(userId);
        // get account and amount to deposit into
        System.out.println("Enter account ID of account to deposit into.");
        int accountId = Integer.parseInt(br.readLine());
        System.out.println("Enter amount to deposit: ");
        float depositAmountF = Float.parseFloat(br.readLine());
        BigDecimal depositAmount = new BigDecimal(depositAmountF);
        try {
          // deposit into the account
          atmSession.makeDeposit(depositAmount, accountId);
        } catch (IllegalAmountException e) {
          e.printStackTrace();
        }
      } 
      // if user chooses 3 
      else if (selection == 3) {
        System.out.println("Check Balance");
        System.out.println("Which account's balance would you like to check?");
        // get account id to check its balance
        int accountId = Integer.parseInt(br.readLine());
        System.out.println("This account has $" + atmSession.checkBalance(accountId));
      } 
      // if user chooses 4
      else if (selection == 4) {
        System.out.println("Make Withdrawal");
        // get account and amount to withdraw from
        System.out.println("Enter ID for the account to withdraw from: ");
        int accountId = Integer.parseInt(br.readLine());
        System.out.println("Enter the amount to withdraw: ");
        float withdrawAmountF = Float.parseFloat(br.readLine());
        BigDecimal withdrawAmount = new BigDecimal(withdrawAmountF);
        try {
          // call withdraw method on the account
          atmSession.makeWithdrawal(withdrawAmount, accountId);
        } catch (InsuffiecintFundsException e) {
          System.out.println(e);
        }
      }
      // if user chooses 5
      else if (selection == 5) {
        System.out.println("Exiting the program. Good bye.");
      }
    }
  }
  
  /**
   * This method provides the functionality for the TellerInterface.
   *  
   * @throws IOException Exception thrown if error in getting inputs.
   * @throws SQLException Exception thrown if error in SQL queries.
   * @throws NotTellerIdException Exception thrown if unauthorized role access teller functions.
   * @throws ErrorAuthenticatingException Exception thrown if error in authenticating user.
   * @throws UnauthorizedAccessException Exception thrown if roles access functions they don't have.
   */
  private static void tellerInterface() throws IOException,
      SQLException, NotTellerIdException, ErrorAuthenticatingException,
      UnauthorizedAccessException {
    // teller interface
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    
    System.out.println("Teller Interface");
    // get user id and password
    int userId;
    int currCustomerId = -1;
    System.out.println("Enter your id: ");
    userId = Integer.parseInt(br.readLine());
    String password;
    System.out.println("Enter your password: ");
    password = br.readLine();
    
    // attempt to authenticate
    // find user from db
    TellerTerminal tellerTerminal = new TellerTerminal(userId, password);
   
    int selection = -1;
    // loop until exit code 9 
    while (selection != 9) {
      // prompt for tellerInterface options
      System.out.println("1. Authenticate new customer");
      System.out.println("2. Make new user");
      System.out.println("3. Make new account");
      System.out.println("4. Give interest");
      System.out.println("5. Make a deposit");
      System.out.println("6. Make a withdrawal");
      System.out.println("7. Check balance");
      System.out.println("8. Close customer session");
      System.out.println("9. Exit");
      System.out.println("Please enter a selection: ");
      // get user selection
      selection = Integer.parseInt(br.readLine().trim());
      
      // option 1: authenticate new customer
      if (selection == 1) {
        System.out.println("Authenticate new customer.");
        System.out.println("Enter ID for the customer: ");
        int customerId = Integer.parseInt(br.readLine());
        System.out.println("Enter password for the customer: ");
        String customerPassword = br.readLine();
        //  CHECK IF THIS IS ACTUALLY A CUSTOMER
        int custRoleId = DatabaseSelectHelper.getUserRole(userId);
        if (DatabaseSelectHelper.getRole(custRoleId).equalsIgnoreCase(Roles.CUSTOMER.toString())) {
          throw new UnauthorizedAccessException("This is not a customer id.");
        }
        Customer c = (Customer) DatabaseSelectHelper.getUserDetails(customerId);
        currCustomerId = c.getId();
        tellerTerminal.setCurrentCustomer(c);
        // authenticate customer
        tellerTerminal.authenticateCurrentCustomer(customerPassword);
      }
      // option 2: make new user
      else if (selection == 2) {
        // get info for new customer account
        System.out.println("Make new customer.");
        System.out.println("Enter new customer's name: ");
        String custName = br.readLine();
        System.out.println("Enter new customer's age: ");
        int custAge = Integer.parseInt(br.readLine());
        System.out.println("Enter new customer's address: ");
        String custAdd = br.readLine();
        System.out.println("Enter new customer's password: ");
        String custPwd = br.readLine();
        // create the new customer
        tellerTerminal.makeNewUser(custName, custAge, custAdd, custPwd);
        System.out.println("New customer successfully created.");
        System.out.println("To assign as current customer, authenticate by typing 1.");
      }
      // option 3: make new account for the customer
      else if (selection == 3) {
        // get info to make new account
        System.out.println("Create new account for customer.");
        System.out.println("Enter name for new account: ");
        String accName = br.readLine();
        System.out.println("Enter balance for this account: ");
        float tempBal = Float.parseFloat(br.readLine());
        BigDecimal accBalance = new BigDecimal(tempBal);
        System.out.println("Enter type of account: ");
        List<Integer> accountTypeIds = new ArrayList<Integer>();
        accountTypeIds = DatabaseSelectHelper.getAccountTypesIds();
        // display all account types in database
        for (int i = 0; i < accountTypeIds.size(); i++) {
          String tempAccountName = DatabaseSelectHelper.getAccountTypeName(accountTypeIds.get(i));
          System.out.println(accountTypeIds.get(i) + " : " + tempAccountName);
        }
        int accountTypeId = Integer.parseInt(br.readLine());
        boolean success;
        // create new account with the given info
        success = tellerTerminal.makeNewAccount(accName, accBalance, accountTypeId);   
        // prompt success or failure in creating new account
        if (success == true){
          System.out.println("New account successfully made.");
        } else {
          System.out.println("Error creating new account.");
        }
      }
      // option 4: give interest
      else if (selection == 4) {
        System.out.println("Give interest: ");
        // get all accounts for the current customer
        printUserInfo(currCustomerId);
        // get the account to add interest
        System.out.println("Enter the account ID to add interest to: ");
        int accountId = Integer.parseInt(br.readLine());
        // call method to update interest on database
        tellerTerminal.giveInterest(accountId);
      }
      // option 5: Make a deposit
      else if (selection == 5) {
        System.out.println("Make a deposit: ");
        printUserInfo(currCustomerId);
        // get account and amount to deposit 
        System.out.println("Enter the account ID to deposit to: ");
        int accountId = Integer.parseInt(br.readLine());
        System.out.println("Enter amount to deposit: ");
        String depositAmountS = br.readLine();
        BigDecimal depositAmount = new BigDecimal(depositAmountS);
        try {
          // call method to update balance on database
          tellerTerminal.makeDeposit(depositAmount, accountId);
        } catch (IllegalAmountException e) {
          System.out.println(e);
        }
      }
      // option 6: Make Withdrawal
      else if (selection == 6) {
        System.out.println("Make withdrawal: ");
        // print info of customer's accounts
        printUserInfo(currCustomerId);
        // get the account and amount to withdraw from
        System.out.println("Enter the account ID to withdraw from: ");
        int accountId = Integer.parseInt(br.readLine());
        System.out.println("Enter amount to withdraw: ");
        float withdrawAmountF = Float.parseFloat(br.readLine());
        BigDecimal withdrawAmount = new BigDecimal(withdrawAmountF);
        // withdraw from the account
        try {
          tellerTerminal.makeWithdrawal(withdrawAmount, accountId);
        } catch (InsuffiecintFundsException e) {
          // TODO Auto-generated catch block
          System.out.println(e);
        }
      }
      // option 7: Check Balance
      else if (selection == 7) {
        System.out.println("Check Balance");
        // show the balances for all of customer's accounts
        printUserInfo(currCustomerId);
      }
      // option 8: close customer session
      else if (selection == 8) {
        System.out.println("Closing customer session...");
        // deauthenticate the customer and set the currCustomerId to -1
        tellerTerminal.deAuthenticateCustomer();
        currCustomerId = -1;
      } else if (selection == 9) {
        System.out.println("Exiting application");
      } 
    }
  }
  
  /**
   * This method prints all of the user's accounts' information. 
   * 
   * @param currCustomerId Id of the customer whose info is to be printed
   * @throws SQLException Exception thrown if error in SQL queries. 
   */
  private static void printUserInfo(int currCustomerId) throws SQLException {
    List<Integer> customerAccountIds = new ArrayList<Integer>();
    // get ids belonging to currCustomerId
    customerAccountIds = DatabaseSelectHelper.getAccountIds(currCustomerId);
    // print out info for this customer
    // System.out.println("Current customer is: " + currCustomerId);
    System.out.println("Has " + customerAccountIds.size() + " account(s).");
    // print out all accounts for this customer
    for (int i = 0; i < customerAccountIds.size(); i++) {
      Account a = DatabaseSelectHelper.getAccountDetails(customerAccountIds.get(i));
      System.out.println("Account name: " + a.getName());
      System.out.println("Account ID: " + a.getId());
      System.out.println("Account balance: $" + a.getBalance());
      int accountType = DatabaseSelectHelper.getAccountType(customerAccountIds.get(i));
      System.out.println("Account Type: " + DatabaseSelectHelper.getAccountTypeName(accountType));
      System.out.println();
    }
  }
  
  /**
   * This method prints the context menu of the program to the user.
   */
  private static void printContextMenu() {
    // prints out the contextMenu of the program
    System.out.println("1 - TELLER Interface");
    System.out.println("2 - ATM Interface");
    System.out.println("0 - Exit");
    System.out.println("Enter Selection:");
  }

  /**
   * This is the main method to run your entire program! Follow the Candy Cane instructions to
   * finish this off.
   * @param argv unused.
   */
  public static void main(String[] argv) {
    // create an InputStreamReader and BufferedReader objects
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    
    Connection connection = DatabaseDriverExtender.connectOrCreateDataBase();
    try {      
      if (argv.length > 0 && (argv[0].equals("-1") || argv[0].equals("1"))) {
        // Check what is in argv 
        //If it is -1
        if (argv[0].equals("-1")) {
          
          // INITIALIZE DATABASE
          DatabaseDriverExtender.initialize(connection);
          
          // add roles to the Roles table in db
          DatabaseInsertHelper.insertRole(Roles.ADMIN.toString());
          DatabaseInsertHelper.insertRole(Roles.CUSTOMER.toString());
          DatabaseInsertHelper.insertRole(Roles.TELLER.toString());
          // add account types to the db
          BigDecimal interest = new BigDecimal(0.10);
          DatabaseInsertHelper.insertAccountType(AccountTypes.CHEQUING.toString(), interest);
          DatabaseInsertHelper.insertAccountType(AccountTypes.SAVING.toString(), interest);
          DatabaseInsertHelper.insertAccountType(AccountTypes.TFSA.toString(), interest);
          // create first account, an administrator with a password
          String name;
          System.out.println("Enter name of admin: ");
          name = br.readLine();
          int age;
          System.out.println("Enter admin's age: ");
          age = Integer.parseInt(br.readLine());
          String address;
          System.out.println("Enter admin's address: ");
          address = br.readLine();
          String password;
          System.out.println("Enter admin's password: ");
          password = br.readLine();
          // get admin role id
          List<Integer> rolesId = new ArrayList<Integer>();
          rolesId = DatabaseSelectHelper.getRoles();
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
          System.out.println("Admin id is " + u);
          // proceed to context menu
          printContextMenu();
          int option = Integer.parseInt(br.readLine());
          if (option == 1) {
            // execute teller interface
            tellerInterface();
          } else if (option == 2) {
            // execute ATM interface
            atmInterface();
          }
          else if (option == 0) {
            // exit
            System.out.println("Exiting application. Good bye.");
          }
        }
        //If it is 1
        else if (argv[0].equals("1")) {
          /*
           * In admin mode, the user must first login with a valid admin account
           * This will allow the user to create new Teller's.  At this point, this is
           * all the admin can do.
           */
          System.out.println("You are in admin mode.");
          // prompt for Admin user id and password
          int userId;
          String password;
          System.out.println("Please enter your user id:");
          userId = Integer.parseInt(br.readLine());
          System.out.println("Please enter your password:");
          password = br.readLine();
          // remove trailing and leading whitespace
          password.trim();
          
          // get user details from database with id
          // check that userId is actually an admin
          if (DatabaseSelectHelper.getUserRole(userId) != 1) {
            throw new UnauthorizedAccessException("This userId is not an admin.");
          }
          Admin u = (Admin) DatabaseSelectHelper.getUserDetails(userId);
          // error checking
          if (u == null) {
            throw new SQLException("Admin not found.");
          }
          // authenticate with password
          boolean authenticated = u.authenticate(password);
          String retry = "Y";
          // let user retry password
          while (authenticated == false && !retry.equals("N")) {
            System.out.println("Incorrect password.");
            System.out.println("Retry? (Y/N)");
            retry = br.readLine();
            retry = retry.toUpperCase();
            if (retry.equals("Y")) {
              System.out.println("Enter password: ");
              password = br.readLine();
              authenticated = u.authenticate(password);
            }
          }
          // if authenticated, allow access to create new teller
          if (authenticated == true) {
            // give options to create a new teller
            System.out.println("To create a new teller?");
            System.out.println("Y = Yes\nN = No/Exit");
            String option;
            option = br.readLine();
            option = option.toUpperCase();
            // SET TELLER ROLEID
            List<Integer> rolesId = new ArrayList<Integer>();
            rolesId = DatabaseSelectHelper.getRoles();
            int tellerId = -1;
            // get the roleId of the teller role
            for (int i = 0; i < rolesId.size(); i++) {
              String roleName = Roles.TELLER.toString();
              if (DatabaseSelectHelper.getRole(rolesId.get(i)).equalsIgnoreCase(roleName)) {
                // set the role id to tellerId
                tellerId = rolesId.get(i);
              }
            }
            // keep loop until N is given by user
            while (!option.equals("N")) {
              // get info for the new teller
              String name;
              System.out.println("Enter name of teller: ");
              name = br.readLine();
              System.out.println("Enter teller's age: ");
              int age = Integer.parseInt(br.readLine());
              System.out.println("Enter teller's address: ");
              String address = br.readLine();
              System.out.println("Enter teller's password: ");
              String tellerPwd = br.readLine();
              // insert new teller into database
              int newId;
              // get the newId of the inserted teller
              newId = DatabaseInsertHelper.insertNewUser(name, age, address, tellerId, tellerPwd);
              // print id of new teller
              System.out.println("New teller's id: " + newId);
              // ask user if they want to repeat
              System.out.println("Do you want to insert another teller? (Y/N)");
              option = br.readLine();
              option = option.toUpperCase();
            }
          } else {
            // if authentication fails
            System.out.println("Incorrect password.");
          }
          // prompt end of admin mode
          System.out.println("You have exited the admin mode.");
        }
      }
      //If anything other params - including nothing
      else {
        // print options for the user
        printContextMenu();
        int option = Integer.parseInt(br.readLine());
        // loop until we get exit status 0
        while (option != 0) {
          if (option == 1) {
            // execute teller interface
            tellerInterface();
          }
          else if (option == 2) {
            //If the user entered 2, execute atm interface
            atmInterface();
          } else if (option == 0) {
            //If the user entered 0, exit condition
            System.out.println("Exiting application. Good bye.");
          }
          //If the user entered anything else:
          // Re-prompt the user
          printContextMenu();
          option = Integer.parseInt(br.readLine());
        }
      }
    } catch (Exception e) {
      System.out.println(e);
      e.printStackTrace();
    } finally {
      try {
        connection.close();
      } catch (Exception e) {
        System.out.println("Looks like it was closed already :)");
      }
    }
  }
}
