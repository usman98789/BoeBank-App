package com.bank.bank.functions;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing functions that deal with accounts.
 * 
 * @author Austin Seto
 */
public class AccountFunctions {

  /**
   * This method prints all of the user's accounts' information. 
   * 
   * @param currCustomerId Id of the customer whose info is to be printed
   * @throws ConnectionFailedException if connection to the database fails somehow
   * @throws ItemNotFoundException if customer does not exist
   */
  public static void printUserInfo(int currCustomerId)
      throws ConnectionFailedException, ItemNotFoundException {
    List<Integer> customerAccountIds = new ArrayList<Integer>();
    // get ids belonging to currCustomerId
    customerAccountIds = DatabaseSelectHelper.getAccountIds(currCustomerId);
    // print out info for this customer
    System.out.println("Has " + customerAccountIds.size() + " account(s).");
    // print out all accounts for this customer
    for (Integer accountId : customerAccountIds) {
      Account a = DatabaseSelectHelper.getAccountDetails(accountId);
      System.out.println("Account name: " + a.getName());
      System.out.println("Account ID: " + a.getId());
      System.out.println("Account balance: $" + a.getBalance());
      int accountType = DatabaseSelectHelper.getAccountType(accountId);
      System.out.println("Account Type: " + DatabaseSelectHelper.getAccountTypeName(accountType));
      System.out.println();
    }
  }

  /**
   * Views a user's total balance.
   * @param custId the id of the user
   * @throws ConnectionFailedException if connection to the database fails
   * @throws ItemNotFoundException if the user does not exist in the database
   */
  public static void viewUserBalance(int custId)
      throws ConnectionFailedException, ItemNotFoundException {
    // create variable to hold customer id
    int custRoleId = 0;
    // loop over roles and get customer role id
    for (int i = 0; i < DatabaseSelectHelper.getRoles().size(); i++) {
      if (DatabaseSelectHelper.getRole(DatabaseSelectHelper.getRoles().get(i)).equals("CUSTOMER")) {
        custRoleId = DatabaseSelectHelper.getRoles().get(i);
      }
    }
    // check if given id is a customer
    if (DatabaseSelectHelper.getUserRole(custId) != custRoleId) {
      // id is not customer type
      System.out.println("Given Id is not Customer type");
    } else {
      List<Integer> customerAccountIds = new ArrayList<Integer>();
      // get account ids belonging to currCustomerId
      customerAccountIds = DatabaseSelectHelper.getAccountIds(custId);
      // Sum all account balances for this customer
      BigDecimal bal = new BigDecimal(0);
      for (Integer accountId : customerAccountIds) {
        Account a = DatabaseSelectHelper.getAccountDetails(accountId);
        bal = bal.add(a.getBalance());
      }
      System.out.println("Customer's total balance is " + bal);
    }
  }
}
