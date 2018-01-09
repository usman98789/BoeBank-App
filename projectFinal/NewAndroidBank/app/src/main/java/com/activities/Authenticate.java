package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.NotAuthenticatedException;
import com.bank.generics.User;
import com.bank.users.Customer;

public class Authenticate extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticate);
    }

    /**
     * Authenticate the User.
     * @param v the button that calls this function
     * @throws IllegalObjectTypeException
     * @throws ItemNotFoundException
     * @throws ConnectionFailedException
     * @throws IllegalInputParameterException
     * @throws IllegalAmountException
     * @throws NotAuthenticatedException
     */
    public void authenticateUser(View v)
            throws IllegalObjectTypeException, ItemNotFoundException,
                   ConnectionFailedException, IllegalInputParameterException,
                   IllegalAmountException, NotAuthenticatedException {

        // declare editText objects
        EditText id = (EditText)findViewById(R.id.customerIdField);
        EditText pass = (EditText)findViewById(R.id.customerPwdField);
        Toast toastNotif;
        boolean idFieldEmpty = !(id.getText().toString().trim().length() > 0);
        boolean passFieldEmpty = !(id.getText().toString().trim().length() > 0);
        // check if the fields are empty
        if (idFieldEmpty || passFieldEmpty) {
            toastNotif = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }

        // get the id and password for customer
        String getid = id.getText().toString();
        String getpass = pass.getText().toString();

        int intId = Integer.parseInt(getid);
        // set up a toast
        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(Authenticate.this);
        // get the customer
        User tempU = selectHelper.getUser(intId, Authenticate.this);
        // if not customer
        if (!(tempU instanceof Customer)) {
            // id doesnt belong to customer
            toastNotif = Toast.makeText(this, "ID doesn't belong to a customer",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }
        // create customer
        Customer c = (Customer) selectHelper.getUser(intId, Authenticate.this);
        // authenticate this user
        boolean authenticated = c.authenticate(getpass, Authenticate.this);
        if (authenticated) {
            // if it's authenticated, return to the previous screen
            // set the currentCustomer and currentCustomerAuthenticated to true
            // pass the customer to the last screen
            Intent prevActivity = new Intent();
            // pass back customer id
            prevActivity.putExtra("customerId", intId);
            // pass back customer password
            prevActivity.putExtra("customerPassword", getpass);
            // pass back authenticated result
            prevActivity.putExtra("authResult", authenticated);
            setResult(Activity.RESULT_OK, prevActivity);
            finish();
        } else {
            // print error message
            toastNotif = Toast.makeText(this, "Invalid password",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
        }
        // close database
        selectHelper.close();
    }
}
