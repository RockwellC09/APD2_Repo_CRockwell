/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	Ratebox
 * 
 * 	Package: 	com.RockwellChristopher.ratebox
 * 
 * 	File: 		GetRedboxData.java
 * 
 *	Purpose:	This class will gather the Redbox inventory and parse it to be displayed in Main Activity. This class 
 *				will also check for a valid Internet connection.
 * 
 */

package com.RockwellChristopher.ratebox;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.util.Log;

public class GetRedboxData {
	static String _urlString = "https://api.redbox.com/v3/products/movies?apiKey=de9d264f6780232f9da733b63d4569ee&pageSize=1&pageNum=1";
	static String TAG = "NETWORK DATA - MAINACTIVITY";
	
	// create async task
		public static class getData extends AsyncTask<String, Void, String> {

			@Override
			protected String doInBackground(String... params) {
				String responseString = "";
				try {
					URL url = new URL(_urlString);
					responseString = getResponse(url);
				} catch (MalformedURLException e) {
					responseString = "Something went wrong";
					Log.e(TAG, "Error 1: " + e.getMessage().toString());
				}
				return responseString;
			}

			@Override
			protected void onPostExecute(String result) {
				// parse JSON Data
	            try {
					readJson(result);
				} catch (JSONException e) {
					Log.e(TAG, "Error 2: " + e.getMessage().toString());
				}
				Log.i("Works:", result);
				super.onPostExecute(result);
			}

		}
		
		// check to see if user have a valid internet connection
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
				URLConnection connect =  url.openConnection();
				connect.setRequestProperty("Accept", "application/json");
				byte[] data = StreamUtils.streamToBytes(connect.getInputStream());
				response = new String(data);
			} catch (IOException e) {
				response = "Something went wrong";
				Log.e(TAG, "Error 3: " + e.getMessage().toString());
			}

			return response;
		}
		
		// read JSON object
		public static String readJson(String jsonStr) throws JSONException {

			String result;

			// set JSONOject and cast into array the back into an object to get movie proper info
			JSONObject obj = new JSONObject(jsonStr);
			JSONArray products = obj.getJSONArray("Products");
			JSONObject castMovies = products.getJSONObject(0);
			JSONArray movTitle =  castMovies.getJSONArray("title");
			
//			Log.i("Movie InfoL:", "Title: " + movTitle + " Link: " + ImgUrl);
			Log.i("Movie:", "Title: " + castMovies.getString("title"));

			return null;
		}
}
