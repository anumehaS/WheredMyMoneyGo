package com.smc.wheredmymoneygo.expense;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.smc.wheredmymoneygo.Globals;
import com.smc.wheredmymoneygo.MainActivity;
import com.smc.wheredmymoneygo.R;
import com.smc.wheredmymoneygo.category.CategoryCursorLoader;
import com.smc.wheredmymoneygo.currency.CurrencyCursorLoader;
import com.smc.wheredmymoneygo.dbhelpers.ExpenseDbHelper;
import com.smc.wheredmymoneygo.services.CurrencyConverter;
import com.smc.wheredmymoneygo.services.WmmgAlarmManager;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.app.NotificationManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AdapterView.OnItemSelectedListener;

public class ExpenseEditActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor> {
	
	private static ImageButton add, cancel;
	private static Button expenseDate;
	private static Spinner category1, currency, frequency;
	private String e_name;
	private static String e_date, e_date_edit;
	private String e_desc;
	private String e_category1;
	private String e_currency;
	private int e_freq;
	private boolean e_notify = false;
    private static float e_amount;	
    private static float e_convAmt;	
    private static ArrayAdapter<String> dataAdapter1, dataAdapter2;
    private ArrayAdapter<CharSequence> freqadapter;
    static String dateFormat;
    private CheckBox ask;
	boolean loadFinished1 =false;
	boolean loadFinished2 =false;
	boolean loadFinished3 =false;
	
	ExpenseDbHelper dbh;
	CategoryCursorLoader loader;
	boolean valid = true, noChanges =true, fromNoti = false;
	int expId;
	Intent i;
	boolean hasRec;
	
	CurrencyConverter convFrag;
	protected String e_name_edit;
	protected String e_desc_edit;
	protected String e_currency_edit;
	protected float amount;
	protected String e_category1_edit;
	protected String e_freq_edit;
	protected boolean e_notify_edit;
	final static int DATE_DIALOG_ID = 999;
	private static final int REC_EDITED = 01;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.cashflow_add_edit_activity);
	        
	        category1 = (Spinner)findViewById(R.id.category1);
	        currency = (Spinner)findViewById(R.id.inputCurrency);
	        frequency = (Spinner)findViewById(R.id.inputFreq);
	        
	        freqadapter = ArrayAdapter.createFromResource(this,
			        R.array.frequency_spinner_items, android.R.layout.simple_spinner_item);
			freqadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			frequency.setAdapter(freqadapter);
			

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
	        expenseDate = (Button)findViewById(R.id.pickDate);
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
			
			expId = getIntent().getIntExtra("id",0);
			if(getIntent().hasExtra("notify")) { // has been started by notification - then remove notification
				fromNoti = true;
				if (Context.NOTIFICATION_SERVICE!=null) {
			        String ns = Context.NOTIFICATION_SERVICE;
			        NotificationManager nMgr = (NotificationManager) getApplicationContext().getSystemService(ns);
			        nMgr.cancel(0);
			    }
				
			}
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
			dateFormat = prefs.getString("def_dateformat", "MM-dd-yyyy");
			getLoaderManager().initLoader(1,null, this ); //load categories
			getLoaderManager().initLoader(0,null, this ); //get expense with that id.
			getLoaderManager().initLoader(5,null, this ); //get currencies
			i = new Intent (this,com.smc.wheredmymoneygo.expense.ExpenseAlarmManager.class);
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
		 
		 StringBuffer sb = new StringBuffer("Please check the following :\n");
			valid = true;
			noChanges = true;
			
			if(arg0.getId() == R.id.cancel) {
				endActivity("cancelled");
			}
			else if(arg0.getId() == R.id.save) {
				
				//name
				 e_name_edit = ((EditText)findViewById(R.id.inputName)).getText().toString();
				if(e_name_edit.trim().equals("")){
					sb.append("- Expense Name cannot be blank. \n");
					valid = false;
				}
				if(noChanges && !e_name_edit.trim().equals(e_name.trim())){
					noChanges = false;
					
				}
				
				if(!e_date.equals(e_date_edit.trim())){
					noChanges = false;
					hasRec = false;
				}
				
				//amount
				
				String e_amount_edit = ((EditText)findViewById(R.id.inputAmount)).getText().toString();
				if(e_amount_edit.trim().equals("")){
					sb.append("- Expense Amount cannot be blank. \n");
					valid =false;
				}
				else {
					try{
						amount = Float.parseFloat(e_amount_edit);
						
						if(noChanges && amount != e_amount){
							noChanges = false;
							
						}
						
					}catch (Exception e){
						sb.append("- Please enter a valid number in the amount field!\n");
						valid = false;
					}
				}
				
				//currency
				e_currency_edit = ((Spinner) findViewById(R.id.inputCurrency)).getSelectedItem().toString(); 
				if(noChanges && !e_currency_edit.trim().equals(e_currency)){
					noChanges = false;
				
				}
				
				//category 1
				e_category1_edit = category1.getSelectedItem().toString(); 
				if(noChanges && !e_category1_edit.trim().equals(e_category1)){
					noChanges = false;
				
				}
				
				String e_desc_edit = ""; // ((EditText)findViewById(R.id.inputExpenseDescEdit)).getText().toString();
				/*if(e_desc.trim().equals("")) {
					e_desc =" ";
				}
				if(noChanges && !e_desc_edit.trim().equals(e_desc.trim())){
					noChanges = false;
					
				}*/
				String e_freq_edit = frequency.getSelectedItem().toString();
				
				int newFreqPos = frequency.getSelectedItemPosition();
				if(newFreqPos != e_freq){ 
					noChanges = false;			
					hasRec = true;
				}
				
				e_notify_edit = ask.isChecked();
				if(e_notify_edit != e_notify){ 
					noChanges = false;
					hasRec = true;
				}
				
				i.putExtra(WmmgAlarmManager.REC_FREQ,e_freq_edit );
	 			i.putExtra(WmmgAlarmManager.REC_ADD,false );
	 			i.putExtra(WmmgAlarmManager.REC_ISINCOME,false );
				i.putExtra(WmmgAlarmManager.REC_REMOVE, false);
				i.putExtra(WmmgAlarmManager.REC_NOTIFY, e_notify_edit);
				Resources res = getResources();
				String[] freqs = res.getStringArray(R.array.frequency_spinner_items);
				i.putExtra(WmmgAlarmManager.OLD_FREQ,freqs[e_freq] );
				i.putExtra(WmmgAlarmManager.REC_DATE,e_date_edit);
				//i.putExtra("old_notify", e_notify);
				
				if(valid && !noChanges && (!fromNoti || e_notify!=e_notify_edit)) {	
					if(!e_currency_edit.equals(e_currency)) {
						convFrag.getConvertedRate(new CurrencyConverter.ResultListener<Long>() {	
		 					@Override
		 					public void OnSuccess(Long id) {
		 						//endActivity("edited");
		 						startRecActivity(expId);
		 					}	
		 					@Override
		 					public void OnFaiure(float oldRate) {
		 						showConvRateAlert(oldRate);	 	
		 					}  },new Expense(expId,e_name_edit,e_desc_edit,e_date_edit,e_currency_edit,amount,e_category1_edit,e_freq_edit,e_notify_edit),true); 
					} else {
						dbh.updateExpense(new Expense(e_name_edit,e_desc_edit,e_date_edit,e_currency_edit,amount,e_category1_edit,e_convAmt,e_freq_edit,e_notify_edit),expId);
						//endActivity("edited");
						startRecActivity(expId);
					}
            		
				} else if (valid && fromNoti) {
					
					String newDate = getCurrentDate();
					Expense e = new Expense(e_name,e_desc,newDate,e_currency,amount,e_category1,e_convAmt,freqs[e_freq],e_notify);
					dbh.updateExpense(e,expId);
					e.setDate(e_date); 
					e.setFreq("Do not repeat");
					e.setAmount(e_amount);
					
					dbh.addExpense(e);	
					endActivity("edited");
					
				}			
				else
				{
					String title = "Invalid Entries";
					if(noChanges){
						title = "No Changes";
						sb = new StringBuffer("No changes were made");
					}
					AlertDialog.Builder builder = new AlertDialog.Builder(this);
			        builder.setTitle(title)
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
	 
	 protected void showConvRateAlert(float oldRate){
		 final EditText rate = new EditText(ExpenseEditActivity.this);
		 String msg = "We couldn't find a conversion rate! You can use this rate ("+e_currency_edit+" to " + MainActivity.defaultCurrency+": "+oldRate+") from before or enter your own";
			if(oldRate == -1) {
				msg = "Please enter valid a conversion rate from "+e_currency_edit+" to "+ MainActivity.defaultCurrency;
				} else {
					//rate.setText(errCode);
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseEditActivity.this);
		        builder.setTitle("Could not find conversion rate!")
		        .setMessage(msg)
		        .setView(rate)
		        .setCancelable(true)
		        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				  String value = rate.getText().toString();
				  try {
					  float convRate = Float.parseFloat(value);
						dbh.updateExpense(new Expense(e_name_edit,e_desc_edit,e_date_edit,e_currency_edit,amount,e_category1_edit,convRate,e_freq_edit,e_notify_edit),expId);
						//endActivity("edited");
						startRecActivity(expId);
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

	 public String getCurrentDate() {			 
		    Calendar cal = Calendar.getInstance();	    
		    Date  myDate = cal.getTime();
		    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
		    String date = sdf.format(myDate);
		    return date;
		 }

	protected void startRecActivity(long id) {
		 if(hasRec) {
		 i.putExtra("rec_id", id);
		 this.startActivityForResult(i,REC_EDITED);
		 } else {
			 endActivity("edited");
		 }
		
	}
	 
	 @Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (resultCode == RESULT_OK ) {
			  
			  switch(requestCode) {
			  case 01: 
				  endActivity("edited");
			  break;
			  }
		}				  
		
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
	        e_date_edit = dateFormat.format(myDate);
	        dateFormat = new SimpleDateFormat(Globals.USER_DATE_FORMAT,Locale.ENGLISH);     
			expenseDate.setText(dateFormat.format(myDate));
				}
			}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
	    if(id == 1)
			return new CategoryCursorLoader(this);
	    else if (id == 5) 
	    	return new CurrencyCursorLoader(this);
	    else
			return new ExpenseCursorLoader(this,expId,2);	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		
		if(arg0.getId() == 1) {
		//cursor has category data
		loadSpinners(c);
		loadFinished2 =true;
		
		}
		else if(arg0.getId() == 5) {
			loadCurrencies(c);
			loadFinished3 = true;
		}
		else {
		
			if(c == null || c.getCount() == 0) {
				//no entry found with this id
				AlertDialog.Builder builder = new AlertDialog.Builder(ExpenseEditActivity.this);
		        builder.setTitle("Sorry!")
		        .setMessage("This expense no longer exists!")
		        .setCancelable(false)
		        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		                endActivity("cancelled");
		            }
		        });
		        AlertDialog alert = builder.create();
		        alert.show();
		        
			} else {
			
					c.moveToFirst(); 
					
					e_name = c.getString(1);
					e_desc =  c.getString(2);
					e_date = c.getString(3);
					e_date_edit = e_date; //dates are in the format yyyy-MM-dd for storage
				
					String tempdate="";
					SimpleDateFormat sdf = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT);
					SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat);
					try {
						tempdate = sdf1.format((sdf.parse(e_date))); //show the date in user specified format
					} catch (ParseException e) {
						e.printStackTrace();
					} 
					e_currency =  c.getString(4);
					e_amount = c.getFloat(5);
					e_category1 = c.getString(6);
					e_convAmt = c.getFloat(7);
					String freq = c.getString(8);
					Log.d("Edit Activity","Frequency is "+freq);
					for(int i =0;i<freqadapter.getCount();i++) {
						if(freq.equals(freqadapter.getItem(i).toString())) {
							frequency.setSelection(i);
							e_freq = i;
							Log.d("Edit Activity","selection is "+i);
							break;
						}
					}
					String askTemp = c.getString(9);
					if(askTemp.equals("yes")) {
						ask.setChecked(true);
						e_notify = true;
					}
		
					
					EditText name = ((EditText)findViewById(R.id.inputName));
					name.setText(e_name);
					
					((EditText)findViewById(R.id.inputAmount)).setText(Float.toString(e_amount));
					//EditText des = ((EditText)findViewById(R.id.inputExpenseDescEdit));
					//des.setText(e_desc);
					//((EditText)findViewById(R.id.inputExpenseCurrencyEdit)).setText(e_currency);
					((Button)findViewById(R.id.pickDate)).setText(tempdate);
					
					if(fromNoti) {
						name.setEnabled(false);
						//des.setEnabled(false);
						category1.setClickable(false);
						frequency.setClickable(false);
						currency.setClickable(false);
					}
					
					loadFinished1= true;
			}
		}
		
		if(loadFinished1 && loadFinished2 && loadFinished3){ 
			
			int spinnerPosition = dataAdapter1.getPosition(e_category1);
			((Spinner) findViewById(R.id.category1)).setSelection(spinnerPosition);	
			loadFinished1 =false;
			loadFinished2 = false;	
			
			
			spinnerPosition = dataAdapter2.getPosition(e_currency);
			((Spinner) findViewById(R.id.inputCurrency)).setSelection(spinnerPosition);	
			loadFinished3 = false;	
		}
		
			
		
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
	
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
		dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        category1.setAdapter(dataAdapter1);  // attaching data adapter to category spinner
        
	}
	
	private void loadCurrencies(Cursor c)
	{	
		List<String> names = new ArrayList<String>();

		if(c.moveToFirst()) { 
			do{
				names.add(c.getString(0));
			}while(c.moveToNext()); 
		}
		//Toast.makeText(getApplicationContext(), Integer.toString(test.length), Toast.LENGTH_SHORT).show();	
		dataAdapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        currency.setAdapter(dataAdapter2);  // attaching data adapter to category spinner
        
	}


}
