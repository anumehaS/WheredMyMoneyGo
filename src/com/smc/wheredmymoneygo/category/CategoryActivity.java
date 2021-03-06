package com.smc.wheredmymoneygo.category;


import com.smc.wheredmymoneygo.MainActivity;
import com.smc.wheredmymoneygo.R;
import com.smc.wheredmymoneygo.dbhelpers.CategoryDbHelper;
import com.smc.wheredmymoneygo.expense.ExpenseListFragment;
import com.smc.wheredmymoneygo.services.BackupOps;
import com.smc.wheredmymoneygo.services.BackupOps.BackupCreatedListener;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

public class CategoryActivity extends FragmentActivity implements OnClickListener {
	
	private Button add;
	private String catName, catFreq;
	float catBudget;
	private CategoryDbHelper dbh;
	static int CAT_EDIT = 1;

	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.category_activity);
	       
	        ActionBar actionBar = getActionBar();
		    actionBar.setDisplayHomeAsUpEnabled(true);
	        
	        add = (Button)findViewById(R.id.catAdd);
			add.setOnClickListener(this);	
			
			dbh = new CategoryDbHelper(this);
			
	    }
	 
		
		@Override
		public void onClick(View arg0) {
			
			StringBuffer sb = new StringBuffer("Please check the following :\n");
			StringBuilder sb1 = new StringBuilder ("Add the following ? \n");
			boolean valid = true;			
			
			 if(arg0.getId() == R.id.catAdd) {

				 //name
				catName = ((EditText)findViewById(R.id.inputCategoryName)).getText().toString();
				if(catName.trim().equals(""))
				{
					sb.append("- Category Name cannot be blank. \n");
					valid = false;
				}
				else if(dbh.nameExists(catName))
				{
					sb.append("- Category Name already exists. Please enter another name. \n");
					valid = false;
				}
				else
				{
					sb1.append("Category name :"+ catName);
				}
				
				//amount
				//float budget = 0;
				String budget = ((EditText)findViewById(R.id.inputCategoryBudget)).getText().toString();
				if(budget.trim().equals(""))
				{
					catBudget = -1;
				}
				else 
				{			
					try
					{
						catBudget = Float.parseFloat(budget);
						RadioButton monthlyBudget = (RadioButton)findViewById(R.id.radioMonthly);
						if(monthlyBudget.isChecked())
						{
							catFreq = "Monthly";
							sb1.append("\nMonthlyBudget :" + catBudget);
						}
						
						RadioButton dailyBudget = (RadioButton)findViewById(R.id.radioDaily);
						if(dailyBudget.isChecked())
						{
							catFreq = "Daily";
							sb1.append("\nDailyBudget :" + catBudget);
						}
						
						
					}catch (Exception e)
					{
						sb.append("- Please enter a valid number in the budget field!\n");
						valid = false;
					}
				}
				
				
				AlertDialog.Builder builder = new AlertDialog.Builder(this);
				
				
				if(valid)
				{
					 builder.setTitle("Confirm Add")
				        .setMessage(sb1.toString())
				        .setCancelable(true)
				        .setNegativeButton("No",new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int id) {
				                dialog.cancel();
				            }
				        })
				        .setPositiveButton("Yes",new DialogInterface.OnClickListener() {
				            public void onClick(DialogInterface dialog, int id) {
				     
				            	dbh.addCategory(new Category(catName.trim(), catBudget, catFreq));
				  
				                dialog.cancel();
				                finish();
						        startActivity(getIntent());
				            }
				        });
				        AlertDialog alert = builder.create();
				        alert.show();
				       
				
				}
				
				else
				{
					
			        builder.setTitle("Invalid entries")
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
		
	
		@Override
		public boolean onCreateOptionsMenu(Menu menu) {
			// Inflate the menu; this adds items to the action bar if it is present.
			getMenuInflater().inflate(R.menu.menu2, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
			Intent intent;
		    // Handle item selection
		    switch (item.getItemId()) {
		        case R.id.action_categories:
		        	//define a new Intent for the add expense form Activity
					 intent = new Intent(this,com.smc.wheredmymoneygo.category.CategoryActivity.class);
			 
					//start the add expense form Activity
					this.startActivity(intent);
		            return true;
		            
		        case R.id.action_sources:
		        	
					 intent = new Intent(this,com.smc.wheredmymoneygo.source.SourceActivity.class);

					this.startActivity(intent);
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
		
		
}
