package com.activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.Account;
import com.bank.users.Customer;

import java.util.ArrayList;
import java.util.List;

public class CustomerAccountsInfo extends AppCompatActivity {

    TextView prompt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_customer_accounts_info);

        // get info passed via intent
        Intent i = getIntent();
        Customer currCustomer = (Customer) i.getSerializableExtra("currCustomer");
        showAccountsInfo(currCustomer);
    }

    /**
     * Show Accounts Info
     * @param currCustomer
     */
    public void showAccountsInfo(Customer currCustomer) {
        // update prompt on screen
         prompt = (TextView) findViewById(R.id.CustomerAccountsInfoHeader);
        DatabaseSelectHelper dbs = new DatabaseSelectHelper(this);

        List<Integer> customerAccountsIds = new ArrayList<Integer>();
        // get a list of accounts IDs belonging to currCustomer
        customerAccountsIds = dbs.getAccountIdsList(currCustomer.getId());
        String msg = currCustomer.getName() + " has " + customerAccountsIds.size() + " account(s).";
        prompt.setText(msg);
        List<String> accountsInfoList = new ArrayList<String>();

        // create array of strings tha have details for each account
        for (int i = 0; i < customerAccountsIds.size(); i++) {
            Account a = dbs.getAccount(customerAccountsIds.get(i));
            String accType = dbs.getAccountTypeName(a.getType());
            // construct message
            String body = "Account Name: " + a.getName()
                    + "\n Account ID: " + a.getId()
                    + "\n Account Balance: " + a.getBalance()
                    + "\n Account Type: " + accType;
            // add message to list
            accountsInfoList.add(body);
        }

        // create the list view to display info
        ListAdapter accountsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, accountsInfoList);
        ListView accountsInfo = (ListView) findViewById(R.id.accountsListView);
        accountsInfo.setAdapter(accountsAdapter);

        // close db
        dbs.close();
    }


}
