package com.anumeha.wheredmymoneygo;


import java.util.ArrayList;
import java.util.List;

import com.anumeha.wheredmymoneygo.Expense.ExpenseListFragment;
import com.anumeha.wheredmymoneygo.Expense.ExpensePieFragment;
import com.anumeha.wheredmymoneygo.Income.IncomeListFragment;
import com.anumeha.wheredmymoneygo.Services.DefaultPreferenceAccess;
import com.example.wheredmymoneygo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;


public class MainActivity extends FragmentActivity implements OnClickListener{


	static String currentTab ;
	private int INCOME_ADDED = 00;
	private int EXPENSE_ADDED = 10;
	private int OPTIONS = 99;
	private static int EDIT_INCOME = 01; //0 FOR INCOME 1 FOR EDIT	
	static String EXPENSE_TAG = "expense";
	private static String INCOME_TAG = "income";
	
	private Button options,listPie;
	private MyTabListener expenseTab,incomeTab,IvETab;
	DefaultPreferenceAccess prefAccess; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefAccess = new DefaultPreferenceAccess();
		
		setContentView(R.layout.activity_main);

		setDefaults();
	
		
		listPie = (Button)findViewById(R.id.listPie);
		listPie.setOnClickListener(this);	
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// add the income, expense and income vs expense tabs
		expenseTab = new MyTabListener(this,EXPENSE_TAG,ExpenseListFragment.class);
		incomeTab = new MyTabListener(this,INCOME_TAG,IncomeListFragment.class);
		actionBar.addTab(actionBar.newTab().setText(R.string.title_expensetab).setTabListener(expenseTab));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_incometab).setTabListener(incomeTab));
	//	actionBar.addTab(actionBar.newTab().setText(R.string.title_expvsinctab).setTabListener(new TwoFragTabListener(this, "tag5", "evi", ExpenseOptionsFragment.class, ExpenseListFragment.class )));
		
	}
	
	@Override
	  public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Restore the previously serialized current tab position.
	    if (savedInstanceState.containsKey("Navigation_item_state")) {
	      getActionBar().setSelectedNavigationItem(savedInstanceState.getInt("Navigation_item_state"));
	    }
	  }

	  @Override
	  public void onSaveInstanceState(Bundle outState) {
	    // Serialize the current tab position.
	    outState.putInt("Navigation_item_state", getActionBar()
	        .getSelectedNavigationIndex());
	  }

	

	  @Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			
			Intent intent;
		    // Handle item selection
		    switch (item.getItemId()) {
		        case R.id.action_categories:
		        	//define a new Intent for the add expense form Activity
					 intent = new Intent(this,com.anumeha.wheredmymoneygo.Category.CategoryActivity.class);
			 
					//start the add expense form Activity
					this.startActivity(intent);
		            return true;
		            
		        case R.id.action_sources:
		        	//define a new Intent for the add expense form Activity
					 intent = new Intent(this,com.anumeha.wheredmymoneygo.Source.SourceActivity.class);
			 
					//start the add expense form Activity
					this.startActivity(intent);
		            return true;
		            
		        case R.id.action_add:
		        	//define a new Intent for the add expense form Activity
		        	if(currentTab.equals("income")) {
		        		intent = new Intent(this,com.anumeha.wheredmymoneygo.Income.IncomeAddActivity.class);
		        		this.startActivityForResult(intent, INCOME_ADDED);
		        	}
		        	else if(currentTab.equals("expense")){
		        		intent = new Intent(this,com.anumeha.wheredmymoneygo.Expense.ExpenseAddActivity.class);
		        		this.startActivityForResult(intent, EXPENSE_ADDED);
		        	}			 
					
		            return true;
		            
		        case android.R.id.home:
		        	  Intent intent1 = new Intent(this, MainActivity.class);
		        	  intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		        	  startActivity(intent1);
		        	  return true;
		        	  
		        default:
		            return super.onOptionsItemSelected(item);
		    }
		}
		
		@Override
		protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		  if (resultCode == RESULT_OK ) {
			  
			  switch(requestCode) {
			  case 00: 
				  if (data.hasExtra("result") && data.getExtras().getString("result").equals("added")) {	
					  //add part about list/pie
				    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(INCOME_TAG);
						inc.restartLoader();	  
				    }
				  break;
			  case 03: 
				  ExpenseListFragment expdel = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(EXPENSE_TAG);
				  expdel.restartLoader();  
				  break;
				  
			  case 04: 
				  IncomeListFragment incdel = (IncomeListFragment)  getFragmentManager().findFragmentByTag(INCOME_TAG);
					incdel.restartLoader();	  
				  break;
			  
			  case 10:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("added")) { 
					ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(EXPENSE_TAG);
					exp.restartLoader();
				  }
			  break;
			  case 01: 
				  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) {	
				    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(INCOME_TAG);
						inc.restartLoader();	  
				    }
				  break;				  
			  
			  case 11:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) { 
					ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(EXPENSE_TAG);
					exp.restartLoader();
				  }
			  break;

			  case 99: if(data.hasExtra("refresh") && data.getExtras().getString("refresh").equals("yes")){
				  	if(currentTab == EXPENSE_TAG) {
				  		ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(EXPENSE_TAG);
						exp.restartLoader();
				  	} else if (currentTab == INCOME_TAG) {
				  		IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(INCOME_TAG);
						inc.restartLoader();
				  	}
			  	  }
			  break;
			  } 
		  }
	  }
	
	 private void setDefaults() {
		  
		  SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	      Editor editor = prefs.edit();
		 
		  
	      if(!prefs.contains("base_currency"))
		  editor.putString("base_currency", "USD");
	      
	      if(!prefs.contains("def_currency"))
		  editor.putString("def_currency", "USD");
		  
	      if(!prefs.contains("def_dateformat"))
		  editor.putString("def_dateformat", "MMMM dd, yyyy");
		  //----------------------------------------------
		  
		  if(!prefs.contains("exp_def_orderBy"))
		  editor.putString("exp_def_orderBy", "date(e_date)");
		  if(!prefs.contains("exp_def_sortOrder"))
		  editor.putString("exp_def_sortOrder", "DESC"); 
		  if(!prefs.contains("exp_viewBy"))
		  editor.putString("exp_viewBy", "all");
		  if(!prefs.contains("exp_def_viewAs"))
		  editor.putString("exp_def_viewAs", "list");
		  if(!prefs.contains("exp_filter"))
		  editor.putString("exp_filter", "");
		  
		  //---------------------------------------------
		  if(!prefs.contains("inc_def_orderBy"))
		  editor.putString("inc_def_orderBy", "date(i_date)");
	      if(!prefs.contains("inc_def_sortOrder"))
		  editor.putString("inc_def_sortOrder", "DESC"); 
		  if(!prefs.contains("inc_viewBy"))
		  editor.putString("inc_viewBy", "all");
		  if(!prefs.contains("inc_def_viewAs"))
		  editor.putString("inc_def_viewAs", "list");
		  if(!prefs.contains("inc_filter"))
		  editor.putString("inc_filter", "");
		  
		  editor.commit();
		  
	}

	
	

	public static class MyTabListener implements
	ActionBar.TabListener {
		
		private Activity activity;
		private String tag;
		private Class fragClass;
		private Fragment currentFrag;
		
		
		public MyTabListener(Activity activity, String tag, Class fragClass){
			this.tag = tag;
			this.fragClass = fragClass;
			this.activity = activity;			
		}
		
		@Override
		public void onTabSelected(ActionBar.Tab tab,
				FragmentTransaction ft) {

	        // Check if the fragment is already initialized
	        if (currentFrag == null) {
	            // If not, instantiate and add it to the activity
	            currentFrag = Fragment.instantiate(activity, fragClass.getName());
	            ft.add(R.id.fragment_cashflow, currentFrag, tag);
	           
	        } else{
	            // If it exists, simply attach it in order to show it
	            ft.attach(currentFrag);
	        }	      
	        currentTab = tag;	
		}

		@Override
		public void onTabUnselected(ActionBar.Tab tab,
				FragmentTransaction ft) {
			if(currentFrag!=null){	
				ft.detach(currentFrag);
			}
		}

		@Override
		public void onTabReselected(ActionBar.Tab tab,
				FragmentTransaction fragmentTransaction) {
		}
		
		public void setCurrentFrag(Fragment f){
				currentFrag = f;
		}
		
	}


	@Override
	public void onClick(View v) {
		
		 SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
	      Editor editor = prefs.edit();
	      Button b = (Button)v;
		
		if(v.getId() == R.id.listPie) {

			FragmentManager fm = getFragmentManager();
			Fragment f;
			FragmentTransaction ft;
			
			if(currentTab.equals(EXPENSE_TAG)) {
				if(prefs.getString("exp_cur_viewAs", "list").equals("list")) {
					editor.putString("exp_cur_viewAs", "pie");
					b.setText("List");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, PieFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, "expense_pie");
					expenseTab.setCurrentFrag(f);
					ft.commit();
				}
				else {
					editor.putString("exp_cur_viewAs", "list");
					b.setText("Pie");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, ExpenseListFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, "expense");
					expenseTab.setCurrentFrag(f);
					ft.commit();
				}
				
			} else {
				if(prefs.getString("inc_cur_viewAs", "list").equals("list")) {
					editor.putString("inc_cur_viewAs", "pie");
					b.setText("List");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, PieFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, "income_pie");
					incomeTab.setCurrentFrag(f);
					ft.commit();
				}
				else {
					editor.putString("inc_cur_viewAs", "list");
					b.setText("Pie");	
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, IncomeListFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, "income");
					incomeTab.setCurrentFrag(f);
					ft.commit();
				}
			}
			
			
			
		}
		
	}

	public void showOptionsDialog(View v) {
		
		Intent i = new Intent(this,com.anumeha.wheredmymoneygo.OptionsDialog.class);
		i.putExtra("currentTab",currentTab);
		
		this.startActivityForResult(i,OPTIONS);
	}
}
