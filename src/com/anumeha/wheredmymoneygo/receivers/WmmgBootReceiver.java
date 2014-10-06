package com.anumeha.wheredmymoneygo.receivers;

import com.anumeha.wheredmymoneygo.DBhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.DBhelpers.IncomeDbHelper;
import com.anumeha.wheredmymoneygo.Expense.ExpenseAlarmManager;
import com.anumeha.wheredmymoneygo.Income.IncomeAlarmManager;
import com.anumeha.wheredmymoneygo.Services.AlarmOps;
import com.anumeha.wheredmymoneygo.Services.AlarmOps.OnAlarmOpsCompleted;
import com.anumeha.wheredmymoneygo.Services.WmmgAlarmManager;
import com.anumeha.wheredmymoneygo.Services.WmmgNotificationCreator;

import android.app.AlarmManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.preference.PreferenceManager;
import android.util.Log;

public class WmmgBootReceiver extends BroadcastReceiver{

	boolean isIncome,notify; 
	String freq;
	int id;
	
	private static final String DEBUG_TAG = "BootReceiver";
	@Override
	public void onReceive(Context ctx, Intent intent) {
		
		 if (intent.getAction()!= null && intent.getAction().equals("android.intent.action.BOOT_COMPLETED")) {
	           //reset alarms for expenses
			 	ExpenseDbHelper expDb = new ExpenseDbHelper(ctx);
			 	Cursor c = expDb.getAllRecurences();
			 	resetAllAlarms(ctx,c,false);
			 	
			 	//reset alarms for Incomes
			 	IncomeDbHelper incDb = new IncomeDbHelper(ctx);
			 	c = incDb.getAllRecurences();
			 	resetAllAlarms(ctx,c,true);
			 	
		 }
		
		Log.d(DEBUG_TAG,"In Boot receiver");

	}
	
	private void resetAllAlarms(Context ctx,Cursor c, boolean isInc) {
		
		//Intent i = new Intent (ctx,com.anumeha.wheredmymoneygo.Expense.ExpenseAlarmManager.class);
		
		WmmgAlarmManager alarm;
		if(!isInc){
			alarm = new ExpenseAlarmManager();
		}else {
			alarm = new IncomeAlarmManager();
		}
		
		AlarmManager mgr = (AlarmManager)ctx.getSystemService(Context.ALARM_SERVICE);
		c.moveToFirst();
		do{
			int id = c.getInt(0);
			String freq = c.getString(8);
			boolean notify = c.getString(9).equals("yes");
			alarm.addRecurrence(mgr, ctx,id, freq, isInc, notify);
			
		}while(c.moveToNext());
		
	}

}
