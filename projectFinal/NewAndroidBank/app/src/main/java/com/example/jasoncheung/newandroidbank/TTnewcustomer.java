package com.example.jasoncheung.newandroidbank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.EditText;

import com.activities.R;
import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.User;

/**
 * Created by usman on 30/07/17.
 */

public class TTnewcustomer  extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_customer);
    }

    public void createCustomer()
            throws com.bank.exceptions.IllegalObjectTypeException,
            com.bank.exceptions.ItemNotFoundException,
            com.bank.exceptions.ConnectionFailedException,
            com.bank.exceptions.IllegalInputParameterException,
            com.bank.exceptions.IllegalAmountException,
            com.bank.exceptions.NotAuthenticatedException{

        EditText name = (EditText)findViewById(R.id.newCustomerNameField);
        EditText age = (EditText)findViewById(R.id.newCustomerAgeField);
        EditText address = (EditText)findViewById(R.id.newCustomerAddressField);
        EditText password = (EditText)findViewById(R.id.newCustomerPasswordField);

        String getname = name.getText().toString();
        String getage = name.getText().toString();
        String getaddress = address.getText().toString();
        String getpassword = password.getText().toString();

        int intage = Integer.parseInt(getage);

        // GET TELLER ID AND PASSWORD USING INTENT
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        User teller = (User)extras.get("currentUser");
        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(TTnewcustomer.this);
        String tellerPass = selectHelper.getPassword(teller.getId());
        com.bank.bank.TellerTerminal tellerTerminal = new com.bank.bank.TellerTerminal(teller.getId(), tellerPass , TTnewcustomer.this);
        tellerTerminal.makeNewUser(getname, intage, getaddress, getpassword, TTnewcustomer.this);
    }
}
