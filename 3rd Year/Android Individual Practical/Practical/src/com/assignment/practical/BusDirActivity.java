package com.assignment.practical;

import android.os.Bundle;
import android.widget.RelativeLayout.LayoutParams;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;


public class BusDirActivity extends MapActivity {
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.walk);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        //TODO Find a list of bus stops, and use the GPS location to draw a route from your current location to the bus, with information appearing as to which bus to take.
        
     /*   Button b = (Button) findViewById(R.id.menu);
        b.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        }); */

        

   
        
    }

}
