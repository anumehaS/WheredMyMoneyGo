package com.anumeha.wheredmymoneygo.Services;


import java.util.Calendar;
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
	private String freq, old_freq;
	private boolean  add,remove,isIncome, notify, old_notify;
	private AlarmManager alarmMgr;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);	
		
		SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this.getApplicationContext());
		int numAlarms = prefs.getInt("num_alarms", 0);
		Editor editor = prefs.edit();
		id = (int)getIntent().getLongExtra("rec_id",0);
		freq = getIntent().getStringExtra("rec_freq");
		add = getIntent().getBooleanExtra("rec_add",true);
		isIncome = getIntent().getBooleanExtra("rec_isIncome",false);
		remove = getIntent().getBooleanExtra("rec_rem",true);
		notify = getIntent().getBooleanExtra("rec_notify",false);
		old_freq = getIntent().getStringExtra("old_freq");
		old_notify = getIntent().getBooleanExtra("old_notify",false);
		
		alarmMgr = (AlarmManager)this.getSystemService(Context.ALARM_SERVICE);
		
		if(remove) //delete
			{
				numAlarms--;
				
				if(numAlarms == 0){
					disableReceiver();
				}
				editor.putInt("num_alarms", numAlarms);
				cancelRecurrence(alarmMgr,id,old_freq,isIncome,old_notify);
			}
		else {
			if(add) //insert
			{				
				if(numAlarms == 0){
					enableReceiver();
				}
				numAlarms++;
				editor.putInt("num_alarms", numAlarms);
				addRecurrence(alarmMgr,id,freq,isIncome,notify);
			}
			else // edit 
			{
				if(!old_freq.equals("Do not repeat")) {
					cancelRecurrence(alarmMgr,id,old_freq,isIncome,old_notify);
				}
				if(!freq.equals("Do not repeat")) {
					addRecurrence(alarmMgr,id,freq,isIncome,notify);
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

	public void addRecurrence(AlarmManager alarmMgr, int id, String freq, boolean isIncome, boolean notify) {
		long duration = AlarmManager.INTERVAL_DAY;
			Intent intent = new Intent(this, RecEventReceiver.class);
			intent.putExtra("isIncome", isIncome);
			intent.putExtra("rec_notify", notify );
			intent.putExtra("freq",freq);
			intent.putExtra("id", id);
			
		
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
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, id, intent, 0);
		alarmMgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + duration, duration, alarmIntent);
	}
	 public void cancelRecurrence(AlarmManager alarmMgr, int id, String old_freq, boolean isIncome, boolean notify) {
		Intent intent = new Intent(this, RecEventReceiver.class);
		PendingIntent alarmIntent = PendingIntent.getBroadcast(this, id, intent, 0);
		if(alarmMgr != null)
			alarmMgr.cancel(alarmIntent);
	}
	 
	 public void addRecurrence(AlarmManager alarmMgr,Context ctx, int id, String freq, boolean isIncome, boolean notify) {
			long duration = AlarmManager.INTERVAL_DAY;
				Intent intent = new Intent(ctx, RecEventReceiver.class);
				intent.putExtra("isIncome", isIncome);
				intent.putExtra("rec_notify", notify );
				intent.putExtra("freq",freq);
				intent.putExtra("id", id);
				
			
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
			PendingIntent alarmIntent = PendingIntent.getBroadcast(ctx, id, intent, 0);
			alarmMgr.setRepeating(AlarmManager.RTC, System.currentTimeMillis() + duration, duration, alarmIntent);
		}
		 public void cancelRecurrence(AlarmManager alarmMgr,Context ctx, int id, String old_freq, boolean isIncome, boolean notify) {
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
