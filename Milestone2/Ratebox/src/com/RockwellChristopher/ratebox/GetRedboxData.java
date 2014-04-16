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

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
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
import android.widget.Toast;

@SuppressLint("DefaultLocale")
public class GetRedboxData {
	static String _urlString = "https://api.redbox.com/v3/products/movies?apiKey=de9d264f6780232f9da733b63d4569ee&pageSize=15&pageNum=1";
	static String TAG = "NETWORK DATA - MAINACTIVITY";
	static JSONArray links;
	static JSONObject imgs, castLinks;
	static String linkStr, titleStr;
	static ArrayList<String> titles = new ArrayList<String>();
	static ArrayList<String> imgLinks = new ArrayList<String>();
	
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
				e.printStackTrace();
				Toast.makeText(MainActivity.context, "Something went wrong while retrieving the data. Please check your " +
						"connection and try again.", Toast.LENGTH_LONG).show();
			}

			return response;
		}
		
		// parse JSON object
		public static void readJson(String jsonStr) throws JSONException {

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
					Log.i("Movie:", "Title: " + titleStr);
					Log.i("Movie:", "Link: " + linkStr);
				}
			}
			
			// call the loadData function to load data into the ListView
			MainActivity.loadData();

		}
}
