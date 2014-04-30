package com.RockwellChristopher.ratebox;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

public class MachineSelectionActivity extends Activity {
	
	static public List<Store> mList;
	static ListView storesList;
	static MyProgressDialog mDialog;
	static Context context;
	static StoresArrayAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_machine_selection);
		context = this;
		
		// inflate the menu; this adds items to the action bar if it is present.
    	// set custom action bar title
        this.getActionBar().setDisplayShowCustomEnabled(true);
        this.getActionBar().setDisplayShowTitleEnabled(false);

        LayoutInflater inflator = (LayoutInflater)this.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View v = inflator.inflate(R.layout.actionbar_textview, null);

        //assign the view to the actionbar
        this.getActionBar().setCustomView(v);
        
     // custom typeface
  		Typeface customFont = Typeface.createFromAsset(MachineSelectionActivity.this.getAssets(), "RammettoOne-Regular.ttf");
  		TextView actionTv = (TextView) findViewById(R.id.actionbar_tv);
  		actionTv.setText("Get Nearby Inventory");
  		actionTv.setTypeface(customFont);
        
        storesList = (ListView) findViewById(R.id.store_list);
        
        // add progress dialog to illustrate to the user that the data is loading 
		mDialog = new MyProgressDialog(context);
        mDialog.setMessage("Loading Machine Data...");
        mDialog.setCancelable(false);
        mDialog.show();
        
        storesList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				
				// create a bundle to store the store id and address to be used in MainActivity
				Bundle bun = new Bundle();
				bun.putString("ID", GetApiData.storeIDs.get(position).toString());
				bun.putString("ADDRESS", GetApiData.addresses.get(position).toString());
				Intent mainActivity = new Intent(context, MainActivity.class);
				mainActivity.putExtras(bun);
				startActivity(mainActivity);
				
			}
    	});
	}
	
	// add custom progress dialog style
	private class MyProgressDialog extends ProgressDialog {

		private MyProgressDialog(Context context) {
            super(context,R.style.CustomDialog);

            // TODO Auto-generated constructor stub
        }

    }
	
	// load stores data into ListView
    static public void loadData() {
    	ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
		mList = new ArrayList<Store>();
		
		for (int i = 0; i < GetApiData.storeStrings.size(); i++) {
			
			Store store = new Store(GetApiData.storeStrings.get(i));
			mList.add(store);
			HashMap<String, String> displayMap = new HashMap<String, String>();
			displayMap.put("storeStr", GetApiData.storeStrings.get(i));

			list.add(displayMap);
		}
		
		adapter = new StoresArrayAdapter(context, R.layout.list_row2, mList);

		storesList.setAdapter(adapter);
		
		mDialog.dismiss();
    }

}
