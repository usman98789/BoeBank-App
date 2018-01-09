package com.bank.functions;

import android.content.Context;

import com.bank.exceptions.ConnectionFailedException;
import com.bank.exceptions.IllegalAmountException;
import com.bank.exceptions.IllegalInputParameterException;
import com.bank.exceptions.IllegalObjectTypeException;
import com.bank.exceptions.ItemNotFoundException;
import com.bank.serializer.SerializedDatabase;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SerializerFunctions {
  
  public static void serializeDatabase (Context context)
      throws ConnectionFailedException, ItemNotFoundException {
    // initializes the object representing the db
    SerializedDatabase serialDB = new SerializedDatabase(context);
    
    // we can change output dir later
    try {
      FileOutputStream fileOut = new FileOutputStream("c:/Users/richard/Desktop/bankDB.ser");
      ObjectOutputStream objectOut = new ObjectOutputStream(fileOut);
      // writes it to the object output stream and close the stream 
      objectOut.writeObject(serialDB);
      objectOut.close();
      fileOut.close();
      // prompt to tell user where the serialized object has been stored
      System.out.println("Your serialized DB object has been stored on the desktop");
      
    } catch (IOException i) {
      i.printStackTrace();
    }
  }

  public static void deserializeDatabase (Context context) throws ConnectionFailedException,
          ClassNotFoundException, IllegalObjectTypeException, IllegalInputParameterException,
  IllegalAmountException, ItemNotFoundException {
    try {
      FileInputStream fileIn = new FileInputStream("c:/Users/richard/Desktop/bankDB.ser");
      ObjectInputStream objectIn = new ObjectInputStream(fileIn);
      // stores the serialized database object
      SerializedDatabase serializedDB = (SerializedDatabase) objectIn.readObject();
      // serialize the database
      serializedDB.deserialize(context);
      // close the streams
      objectIn.close();
      fileIn.close();
    } catch (IOException i) {
      i.printStackTrace();
    }
    
  }


  
}
