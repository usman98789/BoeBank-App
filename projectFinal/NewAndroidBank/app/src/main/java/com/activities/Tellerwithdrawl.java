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

public class Tellerwithdrawl extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tellerwithdrawl);
    }

    /**
     * Make a withdrawl
     * @param v View to be used
     * @throws com.bank.exceptions.IllegalObjectTypeException Throw exception if illegalObject
     * @throws com.bank.exceptions.ItemNotFoundException Throw exception if Item not found
     * @throws com.bank.exceptions.IllegalInputParameterException throw exception if illegal param
     * @throws com.bank.exceptions.IllegalAmountException throw exception if illegal amount
     * @throws com.bank.exceptions.NotAuthenticatedException throw exception if not authenticated
     */
    public void makeWithdrawl(View v)
            throws com.bank.exceptions.IllegalObjectTypeException,
            com.bank.exceptions.ItemNotFoundException,
            com.bank.exceptions.IllegalInputParameterException,
            com.bank.exceptions.IllegalAmountException,
            com.bank.exceptions.NotAuthenticatedException {

        // get the Intent and create the customer
        Intent intent = getIntent();
        Customer customer = (Customer) intent.getSerializableExtra("currCustomer");
        // create database selectHelper object
        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(Tellerwithdrawl.this);

        EditText tempId = (EditText)findViewById(R.id.accountId);
        EditText tempWithdraw = (EditText)findViewById(R.id.amount_withdraw);
        // CHECK THAT ALL FIELDS ARE FILLED IN
        Toast toastNotif;
        boolean idFieldEmpty = !(tempId.getText().toString().trim().length() > 0);
        boolean withdrawFieldEmpty = !(tempWithdraw.getText().toString().trim().length() > 0);
        if (idFieldEmpty || withdrawFieldEmpty) {
            toastNotif = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }

        // get account Id
        int accountId = Integer.parseInt(tempId.getText().toString());

        // get withdrawl amount
        float tempwithdrawlamount = Float.parseFloat(tempWithdraw.getText().toString());


        BigDecimal withdrawlamount = new BigDecimal(tempwithdrawlamount);
        withdrawlamount = withdrawlamount.setScale(2, java.math.BigDecimal.ROUND_HALF_EVEN);

        // check if account belongs to this customer
        List<Integer> customerAccountIds = selectHelper.getAccountIdsList(customer.getId());
        if (customerAccountIds.contains(accountId)) {
            // send the account id and amount back
            intent.putExtra("Accid", accountId);
            intent.putExtra("withdrawlAmount", withdrawlamount);
            // set result Activity to OK
            setResult(Activity.RESULT_OK, intent);
            finish();
        } else {
            // make error toast
            toastNotif = Toast.makeText(this, "This account doesn't belong to the current " +
                    " customer", Toast.LENGTH_SHORT);
            toastNotif.show();
            // set result to cancel
            setResult(Activity.RESULT_CANCELED, intent);
            return;
        }

    }
}
