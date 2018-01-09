package com.bank.bank;

import com.bank.bank.functions.AccountFunctions;
import com.bank.bank.functions.AdminFunctions;
import com.bank.bank.functions.InputFunctions;
import com.bank.bank.functions.SerializerFunctions;
import com.bank.bank.functions.TellerFunctions;
import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.databasehelper.DatabaseUpdateHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.NotAuthenticatedException;
import com.bank.generics.Account;

import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesMap;
import com.bank.generics.Roles;
import com.bank.generics.RolesMap;
import com.bank.generics.User;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class Bank {
  
  // Constants for selecting interface
  private static final int INTERFACE_TELLER = 1;
  private static final int INTERFACE_ATM = 2;
  private static final int INTERFACE_EXIT = 0;

  // Constants for admin options
  private static final int ADMIN_OPTION_NEW_ADMIN = 1;
  private static final int ADMIN_OPTION_NEW_TELLER = 2;
  private static final int ADMIN_OPTION_VIEW_ADMINS = 3;
  private static final int ADMIN_OPTION_VIEW_TELLERS = 4;
  private static final int ADMIN_OPTION_VIEW_CUSTOMERS = 5;
  private static final int ADMIN_OPTION_PROMOTE_TELLER = 6;
  private static final int ADMIN_OPTION_VIEW_CUSTOMER_BALANCE = 7;
  private static final int ADMIN_OPTION_VIEW_BANK_FUNDS = 8;
  private static final int ADMIN_OPTION_LEAVE_MESSAGE = 9;
  private static final int ADMIN_OPTION_VIEW_MESSAGE = 10;
  private static final int ADMIN_OPTION_EXPORT_DB = 11;
  private static final int ADMIN_OPTION_IMPORT_DB = 12;
  private static final int ADMIN_OPTION_EXIT = 0;
  
  // Constants for teller options
  private static final int TELLER_OPTION_AUTHENTICATE_CUSTOMER = 1;
  private static final int TELLER_OPTION_NEW_CUSTOMER = 2;
  private static final int TELLER_OPTION_NEW_ACCOUNT = 3;
  private static final int TELLER_OPTION_GIVE_INTEREST = 4;
  private static final int TELLER_OPTION_DEPOSIT = 5;
  private static final int TELLER_OPTION_WITHDRAW = 6;
  private static final int TELLER_OPTION_CHECK_BALANCE = 7;
  private static final int TELLER_OPTION_ANY_BALANCE = 8;
  private static final int TELLER_OPTION_LEAVE_MESSAGE = 9;
  private static final int TELLER_OPTION_TELLER_INBOX = 10;
  private static final int TELLER_OPTION_CUSTOMER_INBOX = 11;
  private static final int TELLER_OPTION_UPDATE_CUSTOMER_INFO = 12;
  private static final int TELLER_OPTION_CUSTOMER_LEAVE = 13;
  private static final int TELLER_OPTION_EXIT = 0;
  
  // Constants for ATM options
  private static final int ATM_OPTION_LIST_ALL_ACCOUNTS = 1;
  private static final int ATM_OPTION_LIST_ONE_ACCOUNT = 2;
  private static final int ATM_OPTION_DEPOSIT = 3;
  private static final int ATM_OPTION_WITHDRAW = 4;
  private static final int ATM_OPTION_EXIT = 0;

  /**
   * This is the atmInterface method that provides all the functionality for the ATM interface.
   * 
   * @throws IOException Exception thrown if invalid input
   * @throws NotAuthenticatedException if user is not authenticated to do something
   * @throws ConnectionFailedException if connection to the database fails
   * @throws ItemNotFoundException if access a user or account that does not exist in the database
   */
  private static void atmInterface()
      throws IOException, NotAuthenticatedException,
             ItemNotFoundException, ConnectionFailedException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    boolean authenticated = false;
    System.out.println("ATM Interface");
    // loop until the user is authenticated
    int userId = -1;
    String password = null;
    Atm atmSession = null;
    // loop until authenticated
    while (authenticated != true) {
      // get user id and password
      System.out.println("Enter your ID: ");
      userId = InputFunctions.getAndCheckValidInt();
      System.out.println("Enter your password: ");
      password = br.readLine();
      try {
        atmSession = new Atm(userId, password);
        Customer c = (Customer) DatabaseSelectHelper.getUserDetails(userId);
        authenticated = c.authenticate(password); // Try to authenticate user
        System.out.println("\nName: " + c.getName());
        System.out.println("Address: " + c.getAddress() + "\n");
        AccountFunctions.printUserInfo(userId);
      } catch (IllegalObjectTypeException e) {
        authenticated = false;
        System.out.println("Input id is not for a customer");
      } catch (ItemNotFoundException e) {
        authenticated = false;
        System.out.println("Input customer does not exist in database");
      }
    }
    // default value for selection
    int selection = -1;
    
    // loop until exit code 5 is given
    while (selection != ATM_OPTION_EXIT) {
      // print ATM menu
      System.out.println(ATM_OPTION_LIST_ALL_ACCOUNTS + ". List Accounts and balances");
      System.out.println(ATM_OPTION_LIST_ONE_ACCOUNT + ". Check Balance");
      System.out.println(ATM_OPTION_DEPOSIT + ". Make Deposit");
      System.out.println(ATM_OPTION_WITHDRAW + ". Make withdrawal");
      System.out.println(ATM_OPTION_EXIT + ". Exit");
      System.out.println("Please make a selection: ");
      // get user selection
      selection = InputFunctions.getAndCheckValidInt();
      if (selection == ATM_OPTION_LIST_ALL_ACCOUNTS) {
        // check if current customer has any accounts
        if (atmSession.hasAccounts() == false) {
          continue;
        }
        // If user chooses to view all accounts
        System.out.println("Showing balances for all accounts: ");
        // get all accounts for the customer
        List<Account> accounts = new ArrayList<Account>();
        accounts = atmSession.listAccounts();
        // print out all account names and balances
        for (int i = 0; i < accounts.size(); i++) {
          System.out.println("Account name: " + accounts.get(i).getName());
          System.out.println("Balance: " + accounts.get(i).getBalance() + "\n");
        }
        // added blank space for formatting if no accounts
        System.out.println();
      } else if (selection == ATM_OPTION_DEPOSIT) {
        // check if current customer has any accounts
        if (atmSession.hasAccounts() == false) {
          continue;
        }
        // If user chooses to make a deposit
        // make a deposit
        System.out.println("Make a deposit");
        // print accounts to let user decide which account to deposit in
        AccountFunctions.printUserInfo(userId);
        // get account and amount to deposit into
        System.out.println("Enter account ID of account to deposit into.");
        int accountId = InputFunctions.getAndCheckValidInt();
        System.out.println("Enter amount to deposit: ");     
        float depositAmountF = InputFunctions.getAndCheckValidFloat();
        BigDecimal depositAmount = new BigDecimal(depositAmountF);
        depositAmount = depositAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        try {
          // deposit into the account
          atmSession.makeDeposit(depositAmount, accountId);
        } catch (IllegalAmountException e) {
          System.out.println("Must deposit a non-negative amount of money");
        } catch (NotAuthenticatedException e) {
          System.out.println("This account doesn't exist/does not belong to you.");
          System.out.println("Please enter a different acount id.");
        }
      } else if (selection == ATM_OPTION_LIST_ONE_ACCOUNT) {
        // check if current customer has any accounts
        if (atmSession.hasAccounts() == false) {
          continue;
        }
        // If user chooses to check balance
        System.out.println("Check Balance");
        System.out.println("Which account's balance would you like to check?");
        // get account id to check its balance
        int accountId = InputFunctions.getAndCheckValidInt();
        System.out.println("This account has $" + atmSession.checkBalance(accountId));
      } else if (selection == ATM_OPTION_WITHDRAW) {
        // check if current customer has any accounts
        if (atmSession.hasAccounts() == false) {
          continue;
        }
        // If user chooses to make a withdrawal
        System.out.println("Make Withdrawal");
        // get account and amount to withdraw from
        System.out.println("Enter ID for the account to withdraw from: ");
        int accountId = InputFunctions.getAndCheckValidInt();
        System.out.println("Enter the amount to withdraw: ");
        float withdrawAmountF = InputFunctions.getAndCheckValidFloat();
        BigDecimal withdrawAmount = new BigDecimal(withdrawAmountF);
        try {
          // call withdraw method on the account
          atmSession.makeWithdrawal(withdrawAmount, accountId);
        } catch (InsufficientFundsException e) {
          System.out.println("Insufficient funds in this account.");
        } catch (IllegalAmountException e) {
          System.out.println("Withdraw amount must be positive.");
        } catch (NotAuthenticatedException e) {
          System.out.println("This account doesn't exist/does not belong to you.");
          System.out.println("Please enter a different acount id.");
        }
      } else if (selection == ATM_OPTION_EXIT) { // If choosing to exit program
        System.out.println("Exiting the ATM session. Good bye.");
      } else {
        System.out.println("Invalid option, pick one of the listed options");
      }
    }
  }
  
  /**
   * This method provides the functionality for the TellerInterface.
   *  
   * @throws IOException Exception thrown if error in getting inputs.
   * @throws IllegalAmountException Exception thrown if illegal amount entered
   * @throws ConnectionFailedException if connection to the database fails somehow
   * @throws ItemNotFoundException if an input account or user does not exist in the database
   * @throws IllegalObjectTypeException if input is not of an expected type
   * @throws IllegalInputParameterException exception thrown if illegal input is entered
   */
  private static void tellerInterface()
      throws IOException, IllegalAmountException, ItemNotFoundException,
             ConnectionFailedException, IllegalObjectTypeException, IllegalInputParameterException {
    // teller interface
    System.out.println("Teller Interface");
    // get user id and password
    int userId;
    int currCustomerId = -1;
    // check if this is a teller id
    User tempUser = null;
    do {
      System.out.println("Enter your id: ");
      userId = InputFunctions.getAndCheckValidInt();
      tempUser = DatabaseSelectHelper.getUserDetails(userId);
      if (tempUser == null || !(tempUser instanceof Teller)) {
        System.out.println("Invalid ID entered.\nPlease try again.");
      }
      // check if id exists in db and if it's a teller
    } while (tempUser == null || !(tempUser instanceof Teller));
    
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    
    String password;
    System.out.println("Enter your password: ");
    password = br.readLine();
    // attempt to authenticate
    while (!(DatabaseSelectHelper.getUserDetails(userId).authenticate(password))) {
      System.out.println("Incorrect password. Retry? (Y/N)");
      String choice = br.readLine();
      if (choice.equalsIgnoreCase("N")) {
        return;
      } else {
        System.out.println("Retry with a different password.");
        password = br.readLine();
      }
    }
    // Create a tellerTerminal based on this teller
    TellerTerminal tellerTerminal = new TellerTerminal(userId, password);      
    int selection = -1;
    // loop until exit code
    while (selection != TELLER_OPTION_EXIT) {
      // prompt for tellerInterface options
      printTellerModeMenu();
      // get user selection
      selection = InputFunctions.getAndCheckValidInt();
      
      // Figure out what option was input
      if (selection == TELLER_OPTION_AUTHENTICATE_CUSTOMER) {
        TellerFunctions.authenticateCustomer(tellerTerminal);
      } else if (selection == TELLER_OPTION_NEW_CUSTOMER) {
        TellerFunctions.newCustomer(tellerTerminal);
      } else if (selection == TELLER_OPTION_NEW_ACCOUNT) {
        // check if there's currently a customer
        if (tellerTerminal.currentCustomerPresent() == false) {
          continue;
        } else {
          // get info to make new account
          System.out.println("Create new account for customer.");
          System.out.println("Enter name for new account: ");
          String accName = br.readLine();
          // make sure balance is not negative
          float tempBal = 0;
          BigDecimal accBalance;

          System.out.println("Enter type of account: ");
          List<Integer> accountTypeIds = new ArrayList<Integer>();
          accountTypeIds = AccountTypesMap.getAccountTypesMappings();
          
          // display all account types in database
          for (int i = 0; i < accountTypeIds.size(); i++) {
            String tempAccountName = DatabaseSelectHelper.getAccountTypeName(accountTypeIds.get(i));
            System.out.println(accountTypeIds.get(i) + " : " + tempAccountName);
          }
          int accountTypeId = InputFunctions.getAndCheckValidInt();
          if (DatabaseSelectHelper.getAccountTypeName(accountTypeId).equalsIgnoreCase("LOAN")) {
            do {
              System.out.println("Enter balance for this account(Negative): ");
              tempBal = InputFunctions.getAndCheckValidFloat();
              accBalance = new BigDecimal(tempBal);
              if (accBalance.compareTo(BigDecimal.ZERO) > 0) {
                System.out.println("Balance cannot be positive.");
              }
            } while (accBalance.compareTo(BigDecimal.ZERO) > 0);
          } else {
            do {
              System.out.println("Enter balance for this account: ");
              tempBal = InputFunctions.getAndCheckValidFloat();
              accBalance = new BigDecimal(tempBal);
              if (accBalance.compareTo(BigDecimal.ZERO) < 0) {
                System.out.println("Balance cannot be negative.");
              }
            } while (accBalance.compareTo(BigDecimal.ZERO) < 0);
          }
          boolean success = false;
          // create new account with the given info
          try {
            success = tellerTerminal.makeNewAccount(accName, accBalance, accountTypeId);   
          } catch (NotAuthenticatedException e) {
            System.out.println("User and teller must be authenticated");
          }
          // prompt success or failure in creating new account
          if (success == true) {
            System.out.println("New account successfully made.");
          } else {
            System.out.println("Error creating new account.");
          }
        } 
      } else if (selection == TELLER_OPTION_GIVE_INTEREST) {
        // check if there's currently a customer
        if (tellerTerminal.currentCustomerPresent() == false) {
          continue;
        }
        // check if current customer has any accounts
        if (tellerTerminal.hasAccounts() == false) {
          continue;
        }
        System.out.println("Give interest: ");
        // get all accounts for the current customer
        AccountFunctions.printUserInfo(currCustomerId);
        // get the account to add interest
        System.out.println("Enter the account ID to add interest to: ");
        int accountId = InputFunctions.getAndCheckValidInt();
        // call method to update interest on database
        try {
          tellerTerminal.giveInterest(accountId);
          // leave message for user saying interest was given
          Account accountGivenInterest = DatabaseSelectHelper.getAccountDetails(accountId);
          String accountName = accountGivenInterest.getName();
          // construct message
          String msg = "Your account, " + accountName  + ", was given interest.";
          // leave message for customer
          DatabaseInsertHelper.insertMessage(tellerTerminal.currentCustomer.getId(), msg);
        } catch (NotAuthenticatedException e) {
          System.out.println("Teller and customer must be authenticated to give interest");
        }
      } else if (selection == TELLER_OPTION_DEPOSIT) {
        // check if there's currently a customer
        if (tellerTerminal.currentCustomerPresent() == false) {
          continue;
        }
        // check if current customer has any accounts
        if (tellerTerminal.hasAccounts() == false) {
          continue;
        } else {
          System.out.println("Make a deposit: ");
          AccountFunctions.printUserInfo(currCustomerId);
          // get account and amount to deposit 
          System.out.println("Enter the account ID to deposit to: ");
          int accountId = InputFunctions.getAndCheckValidInt();
          System.out.println("Enter amount to deposit: ");
          Float depositAmountF = InputFunctions.getAndCheckValidFloat();
          BigDecimal depositAmount = new BigDecimal(depositAmountF);
          // user banker's rounding
          depositAmount = depositAmount.setScale(2, BigDecimal.ROUND_HALF_EVEN);
          try {
            // call method to update balance on database
            tellerTerminal.makeDeposit(depositAmount, accountId);
          } catch (IllegalAmountException e) {
            System.out.println("Invalid deposit amount entered.");
          } catch (NotAuthenticatedException e) {
            System.out.println("This account doesn't exist/does not belong to you.");
            System.out.println("Please enter a different acount id.");
          }
        }
      } else if (selection == TELLER_OPTION_WITHDRAW) { // If choosing to make a withdrawal
        // check if there's currently a customer
        if (tellerTerminal.currentCustomerPresent() == false) {
          continue;
        }
        // check if current customer has any accounts
        if (tellerTerminal.hasAccounts() == false) {
          continue;
        }
        System.out.println("Make withdrawal: ");
        // print info of customer's accounts
        AccountFunctions.printUserInfo(currCustomerId);
        // get the account and amount to withdraw from
        System.out.println("Enter the account ID to withdraw from: ");
        int accountId = InputFunctions.getAndCheckValidInt();
        System.out.println("Enter amount to withdraw: ");
        float withdrawAmountF = InputFunctions.getAndCheckValidFloat();
        BigDecimal withdrawAmount = new BigDecimal(withdrawAmountF);
        // withdraw from the account
        try {
          tellerTerminal.makeWithdrawal(withdrawAmount, accountId);
        } catch (InsufficientFundsException e) {
          System.out.println("Not enough funds to withdraw");
        } catch (IllegalAmountException e) {
          System.out.println("Must withdraw a positive amount of money");
        } catch (NotAuthenticatedException e) {
          System.out.println("This account doesn't exist/does not belong to you.");
          System.out.println("Please enter a different acount id.");
        }
      } else if (selection == TELLER_OPTION_CHECK_BALANCE) { // If choosing to check balances
        if (tellerTerminal.isCustomerAuthenticated() && tellerTerminal.currentCustomerPresent()) {
          // If current customer is authenticated and present
          AccountFunctions.printUserInfo(tellerTerminal.currentCustomer.getId());
        } else {
          System.out.println("No customer present");
        }
      } else if (selection == TELLER_OPTION_CUSTOMER_LEAVE) {
        System.out.println("Closing customer session...");
        // deauthenticate the customer and set the currCustomerId to -1
        tellerTerminal.deauthenticateCustomer();
      } else if (selection == TELLER_OPTION_LEAVE_MESSAGE) {
        if (tellerTerminal.isTellerAuthenticated()) {
          System.out.println("Type in the id of the customer you wish to leave a message for");
          int recipientId = InputFunctions.getAndCheckValidInt();
          System.out.println("Type in the message you would like to leave for this customer");
          System.out.println("The message must be 512 characters or less");
          String message = br.readLine();
          try {
            if (DatabaseSelectHelper.getUserRole(recipientId) == RolesMap.getRoleId("CUSTOMER")) {
              DatabaseInsertHelper.insertMessage(recipientId, message);
            } else {
              System.out.println("Can only leave messages for customers");
            }
          } catch (ItemNotFoundException e) {
            System.out.println("Customer not found in database, please try again");
          } catch (IllegalInputParameterException e) {
            System.out.println("Message too long, please try again");
          }
        } else {
          System.out.println("Teller must be authenticated to leave messages");
        }
      } else if (selection == TELLER_OPTION_ANY_BALANCE) {
        // View specific User's total balance
        System.out.println("Enter the Customer's Id");
        int custId = InputFunctions.getAndCheckValidInt();
        AccountFunctions.viewUserBalance(custId);
      } else if (selection == TELLER_OPTION_UPDATE_CUSTOMER_INFO) {
        // check if there's a current customer
        if (tellerTerminal.currentCustomerPresent() == false) {
          continue;
        }
        // Update the current customer's information
        int input;
        do {
          System.out.println("Customer Info: ");
          System.out.println("Name: " + tellerTerminal.getCurrentCustomer().getName());
          System.out.println("Address: " + tellerTerminal.getCurrentCustomer().getAddress());
          System.out.println("Age: " + tellerTerminal.getCurrentCustomer().getAge() + "\n");
          System.out.println("What do you to update?");
          System.out.println("1. Name");
          System.out.println("2. Address");
          System.out.println("3. Age");
          System.out.println("4. Exit");
          input = InputFunctions.getAndCheckValidInt();
          // input validation
          while (input < 1 || input > 4) {
            System.out.println("Please enter a number 1-4: ");
            input = InputFunctions.getAndCheckValidInt();
          }
          // execute user request
          if (input == 1) {
            // get new name and update it
            System.out.println("Enter new name: ");
            String newName = br.readLine();
            tellerTerminal.updateCustomerName(newName);
          } else if (input == 2) {
            // get new address and update it
            System.out.println("Enter new address: ");
            String newAddress = br.readLine();
            tellerTerminal.updateCustomerAddres(newAddress);
          } else if (input == 3) {
            // get new age and update it
            System.out.println("Enter new age: ");
            int newAge = InputFunctions.getAndCheckValidInt();
            tellerTerminal.updateCustomerAge(newAge);
          }
          System.out.println("Customer information is now up to date.");
        } while (input != 4);
      } else if (selection == TELLER_OPTION_TELLER_INBOX) {
        System.out.println("Viewing your messages:");
        List<Integer> messageIds = new ArrayList<Integer>();
        // get all messages for the teller
        messageIds = DatabaseSelectHelper.getAllMessages(userId);
        if (messageIds.size() == 0) {
          System.out.println("Teller has no messages.");
        } else {
          // loop through each message and print it out and change its status
          for (int i = 0; i < messageIds.size(); i++) {
            // print out the message
            System.out.println(DatabaseSelectHelper.getMessage(messageIds.get(i)));
            // change message's status
            DatabaseUpdateHelper.updateMessageStatus(messageIds.get(i));
          }
        }
      } else if (selection == TELLER_OPTION_CUSTOMER_INBOX) {
        // view current customer's messages
        // check if there is a current customer
        if (!tellerTerminal.currentCustomerPresent()) {
          continue;
        }
        System.out.println("Viewing current customer's messages:");
        // get all messages belonging to user
        List<Integer> messageIds = new ArrayList<Integer>();
        messageIds = DatabaseSelectHelper.getAllMessages(
                                          tellerTerminal.getCurrentCustomer().getId());
        if (messageIds.size() == 0) {
          System.out.println("The current customer has no messages.");
        } else {
          // loop through to print and update status of each message
          for (int i = 0; i < messageIds.size(); i++) {
            //print out message
            System.out.println(DatabaseSelectHelper.getMessage(messageIds.get(i)));
            // change message's status
            DatabaseUpdateHelper.updateMessageStatus(messageIds.get(i));
          }
        }        
      } else if (selection == TELLER_OPTION_EXIT) {
        System.out.println("Exiting Teller Interface.");
        break;
      } else {
        System.out.println("Invalid Selection. Please enter another one.");
      }
    }
  }
  

  /**
   * This method prints the context menu of the program to the user.
   */
  private static void printContextMenu() {
    // prints out the contextMenu of the program
    System.out.println(INTERFACE_TELLER + " - TELLER Interface");
    System.out.println(INTERFACE_ATM + " - ATM Interface");
    System.out.println(INTERFACE_EXIT + " - Exit");
    System.out.print("Enter desired interface: ");
  }
  
  /**
   * This method prints the menu for the admin mode.
   */
  private static void printAdminModeMenu() {
    // prints out the menu for the Admin Mode
    System.out.println(ADMIN_OPTION_NEW_ADMIN + ". Make a new Admin.");
    System.out.println(ADMIN_OPTION_NEW_TELLER + ". Make a new Teller.");
    System.out.println(ADMIN_OPTION_VIEW_ADMINS + ". View all current Admins.");
    System.out.println(ADMIN_OPTION_VIEW_TELLERS + ". View all current Tellers.");
    System.out.println(ADMIN_OPTION_VIEW_CUSTOMERS + ". View all current Customers.");
    System.out.println(ADMIN_OPTION_PROMOTE_TELLER + ". Promote Teller to Admin.");
    System.out.println(ADMIN_OPTION_VIEW_CUSTOMER_BALANCE
                       + ". View total balance of specific Customer");
    System.out.println(ADMIN_OPTION_VIEW_BANK_FUNDS + ". View total balance's of all Customer's");
    System.out.println(ADMIN_OPTION_LEAVE_MESSAGE + ". Leave a message for a user");
    System.out.println(ADMIN_OPTION_VIEW_MESSAGE + ". View a message in the database");
    System.out.println(ADMIN_OPTION_EXPORT_DB + ". Export the serialized database");
    System.out.println(ADMIN_OPTION_IMPORT_DB + ". Import serialized database");
    System.out.println(ADMIN_OPTION_EXIT + ". Exit Admin mode.");
    System.out.print("Please enter an option: ");
  }
  
  /**
   * Prints menu for teller terminal.
   */
  private static void printTellerModeMenu() {
    System.out.println(TELLER_OPTION_AUTHENTICATE_CUSTOMER + ". Authenticate new customer");
    System.out.println(TELLER_OPTION_NEW_CUSTOMER + ". Create a new customer");
    System.out.println(TELLER_OPTION_NEW_ACCOUNT + ". Create a new account");
    System.out.println(TELLER_OPTION_GIVE_INTEREST + ". Give interest");
    System.out.println(TELLER_OPTION_DEPOSIT + ". Make a deposit");
    System.out.println(TELLER_OPTION_WITHDRAW + ". Make a withdrawal");
    System.out.println(TELLER_OPTION_CHECK_BALANCE + ". Check balance");
    System.out.println(TELLER_OPTION_ANY_BALANCE + ". View total balance of any customer");
    System.out.println(TELLER_OPTION_LEAVE_MESSAGE + ". Leave a message for a customer");
    System.out.println(TELLER_OPTION_TELLER_INBOX + ". View my messages");
    System.out.println(TELLER_OPTION_CUSTOMER_INBOX + ". View current customer's messages");
    System.out.println(TELLER_OPTION_UPDATE_CUSTOMER_INFO
                       + ". Update current customer's information");
    System.out.println(TELLER_OPTION_EXIT + ". Exit");
    System.out.print("Please enter a selection: ");
  }

  /**
   * This is the main method to run your entire program! 
   * @param argv unused.
   */
  public static void main(String[] argv) {
    // create an InputStreamReader and BufferedReader objects
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    
    Connection connection = DatabaseDriverExtender.connectOrCreateDataBase();
    
    try {      
      AccountTypesMap.updateMap();
      RolesMap.updateMap();
      if (argv.length > 0 && (argv[0].equals("-1") || argv[0].equals("1"))) {
        // if the user has chosen to initialize the database
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
          DatabaseInsertHelper.insertAccountType(
              AccountTypes.RESTRICTEDSAVING.toString(), interest);
          DatabaseInsertHelper.insertAccountType(AccountTypes.LOAN.toString(), interest);

          // make a new admin
          AdminFunctions.makeNewAdmin();
          // proceed to context menu
          printContextMenu();
          int option = InputFunctions.getAndCheckValidInt();
          if (option == 1) {
            // execute teller interface
            tellerInterface();
          } else if (option == 2) {
            // execute ATM interface
            atmInterface();
          } else if (option == 0) {
            // exit
            System.out.println("Exiting application. Good bye.");
          }        
        } else if (argv[0].equals("1")) { // If a 1 is input for admin mode
          /*
           * In admin mode, the user must first login with a valid admin account
           * This will allow the user to create new Teller's.  At this point, this is
           * all the admin can do.
           */
          System.out.println("You are in admin mode.");
          // prompt for Admin user id and password
          int userId;
          String password;
          boolean isAdmin = false;
          do {
            System.out.println("Please enter your user id:");
            userId = InputFunctions.getAndCheckValidInt();
            System.out.println("Please enter your password:");
            password = br.readLine();
            // remove trailing and leading whitespace
            password.trim();
            // check that userId is actually an admin
            if (DatabaseSelectHelper.getUserDetails(userId) instanceof Admin) {
              isAdmin = true;
            } else {
              System.out.println("This user is not an admin. Do you want to try again? (Y/N)");
              String input = br.readLine();
              if (input.equalsIgnoreCase("N")) {
                System.out.println("You have exited admin mode.");
                return;
              }
            }
          } while (!isAdmin);
          
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
            } else {
              // end program is user doesn't want to retry.
              System.out.println("You have exited admin mode.");
              return;
            }
          }
          int input;
          String repeat = "Y";
          // LOOP THIS UNTIL THE USER SAYS EXIT, exit code = 9
          do {
            // print menu for admin mode
            printAdminModeMenu();
            // read user's choice
            input = InputFunctions.getAndCheckValidInt();
            // check input is not out of range
            while (input < 0 || input > 12) {
              // prompt user to retry
              System.out.println("Invalid choice. Please choose from 0 to 12.");
              input = InputFunctions.getAndCheckValidInt();
            }
            // Choices
            switch (input) {
              case ADMIN_OPTION_NEW_ADMIN:
                AdminFunctions.makeNewAdmin();
                break;
              case ADMIN_OPTION_NEW_TELLER:
                AdminFunctions.makeNewTeller();
                break;
              case ADMIN_OPTION_VIEW_ADMINS:
                AdminFunctions.printUsersOfType(RolesMap.getRoleId("ADMIN"));
                break;
              case ADMIN_OPTION_VIEW_TELLERS:
                AdminFunctions.printUsersOfType(RolesMap.getRoleId("TELLER"));
                break;
              case ADMIN_OPTION_VIEW_CUSTOMERS:
                AdminFunctions.printUsersOfType(RolesMap.getRoleId("CUSTOMER"));
                break;
              case ADMIN_OPTION_PROMOTE_TELLER:
                boolean isTeller = false;
                int promoTellerId;
                do {
                  System.out.println("Enter the Id of the Teller you would like to promote");      
                  promoTellerId = InputFunctions.getAndCheckValidInt();
                  // check that the given ID exists in the db
                  User tempUser = DatabaseSelectHelper.getUserDetails(promoTellerId);
                  if (tempUser instanceof Teller) {
                    isTeller = true;
                  } else {
                    System.out.println("This ID does not belong to a teller.");
                  }
                } while (!isTeller);
                
                AdminFunctions.promoteTeller(promoTellerId);
                // add message to teller's account to inform them of the promotion
                String msg = "Congratulations! You have been promoted to be an Admin.";
                DatabaseInsertHelper.insertMessage(promoTellerId, msg);
                break;
              case ADMIN_OPTION_VIEW_BANK_FUNDS:
                AdminFunctions.printAllBal(RolesMap.getRoleId("CUSTOMER"));
                break;
              case ADMIN_OPTION_VIEW_CUSTOMER_BALANCE:
                boolean isCustomer = false;
                int custId;
                do {
                  System.out.println("What is the id of the customer?");
                  custId = InputFunctions.getAndCheckValidInt();
                  // check that id exists and belongs to customer
                  User tempCust = DatabaseSelectHelper.getUserDetails(custId);
                  if (tempCust instanceof Customer) {
                    isCustomer = true;
                  } else {
                    System.out.println("ID entered doesn't belong to a customer.");
                  }
                } while (!isCustomer);
                
                AccountFunctions.viewUserBalance(custId);
                break;
              case ADMIN_OPTION_LEAVE_MESSAGE:
                AdminFunctions.leaveMessage();
                break;
              case ADMIN_OPTION_VIEW_MESSAGE:
                System.out.println("Please enter the id of the message you wish to view");
                int messageId = InputFunctions.getAndCheckValidInt();
                AdminFunctions.viewMessage(userId, messageId);
                break;
              case ADMIN_OPTION_EXPORT_DB:
                // serialize the entire database
                SerializerFunctions.serializeDatabase();
                break;
              case ADMIN_OPTION_IMPORT_DB:
                // deserialize entire database
                SerializerFunctions.deserializeDatabase();
                break;
              default:
                break;
            }
            // ask if user wants to repeat program
            if (input != 0) {
              System.out.println("Do you want to repeat the program? (Y/N).");
              repeat = br.readLine();
            }
            // check Y/N selection
            while (!repeat.equalsIgnoreCase("Y") && !repeat.equalsIgnoreCase("N")) {
              System.out.println("Please enter either Y or N.");
              repeat = br.readLine();
            }
          } while (input != 0 && repeat.equalsIgnoreCase("Y"));
          // prompt end of admin mode
          System.out.println("You have exited the admin mode.");
        }
      } else { // Any other input results in standard operation
        int option;
        // do once and loop until we get exit status 0
        do {
          // print options for the user
          printContextMenu();
          option = InputFunctions.getAndCheckValidInt(); 
          // user's options
          if (option == INTERFACE_TELLER) {
            // execute teller interface
            tellerInterface();
          } else if (option == INTERFACE_ATM) {
            // Execute atm interface
            atmInterface();
          } else if (option == INTERFACE_EXIT) {
            System.out.println("Exiting application. Good bye.");
          } else {
            System.out.println("Invalid selection!");
          }
        } while (option != INTERFACE_EXIT);
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