package com.anumeha.wheredmymoneygo;

import com.example.wheredmymoneygo.R;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ImageView;
import android.widget.ResourceCursorAdapter;
import android.widget.TextView;

public class PieLegendCursorAdapter extends ResourceCursorAdapter{

	
	public PieLegendCursorAdapter(Context context, int layout, Cursor c) {
		super(context, layout, c);		
	}

	@Override
	public void bindView(View view, Context ctx, Cursor c) {
		
		ImageView colorPatch = (ImageView)view.findViewById(R.id.catColor);
		colorPatch.setBackgroundColor(c.getInt(3));
		
		TextView catName = (TextView) view.findViewById(R.id.legendCatName);
		catName.setText(" - "+c.getString(1));
		
		TextView catAmount = (TextView) view.findViewById(R.id.legendCatAmount);
		catAmount.setText(" : "+c.getString(2));
	}

}
