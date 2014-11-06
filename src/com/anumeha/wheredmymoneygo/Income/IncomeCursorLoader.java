package com.anumeha.wheredmymoneygo.income;


import com.anumeha.wheredmymoneygo.dbhelpers.IncomeDbHelper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class IncomeCursorLoader extends CursorLoader {

	IncomeDbHelper db; 
	int incId = -1;
	int type =0;
	String s_name;
	public IncomeCursorLoader(Context context,int type) {
		super(context);		
		db = new IncomeDbHelper(context);
		this.type = type;
		
	}
	
	public IncomeCursorLoader(Context context, int incId, int type) {
		super(context);
		this.incId= incId;
		this.type = type;
		db = new IncomeDbHelper(context);	
	}

	@ Override
	public Cursor loadInBackground() {

		switch(type) {
		case 1 : 
			return db.getAllIncome();
		case 2:
			return db.getIncomeById(incId);
		case 3: 
			return db.getCategoriesAndIncome();
		default :
			return db.getAllIncome();
		}
		
	}
}
