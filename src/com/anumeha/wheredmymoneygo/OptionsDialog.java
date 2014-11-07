package com.anumeha.wheredmymoneygo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.anumeha.wheredmymoneygo.category.CategoryCursorLoader;
import com.anumeha.wheredmymoneygo.services.DefaultPreferenceAccess;
import com.anumeha.wheredmymoneygo.services.DefaultPreferenceAccess.PrefAddedListener;
import com.anumeha.wheredmymoneygo.services.DefaultPreferenceAccess.PrefLoadedListener;
import com.anumeha.wheredmymoneygo.source.SourceCursorLoader;
import com.example.wheredmymoneygo.R;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.Spinner;
import android.widget.TextView;

public class OptionsDialog extends Activity implements LoaderCallbacks<Cursor>{
	
	private static Button startDateBtn, endDateBtn, yahooLogo;
	private String currentTab;
	private static Spinner sortOrder,filters;
	private CheckBox convert, showRec;
	private static boolean dateRange, startDateClicked = true;
	private RadioGroup viewBy;
	private LinearLayout dateRangeLayout;
	private static String startDateVal = null, endDateVal = null;
	String defFilterVal,defSortOrderVal,defOrderByVal, defViewByVal;
	static String defStartDateVal = null;
	static String defEndDateVal = null;
	String defConvVal, defOnlyRecVal;
	String filterVal, sortOrderVal, orderByVal,  viewByVal, convVal, onlyRecVal;
	String filterKey, sortOrderKey, orderByKey,  viewByKey, startDateKey, endDateKey,convKey,onlyRecKey;
	DefaultPreferenceAccess prefAccess; 
	boolean isPie;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);		
		prefAccess = new DefaultPreferenceAccess();
		
		

		currentTab = getIntent().getStringExtra("currentTab");	
		isPie = getIntent().getBooleanExtra("isPie",false);
		
		if(currentTab.equals(MainActivity.EXPENSE_TAG)) {
			  filterKey = Globals.EXP_FILTER; 
			  sortOrderKey = Globals.EXP_DEF_SORTORDER;
			  orderByKey = Globals.EXP_DEF_ORDERBY;
	   		  viewByKey = Globals.EXP_VIEWBY;
	   		  startDateKey = Globals.EXP_START_DATE;
	   		  endDateKey = Globals.EXP_END_DATE;
	   		  convKey = Globals.EXP_CONV;
	   		  onlyRecKey = Globals.EXP_VIEW_REC;
		} else {
			filterKey = Globals.INC_FILTER; 
			  sortOrderKey = Globals.INC_DEF_SORTORDER;
			  orderByKey = Globals.INC_DEF_ORDERBY;
	   		  viewByKey = Globals.INC_VIEWBY;
	   		  startDateKey = Globals.INC_START_DATE;
	   		  endDateKey = Globals.INC_END_DATE;
	   		  convKey = Globals.INC_CONV;
	   		  onlyRecKey = Globals.INC_VIEW_REC;
		}
		
		List<String> keys = new ArrayList<String>();
		keys.add(filterKey);
		keys.add(sortOrderKey); 
		keys.add(orderByKey); 
		keys.add(viewByKey); 
		keys.add(startDateKey);
		keys.add(endDateKey);
		keys.add(convKey);
		keys.add(onlyRecKey);
		
		prefAccess.getValues(new PrefLoadedListener<List<String>>(){

			@Override
			public void OnSuccess(List<String> data) {
				
				OptionsDialog.this.setContentView(R.layout.options_dialog);
				filters = (Spinner)findViewById(R.id.filter);
				defFilterVal = data.get(0);
				defSortOrderVal = data.get(1);
				defOrderByVal = data.get(2);
				defViewByVal = data.get(3);
				defStartDateVal = data.get(4);
				defEndDateVal = data.get(5);
				defConvVal = data.get(6);
				defOnlyRecVal = data.get(7);
				
				startDateBtn = ((Button)findViewById(R.id.startDate));
				endDateBtn = ((Button)findViewById(R.id.endDate));
				
				yahooLogo = (Button)findViewById(R.id.yahooLogo);
				sortOrder = (Spinner)findViewById(R.id.sortOrder);
				dateRangeLayout = (LinearLayout)findViewById(R.id.dateRangeLayout);
				convert = (CheckBox)findViewById(R.id.convertCur);
				showRec = (CheckBox)findViewById(R.id.onlyRec);
				if(isPie){
				 // hide all
				  ((TextView)findViewById(R.id.sortLabel)).setVisibility(View.GONE);
				  ((TextView)findViewById(R.id.filterLabel)).setVisibility(View.GONE);
					filters.setVisibility(View.GONE);
					sortOrder.setVisibility(View.GONE);
					convert.setVisibility(View.GONE);
					showRec.setVisibility(View.GONE);
				} else {
					//populate all
					yahooLogo.setText(Html.fromHtml("<a href=\"https://www.yahoo.com/?ilc=401\">Currency conversion  </a>"));
				    yahooLogo.setMovementMethod(LinkMovementMethod.getInstance());
					ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(OptionsDialog.this,
					        R.array.sort_spinner_items, android.R.layout.simple_spinner_item);
					adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
					sortOrder.setAdapter(adapter);
					sortOrder.setSelection(getDefaultSortSelection(defSortOrderVal,defOrderByVal));
					if(defConvVal.equals("on")) {
						convert.setChecked(true);
					}
									
					if(defOnlyRecVal.equals("on")) {
						showRec.setChecked(true);
					}
					
					OptionsDialog.this.getLoaderManager().initLoader(1,null, OptionsDialog.this ); // 1 for category
					//OptionsDialog.this.getLoaderManager().initLoader(2,null, OptionsDialog.this ); // 2 for sources
				}
				
				
				
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
					
				if(defViewByVal.equals("all")) {
					RadioButton rdb = ((RadioButton) findViewById(R.id.radioAll));
					rdb.setChecked(true);
				} else {
					((RadioButton) findViewById(R.id.radioRange)).setChecked(true);
					
				}

			}

			@Override
			public void OnFaiure(int errCode) {
				
			}
			
		}, keys, this);
		
	}
	
	protected int getDefaultSortSelection(String sortOrder,
			String orderBy) {
		int position = 0;
		if(orderBy.equals("date(e_date)")){
			if(sortOrder.equals("ASC")) {
				position = 1;
			}
		} else {
			if(sortOrder.equals("ASC")) {
				position = 3;
			} else {
				position = 2;
			}
		}
			
			return position;
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
	   	  
	   	  //show only recurrences 
	   	  if(showRec.isChecked()) {
	   		  onlyRecVal = "on";
	   	  } else {
	   		  onlyRecVal = "off";
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
	   	  keys.add(onlyRecKey);
	   	  values.add(onlyRecVal);
	   	  
	   	  
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
		int pos = 0;
		names.add("All");
		if(c.moveToFirst()) { 
			do{
				names.add(c.getString(1));
			}while(c.moveToNext()); 
		}
		//Toast.makeText(getApplicationContext(), Integer.toString(test.length), Toast.LENGTH_SHORT).show();	
		ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
		dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item); // layout style -> list view with radio button   
		if(defFilterVal != null) 
			pos = dataAdapter.getPosition(defFilterVal);
        filters.setAdapter(dataAdapter);  // attaching data adapter to category spinner
        filters.setSelection(pos);
        
	}

	public static class SelectDateFragment extends DialogFragment
    implements DatePickerDialog.OnDateSetListener {

		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
		//change it to date from the defaults
			int year = 0, month = 0, day = 0;
			String dateVal, defDateVal;
			final Calendar c = Calendar.getInstance();
			if(startDateClicked) {
				dateVal = startDateVal;
				defDateVal = defStartDateVal;
			}else {
				dateVal = endDateVal;
				defDateVal = defEndDateVal;
			}
				if(dateVal != null || defDateVal != null){
					String date;
					if(dateVal != null) {
						date = dateVal;
					} else {
						date = defDateVal;
					}
						try {
							Date myDate = (new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT)).parse(date);
							c.setTime(myDate);
						} catch (ParseException e) {
							e.printStackTrace();
						}
					
				} 				
				 year = c.get(Calendar.YEAR);
				 month = c.get(Calendar.MONTH);
				 day = c.get(Calendar.DAY_OF_MONTH); 
			 
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
	        SimpleDateFormat dateFormat = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT); 
	        
	        
	        if(startDateClicked) {
	        	startDateVal = dateFormat.format(myDate);
	        	dateFormat = new SimpleDateFormat(Globals.USER_DATE_FORMAT); 	        	
	        	startDateBtn.setText(dateFormat.format(myDate));
	        } else {
	        	
	        	endDateVal = dateFormat.format(myDate);
	        	dateFormat = new SimpleDateFormat(Globals.USER_DATE_FORMAT); 	        	
	        	endDateBtn.setText(dateFormat.format(myDate));
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
