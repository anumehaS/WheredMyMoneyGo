package com.anumeha.wheredmymoneygo.Services;

import java.util.ArrayList;
import java.util.List;

import com.anumeha.wheredmymoneygo.DBhelpers.CurrencyDbHelper;

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
			if(prefs.contains("firstLoad")) {
				//not the first time, just return value of list/pie for income and expense
				data.add(prefs.getString("exp_def_viewAs", "list"));
				data.add(prefs.getString("inc_def_viewAs", "list"));
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

		      editor.putString("firstLoad", "done");
			  editor.putString("def_dateformat", "MMMM dd, yyyy");
			  //----------------------------------------------

			  editor.putString("exp_def_orderBy", "date(e_date)");
			  editor.putString("exp_def_sortOrder", "DESC"); 
			  editor.putString("exp_viewBy", "all");
			  editor.putString("exp_def_viewAs", "list");
			  editor.putString("exp_filter", "");
			  
			  //---------------------------------------------
			  editor.putString("inc_def_orderBy", "date(i_date)");
			  editor.putString("inc_def_sortOrder", "DESC"); 
			  editor.putString("inc_viewBy", "all");
			  editor.putString("inc_def_viewAs", "list");
			  editor.putString("inc_filter", "");
			  
			  editor.commit();
			  
		}

		
		
	}
}

