package com.assignment.practical;


import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

public class DisplayDatabaseActivity extends Activity {
	//TODO: Make this as adaptable as possible
	private HotelDbAdapter hAdapter;
	private RestDbAdapter rAdapter;
	private Long hRowId;
	private Long rRowId;
	private String hName;
	private String hAddress;
	private String hNumber;
	private String hDistance;
	private String hRanking;
	private String rName;
	private String rAddress;
	private String rNumber;
	private String rFood;
	final int Rest = 4;
	final int Hotel = 5;
	final TextView[] myRest = new TextView[Rest];
    final TextView[] myHotel = new TextView[Hotel];                                            

    public void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.displaydatabase);
		LinearLayout myLayout = (LinearLayout) findViewById(R.id.databasedisplay);

		Bundle extras = getIntent().getExtras();
		if(extras.get("ADAPTER")=="hotel") {
			hAdapter = new HotelDbAdapter(this);
			
			hRowId = (bundle == null) ? null : (Long) bundle
					.getSerializable(HotelDbAdapter.KEY_ROWID);
			if (extras != null) {
				hRowId = extras.getLong(HotelDbAdapter.KEY_ROWID);

			}
			populateHotelFields();
			for (int i = 0; i < Hotel; i++) {
			    // create a new textview
			    final TextView rowTextView = new TextView(this);

			    // set some properties of rowTextView or something
			    switch(i) {
			    case 1:
			    	rowTextView.setText("Hotel Name: "+hName);
			    	break;
			    case 2:
			    	rowTextView.setText("Hotel Address: "+hAddress);
			    	break;
			    case 3:
			    	rowTextView.setText("Hotel Number: "+hNumber);
			    	break;
			    case 4:
			    	rowTextView.setText("Hotel Distance: "+hDistance);
			    	break;
			    case 5:
			    	rowTextView.setText("Hotel Ranking: "+hRanking);
			    	break;			    
			    }
			    
			    myLayout.addView(rowTextView);

			    // save a reference to the textview for later
			    myHotel[i] = rowTextView;
			}
		}
		else if(extras.get("ADAPTER")=="rest") {
			rAdapter = new RestDbAdapter(this);
			
			rRowId = (bundle == null) ? null : (Long) bundle
					.getSerializable(RestDbAdapter.KEY_ROWID);
			if (extras != null) {
				rRowId = extras.getLong(RestDbAdapter.KEY_ROWID);

			}
			populateRestFields();
			for (int i = 0; i < Rest; i++) {
			    // create a new textview
			    final TextView rowTextView = new TextView(this);

			    switch(i) {
			    case 1:
			    	rowTextView.setText("Resturaunt Name: "+rName);
			    	break;
			    case 2:
			    	rowTextView.setText("Resturaunt Address: "+rAddress);
			    	break;
			    case 3:
			    	rowTextView.setText("Resturaunt Number: "+rNumber);
			    	break;
			    case 4:
			    	rowTextView.setText("Resturaunt Food: "+rFood);
			    	break;		    
			    }
			    
			    myLayout.addView(rowTextView);

			    // save a reference to the textview for later
			    myRest[i] = rowTextView;
			}
		}
	}

	private void populateHotelFields() {
		if (hRowId != null) {
			Cursor hotel = hAdapter.fetchHotel(hRowId);
			startManagingCursor(hotel);
			hName = hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_NAME));
			hAddress = hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_ADDRESS));
			hNumber = hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER));
			hDistance = hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_DISTANCE));
			hRanking = hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_RANKING));
		}
	}

	private void populateRestFields() {
		if (rRowId != null) {
			Cursor rest = rAdapter.fetchRest(rRowId);
			startManagingCursor(rest);
			rName = rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_NAME));
			rAddress = rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_ADDRESS));
			rNumber = rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_NUMBER));
			rFood = rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_FOOD));
		}
	}

}
