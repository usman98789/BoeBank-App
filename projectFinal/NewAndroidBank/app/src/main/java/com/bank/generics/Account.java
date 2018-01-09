package com.bank.generics;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * Abstract class for Account Object.
 * @author Ka-Kit Jason Cheung
 *
 */
public abstract class Account implements Serializable {
  private int id;
  private String name;
  private BigDecimal balance;
  private int type;
  
  /**
   * getter for id of Account object.
   * @return id id of Account object
   */
  public int getId() {
    return this.id;
  }
  
  /**
   * setter for id of Account object.
   * @param id id to be set
   */
  public void setId(int id) {
    this.id = id;
  }
  
  /**
   * getter for name of Account object.
   * @return name name of account object
   */
  public String getName() {
    return this.name;
  }
  
  /**
   * setter for name of Account object.
   * @param name name to be set
   */
  public void setName(String name) {
    this.name = name;
  }
  
  /**
   * getter for balance of Account object.
   * @return balance balance of account object
   */
  public BigDecimal getBalance() {
    return this.balance;
  }
  
  /**
   * setter for balance of Account object.
   * @param balance balance to be set
   */
  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
  
  /**
   * getter for typeId of Account object.
   * @return type typeId of the type of account object
   */
  public int getType() {
    return this.type;
  }

  /**
   * Setter for typeId of this account.
   * @param newType the new type id for this account
   */
  public void setType(int newType) { this.type = newType; }
}
