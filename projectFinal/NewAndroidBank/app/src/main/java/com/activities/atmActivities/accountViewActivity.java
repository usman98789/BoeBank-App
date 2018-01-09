package com.activities.atmActivities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.activities.R;
import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.Account;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity for viewing all of a user's accounts
 * @author Austin Seto
 */
public class accountViewActivity extends AppCompatActivity {

  TextView accountName;
  TextView accountType;
  TextView accountBalance;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    setContentView(R.layout.account_overview);
    // Get account_content.xml
    LinearLayout accounts = (LinearLayout) findViewById(R.id.view_of_accounts);
    ArrayList<Integer> accountList = (ArrayList<Integer>) intent.getSerializableExtra("accounts");
    addAccountsToLayout(accounts, accountList);
  }

  private void addAccountsToLayout(LinearLayout layout, List<Integer> accountIds) {
    int generalPadding = 10;
    int textSize = 25;
    DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    for (Integer id : accountIds) {
      Account a = dbInfo.getAccount(id);
      // Set up name text view
      accountName = new TextView(this);
      String name = a.getId() + " - " + a.getName();
      accountName.setText(name);
      accountName.setTextSize(textSize);
      accountName.setPadding(generalPadding, generalPadding, generalPadding, generalPadding * 2);
      // Set up account type text view
      accountType = new TextView(this);
      String type = dbInfo.getAccountTypeName(a.getType());
      accountType.setText(type);
      accountType.setTextSize(textSize/2);
      accountType.setPadding(generalPadding, generalPadding, generalPadding, generalPadding * 2);
      // Set up account balance
      accountBalance = new TextView(this);
      BigDecimal balance = a.getBalance();
      String balanceString = "$" + balance.toPlainString();
      accountBalance.setText(balanceString);
      accountBalance.setTextSize(textSize);
      if (balance.compareTo(BigDecimal.ZERO) < 0) {
        // If balance is negative
        accountBalance.setTextColor(getResources().getColor(R.color.negativeBalanceColour, null));
      } else if (balance.compareTo(BigDecimal.ZERO) > 0) {
        // If balance is positive
        accountBalance.setTextColor(getResources().getColor(R.color.positiveBalanceColour, null));
      } else {
        // If balance is zero
        accountBalance.setTextColor(getResources().getColor(R.color.zeroBalanceColour, null));
      }
      accountBalance.setPadding(generalPadding, generalPadding, generalPadding, generalPadding * 2);
      // Add details in order
      layout.addView(accountName);
      layout.addView(accountType);
      layout.addView(accountBalance);
    }
    dbInfo.close();
  }
}
