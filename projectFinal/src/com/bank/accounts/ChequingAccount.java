package com.bank.accounts;

import com.bank.generics.Account;

import java.math.BigDecimal;

/**
 * Class that contains methods for the ChequingAccount object which is a child of Account.
 * 
 * @author Ka-Kit Jason Cheung
 *
 */
public class ChequingAccount extends Account {

  private static final long serialVersionUID = -335106989995546235L;

  /**
   * Constructor for ChequingAccount object.
   * @param id id to be set to ChequingAccount object
   * @param name name to be set to ChequingAccount object
   * @param balance balance to be set to ChequingAccount object
   */
  public ChequingAccount(int id, String name, BigDecimal balance) {
    this.setId(id);
    this.setName(name);
    this.setBalance(balance);    
  }
}
