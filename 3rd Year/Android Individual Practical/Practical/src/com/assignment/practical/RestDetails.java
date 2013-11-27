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


public class RestDetails extends Activity {
	private EditText mName;
	private EditText mAddress;
	private EditText mNumber;
	private EditText mWebsite;
	private Spinner mFood;
	private Spinner mDistance;
	private RatingBar mBar;
	private Long mRowId;
	private int directions;
	private RestDbAdapter mDbHelper;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mDbHelper = new RestDbAdapter(this);
		mDbHelper.open();
		setContentView(R.layout.restaurant_edit);
		mName = (EditText) findViewById(R.id.getname);
		mAddress = (EditText) findViewById(R.id.getaddress);
		mNumber = (EditText) findViewById(R.id.getphone);
		mWebsite = (EditText) findViewById(R.id.getwebsite);
		mFood = (Spinner) findViewById(R.id.getfood);
		mDistance = (Spinner) findViewById(R.id.getDistance);
		mBar = (RatingBar) findViewById(R.id.getRating);

		Button confirmButton = (Button) findViewById(R.id.hotel_edit_button);
		Button deleteButton = (Button) findViewById(R.id.hotel_delete_button);
		mRowId = null;
		Bundle extras = getIntent().getExtras();
		mRowId = (savedInstanceState == null) ? null : (Long) savedInstanceState
				.getSerializable(RestDbAdapter.KEY_ROWID);
		if (extras != null) {
			mRowId = extras.getLong(RestDbAdapter.KEY_ROWID);
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
				AlertDialog.Builder builder2 = new AlertDialog.Builder(RestDetails.this);
				builder2.setMessage("Are you sure you want to delete?")
				.setCancelable(false)
				.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
					public void onClick(DialogInterface dialog, int location) {
						if(mRowId==null) {
							long id = mDbHelper.createRest(" ", " ", " ", " ", " ", " ", 0, 0);
							if (id > 0) {
								mRowId = id;
							}
						}
						setResult(RESULT_OK);
						finish();
						mDbHelper.deleteRest(mRowId);	
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

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)  {
		Bundle bundle = new Bundle();
		if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {

			//TODO: if time, have this information store the stuff so that you can come back to it
			String name = mName.getText().toString();
			String address = mAddress.getText().toString();
			String number = mNumber.getText().toString();
			String website = mWebsite.getText().toString();
			String food = mFood.getSelectedItem().toString();
			float ranking = mBar.getRating();
			String distance = mDistance.getSelectedItem().toString();

			if (mRowId == null) {
				long id = mDbHelper.createRest(name, address, number, website, food, distance, ranking, 0);
				if (id > 0) {
					mRowId = id;
				}
			}

			bundle.putLong("Row", mRowId);
			bundle.putString("Name", name);
			bundle.putString("Address", address);
			bundle.putString("Number", number);
			bundle.putString("Website", website);
			bundle.putString("Food", food);
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
			Cursor rest = mDbHelper.fetchRest(mRowId);
			startManagingCursor(rest);
			mName.setText(rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_NAME)));
			mAddress.setText(rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_ADDRESS)));
			mNumber.setText(rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_NUMBER)));
			mWebsite.setText(rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_WEBSITE)));
			directions = rest.getInt(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_DIRECTIONS));

			String distString = rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_DISTANCE)); //the value you want the position for
			String foodString = rest.getString(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_FOOD));
			ArrayAdapter<String> distAdap = (ArrayAdapter<String>) mDistance.getAdapter(); //cast to an ArrayAdapter
			ArrayAdapter<String> foodAdap = (ArrayAdapter<String>) mFood.getAdapter();
			int distPosition = distAdap.getPosition(distString);
			int foodPosition = foodAdap.getPosition(foodString);
			//set the default according to value
			mFood.setSelection(foodPosition);
			mDistance.setSelection(distPosition);
			mBar.setRating(rest.getFloat(rest.getColumnIndexOrThrow(RestDbAdapter.KEY_RATING)));
		}
	}

	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		saveState();
		outState.putSerializable(RestDbAdapter.KEY_ROWID, mRowId);
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
		String food = mFood.getSelectedItem().toString();
		float ranking = mBar.getRating();
		String distance = mDistance.getSelectedItem().toString();


		if (mRowId == null) {
			long id = mDbHelper.createRest(name, address, number, website, food, distance, ranking, 0);
			if (id > 0) {
				mRowId = id;
			}
		} else {
			mDbHelper.updateRest(mRowId, name, address, number, website, food, distance, ranking, directions);
		}
	}
}