package com.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.User;
import com.bank.users.Customer;

public class CreateMessage extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_message);
    }

    /**
     * Get message details
     * @param v the button that call this function
     */
    public void getMessageDetails(View v) {
        // get fields from view
        EditText recipientField = (EditText) findViewById(R.id.recipientIdField);
        EditText messageBody = (EditText) findViewById(R.id.msgField);
        // get data within them
        boolean recipientFieldNotEmpty = recipientField.getText().toString().trim().length() > 0;
        boolean messageBodyNotEmpty = messageBody.getText().toString().trim().length() > 0;
        Toast notifToast;
        if (recipientFieldNotEmpty && messageBodyNotEmpty) {
            // send the message to recipient
            int recipientId = Integer.parseInt(recipientField.getText().toString());
            String msgBody = messageBody.getText().toString();
            DatabaseInsertHelper dbi = new DatabaseInsertHelper(this);
            // check that recipient is a customer
            try {
                DatabaseSelectHelper dbs = new DatabaseSelectHelper(this);
                User tempUser = dbs.getUser(recipientId, this);
                dbs.close();
                if (!(tempUser instanceof Customer)) {
                    // prompt error toast
                    notifToast = Toast.makeText(this, "ID doesn't belong to a customer",
                            Toast.LENGTH_SHORT);
                    notifToast.show();
                    return;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            // insert message into database
            dbi.insertMessage(recipientId, msgBody);
            // close database
            dbi.close();
            // make toast to say message was sent
            notifToast = Toast.makeText(this, "Message sent.", Toast.LENGTH_SHORT);
            notifToast.show();
            finish();
        } else {
            // if one of em are empty
            // prompt error toast
            notifToast = Toast.makeText(this, "One or more fields are empty.",
                    Toast.LENGTH_SHORT);
            notifToast.show();
            return;
        }
    }
}
