package com.smc.wheredmymoneygo.expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.smc.wheredmymoneygo.Globals;
import com.smc.wheredmymoneygo.R;
import com.smc.wheredmymoneygo.dbhelpers.CurrencyDbHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class ExpenseCursorAdapter extends ResourceCursorAdapter{

	SharedPreferences prefs;
	CurrencyDbHelper db;
	String defaultCurrency;
	
	public ExpenseCursorAdapter(Context context, int layout, Cursor c, int flags,String currency) {
		super(context, layout, c, flags);
		defaultCurrency = currency;
		prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		db = new CurrencyDbHelper(context);
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		final String dateFormat, currency;
		Date tempDate = null;
		SimpleDateFormat sdf;		
		
		//set expense id -- invisible but will be used for editing
		TextView e_Id = (TextView)view.findViewById(R.id.expenseId);
		String id = cursor.getString(0);
		e_Id.setText(id);
		
		//set expense frequency-- invisible but will be used for editing
		TextView e_Freq = (TextView)view.findViewById(R.id.expenseFreq);
		e_Freq.setText(cursor.getString(8));
		//set expense notify-- invisible but will be used for editing
		TextView e_Notify = (TextView)view.findViewById(R.id.expenseNotify);
		e_Notify.setText(cursor.getString(9));
		
	
		TextView e_Name = (TextView)view.findViewById(R.id.expenseName);
		e_Name.setText(cursor.getString(1));
		
		//set expense date--after adapting it to the user's prefered format
		TextView e_date = (TextView)view.findViewById(R.id.expenseDate);		
		//adapt date to user's preference 
		dateFormat = Globals.USER_DATE_FORMAT;// prefs.getString("def_dateformat", "MMMM dd, yyyy");
		sdf = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT);
		try {
			tempDate = sdf.parse(cursor.getString(3));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		sdf = new SimpleDateFormat(dateFormat);
		e_date.setText(sdf.format(tempDate)); 
		
		//show amount after appending currency symbol
		currency = cursor.getString(4);
	    final TextView e_Amount = (TextView)view.findViewById(R.id.expenseAmount);
	    final float convRate = Float.parseFloat(cursor.getString(7));
	    
	   
		if(prefs.getString(Globals.EXP_CONV, "off").equals("off") || currency.equals(defaultCurrency)) { //conversion not required
			
			e_Amount.setText(currency + " "+cursor.getString(5));
		} else { //conversion to default
			
			final float amount = Float.parseFloat(cursor.getString(5));
			 if(convRate == 0) {
				 e_Amount.setText(currency + " " + amount);
				 e_Amount.setTextColor(Color.RED);			    	
			  } else {
				  e_Amount.setText(defaultCurrency + " " + amount*convRate);
			  }
		}
	
		TextView e_Category = (TextView)view.findViewById(R.id.expenseCategory);
		e_Category.setText(cursor.getString(6));
		
		
	
	}

}
