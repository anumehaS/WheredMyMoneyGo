package com.smc.wheredmymoneygo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * @author asrivastava
 *
 *Cash Flow is the base class for Income and Expenses
 */

public class CashFlow {
	
	 //private variables
    private long _id;
    private String _name;
    private String _desc;
    private String _date;
    private String _currency;
    private float _amount;
    private float _convToDef;
    private String freq;
    private boolean ask;
    
    //empty constructor
    public CashFlow(){}
    

    // constructor
    public CashFlow(long id, String name, String desc, String date, String currency, float amount, String freq, boolean ask){
       
    	this._id = id;
        this._name = name;
        this._desc = desc;
        this._date = date;
        this._currency = currency;
        this._amount = amount;
        this.setFreq(freq);
        this.setAsk(ask);
        
    }
     
   
	// constructor
    public CashFlow(String name, String desc, String date, String currency, float amount,String freq, boolean ask){
        
        this._name = name;
        this._desc = desc;
        this._date = date;
        this._currency = currency;
        this._amount = amount;
        this.setFreq(freq);
        this.setAsk(ask);
         
    }
    
    //constructor with currency conv 
    public CashFlow(String name, String desc, String date, String currency, float amount, float convToDef, String freq, boolean ask){
        
        this._name = name;
        this._desc = desc;
        this._date = date;
        this._currency = currency;
        this._amount = amount;
        this._convToDef = convToDef; 
        this.setFreq(freq);
        this.setAsk(ask);
    }
    
 // getting ID
    public long getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
     
    // getting name
    public String getName(){
        return this._name;
    }
     
    // setting name
    public void setName(String name){
        this._name = name;
    }
     
    // getting description
    public String getDesc(){
        return this._desc;
    }
     
    // setting description
    public void setDesc(String desc){
        this._desc = desc;
    }  
    
    // getting date in a format
    public String getDateinFormat(String format ){
    	
    	Date date;
    	String tempDate = "";
		try {
			date = new SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).parse(this._date);
			
			SimpleDateFormat sdf = new SimpleDateFormat(format);
			tempDate = sdf.format(date);
			
	      
			
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		  return tempDate;
    	
    }
    
    public String getDate(){
    	return this._date;
    }
     
    // setting date
    public void setDate(String date){
        this._date = date;
    }
    
    // getting amount
    public float getAmount(){
        return this._amount;
    }
     
    // setting amount
    public void setAmount(float amount){
        this._amount = amount;
    }
    
    // getting currency
    public String getCurrency(){
        return this._currency;
    }
     
    // setting currency
    public void setCurrency(String currency){
        this._currency = currency;
    }


	public float get_convToDef() {
		return _convToDef;
	}


	public void set_convToDef(float _convToDef) {
		this._convToDef = _convToDef;
	}


	public String getFreq() {
		return freq;
	}


	public void setFreq(String freq) {
		this.freq = freq;
	}


	public boolean getAsk() {
		return ask;
	}


	public void setAsk(boolean ask) {
		this.ask = ask;
	}

}
