package com.smc.wheredmymoneygo.dbhelpers;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.smc.wheredmymoneygo.DBHandler;
import com.smc.wheredmymoneygo.source.Source;

public class SourceDbHelper {
	
	 /** Sources Table **/
    private static final String TABLE_SOURCE	 = "Source";
 

    private static final String KEY_S_NAME = "s_name";
    public static final String KEY_S_COLOR = "s_color";
    
    
    private DBHandler dbh;
	
	 public SourceDbHelper(Context context){
			
		dbh = DBHandler.getInstance(context);			
	 }
	 
	 // add a Source
	 public void addSource(Source source) {
		 
		 SQLiteDatabase   db = dbh.getWritableDatabase();
		 ColorDbHelper colorDb = new ColorDbHelper(db);
		 int color = colorDb.getFirstAvailableColor();
		 
		 ContentValues values = new ContentValues();
		 values.put(KEY_S_NAME, source.getName()); // Source Name	
		 values.put(KEY_S_COLOR, color ); //color for source
		 colorDb.updateColor(color, "true");
		 // Inserting Row
		 db.insert(TABLE_SOURCE, null, values);
		 db.close(); // Closing database connection
		 
	 }
	 
	 public Cursor getAllSources() {
		 
		 SQLiteDatabase db = dbh.getReadableDatabase();
		 
		 Cursor cursor = db.query(TABLE_SOURCE, null, null, null, null , null, null);
		
		 return cursor;
		 
	 }
	 
	 public void updateSource(Source source, String name) {
	    	SQLiteDatabase db = dbh.getWritableDatabase();
	    	 
	    	 ContentValues values = new ContentValues();
			 values.put(KEY_S_NAME, source.getName()); // Source Name
	  	   
	  	    // Updating Row
	  	    db.update(TABLE_SOURCE, values, KEY_S_NAME+"=\""+ name+ "\"", null);
	  	    db.close(); // Closing database connection
	 
	   }
	 
	 public void deleteSource(String name)
	 {
	      SQLiteDatabase db = dbh.getWritableDatabase();
	 
	  	    // Deleting Row
	  	    db.delete(TABLE_SOURCE, KEY_S_NAME + "=\"" + name +"\"", null);
	  	    db.close(); // Closing database connection
	  
    }
	 
	 public Boolean nameExists(String name) {
	        // Select All Query
	        String selectQuery = "SELECT "+KEY_S_NAME+" FROM " + TABLE_SOURCE + " WHERE s_name= \"" + name+"\"";
	     
	        SQLiteDatabase db = dbh.getReadableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        
	        if(cursor.getCount() == 0)
	        {
	        	return false;
	        }
	      
	        return true;
	    }

	public Cursor getCategoryByName(String souName) {
		
		SQLiteDatabase db = dbh.getReadableDatabase();
		String []arr = {souName};
		Cursor c = db.query(TABLE_SOURCE, null, KEY_S_NAME+ " = ?", arr, null , null, null);	
		return c;
	}
}
