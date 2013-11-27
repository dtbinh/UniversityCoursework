package com.assignment.practical;

import android.os.Bundle;

import com.google.android.maps.MapActivity;
import com.google.android.maps.MapView;


public class WalkDirActivity extends MapActivity {
	@Override
	protected boolean isRouteDisplayed() {
	    return false;
	}
	
	public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.walk);
        
        MapView mapView = (MapView) findViewById(R.id.mapview);
        mapView.setBuiltInZoomControls(true);
        
        //TODO:using current GPS location, find way to the Forum via walking. 
        
        /*Button b = (Button) findViewById(R.id.menu);
        b.setOnClickListener(new OnClickListener() {
        	public void onClick(View v) {
        		finish();
        	}
        }); */

   
        
    }

}
