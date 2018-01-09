package com.activities;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;

import com.bank.functions.AdminFunctions;
import com.bank.functions.SerializerFunctions;
import com.bank.generics.User;

import com.bank.database.DatabaseInsertHelper;
import com.bank.database.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.RolesMap;
import com.bank.serializer.SerializedDatabase;
import com.bank.users.Admin;

import java.util.ArrayList;
import java.util.List;


public class AdminMenuActivity extends AppCompatActivity
    implements NavigationView.OnNavigationItemSelectedListener {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_admin_menu);
    Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
    setSupportActionBar(toolbar);

    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    try {
      // gets the current role Id of customers
      int typeOfUser = RolesMap.getRoleId("CUSTOMER", this);
      listUsers(typeOfUser);
    } catch (Exception e) {
      prompt.setText("Sorry, but there was an error listing all the users");
    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
        this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
    drawer.setDrawerListener(toggle);
    toggle.syncState();

    NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
    navigationView.setNavigationItemSelectedListener(this);

  }

  @Override
  public void onBackPressed() {
    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    if (drawer.isDrawerOpen(GravityCompat.START)) {
      drawer.closeDrawer(GravityCompat.START);
    } else {
      super.onBackPressed();
    }
  }

  @Override
  public boolean onCreateOptionsMenu(Menu menu) {
    // Inflate the menu; this adds items to the action bar if it is present.
    getMenuInflater().inflate(R.menu.admin_menu, menu);
    return true;
  }



  @Override
  public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    //noinspection SimplifiableIfStatement
    if (id == R.id.action_settings) {
      return true;
    }

    return super.onOptionsItemSelected(item);
  }

  @Override
  public boolean onNavigationItemSelected(MenuItem item) {
    // Creates a text view object to allow us to prompt users
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    // Handle navigation view item clicks here.
    int id = item.getItemId();

    try {
      int typeOfUser = -1;

      if (id == R.id.view_all_customers) {
        // gets the current role Id of customers
        typeOfUser = RolesMap.getRoleId("CUSTOMER", this);
      } else if (id == R.id.view_all_admins) {
        // gets the current role Id of admins
        typeOfUser = RolesMap.getRoleId("ADMIN", this);
      } else if (id == R.id.view_all_tellers) {
        // gets the current role Id of tellers
        typeOfUser = RolesMap.getRoleId("TELLER", this);
      } else if (id == R.id.nav_manage) {

      } else if (id == R.id.nav_share) {

      } else if (id == R.id.nav_send) {

      } else {
        // gets the curren role Id of customers
        typeOfUser = RolesMap.getRoleId("CUSTOMER", this);
      }

      listUsers(typeOfUser);

    } catch (Exception e){

    }

    DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
    drawer.closeDrawer(GravityCompat.START);
    return true;
  }

  /**
   * Creates the dialog to create new admin or teller user
   * @param view current view
   */
  public void createDialog (View view)  {
    // creates a layout inflator to inflate layout and create a view from it
    LayoutInflater layoutInflator = getLayoutInflater();
    final View dialogView = layoutInflator.inflate(R.layout.create_user_dialog, null);

    final TextView prompt = (TextView) dialogView.findViewById(R.id.new_user_warning);
    // create objects to gather info from the edit text fields
    final EditText newNameField = (EditText) dialogView.findViewById(R.id.new_user_name);
    final EditText newAgeField = (EditText) dialogView.findViewById(R.id.new_user_age);
    final EditText newAddressField = (EditText) dialogView.findViewById(R.id.new_user_address);
    final EditText newPasswordField = (EditText) dialogView.findViewById(R.id.new_user_password);

    // gets the radio buttons to check what user type should be created
    final RadioButton choiceAdminButton = (RadioButton) dialogView.findViewById(R.id.radio_admin);
    final RadioButton choiceTellerButton = (RadioButton) dialogView.findViewById(R.id.radio_teller);

    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    // alters the alert dialogue
    alertBuilder.setTitle("MAKE NEW USER");
    alertBuilder.setView(dialogView);
    alertBuilder.setPositiveButton("CREATE",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {

            try {
              // collects the user input from the fields
              String newName = newNameField.getText().toString();
              String newAge = newAgeField.getText().toString();
              String newAddress = newAddressField.getText().toString();
              String newPassword = newPasswordField.getText().toString();

              /// instantiates an object to allow us to insert into the database
              DatabaseInsertHelper inserter = new DatabaseInsertHelper(AdminMenuActivity.this);

              int ntAge = Integer.parseInt(newAge);
              long userId = -1;

              // checks what type of user needs to be inserted
              boolean chooseAdmin = choiceAdminButton.isChecked();
              boolean chooseTeller = choiceTellerButton.isChecked();

              // checks if all the fields have been properly filled in
              boolean allCompleted = (!newName.matches("") && !newAge.matches("") &&
                  !newAddress.matches("") && newPassword.matches(""));
              // if neither are checked
              if (!chooseAdmin && !chooseTeller) {
                prompt.setText(R.string.lacking_information);
              }
              else if (chooseAdmin && chooseTeller) {
                prompt.setText(R.string.lacking_information);
              }
              else if (chooseAdmin) {
                // create a new admin
                int adminRoleId = RolesMap.getRoleId("ADMIN", AdminMenuActivity.this);
                userId = inserter.insertNewUser(newName, ntAge, newAddress, adminRoleId, newPassword);
              }
              else if (chooseTeller) {
                // create a new teller
                int tellerRoleId = RolesMap.getRoleId("TELLER", AdminMenuActivity.this);
                userId = inserter.insertNewUser(newName, ntAge, newAddress, tellerRoleId, newPassword);
              }

            } catch (ItemNotFoundException e) {

            } catch (ConnectionFailedException e) {

            }
          }
        }
    );
    alertBuilder.setNegativeButton("CANCEL",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
          }
        }
    );
    alertBuilder.show();
  }

  /**
   * displays all users of a given role type
   * @param userRole the type of user we wwant to see
   * @param view the current view of the method calling it
   */
  private void populateList(String userRole, View view) {
    // Creates a text view object to allow us to prompt users
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    try {
      // gets the curren role Id of customers
      int typeOfUser = RolesMap.getRoleId(userRole, this);
      prompt.setText(typeOfUser + "");
      // initializes the list of users
      listUsers(typeOfUser);
    } catch (Exception e) {
      prompt.setText("Sorry, but there was an error listing these users");
    }
  }

  public void listUsers (int typeOfUser) throws ItemNotFoundException, ConnectionFailedException{
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    // gets a list of users from the db
    ArrayList <User> usersFromDB = (ArrayList) AdminFunctions.getAllUsersOfType(typeOfUser, this);

    ArrayList <User> newList = new ArrayList<>();

    String allUsers = "";

    for (User currentUser: usersFromDB) {
      if (currentUser.getRoleId() == typeOfUser) {
        // add all the users from the database to the display string
        allUsers += (" USER #" + currentUser.getId() +
        "\n------- NAME: " + currentUser.getName() +
        "\n------- AGE: " + currentUser.getAge() +
        "\n------- ADDRESS: " +  currentUser.getAddress() + "\n\n\n");
      }
    }
    // sets the text to show the users
    prompt.setText(allUsers);
  }

  /**
   * Displays the current admins in the system
   * @param view current view of the method calling it
   */
  public void displayCurrentAdmins (View view) {
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    // gets the current role Id of customers
    try {
      int typeOfUser = RolesMap.getRoleId("ADMIN", this);
      listUsers(typeOfUser);
    } catch (ConnectionFailedException | ItemNotFoundException e) {
      prompt.setText(R.string.connection_failed);
    }
  }

  /**
   * Displays the current tellers in the system
   * @param view current view of the method calling it
   */
  public void displayCurrentTellers (View view) {
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    // gets the current role Id of customers
    try {
      int typeOfUser = RolesMap.getRoleId("TELLER", this);
      listUsers(typeOfUser);
    } catch (ConnectionFailedException | ItemNotFoundException e) {
      prompt.setText(R.string.connection_failed);
    }
  }

  /**
   * Displays the current customers in the system
   * @param view current view of the method calling it
   */
  public void displayCurrentCustomers (View view) {
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    // gets the current role Id of customers
    try {
      int typeOfUser = RolesMap.getRoleId("CUSTOMER", this);
      listUsers(typeOfUser);
    } catch (ConnectionFailedException | ItemNotFoundException e) {
      prompt.setText(R.string.connection_failed);
    }
  }

  /**
   * Displays the total balance of all users in the system
   * @param view current view of the method calling it
   */
  public void viewTotalBalance (View view) {
    TextView prompt = (TextView) findViewById(R.id.admin_menu);

    // gets the current role Id of customers
    try {
      int typeOfUser = RolesMap.getRoleId("CUSTOMER", this);
      ArrayList <User> users = (ArrayList) AdminFunctions.getAllUsersOfType(typeOfUser, this);
      int totalBalance = 0;
      for (User currentCustomer: users) {
        if (currentCustomer.getRoleId() == typeOfUser) {
          totalBalance += AdminFunctions.getAllBalance(currentCustomer.getId(), this);
        }
      }
      prompt.setText("The total balance of all customers is: " + totalBalance);
    } catch (ConnectionFailedException | ItemNotFoundException e) {
      prompt.setText(R.string.connection_failed);
    }
  }

  /**
   * Displays all the current customers in the system
   * @param view current view of the method calling it
   */
  public void promoteTeller (View view)  {
    // creates a layout inflator to inflate layout and create a view from it
    LayoutInflater layoutInflator = getLayoutInflater();
    final View dialogView = layoutInflator.inflate(R.layout.single_input, null);

    // create objects to gather info from the edit text fields
    final EditText singleInput = (EditText) dialogView.findViewById(R.id.single_input);
    final TextView prompt = (TextView) findViewById(R.id.admin_menu);

    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    // alters the alert dialogue
    alertBuilder.setTitle("PROMOTE THIS TELLER");
    alertBuilder.setView(dialogView);
    alertBuilder.setPositiveButton("PROMOTE",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
            try {
              // collects the user input from the fields
              String newName = singleInput.getText().toString();
              int tellerId = Integer.parseInt(singleInput.getText().toString());
              AdminFunctions.promoteTeller(tellerId, getApplicationContext());
            } catch (ItemNotFoundException e) {
              prompt.setText(R.string.user_not_found);
            } catch (ConnectionFailedException e) {
              prompt.setText(R.string.connection_failed);
            }
          }
        }
    );
    alertBuilder.setNegativeButton("CANCEL",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
          }
        }
    );
    alertBuilder.show();
  }

  /**
   * displays the balance of a single account in the system
   * @param view
   */
  public void viewAccountBalance (View view)  {
    // creates a layout inflator to inflate layout and create a view from it
    LayoutInflater layoutInflator = getLayoutInflater();
    final View dialogView = layoutInflator.inflate(R.layout.single_input, null);

    // create objects to gather info from the edit text fields
    final EditText singleInput = (EditText) dialogView.findViewById(R.id.single_input);
    singleInput.setHint("Account ID");
    final TextView prompt = (TextView) findViewById(R.id.admin_menu);

    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    // alters the alert dialogue
    alertBuilder.setTitle("VIEW");
    alertBuilder.setView(dialogView);
    alertBuilder.setPositiveButton("VIEW",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
            try {
              // Creates a new database helper object to interact with the database
              DatabaseSelectHelper dbSelect = new DatabaseSelectHelper(getApplicationContext());
              // collects the user input from the fields
              String accountId = singleInput.getText().toString();
              // gets the account balance by parsing user input as int
              int accountBalance = dbSelect.getBalance(Integer.parseInt(accountId)).intValue();
              prompt.setText(accountId + " balance : " + accountBalance + "$");
            }
            catch(Exception e) {
              prompt.setText("There was a problem in retrieving the balance for this account");
            }
          }
        }
    );
    alertBuilder.setNegativeButton("CANCEL",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
          }
        }
    );
    alertBuilder.show();
  }

  /**
   * Displays all the messages in the system
   * @param view current view of the method calling it
   */
  public void viewAllMessages (View view) {
    // Creates a text view object to allow us to prompt users
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    // store all messages of the database in this list
    List<String> allMessagesList = new ArrayList<String>();

    // creates a string to store the message
    String message = " ";
    // value to allow us to iterate through the messages

    try {
      DatabaseSelectHelper db = new DatabaseSelectHelper(this);
      // String to be display messages
      String outputMessages = "";
      // iterate through all users in db
      int userId = 1;
      User tempUser = db.getUser(userId, this);
      while (tempUser != null) {
        outputMessages += (" USER ID : " + userId + " \n");
        List<String> tempMessages = new ArrayList<String>();
        tempMessages = db.getAllMessagesList(userId);
        for (int i = 0; i < tempMessages.size(); i++) {
          allMessagesList.add(tempMessages.get(i));
          // adds the message to the total message
          outputMessages += ("-------- Message ID: " + i +
              "\n " + tempMessages.get(i) +  "\n");
        }
        outputMessages += "\n";
        userId++;
        tempUser = db.getUser(userId, this);
      }
      prompt.setText(outputMessages);
    } catch (Exception e){
      e.printStackTrace();
    }
  }

  /**
   * Leaves a message for a given user
   * @param view current view of the method calling it
   */
  public void leaveMessage (View view) {
    // creates a layout inflator to inflate layout and create a view from it
    LayoutInflater layoutInflator = getLayoutInflater();
    final View dialogView = layoutInflator.inflate(R.layout.double_int, null);

    // create objects to gather info from the edit text fields
    final EditText userInput = (EditText) dialogView.findViewById(R.id.user_id_double);
    final EditText messageInput = (EditText) dialogView.findViewById(R.id.message_double);
    userInput.setHint("user ID");
    messageInput.setHint("message");
    final TextView prompt = (TextView) findViewById(R.id.admin_menu);

    final AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);
    // alters the alert dialogue
    alertBuilder.setTitle("LEAVE MESSAGE");
    alertBuilder.setView(dialogView);
    alertBuilder.setPositiveButton("LEAVE MESSAGE",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
            try {
              // collects the user input from the fields
              int newId = Integer.parseInt(userInput.getText().toString());
              String newMessage = messageInput.getText().toString();
              // calls the admin function to leave a messave
              AdminFunctions.leaveMessage(newId, newMessage, getApplicationContext());
              prompt.setText("Message left for user " + newId);
            } catch (ItemNotFoundException e) {
              prompt.setText("Sorry, but a teller with this id does not exist");
            } catch (Exception e) {
              prompt.setText("Sorry, but there is a problem with your connection");
            }
          }
        }
    );
    alertBuilder.setNegativeButton("CANCEL",
        new DialogInterface.OnClickListener() {
          public void onClick(DialogInterface dialog, int selection) {
          }
        }
    );
    alertBuilder.show();
  }

  /**
   * Serializes the database and exports it
   * @param view current view of the method calling it
   */
  public void serializeDB (View view) {
    // Creates a text view object to allow us to prompt users
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    try {
      SerializerFunctions.serializeDatabase(this);
    } catch (Exception e) {
      prompt.setText("Sorry, but there was a problem serializing the database");
    }
  }

  /**
   * Deserializes a fiven serialized database
   * @param view current view of the method calling it
   */
  public void deserialize (View view) {
    // Creates a text view object to allow us to prompt users
    TextView prompt = (TextView) findViewById(R.id.admin_menu);
    try {
      SerializerFunctions.deserializeDatabase(this);
    } catch (Exception e) {
      prompt.setText("Sorry, but there was a problem deserializing the database");
    }
  }

}
