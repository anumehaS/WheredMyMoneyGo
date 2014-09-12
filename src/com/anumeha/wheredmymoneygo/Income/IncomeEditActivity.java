package com.anumeha.wheredmymoneygo.Income;

import java.text.ParseException;
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
import com.anumeha.wheredmymoneygo.Expense.ExpenseEditActivity;
import com.anumeha.wheredmymoneygo.Income.Income;
import com.anumeha.wheredmymoneygo.Income.IncomeCursorLoader;
import com.anumeha.wheredmymoneygo.Services.CurrencyConverter;
import com.anumeha.wheredmymoneygo.Source.SourceCursorLoader;
import com.example.wheredmymoneygo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.NotificationManager;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

public class IncomeEditActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor> {
	
	private static Button add, cancel;
	private static TextView incomeDate;
	private static Spinner source,currency,frequency;
	private String i_name;
	private static String i_date, i_date_edit;
	private String i_desc;
	private String i_source;
	private String i_currency;
	private int i_freq;
	private boolean i_notify;
    private static float i_amount;	
    private static float i_convAmt;	
    private static ArrayAdapter<String> dataAdapter1, dataAdapter2;
    private ArrayAdapter<CharSequence> freqadapter;
    private CheckBox ask;
    static String dateFormat;
	boolean loadFinished1 =false;
	boolean loadFinished2 =false;
	boolean loadFinished3 =false;
	String i_name_edit,i_desc_edit,i_currency_edit,i_source_edit,i_freq_edit;
	boolean i_notify_edit;
	float amount;
	
	IncomeDbHelper dbh;
	CategoryCursorLoader loader;
	boolean valid = true, noChanges =true;
	int incId;
	CurrencyConverter convFrag;
	Intent i;
	boolean hasRec, fromNoti = false;
	
	final static int DATE_DIALOG_ID = 999;
	private static final int REC_EDITED = 01;
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.income_edit_activity);
        
	        source = (Spinner)findViewById(R.id.incSourceEdit);
	        currency = (Spinner)findViewById(R.id.inputIncomeCurrencyEdit);
	        
	        frequency = (Spinner)findViewById(R.id.inputIncomeFreqEdit);
	        
	        freqadapter = ArrayAdapter.createFromResource(this,
			        R.array.frequency_spinner_items, android.R.layout.simple_spinner_item);
			freqadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
			frequency.setAdapter(freqadapter);

	        ask = (CheckBox)findViewById(R.id.inputIncNotifyEdit); 
	        incomeDate = (TextView)findViewById(R.id.incomeDateEdit);

	        add = (Button)findViewById(R.id.incSaveEdit);
			add.setOnClickListener(this);
			cancel = (Button)findViewById(R.id.incCancelEdit);
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
			
			incId = getIntent().getIntExtra("id",0);
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
			getLoaderManager().initLoader(1,null, this ); //load sources
			getLoaderManager().initLoader(3,null, this ); //get income with that id.
			getLoaderManager().initLoader(5,null, this ); //get currencies
			
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
		 
		 StringBuffer sb = new StringBuffer("Please check the following :\n");
			valid = true;
			noChanges = true;
			
			if(arg0.getId() == R.id.incCancelEdit) {
				endActivity("cancelled");
			}
			else if(arg0.getId() == R.id.incSaveEdit) {
				
				//name
				String i_name_edit = ((EditText)findViewById(R.id.inputIncomeNameEdit)).getText().toString();
				if(i_name_edit.trim().equals("")){
					sb.append("- Income Name cannot be blank. \n");
					valid = false;
				}
				if(noChanges && !i_name_edit.trim().equals(i_name.trim())){
					noChanges = false;
					
				}
				
				if(!i_date.equals(i_date_edit.trim())){
					noChanges = false;
					
				}
				
				//amount
				float amount = 0;
				String i_amount_edit = ((EditText)findViewById(R.id.inputIncomeAmountEdit)).getText().toString();
				if(i_amount_edit.trim().equals("")){
					sb.append("- Income Amount cannot be blank. \n");
					valid =false;
				}
				else {
					try{
						amount = Float.parseFloat(i_amount_edit);
						
						if(noChanges && amount != i_amount){
							noChanges = false;
							
						}
						
					}catch (Exception e){
						sb.append("- Please enter a valid number in the amount field!\n");
						valid = false;
					}
				}
				
				//currency
				String i_currency_edit =((Spinner) findViewById(R.id.inputIncomeCurrencyEdit)).getSelectedItem().toString(); 
				if(noChanges && !i_currency_edit.trim().equals(i_currency)){
					noChanges = false;
				
				}
				
				//source
				String i_source_edit = ((Spinner) findViewById(R.id.incSourceEdit)).getSelectedItem().toString(); 
				if(noChanges && !i_source_edit.trim().equals(i_source)){
					noChanges = false;
				
				}
				
				String i_desc_edit = ((EditText)findViewById(R.id.inputIncomeDescEdit)).getText().toString();
				if(i_desc.trim().equals("")) {
					i_desc =" ";
				}
				if(noChanges && !i_desc_edit.trim().equals(i_desc.trim())){
					noChanges = false;
					
				}
				String i_freq_edit = frequency.getSelectedItem().toString();
				if(frequency.getSelectedItemPosition()!= i_freq) noChanges = false;
				if(frequency.getSelectedItemPosition() > 0) {
					hasRec = true;
				}
				
				boolean i_notify_edit = ask.isChecked();
				if(i_notify_edit != i_notify) noChanges = false;
				
				i.putExtra("rec_freq",i_freq_edit );
	 			i.putExtra("rec_add",false );
	 			i.putExtra("rec_isIncome",true );
				i.putExtra("rec_rem", false);
				i.putExtra("rec_notify", i_notify_edit);
				Resources res = getResources();
				String[] freqs = res.getStringArray(R.array.frequency_spinner_items);
				i.putExtra("old_freq",freqs[i_freq] );
				i.putExtra("old_notify", i_notify);
				
				if(valid && !noChanges && (!fromNoti || i_notify!=i_notify_edit)) {	
					if(!i_currency_edit.equals(i_currency)) {
					convFrag.getConvertedRate(new CurrencyConverter.ResultListener<Long>() {	
	 					@Override
	 					public void OnSuccess(Long id) {
	 						//endActivity("edited");
	 						startRecActivity(incId);
	 					}	
	 					@Override
	 					public void OnFaiure(int errCode) {
	 						showConvRateAlert(errCode);	
	 						//endActivity("edited");
	 					}  },new Income(incId,i_name_edit,i_desc_edit,i_date_edit,i_currency_edit,amount,i_source_edit,i_freq_edit,i_notify_edit),true); 
					} else {
						dbh.updateIncome(new Income(i_name_edit,i_desc_edit,i_date_edit,i_currency_edit,amount,i_source_edit,i_convAmt,i_freq_edit,i_notify_edit),incId);
						//endActivity("edited");
						startRecActivity(incId);
					}
				} else if (valid && fromNoti) {
					String newDate = getCurrentDate();
					Income inc = new Income(i_name,i_desc,newDate,i_currency,amount,i_source,i_convAmt,freqs[i_freq],i_notify);
					dbh.updateIncome(inc,incId);
					inc.setDate(i_date); 
					inc.setFreq("Do not repeat");
					inc.setAmount(i_amount);
					
					dbh.addIncome(inc);	
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
	 
	 protected void showConvRateAlert(int errCode){
		 final EditText rate = new EditText(IncomeEditActivity.this);
			String msg = "We couldn't find a conversion rate! You can use this rate from before or enter your own";
			if(errCode == -1) {
				msg = "Please enter valid a conversion rate from your currency choice to USD";
				} else {
					//rate.setText(errCode);
				}

				AlertDialog.Builder builder = new AlertDialog.Builder(IncomeEditActivity.this);
		        builder.setTitle("Could not find conversion rate!")
		        .setMessage(msg)
		        .setView(rate)
		        .setCancelable(true)
		        .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int id) {
				  String value = rate.getText().toString();
				  try {
					  float convRate = Float.parseFloat(value);
					  ExpenseDbHelper expDb= new ExpenseDbHelper(IncomeEditActivity.this);
						dbh.updateIncome(new Income(i_name_edit,i_desc_edit,i_date_edit,i_currency_edit,amount,i_source_edit,convRate,i_freq_edit,i_notify_edit),incId);
						//endActivity("edited");
						startRecActivity(incId);
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
	        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd"); 
	        i_date_edit = dateFormat.format(myDate);
	        dateFormat = new SimpleDateFormat("MMMM dd, yyyy",Locale.ENGLISH);     
			incomeDate.setText(dateFormat.format(myDate));
				}
			}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		
	    if(id == 1)
			return new SourceCursorLoader(this);
	    else if (id == 5) 
	    	return new CurrencyCursorLoader(this);
	    else
			return new IncomeCursorLoader(this,incId,2);	
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		
		if(arg0.getId() == 1) {
		//cursor has category data
		loadSpinners(c);
		loadFinished2 =true;
		
		} else if(arg0.getId() == 5) {
			loadCurrencies(c);
			loadFinished3 = true;
		}
		else {
			
			if(c == null || c.getCount() == 0) {
				//no entry found with this id
				AlertDialog.Builder builder = new AlertDialog.Builder(IncomeEditActivity.this);
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
		        
			}
			else {
		c.moveToFirst(); 
				
				i_name = c.getString(1);
				i_desc =  c.getString(2);
				i_date = c.getString(3);
				i_date_edit = i_date;
			
				String tempdate="";
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				SimpleDateFormat sdf1 = new SimpleDateFormat(dateFormat);
				try {
					tempdate = sdf1.format((sdf.parse(i_date))); //show the date in user specified format
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} 
				i_currency =  c.getString(4);
				i_amount = c.getFloat(5);
				i_source = c.getString(6);
				i_convAmt = c.getFloat(7);
				String freq = c.getString(8);
				for(int i =0;i<freqadapter.getCount();i++) {
					if(freq.equals(freqadapter.getItem(i))) {
						frequency.setSelection(i);
						i_freq = i;
						break;
					}
				}
				String askTemp = c.getString(9);
				if(askTemp.equals("yes")) {
					ask.setChecked(true);
					i_notify = true;
				}
				
				((EditText)findViewById(R.id.inputIncomeNameEdit)).setText(i_name);
				((EditText)findViewById(R.id.inputIncomeAmountEdit)).setText(Float.toString(i_amount));
				((EditText)findViewById(R.id.inputIncomeDescEdit)).setText(i_desc);
				//((EditText)findViewById(R.id.inputIncomeCurrencyEdit)).setText(i_currency);
				((TextView)findViewById(R.id.incomeDateEdit)).setText(tempdate);			
				
				loadFinished1= true;
			}
		}
		
		if(loadFinished1 && loadFinished2&& loadFinished3){
			
			int spinnerPosition = dataAdapter1.getPosition(i_source);
			((Spinner) findViewById(R.id.incSourceEdit)).setSelection(spinnerPosition);	
			spinnerPosition = dataAdapter2.getPosition(i_currency);
			((Spinner) findViewById(R.id.inputIncomeCurrencyEdit)).setSelection(spinnerPosition);	
			loadFinished1 =false;
			loadFinished2 = false;	
			loadFinished3 = false;
			
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
		dataAdapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        source.setAdapter(dataAdapter1);  // attaching data adapter to category spinner
        
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
