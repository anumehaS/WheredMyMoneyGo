package com.anumeha.wheredmymoneygo.Expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import com.anumeha.wheredmymoneygo.DBhelpers.CurrencyDbHelper;
import com.anumeha.wheredmymoneygo.Services.CurrencyConverter;
import com.example.wheredmymoneygo.R;
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
	
	public ExpenseCursorAdapter(Context context, int layout, Cursor c, int flags) {
		super(context, layout, c, flags);
		prefs = PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext());
		defaultCurrency = prefs.getString("def_currency", "USD");
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
		System.out.println("id in cursor" + id);
		e_Id.setText(id);
		
		//set expense frequency-- invisible but will be used for editing
		TextView e_Freq = (TextView)view.findViewById(R.id.expenseFreq);
		e_Freq.setText(cursor.getString(7));
		//set expense notify-- invisible but will be used for editing
		TextView e_Notify = (TextView)view.findViewById(R.id.expenseNotify);
		e_Notify.setText(cursor.getString(8));
		
		//set expense name
		TextView e_Name = (TextView)view.findViewById(R.id.expenseName);
		e_Name.setText(cursor.getString(1));
		
		//set expense date--after adapting it to the user's prefered format
		TextView e_date = (TextView)view.findViewById(R.id.expenseDate);		
		//adapt date to user's preference 
		dateFormat = prefs.getString("def_dateformat", "MMMM dd, yyyy");
		sdf = new SimpleDateFormat("yyyy-MM-dd");
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
	    
	   
		if(prefs.getString("exp_conv", "off").equals("off") || currency.equals(defaultCurrency)) { //conversion not required
			
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
		//set the category
		TextView e_Category = (TextView)view.findViewById(R.id.expenseCategory);
		e_Category.setText(cursor.getString(6));
		
		
	
	}

}
