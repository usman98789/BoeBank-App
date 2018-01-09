package com.bank.generics;

import java.math.BigDecimal;
import java.sql.SQLException;

import com.bank.security.PasswordHelpers;
import com.bank.databasehelper.DatabaseSelectHelper;

/**
 * Class for abstract User object
 * @author Ka-Kit Jason Cheung
 *
 */
public abstract class User {
  private int id;
  private String name;
  private int age;
  private String address;
  private int roleId;
  private boolean authenticated = false;
  
  /**
   * Getter for Id.
   * @return Id of User
   */
  public int getId() {
    return this.id;
  }
  
  /**
   * Setter for id.
   * @param id id to be set
   */
  public void setId(int id) {
    this.id = id;
  }
  
  /**
   * getter for name.
   * @return name of user
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * setter for name.
   * @param name name to be set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * getter for age.
   * @return age of user
   */
  public int getAge() {
    return this.age;
  }
  
  /**
   * setter for age.
   * @param age age to be set
   */
  public void setAge(int age) {
    this.age = age;
  }
  
  /**.
   * getter for user's role id
   * @return role id of user
   */
  public int getRoleId() {
    return this.roleId;
  }
  
  /**
   * Method to authenticate user using password.
   * @param password password to be authenticated with
   * @return true if authenticated, false otherwise.
   * @throws SQLException Exception thrown if error in SQL queries.
   */
  public final boolean authenticate(String password) throws SQLException {
    // compare provided password with the password in database
    String pwdFromDatabase = DatabaseSelectHelper.getPassword(this.id);
    this.authenticated = PasswordHelpers.comparePassword(pwdFromDatabase, password);
    return this.authenticated;
  }
  
}
