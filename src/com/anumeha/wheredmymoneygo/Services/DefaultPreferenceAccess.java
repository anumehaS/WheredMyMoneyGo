package com.anumeha.wheredmymoneygo.services;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;


public class DefaultPreferenceAccess {
	
	public static interface PrefLoadedListener<T> {
		void OnSuccess(T data);
		void OnFaiure(int errCode);
	}
	
	public static interface PrefAddedListener<T> {
		void OnSuccess();
		void OnFaiure(int errCode);
	}

	public DefaultPreferenceAccess() {
	}
	
	public void getValues(PrefLoadedListener<List<String>> lstnr, List<String> keys, Context ctx){
		ManagePrefsTask task = new ManagePrefsTask(lstnr,ctx.getApplicationContext());
		task.execute(keys);
	}
	public void addValues(PrefAddedListener<List<String>> lstnr, List<String> keys,List<String> values, Context ctx){
		ManagePrefsTask task = new ManagePrefsTask(lstnr,ctx.getApplicationContext());
		task.execute(keys,values);
	}
	
	static class ManagePrefsTask extends AsyncTask<List<String>,Void,List<String>> {
		SharedPreferences prefs;
		Context ctx;
		PrefLoadedListener<List<String>> loadlstnr;
		PrefAddedListener<List<String>> addlstnr;
		boolean fetch = true; // if it is a task for getting only
		
		public ManagePrefsTask(PrefLoadedListener<List<String>> lstnr,Context ctx) {
			this.loadlstnr = lstnr;
			this.ctx = ctx;		
		}
		
		public ManagePrefsTask(PrefAddedListener<List<String>> lstnr,Context ctx) {
			this.addlstnr = lstnr;
			this.ctx = ctx;	
			fetch = false;
		}
		
		@Override
		protected List<String> doInBackground(List<String>...params) {
			
			List<String> values = new ArrayList<String>();
			prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
			
			ctx = null;
			if(prefs == null) {
				return null;
			}
			if(fetch) {
				for(String s: params[0]) {
					values.add(prefs.getString(s, ""));
				}				
			} else {
				Iterator<String> itr = params[1].iterator();
				Editor editor = prefs.edit();
				for(String key: params[0]) {
					editor.putString(key, itr.next());
				}	
				editor.apply();
			}
			
			return values;
		}
		
		@Override
		protected void onPostExecute(List<String> values){
			
			if(fetch) {
				if(values == null){
					loadlstnr.OnFaiure(0);//prefs was null or no values were passed
				} else {
					loadlstnr.OnSuccess(values);
				}
			}else { // if preferences were to be updated
				if(values == null){
					addlstnr.OnFaiure(0);//prefs was null 
				} else {
					addlstnr.OnSuccess();
				}
			}
		}
		
	}
}
