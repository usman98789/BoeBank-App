package com.bank.database;

import android.content.Context;

import java.math.BigDecimal;

/** This class allows access to the update methods in DatabaseDriverA.java
 * Created by jasoncheung on 2017-07-29.
 */

public class DatabaseUpdateHelper extends DatabaseDriverA {

    /**
     * Call UpdateHelper with context
     * @param context
     */
    public DatabaseUpdateHelper(Context context) {
        super(context);
    }

    @Override
    public boolean updateRoleName(String name, int id) {
        return super.updateRoleName(name, id);
    }

    @Override
    public boolean updateUserName(String name, int id) {
        return super.updateUserName(name, id);
    }

    @Override
    public boolean updateUserAge(int age, int id) {
        return super.updateUserAge(age, id);
    }

    @Override
    public boolean updateUserRole(int roleId, int id) {
        return super.updateUserRole(roleId, id);
    }

    @Override
    public boolean updateUserAddress(String address, int id) {
        return super.updateUserAddress(address, id);
    }

    @Override
    public boolean updateAccountName(String name, int id) {
        return super.updateAccountName(name, id);
    }

    @Override
    public boolean updateAccountBalance(BigDecimal balance, int id) {
        return super.updateAccountBalance(balance, id);
    }

    @Override
    public boolean updateAccountType(int typeId, int id) {
        return super.updateAccountType(typeId, id);
    }

    @Override
    public boolean updateAccountTypeName(String name, int id) {
        return  super.updateAccountTypeName(name, id);
    }

    @Override
    public boolean updateAccountTypeInterestRate(BigDecimal interestRate, int id) {
        return super.updateAccountTypeInterestRate(interestRate, id);
    }

    @Override
    public boolean updateUserPassword(String password, int id) {
        return super.updateUserPassword(password, id);
    }

    @Override
    public boolean updateUserMessageState(int id) {
        return super.updateUserMessageState(id);
    }

}
