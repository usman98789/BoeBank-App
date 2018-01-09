package com.bank.functions;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;
import com.activities.R;
import com.bank.generics.User;
import com.bank.users.Customer;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * A class for storing functions that deal with accounts.
 *
 */
public class AccountFunctions {

  /**
   * This method prints all of the user's accounts' information.
   *
   * @param currCustomerId Id of the customer whose info is to be printed
   * @throws ConnectionFailedException if connection to the database fails somehow
   * @throws ItemNotFoundException if customer does not exist
   */
  public static void printUserInfo(int currCustomerId, Context context)
          throws ConnectionFailedException, ItemNotFoundException {
    List<Integer> customerAccountIds = new ArrayList<Integer>();
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    // get ids belonging to currCustomerId
    customerAccountIds = db.getAccountIdsList(currCustomerId);
    // print out info for this customer
    System.out.println("Has " + customerAccountIds.size() + " account(s).");
    // print out all accounts for this customer
    for (Integer accountId : customerAccountIds) {
      Account a = db.getAccount(accountId);
      System.out.println("Account name: " + a.getName());
      System.out.println("Account ID: " + a.getId());
      System.out.println("Account balance: $" + a.getBalance());
      int accountType = db.getAccountType(accountId);
      System.out.println("Account Type: " + db.getAccountTypeName(accountType));
      System.out.println();
    }
    db.close();
  }

  public static void viewUserBalance(int custId, Context context)
          throws ConnectionFailedException, ItemNotFoundException {
    DatabaseSelectHelper db = new DatabaseSelectHelper(context);
    // create variable to hold customer id
    // loop over roles and get customer role id
    User tempUser = db.getUser(custId, context);
    // check if given id is a customer
    if (tempUser != null && tempUser instanceof Customer) {
      // id is not customer type
      List<Integer> customerAccountIds = new ArrayList<Integer>();
      // get account ids belonging to currCustomerId
      customerAccountIds = db.getAccountIdsList(custId);
      // Sum all account balances for this customer
      BigDecimal bal = new BigDecimal(0);
      for (Integer accountId : customerAccountIds) {
        Account a = db.getAccount(accountId);
        bal = bal.add(a.getBalance());
      }
      TextView output = (TextView) ((Activity)context).findViewById(R.id.totalCustBalance);
      output.setText(tempUser.getName() +"'s total balance is $" + bal);
    } else {
      // if it's not a customer, print toast
      Toast toastNotif = Toast.makeText(context, "ID doesn't belong to user.", Toast.LENGTH_LONG);
      toastNotif.show();
    }
    db.close();
  }
}
