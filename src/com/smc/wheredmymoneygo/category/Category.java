package com.smc.wheredmymoneygo.category;

public class Category {
     
    //private variables
    int _id;
    String _name;
    float _budget;
    String _frequency;
    String _alert;
   
 
     
    // Empty constructor
    public Category(){
         
    }
    // constructor
    public Category(int id, String name, float _budget, String _frequency){
       
    	this._id = id;
        this._name = name;
        this._budget = _budget;
        this._frequency = _frequency;
    
    }
     
    // constructor
    public Category(String name, float _budget, String _frequency){
        
    	this._name = name;
        this._budget = _budget;
        this._frequency = _frequency;

    }
    
    // getting ID
    public int getID(){
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
     
    
    // getting budget
    public float getBudget(){
        return this._budget;
    }
     
    // setting budget
    public void setBudget(float _budget){
        this._budget = _budget;
    }
    
    // getting frequency
    public String getFrequency(){
        return this._frequency;
    }
     
    // setting frequency
    public void setFrequency(String _frequency){
        this._frequency = _frequency;
    }
    
  
}