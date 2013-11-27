package com.assignment.practical;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Simple example of how to use the {@link RealViewSwitcher} class.
 *
 * @author Marc Reichelt, <a href="http://www.marcreichelt.de/">http://www.marcreichelt.de/</a>
 */
public class TestActivity extends Activity {
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
	@Override
	public void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// Create the view switcher
		HorizontalPager realViewSwitcher = new HorizontalPager(getApplicationContext());

		// Add some views to it

		for (int i = 0; i < 5; i++) {
			View v = new View(getApplicationContext()); 
			LayoutInflater inflater = (LayoutInflater)   getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE); 
			v = inflater.inflate(R.layout.hotel_display, null);
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


			realViewSwitcher.addView(v);
		}

		// set as content view
		setContentView(realViewSwitcher);

		// Yeah, it really is as simple as this :-)

		/*
		 * Note that you can also define your own views directly in a resource XML, too by using:
		 * <com.github.ysamlan.horizontalpager.RealViewSwitcher
		 * android:layout_width="fill_parent"
		 * android:layout_height="fill_parent"
		 * android:id="@+id/real_view_switcher">
		 * <!-- your views here -->
		 * </com.github.ysamlan.horizontalpager.RealViewSwitcher>
		 */



		// OPTIONAL: listen for screen changes
		realViewSwitcher.setOnScreenSwitchListener(onScreenSwitchListener);
	}

	private final HorizontalPager.OnScreenSwitchListener onScreenSwitchListener =
		new HorizontalPager.OnScreenSwitchListener() {
		@Override
		public void onScreenSwitched(final int screen) {
			/*
			 * this method is executed if a screen has been activated, i.e. the screen is
			 * completely visible and the animation has stopped (might be useful for
			 * removing / adding new views)
			 */
			Log.d("HorizontalPager", "switched to screen: " + screen);
		}
	};
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
}

