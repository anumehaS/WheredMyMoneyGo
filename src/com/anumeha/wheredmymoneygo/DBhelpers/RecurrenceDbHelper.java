package com.anumeha.wheredmymoneygo.DBhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anumeha.wheredmymoneygo.DBHandler;
import com.anumeha.wheredmymoneygo.Recurrence.Recurrence;

public class RecurrenceDbHelper {
	
	 /** Recurrences Table**/
    private static final String TABLE_RECURRENCE = "Recurrences";
    
    private static final String KEY_R_ID = "_id";
    private static final String KEY_R_IS_EI = "expense_or_income";
    private static final String KEY_R_EI_ID = "ei_id"; //  income or expense ID
    private static final String KEY_R_FREQUENCY = "frequency"; //one time - weekly -monthly or annual
    private static final String KEY_R_DATE = "recurrence_date";
    
    private DBHandler dbh;
	
	 public RecurrenceDbHelper(Context context){
			
		dbh = DBHandler.getInstance(context);			
	 }
	 
	 // add a recurrence
	 public void addRec(Recurrence rec) {
		 
		 SQLiteDatabase   db = dbh.getWritableDatabase();
		 
		 ContentValues values = new ContentValues();
		 values.put(KEY_R_IS_EI, rec.getIsEOrI()); // Recurrence is income or expense
		 values.put(KEY_R_EI_ID, rec.getEiId()); // Income or expense's id - that is recurring
		 values.put(KEY_R_FREQUENCY,rec.getFrequency()); // Recurrence frequency
		 values.put(KEY_R_DATE,rec.getDate()); // Recurrence frequency
		 
		 // Inserting Row
		 db.insert(TABLE_RECURRENCE, null, values);
		 db.close(); // Closing database connection
		 
	 }
	 
 public Cursor getAllRecs() {
		 
		 SQLiteDatabase db = dbh.getReadableDatabase();
		 
		 Cursor cursor = db.query(TABLE_RECURRENCE, null, null, null, null , null, null);
		
		 return cursor;
		 
	 }
	 
	 public void updateRec(Recurrence rec, int id) {
	    	SQLiteDatabase db = dbh.getWritableDatabase();
	    	 
	    	 ContentValues values = new ContentValues();
			 values.put(KEY_R_IS_EI, rec.getIsEOrI()); // Recurrence is income or expense
			 values.put(KEY_R_EI_ID, rec.getEiId()); // Income or expense's id - that is recurring
			 values.put(KEY_R_FREQUENCY,rec.getFrequency()); // Recurrence frequency
			 values.put(KEY_R_DATE,rec.getDate()); // Recurrence frequency
	  	   
	  	    // Updating Row
	  	    db.update(TABLE_RECURRENCE, values, KEY_R_ID+"="+ id, null);
	  	    db.close(); // Closing database connection
	 
	   }
	 
	 public void deleteRec(int id)
	 {
	      SQLiteDatabase db = dbh.getWritableDatabase();
	 
	  	    // Deleting Row
	  	    db.delete(TABLE_RECURRENCE, KEY_R_ID+"="+ id, null);
	  	    db.close(); // Closing database connection
	  
    }
 

}
