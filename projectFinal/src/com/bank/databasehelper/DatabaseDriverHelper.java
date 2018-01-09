package com.bank.databasehelper;

import com.bank.database.DatabaseDriver;
import com.bank.exceptions.ConnectionFailedException;

import java.sql.Connection;
import java.sql.SQLException;

public class DatabaseDriverHelper extends DatabaseDriver {
  protected static Connection connectOrCreateDataBase() {
    return DatabaseDriver.connectOrCreateDataBase();
  }
  
  /**
   * Tries to close the given connection.
   * @param connection the connection to close
   * @throws ConnectionFailedException if something is wrong with the connection
   */
  protected static void closeConnection(Connection connection) throws ConnectionFailedException {
    try {
      if (!connection.isClosed()) { // If connection is not closed
        connection.close();
      }
    } catch (SQLException e) {
      // Something went wrong with the connection
      throw new ConnectionFailedException();
    }
  }
}
