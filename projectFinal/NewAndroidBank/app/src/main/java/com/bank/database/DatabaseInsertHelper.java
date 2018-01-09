package com.bank.database;

import android.content.Context;

import java.math.BigDecimal;

/** This file allows access to the insert methods in DatabaseDriverA.java
 *
 * Created by jasoncheung on 2017-07-29.
 */

public class DatabaseInsertHelper extends DatabaseDriverA {

    /**
     * Call Insert Helper with context
     * @param context
     */
    public DatabaseInsertHelper(Context context) {
        super(context);
    }

    @Override
    public long insertRole(String role) {
        return super.insertRole(role);
    }

    @Override
    public long insertNewUser(String name, int age, String address, int roleId, String password) {
        return super.insertNewUser(name, age, address, roleId, password);
    }

    @Override
    public long insertAccountType(String name, BigDecimal interestRate) {
        return super.insertAccountType(name, interestRate);
    }

    @Override
    public long insertAccount(String name, BigDecimal balance, int typeId) {
        return super.insertAccount(name, balance, typeId);
    }

    @Override
    public long insertUserAccount(int userId, int accountId) {
        return super.insertUserAccount(userId, accountId);
    }

    @Override
    public long insertMessage(int userId, String message) {
        return super.insertMessage(userId, message);
    }

}
