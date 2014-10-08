package com.anumeha.wheredmymoneygo.Income;

import com.anumeha.wheredmymoneygo.DBhelpers.IncomeDbHelper;
import com.anumeha.wheredmymoneygo.Income.IncomeEditActivity;
import com.example.wheredmymoneygo.R;

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
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class IncomeListFragment extends Fragment implements LoaderCallbacks<Cursor> {
	
	public static final String TAG = "Income_list";
	private static final int INC_DEL = 04;
	private ListView listview;
	private TextView t;
	private IncomeCursorAdapter incAdapter;
	private Activity activity;
	View view;
	private int incId;
	private String incFreq;
	private boolean incNotify;
	private static int EDIT_INCOME = 01; //0 FOR INCOME 1 FOR EDIT	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // handle orientation changes
    }
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.income_list_fragment, container, false);
	    	 
	    return view;
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
		t = (TextView) view.findViewById(R.id.incNoResultsText);
		listview = (ListView) view.findViewById(R.id.incomeListView);
		registerForContextMenu(listview);
		getLoaderManager().initLoader(3, null,this); //3 for income
	}
	
	public void restartLoader()	{
		getLoaderManager().restartLoader(3, null,this);
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;      
    }
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	    return new IncomeCursorLoader(activity,1);
	}
	 
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		if(cursor.getCount()==0)
		{
			t.setText("No income added yet!");
			
		}
		else
		{
			t.setText("");
		
		    // incAdapter is a CursorAdapter 
		    incAdapter = new IncomeCursorAdapter(activity, R.layout.income_row, cursor, 0);
		    listview.setAdapter(incAdapter);
		}
	}
	 
	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
	   
		if(incAdapter!=null)
			incAdapter.swapCursor(null);
	}
	
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.incomeListView) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Select an option");
	    String[] menuItems = getResources().getStringArray(R.array.listview_menu);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	    
	      String id = ((TextView)info.targetView.findViewById(R.id.incomeId)).getText().toString();
		  incId = Integer.parseInt(id);
		  System.out.println("income id is  "+incId);
		  incFreq= ((TextView)info.targetView.findViewById(R.id.incomeFreq)).getText().toString();
		  String notify = ((TextView)info.targetView.findViewById(R.id.incomeNotify)).getText().toString();
		  incNotify = notify.equals("yes")?true:false;
	    
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
		  Intent i = new Intent(activity, IncomeEditActivity.class);
		  i.putExtra("id",incId); //pass id of item to be edited
		  activity.startActivityForResult(i,EDIT_INCOME);
	  }
	  else
	  {
		  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		  builder.setTitle("Confirm Delete")
	        .setMessage("Are you sure you want to delete this income ?")
	        .setCancelable(true)
	        .setNegativeButton("No",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	            }
	        })
	        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	IncomeDbHelper dbh = new IncomeDbHelper(activity);
	            	 dbh.deleteIncome(incId);
	            	 if(incFreq.equals("Do not repeat")) {
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
		 Intent i =  new Intent (activity,com.anumeha.wheredmymoneygo.Expense.ExpenseAlarmManager.class);	 
		 long id = incId;
		 i.putExtra("rec_id", id);
		 i.putExtra("rec_freq",incFreq );
		 i.putExtra("rec_add",false );
		 i.putExtra("rec_isIncome",true );
		 i.putExtra("rec_rem", true);
		 i.putExtra("rec_notify", incNotify);
		 i.putExtra("old_freq",incFreq);
		 i.putExtra("old_notify", incNotify);
		 this.startActivityForResult(i,INC_DEL);
		
	}

}
