package com.bank.database;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import com.bank.accounts.ChequingAccount;
import com.bank.accounts.Loan;
import com.bank.accounts.RestrictedSavingsAccount;
import com.bank.accounts.SavingsAccount;
import com.bank.accounts.Tfsa;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;
import com.bank.generics.AccountTypes;
import com.bank.generics.AccountTypesMap;
import com.bank.generics.Roles;
import com.bank.generics.User;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * This class allows access to the Select Methods in that DatabaseDriverA.java file
 * Created by jasoncheung on 2017-07-29.
 */

public class DatabaseSelectHelper extends DatabaseDriverA {

    /**
     * Call DatabaseSelectHelper with context
     * @param context
     */
    public DatabaseSelectHelper(Context context) {
        super(context);
    }

    @Override
    public Cursor getRoles() {
        return super.getRoles();
    }

    /**
     * Returns a list of ints with the role IDs
     */
    public List<Integer> getRolesList() {
        // get cursor from db
        Cursor c = this.getRoles();
        List<Integer> roleIdsList = new ArrayList<Integer>();
        // iterate through cursor
        try {
            while (c.moveToNext()) {
                // add IDs to the list
                int id = c.getInt(c.getColumnIndex("ID"));
                roleIdsList.add(id);
            }
        } finally {
            // close cursor
            c.close();
        }
        // return roleIdsList
        return roleIdsList;
    }

    @Override
    public String getRole(int id) {
        return super.getRole(id);
    }

    @Override
    public int getUserRole(int userId) {
        return super.getUserRole(userId);
    }

    /**
     * Returns a list of users from the database
     * @return returns list of users from the database
     */
    public List<User> getUsersList(Context context)
        throws ConnectionFailedException, ItemNotFoundException{
        // get cursor from db
        Cursor c = this.getUsersDetails();
        // create list to hold users
        List<User> usersList = new ArrayList<User>();
        // iterate through the cursor
        try {
            while (c.moveToNext()) {
                // add User to the list
                int userId = c.getInt(c.getColumnIndex("ID"));
                User u = this.getUser(userId, context);
                // add the User to the list
                usersList.add(u);
            }
        } finally {
            // close the cursor
            c.close();
        }
        // return list of users
        return usersList;
    }

    /**
     * Given a cursor and userId, creates a user object of the right type.
     * @param userId user with userId ID
     * @return a user of the correct subclass
     */
    public User getUser(int userId, Context context)
        throws ConnectionFailedException, ItemNotFoundException {
        // get cursor from db
        Cursor c = this.getUserDetails(userId);
        // move to beginning of set
        if (c != null) {
            c.moveToFirst();
        }
        User output = null;
        // get data from database
        // check if anything was returned from db
        try {
            if (c != null && c.getCount() > 0) {
                // get details of user
                String name = c.getString(c.getColumnIndex("NAME"));
                int age = c.getInt(c.getColumnIndex("AGE"));
                String address = c.getString(c.getColumnIndex("ADDRESS"));
                int userRoleId = c.getInt(c.getColumnIndex("ROLEID"));
                // create user depending on its role
                if (this.getRole(userRoleId).equalsIgnoreCase(Roles.ADMIN.name())) {
                    // make admin object
                    output = new Admin(userId, name, age, address, context);
                } else if (this.getRole(userRoleId).equalsIgnoreCase(Roles.CUSTOMER.name())) {
                    // make customer object
                    output = new Customer(userId, name, age, address, context);
                } else if (this.getRole(userRoleId).equalsIgnoreCase(Roles.TELLER.name())) {
                    // make a teller object
                    output = new Teller(userId, name, age, address, context);
                }
            } else {
                return null;
            }
        } catch (Exception e) {
            // print exception
            e.printStackTrace();
        }
        // close the cursor
        c.close();
        // return the output
        return output;
    }

    @Override
    public String getPassword(int userId) {
        return super.getPassword(userId);
    }

    /**
     * Get the accountIds List
     * @param userId
     * @return List<Integer> of ids
     */
    public List<Integer> getAccountIdsList(int userId) {
      // get cursor from db
      Cursor c = this.getAccountIds(userId);
      // create list to hold ids
      List<Integer> accountIdsList = new ArrayList<Integer>();
      // iterate through cursor
      try {
        while(c.moveToNext()) {
          // add account id to list
          int accId = c.getInt(c.getColumnIndex("ACCOUNTID"));
          accountIdsList.add(accId);
        }
      } finally {
          // close the cursor
        c.close();
      }
      // return list of account ids
      return accountIdsList;
    }

    /**
     * Returns Account Object, given account Id
     * @param accountId
     * @return Account of Customer
     */
    public Account getAccount(int accountId) {
        Log.d("I", "GETACCOUNT RAN");
      // get cursor from db
      Cursor c = this.getAccountDetails(accountId);
      Account output = null;
      // move to beginning of set
      if (c.moveToFirst()) {
        // construct an account
        // check what type of account it is
        String name = c.getString(c.getColumnIndex("NAME"));
        String balanceS = c.getString(c.getColumnIndex("BALANCE"));
        BigDecimal balance = new BigDecimal(balanceS);
        int accountTypeId = c.getInt(c.getColumnIndex("TYPE"));
        // check what type of account this is
        String accountTypeName = this.getAccountTypeName(accountTypeId);
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
          // create restricted savings acc
          output = new RestrictedSavingsAccount(accountId, name, balance);
        } else if (accountTypeName.equalsIgnoreCase(AccountTypes.LOAN.name())) {
          // create loan acc
          output = new Loan(accountId, name, balance);
        }
      }
      output.setType(getAccountType(accountId));
      c.close();
      // return the account object
      return output;
    }

    @Override
    public BigDecimal getBalance(int accountId) {
        return super.getBalance(accountId);
    }

    @Override
    public int getAccountType(int accountId) {
        return super.getAccountType(accountId);
    }

    @Override
    public String getAccountTypeName(int accountTypeId) {
        return super.getAccountTypeName(accountTypeId);
    }

    /**
     * Returns List of AccountTypesIds
     * @return List<Integer> types of Accounts a customer can have
     */
    public List<Integer> getAccountTypesIdsList() {
      // get cursor from db
      Cursor c = this.getAccountTypesId();
      // declare list to hold ids
      List<Integer> accountTypesIds = new ArrayList<Integer>();
      // check if cursor returned something
      if (c.getCount() > 0) {
        try {
          // iterate through cursor
          while (c.moveToNext()) {
            // add id to the list
            int accountTypeId = c.getInt(c.getColumnIndex("ID"));
            accountTypesIds.add(accountTypeId);
          }
        } finally {
          // close the cursor
          c.close();
        }
      }
      // return the list of acc type ids
      return accountTypesIds;
    }

    @Override
    public BigDecimal getInterestRate(int accountType) {
        return super.getInterestRate(accountType);
    }

    /**
     * Returns a list of all messages left for user with uesrId ID
     * @param userId user with userId to get messages for
     * @return list of messages for user with userId
     */
    public List<String> getAllMessagesList(int userId) {
        // get cursor from db
        Cursor c = this.getAllMessages(userId);
        // declare list to hold messages for user
        List<String> msgsList = new ArrayList<String>();
        // check cursor is not empty
        if (c.getCount() > 0) {
            try {
                while (c.moveToNext()) {
                    // get message
                    String msg = c.getString(c.getColumnIndex("MESSAGE"));
                    // add message to list
                    msgsList.add(msg);
                }
            } finally {
                c.close();
            }
        }
        // return list of messages
        return msgsList;
    }

    @Override
    public String getSpecificMessage(int messageId) {
        return super.getSpecificMessage(messageId);
    }

}
