package com.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.User;

import java.util.ArrayList;
import java.util.List;

public class ViewUserInbox extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_curr_customer_messages);

        // create Intent
        Intent i = getIntent();
        // get current User
        User user = (User) i.getSerializableExtra("currUser");

        // get all messages from the db
        List<String> messages = new ArrayList<String>();
        // create database SelectHelper object
        DatabaseSelectHelper dbs = new DatabaseSelectHelper(this);
        // get all the database messages
        messages = dbs.getAllMessagesList(user.getId());

        // create list adapter
        ListAdapter msgsAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, messages);

        // print it out to the screen
        ListView messagesList = (ListView) findViewById(R.id.messagesList);
        // set the messages
        messagesList.setAdapter(msgsAdapter);
        // create msg count and viewHeader
        TextView msgCount = (TextView) findViewById(R.id.messageCount);
        TextView viewHeader = (TextView) findViewById(R.id.viewCustomerMsgsHeader);
        // set the text for viewHeader and msgCount
        viewHeader.setText(user.getName() + getString(R.string.ViewUserInboxSMessages));
        msgCount.setText(messages.size() + getString(R.string.ViewUserINboxMessagesPlural));
    }

}
