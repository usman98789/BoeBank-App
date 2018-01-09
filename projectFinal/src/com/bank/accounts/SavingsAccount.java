package com.bank.accounts;

import com.bank.generics.Account;

import java.math.BigDecimal;

/**
 * Class that contains methods for the SavingsAccount object which is a child of Account.
 * @author Ka-Kit Jason Cheung
 *
 */
public class SavingsAccount extends Account {

  private static final long serialVersionUID = 5187327696629558833L;

  /**
   * Constructor for SavingsAccount object.
   * @param id id to be set to SavingsAccount object
   * @param name name to be set to SavingsAccount object
   * @param balance balance to be set to SavingsAccount object
   */
  public SavingsAccount(int id, String name, BigDecimal balance) {
    this.setId(id);
    this.setName(name);
    this.setBalance(balance); 
  }
}
