package com.anumeha.wheredmymoneygo;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.view.View;

public class PieChart extends Drawable {
	

	View v; 
	Cursor c;
	RectF pie_bounds;
	Paint paint;
	
	public PieChart(View v, Cursor c) {
		
		paint = new Paint();
		this.v =v;
		this.c =c; 
		
			
	}


	@Override
	public void draw(Canvas canvas) {
		
	
		int view_w = v.getWidth();
		int view_h = v.getHeight();
		//System.out.println("Height" + view_w);
		
		//chart area rectangle 
		pie_bounds= new RectF(0.20f*view_w,0.1f*view_h, 0.8f*view_w, 0.8f*view_h);
		
		float sum =0;
		//sum of amounts
		c.moveToFirst();
		do { 
			sum += c.getFloat(2);			
		} while(c.moveToNext());
		
		float startAngle =0; 
		c.moveToFirst();
		float nextStartAngle;
		do { 
			float catAmount = c.getFloat(2);
			
			if(catAmount ==0) 
				continue;
			float endAngle = 360*(catAmount/sum);
			nextStartAngle = startAngle + endAngle;
			
		//	System.out.println("Color size is" + colors.size());
			
			paint.setColor((int) c.getFloat(3));
			paint.setAntiAlias(true);
			paint.setStyle(Paint.Style.FILL);
			paint.setStrokeWidth(0.5f);
			
			canvas.drawArc(pie_bounds, startAngle, endAngle, true, paint);
			
			 Paint border = new Paint();

             border.setAntiAlias(true);
             border.setStyle(Paint.Style.STROKE);
             border.setStrokeJoin(Join.ROUND);
             border.setStrokeCap(Cap.ROUND);
             border.setStrokeWidth(0.5f);
             border.setColor(Color.RED);

             //draw border arc
             canvas.drawArc(pie_bounds, startAngle, endAngle, true, border);
             
             startAngle = nextStartAngle;
			
			
		} while(c.moveToNext());
		
		
		
	}

	@Override
	public int getOpacity() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setAlpha(int alpha) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		// TODO Auto-generated method stub
		
	}

}
