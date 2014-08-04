package com.anumeha.wheredmymoneygo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.anumeha.wheredmymoneygo.Category.CategoryCursorLoader;
import com.anumeha.wheredmymoneygo.Services.DefaultPreferenceAccess;
import com.anumeha.wheredmymoneygo.Services.DefaultPreferenceAccess.PrefAddedListener;
import com.anumeha.wheredmymoneygo.Services.DefaultPreferenceAccess.PrefLoadedListener;
import com.anumeha.wheredmymoneygo.Source.SourceCursorLoader;
import com.example.wheredmymoneygo.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;

public class OptionsDialog extends Activity implements OnClickListener, LoaderCallbacks<Cursor>{
	
	private String currentTab;
	private static Spinner sortOrder,filters;
	private CheckBox convert;
	private static boolean dateRange, startDateClicked = true;
	private RadioGroup viewBy;
	private LinearLayout dateRangeLayout;
	private static String startDateVal, endDateVal;
	String filterVal, sortOrderVal, orderByVal,  viewByVal, convVal;
	String filterKey, sortOrderKey, orderByKey,  viewByKey, startDateKey, endDateKey,convKey;
	DefaultPreferenceAccess prefAccess; 
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);		
		prefAccess = new DefaultPreferenceAccess();
		
		

		currentTab = getIntent().getStringExtra("currentTab");	
		
		if(currentTab.equals(MainActivity.EXPENSE_TAG)) {
			  filterKey = "exp_filter"; 
			  sortOrderKey = "exp_cur_sortOrder";
			  orderByKey = "exp_cur_orderBy";
	   		  viewByKey = "exp_viewBy";
	   		  startDateKey = "exp_startDate";
	   		  endDateKey = "exp_endDate";
	   		  convKey = "exp_conv";
		} else {
			  filterKey = "inc_filter";
			  sortOrderKey = "inc_cur_sortOrder";
			  orderByKey = "inc_cur_orderBy";
			  viewByKey = "inc_viewBy";
			  startDateKey = "inc_startDate";
	   		  endDateKey = "inc_endDate";
	   		  convKey = "inc_conv";
		}
		
		List<String> keys = new ArrayList<String>();
		keys.add(filterKey);
		keys.add(sortOrderKey); 
		keys.add(orderByKey); 
		keys.add(viewByKey); 
		keys.add(startDateKey);
		keys.add(endDateKey);
		keys.add(convKey);
		
		prefAccess.getValues(new PrefLoadedListener<List<String>>(){

			@Override
			public void OnSuccess(List<String> data) {
				
				OptionsDialog.this.setContentView(R.layout.options_dialog);
				filters = (Spinner)findViewById(R.id.filter);
				
				dateRangeLayout = (LinearLayout)findViewById(R.id.dateRangeLayout);
				viewBy = (RadioGroup) findViewById(R.id.radioViewin);
				viewBy.setOnCheckedChangeListener(new OnCheckedChangeListener(){

					@Override
					public void onCheckedChanged(RadioGroup group, int selected) {
						
						if(selected == R.id.radioAll) {
							dateRangeLayout.setVisibility(View.GONE);
							dateRange = false;
						} else {
							dateRangeLayout.setVisibility(View.VISIBLE);
							dateRange = true;
						}	
					}
					
				});
					
				convert = (CheckBox)findViewById(R.id.convertCur);
				
				sortOrder = (Spinner)findViewById(R.id.sortOrder);
				ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(OptionsDialog.this,
				        R.array.sort_spinner_items, android.R.layout.simple_spinner_item);
				adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
				sortOrder.setAdapter(adapter);
				
				OptionsDialog.this.getLoaderManager().initLoader(1,null, OptionsDialog.this ); // 1 for category
				OptionsDialog.this.getLoaderManager().initLoader(2,null, OptionsDialog.this ); // 2 for sources
			}

			@Override
			public void OnFaiure(int errCode) {
				
			}
			
		}, keys, this);
		
	}
	
	@Override
	public void onClick(View v) {	 
	      Button b = (Button)v;
 
		if(v.getId() == R.id.convertCur) {
			 SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		      Editor editor = prefs.edit();
			
			if(currentTab.equals(MainActivity.EXPENSE_TAG)) {
				if(prefs.getString("exp_conv", "off").equals("off")) {
					editor.putString("exp_conv", "on");
					b.setText("convert to Original");					
				}
				else {
					editor.putString("exp_conv", "off");
					b.setText("convert to Default");
				}
				editor.commit();
				/*ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag("expense");
				exp.restartLoader();*/
			} else {
				if(prefs.getString("inc_conv", "off").equals("off")) {
					editor.putString("inc_conv", "on");
					b.setText("convert to Original");
				}
				else {
					editor.putString("inc_conv", "off");
					b.setText("convert to Default");					
				}
				editor.commit();
				/*	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag("income");
					inc.restartLoader();*/
			}
			
		}
		
	}
	
	public void cancelOptions(View v){
		OptionsDialog.this.finish();
	}
	
	public void saveOptions(View v){
		
		//save filters , sort order, category and if in date range then date range
		
		String item = (String)sortOrder.getSelectedItem();
		String sortBy = item.split(" ")[0].trim();
		String order = item.split(" ")[2].trim();
		
		List<String> keys = new ArrayList<String>();
		List<String> values = new ArrayList<String>();
		
		 if(currentTab.equals(MainActivity.EXPENSE_TAG)) {
			  if(sortBy.equals("Date")) 			  
		   			 orderByVal= "date(e_date)";
		   	  else 
		   			 orderByVal = "e_amount * e_convrate";
			  
		  } else {
			  if(sortBy.equals("Date")) 			  
		   			 orderByVal= "date(i_date)";
		   	  else 
		   			 orderByVal = "i_amount * i_convrate";	 
		  }
		 
		 //filters
		  if(((String)filters.getSelectedItem()).equals("All")) {
			  filterVal = "";
		  }
		  else {
			  filterVal = (String)filters.getSelectedItem();
		  }
		  
		  //order by
	   			 
	   	  if(order.equals("Newest")||order.equals("Highest"))
	   			  sortOrderVal = "DESC";
	   	  else 
	   			  sortOrderVal = "ASC";
	   	  
	   	  //view by
	   	  if(dateRange) {
	   		  viewByVal = "inRange";
	   	  } else {
	   		  viewByVal = "all";
	   	  } 
	   	  
	   	  //currency conversion to default currency
	   	  if(convert.isChecked()) {
	   		  convVal = "on";
	   	  } else {
	   		  convVal = "off";
	   	  }
	   	  keys.add(filterKey);
	   	  values.add(filterVal);
	   	  keys.add(sortOrderKey);
	   	  values.add(sortOrderVal);
	   	  keys.add(orderByKey);
	   	  values.add(orderByVal);
	   	  keys.add(viewByKey);
	   	  values.add(viewByVal);
	   	  if(dateRange) {
	   		  keys.add(startDateKey);
	   		  values.add(startDateVal);
	   		  keys.add(endDateKey);
	   		  values.add(endDateVal);
	   	  }
	   	  keys.add(convKey);
	   	  values.add(convVal);
	   	  
	   	  
	   	  prefAccess.addValues(new PrefAddedListener<List<String>>(){

			@Override
			public void OnSuccess() {
				 Intent data = new Intent();
				 data.putExtra("refresh","yes");
				 // Activity finished ok, return the data
				 setResult(RESULT_OK, data);	 		 
				 OptionsDialog.this.finish();			
			}

			@Override
			public void OnFaiure(int errCode) {
				
			}
	   		  
	   	  }, keys, values, this);
		
	}
	/*
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int pos,
			long arg3) {
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	    Editor editor = prefs.edit();
	    
	    if(((Spinner)parent).getId() == R.id.sortOrder) {
			String item = (String)parent.getSelectedItem();
			String sortBy = item.split(" ")[0].trim();
			String order = item.split(" ")[2].trim();
			
			 if(currentTab.equals(MainActivity.EXPENSE_TAG)) {   		  
	   		  if(sortBy.equals("Date")) 			  
	   			  editor.putString("exp_cur_orderBy","date(e_date)");
	   		  else 
	   			  editor.putString("exp_cur_orderBy","e_amount * e_convrate");
	   		  if(order.equals("Newest")||order.equals("Highest"))
	   			  editor.putString("exp_cur_sortOrder","DESC"); 
	   		  else 
	   			  editor.putString("exp_cur_sortOrder","ASC");
	   		   
	   		  editor.commit();
	  
	   	  } else {
	   		  
	   		  if(sortBy.equals("Date")) 			  
	   			  editor.putString("inc_cur_orderBy","date(i_date)");
	   		  else 
	   			  editor.putString("inc_cur_orderBy","i_amount * i_convrate");
	  
	   		  if(order.equals("Newest")||order.equals("Highest"))
	   			  editor.putString("inc_cur_sortOrder","DESC"); 
	   		  else 
	   			  editor.putString("inc_cur_sortOrder","ASC");
	   		  
	   		  editor.commit();
	   		  
	   	  }
	   	  
	  } else {
		  if(currentTab.equals(MainActivity.EXPENSE_TAG)) {
			  if(((String)parent.getSelectedItem()).equals("All")) {
				  editor.putString("exp_filter","");
			  }
			  else {
				  editor.putString("exp_filter",(String)parent.getSelectedItem());
			  }
			  
	   		
			  editor.commit();
		  } else {
			  if(((String)parent.getSelectedItem()).equals("All")) {
				  editor.putString("inc_filter","");
			  }
			  else
			  editor.putString("inc_filter",(String)parent.getSelectedItem());
			  editor.commit();
	   		 
		  }
	  }
		
	}

	@Override
	public void onNothingSelected(AdapterView<?> arg0) {
		
	}
*/
	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		if(currentTab.equals(MainActivity.EXPENSE_TAG)) {
			return new CategoryCursorLoader(this);
		} else {
			return new SourceCursorLoader(this);
		}
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
		loadSpinners(c);	
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void loadSpinners(Cursor c){	
		List<String> names = new ArrayList<String>();
		names.add("All");
		if(c.moveToFirst()) { 
			do{
				names.add(c.getString(1));
			}while(c.moveToNext()); 
		}
		//Toast.makeText(getApplicationContext(), Integer.toString(test.length), Toast.LENGTH_SHORT).show();	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
        filters.setAdapter(dataAdapter);  // attaching data adapter to category spinner
        
	}

	public static class SelectDateFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		//change it to date from the defaults
			final Calendar c = Calendar.getInstance();
			int year = c.get(Calendar.YEAR);
			int month = c.get(Calendar.MONTH);
			int day = c.get(Calendar.DAY_OF_MONTH);
			return new DatePickerDialog(getActivity(), this, year, month, day);
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
	        
	        if(startDateClicked) {
	        	startDateVal = dateFormat.format(myDate);
	        } else {
	        	endDateVal = dateFormat.format(myDate);
	        }
		}
  }

	public void processDate(View v) {
		if(v.getId() == R.id.startDate) {
			startDateClicked = true;
		} else {
			startDateClicked = false;
		}
		
		DialogFragment dateFragment = (DialogFragment) getFragmentManager().findFragmentByTag("DatePicker");
		
		if(dateFragment == null)
		dateFragment = new SelectDateFragment();
		dateFragment.show(getFragmentManager(), "DatePicker");
	}
}
