package com.anumeha.wheredmymoneygo.services;

import java.io.File;
import java.io.FileWriter;

import com.anumeha.wheredmymoneygo.dbhelpers.CategoryDbHelper;
import com.anumeha.wheredmymoneygo.dbhelpers.ExpenseDbHelper;
import com.anumeha.wheredmymoneygo.dbhelpers.IncomeDbHelper;
import com.anumeha.wheredmymoneygo.dbhelpers.SourceDbHelper;

import android.app.ProgressDialog;
import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Environment;

public class BackupOps {

	public static interface BackupCreatedListener<T> {
		void OnSuccess(T data);
		void OnFaiure(int errCode);
	}
	
	public void createBackup(BackupCreatedListener<String> lstnr, Context ctx){
		BackupCreater task = new BackupCreater(lstnr,ctx);
		task.execute();
	}
	
	private class BackupCreater extends AsyncTask<Void, Void, String>{
		
		BackupCreatedListener<String> lstnr;
		ExpenseDbHelper expDbh;
		IncomeDbHelper incDbh;
		CategoryDbHelper catDbh;
		SourceDbHelper souDbh;
		File internalDir;
		ProgressDialog pd;
		BackupCreater(BackupCreatedListener<String> lstnr, Context ctx){
			
			this.lstnr = lstnr;
			expDbh = new ExpenseDbHelper(ctx);
			incDbh = new IncomeDbHelper(ctx);
			catDbh = new CategoryDbHelper(ctx);
			souDbh = new SourceDbHelper(ctx);
			internalDir = ctx.getFilesDir();
			pd = new ProgressDialog(ctx);
		}
		
		@Override
		protected void onPreExecute() {
			pd.setMessage("Creating Backup..");
			pd.show();
		}
		@Override
		protected String doInBackground(Void... arg0) {
			
			File dir;
			if(isExternalStorageWritable()){ //use external storage if available
				File sdCard = Environment.getExternalStorageDirectory();
				dir = new File (sdCard.getAbsolutePath() + "/wmmgbackup/");
				dir.mkdirs();				
			} else {
				dir = internalDir;
			}
			boolean success;
			Cursor c = expDbh.getExpensesForBackup();
			success = writeBackupToFile(c, "wmmgExpensebackup.csv",dir);
			if(!success) {
				return null;
			}
			c = incDbh.getIncomesForBackup();
			success = writeBackupToFile(c, "wmmgIncomebackup.csv",dir);
			if(!success) {
				return null;
			}
			c = catDbh.getAllCategories();
			success = writeBackupToFile(c, "wmmgCategorybackup.csv",dir);
			if(!success) {
				return null;
			}
			c = souDbh.getAllSources();
			success = writeBackupToFile(c, "wmmgSourcebackup.csv",dir);
			if(!success) {
				return null;
			}
			return dir.toString();
		}
		
		@Override
		protected void onPostExecute(String dir) {
			
			if(dir == null) {
				if(pd.isShowing())
				pd.dismiss();
				lstnr.OnFaiure(0);
			} else {
				pd.dismiss();
				lstnr.OnSuccess(dir); 
			}
			
		}
		
		private boolean writeBackupToFile(Cursor c, String fileName, File dir) {
			
			File file = new File(dir,fileName);
			file.setReadable(true, false);
			FileWriter writer; 
			c.moveToFirst();
			
			try {
				writer = new FileWriter(file);
				do{
					
						boolean first = true;
						for(int i =0; i< c.getColumnCount();i++) {
							if(first) {
								writer.append(c.getString(i));
								first = false;
							} else {
								writer.append(","+c.getString(i));
							}
						}
						writer.append('\n');
					
				}while(c.moveToNext());
				writer.flush();
				writer.close();
			}catch(Exception e){
				return false;
			}
			c.close();
			return true;
		}
		public boolean isExternalStorageWritable() {
		    String state = Environment.getExternalStorageState();
		    if (Environment.MEDIA_MOUNTED.equals(state)) {
		        return true;
		    }
		    return false;
		}

		
	}
}
