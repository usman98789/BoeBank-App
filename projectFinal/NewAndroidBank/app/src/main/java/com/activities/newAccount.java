package com.activities;

import android.app.Activity;
import android.content.Intent;
import java.math.BigDecimal;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.bank.database.DatabaseSelectHelper;

import java.util.ArrayList;
import java.util.List;

public class newAccount extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_account);
    }

    /**
     * Get the account type ID
     * @param accName name of account who's type ID to is be found
     * @return return the ID for the account type
     * @throws com.bank.exceptions.ItemNotFoundException throws exception if item not found
     */
    public int getTypeId(String accName)
        throws com.bank.exceptions.ItemNotFoundException {

        // get account list
        List<Integer> accountTypeIds = new ArrayList<Integer>();
        int accountTypeId = 0;

        // create database selectHelper
        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(newAccount.this);
        // get list of roleTypeIds from db
        accountTypeIds = selectHelper.getAccountTypesIdsList();
        // display all account types in database
        for (int i = 0; i < accountTypeIds.size(); i++) {
            String tempAccountName = selectHelper.getAccountTypeName(accountTypeIds.get(i));
            // if account Name is what given
            if (tempAccountName.equalsIgnoreCase(accName)){
                accountTypeId = accountTypeIds.get(i);
                break;
            }
        }
        // return accountTypeId
        return  accountTypeId;
    }

    /**
     * create new Account
     * @param v view to be used
     * @throws com.bank.exceptions.IllegalObjectTypeException exception thrown for illegal obj type
     * @throws com.bank.exceptions.ItemNotFoundException exception thrown for object not found
     * @throws com.bank.exceptions.IllegalInputParameterException exception thrown for illegal param
     * @throws com.bank.exceptions.IllegalAmountException exception thrown for illegal amount
     * @throws com.bank.exceptions.NotAuthenticatedException exception thrown if not authenticated
     */
    public void newAccount(View v)
            throws com.bank.exceptions.IllegalObjectTypeException,
            com.bank.exceptions.ItemNotFoundException,
            com.bank.exceptions.IllegalInputParameterException,
            com.bank.exceptions.IllegalAmountException,
            com.bank.exceptions.NotAuthenticatedException{

        // create database selectHelper
        DatabaseSelectHelper selectHelper = new DatabaseSelectHelper(newAccount.this);
        // get the Intent
        Intent intent = getIntent();

        // get name and balance
        EditText name = (EditText)findViewById(R.id.accountNameField);
        EditText bal = (EditText)findViewById(R.id.editText9);
        String accName = null;
        if (name.getText().toString().trim().length() > 0) {
            accName = name.getText().toString();
        }

        // initialize vars for the balance
        BigDecimal accBalance = new BigDecimal(-1);
        float balance = -1;
        if (bal.getText().toString().trim().length() > 0) {
            balance = Float.parseFloat(bal.getText().toString());
            accBalance = new BigDecimal(balance);
            accBalance = accBalance.setScale(2, BigDecimal.ROUND_HALF_EVEN);
        }

        // get the radio buttons by their id
        RadioButton cheq = (RadioButton) findViewById(R.id.cheqBtn);
        RadioButton save = (RadioButton) findViewById(R.id.savingsBtn);
        RadioButton Rsave = (RadioButton) findViewById(R.id.restrictedSavingsBtn);
        RadioButton tfsa = (RadioButton) findViewById(R.id.tfsaBtn);
        RadioButton loan = (RadioButton) findViewById(R.id.loanBtn);

        // CHECK IF ALL FIELDS FILLED IN
        Toast toastNotif;
        boolean nameFieldEmpty = !(name.getText().toString().trim().length() > 0);
        boolean balanceFieldEmpty = !(bal.getText().toString().trim().length() > 0);
        boolean noOptionSelection = !(cheq.isChecked() || save.isChecked() || Rsave.isChecked() ||
                tfsa.isChecked() || loan.isChecked());

        // check if the fields are empty
        if (nameFieldEmpty || balanceFieldEmpty || noOptionSelection) {
            toastNotif = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }

        int accountTypeId = 0;

        // Check which account type is chosen and call according method
        if (cheq.isChecked()) {
            accountTypeId = getTypeId("CHEQUING");
            Log.d("I", "ACCOUNT TYPE " + accountTypeId);

        } else if (save.isChecked()) {
            accountTypeId = getTypeId("SAVING");
            Log.d("I", "ACCOUNT TYPE " + accountTypeId);

        } else if (Rsave.isChecked()) {
            accountTypeId = getTypeId("RESTRICTEDSAVINGACCOUNT");
            Log.d("I", "ACCOUNT TYPE " + accountTypeId);

        } else if (tfsa.isChecked()) {
            accountTypeId = getTypeId("TFSA");
            Log.d("I", "ACCOUNT TYPE " + accountTypeId);

        } else if (loan.isChecked()) {
            accountTypeId = getTypeId("LOAN");
            Log.d("I", "ACCOUNT TYPE " + accountTypeId);

        }
        DatabaseSelectHelper db = new DatabaseSelectHelper(this);
        Log.d("I", "ACCOUNT TYPE NAME " + db.getAccountTypeName(accountTypeId));

        // make that particular account
        intent.putExtra("accName", accName);
        intent.putExtra("accBalance", accBalance);
        intent.putExtra("accTypeId", accountTypeId);
        // mark intent to be ok
        setResult(Activity.RESULT_OK, intent);
        // return to previous screen
        finish();
    }
}
