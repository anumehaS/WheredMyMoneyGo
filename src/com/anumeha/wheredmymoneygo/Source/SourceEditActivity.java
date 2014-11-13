package com.anumeha.wheredmymoneygo.source;


import com.anumeha.wheredmymoneygo.dbhelpers.SourceDbHelper;
import com.example.wheredmymoneygo.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class SourceEditActivity extends Activity implements OnClickListener {
	
	private Button save, cancel;
	private String oldSourceName;
	
	private static String souName;
    SourceDbHelper dbh;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        this.setContentView(R.layout.source_edit_activity);
      
        save = (Button)findViewById(R.id.souSaveEdit);
		save.setOnClickListener(this);
		cancel = (Button)findViewById(R.id.souCancelEdit);
		cancel.setOnClickListener(this);
		dbh = new SourceDbHelper(this);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
		   souName = extras.getString("souName");
		    oldSourceName = souName;
		}
		
		EditText t = (EditText) findViewById(R.id.inputSourceNameEdit);
		t.setText(souName);
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
   		if(arg0.getId() == R.id.souCancelEdit) {
   			endActivity("cancelled");
   		}
   		
   		else if(arg0.getId() == R.id.souSaveEdit){

   			//name
   			souName = ((EditText)findViewById(R.id.inputSourceNameEdit)).getText().toString();
   			if(souName.trim().equals(""))
   			{
   				sb.append("- Category Name cannot be blank. \n");
   				valid = false;
   			}
   			else if((!souName.trim().equalsIgnoreCase(oldSourceName.trim()))&&dbh.nameExists(souName))
   			{
   				sb.append("- Source Name already exists. Please enter another name. \n");
   				valid = false;
   			}
   			
   			if(noChanges && !souName.trim().equalsIgnoreCase(oldSourceName.trim())) {
   				noChanges = false;
   			}
   	
   			AlertDialog.Builder builder = new AlertDialog.Builder(this);
   			
   			if(valid && !noChanges) {	
   				System.out.println("Updating for "+ oldSourceName);
   				dbh.updateSource(new Source(souName.trim()),oldSourceName);
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
    

}
