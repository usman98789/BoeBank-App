package com.bank.users;

import android.content.Context;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Account;
import com.bank.generics.RolesMap;
import com.bank.generics.User;

import java.util.ArrayList;
import java.util.List;

/**
 * Customer class that extends the abstract User class.
 * @author Ka-Kit Jason Cheung
 *
 */
public class Customer extends User {
  private List<Account> accounts = new ArrayList<Account>();

  /**
   * Constructor for Customer object with 4 parameters.
   * @param id id to be set to Customer object
   * @param name name to be set to Customer object
   * @param age age to be set to Customer object
   * @param address address to be set to Customer object
   */
  public Customer(int id, String name, int age, String address, Context context)
      throws ConnectionFailedException, ItemNotFoundException {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
    // sets the current role id
    this.roleId = RolesMap.getRoleId("CUSTOMER", context);
  }
  
  /**
   * Constructor for Customer object with 5 parameters.
   * @param id id to be set to Customer object
   * @param name name to be set to Customer object
   * @param age age to be set to Customer object
   * @param address address to be set to Customer object
   * @param authenticated authenticated status to be set to Customer object
   */
  public Customer(int id, String name, int age, String address, boolean authenticated) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    this.setAddress(address);
  }
  
  /**
   * Method to get a list of account objects for the customer object.
   * @return list of accounts belonging to customer
   */ 
  public List<Account> getAccounts() {
    return this.accounts;
  }
  
  /**
   * Adds the given account to this customer's list of accounts.
   * @param account account to be added to customer's list of accounts
   */
  public void addAccount(Account account) {
    this.accounts.add(account);
  }
}
