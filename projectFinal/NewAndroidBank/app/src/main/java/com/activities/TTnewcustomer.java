package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by usman on 30/07/17.
 */

public class TTnewcustomer  extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_customer);
    }

    /**
     * Create Customer
     * @param v View to be used
     * @throws com.bank.exceptions.IllegalObjectTypeException Throws exception if wrong object type
     * @throws com.bank.exceptions.ItemNotFoundException Throws exception if item not found
     * @throws com.bank.exceptions.IllegalInputParameterException throws exception if illegal param
     * @throws com.bank.exceptions.IllegalAmountException throws exception if illegal amount entered
     * @throws com.bank.exceptions.NotAuthenticatedException throws execption if not authenticated
     */
    public void createCustomer(View v)
            throws com.bank.exceptions.IllegalObjectTypeException,
            com.bank.exceptions.ItemNotFoundException,
            com.bank.exceptions.ConnectionFailedException,
            com.bank.exceptions.IllegalInputParameterException,
            com.bank.exceptions.IllegalAmountException,
            com.bank.exceptions.NotAuthenticatedException{

        // create name, age, address, password
        EditText name = (EditText)findViewById(R.id.newCustomerNameField);
        EditText age = (EditText)findViewById(R.id.newCustomerAgeField);
        EditText address = (EditText)findViewById(R.id.newCustomerAddressField);
        EditText password = (EditText)findViewById(R.id.newCustomerPasswordField);

        // CHECK IF ALL FIELDS ARE FILLED IN
        Toast toastNotif;
        boolean idFieldEmpty = !(name.getText().toString().trim().length() > 0);
        boolean ageFieldEmpty = !(age.getText().toString().trim().length() > 0);
        boolean addressFieldEmpty = !(address.getText().toString().trim().length() > 0);
        boolean passwordFieldEmpty = !(password.getText().toString().trim().length() > 0);

        if (idFieldEmpty || ageFieldEmpty || addressFieldEmpty || passwordFieldEmpty ) {
            toastNotif = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }

        String getname = name.getText().toString();
        String getage = age.getText().toString();
        String getaddress = address.getText().toString();
        String getpassword = password.getText().toString();

        // intage to parseInt
        int intage = Integer.parseInt(getage);

        // craete DatabaseSelectHelper
        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(TTnewcustomer.this);
        // get the Intent
        Intent intent = getIntent();

        // get list of all roles in db
        List<Integer> customerRoleIds = new ArrayList<Integer>();
        customerRoleIds = selectHelper.getRolesList();

        // find the roleTypeId of customer role
        // set to -1 for default value
        int customerRoleId = -1;
        for (int i = 0; i < customerRoleIds.size(); i++) {
            int tempId = customerRoleIds.get(i);
            // get the role for this roleId
            String tempRole = selectHelper.getRole(tempId);
            // check if it's a customer role
            if (tempRole.equalsIgnoreCase("CUSTOMER")) {
                customerRoleId = tempId;
                break;
            }
        }
        // create the new user
        DatabaseInsertHelper dbi = new DatabaseInsertHelper(this);
        int newCustId = (int) dbi.insertNewUser(getname, intage, getaddress, customerRoleId,
                getpassword);
        // pass its userId back to the previous activity and set it as the current new user
        intent.putExtra("newCustId", newCustId);
        // close db
        selectHelper.close();
        // close the database
        dbi.close();
        // set the result to OK
        setResult(Activity.RESULT_OK, intent);
        finish();
    }
}
