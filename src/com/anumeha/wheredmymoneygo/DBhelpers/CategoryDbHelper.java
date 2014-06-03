package com.anumeha.wheredmymoneygo.DBhelpers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.anumeha.wheredmymoneygo.DBHandler;
import com.anumeha.wheredmymoneygo.Category.Category;
import com.anumeha.wheredmymoneygo.Expense.Expense;

public class CategoryDbHelper {
	
	/** Category Table Variables **/
	 private static final String TABLE_CATEGORY	 = "Category";
	 
	 private static final String KEY_C_ID = "_id";
	 private static final String KEY_C_NAME = "c_name";
	 private static final String KEY_C_BUDGET = "c_budget";
	 private static final String KEY_C_FREQUENCY = "c_frequency";
	

	 private DBHandler dbh;
	
	 public CategoryDbHelper(Context context){
			
		dbh = new DBHandler(context);			
	 }
	 
	 // add a category 
	 public void addCategory(Category category) {
		 
		 SQLiteDatabase   db = dbh.getWritableDatabase();
		 
		 ContentValues values = new ContentValues();
		 values.put(KEY_C_NAME, category.getName()); // Category Name
		 values.put(KEY_C_BUDGET, category.getBudget()); // Category budget
		 values.put(KEY_C_FREQUENCY, category.getFrequency()); // Category frequency
		 
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
