package com.assignment.practical;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

public class PracticalActivity extends Activity {
	private boolean isNetworkAvailable() {
	    ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
	    NetworkInfo activeNetworkInfo = cm.getActiveNetworkInfo();
	    return activeNetworkInfo != null;
	}
	
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        
        Button directions = (Button) findViewById(R.id.Direct);
        Button data = (Button) findViewById(R.id.Edinburgh);
        Button facts = (Button) findViewById(R.id.Factsinfo);
        Button rest = (Button) findViewById(R.id.Resturaunts);
        
        directions.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	if(isNetworkAvailable()) {
            		Intent i = new Intent(PracticalActivity.this, DirectionsTab.class);
                    startActivity(i);
            	}
            	else {
            		AlertDialog.Builder builder = new AlertDialog.Builder(PracticalActivity.this);
            		builder.setMessage(R.string.directions)
            		.setCancelable(false)
            		.setPositiveButton("Okay", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int id) {
                    		Intent i = new Intent(PracticalActivity.this, DirectionsTab.class);
            				dialog.cancel();
                    		startActivity(i);
            			}
            		})
            		.setNegativeButton("No", new DialogInterface.OnClickListener() {
            			public void onClick(DialogInterface dialog, int id) {
            				dialog.cancel();
            			}
            		});
            		AlertDialog alert = builder.create();
            		alert.show(); 
            	}
            }
        });
        
        data.setOnClickListener(new View.OnClickListener() {		
			@Override
			public void onClick(View arg0) {
				Intent i2 = new Intent(PracticalActivity.this, HotelOverview.class);
				startActivity(i2);
				
			}
		});
        
        facts.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent i3 = new Intent(PracticalActivity.this, FactsActivity.class);
				startActivity(i3);
				
			}
		});
        
        rest.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				Intent i4 = new Intent(PracticalActivity.this, RestOverview.class);
				startActivity(i4);
				
			}
		});

    }
}