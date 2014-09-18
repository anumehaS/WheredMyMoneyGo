package com.anumeha.wheredmymoneygo.receivers;

import com.anumeha.wheredmymoneygo.DBhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.DBhelpers.IncomeDbHelper;
import com.anumeha.wheredmymoneygo.Services.AlarmOps;
import com.anumeha.wheredmymoneygo.Services.AlarmOps.OnAlarmOpsCompleted;
import com.anumeha.wheredmymoneygo.Services.WmmgAlarmManager;
import com.anumeha.wheredmymoneygo.Services.WmmgNotificationCreator;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

public class RecEventReceiver extends BroadcastReceiver{

	boolean isIncome,notify; 
	String freq;
	int id;
	
	private static final String DEBUG_TAG = "RecEventReceiver";
	@Override
	public void onReceive(Context ctx, Intent intent) {
		
	/*	 if (intent.getAction()!= null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
	           //reset alarms for expenses
			 	ExpenseDbHelper expDb = new ExpenseDbHelper(ctx);
			 	Cursor c = expDb.getAllRecurences();
			 	resetAllAlarms(ctx,c,false);
			 	
			 	//reset alarms for Incomes
			 	IncomeDbHelper incDb = new IncomeDbHelper(ctx);
			 	c = incDb.getAllRecurences();
			 	resetAllAlarms(ctx,c,true);
			 		
			 	return;
	      }*/
		
		Log.d(DEBUG_TAG,"In receiver");
		
		boolean isIncome = intent.getBooleanExtra("isIncome",false);
		boolean notify = intent.getBooleanExtra("rec_notify", false );
		String freq = intent.getStringExtra("freq");
		int id = intent.getIntExtra("id",0);
		String contentTitle, contentText;
		Class startActivity;
		
		if(notify) {
			Log.d(DEBUG_TAG,"is notify");
			if(isIncome) {
				startActivity = com.anumeha.wheredmymoneygo.Income.IncomeEditActivity.class;
				contentTitle = "Income Reminder";
				contentText = "Tap to add the recurring income";
				
			} else {
				startActivity = com.anumeha.wheredmymoneygo.Expense.ExpenseEditActivity.class;
				contentTitle = "Expense Reminder";
				contentText = "Tap to add the recurring expense";
			}
			Log.d(DEBUG_TAG,"creating notification");
			WmmgNotificationCreator.createNotification(ctx, startActivity, id, contentTitle, contentText);
		} else {
		
			AlarmOps ops = new AlarmOps(ctx,isIncome);
			ops.addRecToDb(new OnAlarmOpsCompleted(){
	
				@Override
				public void OnSuccess() {
					//added the reccurrence in the db - background task - fetch from db and add it into db again								
				}
	
				@Override
				public void OnFaiure() {
					Log.e(DEBUG_TAG,"Failed to add recurrance");
				}
				
			}, id);
		}
		if(freq.equals("Monthly")) {
			rescheduleMonthlyRec(ctx);
		}
		
	}
	
	private void resetAllAlarms(Context ctx,Cursor c, boolean isExp) {
		WmmgAlarmManager alarm = new WmmgAlarmManager();
		AlarmManager mgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
		c.moveToFirst();
		do{
			int id = c.getInt(0);
			String freq = c.getString(8);
			boolean notify = c.getString(9).equals("yes");
			alarm.addRecurrence(mgr, ctx,id, freq, isExp, notify);
			
		}while(c.moveToNext());
		
	}

	void rescheduleMonthlyRec(Context ctx) {
		WmmgAlarmManager alarm = new WmmgAlarmManager();
		AlarmManager mgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
		alarm.cancelRecurrence(mgr, ctx, id, freq, isIncome, notify);
		alarm.addRecurrence(mgr, ctx, id, freq, isIncome, notify);
		
	}

}
