/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	Ratebox
 * 
 * 	Package: 	com.RockwellChristopher.ratebox
 * 
 * 	File: 		MoviesArrayAdapter.java
 * 
 *	Purpose:	This custom array adapter will get the movie title and poster image and populate my ListView.
 * 
 */


package com.RockwellChristopher.ratebox;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Typeface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StoresArrayAdapter extends ArrayAdapter<Store>{
	Context context;

	private TextView storeText;
	static String TAG = "NETWORK DATA - MAINACTIVITY";

	private List<Store> stores = new ArrayList<Store>();

	public StoresArrayAdapter(Context context, int textViewResourceId,
			List<Store> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.stores = objects;
	}

	public Store getItem(int index) {
		return this.stores.get(index);
	}

	// get the ListView row and inflate it
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		
		// author: Android Cookbook
		// *** Begin ***
		// inflate list row
		if (row == null) {
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_row2, parent, false);
		}
		// *** End ***

		// Get item by position
		Store store = getItem(position);
		
		storeText = (TextView) row.findViewById(R.id.storeText);

		// set list item row values
		storeText.setText(store.storeStr);
		
		// custom typeface for movie title
		Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "MarkoOne-Regular.ttf");
		storeText.setTypeface(customFont);

		return row;
	}	
}