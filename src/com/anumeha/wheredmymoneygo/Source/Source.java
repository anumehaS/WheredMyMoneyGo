package com.anumeha.wheredmymoneygo.source;

public class Source {
	
	//class variables
	private int _id;
	private String _name;
	
	//empty constructor
	public Source(){
		
	}
	
	public Source(int id, String name)
	{
		this.set_id(id);
		this._name = name;
	}
	
	public Source(String name)
	{
		setName(name);
	}
	
	public void setName(String name) {
		this._name = name;		
	}
	public String getName() {
		
		return this._name;
	}

	public int get_id() {
		return _id;
	}

	public void set_id(int _id) {
		this._id = _id;
	}

}
