package com.activities.atmActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.activities.R;
import com.bank.database.DatabaseSelectHelper;
import com.bank.database.DatabaseUpdateHelper;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

/**
 * Activity so a user can make a withdrawal from an account
 * @author Austin Seto
 */
public class withdrawalActivity extends accountActivity {

  private EditText withdrawAmount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    setContentView(R.layout.activity_withdrawal);
    // Sets up the text boxes
    selectedAccountTextView = (TextView) findViewById(R.id.atm_withdraw_selected_account);
    withdrawAmount = (EditText) findViewById(R.id.atm_withdraw_amount);
    // Get content_withdrawal.xml
    LinearLayout accountList = (LinearLayout) findViewById(R.id.withdraw_account_select);
    // Get list of account ids from intent
    List<Integer> accountIds = (ArrayList<Integer>) intent.getSerializableExtra("accounts");
    addAccountSelectors(accountList, accountIds);
  }

  /**
   * Make a withdrawal with the given information.
   * @param view the button that calls this
   */
  public void makeWithdrawal(View view) {
    DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    DatabaseUpdateHelper dbUpdate = new DatabaseUpdateHelper(this);
    BigDecimal newBalance = dbInfo.getBalance(selectedAccountId); // Initial balance
    BigDecimal amount = new BigDecimal(withdrawAmount.getText().toString()); // Amount withdrawn
    newBalance = newBalance.subtract(amount);
    boolean successful = dbUpdate.updateAccountBalance(newBalance, selectedAccountId);
    // Notify user of withdraw
    Toast notif;
    if (successful) {
      notif = Toast.makeText(this, "Withdraw successful!", Toast.LENGTH_SHORT);
    } else {
      notif = Toast.makeText(this, "Withdraw failed!", Toast.LENGTH_SHORT);
    }
    notif.show();
  }
}
