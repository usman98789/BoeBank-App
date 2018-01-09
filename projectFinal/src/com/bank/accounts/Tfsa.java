package com.bank.accounts;

import com.bank.generics.Account;

import java.math.BigDecimal;

/**
 * Class that contains methods for the ChequingAccount object which is a child of Account.
 * 
 * @author Ka-Kit Jason Cheung
 *
 */
public class Tfsa extends Account {

  private static final long serialVersionUID = 6630415707393782597L;

  /**
   * Constructor for Tfsa object.
   * @param id id to be set to Tfsa object
   * @param name name to be set to Tfsa object
   * @param balance balance to be set to Tfsa object
   */
  public Tfsa(int id, String name, BigDecimal balance) {
    this.setId(id);
    this.setName(name);
    this.setBalance(balance); 
  }
}
