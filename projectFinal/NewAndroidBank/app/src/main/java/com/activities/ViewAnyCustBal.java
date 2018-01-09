package com.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bank.functions.AccountFunctions;

public class ViewAnyCustBal extends AppCompatActivity {
    TextView totalCustBalance;
    EditText custId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_any_cust_bal);
    }

    /**
     * Get total balance
     * @param v View to be used
     */
    public void getTotalBalance(View v) {
        // get total Customer Balance and Id
        totalCustBalance = (TextView) findViewById(R.id.totalCustBalance);
        // check that field is filled in
        custId = (EditText) findViewById(R.id.custId);
        Toast toastNotif;
        boolean idFieldEmpty = !(custId.getText().toString().trim().length() > 0);
        if (idFieldEmpty) {
            toastNotif = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            toastNotif.show();
            return;
        }
        // get id for desired user
        String idS = custId.getText().toString();
        int currCustomerId = Integer.parseInt(idS);

        // call accountFunction's viewUserBalance method
        try{
            // call viewUserBalance
            AccountFunctions.viewUserBalance(currCustomerId, ViewAnyCustBal.this);
        } catch (Exception e){
            toastNotif = Toast.makeText(this, "Oops! Something went wrong", Toast.LENGTH_LONG);
            // display the toast
            toastNotif.show();
        }
    }
}
