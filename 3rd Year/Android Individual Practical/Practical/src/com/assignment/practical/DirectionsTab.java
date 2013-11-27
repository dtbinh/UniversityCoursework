package com.assignment.practical;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.widget.TabHost;

public class DirectionsTab extends TabActivity {
	private boolean isNetworkAvailable() {
		ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
		return activeNetworkInfo != null;
	}

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directions);

		Resources res = getResources();
		TabHost tabHost = getTabHost();  
		TabHost.TabSpec spec;  
		Intent intent;  

		// Create an Intent to launch an Activity for the tab (to be reused)
		if(isNetworkAvailable()) {
			intent = new Intent().setClass(this, BusDirActivity.class);
		}
		else {
			intent = new Intent().setClass(this, BusOfflineActivity.class);
		}

		// Initialize a TabSpec for each tab and add it to the TabHost
		spec = tabHost.newTabSpec("bus").setIndicator("Bus",
				res.getDrawable(R.drawable.tab_bus))
				.setContent(intent);
		tabHost.addTab(spec);

		// Do the same for the other tabs
		intent = new Intent().setClass(this, TaxiDirActivity.class);
		spec = tabHost.newTabSpec("taxi").setIndicator("Taxi",
				res.getDrawable(R.drawable.tab_taxi))
				.setContent(intent);
		tabHost.addTab(spec);

		if(isNetworkAvailable()) {
			intent = new Intent().setClass(this, WalkDirActivity.class);	
		}
		else {
			intent = new Intent().setClass(this, WalkOfflineActivity.class);
		}

		spec = tabHost.newTabSpec("walk").setIndicator("Walk",
				res.getDrawable(R.drawable.tab_walk))
				.setContent(intent);
		tabHost.addTab(spec);

		for (int i = 0; i < tabHost.getTabWidget().getChildCount(); i++){
			tabHost.getTabWidget().getChildAt(i).setPadding(30,10,10,10);
		}

		tabHost.setCurrentTab(0);
	}

}
