package com.bank.accounts;

import java.math.BigDecimal;

/**
 * A class to represent a restricted savings account. This account can only be withdrawn from at a
 * TellerTerminal. 
 * @author Richard (ChengChu) Wei
 *
 */
public class RestrictedSavingsAccount extends SavingsAccount {

  private static final long serialVersionUID = -1209257382803218550L;

  /**
   * Constructor for the restricted savings account Object.
   * @param id (int): the id of the newly created account
   * @param name (String): the name of the newly created account
   * @param balance (BigDecimal): the balance of the newly created account
   */
  public RestrictedSavingsAccount(int id, String name, BigDecimal balance) {
    // calls the parent constructor: no difference in variables
    super(id, name, balance);
  }
}
