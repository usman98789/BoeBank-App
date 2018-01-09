package com.example.jasoncheung.newandroidbank;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import com.activities.R;
import android.view.View;
import android.content.Intent;
import android.widget.TextView;

/**
 * Created by usman on 30/07/17.
 */

public class TellerTerminal extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
      // gets the textfield we want to use to tell the user
      TextView prompt = (TextView) findViewById(R.id.loginStatus);
      super.onCreate(savedInstanceState);
      setContentView(R.layout.teller_terminal);

    }

    /**
     * Method for clicking authenticate User
     * @param
     */
    public void onButton1click(){

    }

    /**
     * Method for clicking create new Customer
     */
    public void onButton2click(View v) {
        Intent i =  new Intent(this, TTnewcustomer.class);
        startActivity(i);
    }

    /**
     * Method for clicking create new Account
     */
    public void onButton3click(){

    }

    /**
     * Method for clicking give interest
     */
    public void onButton4click(){

    }

    /**
     * Method for clicking Make a deposit
     */
    public void onButton5click(){

    }

    /**
     * Method for make a withdrawl
     */
    public void onButton6click(){

    }

    //TODO @Richard add and implement remaining methods
}
