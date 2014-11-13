package com.smc.wheredmymoneygo.currency;

import com.smc.wheredmymoneygo.dbhelpers.CurrencyDbHelper;

import android.content.Context;
import android.content.CursorLoader;
import android.database.Cursor;

public class CurrencyCursorLoader extends CursorLoader {
	
	private CurrencyDbHelper db;
	
	public CurrencyCursorLoader(Context context) {
		super(context);
		db = new CurrencyDbHelper(context);	
	}
	
	@Override
	public Cursor loadInBackground() {
		
		Cursor c = db.getAllCurrencies();
		return c;
	}

	

}
