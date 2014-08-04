package com.anumeha.wheredmymoneygo.DBhelpers;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

import com.anumeha.wheredmymoneygo.DBHandler;
import com.anumeha.wheredmymoneygo.Income.Income;

public class IncomeDbHelper {

	 /** Income Table **/
    private static final String TABLE_INCOME	 = "Income";

    private static final String KEY_I_ID = "_id";
    private static final String KEY_I_NAME = "i_name";
    private static final String KEY_I_DESC = "i_description";
    private static final String KEY_I_CURRENCY = "i_currency";
    private static final String KEY_I_DATE = "i_date";
    private static final String KEY_I_AMOUNT = "i_amount";
    private static final String KEY_I_SOURCE = "i_source";
    private static final String KEY_I_CONVRATE = "i_convrate";
    
    private SQLiteDatabase database;
	private DBHandler dbh;
	private SharedPreferences prefs;
	
	public IncomeDbHelper(Context context){
		
		dbh = new DBHandler(context);
		prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());	
	}
	
	// Adding new income
    public void addIncome(Income income) {
	    database = dbh.getWritableDatabase();
	    ContentValues values = new ContentValues();
	    values.put(KEY_I_NAME, income.getName()); // IncomeName
	    values.put(KEY_I_DESC, income.getDesc()); // income description
	    values.put(KEY_I_DATE, income.getDate()); // income date
	    values.put(KEY_I_CURRENCY, income.getCurrency()); // income currency
	    values.put(KEY_I_AMOUNT, income.getAmount()); // income amount  
	    values.put(KEY_I_SOURCE, income.getSource()); // income Source
	    values.put(KEY_I_CONVRATE, income.get_convToDef()); // Income conversion to default rate
	    // Inserting Row
	    database.insert(TABLE_INCOME, null, values);
	    database.close(); // Closing database connection
    }
    
    //getting all income
    public Cursor getAllIncome() {
    	
    	StringBuffer temp = new StringBuffer();
    	StringBuffer temp2 = new StringBuffer();
    	ArrayList<String> temp1 = new ArrayList<String>();
    	String orderBy = null, selection = null;
    	String [] selectionArgs = null;
    	
    	 SQLiteDatabase db = dbh.getWritableDatabase();
    	 
    	 //if view as pie, no ordering required
    	 if(prefs.getString("inc_cur_viewAs","").equals("pie") || (prefs.getString("inc_cur_viewAs","").equals("")&&prefs.getString("inc_def_viewAs","").equals("pie"))){
    	 
    		 orderBy = null;
    	 }
    	 else {    		 
    		 temp2.append(prefs.getString("inc_cur_orderBy", "").equals("")? prefs.getString("inc_def_orderBy", ""):prefs.getString("inc_cur_orderBy", ""));   
    		 temp2.append(" ");
    		 temp2.append(prefs.getString("inc_cur_sortOrder", "").equals("")? prefs.getString("inc_def_sortOrder", ""):prefs.getString("inc_cur_sortOrder", ""));
    	 }
    	 
    	 // add selection args if "inrange" is selected
    	 if(prefs.getString("inc_viewBy","").equals("inRange")){
        	 
    		temp.append("date(");
    		temp.append(KEY_I_DATE);
    		temp.append(") >= ? AND date(");
    		temp.append(KEY_I_DATE);
    		temp.append(") <= ?");
    		
    		temp1.add(prefs.getString("inc_startDate",""));
    		temp1.add(prefs.getString("inc_endDate",""));
    		
    		 
    	 }
    	 
    	 if(!prefs.getString("inc_filter","").equals(""))
    	 {
    		 if(temp.length() > 0)
    		 {
    			 temp.append(" AND ");
    		 }
    		 
    		 temp.append(KEY_I_SOURCE);
    		 temp.append(" = ?");
    		 temp1.add(prefs.getString("inc_filter","")); // add selection args
    	 }
    	
    	 if(temp.length() > 0) {
    	 
	    	 selection = temp.toString();	    	 
	    	 selectionArgs = (String[]) temp1.toArray(new String[temp1.size()]);    	 
    	 } 
    	 
    	 if(temp2.length() > 0){
    		 orderBy = temp2.toString();
    	 }
         
        Cursor cursor = db.query(TABLE_INCOME, null, selection, selectionArgs, null , null, orderBy);
        //Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_INCOME +" ORDER BY date("+KEY_I_DATE+") DESC",null);
         
         
         return cursor;
    	
    }

    public Cursor getIncomeById(int id) {
    	
      	 SQLiteDatabase db = dbh.getWritableDatabase();   	 
      	 Cursor c = db.query(TABLE_INCOME, null ,KEY_I_ID +" = "+id,null,null,null,null);
       	
       	return c;
       }
   	

       //update income
       public void updateIncome(Income income, int id) {
         SQLiteDatabase db = dbh.getWritableDatabase();
       		 
         ContentValues values = new ContentValues();
	 	    values.put(KEY_I_NAME, income.getName()); // Expense Name
	 	    values.put(KEY_I_DESC, income.getDesc()); // income description
	 	    values.put(KEY_I_DATE, income.getDate()); // income date
	 	    values.put(KEY_I_CURRENCY, income.getCurrency()); // income currency
	 	    values.put(KEY_I_AMOUNT, income.getAmount()); // income amount  
	 	    values.put(KEY_I_SOURCE, income.getSource()); // income Source
	 	    values.put(KEY_I_CONVRATE, income.get_convToDef()); // Income conversion to default rate
     	 
     	    // Updating Row
     	    db.update(TABLE_INCOME, values, KEY_I_ID+"="+ id , null);
     	    db.close(); // Closing database connection
     
       }
       
       //delete income
       
       public void deleteIncome(int incId) {
         SQLiteDatabase db = dbh.getWritableDatabase();
    
     	    // Deleting Row
     	    db.delete(TABLE_INCOME, KEY_I_ID + "=" + incId, null);
     	    db.close(); // Closing database connection
     
       }
}
