package com.anumeha.wheredmymoneygo;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import com.anumeha.wheredmymoneygo.category.Category;
import com.anumeha.wheredmymoneygo.currency.Currency;
import com.anumeha.wheredmymoneygo.dbhelpers.ColorDbHelper;
import com.anumeha.wheredmymoneygo.source.Source;

import android.content.ContentValues;
import android.content.Context;
import android.content.res.AssetManager;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Color;

public class DBHandler extends SQLiteOpenHelper{
	
	private static AssetManager assetManager;
	/** class variables  - database**/
	private static final int DATABASE_VERSION = 1;	  
	private static final String DATABASE_NAME = "WMMGDatabase";
	
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
    private static final String KEY_I_FREQ = "i_freq"; //frequecy of repetition 
    private static final String KEY_I_ASK = "i_ask"; //ask the user before adding recurrence
    
    /** Category Table **/
    public static final String TABLE_CATEGORY	 = "Category";
 
    public static final String KEY_C_ID = "_id";
    public static final String KEY_C_NAME = "c_name";
    private static final String KEY_C_BUDGET = "c_budget";
    private static final String KEY_C_FREQUENCY = "c_frequency";
    public static final String KEY_C_COLOR = "c_color";
        
    /** Sources Table **/
    public static final String TABLE_SOURCE	 = "Source";
 
    public static final String KEY_S_ID = "_id";
    public static final String KEY_S_NAME = "s_name";
    public static final String KEY_S_COLOR = "s_color";
    
    /** Recurrences Table
    private static final String TABLE_RECURRENCE = "Recurrences";
    
    private static final String KEY_R_ID = "_id";
    private static final String KEY_R_IS_EI = "expense_or_income";
    private static final String KEY_R_EI_ID = "ei_id"; //  income or expense ID
    private static final String KEY_R_FREQUENCY = "frequency"; //one time - weekly -monthly or annual
    private static final String KEY_R_DATE = "recurrence_date"; **/
    
    /** Currency Table**/
    private static final String TABLE_CURRENCY = "Currency";
    
    private static final String KEY_Cu_ID = "_id";
    private static final String KEY_Cu_CONVRATE = "conversion_rate"; //  rate of conversion to base currency
    private static final String KEY_Cu_COUNTRY = "Country"; //
    private static final String KEY_Cu_SYMBOL = "symbol";
    private static final String KEY_Cu_TS = "timestamp";
    
    /** Alerts Table
    private static final String TABLE_ALERTS = "Alerts"; // basically a reminder to pay bills etc
    
    private static final String KEY_A_ID = "_id";
    private static final String KEY_A_TEXT = "alert_text"; 
    private static final String KEY_A_FREQUENCY = "frequency"; //one time - weekly -monthly or annual
    private static final String KEY_A_DATE = "recurrence_date"; **/
 
    /** Colors Table**/
    private static final String TABLE_COLORS = "Colors"; 
    
    private static final String KEY_Co_COLOR = "color_code";
    private static final String KEY_Co_TAKEN = "taken";  

    /*Make it singleton*/
    private static DBHandler sInstance;
    
    public static DBHandler getInstance(Context context) {
       
        if (sInstance == null) {
           	sInstance = new DBHandler(context.getApplicationContext());

          if(assetManager == null)
	        	assetManager = context.getApplicationContext().getAssets();
        }
        
        return sInstance;
      }
	 private DBHandler(Context context) {
	        super(context, DATABASE_NAME, null, DATABASE_VERSION);
	        if(assetManager == null)
	        	assetManager = context.getApplicationContext().getAssets();
	  }
	 
	 /** Creating tables **/
	 
	    @Override
	    public void onCreate(SQLiteDatabase db) {
	    	
	    	//Expense Table
	        String CREATE_EXPENSES_TABLE = "CREATE TABLE " + TABLE_EXPENSES + "("
	                + KEY_E_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_E_NAME + " TEXT, "
	                + KEY_E_DESC + " TEXT, " 
	        		+ KEY_E_DATE + " datetime, " 
	        		+ KEY_E_CURRENCY + " TEXT, " 
	                + KEY_E_AMOUNT + " REAL, "
	                + KEY_E_CATEGORY1 + " TEXT, "
	                + KEY_E_CONVRATE + " REAL, "
	                + KEY_E_FREQ + " TEXT, "
	                + KEY_E_ASK + " TEXT"
	                +")";
	        db.execSQL(CREATE_EXPENSES_TABLE);
	        
	      //Income Table
	        String CREATE_INCOME_TABLE = "CREATE TABLE " + TABLE_INCOME + "("
	                + KEY_I_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_I_NAME + " TEXT, "
	                + KEY_I_DESC + " TEXT, " 
	        		+ KEY_I_DATE + " datetime, " 
	        		+ KEY_I_CURRENCY + " TEXT, " 
	                + KEY_I_AMOUNT + " REAL, "
	                + KEY_I_SOURCE + " TEXT, "
	                + KEY_I_CONVRATE + " REAL, "
	                + KEY_I_FREQ + " TEXT, "
	                + KEY_I_ASK + " TEXT"
	                +")";
	        db.execSQL(CREATE_INCOME_TABLE);
	        	        
	        //Category Table     
	        String CREATE_CATEGORY_TABLE = "CREATE TABLE " + TABLE_CATEGORY + "("
	                + KEY_C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_C_NAME + " TEXT," 
	                + KEY_C_BUDGET + " REAL," 
	        		+ KEY_C_FREQUENCY + " TEXT,"
	        		+ KEY_C_COLOR + " REAL"
	                + ")";
	        db.execSQL(CREATE_CATEGORY_TABLE);
	        
	      //Sources Table     
	        String CREATE_SOURCE_TABLE = "CREATE TABLE " + TABLE_SOURCE + "("
	                + KEY_S_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_S_NAME + " TEXT,"
	        		+ KEY_S_COLOR + " REAL"
	                + ")";
	        db.execSQL(CREATE_SOURCE_TABLE);
	         
	        //Currency Table    
	        String CREATE_CURRENCY_TABLE = "CREATE TABLE " + TABLE_CURRENCY + "("
	                + KEY_Cu_ID + " TEXT PRIMARY KEY," 
	        		+ KEY_Cu_CONVRATE + " REAL," 
	                + KEY_Cu_COUNTRY + " TEXT," 
	        		+ KEY_Cu_SYMBOL + " TEXT,"
	                + KEY_Cu_TS + " TEXT" + ")";
	        db.execSQL(CREATE_CURRENCY_TABLE);
	        
	        //Alerts Table     -------- next release
/**	        String CREATE_ALERTS_TABLE = "CREATE TABLE " + TABLE_ALERTS + "("
	                + KEY_A_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," 
	        		+ KEY_A_TEXT + " TEXT," 
	        		+ KEY_A_FREQUENCY + " TEXT,"
	                + KEY_A_DATE + " datetime" + ")";
	        db.execSQL(CREATE_ALERTS_TABLE);
**/
	       
	        //Colors Table    
	        String CREATE_COLORS_TABLE = "CREATE TABLE " + TABLE_COLORS + "("
	                + KEY_Co_COLOR + " INTEGER PRIMARY KEY," 
	                + KEY_Co_TAKEN + " TEXT" + ")";
	        db.execSQL(CREATE_COLORS_TABLE);
	       
	      
	      populateSources(db); //pre populate the sources
	      populateCurrency(db); //pre populate the currency
	      populateColors(db); //pre populate the colors 
	      populateCategories(db); //pre populate the categories
	      
	    }
	    
	    // Upgrading database
	    @Override
	    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	        // Drop older table if existed
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_EXPENSES);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_INCOME);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORY);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SOURCE);
	        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CURRENCY);

	        // Create tables again
	        onCreate(db);
	    }
	    
	    private void populateCategories(SQLiteDatabase db) {
	    	Category category;
	    	
	    	
	    	category = new Category ("Food",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Entertainment",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Utilities",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Rent",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Electricity",-1f,"");
	    	addCategory(category,db);
	    	category = new Category ("Other",-1f,"");
	    	addCategory(category,db);
	    	
	    	System.out.println("populating categories");
	    	
	    }
	    
	    private void populateCurrency(SQLiteDatabase db) {
	    	Currency cur;
	    	
	    	
	    	cur = new Currency ("USD",1,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("EUR",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("JPY",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("AUD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("CAD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("SGD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("CHF",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("INR",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("CNY",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("HKD",0,"","","");
	    	addCurrency(cur,db);
	    	cur = new Currency ("AED",0,"","","");
	    	addCurrency(cur,db);
	    	
	    	System.out.println("populating currency");
	    	
	    }
	    
	    private void populateSources(SQLiteDatabase db) {
	    	Source source;
 	
	    	source = new Source("Other");
	    	addSource(source,db);
	    	
	    	System.out.println("populating source");
	    	
	    }
	    
	    
	    
	    public void addCategory(Category category, SQLiteDatabase db) {
			 
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
			
			 
		 }
	    
	    public void addCurrency(Currency cur, SQLiteDatabase db) {
			 
			
			 ContentValues values = new ContentValues();
			 values.put(KEY_Cu_ID , cur.get_id()); // Currency code eg USD - mandatory
			 values.put(KEY_Cu_CONVRATE, cur.getConversionRate()); // Currency conversion rate
			 values.put(KEY_Cu_COUNTRY, cur.getCountry()); // Currency country
			 values.put(KEY_Cu_SYMBOL, cur.getConversionRate()); // Currency symbol - $
			 values.put(KEY_Cu_TS, cur.getTimeStamp()); // Currency time stamp
			 
			 // Inserting Row
			 db.insert(TABLE_CURRENCY, null, values);
			
			 
		 }
	    
	    
	    public void addSource(Source source, SQLiteDatabase db) {
	    	
	    	ColorDbHelper colorDb = new ColorDbHelper(db);
	    	 int color = colorDb.getFirstAvailableColor();
	    	
	    	ContentValues values = new ContentValues();
			 values.put(KEY_S_NAME, source.getName()); //Source Name
			 values.put(KEY_S_COLOR, color ); //color for category
			 colorDb.updateColor(color, "true");
			 // Inserting Row
			 db.insert(TABLE_SOURCE, null, values);
			
			 
		 }
	    
		
	    private void populateColors(SQLiteDatabase db) {
	    	
	    	//Read the colors file from 
	    	try {
				BufferedReader br = new BufferedReader(new InputStreamReader(assetManager.open("colors.txt")));
				String line;
				while( (line = br.readLine())!= null ){
					String[] lineElems = line.split(",");
					if(lineElems.length<3)
						continue;
					int red = Integer.parseInt(lineElems[0]);
					int green = Integer.parseInt(lineElems[1]);
					int blue = Integer.parseInt(lineElems[2]);
					
					if(validateColorComponent(red)
							&& validateColorComponent(green)
							&& validateColorComponent(blue)){
						//System.out.println("RGB: (" +red + "," + green + "," + blue + ")=" + Color.rgb(red, green, blue) );
						addColor(Color.rgb(red, green, blue), db);
					}
					
				}
			} catch (IOException e) {
				System.out.println(e.getMessage());
			}
	    	System.out.println("populating colors");
	    	
	    }
	    
	    public void addColor(int color_code, SQLiteDatabase db) {
			 
			ContentValues values = new ContentValues();
			values.put(KEY_Co_COLOR, color_code);
			values.put(KEY_Co_TAKEN, "false"); 
			 
			// Inserting Row
			db.insert(TABLE_COLORS, null, values);
			
			 
		}
	    
	    public boolean validateColorComponent(int colorComponent){
	    	if(colorComponent >= 0 && colorComponent <= 256)
	    		return true;
	    	else
	    		return false;
	    }
	    
	    

}
