package com.assignment.practical;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

public class RestSpecific extends Activity{
	private RestDbAdapter dbHelper;
	private TextView name;
	private TextView address;
	private TextView number;
	private TextView distance;
	private TextView food;
	private RatingBar rating;
	private TextView website;
	private int directions;
	private Long row;
	private static final int ACTIVITY_PHONE = 2;
	private static final int FOOD = 3;

	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.hotel_display);
		dbHelper = new RestDbAdapter(this);
		dbHelper.open();

		name = (TextView) findViewById(R.id.hotelname);
		address = (TextView) findViewById(R.id.hoteladdress);
		number = (TextView) findViewById(R.id.hotelnumber);
		distance = (TextView) findViewById(R.id.hoteldistance);
		website = (TextView) findViewById(R.id.hotelwebsite);
		food = (TextView) findViewById(FOOD);
		rating = (RatingBar) findViewById(R.id.hotelrating);

		Button directionsButton = (Button) findViewById(R.id.directions);
		Button deleteButton = (Button) findViewById(R.id.delete);
		
		Bundle extras = getIntent().getExtras();
		if (extras != null) {
			row = extras.getLong(HotelDbAdapter.KEY_ROWID);
		}
		populateFields();
		
		directionsButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				Bundle bundle = new Bundle();
				bundle.putInt("Directions", directions);
				bundle.putCharSequence("Name", name.getText());
				Intent i = new Intent(RestSpecific.this, HotelDirectionsTab.class);
				i.putExtras(bundle);
				startActivity(i);
			}
		});
		
		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder builder2 = new AlertDialog.Builder(RestSpecific.this);
				builder2.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int location) {
						if(row==null) {
							long id = dbHelper.createRest(" ", " ", " ", " ", " ", " ", 0, 0);
							if (id > 0) {
								row = id;
							}
						}
						setResult(RESULT_OK);
						finish();
						dbHelper.deleteRest(row);	
					}




				})
				.setNegativeButton("No", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int id) {
						dialog.cancel();
					}
				});
				builder2.show();

			}
		});
		
		
		
		
	}
	
	private void populateFields() {
		if (row != null) {
			Cursor hotel = dbHelper.fetchRest(row);
			startManagingCursor(hotel);
			name.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_NAME)));
			address.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_ADDRESS)));
			number.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER)));
			website.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_WEBSITE)));
			distance.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_DISTANCE))); 			
			rating.setRating(hotel.getFloat(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_RANKING)));
			final String call = hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER)).replace(" ", "");
			directions = hotel.getInt(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_DIRECTIONS));
			
			number.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
				     try {
				    	 String url = "tel:"+call;
				         Intent callIntent = new Intent(Intent.ACTION_CALL);
				         callIntent.setData(Uri.parse(url));				
				         startActivityForResult(callIntent, ACTIVITY_PHONE);
				     } catch (ActivityNotFoundException activityException) {
				         Log.e("dialing-example", "Call failed", activityException);
				     }
				
					
				}
				
			});
			
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		dbHelper.updateRest(row, name.getText().toString(), address.getText().toString(), number.getText().toString(), website.getText().toString(), food.getText().toString(), distance.getText().toString(), rating.getRating(), directions);
		outState.putSerializable(HotelDbAdapter.KEY_ROWID, row);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.updateRest(row, name.getText().toString(), address.getText().toString(), number.getText().toString(), website.getText().toString(), food.getText().toString(), distance.getText().toString(), rating.getRating(), directions);
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}
	


}
