package com.assignment.practical;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.FeatureInfo;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

public class HotelSpecific extends Activity{
	private HotelDbAdapter dbHelper;
	private TextView name;
	private TextView address;
	private TextView number;
	private TextView distance;
	private RatingBar rating;
	private TextView website;
	private int directions;
	private Long row;
	private static final int ACTIVITY_PHONE = 2;
	float downXValue;
	Cursor hotel;



	protected void onCreate(Bundle bundle) {
		super.onCreate(bundle);
		setContentView(R.layout.hotel_display);

		dbHelper = new HotelDbAdapter(this);
		dbHelper.open();
		hotel = dbHelper.fetchAllHotels(HotelDbAdapter.KEY_NAME);

		name = (TextView) findViewById(R.id.hotelname);
		address = (TextView) findViewById(R.id.hoteladdress);
		number = (TextView) findViewById(R.id.hotelnumber);
		distance = (TextView) findViewById(R.id.hoteldistance);
		website = (TextView) findViewById(R.id.hotelwebsite);
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
				Intent i = new Intent(HotelSpecific.this, HotelDirectionsTab.class);
				i.putExtras(bundle);
				startActivity(i);
			}
		});

		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder builder2 = new AlertDialog.Builder(HotelSpecific.this);
				builder2.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int location) {
						if(row==null) {
							long id = dbHelper.createHotel(" ", " ", " ", " ", " ", 0, 0);
							if (id > 0) {
								row = id;
							}
						}
						setResult(RESULT_OK);
						finish();
						dbHelper.deleteHotel(row);	
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
			Cursor populate = dbHelper.fetchHotel(row);
			startManagingCursor(populate);
			name.setText(populate.getString(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_NAME)));
			address.setText(populate.getString(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_ADDRESS)));
			number.setText(populate.getString(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER)));
			website.setText(populate.getString(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_WEBSITE)));
			distance.setText(populate.getString(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_DISTANCE))); 			
			rating.setRating(populate.getFloat(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_RANKING)));
			final String call = populate.getString(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER)).replace(" ", "");
			directions = populate.getInt(populate.getColumnIndexOrThrow(HotelDbAdapter.KEY_DIRECTIONS));

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
		dbHelper.updateHotel(row, name.getText().toString(), address.getText().toString(), number.getText().toString(), website.getText().toString(), distance.getText().toString(), rating.getRating(), directions);
		outState.putSerializable(HotelDbAdapter.KEY_ROWID, row);
	}

	@Override
	protected void onPause() {
		super.onPause();
		dbHelper.updateHotel(row, name.getText().toString(), address.getText().toString(), number.getText().toString(), website.getText().toString(), distance.getText().toString(), rating.getRating(), directions);
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}


	@Override

	public boolean onTouchEvent(MotionEvent event) {
		super.onTouchEvent(event);
		int action = event.getAction() & MotionEvent.ACTION_MASK;

		switch (action) {



		case MotionEvent.ACTION_DOWN:

			// store the X value when the user's finger was pressed down
			Log.d("MultitouchExample","Action Down");
			downXValue = event.getX();
			break;


		case MotionEvent.ACTION_UP:
			Log.d("Multitouch", "Action up");
			// Get the X value when the user released his/her finger
			float currentX = event.getX();            

			// going backwards: pushing stuff to the right
			if (downXValue > currentX) {
				ViewFlipper vf = (ViewFlipper) findViewById(R.id.details);
				// Set the animation
				vf.setAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_out));
				vf.showPrevious();

				hotel.moveToPosition(hotel.getPosition()+1);
				row = hotel.getLong(hotel.getColumnIndex(HotelDbAdapter.KEY_ROWID));

			}


			// going forwards: pushing stuff to the left
			if (downXValue < currentX) 	{
				// Get a reference to the ViewFlipper
				// Get a reference to the ViewFlipper
				ViewFlipper vf = (ViewFlipper) findViewById(R.id.details);
				// Set the animation
				vf.setInAnimation(AnimationUtils.loadAnimation(this, R.anim.push_left_in));
				//TODO:Make animations smoother
				//TODO:Make populateFields() come after the OnTouchEvent has occured.
				hotel.moveToPosition(hotel.getPosition()-1); //TODO: Ensure neither of these methods breaks when the cursor reaches the end of the table list
				row = hotel.getLong(hotel.getColumnIndex(HotelDbAdapter.KEY_ROWID));

			}
			break;

		}
		populateFields();
		return true;
	}





}
