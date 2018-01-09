package com.bank.generics;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.Roles;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class RolesMap {
  //creates an enum map for for the roles
  static Map<Roles, Integer> rolesMap = new EnumMap<Roles, Integer>(Roles.class);
   
  /**
   * 
   * @param roleName
   * @return
   */
  public static int getRoleId(String roleName) {
    // creates a variable to store the role ID
    int roleId = -1;
    if (roleName.equals("ADMIN")) { 
      roleId = rolesMap.get(Roles.ADMIN);
    } else if (roleName.equals("TELLER")) { 
      roleId = rolesMap.get(Roles.TELLER);
    } else if (roleName.equals("CUSTOMER")) { 
      roleId = rolesMap.get(Roles.CUSTOMER);
    }
    return roleId;
  }
  
  /**
   * Allows the user to update the Role map whenever there is a change to the database.
   * @throws ConnectionFailedException if the connection to the database fails
   * @throws ItemNotFoundException 
   */
  public static void updateMap() throws ConnectionFailedException, ItemNotFoundException {
    // calls the getRoles method to get all the role ids from the database
    List<Integer> rolesList = DatabaseSelectHelper.getRoles();
    
    // iterates through each value from the database
    for (int position = 0; position < rolesList.size(); position++) {
      // stores current values for comparison and assignment
      int currentId = rolesList.get(position);
      String roleName = DatabaseSelectHelper.getRole(currentId);
      
      // updates the values within the rolesList with database values
      if (roleName.equals("ADMIN")) {
        rolesMap.put(Roles.ADMIN, rolesList.get(position));
      } else if (roleName.equals("TELLER")) {
        rolesMap.put(Roles.TELLER, rolesList.get(position));
      } else if (roleName.equals("CUSTOMER")) {
        rolesMap.put(Roles.CUSTOMER, rolesList.get(position));
      }    
    }
  }
  
  /*
   * 
   */
  public static List<Integer> getRolesMappings()
      throws ConnectionFailedException, ItemNotFoundException {
    // checks if the map is null
    if (rolesMap.get(0) == null) {
      updateMap();
    }
    // creates a new list to store all the roles
    List<Integer> rolesList = new ArrayList<Integer>();
    rolesList.add(rolesMap.get(Roles.ADMIN));
    rolesList.add(rolesMap.get(Roles.TELLER));
    rolesList.add(rolesMap.get(Roles.CUSTOMER));
    // returns the list of roles
    return rolesList;
  }

}
