package com.bank.accounts;

import com.bank.generics.Account;
import java.math.BigDecimal;

public class Loan extends Account {
  
  static final long serialVersionUID = 7203590393744090941L;

  /**
   * Constructor for Loan Account.
   * @param id id to be set to Loan object
   * @param name name to be set to Loan object
   * @param balance balance to be set to Loan object
   */
  public Loan(int id, String name, BigDecimal balance) {
    this.setId(id);
    this.setName(name);
    this.setBalance(balance);  
  }
}
