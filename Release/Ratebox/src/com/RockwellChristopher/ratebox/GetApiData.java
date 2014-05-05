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

//create async task
@SuppressLint("DefaultLocale")
public class GetApiData extends AsyncTask<String, Void, String> {
	static String TAG = "NETWORK DATA - MAINACTIVITY";
	static ArrayList<String> titles = new ArrayList<String>();
	static ArrayList<String> imgLinks = new ArrayList<String>();
	static ArrayList<String> storeStrings = new ArrayList<String>();
	static ArrayList<String> storeIDs = new ArrayList<String>();
	static ArrayList<String> productIDs = new ArrayList<String>();
	static ArrayList<String> addresses = new ArrayList<String>();
	static int code;
	static int count = 0;
	static String allMovies;

	@Override
	protected String doInBackground(String... params) {

		String responseString = "";
		try {
			URL url = new URL(params[0]);
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
				allMovies = result;
			} else if (code == 1) {
				readRottenJson(result);
			} else if (code == 2) {
				readNearbyJson(result);
			} else if (code == 3) {
				readStoreIDs(result);
			} else if (code == 4) {
				readNearbyReboxJson(result);
			}
		} catch (JSONException e) {
			e.printStackTrace();
			Toast.makeText(MainActivity.context, "Something went wrong while retrieving the data. Please check your " +
					"connection and try again.", Toast.LENGTH_LONG).show();
		}
		//Log.i("Works:", result);
		super.onPostExecute(result);
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
			if (code == 0 || code == 2 || code == 3 || code == 4) {
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
	public void readReboxJson(String jsonStr) throws JSONException {
		// clear data from array lists
		titles.clear();
		imgLinks.clear();

		JSONArray links;
		JSONObject imgs, castLinks;
		String linkStr, titleStr;
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
	public void readRottenJson(String jsonStr) throws JSONException {
		// read JSON and set TextView text
		String title, criticScore, audienceScore, synopsis;

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

	// parse nearby Redbox stores JSON object
	public void readNearbyJson(String jsonStr) throws JSONException {
		// clear data from array lists
		storeStrings.clear();
		storeIDs.clear();

		String formatAddress;

		JSONObject obj = new JSONObject(jsonStr);
		JSONObject castObj = obj.getJSONObject("StoreBulkList");
		JSONArray storesArray = castObj.getJSONArray("Store");

		// setup string for get all Redbox inventory
		storeStrings.add("Browse All Movies");
		storeIDs.add("All Movies");
		addresses.add("");

		// sort through the stores
		for (int i = 0; i < storesArray.length(); i++) {
			JSONObject store = storesArray.getJSONObject(i);
			JSONObject location = store.getJSONObject("Location");
			// store the storeID to get inventory from a specific machine
			String storeID = store.getString("@storeId");
			storeIDs.add(storeID.toString());
			String retailer = store.getString("Retailer");
			String address = location.getString("Address");
			String city = location.getString("City");
			String state = location.getString("State");
			String zip = location.getString("Zipcode");
			StringBuilder storeStr = new StringBuilder(200);
			storeStr.append("Retailer: " + retailer + "\n" + address  + "\n" + city + ", " + state + " " + zip);
			// check to see if there's a machine label
			if(store.has("Label")) {
				String machineLabel = store.getString("Label");
				formatAddress = retailer + " " + address + " - Machine " + machineLabel;
				storeStr.append("\nMachine: " + machineLabel);
			} else {
				formatAddress = retailer + " " + address;
			}

			// add address to list array for use in the machine TextView in MainActivity
			addresses.add(formatAddress);

			storeStrings.add(storeStr.toString());
		}
		MachineSelectionActivity.loadData();
	}

	public void readStoreIDs (String jsonStr) throws JSONException {
		// clear data from array list
		productIDs.clear();

		JSONObject obj = new JSONObject(jsonStr);
		JSONObject castObj = obj.getJSONObject("Inventory");
		JSONObject storeInventory = castObj.getJSONObject("StoreInventory");
		JSONArray productInventory = storeInventory.getJSONArray("ProductInventory");
		for (int i = 0; i < productInventory.length(); i++) {
			JSONObject productObj = productInventory.getJSONObject(i);
			String stock = productObj.getString("@inventoryStatus");
			if (stock.equals("InStock")) {
				String productID = productObj.getString("@productId");
				productIDs.add(productID);
			}
		}

		if (allMovies.length() > 0) {
			readNearbyReboxJson(allMovies);
		} else {
			GetApiData.code = 4;
			GetApiData data = new GetApiData();
			String urlStr = "https://api.redbox.com/v3/products/movies?apiKey=de9d264f6780232f9da733b63d4569ee";
			data.execute(urlStr);
		}
	}

	// parse Redbox JSON object and only get the products from the nearby machine selected
	public void readNearbyReboxJson(String jsonStr) throws JSONException {
		// clear data from array lists
		titles.clear();
		imgLinks.clear();

		JSONArray links;
		JSONObject imgs, castLinks;
		String linkStr, titleStr;
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
				String productID = movie.getString("@productId");
				// check to see if the current movie is available at this machines
				if(productIDs.contains(productID)) {
					titles.add(titleStr);
					imgLinks.add(linkStr);
				}
			}
		}

		// call the loadData function to load data into the ListView
		MainActivity.loadData();

	}


}
