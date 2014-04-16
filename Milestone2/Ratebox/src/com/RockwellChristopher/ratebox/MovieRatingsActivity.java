package com.RockwellChristopher.ratebox;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

public class MovieRatingsActivity extends Activity {
	
	static String movieTitle;
	static String _urlString = "https://api.redbox.com/v3/products/movies?apiKey=de9d264f6780232f9da733b63d4569ee&pageSize=15&pageNum=1";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_ratings);
		
		Log.i("Title", movieTitle);
	}

}
