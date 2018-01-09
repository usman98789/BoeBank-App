package com.bank.users;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.generics.User;

/**
 * Admin class that extends the abstract User class.
 * @author Ka-Kit Jason Cheung
 *
 */
public class Admin extends User {
  
  /**
   * Constructor for Admin object with 4 params.
   * @param id id to be set to Admin object
   * @param name name to be set to Admin object
   * @param age age to be set to Admin object
   * @param address address to be set to Admin object
   */
  public Admin(int id, String name, int age, String address) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);
  }
  
  /**
   * Constructor for Admin object with 5 params.
   * @param id id to be set to Admin object
   * @param name name to be set to Admin object
   * @param age age to be set to Admin object
   * @param address address to be set to Admin object
   * @param authenticated authenticated status to be set to Admin object
   */
  public Admin(int id, String name, int age, String address, boolean authenticated) {
    this.setId(id);
    this.setName(name);
    this.setAge(age);

  }
}
