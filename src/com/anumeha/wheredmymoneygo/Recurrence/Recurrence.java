package com.anumeha.wheredmymoneygo.recurrence;


public class Recurrence {
	/** 
	 *  class to represent recurring payments/income
	 **/
	
	//private variables
    int _id;
    String _isEOrI;
    int _eiId;
    String _frequency;
    Boolean _alert;
	String _date;
	
	public Recurrence(){
	}
	
	public Recurrence(int id, String isEOrI, int eiId, String frequency, Boolean alert, String date ){
		
		this._id= id;
		this._isEOrI = isEOrI;
		this._eiId = eiId;
		this._frequency = frequency;
		this._alert = alert;
		this._date = date;
				
	}
	
	public Recurrence(String isEOrI, int eiId, String frequency, Boolean alert, String date ){
		
		this._isEOrI = isEOrI;
		this._eiId = eiId;
		this._frequency = frequency;
		this._alert = alert;
		this._date = date;
				
	}
	
    // getting ID
    public int getID(){
        return this._id;
    }
     
    // setting id
    public void setID(int id){
        this._id = id;
    }
     
    // getting is income or expense
    public String getIsEOrI(){
        return this._isEOrI;
    }
     
    // setting is income or expense
    public void setIsEOrI(String isEOrI){
    	this._isEOrI = isEOrI;
    }
    
    // getting is income or expense id
    public int getEiId(){
        return this._eiId;
    }
     
    // setting is income or expense
    public void setEiId(int eiId){
    	this._eiId = eiId;
    }

    // getting frequency
    public String getFrequency(){
        return this._frequency;
    }
     
    // setting frequency
    public void setFrequency(String _frequency){
        this._frequency = _frequency;
    }
    
    // getting frequency
    public Boolean getAlert(){
        return this._alert;
    }
     
    // setting alert
    public void setAlert(Boolean _alert){
        this._alert = _alert;
    }
    
    // getting date
    public String getDate(){
        return this._date;
    }
     
    // setting date
    public void setDate(String _date){
        this._date = _date;
    }


}
