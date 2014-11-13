package com.smc.wheredmymoneygo.income;

import com.smc.wheredmymoneygo.CashFlow;

public class Income extends CashFlow{
	
	 String _source;
	    
     
	    // Empty constructor
	    public Income(){
	         
	    }
	    // constructor
	    public Income(long id, String name, String desc, String date, String currency, float amount,  String source, String freq, boolean ask){
	       
	    	super(id,name,desc,date,currency,amount,freq,ask);
	        this._source = source;
	        
	    }
	     
	   
		// constructor
	    public Income(String name, String desc, String date, String currency, float amount, String source, String freq, boolean ask){
	       
	    	super(name,desc,date,currency,amount,freq,ask);
	        this._source = source;
	         
	    }
	    

		// constructor
	    public Income(String name, String desc, String date, String currency, float amount, String source, float convToDef, String freq, boolean ask){
	       
	    	super(name,desc,date,currency,amount,convToDef, freq,ask);
	        this._source = source;
	         
	    }
	     
	    
	    // getting category1
	    public String getSource(){
	        return this._source;
	    }
	     
	    // setting category1
	    public void setSource(String source){
	        this._source = source;
	    }

}
