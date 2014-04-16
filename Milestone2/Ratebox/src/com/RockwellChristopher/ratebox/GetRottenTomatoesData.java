package com.RockwellChristopher.ratebox;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class GetRottenTomatoesData {
	static String urlString = "http://api.rottentomatoes.com/api/public/v1.0/movies.json?apikey=bf72tfc2zjfbdscenpwx2e2r&q=" + MovieRatingsActivity.movieTitle + "&page_limit=1";

	public static class getData extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			String responseString = "";
			try {
				Log.i("URL", urlString);
				URL url = new URL(urlString);
				responseString = getResponse(url);
			} catch (MalformedURLException e) {
				responseString = "Something went wrong";
				Toast.makeText(MainActivity.context, "Something went wrong while retrieving the data. Please check your " +
						"connection and try again.", Toast.LENGTH_LONG).show();
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
				Toast.makeText(MainActivity.context, "Something went wrong while retrieving the data. Please check your " +
						"connection and try again.", Toast.LENGTH_LONG).show();
			}
			//Log.i("Works:", result);
			super.onPostExecute(result);
		}
	}

	public static String getResponse(URL url) {
		String response = "";
		try {
			HttpURLConnection connect = (HttpURLConnection) url.openConnection();
			connect.setRequestMethod("GET");
			connect.connect();
			byte[] data = StreamUtils.streamToBytes(connect.getInputStream());
			response = new String(data);
		} catch (IOException e) {
			response = "Something went wrong";
			e.printStackTrace();
			Toast.makeText(MainActivity.context, "Something went wrong while retrieving the data. Please check your " +
					"connection and try again.", Toast.LENGTH_LONG).show();
		}

		return response;
	}

	// parse JSON object
	public static void readJson(String jsonStr) throws JSONException {
		Log.i("JSON", jsonStr.toString());
	}

}
