package com.assignment.practical;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Spinner;

public class HotelDetails extends Activity {
	private EditText mName;
	private EditText mAddress;
	private EditText mNumber;
	private EditText mWebsite;
	private Spinner mDistance;
	private RatingBar mBar;
	private Long mRowId;
	private int directions;
	private HotelDbAdapter mDbHelper;



	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new HotelDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.hotel_edit);
		mName = (EditText) findViewById(R.id.getname);
		mAddress = (EditText) findViewById(R.id.getaddress);
		mNumber = (EditText) findViewById(R.id.getphone);
		mWebsite = (EditText) findViewById(R.id.getwebsite);
		mDistance = (Spinner) findViewById(R.id.getDistance);
		mBar = (RatingBar) findViewById(R.id.getRating);

		Button confirmButton = (Button) findViewById(R.id.hotel_edit_button);
		Button deleteButton = (Button) findViewById(R.id.hotel_delete_button);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState
				.getSerializable(HotelDbAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(HotelDbAdapter.KEY_ROWID);
		}
		populateFields();


		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});

		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder builder2 = new AlertDialog.Builder(HotelDetails.this);
				builder2.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int location) {
						if(mRowId==null) {
							long id = mDbHelper.createHotel(" ", " ", " ", " ", " ", 0, 0);
							if (id > 0) {
								mRowId = id;
							}
						}
						setResult(RESULT_OK);
						finish();
						mDbHelper.deleteHotel(mRowId);	
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
	/*
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		Bundle extras = intent.getExtras();
		mDbHelper = new HotelDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.hotel_edit);
		mName = (EditText) findViewById(R.id.getname);
		mAddress = (EditText) findViewById(R.id.getaddress);
		mNumber = (EditText) findViewById(R.id.getphone);
		mDistance = (Spinner) findViewById(R.id.getDistance);
		mBar = (RatingBar) findViewById(R.id.getRating);

		Button confirmButton = (Button) findViewById(R.id.hotel_edit_button);
		Button deleteButton = (Button) findViewById(R.id.hotel_delete_button);

		confirmButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				setResult(RESULT_OK);
				finish();
			}

		});

		deleteButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View view) {
				AlertDialog.Builder builder2 = new AlertDialog.Builder(HotelDetails.this);
				builder2.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int location) {
						setResult(RESULT_OK);
						finish();
						mDbHelper.deleteHotel(mRowId);
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

		switch(requestCode) {
		case ACTIVITY_CREATE:
			if(extras!=null) {
				mRowId = extras.getLong("Row");
				mName.setText(extras.getString("Name"));
				mAddress.setText(extras.getString("Address"));
				mNumber.setText(extras.getString("Number"));

				String myString = extras.getString("Distance");

				ArrayAdapter myAdap = (ArrayAdapter) mDistance.getAdapter(); //cast to an ArrayAdapter

				int spinnerPosition = myAdap.getPosition(myString);

				//set the default according to value
				mDistance.setSelection(spinnerPosition);
				mBar.setRating(extras.getFloat("Rating"));
			}
			break;
		case ACTIVITY_EDIT:
			mRowId = null;
			if (extras != null) {
				mRowId = extras.getLong(HotelDbAdapter.KEY_ROWID);
			}
			populateFields();
			break;
		}
	}
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		Bundle bundle = new Bundle();
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			//TODO: if time, have this information store the stuff so that you can come back to it
			String name = mName.getText().toString();
			String address = mAddress.getText().toString();
			String number = mNumber.getText().toString();
			String website = mWebsite.getText().toString();
			float ranking = mBar.getRating();
			String distance = mDistance.getSelectedItem().toString();

			if (mRowId == null) {
				long id = mDbHelper.createHotel(name, address, number, website, distance, ranking, 0);
				if (id > 0) {
					mRowId = id;
				}
			}

			bundle.putLong("Row", mRowId);
			bundle.putString("Name", name);
			bundle.putString("Address", address);
			bundle.putString("Number", number);
			bundle.putString("Website", website);
			bundle.putFloat("Ranking", ranking);
			bundle.putString("Distance", distance);
			bundle.putInt("Directions", directions);
			Intent mIntent = new Intent();
			mIntent.putExtras(bundle);
			setResult(RESULT_FIRST_USER, mIntent);
			finish();	

			return true;
		}

		return super.onKeyDown(keyCode, event);
	}


	private void populateFields() {
		if (mRowId != null) {
			Cursor hotel = mDbHelper.fetchHotel(mRowId);
			startManagingCursor(hotel);
			mName.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_NAME)));
			mAddress.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_ADDRESS)));
			mNumber.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER)));
			mWebsite.setText(hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_WEBSITE)));
			directions = hotel.getInt(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_DIRECTIONS));

			String myString = hotel.getString(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_DISTANCE)); //the value you want the position for

			@SuppressWarnings("unchecked")
			ArrayAdapter<String> myAdap = (ArrayAdapter<String>) mDistance.getAdapter(); //cast to an ArrayAdapter

			int spinnerPosition = myAdap.getPosition(myString);

			//set the default according to value
			mDistance.setSelection(spinnerPosition);
			mBar.setRating(hotel.getFloat(hotel.getColumnIndexOrThrow(HotelDbAdapter.KEY_RANKING)));
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(HotelDbAdapter.KEY_ROWID, mRowId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		saveState();
	}

	@Override
	protected void onResume() {
		super.onResume();
		populateFields();
	}

	private void saveState() {
		String name = mName.getText().toString();
		String address = mAddress.getText().toString();
		String number = mNumber.getText().toString();
		String website = mWebsite.getText().toString();
		float ranking = mBar.getRating();
		String distance = mDistance.getSelectedItem().toString();


		if (mRowId == null) {
			long id = mDbHelper.createHotel(name, address, number, website, distance, ranking, 0);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateHotel(mRowId, name, address, number, website, distance, ranking, directions);
		}
	}
}
