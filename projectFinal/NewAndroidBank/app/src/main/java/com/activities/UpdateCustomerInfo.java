package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.bank.database.DatabaseUpdateHelper;
import com.bank.users.Customer;

public class UpdateCustomerInfo extends AppCompatActivity {

    // DECLARE WIDGETS
    EditText newNameField;
    EditText newAgeField;
    EditText newAddressField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_customer_info);
    }

    /**
     * update Current Customer Information
     * @param v View to be used
     */
    public void updateCurrCustomerInfo(View v) {
        // initialize widgets of name, age and address
        newNameField = (EditText) findViewById(R.id.updateNameField);
        newAgeField = (EditText) findViewById(R.id.updateAgeField);
        newAddressField = (EditText) findViewById(R.id.updateAddressField);

        Intent i = getIntent();
        // get curr customer in terminal
        Customer c = (Customer) i.getSerializableExtra("currCustomer");
        // create database updater
        DatabaseUpdateHelper dbu = new DatabaseUpdateHelper(this);

        // create new name,address and age
        String newName = null;
        String newAddress = null;
        int newAge = -1;

        // update the fields that are not empty
        if (newNameField.getText().toString().trim().length() > 0) {
            // update with the new name
            newName = newNameField.getText().toString();
        }
        if (newAgeField.getText().toString().trim().length() > 0) {
            // update with the new age
            newAge = Integer.parseInt(newAgeField.getText().toString());
        }

        if (newAddressField.getText().toString().trim().length() > 0) {
            // update with the new address
            newAddress = newAddressField.getText().toString();
        }
        // pass values back to teller terminal activity
        i.putExtra("newName", newName);
        i.putExtra("newAddress", newAddress);
        i.putExtra("newAge", newAge);
        // close the database
        dbu.close();
        // set the Activity to OK
        setResult(Activity.RESULT_OK, i);
        finish();
    }

}
