package com.activities;

import android.app.Activity;
import android.content.Intent;
import java.math.BigDecimal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.database.DatabaseSelectHelper;
import com.bank.users.Customer;

import java.util.List;

public class Tellerdeposit extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tellerdeposit);
    }

    /**
     * Deposits specified money into a desired account
     * @param v View to be used
     * @throws com.bank.exceptions.IllegalObjectTypeException exception thrown for illegal obj type
     * @throws com.bank.exceptions.ItemNotFoundException exception thrown for object not found
     * @throws com.bank.exceptions.IllegalInputParameterException exception thrown for illegal param
     * @throws com.bank.exceptions.IllegalAmountException exception thrown for illegal amount
     * @throws com.bank.exceptions.NotAuthenticatedException exception thrown if not authenticated
     */
    public void deposit(View v)
            throws com.bank.exceptions.IllegalObjectTypeException,
            com.bank.exceptions.ItemNotFoundException,
            com.bank.exceptions.IllegalInputParameterException,
            com.bank.exceptions.IllegalAmountException,
            com.bank.exceptions.NotAuthenticatedException {

        Intent intent = getIntent();
        Customer customer = (Customer) intent.getSerializableExtra("currCustomer");

        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(Tellerdeposit.this);

        EditText id = (EditText)findViewById(R.id.accId);
        EditText bal = (EditText)findViewById(R.id.amount_deposit);
        // CHECK THAT ALL FIELDS ARE FILLED IN
        Toast toastNotif;
        boolean idFieldEmpty = !(id.getText().toString().trim().length() > 0);
        boolean balFieldEmpty = !(bal.getText().toString().trim().length() > 0);
        if (idFieldEmpty || balFieldEmpty) {
            toastNotif = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }

        // get id
        int accountId = Integer.parseInt(id.getText().toString());

        // get balance to deposit
        float tempbal = Float.parseFloat(bal.getText().toString());
        BigDecimal balance = new BigDecimal(tempbal);
        balance = balance.setScale(2, java.math.BigDecimal.ROUND_HALF_EVEN);

        // check if account belongs to this customer
        List<Integer> customerAccountIds = selectHelper.getAccountIdsList(customer.getId());
        if (customerAccountIds.contains(accountId)) {
            // send the account id back
            intent.putExtra("id", accountId);
            intent.putExtra("depositbalance", balance);
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
