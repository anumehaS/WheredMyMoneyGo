package com.smc.wheredmymoneygo.dbhelpers;



import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.smc.wheredmymoneygo.DBHandler;
import com.smc.wheredmymoneygo.category.Category;

public class CategoryDbHelper {
	
	/** Category Table Variables **/
	 private static final String TABLE_CATEGORY	 = "Category";

	 private static final String KEY_C_NAME = "c_name";
	 private static final String KEY_C_BUDGET = "c_budget";
	 private static final String KEY_C_FREQUENCY = "c_frequency";
	 public static final String KEY_C_COLOR = "c_color";
	

	 private DBHandler dbh;
	
	 public CategoryDbHelper(Context context){
			
		dbh = DBHandler.getInstance(context);			
	 }
	 
	 // add a category 
	 public void addCategory(Category category) {
		 
		 SQLiteDatabase   db = dbh.getWritableDatabase();
		 ColorDbHelper colorDb = new ColorDbHelper(db);
		 int color = colorDb.getFirstAvailableColor();
		 
		 ContentValues values = new ContentValues();
		 values.put(KEY_C_NAME, category.getName()); // Category Name
		 values.put(KEY_C_BUDGET, category.getBudget()); // Category budget
		 values.put(KEY_C_FREQUENCY, category.getFrequency()); // Category frequency
		 values.put(KEY_C_COLOR, color ); //color for category
		 colorDb.updateColor(color, "true");
		 
		 // Inserting Row
		 db.insert(TABLE_CATEGORY, null, values);
		 db.close(); // Closing database connection
		 
	 }
	 
	 public Cursor getAllCategories() {
		 
		 SQLiteDatabase db = dbh.getReadableDatabase();
		 
		 Cursor cursor = db.query(TABLE_CATEGORY, null, null, null, null , null, null);
		
		 return cursor;
		 
	 }
	 
	 public void updateCategory(Category category, String name) {
	    	SQLiteDatabase db = dbh.getWritableDatabase();
	    	 
	  	    ContentValues values = new ContentValues();
	  	    values.put(KEY_C_NAME, category.getName()); // Category Name
	  	    values.put(KEY_C_BUDGET, category.getBudget()); // Category  budget
	  	    values.put(KEY_C_FREQUENCY, category.getFrequency()); //Category frequency of budget
	  	   
	  	    // Updating Row
	  	    db.update(TABLE_CATEGORY, values, KEY_C_NAME+"=\""+ name+ "\"", null);
	  	    db.close(); // Closing database connection
	 
	   }
	 
	 public void deleteCategory(String name)
	 {
	      SQLiteDatabase db = dbh.getWritableDatabase();
	 
	  	    // Deleting Row
	      //Add part to free color
	  	    db.delete(TABLE_CATEGORY, KEY_C_NAME + "=\"" + name +"\"", null);
	  	    db.close(); // Closing database connection
	  
    }
	 
	 public Boolean nameExists(String name) {
	        // Select All Query
	        String selectQuery = "SELECT "+KEY_C_NAME+" FROM " + TABLE_CATEGORY + " WHERE "+KEY_C_NAME+"= \"" + name+"\"";
	     
	        SQLiteDatabase db = dbh.getReadableDatabase();
	        Cursor cursor = db.rawQuery(selectQuery, null);
	        
	        if(cursor.getCount() == 0)
	        {
	        	return false;
	        }
	      
	        return true;
	    }

	public Cursor getCategoryByName(String catName) {
		
		SQLiteDatabase db = dbh.getReadableDatabase();
		String []arr = {catName};
		Cursor c = db.query(TABLE_CATEGORY, null, KEY_C_NAME+"= ?", arr, null , null, null);	
		return c;
	}
		
		

}
