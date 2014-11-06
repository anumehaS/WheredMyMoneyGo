package com.anumeha.wheredmymoneygo.category;

import com.anumeha.wheredmymoneygo.dbhelpers.CategoryDbHelper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class CategoryCursorLoader extends CursorLoader{

	private CategoryDbHelper db;
	private String catName=null;
	
	public CategoryCursorLoader(Context context) {
		super(context);
		db = new CategoryDbHelper(context);
		
	}
	
	public CategoryCursorLoader(Context context, String catName) {
		super(context);
		db = new CategoryDbHelper(context);
		this.catName = catName;
		
	}
	
	@Override
	public Cursor loadInBackground() {
		Cursor c ;
		if(catName == null) {
		c =  db.getAllCategories();		
				
		}
		else {
			c = db.getCategoryByName(catName);
			catName = null;
		}
		return c;
	}
	

}
