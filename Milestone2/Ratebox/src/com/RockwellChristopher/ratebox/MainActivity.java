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
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.RockwellChristopher.ratebox.GetRedboxData.getData;
import com.loopj.android.image.SmartImageView;

public class MainActivity extends Activity {
	static Context context;
	static ListView moviesList;
	SmartImageView posterImg;
	TextView movieTitle;
	static public List<Movie> mList;
	static MoviesArrayAdapter adapter;
	static MyProgressDialog mDialog;
	static EditText input;
	static String srcText;
	static Button cancelBtn;
	static Button srcButton;
	static Button clearBtn;
	AlertDialog builder;
	static TextView emptyTv;
	Intent secondActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        context = this;
     // check to see if there's a valid connection
    	if (GetRedboxData.connectionStatus(context)){
    		GetRedboxData.getData data = new getData();
    		data.execute(GetRedboxData._urlString);
    		
    		// add progress dialog to illustrate to the user that the data is loading 
    		mDialog = new MyProgressDialog(context);
            mDialog.setMessage("Loading Movie Data...");
            mDialog.setCancelable(false);
            mDialog.show();
            
            LayoutInflater inflater = this.getLayoutInflater();
            inflater.inflate(R.layout.custom_alert_dialog, null);
    		
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
    	
    	secondActivity = new Intent(context,MovieRatingsActivity.class);
    	
    	moviesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				String itemText = moviesList.getItemAtPosition(position).toString();
				MovieRatingsActivity.movieTitle = itemText;
				startActivity(secondActivity);
				
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
  		actionTv.setTypeface(customFont);
  		
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
    	MainActivity.emptyTv.setVisibility(View.GONE);
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
    		builder.show();
		} else {
			// add functionality for locate button
		}
		return super.onOptionsItemSelected(item);
	}
    
    // load movie data into ListView
    static public void loadData() {
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		mList = new ArrayList<Movie>();
		
		for (int i = 0; i < GetRedboxData.titles.size(); i++) {
			
			Movie movie = new Movie(GetRedboxData.titles.get(i), GetRedboxData.imgLinks.get(i));
			mList.add(movie);
			HashMap<String, String> displayMap = new HashMap<String, String>();
			displayMap.put("title", GetRedboxData.titles.get(i));
			displayMap.put("image", GetRedboxData.imgLinks.get(i));

			list.add(displayMap);
		}
		
		adapter = new MoviesArrayAdapter(context, R.layout.list_row, mList);

		moviesList.setAdapter(adapter);
		mDialog.dismiss();
    }
    
    // add custom progress dialog style
    public class MyProgressDialog extends ProgressDialog {

        public MyProgressDialog(Context context) {
            super(context,R.style.CustomDialog);

            // TODO Auto-generated constructor stub
        }

    }
    
    
    public void doPositiveClick(View v) {
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
		// Do nothing
		Log.i("Clicked Cancel", "Negative click!");
		builder.dismiss();
	}
	
	public void doNeutralClick(View v) {
		adapter.getFilter().filter("");
		srcText = "";
		builder.dismiss();
	}
    
}