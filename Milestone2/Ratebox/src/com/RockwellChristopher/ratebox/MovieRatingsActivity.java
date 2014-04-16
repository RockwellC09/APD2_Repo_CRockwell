package com.RockwellChristopher.ratebox;

import com.RockwellChristopher.ratebox.GetRottenTomatoesData.getData;

import android.app.Activity;
import android.os.Bundle;

public class MovieRatingsActivity extends Activity {
	
	static String movieTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_ratings);
		
		GetRottenTomatoesData.getData data = new getData();
		data.execute(GetRottenTomatoesData.urlString);
	}

}
