package com.anumeha.wheredmymoneygo.expense;

import com.anumeha.wheredmymoneygo.dbhelpers.ExpenseDbHelper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class ExpenseCursorLoader extends CursorLoader{
	
	ExpenseDbHelper db; 
	int expId = -1;
	int type =0;
	String c_name;
	public ExpenseCursorLoader(Context context, int type) { //1 for all exp, 2 for expId, 3 for category aggregation 
		super(context);		
		db = new ExpenseDbHelper(context);
		this.type = type;
		
	}
	
	public ExpenseCursorLoader(Context context, int expId,int type) {
		super(context);
		this.expId= expId;
		this.type = type;
		db = new ExpenseDbHelper(context);
		
	}
	
	
	@ Override
	public Cursor loadInBackground() {

		
		switch(type) {
		case 1 : 
			return db.getAllExpenses();
		case 2:
			return db.getExpenseById(expId);
		case 3: 
			return db.getCategoriesAndExpenses();
		default :
			return db.getAllExpenses();
		}
		
	}

}
