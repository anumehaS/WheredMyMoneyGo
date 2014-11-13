package com.smc.wheredmymoneygo.expense;


import com.smc.wheredmymoneygo.MainActivity;
import com.smc.wheredmymoneygo.R;
import com.smc.wheredmymoneygo.dbhelpers.ExpenseDbHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.ListView;
import android.widget.TextView;

public class ExpenseListFragment extends Fragment implements LoaderCallbacks<Cursor> {
	
	public static final String TAG = "Expense_list";
	private static final int EXP_DEL = 03;
	private ListView listview;
	private TextView t;
	private ExpenseCursorAdapter expAdapter;
	private Activity activity;
	View view;
	private int expId;
	private String expFreq;
	private boolean expNotify;
	private static int EDIT_EXPENSE= 11; //1 FOR EXPENSE 1 FOR EDIT	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // handle orientation changes
    }
	
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.expense_list_fragment, container, false);
	    	 
	    return view;
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		t = (TextView) view.findViewById(R.id.expNoResultsText);
		listview = (ListView) view.findViewById(R.id.expenseListView);
		registerForContextMenu(listview);
		getLoaderManager().initLoader(0, null,this);
	}
	
	public void restartLoader()	{
		getLoaderManager().restartLoader(0, null,this);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;      
    }
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	    return new ExpenseCursorLoader(activity, 1);
	}
	 
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		if(cursor.getCount()==0)
		{
			t.setText("No expenses found!");
			listview.setVisibility(View.GONE); //invisible
			
		}
		else
		{
			t.setText("");
			listview.setVisibility(View.VISIBLE); //visible
		    // expAdapter is a CursorAdapter 
		    expAdapter = new ExpenseCursorAdapter(activity, R.layout.expense_row, cursor, 0,MainActivity.defaultCurrency);
		    listview.setAdapter(expAdapter);
		}
	}
	 
	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
	   
		if(expAdapter!=null)
			expAdapter.swapCursor(null);
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.expenseListView) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Select an option");
	    String[] menuItems = getResources().getStringArray(R.array.listview_menu);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	    
	      String id = ((TextView)info.targetView.findViewById(R.id.expenseId)).getText().toString();
		  expId = Integer.parseInt(id);
		  expFreq= ((TextView)info.targetView.findViewById(R.id.expenseFreq)).getText().toString();
		  String notify = ((TextView)info.targetView.findViewById(R.id.expenseNotify)).getText().toString();
		  expNotify = notify.equals("yes")?true:false;
	  }  
	}
	
	@Override
	public boolean onContextItemSelected(MenuItem item) {	
		
	
	  int menuItemIndex = item.getItemId();
	  String[] menuItems = getResources().getStringArray(R.array.listview_menu);
	  String menuItemName = menuItems[menuItemIndex];
	  
	  if(menuItemName.equals("Edit"))
	  {
		  //start edit activity
		  Intent i = new Intent(activity, ExpenseEditActivity.class);
		  i.putExtra("id",expId); //pass id of item to be edited
		  activity.startActivityForResult(i,EDIT_EXPENSE);
	  }
	  else
	  {
		  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		  builder.setTitle("Confirm Delete")
	        .setMessage("Are you sure you want to delete this expense ?")
	        .setCancelable(true)
	        .setNegativeButton("No",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	            }
	        })
	        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	ExpenseDbHelper dbh = new ExpenseDbHelper(activity);
	            	 dbh.deleteExpense(expId);
	            	 if(expFreq.equals("Do not repeat")) {
		            	 restartLoader();
		                 dialog.cancel(); 
	                 } else {
	                	 startRecActivity();
	                	 restartLoader();
	                	 dialog.cancel();
	                 }
	            }
	        });
	        AlertDialog alert = builder.create();
	        alert.show();
		 
		  
	}

	  return true;
	} 
	 protected void startRecActivity() {
		 Intent i =  new Intent (activity,com.smc.wheredmymoneygo.expense.ExpenseAlarmManager.class);	 
		 long id = expId;
		 i.putExtra("rec_id", id);
		 i.putExtra("rec_freq",expFreq );
		 i.putExtra("rec_add",false );
		 i.putExtra("rec_isIncome",false );
		 i.putExtra("rec_rem", true);
		 i.putExtra("rec_notify", expNotify);
		 i.putExtra("old_freq",expFreq);
		 i.putExtra("old_notify", expNotify);
		 this.startActivityForResult(i,EXP_DEL);
		
	}
	
}
