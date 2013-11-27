package com.assignment.practical;

import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.TabHost;

public class HotelDirectionsTab extends TabActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.directions);
		Bundle extras = getIntent().getExtras();

		
		Resources res = getResources();
		TabHost tabHost = getTabHost();  
		TabHost.TabSpec spec;  
		Intent intent;  

		intent = new Intent().setClass(this, HotelMap.class);
		intent.putExtras(extras);
		spec = tabHost.newTabSpec("map").setIndicator("Map",
				res.getDrawable(R.drawable.tab_taxi))
				.setContent(intent);
		tabHost.addTab(spec);
		
		intent = new Intent().setClass(this, HotelDirections.class);
		intent.putExtras(extras);
		spec = tabHost.newTabSpec("directions").setIndicator("Directions",
				res.getDrawable(R.drawable.tab_taxi))
				.setContent(intent);
		tabHost.addTab(spec);	
	}
}
