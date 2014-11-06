package com.anumeha.wheredmymoneygo.income;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.anumeha.wheredmymoneygo.Globals;
import com.example.wheredmymoneygo.R;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class IncomeCursorAdapter extends ResourceCursorAdapter {
	
SharedPreferences prefs;
String defaultCurrency;
	
	public IncomeCursorAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		defaultCurrency = prefs.getString("def_currency", "USD");
	
	}
	
	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		String dateFormat, currency;
		Date tempDate = null;
		SimpleDateFormat sdf;		
		
		//set income id -- invisible but will be used for editing
		TextView e_Id = (TextView)view.findViewById(R.id.incomeId);
		e_Id.setText(cursor.getString(0));
		
		//set expense frequency-- invisible but will be used for editing
		TextView e_Freq = (TextView)view.findViewById(R.id.incomeFreq);
		e_Freq.setText(cursor.getString(8));
		//set expense notify-- invisible but will be used for editing
		TextView e_Notify = (TextView)view.findViewById(R.id.incomeNotify);
		e_Notify.setText(cursor.getString(9));
		
		//set income name
		TextView e_Name = (TextView)view.findViewById(R.id.incomeName);
		e_Name.setText(cursor.getString(1));
		
		//set income date--after adapting it to the user's prefered format
		TextView e_date = (TextView)view.findViewById(R.id.incomeDate);		
		//adapt date to user's preference 
		dateFormat = prefs.getString(Globals.DEF_DATEFORMAT, "MMMM dd, yyyy");
		sdf = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT); // the standard format for storage
		try {
			tempDate = sdf.parse(cursor.getString(3));
		} catch (ParseException e) {
			
			e.printStackTrace();
		}
		sdf = new SimpleDateFormat(dateFormat);
		e_date.setText(sdf.format(tempDate)); 
		
		//show amount after appending currency symbol
		TextView i_Amount = (TextView)view.findViewById(R.id.incomeAmount);
		currency = cursor.getString(4);
		
		final float convRate = Float.parseFloat(cursor.getString(7));		   
			if(prefs.getString(Globals.INC_CONV, "off").equals("off") || currency.equals(defaultCurrency)) { //conversion not required
				
				i_Amount.setText(currency + " "+cursor.getString(5));
			} else { //conversion to default
				
				final float amount = Float.parseFloat(cursor.getString(5));
				 if(convRate == -1) {
					 i_Amount.setText(currency + " " + amount);
					 i_Amount.setTextColor(Color.RED);			    	
				  } else {
					  i_Amount.setText(defaultCurrency + " " + amount*convRate);
				  }
			}
		
		
		//set the source
		TextView e_Category = (TextView)view.findViewById(R.id.incomeSource);
		e_Category.setText(cursor.getString(6));
		
		
	
	}

}
