package com.bank.accounts;

import com.bank.databasehelper.DatabaseSelectHelper;

import com.bank.generics.Account;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Class that contains methods for the SavingsAccount object which is a child of Account.
 * @author Ka-Kit Jason Cheung
 *
 */
public class SavingsAccount extends Account {
  private BigDecimal interestRate;
  
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
  
  /**
   * Finds and sets the interest rate of the given account.
   * @throws SQLException Exception thrown if errors during SQL queries
   */
  public void findAndSetInterestRate() throws SQLException {
    BigDecimal rate = DatabaseSelectHelper.getInterestRate(this.getType());
    this.interestRate = rate;
  }
  
  /**
   * Adds the interest to the current account balance.
   */
  public void addInterest() {
    BigDecimal interest;
    interest = this.getBalance().multiply(this.interestRate);
    this.setBalance(this.getBalance().add(interest));
  }
}
