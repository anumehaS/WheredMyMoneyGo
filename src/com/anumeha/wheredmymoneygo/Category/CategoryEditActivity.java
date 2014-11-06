package com.anumeha.wheredmymoneygo.category;


import com.example.wheredmymoneygo.*;
import com.anumeha.wheredmymoneygo.dbhelpers.CategoryDbHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;

public class CategoryEditActivity extends Activity implements OnClickListener, LoaderCallbacks<Cursor>{
	
	private Button save, cancel;
	private String oldCategoryName;
	
	private static String catName, catFreq, catFreqEdit; 
    private static float catBudget, catBudgetEdit = 0;	
    CategoryDbHelper dbh;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.category_edit_activity);
            
        getLoaderManager().initLoader(2,null,this);
        
        save = (Button)findViewById(R.id.catSaveEdit);
		save.setOnClickListener(this);
		cancel = (Button)findViewById(R.id.catCancelEdit);
		cancel.setOnClickListener(this);
		dbh = new CategoryDbHelper(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		    catName = extras.getString("catName");
		    oldCategoryName = catName;
		}
 
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
		boolean valid = true, noChanges = true;
		if(arg0.getId() == R.id.catCancelEdit) {
			endActivity("cancelled");
		}
		
		else if(arg0.getId() == R.id.catSaveEdit){

			//name
			catName = ((EditText)findViewById(R.id.inputCategoryNameEdit)).getText().toString();
			if(catName.trim().equals(""))
			{
				sb.append("- Category Name cannot be blank. \n");
				valid = false;
			}
			else if((!catName.trim().equalsIgnoreCase(oldCategoryName.trim()))&&dbh.nameExists(catName))
			{
				sb.append("- Category Name already exists. Please enter another name. \n");
				valid = false;
			}
			
			if(noChanges && !catName.trim().equalsIgnoreCase(oldCategoryName.trim())) {
				noChanges = false;
			}
			
			//amount
			//float budget = 0;
			
			String budget = ((EditText)findViewById(R.id.inputCategoryBudgetEdit)).getText().toString();
			if(budget.trim().equals(""))
			{
				catBudgetEdit = -1f;
			}
			else 
			{			
				try {					
					catBudgetEdit = Float.parseFloat(budget);	
					
				}catch (Exception e) {
					sb.append("- Please enter a valid number in the budget field!\n");
					valid = false;
				}
			}
			
			if(noChanges && catBudget !=catBudgetEdit) {
				noChanges = false;
			}
			
			
			
		
			RadioButton monthlyBudget = (RadioButton)findViewById(R.id.radioMonthlyEdit);
	
			if(monthlyBudget.isChecked()) {
				catFreqEdit = "Monthly";			 
			}
			else {
				catFreqEdit = "Daily";	
			}
			if(noChanges && !catFreq.trim().toLowerCase().equals(catFreq.toLowerCase())) {
				noChanges = false;
			}
			

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			
			if(valid && !noChanges) {	
				System.out.println("Updating for "+ oldCategoryName);
				dbh.updateCategory(new Category(catName.trim(),catBudgetEdit,catFreqEdit),oldCategoryName);
        		endActivity("updated");
			}
			
			else {
				String title = "Invalid Entries";
				if(noChanges){
					title = "No Changes";
					sb = new StringBuffer("No changes were made");
				}
				
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
	
	public void endActivity()
	{
		
		this.finish();
		
	}

	@Override
	public Loader<Cursor> onCreateLoader(int id, Bundle args) {	
		return new CategoryCursorLoader(this, catName);
	}

	@Override
	public void onLoadFinished(Loader<Cursor> arg0, Cursor c) {
	 
		c.moveToFirst();
		catBudget = c.getFloat(2);
		catFreq = c.getString(3);
		
		((EditText)findViewById(R.id.inputCategoryNameEdit)).setText(catName);
	
		((EditText)findViewById(R.id.inputCategoryBudgetEdit)).setText(catBudget > 0 ? Float.toString(catBudget):"");
		if(catFreq.equals("Monthly")) {
			((RadioButton)findViewById(R.id.radioMonthlyEdit)).setChecked(true);
		}
		else {
			((RadioButton)findViewById(R.id.radioDailyEdit)).setChecked(true);
		}
	
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
