package com.bank.accounts;

import com.bank.databasehelper.DatabaseInsertHelper;
import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.generics.Account;

import java.math.BigDecimal;
import java.sql.SQLException;

/**
 * Class that contains methods for the ChequingAccount object which is a child of Account.
 * 
 * @author Ka-Kit Jason Cheung
 *
 */
public class ChequingAccount extends Account {
  private BigDecimal interestRate;
  
  /**
   * Constructor for ChequingAccount object.
   * @param id id to be set to ChequingAccount object
   * @param name name to be set to ChequingAccount object
   * @param balance balance to be set to ChequingAccount object
   * @throws SQLException Exception thrown if errors in SQL queries
   */
  public ChequingAccount(int id, String name, BigDecimal balance) throws SQLException {
    this.setId(id);
    this.setName(name);
    this.setBalance(balance);    
  }
  
  /**
   * Finds and sets the interest rate of the given account.
   * @throws SQLException Exception thrown if errors in SQL queries
   */
  public void findAndSetInterestRate() throws SQLException {
    BigDecimal rate = DatabaseSelectHelper.getInterestRate(this.getType());
    this.interestRate = rate;
  }
  
  /**
   * Adds the interest to the current account balance.
   * 
   */
  public void addInterest() {
    BigDecimal interest;
    interest = this.getBalance().multiply(this.interestRate);
    this.setBalance(this.getBalance().add(interest));
  }
}
