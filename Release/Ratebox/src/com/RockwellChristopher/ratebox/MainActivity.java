/*
 *	Author:		Christopher Rockwell
 * 
 * 	Project: 	Ratebox
 * 
 * 	Package: 	com.RockwellChristopher.ratebox
 * 
 * 	File: 		MainActivity.java
 * 
 *	Purpose:	This activity will get current Redbox DVD rentals and display them to the user allowing them to search 
 *				through and select the one they'd like and view the ratings and synopsis.
 * 
 */


package com.RockwellChristopher.ratebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.loopj.android.image.SmartImageView;

public class MainActivity extends Activity {
	static Context context;
	static ListView moviesList;
	SmartImageView posterImg;
	TextView movieTitle;
	static List<Movie> mList;
	static MoviesArrayAdapter adapter;
	static MyProgressDialog mDialog;
	EditText input;
	String srcText;
	Button cancelBtn;
	Button srcButton;
	Button clearBtn;
	TextView alertTitle;
	TextView machineTV;
	AlertDialog builder;
	static TextView emptyTv;
	Intent ratingsActivity;
	String urlStr = "https://api.redbox.com/v3/products/movies?apiKey=de9d264f6780232f9da733b63d4569ee&pageSize=100&pageNum=1";
	String urlStr2;
	boolean isNearby;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		context = this;
		// check to see if there's a valid connection
		if (GetApiData.connectionStatus(context)){
			
			machineTV = (TextView) findViewById(R.id.machineTv);
			Typeface customFont = Typeface.createFromAsset(MainActivity.this.getAssets(), "Righteous-Regular.ttf");
			machineTV.setTypeface(customFont);

			// get movie title from bundle extra
			Bundle bun = getIntent().getExtras();
			if (bun != null) {
				String address = bun.getString("ADDRESS");
				String storeID = bun.getString("ID");
				// check to see if user selected to get all movies
				if (storeID.equals("All Movies")) {
					machineTV.setText(R.string.loc);
					GetApiData.code = 0;
					GetApiData data = new GetApiData();
					data.execute(urlStr);
				} else {
					machineTV.setText(address.toString());
					urlStr2 = "https://api.redbox.com/v3/inventory/stores/"+ storeID + "?apiKey=de9d264f6780232f9da733b63d4569ee";
					GetApiData.code = 3;
					GetApiData data = new GetApiData();
					data.execute(urlStr2);
				}
			} else {
				GetApiData.code = 0;
				GetApiData data = new GetApiData();
				data.execute(urlStr);
			}

			// add progress dialog to illustrate to the user that the data is loading 
			mDialog = new MyProgressDialog(context);
			mDialog.setMessage("Loading Movie Data...");
			mDialog.setCancelable(false);
			mDialog.show();

			// initialize views
			moviesList = (ListView) findViewById(R.id.movie_list);
			posterImg = (SmartImageView) findViewById(R.id.img);
			movieTitle = (TextView) findViewById(R.id.actionbar_tv);
			emptyTv = (TextView) findViewById(R.id.movies_empty);

			// hide no results TextView
			emptyTv.setVisibility(View.GONE);

		} else {
			// create alert dialog for users without a valid internet connection
			AlertDialog.Builder builder1 = new AlertDialog.Builder(MainActivity.this);
			builder1.setTitle("No Connection");
			builder1.setMessage("You don't have a valid internet connection.");
			builder1.setPositiveButton("OK", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface arg0, int arg1) {
					// TODO Auto-generated method stub
					Toast.makeText(getApplicationContext(), "Check your connection and try again.", Toast.LENGTH_LONG).show();
				}
			});

			// show alert
			builder1.show();
		}

		ratingsActivity = new Intent(context,MovieRatingsActivity.class);

		moviesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String itemText = moviesList.getItemAtPosition(position).toString();
				// pass the movie title to MovieRatingsActivity
				Bundle bun = new Bundle();
				bun.putString("MOV_TITLE", itemText);
				ratingsActivity.putExtras(bun);
				startActivity(ratingsActivity);

			}
		});
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// set custom action bar title
		this.getActionBar().setDisplayShowCustomEnabled(true);
		this.getActionBar().setDisplayShowTitleEnabled(false);

		LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View v = inflator.inflate(R.layout.actionbar_textview, null);

		//assign the view to the actionbar
		this.getActionBar().setCustomView(v);

		// custom typeface
		Typeface customFont = Typeface.createFromAsset(MainActivity.this.getAssets(), "RammettoOne-Regular.ttf");
		TextView actionTv = (TextView) findViewById(R.id.actionbar_tv);
		actionTv.setText("Ratebox");
		actionTv.setTypeface(customFont);

		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.search_menu_item) {
			final View customAlertView = View.inflate(this,R.layout.custom_alert_dialog, null);
			builder = new AlertDialog.Builder(this).create();
			builder.setView(customAlertView);
			input = (EditText) customAlertView.findViewById(R.id.searchText);
			input.setText(srcText);
			if (input.getText().toString().trim() != "") {
				input.selectAll();
			}

			// initialize view to set custom font
			srcButton = (Button) customAlertView.findViewById(R.id.searchBtn);
			clearBtn = (Button) customAlertView.findViewById(R.id.clearBtn);
			cancelBtn = (Button) customAlertView.findViewById(R.id.cancelBtn);
			alertTitle = (TextView) customAlertView.findViewById(R.id.alertTitle);

			Typeface customFont2 = Typeface.createFromAsset(MainActivity.this.getAssets(), "Raleway-Medium.ttf");
			Typeface customFont3 = Typeface.createFromAsset(MainActivity.this.getAssets(), "Raleway-Bold.ttf");
			input.setTypeface(customFont2);
			srcButton.setTypeface(customFont2);
			clearBtn.setTypeface(customFont2);
			cancelBtn.setTypeface(customFont2);
			alertTitle.setTypeface(customFont3);
			alertTitle.setTextSize(20);

			builder.show();
		} else {
			// do nothing
		}
		return super.onOptionsItemSelected(item);
	}

	// load movie data into ListView
	static public void loadData() {
		ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		mList = new ArrayList<Movie>();

		for (int i = 0; i < GetApiData.titles.size(); i++) {

			Movie movie = new Movie(GetApiData.titles.get(i), GetApiData.imgLinks.get(i));
			mList.add(movie);
			HashMap<String, String> displayMap = new HashMap<String, String>();
			displayMap.put("title", GetApiData.titles.get(i));
			displayMap.put("image", GetApiData.imgLinks.get(i));

			list.add(displayMap);
		}

		adapter = new MoviesArrayAdapter(context, R.layout.list_row, mList);

		moviesList.setAdapter(adapter);
		mDialog.dismiss();
	}

	// add custom progress dialog style
	private class MyProgressDialog extends ProgressDialog {

		private MyProgressDialog(Context context) {
			super(context,R.style.CustomDialog);

			// TODO Auto-generated constructor stub
		}

	}


	public void doPositiveClick(View v) {
		MainActivity.emptyTv.setVisibility(View.GONE);
		srcText = input.getText().toString();
		// check to see if the input is blank
		if (srcText.trim().matches("")) {
			Toast.makeText(context, "Please enter a movie title you'd like to search for.", Toast.LENGTH_LONG).show();
		} else {
			adapter.getFilter().filter(srcText);
		}
		builder.dismiss();
	}

	public void doNegativeClick(View v) {
		MainActivity.emptyTv.setVisibility(View.GONE);
		// Do nothing
		Log.i("Clicked Cancel", "Negative click!");
		builder.dismiss();
	}

	public void doNeutralClick(View v) {
		MainActivity.emptyTv.setVisibility(View.GONE);
		adapter.getFilter().filter("");
		srcText = "";
		builder.dismiss();
	}

	public void noGPSsub(View v) {
		builder.dismiss();
	}

	public void machineSelection(View v) {
		// get the current location longitude and latitude
		LocationManager locManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
		Location loc = locManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		LocationListener loclistener = new LocationListener() {

			public void onLocationChanged(Location loc) {

			}

			public void onProviderEnabled(String proEnabled) {

			}

			public void onProviderDisabled(String proDisabled) {

			}

			public void onStatusChanged(String p, int status, Bundle extras) {

			}
		};
		locManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, loclistener);

		if (loc != null) {
			double gpsLongitude = loc.getLongitude();
			double gpsLatitude = loc.getLatitude();
			GetApiData.code = 2;
			GetApiData data = new GetApiData();
			String urlStr = "https://api.redbox.com/v3/stores/latlong/" + gpsLatitude + "," + gpsLongitude + "?radius=25&pageNum=1&pageSize=10&apiKey=de9d264f6780232f9da733b63d4569ee";
			data.execute(urlStr);
			Intent machinesActivity = new Intent(context,MachineSelectionActivity.class);
			context.startActivity(machinesActivity);
		} else {
			final View customAlertView = View.inflate(this,R.layout.custom_alert_dialog2, null);
			builder = new AlertDialog.Builder(this).create();
			builder.setView(customAlertView);

			// initialize view to set custom font
			Button subButton = (Button) customAlertView.findViewById(R.id.noGPSsubmitBtn);
			TextView noGpsTitle = (TextView) customAlertView.findViewById(R.id.noGPSalertTitle);
			TextView noGpsMsg = (TextView) customAlertView.findViewById(R.id.noGPStv);

			Typeface customFont2 = Typeface.createFromAsset(MainActivity.this.getAssets(), "Raleway-Medium.ttf");
			Typeface customFont3 = Typeface.createFromAsset(MainActivity.this.getAssets(), "Raleway-Bold.ttf");
			subButton.setTypeface(customFont2);
			noGpsMsg.setTypeface(customFont2);
			noGpsTitle.setTypeface(customFont3);
			noGpsTitle.setTextSize(20);

			builder.show();
		}
	}

}
