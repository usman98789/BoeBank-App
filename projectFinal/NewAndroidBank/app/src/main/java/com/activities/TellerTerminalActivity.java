package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.bank.bank.TellerTerminal;
import com.bank.database.DatabaseSelectHelper;
import com.bank.users.Customer;

import java.math.BigDecimal;

/**
 * Created by usman on 30/07/17.
 */
public class TellerTerminalActivity extends AppCompatActivity {
    // create a global teller terminal object
    TellerTerminal tellerTerminal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.teller_terminal);
        // TellerTerminal tellerTerminal = new TellerTerminal();
        Intent intent = this.getIntent();
        int currTellerId = intent.getIntExtra("currentUserId", 0);
        String password = intent.getStringExtra("password");
        // create tellerTerminal object
        try {
            tellerTerminal = new TellerTerminal(currTellerId, password, this);
        } catch (Exception e) {
            // go back to log in screen
            super.onBackPressed();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // for data passed via authentication of customer
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                authenticateCustomerAttempt(data);
            }
        } else if (requestCode == 2) {
            // for data passed via creating new customer
            if (resultCode == Activity.RESULT_OK) {
                createNewCustomerAttempt(data);
            }

        } else if (requestCode == 3) {
            // for data passed via creating a new account
            if (data != null) {
                createNewAccountAttempt(data);
                makeCustomToast("New account created.");
            }
        } else if (requestCode == 4) {
            // for data passed via adding interest to account
            if (resultCode == Activity.RESULT_OK) {
                // attempt to add interest
                giveInterestAttempt(data);
            }

        } else if (requestCode == 5) {
            // for data passed back for depositing to account
            if (resultCode == Activity.RESULT_OK) {
                // attempt to add interest
                TellerdepositAttempt(data);
            }
        }  else if (requestCode == 6) {
            // for data passed back for withdrawing to account
            if (resultCode == Activity.RESULT_OK) {
                // attempt to add interest
                withdrawlAttempt(data);
            }
        } else if (requestCode == 9) {
            // for data passed back for updating customer info
            if (data != null) {
                updateCustomerAttempt(data);
                makeCustomToast("Customer information updated.");
            }
        }
    }

    /**
     * Method for clicking authenticate User
     * @param v View to be used
     */
    public void authenticateCustomer(View v){
        // create intent to go to authenticate screen
        Intent i = new Intent(this, Authenticate.class);
        // start the screen
        startActivityForResult(i, 1);
    }

    /**
     * Method for clicking create new Customer
     * @param v View to be used
     */
    public void onButton2click(View v) {
        // create intent to go to create new customer
        Intent i =  new Intent(this, TTnewcustomer.class);
        startActivityForResult(i, 2);
    }

    /**
     * Method for clicking create new Account
     * @param v View to be used
     */
    public void createNewAccount(View v) {
        // validate that currCustomer is authenticated
        if (tellerTerminal.isCustomerAuthenticated()) {
            Intent i = new Intent(this, newAccount.class);
            startActivityForResult(i, 3);
        } else {
            printNotAuthenticatedToast();
        }
    }

    /**
     * Method for clicking give interest
     * @param v View to be used
     */
    public void giveAccountInterest(View v){
        //check if there's a customer
        if (tellerTerminal.currentCustomerPresent()) {
            Intent i = new Intent(this, giveInterest.class);
            i.putExtra("currCustomer", tellerTerminal.getCurrentCustomer());
            startActivityForResult(i, 4);
        } else {
            printNotAuthenticatedToast();
        }
    }

    /**
     * Method for clicking Make a deposit
     * @param v View to be used
     */
    public void makeDeposit(View v){
        // check if there's a customer
        if (tellerTerminal.currentCustomerPresent()){
            Intent i = new Intent(this, Tellerdeposit.class);
            i.putExtra("currCustomer", tellerTerminal.getCurrentCustomer());
            startActivityForResult(i, 5);
        } else {
            printNotAuthenticatedToast();
        }
    }

    /**
     * Attempt to deposit with Teller
     * @param data Intent object to be used
     */
    public void TellerdepositAttempt(Intent data) {
        // get the accountId and balance
        int accountId = data.getIntExtra("id", 0);
        BigDecimal balance = (BigDecimal) data.getSerializableExtra("depositbalance");

        // attempt to make deposit
        try {
            tellerTerminal.makeDeposit(balance, accountId, this);
            makeCustomToast("Deposit successfully placed.");
        } catch (Exception e) {
            e.printStackTrace();
            makeCustomToast("Error placing deposit.");
        }
    }

    /**
     * Method for make a withdrawal
     * @param v view object to be used
     */
    public void withdrawl(View v){
        //check if there's a customer
        if (tellerTerminal.currentCustomerPresent()) {
            // create Intent
            Intent i = new Intent(this, Tellerwithdrawl.class);
            i.putExtra("currCustomer", tellerTerminal.getCurrentCustomer());
            // start Activity with currentCustomer passed in
            startActivityForResult(i, 6);
        } else {
            printNotAuthenticatedToast();
        }
    }

    /**
     * Attempt to withdraw
     * @param data Intent object to be used
     */
    public void withdrawlAttempt (Intent data) {
        // get the accountId and balance
        int accountId = data.getIntExtra("Accid", 0);
        BigDecimal balance = (BigDecimal) data.getSerializableExtra("withdrawlAmount");

        // attempt to withdrawl
        try {
            // make the withdrawl
            tellerTerminal.makeWithdrawal(balance, accountId, this);
            makeCustomToast("withdrawl successfully made.");
        } catch (Exception e) {
            e.printStackTrace();
            makeCustomToast("Error making withdrawl.");
        }
    }

    /**
     * Method for checking the balances for a customer's accounts.
     * @param v View object to be used
     */
    public void checkAccountBalances(View v) {
        // get intent to move to accountBalancesActivity
        Intent intent = new Intent(this, CustomerAccountsInfo.class);
        // check if both teller and customer are authenticated

        if (tellerTerminal.isCustomerAuthenticated()) {
            // pass the currCustomer to next intent
            intent.putExtra("currCustomer", tellerTerminal.getCurrentCustomer());
            // start the activity
            startActivity(intent);
            // get all accounts belonging to user

            // iterate through all the accounts and print out the info
        } else {
           printNotAuthenticatedToast();
        }
    }

    /**
     * method called when button pressed to view balance of any customer
     * @param v View object to be used
     */
    public void viewAnyCustomerBalance(View v) {
        // check if teller is authenticated
        if (tellerTerminal.isTellerAuthenticated()) {
            Intent intent = new Intent(this, ViewAnyCustBal.class);
            // pass current customer into next activity
            startActivity(intent);
        } else {
            printNotAuthenticatedToast();
        }
    }

    /**
     *  Method called when button pressed to update customer info
     * @param v View object to be used
     */
    public void updateCustInfo(View v) {
        // check if there's a customer
        if (tellerTerminal.isCustomerAuthenticated()) {
            // move to new activity
            Intent i = new Intent(this, UpdateCustomerInfo.class);
            // pass the current customer
            i.putExtra("currCustomer", tellerTerminal.getCurrentCustomer());
            // start the activity
            startActivityForResult(i, 9);
        } else {
            // prompt there's no customer
            printNotAuthenticatedToast();
        }
    }

    /**
     * method called when button pressed to view current customer's messages
     * @param v View object to be used
     */
    public void viewCurrCustomerMsgs(View v) {
        // check if there's a customer
        if (tellerTerminal.currentCustomerPresent()) {
            // pass the customer into new activity
            Intent i = new Intent(this, ViewUserInbox.class);
            i.putExtra("currUser", tellerTerminal.getCurrentCustomer());
            // start the activity
            startActivity(i);
        } else {
            printNotAuthenticatedToast();
        }

    }

    /**
     * Method called when button pressed to view teller's messages
     * @param v View object to be used
     */
    public void viewMyMessages(View v) {
        Intent i = new Intent(this, ViewUserInbox.class);
        i.putExtra("currUser", tellerTerminal.getCurrentTeller());
        // start activity
        startActivity(i);
    }

    /**
     * Method called when button pressed to leave message for customer
     * @param v View object to be userd
     */
    public void createMessageForUser(View v) {
        // create the Intent
        Intent i = new Intent(this, CreateMessage.class);
        // start the Activity
        startActivity(i);
    }

    /**
     * Method called when end session btn is pressed
     * @param v View object to be userd
     */
    public void endSession(View v) {
        // deauthenticate and remove customer
        tellerTerminal.deauthenticateCustomer();
        tellerTerminal.setCurrentCustomer(null);
        makeCustomToast("Successfully ended customer session.");
    }

    /**
     * Logout
     * @param v View object to be userd
     */
    public void logout(View v) {
        // set current Teller to NUll
        tellerTerminal.setCurrentTeller(null);
        finish();
        makeCustomToast("Successfully logged out.");
    }
    /**
     * method called when returning from Authenticate.java activity
     * @param data Intent object to be used
     */
    public void authenticateCustomerAttempt(Intent data){
        // add data to the right vars
        int currCustomerId = data.getIntExtra("customerId", 0);
        String currCustomerPassword = data.getStringExtra("customerPassword");
        // add curr customer to tellerTerminal object
        DatabaseSelectHelper dbs = new DatabaseSelectHelper(this);
        try {
            tellerTerminal.setCurrentCustomer((Customer) dbs.getUser(currCustomerId, this));
            // authenticate the customer
            tellerTerminal.authenticateCustomer(currCustomerPassword, this);
            // create user for toast message
            String msg = "Customer with ID " + currCustomerId + " is authenticated.";
            Toast toastNotif = Toast.makeText(this, msg, Toast.LENGTH_LONG);
            toastNotif.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method called when returning from TTnewcustomer.java activity
     * @param data Intent object to be used
     */
    public void createNewCustomerAttempt(Intent data) {
        // prompt teller that user must be authenticated now
        String msg = "To use new customer with id: " + data.getIntExtra("newCustId", 0)
                + " please authenticate.";
        Toast toastNotif = Toast.makeText(this, msg, Toast.LENGTH_LONG);
        toastNotif.show();
    }

    /**
     * method called when returning form the newAccount.java activity
     * @param data Intent object to be used
     */
    public void createNewAccountAttempt(Intent data) {
        // get Account Name and balance
        String accName = data.getStringExtra("accName");
        BigDecimal accBalance = (BigDecimal) data.getSerializableExtra("accBalance");
        int accTypeId = data.getIntExtra("accTypeId", 0);

        // create a new account in the tellerTerminal
        try {
            // use teller terminal to make new account
            tellerTerminal.makeNewAccount(accName, accBalance, accTypeId, this);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * method called when returning from giveInterest activity
     * @param data Intent object to be used
     */
    public void giveInterestAttempt(Intent data) {
        // get account Id
        int accountId = data.getIntExtra("accountId", 0);
        // attempt to give interest
        try {
            tellerTerminal.giveInterest(accountId, this);
            makeCustomToast("Interest successfully given.");
        } catch (Exception e) {
            e.printStackTrace();
            makeCustomToast("Error giving interest.");
        }
    }

    /**
     * method called when returning from updateCustomerInfo activity
     * @param data Intent object to be used
     */
    public void updateCustomerAttempt(Intent data) {
        // get age, name and address
        int newAge = data.getIntExtra("newAge", -1);
        String newName = data.getStringExtra("newName");
        String newAddress = data.getStringExtra("newAddress");

        // update the ones that are not empty
        // for age
        if (newAge != -1 && newAge > 0) {
            try {
                // update age
                tellerTerminal.updateCustomerAge(newAge, this);
            } catch (Exception e) {
                makeCustomToast("Error updating customer age.");
            }
        }
        // for name
        if (newName != null) {
            try {
                // update name
                tellerTerminal.updateCustomerName(newName, this);
            } catch (Exception e) {
                makeCustomToast("Error updating customer name");
            }
        }
        // for address
        if (newAddress != null) {
            try {
                // update address
                tellerTerminal.updateCustomerAddres(newAddress, this);
            } catch (Exception e) {
                makeCustomToast("Error updating customer address");
            }
        }
    }

    /**
     * prints a toast with the default customer not authenticated message
     */
    public void printNotAuthenticatedToast() {
        // make the toast
        Toast toast = Toast.makeText(this, "Please authenticate a customer first.",
                Toast.LENGTH_SHORT);
        // display the toast
        toast.show();
    }

    /**
     * Makes toast with custom message
     * @param msg msg object to be used
     */
    public void makeCustomToast(String msg) {
        // make toast notification
        Toast toastNotif = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        // show the toast
        toastNotif.show();
    }
}

