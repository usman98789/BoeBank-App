package com.activities.atmActivities;

import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.Account;

import java.util.List;

/**
 * Abstract class for an account operation like a deposit or withdrawal
 * @author Austin Seto
 */
public abstract class accountActivity extends AppCompatActivity {
  protected int selectedAccountId;
  protected TextView selectedAccountTextView;
  /**
   * Gets all of a user's accounts and generates buttons that will allow for their selection
   * @param layout the layout to add the buttons to
   * @param accountIds a list of the ids of the accounts
   */
  protected void addAccountSelectors(LinearLayout layout, List<Integer> accountIds) {
    final DatabaseSelectHelper dbInfo = new DatabaseSelectHelper(this);
    for (Integer id : accountIds) {
      // Create new button
      Button newButton = new Button(this);
      // Get details for the current account id
      Account currentAccount = dbInfo.getAccount(id);
      // Set up the text on this button
      String buttonText = "Select account: \n";
      buttonText += currentAccount.getName();
      newButton.setText(buttonText);
      // The account id represents the account the button is linked to
      newButton.setId(currentAccount.getId());
      // Set up button functionality
      newButton.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
          // The buttons id will match the account it is associated with
          selectedAccountId = v.getId();
          // Set the text at the top of the screen that describes the currently selected account
          String selectedMessage = "Current Account: \n";
          selectedMessage += dbInfo.getAccount(v.getId()).getName();
          selectedAccountTextView.setText(selectedMessage);
        }
      });
      // Set dimensions for button
      LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
              LinearLayout.LayoutParams.MATCH_PARENT,  // Width matches parent
              LinearLayout.LayoutParams.WRAP_CONTENT); // Height wraps content
      newButton.setLayoutParams(lp);
      // Add button to layout
      layout.addView(newButton);
    }
    dbInfo.close();
  }
}
