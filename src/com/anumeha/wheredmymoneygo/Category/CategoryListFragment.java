package com.anumeha.wheredmymoneygo.category;

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


import com.anumeha.wheredmymoneygo.dbhelpers.CategoryDbHelper;
import com.example.wheredmymoneygo.R;

public class CategoryListFragment extends Fragment implements LoaderCallbacks<Cursor>{
		
	private ListView listview;
	private CategoryCursorAdapter curAdapter;
	private Activity activity;
	View view;
	String catName;
	int catId;
	
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.category_list_fragment, container, false);
	    	 
	    return view;
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		listview = (ListView) view.findViewById(R.id.categoryListView);
		registerForContextMenu(listview);
		getLoaderManager().initLoader(1, null,this); //  1 is for the loader for the category list
	
	}
	
	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;     
       
    }
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
	    return new CategoryCursorLoader(activity);
	}
	 
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
	    curAdapter = new CategoryCursorAdapter(activity, R.layout.category_row, cursor, 0);
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
	  if (v.getId()==R.id.categoryListView) {
	    AdapterContextMenuInfo info = (AdapterContextMenuInfo)menuInfo;
	    menu.setHeaderTitle("Select an option");
	    String[] menuItems = getResources().getStringArray(R.array.listview_menu);
	    for (int i = 0; i<menuItems.length; i++) {
	      menu.add(Menu.NONE, i, i, menuItems[i]);
	    }
	    String temp = ((TextView)info.targetView.findViewById(R.id.categoryId)).getText().toString();
	    catId =  Integer.parseInt(temp); 
	    catName = ((TextView)info.targetView.findViewById(R.id.categoryName)).getText().toString();
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
		  Intent i = new Intent(activity, CategoryEditActivity.class);
		  i.putExtra("catName",catName); //pass id of item to be edited
		  startActivity(i);
	  }
	  else
	  {
		  
		  AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		  if(catId <= 6) {
			  builder.setTitle("Invalid Request")
		        .setMessage("Sorry, you cannot delete the default categories")
		        .setCancelable(true)
		        .setNegativeButton("Close",new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int id) {
		                dialog.cancel();
		            }
		        }); 
		  }
		  else {
		  builder.setTitle("Confirm Delete")
	        .setMessage("Are you sure you want to delete this category ?")
	        .setCancelable(true)
	        .setNegativeButton("No",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	                dialog.cancel();
	            }
	        })
	        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int id) {
	            	CategoryDbHelper dbh = new CategoryDbHelper(activity);
	            	 dbh.deleteCategory(catName);	  
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
