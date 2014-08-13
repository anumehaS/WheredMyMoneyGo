package com.anumeha.wheredmymoneygo;

import java.util.ArrayList;
import java.util.List;

import com.anumeha.wheredmymoneygo.PieChart;
import com.anumeha.wheredmymoneygo.PieLegendCursorAdapter;
import com.anumeha.wheredmymoneygo.Expense.ExpenseCursorLoader;
import com.anumeha.wheredmymoneygo.Income.IncomeCursorLoader;
import com.example.wheredmymoneygo.R;

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
	
	View view;
	List<String> categories;
	ImageView imgView;
	PieChart pie;
	TextView noExp;
	ExpenseCursorLoader expLoad;
	IncomeCursorLoader incLoad;
	PieLegendCursorAdapter legendAdapter;
	ListView legend;
	Boolean isExpense = true;
	@Override
	  public View onCreateView(LayoutInflater inflater, ViewGroup container,
	      Bundle savedInstanceState) {
	    view = inflater.inflate(R.layout.expense_chart_fragment, container, false);   
	    return view;
	  }
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		categories = new ArrayList<String>();		
		imgView = (ImageView) view.findViewById(R.id.expPieChart);
		noExp = (TextView) view.findViewById(R.id.expNotPresent);
		legend = (ListView)view.findViewById(R.id.legendListView);
		

	}

	@Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);   
        
        String tab = ((MainActivity)activity).currentTab;
        if(tab.equals("income")) {
        	incLoad = new IncomeCursorLoader(activity,3);
        	isExpense = false;
        } else {
        	expLoad = new ExpenseCursorLoader(activity,3);
        }
        getLoaderManager().initLoader(0, null,this); // cursor loader
    }
	
	
	@Override
	public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
		if(isExpense)
			return expLoad; //to get expenses and categories
		else
			return incLoad;
	}
	 
	@Override
	public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
		
		if(cursor.getCount()!= 0) {
			noExp.setVisibility(0);
			pie = new PieChart(imgView,cursor);
			imgView.setImageDrawable(pie);
			legendAdapter = new PieLegendCursorAdapter(getActivity(),R.layout.pie_legend_row,cursor);
			legend.setAdapter(legendAdapter);
		}
		else {
			noExp.setText("No expenses present!");
		}
	}

	@Override
	public void onLoaderReset(Loader<Cursor> arg0) {
		// TODO Auto-generated method stub
		
	}

}
