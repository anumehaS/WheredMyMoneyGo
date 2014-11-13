package com.smc.wheredmymoneygo.source;


import com.smc.wheredmymoneygo.dbhelpers.SourceDbHelper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class SourceCursorLoader extends CursorLoader {
	
	private SourceDbHelper db;
	private String souName = null;
	
	public SourceCursorLoader(Context context) {
		super(context);
		db = new SourceDbHelper(context);
		
	}
	
	public SourceCursorLoader(Context context, String souName) {
		super(context);
		db = new SourceDbHelper(context);
		this.souName = souName;
		
	}
	
	@Override
	public Cursor loadInBackground() {
		Cursor c ;
		if(souName == null) {
		c =  db.getAllSources();		
				
		}
		else {
			c = db.getCategoryByName(souName);
			souName = null;
		}
		return c;
	}

}
