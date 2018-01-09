package com.bank.generics;

import android.content.Context;

import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;

import java.math.BigDecimal;


public abstract class AccountWithInterest extends Account {
  public static final BigDecimal MAX_INTEREST = new BigDecimal("1.0");
  public static final BigDecimal MIN_INTEREST = new BigDecimal("0.0");

  private BigDecimal interestRate;

  /**
   * Determines whether the given interest rate is valid for accounts with interest.
   * @param interest the interest rate to check
   * @return whether or not {@code interest} is valid
   */
  public static final boolean validInterestRate(BigDecimal interest) {
    // Check if interest rate is too big or too small
    boolean tooBig = (interest.compareTo(MAX_INTEREST) > 0);
    boolean tooSmall = (interest.compareTo(MIN_INTEREST) < 0);
    return !(tooBig || tooSmall);
  }

  /**
   * Finds and sets the interest rate of the given account.
   * @throws ConnectionFailedException if conncetion to database fails
   * @throws ItemNotFoundException if this account type is not in the database
   */
  public void findAndSetInterestRate(Context context)
      throws ItemNotFoundException, ConnectionFailedException {

    // creates the DatabaseHelper object
    DatabaseSelectHelper databaseHelper = new DatabaseSelectHelper(context);
    // gets the current account type ID and use that to get the interest rate
    int accountTypeId = this.getType();
    BigDecimal rate = databaseHelper.getInterestRate(accountTypeId);
    // returns the interest rate
    this.interestRate = rate;
  }


  /**
   * Adds interest to the current account balance.
   */
  public void addInterest() {
    BigDecimal interest;
    interest = this.getBalance().multiply(this.interestRate);
    this.setBalance(this.getBalance().add(interest));
  }
}
