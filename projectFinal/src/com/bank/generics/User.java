package com.bank.generics;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.security.PasswordHelpers;

import java.io.Serializable;

/**
 * Class for abstract User object.
 * @author Ka-Kit Jason Cheung
 *
 */
public abstract class User implements Serializable {
  
  private static final long serialVersionUID = -2463071847135561557L;
  
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

  public void setAddress(String address) {
    this.address = address;
  }
  
  public String getAddress() {
    return this.address;
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
   * @throws ConnectionFailedException if connection to database fails somehow
   * @throws ItemNotFoundException if user not in database
   */
  public final boolean authenticate(String password)
      throws ItemNotFoundException, ConnectionFailedException {
    // compare provided password with the password in database
    String pwdFromDatabase = DatabaseSelectHelper.getPassword(this.id);
    this.authenticated = PasswordHelpers.comparePassword(pwdFromDatabase, password);
    return this.authenticated;
  }
  
  public void setRole(int userRoleId) {
    this.roleId = userRoleId;
  }
}
