package com.anumeha.wheredmymoneygo.Income;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.anumeha.wheredmymoneygo.Category.CategoryCursorLoader;
import com.anumeha.wheredmymoneygo.Currency.CurrencyCursorLoader;
import com.anumeha.wheredmymoneygo.DBhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.DBhelpers.IncomeDbHelper;
import com.anumeha.wheredmymoneygo.Expense.Expense;
import com.anumeha.wheredmymoneygo.Expense.ExpenseAddActivity;
import com.anumeha.wheredmymoneygo.Income.Income;
import com.anumeha.wheredmymoneygo.Services.CurrencyConverter;
import com.anumeha.wheredmymoneygo.Source.SourceCursorLoader;
import com.example.wheredmymoneygo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class IncomeAddActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor> {
	
	private Button add, cancel;
	private static TextView incomeDate;
	private Spinner source, currency, frequency;
	private static String i_date;
	IncomeDbHelper dbh;
	CategoryCursorLoader loader;
	boolean valid = true;
	CurrencyConverter convFrag;
	CheckBox ask;
	Intent i;
	boolean hasRec = false;
	String i_name,i_desc,i_currency,freq,i_source;
	float amount;
	boolean notify;
	final static int DATE_DIALOG_ID = 999;
	private static final int REC_ADDED = 0;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.income_add_activity);
	        
	        
	        source = (Spinner)findViewById(R.id.incSource);
	        currency = (Spinner)findViewById(R.id.inputIncomeCurrency);
	        frequency = (Spinner)findViewById(R.id.inputIncomeFreq);
	        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
			        R.array.frequency_spinner_items, android.R.layout.simple_spinner_item);
			adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			frequency.setAdapter(adapter);
			frequency.setSelection(0);
	        
			ask = (CheckBox)findViewById(R.id.inputIncNotify); 

	        setCurrentDate();
	        add = (Button)findViewById(R.id.incAddSubmit);
			add.setOnClickListener(this);
			cancel = (Button)findViewById(R.id.incAddCancel);
			cancel.setOnClickListener(this);
			dbh = new IncomeDbHelper(this);
			FragmentManager fragmentManager = getFragmentManager();
			convFrag = (CurrencyConverter) fragmentManager                
			                      .findFragmentByTag(CurrencyConverter.TAG);
			
			if (convFrag == null) {
	            convFrag = new CurrencyConverter();
	            fragmentManager.beginTransaction().add(convFrag,
	                    CurrencyConverter.TAG).commit();
	        }
			
			getLoaderManager().initLoader(1,null, this );
			getLoaderManager().initLoader(4,null, this );
			i = new Intent (this,com.anumeha.wheredmymoneygo.Income.IncomeAlarmManager.class);
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
			
			
			if(arg0.getId() == R.id.incAddCancel) {
				endActivity("cancelled");
			}
			else if(arg0.getId() == R.id.incAddSubmit) {
				
				//name
				i_name = ((EditText)findViewById(R.id.inputIncomeName)).getText().toString();
				if(i_name.trim().equals("")){
					sb.append("- Income Name cannot be blank. \n");
					valid = false;
				}
				
				//amount
				amount = 0;
				String i_amount = ((EditText)findViewById(R.id.inputIncomeAmount)).getText().toString();
				if(i_amount.trim().equals("")){
					sb.append("- Income Amount cannot be blank. \n");
					valid =false;
				}
				else {
					try{
						amount = Float.parseFloat(i_amount);
						
					}catch (Exception e){
						sb.append("- Please enter a valid number in the amount field!\n");
						valid = false;
					}
				}
				
				//currency
				i_currency = ((Spinner) findViewById(R.id.inputIncomeCurrency)).getSelectedItem().toString(); 
				
				//source
				i_source = ((Spinner) findViewById(R.id.incSource)).getSelectedItem().toString(); 
				
				i_desc = ((EditText)findViewById(R.id.inputIncomeDesc)).getText().toString();
				if(i_desc.trim().equals("")) {
					i_desc =" ";
				}
				freq = frequency.getSelectedItem().toString(); 
				if(frequency.getSelectedItemPosition() > 0) {
					hasRec = true;
				}
				notify = ask.isChecked();

		 			i.putExtra("rec_freq",freq );
		 			i.putExtra("rec_add",true );
		 			i.putExtra("rec_isIncome",true );
					i.putExtra("rec_rem", false);
					i.putExtra("rec_notify", notify);
					i.putExtra("old_freq","" );
				if(valid) {	
					convFrag.getConvertedRate(new CurrencyConverter.ResultListener<Long>() {	
	 					@Override
	 					public void OnSuccess(Long id) {
	 						startRecActivity(id);
	 					}	
	 					@Override
	 					public void OnFaiure(int errCode) {
	 						endActivity("added");
	 					}  },new Income(i_name,i_desc,i_date,i_currency,amount,i_source,freq,notify),false);     
            		
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
		 i.putExtra("rec_id", id);
		 this.startActivityForResult(i,REC_ADDED);
		 } else {
			 endActivity("added");
		 }
		
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
		incomeDate = (TextView) findViewById(R.id.incomeDate);			
		Date myDate;
	    Calendar cal = Calendar.getInstance();	    
	    myDate = cal.getTime();
	    //set Date into SQLIte date format
	    SimpleDateFormat sdf;
	    // set current date into textview
	    sdf = new SimpleDateFormat("MMMM dd, yyyy");
		incomeDate.setText(sdf.format(myDate));
		
		sdf = new SimpleDateFormat("yyyy-MM-dd"); 
	    i_date = sdf.format(myDate);
	 }

	 protected void showConvRateAlert(int errCode){
		 final EditText rate = new EditText(IncomeAddActivity.this);
			String msg = "We couldn't find a conversion rate! You can use this rate from before or enter your own";
			if(errCode == -1) {
				msg = "Please enter valid a conversion rate from your currency choice to USD";
				} else {
					//rate.setText(errCode);
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(IncomeAddActivity.this);
		        builder.setTitle("Could not find conversion rate!")
		        .setMessage(msg)
		        .setView(rate)
		        .setCancelable(true)
		        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				  String value = rate.getText().toString();
				  try {
					  float convRate = Float.parseFloat(value);
					  IncomeDbHelper incDb= new IncomeDbHelper(IncomeAddActivity.this);
					  long temp = incDb.addIncome(new Income(i_name,i_desc,i_date,i_currency,amount,i_source,convRate,freq,notify));
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
	        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM dd, yyyy",Locale.ENGLISH); 
	        i_date = dateFormat.format(myDate);			      
			incomeDate.setText(i_date);
			dateFormat = new SimpleDateFormat("yyyy-MM-dd");
			i_date = dateFormat.format(myDate);
			
		}
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
		if(id == 1) {
			return new SourceCursorLoader(this);
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
        source.setAdapter(dataAdapter);  // attaching data adapter to category spinner
        
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
