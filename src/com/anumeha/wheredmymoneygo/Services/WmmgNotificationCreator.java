package com.anumeha.wheredmymoneygo.Services;

import com.example.wheredmymoneygo.R;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;

public class WmmgNotificationCreator {
	
	public WmmgNotificationCreator() {	
	}
	
	public static void createNotification (Context ctx, Class startActivity, int id,String contentTitle, String contentText) {
		int mId = 0;
		NotificationCompat.Builder mBuilder =
		        new NotificationCompat.Builder(ctx)
		        .setSmallIcon(R.drawable.ic_launcher)
		        .setContentTitle(contentTitle)
		        .setContentText(contentText);
		// Creates an explicit intent for an Activity in your app
		Intent resultIntent = new Intent(ctx, startActivity);
		resultIntent.putExtra("id",id);
		resultIntent.putExtra("notify",true);
		TaskStackBuilder stackBuilder = TaskStackBuilder.create(ctx);
		// Adds the back stack for the Intent (but not the Intent itself)
		stackBuilder.addParentStack(startActivity);
		// Adds the Intent that starts the Activity to the top of the stack
		stackBuilder.addNextIntent(resultIntent);
		PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(resultPendingIntent);
		NotificationManager mNotificationManager =
		    (NotificationManager) ctx.getSystemService(Context.NOTIFICATION_SERVICE);
		// mId allows you to update the notification later on.
		mNotificationManager.notify(mId, mBuilder.build());
	}
}
