package com.anumeha.wheredmymoneygo.Services;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.anumeha.wheredmymoneygo.Globals;
import com.anumeha.wheredmymoneygo.receivers.RecEventReceiver;
import com.anumeha.wheredmymoneygo.receivers.WmmgBootReceiver;
import com.example.wheredmymoneygo.R;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.os.Bundle;
import android.preference.PreferenceManager;

public class WmmgAlarmManager extends Activity{
	
	private int id;
	private String freq, old_freq,date;
	private boolean  add,remove,isIncome, notify;
	private AlarmManager alarmMgr;
	
	public static final String REC_ID = "rec_id";
	public static final String REC_FREQ = "rec_freq";
	public static final String REC_ADD = "rec_add";
	public static final String REC_ISINCOME = "rec_isIncome";
	public static final String REC_REMOVE = "rec_rem";
	public static final String REC_NOTIFY = "rec_notify";
	public static final String OLD_FREQ = "old_freq";
	public static final String REC_DATE = "rec_date";

	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);	
		
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		int numAlarms = prefs.getInt(Globals.NUM_ALARMS, 0);
		Editor editor = prefs.edit();
		id = (int)getIntent().getLongExtra(REC_ID,0);
		freq = getIntent().getStringExtra(REC_FREQ);
		add = getIntent().getBooleanExtra(REC_ADD,true);
		isIncome = getIntent().getBooleanExtra(REC_ISINCOME,false);
		remove = getIntent().getBooleanExtra(REC_REMOVE,true);
		notify = getIntent().getBooleanExtra(REC_NOTIFY,false);
		old_freq = getIntent().getStringExtra(OLD_FREQ);
		date = getIntent().getStringExtra(REC_DATE);
		
		
		
		alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		
		if(remove) //delete
			{
				numAlarms--;
				
				if(numAlarms == 0){
					disableReceiver();
				}
				editor.putInt(Globals.NUM_ALARMS, numAlarms);
				cancelRecurrence(alarmMgr);
			}
		else {
			if(add) //insert
			{				
				if(numAlarms == 0){
					enableReceiver();
				}
				numAlarms++;
				editor.putInt(Globals.NUM_ALARMS, numAlarms);
				addRecurrence(alarmMgr);
			}
			else // edit 
			{
				//if the recurence did exist, cancel it 
				if(!old_freq.equals("Do not repeat")) {
					cancelRecurrence(alarmMgr);
				}
				//if we have to schedule a new recurrence, do it
				if(!freq.equals("Do not repeat")) {
					addRecurrence(alarmMgr);
				}
				
			}
		}
		editor.commit();
		endActivity("rec added");
	}
	public void endActivity(String res) {	
		 
		 Intent data = new Intent();
		 data.putExtra("result",res);
		  // Activity finished ok, return the data
		  setResult(RESULT_OK, data);	 		 
			this.finish();			
		}

	public void addRecurrence(AlarmManager alarmMgr) {
		long duration = AlarmManager.INTERVAL_DAY;
			Intent intent = new Intent(this, RecEventReceiver.class);
			intent.putExtra(REC_ISINCOME, isIncome);
			intent.putExtra(REC_NOTIFY, notify );
			intent.putExtra(REC_FREQ,freq);
			intent.putExtra(REC_ID, id);
			intent.putExtra(REC_DATE, date);
			
		
		Resources res = getResources();
		String[] frqs = res.getStringArray(R.array.frequency_spinner_items);
		int pos = 1;
		for(int i =0; i< frqs.length;i++) {
			if(freq.equals(frqs[i])) {
				pos = i;
				break;
			}
		}
		
		switch(pos) {
		case 1: //daily
			duration = 1000*60;
			//duration = AlarmManager.INTERVAL_DAY;
			break;
		case 2: //weekly
			duration = AlarmManager.INTERVAL_DAY*7;		
		case 3: //monthly
			Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(calendar.getTimeInMillis());
			calendar.add(Calendar.SECOND, 30);
			calendar.add(Calendar.MONTH, 1);
			duration = calendar.getTimeInMillis();
			
			break;	
		}
		
		SimpleDateFormat sdf = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT);
		long startTime = 0;
		try {
			Date recDate = sdf.parse(date);
			startTime = recDate.getTime();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, id, intent, 0);
		alarmMgr.setRepeating(AlarmManager.RTC, startTime + duration, duration, alarmIntent);
	}
	
	public void cancelRecurrence(AlarmManager alarmMgr) {
		Intent intent = new Intent(this, RecEventReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, id, intent, 0);
		if(alarmMgr != null)
			alarmMgr.cancel(alarmIntent);
	}
	 
	 public void addRecurrence(AlarmManager alarmMgr,Context ctx, int id, String freq, boolean isIncome, boolean notify, String date) {
			long duration = AlarmManager.INTERVAL_DAY;
				Intent intent = new Intent(ctx, RecEventReceiver.class);
				intent.putExtra(REC_ISINCOME, isIncome);
				intent.putExtra(REC_NOTIFY, notify );
				intent.putExtra(REC_FREQ,freq);
				intent.putExtra(REC_ID, id);
				intent.putExtra(REC_DATE, date);
				
			
			Resources res = ctx.getApplicationContext().getResources();
			String[] frqs = res.getStringArray(R.array.frequency_spinner_items);
			int pos = 1;
			for(int i =0; i< frqs.length;i++) {
				if(freq.equals(frqs[i])) {
					pos = i;
					break;
				}
			}
			
			switch(pos) {
			case 1: //daily
				duration = 1000*60;
				//duration = AlarmManager.INTERVAL_DAY;
				break;
			case 2: //weekly
				duration = AlarmManager.INTERVAL_DAY*7;		
			case 3: //monthly
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(calendar.getTimeInMillis());
				calendar.add(Calendar.SECOND, 30);
				calendar.add(Calendar.MONTH, 1);
				duration = calendar.getTimeInMillis();
				
				break;	
			}
			SimpleDateFormat sdf = new SimpleDateFormat(Globals.INTERNAL_DATE_FORMAT);
			long startTime = 0;
			try {
				Date recDate = sdf.parse(date);
				startTime = recDate.getTime();
			} catch (ParseException e) {
				e.printStackTrace();
			}
			PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, id, intent, 0);
			alarmMgr.setRepeating(AlarmManager.RTC, startTime + duration, duration, alarmIntent);
		}

	 public void cancelRecurrence(AlarmManager alarmMgr,Context ctx, int id) {
			Intent intent = new Intent(ctx, RecEventReceiver.class);
			PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, id, intent, 0);
			if(alarmMgr != null)
				alarmMgr.cancel(alarmIntent);
		}
	
	 public void enableReceiver(){
		 ComponentName receiver = new ComponentName(this, WmmgBootReceiver.class);
		 PackageManager pm = this.getPackageManager();

		 pm.setComponentEnabledSetting(receiver,
		         PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
		         PackageManager.DONT_KILL_APP);
	 }
	 
	 public void disableReceiver(){
		 ComponentName receiver = new ComponentName(this, WmmgBootReceiver.class);
		 PackageManager pm = this.getPackageManager();

		 pm.setComponentEnabledSetting(receiver,
		         PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
		         PackageManager.DONT_KILL_APP);
	 }
	 
	 

}
