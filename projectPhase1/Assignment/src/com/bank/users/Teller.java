package com.bank.users;

import com.bank.generics.User;

/**
 * Teller class that extends the abstract User class.
 * @author Ka-Kit Jason Cheung
 *
 */
public class Teller extends User {
  /**
   * Constructor for Teller object with 4 params.
   * @param id id to be set to Teller object
   * @param name name to be set to Teller object
   * @param age age to be set to Teller object
   * @param address address to be set to Teller object
   */
  public Teller(int id, String name, int age, String address){
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    // leave the address
  }
  
  /**
   * Constructor for Teller object with 5 params.
   * @param id id to be set to Teller object
   * @param name name to be set to Teller object
   * @param age age to be set to Teller object
   * @param address address to be set to Teller object
   * @param authenticated authenticated status to be set to Teller object
   */
  public Teller(int id, String name, int age, String address, boolean authenticated){
    this.setId(id);
    this.setName(name);
    this.setAge(age);
    // leave the address and authenticated
  }
}
