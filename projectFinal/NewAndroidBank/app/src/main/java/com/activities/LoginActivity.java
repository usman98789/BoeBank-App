package com.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bank.database.DatabaseDriverA;
import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.AccountTypes;
import com.bank.generics.Roles;
import com.bank.generics.AccountTypesMap;
import com.bank.generics.RolesMap;
import com.bank.generics.User;
import com.bank.security.PasswordHelpers;
import com.bank.users.Admin;
import com.bank.users.Customer;
import com.bank.users.Teller;


import java.math.BigDecimal;


public class LoginActivity extends AppCompatActivity {
  // declare db object
  private DatabaseDriverA myDb;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_login);
    // create db object to user
    myDb = new DatabaseDriverA(LoginActivity.this);
    // open to read and write
    myDb.getWritableDatabase();
  }

  /**
     * Initlize the database.
     * @param view button that was pressed to initialize database
     */
  public void initializeDatabase(View view){
    // gets the textfield we want to use to tell the user
    TextView prompt = (TextView) findViewById(R.id.loginStatus);

    try {
      // instantiates an object to allow us to insert into the database
      DatabaseInsertHelper inserter = new DatabaseInsertHelper(LoginActivity.this);
      //Insert role types into database
      for (Roles role : Roles.values()) {
        inserter.insertRole(role.name());
      }
      // initializes the default interest rate for each account
      BigDecimal defaultInterestRate = new BigDecimal(0.10);
      // Insert account types into the database
      for (AccountTypes type : AccountTypes.values()) {
        inserter.insertAccountType(type.name(), defaultInterestRate);
      }
      // updates the maps
      AccountTypesMap.updateMap(this);
      RolesMap.updateMap(this);
      // start the intent to bind the current activity to the next
      //Intent currentIntent = new Intent(this, InitializeFirstAdmin.class);
      // starts the acitivty to initialize the admin using the intent
      //startActivity((currentIntent));
      // insert a dummy admin
      int adminRoleId = RolesMap.getRoleId("ADMIN", LoginActivity.this);

      createDialog(view);
    } catch (Exception e) {
      prompt.setText(R.string.LoginActivityError );
    }
  }

    /**
     * Login to account.
     * @param view button pressed for login
     */
  public void loginToAccount (View view) {
    // gets the textfield we want to use to tell the user
    TextView prompt = (TextView) findViewById(R.id.loginStatus);
    // creates the edit text objects that will allow us to get user input
    EditText userIdInput = (EditText) findViewById(R.id.userIdField);
    EditText passwordInput = (EditText) findViewById(R.id.userPasswordField);
    // gets the data from both edit text widgets
    String stringUserId = userIdInput.getText().toString();
    String userPassword = passwordInput.getText().toString();

    // checks if either inputs are empty
    if (stringUserId.matches("") || userPassword.matches("")) {
      prompt.setText(R.string.LoginActivityMissingParams);
    } else {
      // creates a database select helper to get the user
      DatabaseSelectHelper databaseSelectHelper = new DatabaseSelectHelper(this);
      // parses the user Id as an integer
      int userId = Integer.parseInt(stringUserId);
      // in case the user id doesnt
      try {
        // gets the hashed password from the database for the user id
        String hashedPassword = databaseSelectHelper.getPassword(userId);
        // checks if the password is valid and matches
        if (PasswordHelpers.comparePassword(hashedPassword, userPassword)) {
          prompt.setText(R.string.LoginActivityLoggedInPrompt);
          // calls the method to open the correct view (depending on the id)
          openAccountActivity(userId, userPassword);
        } else {
          prompt.setText(R.string.LoginActivityNotSuccessful);
        }
      } catch (Exception e) {
        prompt.setText(R.string.LoginActivityUserNotValid);
      }
    }
  }



    /**
     * Open Account Activity
     * @param userId id of user who is logging in
     * @param userPassword password of user logging in
     * @throws ConnectionFailedException if connection to database fails
     * @throws ItemNotFoundException if user not in database
     */
  private void openAccountActivity(int userId, String userPassword)
      throws ConnectionFailedException, ItemNotFoundException {

    // creates a database select helper to access the database values
    DatabaseSelectHelper databaseSelectHelper = new DatabaseSelectHelper(this);

    // creates a new user object based using database va123lues
    User currentUser = databaseSelectHelper.getUser(userId, this);
    // creates a new intent to bind this activity to the teller terminal one1
    Intent myIntent = null;

    // starts the activity based on type of user
    if (currentUser instanceof Admin) {
      // opens the admin interface if the user is an admin
      myIntent = new Intent(LoginActivity.this, AdminMenuActivity.class);
    } else if (currentUser instanceof Teller) {
      // opens the teller terminal if the user is a teller
      myIntent = new Intent(LoginActivity.this, TellerTerminalActivity.class);
    } else if (currentUser instanceof Customer) {
      // opens the atm interface if the user is a customer
      myIntent = new Intent(LoginActivity.this, AtmActivity.class);
    }
    // allows the user to be accessed on the new activity
    myIntent.putExtra("currentUserId", currentUser.getId());
    myIntent.putExtra("currentUserPassword", userPassword);
    // starts the activity
    startActivity(myIntent);
  }


  public void createDialog (View view)  {
    // creates a layout inflator to inflate layout and create a view from it
    LayoutInflater layoutInflator = getLayoutInflater();
    final View dialogView = layoutInflator.inflate(R.layout.create_user_dialog, null);

    final TextView prompt = (TextView) dialogView.findViewById(R.id.new_user_warning);
    // create objects to gather info from the edit text fields
    final EditText newNameField = (EditText) dialogView.findViewById(R.id.new_user_name);
    final EditText newAgeField = (EditText) dialogView.findViewById(R.id.new_user_age);
    final EditText newAddressField = (EditText) dialogView.findViewById(R.id.new_user_address);
    final EditText newPasswordField = (EditText) dialogView.findViewById(R.id.new_user_password);

    // gets the radio buttons to check what user type should be created
    final RadioButton choiceAdminButton = (RadioButton) dialogView.findViewById(R.id.radio_admin);
    final RadioButton choiceTellerButton = (RadioButton) dialogView.findViewById(R.id.radio_teller);

    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    // alters the alert dialogue
    alertBuilder.setTitle("MAKE NEW USER");
    alertBuilder.setView(dialogView);
    alertBuilder.setPositiveButton("CREATE",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {

            try {
              // collects the user input from the fields
              String newName = newNameField.getText().toString();
              String newAge = newAgeField.getText().toString();
              String newAddress = newAddressField.getText().toString();
              String newPassword = newPasswordField.getText().toString();

              /// instantiates an object to allow us to insert into the database
              DatabaseInsertHelper inserter = new DatabaseInsertHelper(LoginActivity.this);

              int ntAge = Integer.parseInt(newAge);
              long userId = -1;

              // checks what type of user needs to be inserted
              boolean chooseAdmin = choiceAdminButton.isChecked();
              boolean chooseTeller = choiceTellerButton.isChecked();

              // checks if all the fields have been properly filled in
              boolean allCompleted = (!newName.matches("") && !newAge.matches("") &&
                  !newAddress.matches("") && newPassword.matches(""));
              // if neither are checked
              if (!chooseAdmin && !chooseTeller) {
                prompt.setText("Please fill in all your information");
              }
              else if (chooseAdmin && chooseTeller) {
                prompt.setText("Please fill in all your information");
              }
              else if (chooseAdmin) {
                // create a new admin
                int adminRoleId = RolesMap.getRoleId("ADMIN", LoginActivity.this);
                userId = inserter.insertNewUser(newName, ntAge, newAddress, adminRoleId, newPassword);
              }
              else if (chooseTeller) {
                // create a new teller
                int tellerRoleId = RolesMap.getRoleId("TELLER", LoginActivity.this);
                userId = inserter.insertNewUser(newName, ntAge, newAddress, tellerRoleId, newPassword);
              }

            } catch (ItemNotFoundException e) {

            } catch (ConnectionFailedException e) {

            }
          }
        }
    );
    alertBuilder.setNegativeButton("CANCEL",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
          }
        }
    );
    alertBuilder.show();
  }
}
