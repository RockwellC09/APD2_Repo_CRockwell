package com.RockwellChristopher.ratebox;

import com.RockwellChristopher.ratebox.GetApiData.getData;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class MovieRatingsActivity extends Activity {
	
	static String movieTitleStr;
	static TextView title;
	static TextView critic;
	static TextView audience;
	static TextView synopsis;
	static TextView criticTitle;
	static TextView audienceTitle;
	static TextView synopsisTitle;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_ratings);
		
		// Inflate the menu; this adds items to the action bar if it is present.
    	// set custom action bar title
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_textview, null);

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
        
        // custom typeface
  		Typeface customFont = Typeface.createFromAsset(MovieRatingsActivity.this.getAssets(), "RammettoOne-Regular.ttf");
  		TextView actionTv = (TextView) findViewById(R.id.actionbar_tv);
  		actionTv.setTypeface(customFont);
		
		// initialize views
		title = (TextView) findViewById(R.id.movTitle);
		critic = (TextView) findViewById(R.id.ratingCritic);
		audience = (TextView) findViewById(R.id.ratingAudience);
		synopsis = (TextView) findViewById(R.id.synopsis);
		criticTitle = (TextView) findViewById(R.id.criticTitle);
		audienceTitle = (TextView) findViewById(R.id.audienceTitle);
		synopsisTitle = (TextView) findViewById(R.id.synopsisTitle);
		
		// custom typeface
		Typeface customFont2 = Typeface.createFromAsset(MovieRatingsActivity.this.getAssets(), "Raleway-Medium.ttf");
		Typeface customFont3 = Typeface.createFromAsset(MovieRatingsActivity.this.getAssets(), "Raleway-Bold.ttf");
		title.setTypeface(customFont3);
		critic.setTypeface(customFont2);
		audience.setTypeface(customFont2);
		synopsis.setTypeface(customFont2);
		criticTitle.setTypeface(customFont3);
		audienceTitle.setTypeface(customFont3);
		synopsisTitle.setTypeface(customFont3);
		
		synopsis.setMovementMethod(new ScrollingMovementMethod());
		
		GetApiData.code = 1;
		GetApiData.getData data = new getData();
		data.execute(GetApiData._urlString);
	}

}
