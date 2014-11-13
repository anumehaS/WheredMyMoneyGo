package com.smc.wheredmymoneygo.source;

import com.smc.wheredmymoneygo.R;
import com.smc.wheredmymoneygo.MainActivity;
import com.smc.wheredmymoneygo.category.CategoryActivity;
import com.smc.wheredmymoneygo.dbhelpers.SourceDbHelper;

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

public class SourceActivity extends FragmentActivity implements OnClickListener {
	
	private Button add;
	private String souName;
	private SourceDbHelper dbh;
	static int SOU_EDIT = 1;
	
	 @Override
	    public void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        
	        this.setContentView(R.layout.source_activity);
	       
	        ActionBar actionBar = getActionBar();
		    actionBar.setDisplayHomeAsUpEnabled(true);
	        
	        add = (Button)findViewById(R.id.souAdd);
			add.setOnClickListener(this);	
			
			dbh = new SourceDbHelper(this);
			
	    }
	 
		
		@Override
		public void onClick(View arg0) {
			
			StringBuffer sb = new StringBuffer("Please check the following :\n");
			StringBuilder sb1 = new StringBuilder ("Add the following ? \n");
			boolean valid = true;			
			
			 if(arg0.getId() == R.id.souAdd) {

				 //name
				souName = ((EditText)findViewById(R.id.inputSourceName)).getText().toString();
				if(souName.trim().equals(""))
				{
					sb.append("- Source Name cannot be blank. \n");
					valid = false;
				}
				else if(dbh.nameExists(souName))
				{
					sb.append("- Source Name already exists. Please enter another name. \n");
					valid = false;
				}
				else
				{
					sb1.append("Source name :"+ souName);
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
				     
				            	dbh.addSource(new Source(souName.trim()));
				  
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
			getMenuInflater().inflate(R.menu.main, menu);
			return true;
		}
		
		@Override
		public boolean onOptionsItemSelected(MenuItem item) {
		    // Handle item selection
		    switch (item.getItemId()) {
		        case R.id.action_categories:
		        	//define a new Intent for the add expense form Activity
					Intent intent = new Intent(this,CategoryActivity.class);
			 
					//start the add expense form Activity
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
