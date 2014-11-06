package com.anumeha.wheredmymoneygo;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Cap;
import android.graphics.Paint.Join;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;


public class PieChart extends View {
	
	static Cursor cursor = null;
	RectF pieBounds;
	Paint paint;
	View pieView;
	Paint border;
	Context context;
	
	public PieChart(Context context ) {
		super(context);
		this.context=context;
		init();
		
	}
	
	public PieChart(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context=context;
        init();
	}
	
	private void init() {
        paint = new Paint();
        paint.setAntiAlias(true);
		paint.setStyle(Paint.Style.FILL);
		paint.setStrokeWidth(0.5f);
        border = new Paint();
        border.setAntiAlias(true);
        border.setStyle(Paint.Style.STROKE);
        border.setStrokeJoin(Join.ROUND);
        border.setStrokeCap(Cap.ROUND);
        border.setStrokeWidth(0.5f);
        border.setColor(Color.RED);
	}


	@Override
	public void onDraw(Canvas canvas) {
		
		
		if ( cursor == null || cursor.isClosed())
			return;
		super.onDraw(canvas);
		
		
		float sum =0;
		//sum of amounts
	
		cursor.moveToFirst();
		do { 
			sum += cursor.getFloat(2);			
		} while(cursor.moveToNext());
		
		float startAngle =0; 
		cursor.moveToFirst();
		float nextStartAngle;
		do { 
			float catAmount = cursor.getFloat(2);
			
			if(catAmount ==0) 
				continue;
			float endAngle = 360*(catAmount/sum);
			nextStartAngle = startAngle + endAngle;
			
		//	System.out.println("Color size is" + colors.size());
			
			paint.setColor((int) cursor.getFloat(3));
			
			
			canvas.drawArc(pieBounds, startAngle, endAngle, true, paint);

             //draw border arc
             canvas.drawArc(pieBounds, startAngle, endAngle, true, border);
             
             startAngle = nextStartAngle;
			
			
		} while(cursor.moveToNext());
		
		
		
	}

	
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        
        //Get the padding
        float xpad = (float) (getPaddingLeft() + getPaddingRight());
        float ypad = (float) (getPaddingTop() + getPaddingBottom());
        
        float maxHeight = (float) calculateMaximumPieHeight();
        
        float ww = (float) w - xpad;
        float hh = (float) Math.min(h, maxHeight) - ypad;
              
        
        float diameter = Math.min(ww, hh);
        
        float leftBound = (w - diameter)/2;
        
        pieBounds = new RectF(
                0.0f,
                0.0f,
                diameter,
                diameter);
        pieBounds.offsetTo( leftBound, getPaddingTop());
        
        
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        
    	// Try for a width based on our minimum
        //int minw = getPaddingLeft() + getPaddingRight() + getSuggestedMinimumWidth();

        //int w = Math.max(minw, MeasureSpec.getSize(widthMeasureSpec));

        // Whatever the width ends up being, ask for a height that would let the pie
        // get as big as it can
        int minh = calculateMaximumPieHeight() + getPaddingBottom() + getPaddingTop();
        int h = Math.min(MeasureSpec.getSize(heightMeasureSpec), minh);

        setMeasuredDimension(widthMeasureSpec, h);
    }   
    
	@Override
	protected void onLayout(boolean arg0, int arg1, int arg2, int arg3, int arg4) {
		// TODO Auto-generated method stub
		
	}


	public Cursor getCursor() {
		return cursor;
	}


	public void setCursor(Cursor c) {
		PieChart.cursor = null;
		PieChart.cursor = c;
	}


	public View getPieView() {
		return pieView;
	}


	public void setPieView(View pieView) {
		this.pieView = pieView;
	}
	
	public int calculateMaximumPieHeight() {
		if( context == null)
			return Integer.MAX_VALUE;
		WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);
        
        return (int) Math.ceil(metrics.heightPixels * 0.4);
	}

}
