package com.example.jasoncheung.newandroidbank;

import android.content.Context;
import android.content.Intent;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.activities.R;
import com.bank.bank.TellerTerminal;
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
import com.bank.users.Teller;
import com.bank.users.Customer;

import java.math.BigDecimal;

import org.w3c.dom.Text;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;


public class LoginActivity extends AppCompatActivity {
  // DECLARE WIDGETS
//  TextView tv;
//  EditText userIdField;
//  EditText userPwdField;
//  Button loginBtn;
//  Button initializeDbBtn;
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
    // uncomment to read from database only
    // myDb.getReadableDatabase();

  }

  public void initializeDatabase(View view){
    // gets the textfield we want to use to tell the user
    TextView prompt = (TextView) findViewById(R.id.loginStatus);
    // change the textfield to tell the user the DB has been initialized
    prompt.setText("The field has been initialized");

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
      // starts the activity to initialize the admin using the intent
      //startActivity((currentIntent));
      // insert a dummy admin
      int adminRoleId = RolesMap.getRoleId("ADMIN", LoginActivity.this);
      long userId = inserter.insertNewUser("richard", 12, "china", adminRoleId, "123");
      int tellerRoleId = RolesMap.getRoleId("TELLER", this);
      long userId2 = inserter.insertNewUser("usman", 13, "paki", tellerRoleId, "123");

      DatabaseSelectHelper dbSelect = new DatabaseSelectHelper(this);
      User currentAdmin = dbSelect.getUser((int)userId, LoginActivity.this);
      // gets the user details and print them out
      String userName = currentAdmin.getName();
      int userID = currentAdmin.getId();
      int userRoleID = currentAdmin.getRoleId();
      String userAddress = currentAdmin.getAddress();

      prompt.setText("NAME: " + userName +
          "\nUSERID: " + userID +
          "\nROLEID: " + userRoleID +
          "\nADDRESS: " + userAddress);
    } catch (Exception e) {
      prompt.setText("UH OH, there was an error in initializing the database" );
    }
  }



  public void loginToAccount (View view) throws ConnectionFailedException, ItemNotFoundException{
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
      prompt.setText("one of your login details has been left empty");
    } else {
      // creates a database select helper to get the user
      DatabaseSelectHelper databaseSelectHelper = new DatabaseSelectHelper(this);
      // parses the user Id as an integer
      int userId = Integer.parseInt(stringUserId);
      // gets the hashed password from the database for the user id
      String hashedPassword = databaseSelectHelper.getPassword(userId);
      if (PasswordHelpers.comparePassword(hashedPassword, userPassword)) {
        prompt.setText("Logged in1 ");
        // calls the method to open the correct view (depending on the id)
        openAccountActivity(userId);
      }
      else {
        prompt.setText(getResources().getString(R.string.success));
      }
    }
  }

  private void openAccountActivity(int userId)
      throws ConnectionFailedException, ItemNotFoundException{
    // gets the textfield we want to use to tell the user
    TextView prompt = (TextView) findViewById(R.id.loginStatus);
    // creates a database select helper to access the database values
    DatabaseSelectHelper databaseSelectHelper = new DatabaseSelectHelper(this);

    prompt.setText("Logged asd sa d");

    // creates a new user object based using database va123lues
    User currentUser = databaseSelectHelper.getUser(userId, this);

    // creates a new intent to bind this activity to the teller terminal one1
    Intent myIntent = null;

    // starts the activity based on type of user
    if(currentUser instanceof Admin) {
      // opens the admin interface if the user is an admin
      prompt.setText("Logged in as an admin");
      //
      myIntent = new Intent(LoginActivity.this, TellerTerminal.class);
      // allows the user to be accessed on the new activity
      myIntent.putExtra("userId", currentUser);
      prompt.setText("UR MUM ");
      // starts the activity
      startActivity(myIntent);

    }
    else if (currentUser instanceof Teller) {
      // opens the teller terminal if the user is a teller
      prompt.setText("Logged in as a teller");
      //
      myIntent = new Intent(LoginActivity.this, TellerTerminal.class);
      // allows the user to be accessed on the new activity
      myIntent.putExtra("userId", currentUser);
    }
    else if (currentUser instanceof Customer) {
      // opens the ATM interface if the user is a customer
      prompt.setText("Logged in as an customer");
      //
      myIntent = new Intent(LoginActivity.this, TellerTerminal.class);
      // allows the user to be accessed on the new activity
      myIntent.putExtra("userId", currentUser);
    }

  }


}
