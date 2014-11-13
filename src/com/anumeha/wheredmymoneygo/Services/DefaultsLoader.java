package com.anumeha.wheredmymoneygo.services;

import java.util.ArrayList;
import java.util.List;

import com.anumeha.wheredmymoneygo.Globals;
import com.anumeha.wheredmymoneygo.dbhelpers.CurrencyDbHelper;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

public class DefaultsLoader {

	public static interface DefaultsLoadedListener<T> {
		void defaultsLoaded(T data); //return list of currencies
		void notFirstTime(T data);//return default pie pref
	}


	public void setDefaults(DefaultsLoadedListener<List<String>> lstnr,Context ctx){
		CurrencyDbHelper curDb = new CurrencyDbHelper(ctx);
		SetPrefsTask task = new SetPrefsTask(lstnr,ctx.getApplicationContext(),curDb);
		task.execute();
	}
	
	static class SetPrefsTask extends AsyncTask<Void,Void,List<String>> {

		DefaultsLoadedListener<List<String>> loadlstnr;
		Context ctx;
		CurrencyDbHelper dbh;
		boolean firstLoad = true;
		
		public SetPrefsTask(DefaultsLoadedListener<List<String>> lstnr,Context ctx,CurrencyDbHelper dbh) {
			this.loadlstnr = lstnr;
			this.ctx = ctx;	
			this.dbh = dbh;
		}
		@Override
		protected List<String> doInBackground(Void... arg0) {
			List<String> data = new ArrayList<String>();
			SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(ctx);
			if(prefs.contains(Globals.FIRST_LOAD)) {
				//not the first time, just return value of list/pie for income and expense
				data.add(prefs.getString(Globals.EXP_DEF_VIEWAS, "list"));
				data.add(prefs.getString(Globals.INC_DEF_VIEWAS, "list"));
				data.add(prefs.getString(Globals.DEF_CURRENCY, "USD"));
				firstLoad = false;
			} else {
				//add defaults
				setDefaults(prefs);
				//get currencies and send list back
				Cursor c = dbh.getAllCurrencies();
				c.moveToFirst();
				do{
					data.add(c.getString(0));
				}while(c.moveToNext());
				
			}
			return data;
			
		}
		
		@Override
		protected void onPostExecute(List<String> data){
			if(firstLoad) {
				loadlstnr.defaultsLoaded(data);
			} else {
				loadlstnr.notFirstTime(data);
			}
		
		}

		 private void setDefaults(SharedPreferences prefs) {
			    
		      Editor editor = prefs.edit();

		    /*  if(!prefs.contains("base_currency"))
			  editor.putString("base_currency", "USD");
		      
		      if(!prefs.contains("def_currency"))
			  editor.putString("def_currency", "USD");*/
		      
		      editor.putString(Globals.FIRST_LOAD, "done");
			  editor.putString(Globals.DEF_DATEFORMAT, "MMMM dd, yyyy");
			  editor.putInt(Globals.NUM_ALARMS, 0);
			  //----------------------------------------------

			  editor.putString(Globals.EXP_DEF_ORDERBY, "date(e_date)");
			  editor.putString(Globals.EXP_DEF_SORTORDER, "DESC"); 
			  editor.putString(Globals.EXP_VIEWBY, "all");
			  editor.putString(Globals.EXP_DEF_VIEWAS, "list");
			  editor.putString(Globals.EXP_FILTER, "");
			  
			  //---------------------------------------------
			  editor.putString(Globals.INC_DEF_ORDERBY, "date(i_date)");
			  editor.putString(Globals.INC_DEF_SORTORDER, "DESC"); 
			  editor.putString(Globals.INC_VIEWBY, "all");
			  editor.putString(Globals.INC_DEF_VIEWAS, "list");
			  editor.putString(Globals.INC_FILTER, "");
			  
			  editor.commit();
			  
		}

		
		
	}
}

