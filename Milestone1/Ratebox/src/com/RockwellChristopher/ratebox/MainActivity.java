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

import com.RockwellChristopher.ratebox.GetRedboxData.getData;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.widget.Toast;

public class MainActivity extends Activity {
	static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context = this;
     // check to see if there's a valid connection
    	if (GetRedboxData.connectionStatus(context)){
    		GetRedboxData.getData data = new getData();
    		data.execute(GetRedboxData._urlString);
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
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
}
