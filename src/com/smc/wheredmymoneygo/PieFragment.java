package com.smc.wheredmymoneygo;

import java.util.ArrayList;
import java.util.List;

import com.smc.wheredmymoneygo.PieChart;
import com.smc.wheredmymoneygo.PieLegendCursorAdapter;
import com.smc.wheredmymoneygo.expense.ExpenseCursorLoader;
import com.smc.wheredmymoneygo.income.IncomeCursorLoader;

import android.app.Activity;
import android.app.Fragment;
import android.app.LoaderManager.LoaderCallbacks;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class PieFragment extends Fragment implements LoaderCallbacks<Cursor>{
	
	static final String EXP_TAG = "Expense_Pie";
	static final String INC_TAG = "Income_Pie";
	View view;
	List<String> categories;
	ImageView imgView;
	PieChart pie;
	TextView noExp;
	PieLegendCursorAdapter legendAdapter;
	ListView legend;
	Boolean isExpense = true;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true); // handle orientation changes
    }
	
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
		 String myTag = getTag();
	        if(myTag.equals(INC_TAG)) {
	        	isExpense = false;
	        }
	    view = inflater.inflate(R.layout.pie_fragment, container, false);   
	    return view;
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		categories = new ArrayList<String>();		
		pie = (PieChart) view.findViewById(R.id.expPieChart);
		noExp = (TextView) view.findViewById(R.id.expNotPresent);
		legend = (ListView)view.findViewById(R.id.legendListView);
		
		 String myTag = getTag();
	        if(myTag.equals(INC_TAG)) {
	        	isExpense = false;
	        }
		if(isExpense)
			getLoaderManager().initLoader(0, null,this); // cursor loader
		else
			getLoaderManager().initLoader(1, null,this);

	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);   
        String myTag = getTag();
        if(myTag.equals(INC_TAG)) {
        	isExpense = false;
        }
    }
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		if(isExpense)
			return new ExpenseCursorLoader(getActivity(),3); //to get expenses and categories
		else
			return new IncomeCursorLoader(getActivity(),3);
	}
	 
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		if(cursor.getCount()!= 0) {
			noExp.setVisibility(View.GONE);
			pie.setCursor(cursor);
			pie.setPieView(view);
			pie.invalidate();
			legendAdapter = new PieLegendCursorAdapter(getActivity(),R.layout.pie_legend_row,cursor,MainActivity.defaultCurrency);
			legend.setAdapter(legendAdapter);
		}
		else {
			pie.setCursor(null);
			pie.setPieView(view);
			pie.invalidate();
			
			noExp.setVisibility(View.VISIBLE);
			if(isExpense)
				noExp.setText("No expenses found!");
			else
				noExp.setText("No transactions found!");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		pie.setCursor(null);
	}
	
	public void restartLoader()	{
		if(isExpense) 
			getLoaderManager().restartLoader(0, null,this);
		else
			getLoaderManager().restartLoader(1, null,this);
	}

}
