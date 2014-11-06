package com.anumeha.wheredmymoneygo.expense;



import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import com.anumeha.wheredmymoneygo.Globals;
import com.anumeha.wheredmymoneygo.category.CategoryCursorLoader;
import com.anumeha.wheredmymoneygo.currency.CurrencyCursorLoader;
import com.anumeha.wheredmymoneygo.dbhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.services.CurrencyConverter;
import com.anumeha.wheredmymoneygo.services.WmmgAlarmManager;
import com.example.wheredmymoneygo.*;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.app.DialogFragment;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class ExpenseAddActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor>{
	
	private ImageButton add, cancel;
	private static Button expenseDate;
	private Spinner category1, currency, frequency;
	private static String e_date;
	ExpenseDbHelper dbh;
	CategoryCursorLoader loader;
	boolean valid = true;
	CurrencyConverter convFrag;
	CheckBox ask;
	Intent i;
	boolean hasRec = false;
	String e_name,e_desc,e_currency,e_category1,freq;
	float amount;
	boolean notify;
	
	final static int DATE_DIALOG_ID = 999;
	private static final int REC_ADDED = 0;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.cashflow_add_edit_activity);
	        
	        
	        category1 = (Spinner)findViewById(R.id.category1);
	        currency = (Spinner)findViewById(R.id.inputCurrency);
	        frequency = (Spinner)findViewById(R.id.inputFreq);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.frequency_spinner_items, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			frequency.setAdapter(adapter);
			
			ask = (CheckBox)findViewById(R.id.inputNotify); 
			frequency.setOnItemSelectedListener(new OnItemSelectedListener (){

				@Override
				public void onItemSelected(AdapterView<?> arg0, View arg1,
						int selection, long arg3) {
					
					if(selection == 0) {
						ask.setVisibility(View.GONE);
					} else {
						ask.setVisibility(View.VISIBLE);
					}
				}

				@Override
				public void onNothingSelected(AdapterView<?> arg0) {
					
					
				}
				
			});
			frequency.setSelection(0);
			
	        setCurrentDate();
	        add = (ImageButton)findViewById(R.id.save);
			add.setOnClickListener(this);
			cancel = (ImageButton)findViewById(R.id.cancel);
			cancel.setOnClickListener(this);
			dbh = new ExpenseDbHelper(this);
			FragmentManager fragmentManager = getFragmentManager();
			convFrag = (CurrencyConverter) fragmentManager                
			                      .findFragmentByTag(CurrencyConverter.TAG);
			
			if (convFrag == null) {
	            convFrag = new CurrencyConverter();
	            fragmentManager.beginTransaction().add(convFrag,
	                    CurrencyConverter.TAG).commit();
	        }
			
			getLoaderManager().initLoader(1,null, this ); // 1 for category
			getLoaderManager().initLoader(5,null, this ); //5 for currency
		
			i = new Intent (this,com.anumeha.wheredmymoneygo.expense.ExpenseAlarmManager.class);
	    }
	 
	 public void endActivity(String res) {	
		 
		 Intent data = new Intent();
		 data.putExtra("result",res);
		  // Activity finished ok, return the data
		  setResult(RESULT_OK, data);	 		 
			this.finish();			
		}
	 
	 @Override
	 public void onClick(View arg0) {
		 
		 valid = true;
		 StringBuffer sb = new StringBuffer("Please check the following :\n");
			
			
			if(arg0.getId() == R.id.cancel) {
				endActivity("cancelled");
			}
			else if(arg0.getId() == R.id.save) {
				
				//name
				e_name = ((EditText)findViewById(R.id.inputName)).getText().toString();
				if(e_name.trim().equals("")){
					sb.append("- Expense Name cannot be blank. \n");
					valid = false;
				}
				
				//amount
				//float amount = 0;
				String e_amount = ((EditText)findViewById(R.id.inputAmount)).getText().toString();
				if(e_amount.trim().equals("")){
					sb.append("- Expense Amount cannot be blank. \n");
					valid =false;
				}
				else {
					try{
						amount = Float.parseFloat(e_amount);
						
					}catch (Exception e){
						sb.append("- Please enter a valid number in the amount field!\n");
						valid = false;
					}
				}
				
				//currency
				e_currency = currency.getSelectedItem().toString(); 
				
				
				//category 1
				e_category1 = category1.getSelectedItem().toString(); 
				
			    e_desc = ""; //((EditText)findViewById(R.id.inputExpenseDesc)).getText().toString();
				/*if(e_desc.trim().equals("")) {
					e_desc =" ";
				}*/
				
				freq = frequency.getSelectedItem().toString(); 
				if(frequency.getSelectedItemPosition() > 0) {
					hasRec = true;
				}
				notify = ask.isChecked();
			
					
		 			i.putExtra(WmmgAlarmManager.REC_FREQ,freq );
		 			i.putExtra(WmmgAlarmManager.REC_ADD,true );
		 			i.putExtra(WmmgAlarmManager.REC_ISINCOME,false );
					i.putExtra(WmmgAlarmManager.REC_REMOVE, false);
					i.putExtra(WmmgAlarmManager.REC_NOTIFY, notify);
					i.putExtra(WmmgAlarmManager.OLD_FREQ,"" );
					i.putExtra(WmmgAlarmManager.REC_DATE,e_date );
					
					
				if(valid) {	
					convFrag.getConvertedRate(new CurrencyConverter.ResultListener<Long>() {	
	 					@Override
	 					public void OnSuccess(Long id) {
	 						//endActivity("added");
	 						//start activity for adding rec
	 						startRecActivity(id);
	 		
	 					}	
	 					@Override
	 					public void OnFaiure(int errCode) {
	 						//add code for asking user to input rate manually
	 						showConvRateAlert(errCode);	 						
	 						
	 					}  },new Expense(e_name,e_desc,e_date,e_currency,amount,e_category1,freq,notify),false);          		
				}
				
				else
				{
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
			        builder.setTitle("Invalid entries")
			        .setMessage(sb.toString())
			        .setCancelable(false)
			        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
			            public void onClick(DialogInterface dialog, int id) {
			                dialog.cancel();
			            }
			        });
			        AlertDialog alert = builder.create();
			        alert.show();
					
				}				
			}	 
	 }

	 
	 protected void startRecActivity(long id) {
		 if(hasRec) {
		 i.putExtra(WmmgAlarmManager.REC_ID, id);
		 this.startActivityForResult(i,REC_ADDED);
		 } else {
			 endActivity("added");
		 }
		
	}
	
	 protected void showConvRateAlert(int errCode){
		 final EditText rate = new EditText(ExpenseAddActivity.this);
			String msg = "We couldn't find a conversion rate! You can use this rate from before or enter your own";
			if(errCode == -1) {
				msg = "Please enter valid a conversion rate from your currency choice to USD";
				} else {
					//rate.setText(errCode);
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseAddActivity.this);
		        builder.setTitle("Could not find conversion rate!")
		        .setMessage(msg)
		        .setView(rate)
		        .setCancelable(true)
		        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				  String value = rate.getText().toString();
				  try {
					  float convRate = Float.parseFloat(value);
					  ExpenseDbHelper expDb= new ExpenseDbHelper(ExpenseAddActivity.this);
					  long temp = expDb.addExpense(new Expense(e_name,e_desc,e_date,e_currency,amount,e_category1,convRate,freq,notify));
					  startRecActivity(temp);
				  } catch (Exception e) {  
					  showConvRateAlert(-1);
				  }
				  }
				})
		        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                endActivity("cancelled");
		            }
		        });
		       
		        AlertDialog alert = builder.create();
		        
		        alert.show();	        
		    
	 }
	 
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (resultCode == RESULT_OK ) {
			  
			  switch(requestCode) {
			  case 00: 
				  endActivity("added");
			  break;
			  }
		}				  
		
	  }

	public void setCurrentDate() {			 
		expenseDate = (Button) findViewById(R.id.pickDate);			
		Date myDate;
	    Calendar cal = Calendar.getInstance();	    
	    myDate = cal.getTime();
	    SimpleDateFormat sdf;
	    // set current date into textview
	    sdf = new SimpleDateFormat(Globals.USER_DATE_FORMAT);
		expenseDate.setText(sdf.format(myDate));
		
		sdf = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT); 
	    e_date = sdf.format(myDate);
	 }
	 
	 public void showDatePickerDialog(View view) {
			DialogFragment newFragment = new SelectDateFragment();
			newFragment.show(getFragmentManager(), "DatePicker");
	 }
	
	 public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			final Calendar calendar = Calendar.getInstance();
			int yy = calendar.get(Calendar.YEAR);
			int mm = calendar.get(Calendar.MONTH);
			int dd = calendar.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, yy, mm, dd);
		}
				 
		public void onDateSet(DatePicker view, int yy, int mm, int dd) {
					
			Date myDate;
	        Calendar cal = Calendar.getInstance();
	        cal.set(Calendar.MONTH, mm);
	        cal.set(Calendar.DATE, dd);
	        cal.set(Calendar.YEAR, yy);
	        myDate = cal.getTime();
	        //set Date into SQLIte date format
	        SimpleDateFormat dateFormat = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT); 
	        e_date = dateFormat.format(myDate);
			      
	        dateFormat = new SimpleDateFormat(Globals.USER_DATE_FORMAT);
			expenseDate.setText(dateFormat.format(myDate));
				}
			}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		if(id == 1) {
			return new CategoryCursorLoader(this);
		} else {
			return new CurrencyCursorLoader(this);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		
		
		if(arg0.getId() == 1) {
			//cursor has category data
			loadSpinners(c);
		} else {
			loadCurrencies(c);
		}
		
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void loadSpinners(Cursor c)
	{	
		List<String> names = new ArrayList<String>();

		if(c.moveToFirst()) { 
			do{
				names.add(c.getString(1));
			}while(c.moveToNext()); 
		}
		//Toast.makeText(getApplicationContext(), Integer.toString(test.length), Toast.LENGTH_SHORT).show();	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        category1.setAdapter(dataAdapter);  // attaching data adapter to category spinner
        
	}
	
	private void loadCurrencies(Cursor c)
	{	
		List<String> ids = new ArrayList<String>();

		if(c.moveToFirst()) { 
			do{
				ids.add(c.getString(0));
			}while(c.moveToNext()); 
		}	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ids);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        currency.setAdapter(dataAdapter);  // attaching data adapter toc urrency spinner
        
	}


}
