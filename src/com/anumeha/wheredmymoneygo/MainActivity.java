package com.anumeha.wheredmymoneygo;



import java.util.ArrayList;
import java.util.List;

import com.anumeha.wheredmymoneygo.Expense.ExpenseListFragment;
import com.anumeha.wheredmymoneygo.Income.IncomeListFragment;
import com.anumeha.wheredmymoneygo.Services.BackupOps;
import com.anumeha.wheredmymoneygo.Services.BackupOps.BackupCreatedListener;
import com.anumeha.wheredmymoneygo.Services.DefaultPreferenceAccess;
import com.anumeha.wheredmymoneygo.Services.DefaultPreferenceAccess.PrefAddedListener;
import com.anumeha.wheredmymoneygo.Services.DefaultsLoader;
import com.anumeha.wheredmymoneygo.Services.DefaultsLoader.DefaultsLoadedListener;
import com.example.wheredmymoneygo.R;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.Toast;


public class MainActivity extends FragmentActivity implements OnClickListener{


	static String currentTab, defCurrency;	
	static boolean pie = false, expPie = false, incPie = false;
	private int INCOME_ADDED = 00;
	private int EXPENSE_ADDED = 10;
	private int OPTIONS = 99;
	private static int EDIT_INCOME = 01; //0 FOR INCOME 1 FOR EDIT	
	static String EXPENSE_TAG = "expense";
	private static String INCOME_TAG = "income";
	private int navState = 0;
	private static Button listPie;
	private MyTabListener expenseTab,incomeTab,IvETab;
	DefaultPreferenceAccess prefAccess; 
	DefaultsLoader defLoader;
	 static SharedPreferences prefs;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState!=null &&savedInstanceState.containsKey("Navigation_item_state")) {
			navState = savedInstanceState.getInt("Navigation_item_state");
		      //getActionBar().setSelectedNavigationItem(savedInstanceState.getInt("Navigation_item_state"));
		    }
		prefAccess = new DefaultPreferenceAccess();
		defLoader = new DefaultsLoader();
		prefs= PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
		defLoader.setDefaults(new DefaultsLoadedListener<List<String>>(){

			@Override
			public void defaultsLoaded(List<String> data) {
				SelectCurrencyDialog dialog = new SelectCurrencyDialog(data.toArray(new String[data.size()]));
				dialog.show(getFragmentManager(), "Select Currency");
			}

			@Override
			public void notFirstTime(List<String> data) {
				if(data.get(0).equals("pie") ) {
					expPie = true;
					pie = true;
				}	
				if(data.get(1).equals("pie")){
					incPie = true;
					pie = true;
				}
				setUpMainActivty();
			}
			
		}, this);

	}
	
	protected void populateDefaultCurrency(String defCurrency) {
		List<String> keys = new ArrayList<String>();
		keys.add("base_currency");
		keys.add("def_currency");
		
		List<String> values = new ArrayList<String>();
		values.add(defCurrency);
		values.add(defCurrency);
		
		prefAccess.addValues(new PrefAddedListener<List<String>>() {

			@Override
			public void OnSuccess() {
				
				setUpMainActivty();
			}

			@Override
			public void OnFaiure(int errCode) {
				//end app
				
			}
			
		}, keys, values, this);
		
	}
	
	void setUpMainActivty() {
		setContentView(R.layout.activity_main);

		listPie = (Button)findViewById(R.id.listPie);
		listPie.setOnClickListener(this);	
		
		// Set up the action bar.
		final ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		// add the income, expense and income vs expense tabs
		if(!pie) {
			expenseTab = new MyTabListener(this,ExpenseListFragment.TAG,ExpenseListFragment.class);
			incomeTab = new MyTabListener(this,IncomeListFragment.TAG,IncomeListFragment.class);
		} else {
			if(expPie){
				expenseTab = new MyTabListener(this,PieFragment.EXP_TAG,PieFragment.class);
			} else {
				expenseTab = new MyTabListener(this,ExpenseListFragment.TAG,ExpenseListFragment.class);
			}

			if(incPie) {
				incomeTab = new MyTabListener(this,PieFragment.INC_TAG,PieFragment.class);
		 	} else {
		 		incomeTab = new MyTabListener(this,IncomeListFragment.TAG,IncomeListFragment.class);
		 	}
		}
		
		actionBar.addTab(actionBar.newTab().setText(R.string.title_expensetab).setTabListener(expenseTab));
		actionBar.addTab(actionBar.newTab().setText(R.string.title_incometab).setTabListener(incomeTab));
	//	actionBar.addTab(actionBar.newTab().setText(R.string.title_expvsinctab).setTabListener(new TwoFragTabListener(this, "tag5", "evi", ExpenseOptionsFragment.class, ExpenseListFragment.class )));
		actionBar.setSelectedNavigationItem(navState);
	
	}
	
	@Override
	  public void onRestoreInstanceState(Bundle savedInstanceState) {
	    // Restore the previously serialized current tab position.
		//getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
	    
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
		        	if(currentTab.equals(INCOME_TAG)) {
		        		intent = new Intent(this,com.anumeha.wheredmymoneygo.Income.IncomeAddActivity.class);
		        		this.startActivityForResult(intent, INCOME_ADDED);
		        	}
		        	else if(currentTab.equals(EXPENSE_TAG)){
		        		intent = new Intent(this,com.anumeha.wheredmymoneygo.Expense.ExpenseAddActivity.class);
		        		this.startActivityForResult(intent, EXPENSE_ADDED);
		        	}			 
					
		            return true;
		            
		        case R.id.action_backup:
		        	  BackupOps backup = new BackupOps();
		        	  backup.createBackup(new BackupCreatedListener<String>(){

						@Override
						public void OnSuccess(String data) {
							
							sendEmail(data);
						}

						@Override
						public void OnFaiure(int errCode) {
							Toast.makeText(getApplicationContext(), "Could not create backup ", Toast.LENGTH_SHORT).show();
							
						}},this);
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
					 if(!pie) {
					    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(IncomeListFragment.TAG);
							inc.restartLoader();	 
						} else {
							PieFragment inc = (PieFragment)  getFragmentManager().findFragmentByTag(PieFragment.INC_TAG);
							inc.restartLoader();
						}
				    }
				  break;
			  case 03: 
				  ExpenseListFragment expdel = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(ExpenseListFragment.TAG);
				  expdel.restartLoader();  
				  break;
				  
			  case 04: 
				  IncomeListFragment incdel = (IncomeListFragment)  getFragmentManager().findFragmentByTag(IncomeListFragment.TAG);
					incdel.restartLoader();	  
				  break;
			  
			  case 10:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("added")) { 
					if(!pie) {
					  ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(ExpenseListFragment.TAG);
					  exp.restartLoader();
					} else {
						PieFragment exp = (PieFragment)  getFragmentManager().findFragmentByTag(PieFragment.EXP_TAG);
						exp.restartLoader();
					}
				  } 
			  break;
			  case 01: 
				  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) {	
				    	IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(IncomeListFragment.TAG);
						inc.restartLoader();	  
				    }
				  break;				  
			  
			  case 11:  if (data.hasExtra("result") && data.getExtras().getString("result").equals("edited")) { 
					
						ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(ExpenseListFragment.TAG);
						exp.restartLoader(); 
				  }
			  break;

			  case 99: if(data.hasExtra("refresh") && data.getExtras().getString("refresh").equals("yes")){
				  	if(currentTab.equals(EXPENSE_TAG)) {
				  		if(!pie) {
					  		ExpenseListFragment exp = (ExpenseListFragment)  getFragmentManager().findFragmentByTag(ExpenseListFragment.TAG);
							exp.restartLoader(); 
						}else {
							PieFragment exp = (PieFragment)  getFragmentManager().findFragmentByTag(PieFragment.EXP_TAG);
							exp.restartLoader();
						} 
				  	} else if (currentTab.equals(INCOME_TAG)) {
				  		if(!pie){
					  		IncomeListFragment inc = (IncomeListFragment)  getFragmentManager().findFragmentByTag(IncomeListFragment.TAG);
							inc.restartLoader(); 
						}else {
							PieFragment inc = (PieFragment)  getFragmentManager().findFragmentByTag(PieFragment.INC_TAG);
							inc.restartLoader();
						}
				  	}
			  	  }
			  break;
			  } 
		  }
	  }
	private void sendEmail(String filePath){
		
		String[] fileNames = {"/wmmgExpensebackup.csv","/wmmgIncomebackup.csv","/wmmgCategorybackup.csv","/wmmgSourcebackup.csv"};
		ArrayList<Uri> uris = new ArrayList<Uri>();
		for(String s: fileNames){
		Uri uri = Uri.parse("file://"+filePath+s);
		uris.add(uri);
		}
		Intent i = new Intent(Intent.ACTION_SEND_MULTIPLE);
		i.setType("plain/text");
		i.putExtra(Intent.EXTRA_SUBJECT, "Backup - Where\'d my money go?");
		i.putParcelableArrayListExtra(Intent.EXTRA_STREAM, uris);
		startActivity(Intent.createChooser(i,"Email:"));
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
			currentFrag = activity.getFragmentManager().findFragmentByTag(tag);
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
	        
	        if(tag.equals(ExpenseListFragment.TAG)|| tag.equals(PieFragment.EXP_TAG)) {
	        	currentTab = EXPENSE_TAG;
	        } else {
	        	currentTab = INCOME_TAG;
	        }
	      
	        if(currentTab.equals(EXPENSE_TAG)){
	        	if(prefs.getString("exp_def_viewAs", "list").equals("list")) {
	        		listPie.setText("Pie");  
	        		pie=false;
	        	}else {
	        		listPie.setText("List");  
	        		pie=true;
	        	}
	        }  	
	         else {
		        	if(prefs.getString("inc_def_viewAs", "list").equals("list")) {
		        		listPie.setText("Pie");   
		        		pie=false;
		        	}else {
		        		listPie.setText("List");  
		        		pie=true;
		        	}
		      }  	
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
		
		
	      Editor editor = prefs.edit();
	      Button b = (Button)v;
		
		if(v.getId() == R.id.listPie) {

			FragmentManager fm = getFragmentManager();
			Fragment f;
			FragmentTransaction ft;
			
			if(currentTab.equals(EXPENSE_TAG)) {
				if(prefs.getString("exp_def_viewAs", "list").equals("list")) {
					editor.putString("exp_def_viewAs", "pie");
					b.setText("List");	
					pie = true;
					editor.commit();
					f = MainActivity.this.getFragmentManager().findFragmentByTag(PieFragment.EXP_TAG);
					if(f == null) {
						f = Fragment.instantiate(MainActivity.this, PieFragment.class.getName()); 
					}
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, PieFragment.EXP_TAG);
					expenseTab.setCurrentFrag(f);
					ft.commit();
				}
				else {
					editor.putString("exp_def_viewAs", "list");
					b.setText("Pie");	
					pie = false;
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, ExpenseListFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, ExpenseListFragment.TAG);
					expenseTab.setCurrentFrag(f);
					ft.commit();
				}
				
			} else {
				if(prefs.getString("inc_def_viewAs", "list").equals("list")) {
					editor.putString("inc_def_viewAs", "pie");
					b.setText("List");
					pie = true;
					editor.commit();
					f = fm.findFragmentByTag(PieFragment.INC_TAG);
					if(f == null) {
						f = Fragment.instantiate(MainActivity.this, PieFragment.class.getName()); 
					}
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, PieFragment.INC_TAG);
					incomeTab.setCurrentFrag(f);
					ft.commit();
				}
				else {
					editor.putString("inc_def_viewAs", "list");
					b.setText("Pie");	
					pie = false;
					editor.commit();
					f = Fragment.instantiate(MainActivity.this, IncomeListFragment.class.getName());
					ft = fm.beginTransaction();
					ft.replace(R.id.fragment_cashflow, f, IncomeListFragment.TAG);
					incomeTab.setCurrentFrag(f);
					ft.commit();
				}
			}
			
			
			
		}
		
	}

	public void showOptionsDialog(View v) {
		
		Intent i = new Intent(this,com.anumeha.wheredmymoneygo.OptionsDialog.class);
		i.putExtra("currentTab",currentTab);
		i.putExtra("isPie",pie);
		this.startActivityForResult(i,OPTIONS);
	}
	
	private class SelectCurrencyDialog extends DialogFragment {
		String [] currencyList;
		SelectCurrencyDialog(String[] currencyList){
			this.currencyList = currencyList;
		}
		int currentSelection = 0;
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			
			 AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
			    // Set the dialog title
			    builder.setTitle("Select a  default Currency (Cannot be changed later)")
			           .setSingleChoiceItems(currencyList, 0,
			                      new DialogInterface.OnClickListener() {
			               @Override
			               public void onClick(DialogInterface dialog, int which) {			                  
			                     currentSelection = which;
			               }
			           })
			           .setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			               @Override
			               public void onClick(DialogInterface dialog, int id) {
			                  String  defCurrency = currencyList[currentSelection];
			                  populateDefaultCurrency(defCurrency);	
			               }
			           });
			    
			    return builder.create();
		}
	}

}
