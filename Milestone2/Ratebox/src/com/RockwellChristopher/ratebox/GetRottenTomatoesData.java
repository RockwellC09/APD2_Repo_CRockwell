package com.RockwellChristopher.ratebox;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

public class GetRottenTomatoesData {
	static String urlString;
	static String encodedUrlString;
	static String title, criticScore, audienceScore, synopsis;

	public static class getData extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			urlString = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=bf72tfc2zjfbdscenpwx2e2r&q="
					+ MovieRatingsActivity.movieTitleStr + "&page_limit=1";
			encodedUrlString = new String(urlString.replaceAll(" ", "%20"));
			String responseString = "";
			try {
				URL url = new URL(encodedUrlString);
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
				readJson(result);
			} catch (JSONException e) {
				e.printStackTrace();
				Toast.makeText(
						MainActivity.context,
						"Something went wrong while retrieving the data. Please check your "
								+ "connection and try again.",
						Toast.LENGTH_LONG).show();
			}
			// Log.i("Works:", result);
			super.onPostExecute(result);
		}
	}

	public static String getResponse(URL url) {
		String response = "";
		try {
			HttpURLConnection connect = (HttpURLConnection) url
					.openConnection();
			connect.setRequestMethod("GET");
			connect.connect();
			byte[] data = StreamUtils.streamToBytes(connect.getInputStream());
			response = new String(data);
		} catch (IOException e) {
			response = "Something went wrong";
			e.printStackTrace();
			Toast.makeText(
					MainActivity.context,
					"Something went wrong while retrieving the data. Please check your "
							+ "connection and try again.", Toast.LENGTH_LONG)
					.show();
		}

		return response;
	}

	// parse JSON object
	public static void readJson(String jsonStr) throws JSONException {
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
