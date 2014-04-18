/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	Ratebox
 * 
 * 	Package: 	com.RockwellChristopher.ratebox
 * 
 * 	File: 		GetRedboxData.java
 * 
 *	Purpose:	This class will gather the Redbox inventory and Rotten Tomatoes Ratings and parse it to be displayed in the app. This class 
 *				will also check for a valid Internet connection.
 * 
 */

package com.RockwellChristopher.ratebox;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class GetApiData {
	static String _urlString;
	static String TAG = "NETWORK DATA - MAINACTIVITY";
	static JSONArray links;
	static JSONObject imgs, castLinks;
	static String linkStr, titleStr;
	static String title, criticScore, audienceScore, synopsis;
	static ArrayList<String> titles = new ArrayList<String>();
	static ArrayList<String> imgLinks = new ArrayList<String>();
	static int code;
	
	// create async task
		public static class getData extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				// check for which API need to be called
				if (code == 0) {
					_urlString = "https://api.redbox.com/v3/products/movies?apiKey=de9d264f6780232f9da733b63d4569ee&pageSize=30&pageNum=1";
				} else if (code == 1) {
					String urlStr = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=bf72tfc2zjfbdscenpwx2e2r&q="
							+ MovieRatingsActivity.movieTitleStr + "&page_limit=1";
					_urlString = new String(urlStr.replaceAll(" ", "%20"));
				}
				
				String responseString = "";
				try {
					URL url = new URL(_urlString);
					responseString = getResponse(url);
				} catch (MalformedURLException e) {
					responseString = "Something went wrong";
					e.printStackTrace();
				}
				return responseString;
			}

			@Override
			protected void onPostExecute(String result) {
				// parse JSON Data
	            try {
	            	if (code == 0) {
	            		readReboxJson(result);
	            	} else if (code == 1) {
	            		readRottenJson(result);
	            	}
				} catch (JSONException e) {
					e.printStackTrace();
					Toast.makeText(MainActivity.context, "Something went wrong while retrieving the data. Please check your " +
							"connection and try again.", Toast.LENGTH_LONG).show();
				}
				//Log.i("Works:", result);
				super.onPostExecute(result);
			}

		}
		
		// check to see if user have a valid Internet connection
		public static boolean connectionStatus(Context context) {
			boolean isConnected = false;
			ConnectivityManager ConnectMngr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
			NetworkInfo netInfo = ConnectMngr.getActiveNetworkInfo();
			if (netInfo != null) {
				if (netInfo.isConnected()) {
					isConnected = true;
				}
			}

			return isConnected;
		}

		public static String getResponse(URL url) {
			String response = "";
			try {
				HttpURLConnection connect = (HttpURLConnection) url
						.openConnection();
				if (code == 0) {
					connect.setRequestProperty("Accept", "application/json");
				}
				connect.setRequestMethod("GET");
				connect.connect();
				byte[] data = StreamUtils.streamToBytes(connect.getInputStream());
				response = new String(data);
			} catch (IOException e) {
				response = "Something went wrong";
				e.printStackTrace();
			}

			return response;
		}
		
		// parse Redbox JSON object
		public static void readReboxJson(String jsonStr) throws JSONException {

			JSONObject obj = new JSONObject(jsonStr);
			JSONObject castObj = obj.getJSONObject("Products");
			JSONArray movies = castObj.getJSONArray("Movie");
			
			for (int i = 0; i < movies.length(); i++) {
				JSONObject movie = movies.getJSONObject(i);
				imgs = movie.getJSONObject("BoxArtImages");
				links = imgs.getJSONArray("link");
				castLinks = links.getJSONObject(0);
				titleStr = movie.getString("Title");
				linkStr = castLinks.getString("@href");
				
				// don't get the Blue Ray movie titles (they will be retrieve when getting the local machines inventory)
				if (!movie.getString("Title").toLowerCase().contains("(Blu-ray)".toLowerCase())) {
					titles.add(titleStr);
					imgLinks.add(linkStr);
				}
			}
			
			// call the loadData function to load data into the ListView
			MainActivity.loadData();

		}
		
		// parse Rotten Tomatoes JSON object
		public static void readRottenJson(String jsonStr) throws JSONException {
			// read JSON and set TextView text

			try {
				// get proper movie info
				
				JSONObject obj = new JSONObject(jsonStr);
				String total = obj.getString("total");
				if (total.equals("0")) {
					// hide views and display no data message
					MovieRatingsActivity.criticTitle.setVisibility(View.GONE);
					MovieRatingsActivity.audienceTitle.setVisibility(View.GONE);
					MovieRatingsActivity.synopsisTitle.setVisibility(View.GONE);
					MovieRatingsActivity.title.setText("No Rating Data Available");
				} else {
					// show views
					MovieRatingsActivity.criticTitle.setVisibility(View.VISIBLE);
					MovieRatingsActivity.audienceTitle.setVisibility(View.VISIBLE);
					MovieRatingsActivity.synopsisTitle.setVisibility(View.VISIBLE);
					
					JSONArray movies = obj.getJSONArray("movies");

					JSONObject castObj = movies.getJSONObject(0);
					JSONObject antrCastObj = castObj.getJSONObject("ratings");
					title = castObj.getString("title");
					criticScore = antrCastObj.getString("critics_score");
					audienceScore = antrCastObj.getString("audience_score");
					synopsis = castObj.getString("synopsis");
					
					// check to see if there's a rating and/or synopsis available for the current movie
					if (criticScore.equals("-1") && audienceScore.equals("-1") || audienceScore.equals("0")) {
						// hide views and display no data message
						MovieRatingsActivity.criticTitle.setVisibility(View.GONE);
						MovieRatingsActivity.audienceTitle.setVisibility(View.GONE);
						MovieRatingsActivity.synopsisTitle.setVisibility(View.GONE);
						MovieRatingsActivity.title.setText("No Rating Data Available");
						MovieRatingsActivity.critic.setText("");
						MovieRatingsActivity.audience.setText("");
						MovieRatingsActivity.synopsis.setText("");
					} else {
						if (criticScore.equals("-1")) {
							MovieRatingsActivity.critic.setText("N/A");
						} else {
							MovieRatingsActivity.critic.setText(criticScore + "%");
						}
						if (audienceScore.equals("-1") || audienceScore.equals("0")) {
							MovieRatingsActivity.audience.setText("N/A");
						} else {
							MovieRatingsActivity.audience.setText(audienceScore + "%");
						} 
						if (synopsis.equals("")) {
							MovieRatingsActivity.synopsis.setText("No Synopsis Available");
						} else {
							MovieRatingsActivity.synopsis.setText(synopsis);
						}
						
						MovieRatingsActivity.title.setText(title);
					}
					
				}
				
				
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.i("Error: ", e.getMessage().toString());
				Toast.makeText(
						MainActivity.context,
						"Something went wrong while retrieving the data. Please check your "
								+ "connection and try again.", Toast.LENGTH_LONG)
						.show();
			}
		}
}
