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
import android.widget.Filter;
import android.widget.TextView;

import com.loopj.android.image.SmartImageView;

public class MoviesArrayAdapter extends ArrayAdapter<Movie>{
	Context context;

	private SmartImageView img;
	private TextView title;
	static String TAG = "NETWORK DATA - MAINACTIVITY";

	private List<Movie> movies = new ArrayList<Movie>();

	public MoviesArrayAdapter(Context context, int textViewResourceId,
			List<Movie> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		this.movies = objects;
	}

	public Movie getItem(int index) {
		return this.movies.get(index);
	}

	// get the ListView row and inflate it
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;

		if (row == null) {
			// ROW INFLATION
			//Log.d("Starting: ", "XML Row Inflation ... ");
			LayoutInflater inflater = (LayoutInflater) this.getContext()
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			row = inflater.inflate(R.layout.list_row, parent, false);
			//Log.d("Success: ", "Successfully completed XML Row Inflation!");
		}

		// Get item by position
		Movie movie = getItem(position);
		
		title = (TextView) row.findViewById(R.id.title);
		img = (SmartImageView) row.findViewById(R.id.img);

		// set list item row values
		title.setText(movie.movTitle);
		img.setImageUrl(movie.movImage);
		
		// custom typeface for movie title
		Typeface customFont = Typeface.createFromAsset(getContext().getAssets(), "MarkoOne-Regular.ttf");
		title.setTypeface(customFont);

		return row;
	}
	
	// filter movies by title
	public class MovieFilter extends Filter {

		@Override
		protected FilterResults performFiltering(CharSequence constraint) {
			// TODO Auto-generated method stub

			constraint = constraint.toString();

			FilterResults newFilterResults = new FilterResults();

			if (constraint != null && constraint.length() > 0) {


				List<Movie> auxData = new ArrayList<Movie>();

				for (int i = 0; i < movies.size(); i++) {
					if (movies.get(i).movTitle.contains(constraint))
						auxData.add(movies.get(i));
				}

				newFilterResults.count = auxData.size();
				newFilterResults.values = auxData;
			} else {

				newFilterResults.count = movies.size();
				newFilterResults.values = movies;
			}

			return newFilterResults;
		}

		@SuppressWarnings("unchecked")
		@Override
		protected void publishResults(CharSequence constraint, FilterResults results) {

			List<Movie> resultData = new ArrayList<Movie>();

			resultData = (List<Movie>) results.values;
			
			if (resultData.size() == 0) {
				MainActivity.emptyTv.setVisibility(View.VISIBLE);
			}

			MoviesArrayAdapter adapter = new MoviesArrayAdapter(context, R.layout.list_row, resultData);

			MainActivity.moviesList.setAdapter(adapter);

		}

	}
	@Override
	public Filter getFilter() {
		Filter filter = null;

		if(filter == null)
			filter = new MovieFilter();
		return filter;
	}
}
