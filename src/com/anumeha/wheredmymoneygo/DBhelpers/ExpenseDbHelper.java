
package com.anumeha.wheredmymoneygo.dbhelpers;

import java.util.ArrayList;

import com.anumeha.wheredmymoneygo.expense.Expense;
import com.anumeha.wheredmymoneygo.DBHandler;
import com.anumeha.wheredmymoneygo.Globals;

import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.preference.PreferenceManager;

public class ExpenseDbHelper {
	
	/** Expense Table **/
    private static final String TABLE_EXPENSES	 = "Expenses";

    private static final String KEY_E_ID = "_id";
    private static final String KEY_E_NAME = "e_name";
    private static final String KEY_E_DESC = "e_description";
    private static final String KEY_E_CURRENCY = "e_currency";
    private static final String KEY_E_DATE = "e_date";
    private static final String KEY_E_AMOUNT = "e_amount";
    private static final String KEY_E_CATEGORY1 = "e_category1";
    private static final String KEY_E_CONVRATE = "e_convrate";
    private static final String KEY_E_FREQ = "e_freq"; //frequecy of repetition 
    private static final String KEY_E_ASK = "e_ask"; //ask the user before adding recurrence


	private SQLiteDatabase database;
	private DBHandler dbh;
	private SharedPreferences prefs;
	
	public ExpenseDbHelper(Context context){
		
		dbh = DBHandler.getInstance(context.getApplicationContext());
		prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());	
	}
	
	// Adding new expense
    public long addExpense(Expense expense) {
	    database = dbh.getWritableDatabase();

	    ContentValues values = new ContentValues();
	    values.put(KEY_E_NAME, expense.getName()); // Expense Name
	    values.put(KEY_E_DESC, expense.getDesc()); // Expense description
	    values.put(KEY_E_DATE, expense.getDate()); // Expense date
	    values.put(KEY_E_CURRENCY, expense.getCurrency()); // Expense currency
	    values.put(KEY_E_AMOUNT, expense.getAmount()); // Expense amount  
	    values.put(KEY_E_CATEGORY1, expense.getCategory1()); // Expense Category1
	    values.put(KEY_E_CONVRATE, expense.get_convToDef()); // Expense conversion to default rate
	    values.put(KEY_E_FREQ, expense.getFreq()); // Expense Frequency
	    values.put(KEY_E_ASK, expense.getAsk()?"yes":"no"); // Ask before adding recurrence
	    // Inserting Row
	   long id =  (int) database.insert(TABLE_EXPENSES, null, values);
	    database.close(); // Closing database connection
	    return id;
    }
    

	//getting all expenses
    public Cursor getAllExpenses() {
    	
    	StringBuffer selectionTemp = new StringBuffer();
    	StringBuffer temp2 = new StringBuffer();
    	ArrayList<String> temp1 = new ArrayList<String>();
    	String orderBy = null, selection = null;
    	String [] selectionArgs = null;
    	
    	 SQLiteDatabase db = dbh.getWritableDatabase();
       //  Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_EXPENSES, null);
    	 
    	 //if view as pie, no ordering required
    	 if(prefs.getString(Globals.EXP_DEF_VIEWAS,"").equals("pie")){
    	 
    		 orderBy = null;
    	 }
    	 else {    		 
    		 temp2.append(prefs.getString(Globals.EXP_DEF_ORDERBY, ""));   
    		 temp2.append(" ");
    		 temp2.append(prefs.getString(Globals.EXP_DEF_SORTORDER, ""));
    	 }
    	 
    	 // add selection args if "inrange" is selected
    	 if(prefs.getString(Globals.EXP_VIEWBY,"").equals("inRange")){
        	 
    		selectionTemp.append("date(");
    		selectionTemp.append(KEY_E_DATE);
    		selectionTemp.append(") BETWEEN ? AND ?");
    	//	temp.append(KEY_E_DATE);
    	//	temp.append(") <= ?");
    		
    		temp1.add(prefs.getString(Globals.EXP_START_DATE,""));
    		temp1.add(prefs.getString(Globals.EXP_END_DATE,""));
    		
    		 
    	 }
    	 
    	 if(!prefs.getString(Globals.EXP_FILTER,"").equals(""))
    	 {
    		 if(selectionTemp.length() > 0)
    		 {
    			 selectionTemp.append(" AND ");
    		 }
    		 
    		 selectionTemp.append(KEY_E_CATEGORY1);
    		 selectionTemp.append(" = ?");
    		 temp1.add(prefs.getString(Globals.EXP_FILTER,"")); // add selection args
    	 }
    	 
    	 if(prefs.getString(Globals.EXP_VIEW_REC,"").equals("on"))
    	 {
    		 if(selectionTemp.length() > 0)
    		 {
    			 selectionTemp.append(" AND ");
    		 }
    		 
    		 selectionTemp.append(KEY_E_FREQ);
    		 selectionTemp.append(" != ?");
    		 temp1.add("Do not repeat"); // add selection args to find only recurrances
    	 }
    	
    	 if(selectionTemp.length() > 0) {
    	 
	    	 selection = selectionTemp.toString();	    	 
	    	 selectionArgs = (String[]) temp1.toArray(new String[temp1.size()]);    	 
    	 } 
    	 
    	 if(temp2.length() > 0){
    		 orderBy = temp2.toString();
    	 }
         
         Cursor cursor = db.query(TABLE_EXPENSES, null, selection, selectionArgs, null , null, orderBy);
         // Cursor cursor = db.rawQuery("SELECT * FROM "+ TABLE_EXPENSES+ " ORDER BY date("+ KEY_E_DATE +") DESC", null);
         
         return cursor;
    	
    }

    public Cursor getAllRecurences(){

    	 String selection = KEY_E_FREQ + " !=?";
    	 String[] selectionArgs = {"Do not repeat"};
    	 SQLiteDatabase db = dbh.getWritableDatabase();
    	 Cursor cursor = db.query(TABLE_EXPENSES, null, selection, selectionArgs, null , null, null);
    	 return cursor;
    }
    
    public Cursor getExpenseById(long old_id) {
    	
   	 SQLiteDatabase db = dbh.getWritableDatabase();   	 
   	 Cursor c = db.query(TABLE_EXPENSES, null ,KEY_E_ID +" = "+old_id,null,null,null,null);
    	
    	return c;
    }
    
    public Cursor getExpenseByCategory(String c_name) {
    	
      	 SQLiteDatabase db = dbh.getWritableDatabase();   	 
      	 Cursor c = db.query(TABLE_EXPENSES, null ,KEY_E_CATEGORY1 +" = "+c_name,null,null,null,null);
       	
       	return c;
       }
	

    //update expense
    public void updateExpense(Expense expense, long old_id) {
      SQLiteDatabase db = dbh.getWritableDatabase();
    		 
  	    ContentValues values = new ContentValues();
  	    values.put(KEY_E_NAME, expense.getName()); // Expense Name
	    values.put(KEY_E_DESC, expense.getDesc()); // Expense description
	    values.put(KEY_E_DATE, expense.getDate()); // Expense date
	    values.put(KEY_E_CURRENCY, expense.getCurrency()); // Expense currency
	    values.put(KEY_E_AMOUNT, expense.getAmount()); // Expense amount  
	    values.put(KEY_E_CATEGORY1, expense.getCategory1()); // Expense Category1
	    values.put(KEY_E_CONVRATE, expense.get_convToDef()); // Expense conversion to default rate
	    values.put(KEY_E_FREQ, expense.getFreq()); // Expense Frequency
	    values.put(KEY_E_ASK, expense.getAsk()?"yes":"no"); // Ask before adding recurrence
  	 
  	    // Updating Row
  	    db.update(TABLE_EXPENSES, values, KEY_E_ID+"="+ old_id , null);
  	    db.close(); // Closing database connection
  
    }
    
    //delete expense 
    
    public void deleteExpense(int expId) {
      SQLiteDatabase db = dbh.getWritableDatabase();
 
  	    // Deleting Row
  	    db.delete(TABLE_EXPENSES, KEY_E_ID + "=" + expId, null);
  	    db.close(); // Closing database connection
  
    }

	public Cursor getCategoriesAndExpenses() {
		
		StringBuilder selection = new StringBuilder();
		ArrayList<String> temp = new ArrayList<String>();
		// add selection args if "inrange" is selected
	   	 if(prefs.getString(Globals.EXP_VIEWBY,"").equals("inRange")){
	       	 
	   		selection.append("date(");
	   		selection.append(KEY_E_DATE);
	   		selection.append(") BETWEEN ? AND ?");
	
	   		temp.add(prefs.getString(Globals.EXP_START_DATE,""));
	   		temp.add(prefs.getString(Globals.EXP_END_DATE,""));
	 
	   	 }
		
		 SQLiteDatabase db = dbh.getWritableDatabase();   	
		 String [] columns = {DBHandler.TABLE_CATEGORY+"."+DBHandler.KEY_C_ID,KEY_E_CATEGORY1,"SUM("+KEY_E_AMOUNT+" * "+KEY_E_CONVRATE+")",DBHandler.KEY_C_COLOR};
		 String table = TABLE_EXPENSES + " JOIN "+ DBHandler.TABLE_CATEGORY +" ON "+ KEY_E_CATEGORY1 +" = "+ DBHandler.KEY_C_NAME;
	   	 Cursor c = db.query(table, columns ,selection.toString(),temp.toArray(new String[temp.size()]),KEY_E_CATEGORY1,null,null);	    	
	    	return c;
	}
 
	public Cursor getExpensesForBackup(){
		 SQLiteDatabase db = dbh.getReadableDatabase();   	 
	   	 Cursor c = db.query(TABLE_EXPENSES, null ,null,null,null,null,null);
	   	 return c;
	}
}
