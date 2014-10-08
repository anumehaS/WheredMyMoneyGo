package com.anumeha.wheredmymoneygo.DBhelpers;

import java.text.SimpleDateFormat;
import java.util.Calendar;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.anumeha.wheredmymoneygo.DBHandler;


public class CurrencyDbHelper {
	
	/** Currency Table**/
    private static final String TABLE_CURRENCY = "Currency";
    
    private static final String KEY_Cu_ID = "_id";
    private static final String KEY_Cu_CONVRATE = "conversion_rate"; //  rate of conversion to base currency
   /* private static final String KEY_Cu_COUNTRY = "Country"; //
    private static final String KEY_Cu_SYMBOL = "symbol";*/
    private static final String KEY_Cu_TS = "timestamp";
    
    
    private DBHandler dbh;
	
	 public CurrencyDbHelper(Context context){
			
		dbh = DBHandler.getInstance(context);			
	 }
	 
	 public Cursor getAllCurrencies() {
		 
		 SQLiteDatabase db = dbh.getReadableDatabase();
		 
		 Cursor cursor = db.query(TABLE_CURRENCY, null, null, null, null , null, null);
		
		 return cursor;
		 
	 }
	 
	 public Cursor getCurrencyById(String id) {
			
			SQLiteDatabase db = dbh.getReadableDatabase();
			String []arr = {id};
			Cursor c = db.query(TABLE_CURRENCY, null, KEY_Cu_ID+"= ?", arr, null , null, null);	
			return c;
	}
	 
	 public void updateCurrency(float rate, String id) {
	    	SQLiteDatabase db = dbh.getWritableDatabase();
	    	 
	    	Calendar cal = Calendar.getInstance();
	    	SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	    	String date = sdf.format(cal.getTime());
	    	
	  	    ContentValues values = new ContentValues();
	  	    values.put(KEY_Cu_CONVRATE, rate); //conv rate
	  	    values.put(KEY_Cu_TS, date); //lastest update stamp
	  	   
	  	    // Updating Row
	  	    db.update(TABLE_CURRENCY, values, KEY_Cu_ID+"=\""+ id+ "\"", null);
	  	    db.close(); // Closing database connection
	 
	   }

}
