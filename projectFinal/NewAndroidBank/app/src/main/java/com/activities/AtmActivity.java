package com.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.activities.atmActivities.accountViewActivity;
import com.activities.atmActivities.depositActivity;
import com.activities.atmActivities.withdrawalActivity;
import com.bank.users.Customer;
import com.bank.database.DatabaseSelectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * An activity for selecting atm activities.
 * @author Austin Seto
 */
public class AtmActivity extends AppCompatActivity {
  private Customer customerInfo;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.atm);
    Intent intent = getIntent();
    // Get the id passed to this by the intent, if not found default is -1
    int id = intent.getIntExtra("currentUserId", -1);
    // Set up database info getter
    DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    try {
      customerInfo = (Customer) dbInfo.getUser(id, this);
    } catch (Exception e) {
      super.onBackPressed(); // Go back if error getting user
    } finally {
      dbInfo.close();
    }

    TextView welcome = (TextView) findViewById(R.id.welcome);
    String welcomeMessage = "Welcome " + customerInfo.getName();
    welcome.setText(welcomeMessage);
  }

  /**
   * Goes to a different activity which lists all the accounts the user has.
   * @param view the button that is calling this
   */
  public void viewAccounts(View view) {
    Intent intent = new Intent(this, accountViewActivity.class);
    // Send the current customer's accounts to the activity
    DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    List<Integer> accountIds = dbInfo.getAccountIdsList(customerInfo.getId());
    intent.putExtra("accounts", (ArrayList<Integer>) accountIds);
    dbInfo.close();
    startActivity(intent);
  }

  /**
   * Goes to the activity for making a withdrawal from an account.
   * @param view the button that is calling this
   */
  public void withdraw(View view) {
    Intent intent = new Intent(this, withdrawalActivity.class);
    // Send account ids to activity
    DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    List<Integer> accountIds = dbInfo.getAccountIdsList(customerInfo.getId());
    intent.putExtra("accounts", (ArrayList<Integer>) accountIds);
    dbInfo.close();
    startActivity(intent);
  }

  /**
   * Goes to the activity for making a deposit into an account.
   * @param view the button that is calling this
   */
  public void deposit(View view) {
    Intent intent = new Intent(this, depositActivity.class);
    // Send account ids to activity
    DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    List<Integer> accountIds = dbInfo.getAccountIdsList(customerInfo.getId());
    intent.putExtra("accounts", (ArrayList<Integer>) accountIds);
    dbInfo.close();
    startActivity(intent);
  }

  /**
   * Logs out of this atm, clearing the customer info.
   * @param view the button calling this
   */
  public void logout(View view) {
    customerInfo = null;
    // Go back to previous screen
    super.onBackPressed();
  }
}
