package com.smc.wheredmymoneygo.category;



import com.smc.wheredmymoneygo.R;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class CategoryCursorAdapter extends ResourceCursorAdapter {

	public CategoryCursorAdapter(Context context, int layout, Cursor c,
			int flags) {
		super(context, layout, c, flags);
		
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		
		//set categoryid -- invisible field
		TextView c_id = (TextView)view.findViewById(R.id.categoryId);
		c_id.setText(cursor.getString(0));
		
		//set category name
		TextView c_name = (TextView)view.findViewById(R.id.categoryName);
		c_name.setText(cursor.getString(1));

		TextView c_Budget = (TextView)view.findViewById(R.id.categoryBudget);
		//set budget field if budget has been set
		if(cursor.getFloat(2) != -1f) {			
			c_Budget.setText(cursor.getString(3)+" Budget - "+ cursor.getFloat(2));
		}
		else{
			c_Budget.setText("");
		}
		
	}
	
	

}
