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
 * Activity so a user can make a deposit into an account.
 * @author Austin Seto
 */
public class depositActivity extends accountActivity {

  private EditText depositAmount;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    Intent intent = getIntent();
    setContentView(R.layout.activity_deposit);
    // Sets up the text boxes
    selectedAccountTextView = (TextView) findViewById(R.id.atm_deposit_selected_account);
    depositAmount = (EditText) findViewById(R.id.atm_deposit_amount);
    // Get content_withdrawal.xml
    LinearLayout accountList = (LinearLayout) findViewById(R.id.deposit_account_select);
    // Get list of account ids from intent
    List<Integer> accountIds = (ArrayList<Integer>) intent.getSerializableExtra("accounts");
    addAccountSelectors(accountList, accountIds);
  }

  /**
   * Tries to make a deposit into an account with the rest of the information given
   * @param view the button for this function
   */
  public void makeDeposit(View view) {
    DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    DatabaseUpdateHelper dbUpdate = new DatabaseUpdateHelper(this);
    BigDecimal newBalance = dbInfo.getBalance(selectedAccountId); // Initial balance
    BigDecimal amount = new BigDecimal(depositAmount.getText().toString()); // Amount withdrawn
    newBalance = newBalance.add(amount);
    boolean successful = dbUpdate.updateAccountBalance(newBalance, selectedAccountId);
    // Notify user of withdraw
    Toast notif;
    if (successful) {
      notif = Toast.makeText(this, "Deposit successful!", Toast.LENGTH_SHORT);
    } else {
      notif = Toast.makeText(this, "Deposit failed!", Toast.LENGTH_SHORT);
    }
    notif.show();
  }
}
