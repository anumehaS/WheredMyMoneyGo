package com.anumeha.wheredmymoneygo.Expense;

import com.anumeha.wheredmymoneygo.CashFlow;

public class Expense extends CashFlow{
     
   
    String _category1;
    
     
    // Empty constructor
    public Expense(){
         
    }
    // constructor
    public Expense(int id, String name, String desc, String date, String currency, float amount,  String category1, String freq, boolean ask){
       
    	super(id,name,desc,date,currency,amount,freq,ask);
        this._category1 = category1;
        
    }
     
   
	// constructor
    public Expense(String name, String desc, String date, String currency, float amount, String category1, String freq, boolean ask){
       
    	super(name,desc,date,currency,amount,freq,ask);
        this._category1 = category1;
         
    }
    
 // constructor
    public Expense(String name, String desc, String date, String currency, float amount, String category1,float convToDef, String freq, boolean ask){
       
    	super(name,desc,date,currency,amount,convToDef,freq,ask);
        this._category1 = category1;
         
    }
     
    
    // getting category1
    public String getCategory1(){
        return this._category1;
    }
     
    // setting category1
    public void setCategory1(String category1){
        this._category1 = category1;
    }
    
}