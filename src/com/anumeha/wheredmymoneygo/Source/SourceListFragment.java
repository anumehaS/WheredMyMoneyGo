package com.anumeha.wheredmymoneygo.Source;

import com.anumeha.wheredmymoneygo.DBhelpers.SourceDbHelper;
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
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.AdapterView.AdapterContextMenuInfo;

public class SourceListFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	private ListView listview;
	private SimpleCursorAdapter curAdapter;
	private Activity activity;
	View view;
	String souName;
	int souId;
	
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.source_list_fragment, container, false);
	    	 
	    return view;
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listview = (ListView) view.findViewById(R.id.sourceListView);
		registerForContextMenu(listview);
		getLoaderManager().initLoader(4, null,this); //  4 is for the loader for the category list
	
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;     
       
    }
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	    return new SourceCursorLoader(activity);
	}
	 
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		String [] from = {"_id", "s_name"};
		int to [] = {R.id.sourceId,R.id.sourceName};
	    curAdapter = new SimpleCursorAdapter(activity, R.layout.source_row, cursor, from, to,0);
	    listview.setAdapter(curAdapter);
	}
	 
	@Override
	public void onLoaderReset(Loader<Cursor> cursorLoader) {
		if(curAdapter !=null){
	    curAdapter.swapCursor(null);
		}
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
	  if (v.getId()==R.id.sourceListView) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Select an option");
	    String[] menuItems = getResources().getStringArray(R.array.listview_menu);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	    String temp = ((TextView)info.targetView.findViewById(R.id.sourceId)).getText().toString();
	    souId =  Integer.parseInt(temp); 
	    souName = ((TextView)info.targetView.findViewById(R.id.sourceName)).getText().toString();
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
		  Intent i = new Intent(activity,SourceEditActivity.class);
		  i.putExtra("souName",souName); //pass id of item to be edited
		  startActivity(i);
	  }
	  else
	  {
		  
		  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		  if(souId == 1) {
			  builder.setTitle("Invalid Request")
		        .setMessage("Sorry, you cannot delete the default source")
		        .setCancelable(true)
		        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		            }
		        }); 
		  }
		  else {
		  builder.setTitle("Confirm Delete")
	        .setMessage("Are you sure you want to delete this source?")
	        .setCancelable(true)
	        .setNegativeButton("No",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	            }
	        })
	        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	SourceDbHelper dbh = new SourceDbHelper(activity);
	            	 dbh.deleteSource(souName);	  
	                 dialog.cancel();
	            }
	        });
		  }
	        AlertDialog alert = builder.create();
	        alert.show();
	  
	}

	  return true;
	} 

}
