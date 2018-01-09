package com.bank.bank.functions;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.serializer.SerializedDatabase;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializerFunctions {
  
  /**
   * Serializes the database.
   * @throws ConnectionFailedException if the connection to the database fails
   */
  public static void serializeDatabase() throws ConnectionFailedException {
    // initializes the object representing the db
    SerializedDatabase serialDb = new SerializedDatabase();
    
    // we can change output dir later
    try {
      String filepath = getFilePath();
      FileOutputStream fileOut = new FileOutputStream(filepath);
      ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
      // writes it to the object output stream and close the stream 
      objectOut.writeObject(serialDb);
      objectOut.close();
      fileOut.close();
      // prompt to tell user where the serialized object has been stored
      System.out.println("Your serialized DB object has been stored");
    } catch (IOException i) {
      System.out.println("Please verify that your file path is valid");
    }
  }

  /**
   * Deserializes the database.
   * @throws ConnectionFailedException if the connection to the database fails
   * @throws ClassNotFoundException
   * @throws IllegalObjectTypeException
   * @throws IllegalInputParameterException
   * @throws IllegalAmountException
   * @throws ItemNotFoundException
   */
  public static void deserializeDatabase() throws ConnectionFailedException, 
      ClassNotFoundException, IllegalObjectTypeException, IllegalInputParameterException, 
      IllegalAmountException, ItemNotFoundException {
    try {
      String filepath = getFilePath();
      FileInputStream fileIn = new FileInputStream(filepath);
      ObjectInputStream objectIn = new ObjectInputStream(fileIn);
      // stores the serialized database object
      SerializedDatabase serializedDb = (SerializedDatabase) objectIn.readObject();
      // serialize the database
      serializedDb.deserialize();
      // close the streams
      objectIn.close();
      fileIn.close();
    } catch (IOException i) {
      System.out.println("Please verify that your file path is valid");
    }
    
  }

  /**
   * Gets the file path from the user.
   * @return the file path the user provided
   */
  private static String getFilePath() throws IOException {
    InputStreamReader isr = new InputStreamReader(System.in);
    BufferedReader br = new BufferedReader(isr);
    System.out.println("Input the file path for the serialized database");
    String output = br.readLine();
    return output;
  }
}
