package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.database.DatabaseSelectHelper;
import com.bank.users.Customer;

import java.util.List;

public class giveInterest extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_give_interest);

    }

    /**
     * Give Interest
     * @param v View object used
     * @throws com.bank.exceptions.IllegalObjectTypeException exception thrown for illegal obj type
     * @throws com.bank.exceptions.ItemNotFoundException exception thrown for object not found
     * @throws com.bank.exceptions.IllegalInputParameterException exception thrown for illegal param
     * @throws com.bank.exceptions.IllegalAmountException exception thrown for illegal amount
     * @throws com.bank.exceptions.NotAuthenticatedException exception thrown if not authenticated
     */
    public void giveInterest(View v)
            throws com.bank.exceptions.IllegalObjectTypeException,
            com.bank.exceptions.ItemNotFoundException,
            com.bank.exceptions.IllegalInputParameterException,
            com.bank.exceptions.IllegalAmountException,
            com.bank.exceptions.NotAuthenticatedException {

        // get the intent
        Intent intent = getIntent();
        // create customer and get seraribale current customer
        Customer customer = (Customer) intent.getSerializableExtra("currCustomer");
        // create database selecteHelper
        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(giveInterest.this);

        // get account Id
        EditText tempId = (EditText)findViewById(R.id.editText4);
        // CHECK IF FIELD IS FILLED IN
        Toast toastNotif;
        boolean tempIdFieldEmpty = !(tempId.getText().toString().trim().length() > 0);
        if (tempIdFieldEmpty) {
            toastNotif = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }
        int accountId = Integer.parseInt(tempId.getText().toString());

        // check if account belongs to this customer
        List<Integer> customerAccountIds = selectHelper.getAccountIdsList(customer.getId());
        if (customerAccountIds.contains(accountId)) {
            // send the account id back
            intent.putExtra("accountId", accountId);
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            // make error toast
            toastNotif = Toast.makeText(this, "This account doesn't belong to the current " +
                    " customer", Toast.LENGTH_SHORT);
            toastNotif.show();
            setResult(Activity.RESULT_CANCELED, intent);
            return;
        }
    }
}
