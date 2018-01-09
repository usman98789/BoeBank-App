package com.activities.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.activities.R;
import com.bank.database.DatabaseSelectHelper;
import com.bank.generics.User;

import java.util.ArrayList;

/**
 * Created by Richard on 7/31/2017.
 */

public class UserAdapters extends ArrayAdapter <User> {
  // holds the data of the user in cache
  private static class UserHolder {
    TextView cardUserId = null;
    TextView cardUserName = null;
  }


  public UserAdapters (Context context, int textID, ArrayList <User> userList, int type) {
    super(context, R.layout.user_card, R.id.card_user_id, userList);
  }

  public View getView (int position, View originalView, ViewGroup parent) {
    // gets the user and corresponding data
    UserHolder currentCache = null;

    User currentUser = getItem(position);

    // inflates the card xml into a view if it doesn't exist yet
    if (originalView == null) {
      originalView = LayoutInflater.from(getContext()).inflate(R.layout.user_card, null);

      // creates a new object to store the data
      currentCache = new UserHolder();


      // gets the textview objects so we can put user data into them
      currentCache.cardUserId = (TextView) originalView.findViewById(R.id.card_user_id);
      currentCache.cardUserName = (TextView) originalView.findViewById(R.id.card_user_name);
      // adds the user specific values to the card
      currentCache.cardUserId.setText(currentUser.getId() + "");
      currentCache.cardUserName.setText(currentUser.getName());
    }

    try {
      // adds the user specific values to the card
      currentCache.cardUserId.setText(currentUser.getId() + "");
      currentCache.cardUserName.setText(currentUser.getName());
    } catch (Exception e) {

    }

    // returns the view for the card
    return originalView;


  }

}
