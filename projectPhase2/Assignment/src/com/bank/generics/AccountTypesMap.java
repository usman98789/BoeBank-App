package com.bank.generics;

import com.bank.databasehelper.DatabaseSelectHelper;
import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.generics.AccountTypes;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class AccountTypesMap {
  //creates an enum map for for the roles
  static Map<AccountTypes, Integer> accountTypesMap = 
         new EnumMap<AccountTypes, Integer>(AccountTypes.class);

  /**
   * Allows the user to update the account map whenever there is a change to the database.
   * @throws ConnectionFailedException when the connection to the database fails
   * @throws ItemNotFoundException 
   */
  public static void updateMap() throws ConnectionFailedException, ItemNotFoundException {
    // calls the getRoles method to get all the role ids from the database
    List<Integer> accountTypesList = DatabaseSelectHelper.getAccountTypesIds();
   
    // updates the values within the rolesList with database values
    for (int position = 0; position < accountTypesList.size(); position++) {
     
      // stores current values for comparison and assignment
      int currentId = accountTypesList.get(position);
      String accountTypeName = DatabaseSelectHelper.getAccountTypeName(currentId);
      // updates the values within the accountTypesMap with database values
      
      if (accountTypeName.equals("CHEQUING")) {
        accountTypesMap.put(AccountTypes.CHEQUING, currentId);
      } else if (accountTypeName.equals("SAVING")) {
        accountTypesMap.put(AccountTypes.SAVING, currentId);
      } else if (accountTypeName.equals("TFSA")) {
        accountTypesMap.put(AccountTypes.TFSA, currentId);
      } else if (accountTypeName.equals("RESTRICTEDSAVING")) {
        accountTypesMap.put(AccountTypes.RESTRICTEDSAVING, currentId);
      } else if (accountTypeName.equals("LOAN")) {
    	accountTypesMap.put(AccountTypes.LOAN, currentId);
      }
    }
    
  }
  

  public static List<Integer> getAccountTypesMappings()
      throws ConnectionFailedException, ItemNotFoundException {
    // checks if the map is null
    if (accountTypesMap.get(0) == null) {
      updateMap();
    } 
    // creates a new list to store all the roles
    List<Integer> rolesList = new ArrayList<Integer>();
    rolesList.add(accountTypesMap.get(AccountTypes.CHEQUING));
    rolesList.add(accountTypesMap.get(AccountTypes.SAVING));
    rolesList.add(accountTypesMap.get(AccountTypes.TFSA));
    rolesList.add(accountTypesMap.get(AccountTypes.RESTRICTEDSAVING));
    rolesList.add(accountTypesMap.get(AccountTypes.LOAN));
    // returns the list of roles
    return rolesList;
  }
  
  /**
   * 
   * @param accountType
   * @return
   */
  public static int getAccountTypeID (String accountType) {
    // creates a variable to store the account type ID
    int roleId = -1;
    if (accountType.equals("CHEQUING")) { 
      roleId = accountTypesMap.get(AccountTypes.CHEQUING);
    } else if (accountType.equals("SAVING")) { 
      roleId = accountTypesMap.get(AccountTypes.SAVING);
    } else if (accountType.equals("TFSA")) { 
      roleId = accountTypesMap.get(AccountTypes.TFSA);
    } else if (accountType.equals("RESTRICTEDSAVING")) { 
      roleId = accountTypesMap.get(AccountTypes.RESTRICTEDSAVING);
    } else if (accountType.equals("LOAN")){
      roleId = accountTypesMap.get(AccountTypes.LOAN);
    }
    return roleId;    
  }
  
  
  
}
