package com.RockwellChristopher.ratebox;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONException;

import android.os.AsyncTask;
import android.widget.Toast;

public class GetRottenTomatoesData	extends AsyncTask<String, Void, String> {
	
	@Override
	protected String doInBackground(String... params) {
		String responseString = "";
		try {
			URL url = new URL(MovieRatingsActivity._urlString);
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
	
	public static String getResponse(URL url) {
		String response = "";
		try {
			URLConnection connect =  url.openConnection();
			connect.setRequestProperty("Accept", "application/json");
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
		
	}

}
