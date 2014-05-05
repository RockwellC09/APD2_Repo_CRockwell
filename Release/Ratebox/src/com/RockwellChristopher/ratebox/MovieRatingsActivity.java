package com.RockwellChristopher.ratebox;

import java.text.Normalizer;

import android.app.Activity;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.flurry.android.FlurryAdListener;
import com.flurry.android.FlurryAdSize;
import com.flurry.android.FlurryAdType;
import com.flurry.android.FlurryAds;
import com.flurry.android.FlurryAgent;

public class MovieRatingsActivity extends Activity implements FlurryAdListener {
	
	static TextView title;
	static TextView critic;
	static TextView audience;
	static TextView synopsis;
	static TextView criticTitle;
	static TextView audienceTitle;
	static TextView synopsisTitle;
	String urlStr;
	FrameLayout mBanner;
    private String adSpace="MediatedBannerBottom";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_movie_ratings);
		
		// inflate the menu; this adds items to the action bar if it is present.
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
		mBanner = (FrameLayout) findViewById(R.id.banner);
		
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
		
		// get movie title from bundle extra
		Bundle bun = getIntent().getExtras();
		String movTitle = bun.getString("MOV_TITLE");
		
		urlStr = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=bf72tfc2zjfbdscenpwx2e2r&q="
							+ movTitle + "&page_limit=1";
		String encodedUrlStr = new String(urlStr.replaceAll(" ", "%20"));
		String normalizedStr = Normalizer.normalize(encodedUrlStr, Normalizer.Form.NFD);
		GetApiData.code = 1;
		GetApiData data = new GetApiData();
		data.execute(normalizedStr);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		FlurryAgent.onStartSession(this, "M5DN3CT23BH2CMWQY7MD");
        // get callbacks for ad events
        FlurryAds.setAdListener(this);
        // fetch and prepare ad for this ad space. won't render one yet
        FlurryAds.fetchAd(this, adSpace, mBanner, FlurryAdSize.BANNER_BOTTOM);
	}
	 
	@Override
	protected void onStop() {
		super.onStop();		
		FlurryAds.removeAd(this, adSpace, mBanner);
		FlurryAgent.onEndSession(this);
	}

	@Override
	public void onAdClicked(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdClosed(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAdOpened(String arg0) {
		// TODO Auto-generated method stub
	}

	@Override
	public void onApplicationExit(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRenderFailed(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onRendered(String arg0) {
		// TODO Auto-generated method stub
		mBanner.measure(0, 0);
		int height = mBanner.getMeasuredHeight();
		Log.i("Banner Height", "" + height);
		ViewGroup.MarginLayoutParams marglp = (ViewGroup.MarginLayoutParams) synopsis.getLayoutParams();

		//mlp.setMargins(adjustmentPxs, 0, 0, 0);
		marglp.setMargins(0, marglp.topMargin, 0, height + 5); //substitute parameters for left, top, right, bottom
		//synopsis.setLayoutParams(params);
	}

	@Override
	public void onVideoCompleted(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean shouldDisplayAd(String arg0, FlurryAdType arg1) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void spaceDidFailToReceiveAd(String arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void spaceDidReceiveAd(String arg0) {
		// TODO Auto-generated method stub
		FlurryAds.displayAd(this, adSpace, mBanner);
	}

}
