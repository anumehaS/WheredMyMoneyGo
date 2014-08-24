package com.anumeha.wheredmymoneygo.receivers;

import com.anumeha.wheredmymoneygo.Services.AlarmOps;
import com.anumeha.wheredmymoneygo.Services.AlarmOps.OnAlarmOpsCompleted;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class RecEventReceiver extends BroadcastReceiver{

	private static final String DEBUG_TAG = "RecEventReceiver";
	@Override
	public void onReceive(Context ctx, Intent intent) {
			
		//check if notification needed and add info to open dialog for it
		//ELSE 
		boolean isIncome = intent.getBooleanExtra("isIncome",false);
		boolean notify = intent.getBooleanExtra("rec_notify", false );
		String freq = intent.getStringExtra("freq");
		int id = intent.getIntExtra("id",0);
		
		System.out.println("Freq:" + freq);
		AlarmOps ops = new AlarmOps(ctx,isIncome);
		ops.addRecToDb(new OnAlarmOpsCompleted(){

			@Override
			public void OnSuccess() {
				//add the reccurrence in the db - background task - fetch from db and add it into db again
				//reschedule rec if it was monthly
				System.out.println("Added");
			}

			@Override
			public void OnFaiure() {
				
			}
			
		}, id);
		
		
		
	}

}
