package com.bank.bank;

import android.content.Context;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.InsufficientFundsException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.NotAuthenticatedException;

import java.math.BigDecimal;


/**
 * Interface for performing financial operations with accounts.
 * @author jasoncheung, Austin Seto
 *
 */
public interface FinancialAccountInterface {

  /**
   * Withdraws the given amount of money from the account given.
   * @param amount the amount to withdraw, must be non-negative
   * @param accountId the account to withdraw from
   * @return whether or not withdrawal was made successfully
   * @throws IllegalAmountException if attempting to withdraw a negative amount of money
   * @throws InsufficientFundsException if the account does not have enough 
   * @throws NotAuthenticatedException if one does not have permissions to
   *     withdraw from this account
   * @throws ItemNotFoundException if the account does not exist
   * @throws ConnectionFailedException if the connection to the database fails somehow
   */
  public boolean makeWithdrawal(BigDecimal amount, int accountId, Context context)
      throws IllegalAmountException, InsufficientFundsException, NotAuthenticatedException,
             ItemNotFoundException, ConnectionFailedException;
  
  /**
   * Deposits the given amount of money into the account given.
   * @param amount the amount to deposit, must be non-negative
   * @param accountId the account to deposit into
   * @return whether or not deposit was made successfully
   * @throws IllegalAmountException if attempting to deposit a negative amount of money
   * @throws NotAuthenticatedException if one does not have permission to deposit into this account
   * @throws ConnectionFailedException if connection to database fails somehow
   * @throws ItemNotFoundException if account does not exist
   */
  public boolean makeDeposit(BigDecimal amount, int accountId, Context context)
      throws IllegalAmountException, NotAuthenticatedException,
             ConnectionFailedException, ItemNotFoundException;
  
  /**
   * Returns the balance currently in an account.
   * @param accountId the account to view
   * @return the deposit in the account
   * @throws NotAuthenticatedException If one does not have permission to view this account
   * @throws ConnectionFailedException if connection to database fails somehow
   * @throws ItemNotFoundException if account does not exist
   */
  public BigDecimal checkBalance(int accountId, Context context)
      throws NotAuthenticatedException, ConnectionFailedException, ItemNotFoundException;
}
