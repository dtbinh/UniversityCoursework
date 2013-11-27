package com.assignment.practical;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;


/*
 * HotelOverview is where the user can interact with the Hotel Database
 * 
 * Code adapted from Lars Vogel http://www.vogella.de
 * Images used with permission of dryicons @ http://dryicons.com
 * and
 * From Oxygen Team
 * http://www.iconarchive.com/show/oxygen-icons-by-oxygen-icons.org/Actions-document-edit-icon.html
 */

//TODO: add key listener to jump to these hotels
//TODO: Add, if user inserts and then doesn't follow through, the hotel still is in the insert bar
//TODO: Make ranking/rating consistent throughout application
public class HotelOverview extends ListActivity {
	private HotelDbAdapter dbHelper;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_PHONE = 2;
	private static final int ACTIVTY_VIEW = 3;
	private static final int OPTIONS = 4;
	private static final int SORT_DISTANCE = 6;
	private static final int SORT_RATING = 7;
	private static final int SORT_NAME = 8;
	
	private int sorter;
	private long position;
	private Cursor cursor;
	private boolean delete = false;
	private boolean edit = false;
	private long storedrow = -1;
	private String storedname;
	private String storedaddress;
	private String storednumber;
	private float storedranking;
	private String storeddistance;
	
	
	protected Dialog onCreateDialog(int id) {
		Dialog alert;
		switch(id) {
		case OPTIONS:
			final CharSequence[] items = {"Edit", "Delete", "Cancel"};

			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setTitle("More Options");
			builder.setItems(items, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch(item) {
					case 0:
						Intent i = new Intent(HotelOverview.this, HotelDetails.class);
						i.putExtra(HotelDbAdapter.KEY_ROWID, position);
						if (dbHelper != null) {
							dbHelper.close();
						}
						startActivityForResult(i, ACTIVITY_EDIT);
						break;
					case 1:
						dialog.cancel();
						AlertDialog.Builder builder2 = new AlertDialog.Builder(HotelOverview.this);
						builder2.setMessage("Are you sure you want to delete?")
						       .setCancelable(false)
						       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						                deleteHotel(position);
						           }
						       })
						       .setNegativeButton("No", new DialogInterface.OnClickListener() {
						           public void onClick(DialogInterface dialog, int id) {
						                dialog.cancel();
						           }
						       });
						builder2.show();
						break;
					case 2:
						break;
					default:
						break;
					}
				}
			});
			alert = builder.create();
			break;
		
		case SORT_NAME:
			final CharSequence[] items2 = {"Sort by Name", "Sort by Distance", "Sort by Rating", "Cancel"};
			AlertDialog.Builder builder2 = new AlertDialog.Builder(this);
			builder2.setTitle("Sort Options");
			builder2.setItems(items2, new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int item) {
					switch(item) {
					case 0:
						sorter = SORT_NAME;
						break;
					case 1:
						sorter = SORT_DISTANCE;
						break;
					case 2:
						sorter = SORT_RATING;
						break;
					case 3:
						break;
					default:
						sorter=SORT_NAME;
						break;
					}
					fillData();
				}
			});
			
			alert = builder2.create();
			break;
		default:
			alert = null;
		}
		return alert;
	}

	/** Called when the activity is first created. */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_list);
		dbHelper = new HotelDbAdapter(this);
		dbHelper.open();
		fillData();
	}

	// Create the menu based on the XML definition
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.listmenu, menu);			
		return true;
	} 

	// Reaction to the menu selection
	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createHotel();
			return true;
		case R.id.delete:
			if(delete) {
				delete = false;
			}
			else {
				Toast.makeText(this, "To cancel delete, press menu and select delete again", Toast.LENGTH_LONG).show();
				delete = true;
			}
			edit = false;
			fillData();
			return true;
		case R.id.edit:
			if(edit) {
				edit = false;
			}
			else {
				edit = true;
				Toast.makeText(this, "To cancel edit, press menu and select edit again", Toast.LENGTH_LONG).show();
			}
			delete = false;
			fillData();
			return true;
		case R.id.sort:
			showDialog(SORT_NAME);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	private void deleteHotel(long rowId) {
		dbHelper.deleteHotel(rowId);
		fillData();
	}

	private void createHotel() {
		Intent i = new Intent(this, HotelDetails.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}

	// Called with the result of the other activity
	// requestCode was the origin request code send to the activity
	// resultCode is the return code, 0 is everything is ok
	// intend can be use to get some data from the caller
	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		dbHelper.open();
		if(requestCode == ACTIVITY_CREATE && resultCode == RESULT_FIRST_USER) {
			dbHelper.deleteHotel(intent.getExtras().getLong("Row"));
			storedrow = intent.getExtras().getLong("Row");
			storedname = intent.getExtras().getString("Name");
			storedaddress = intent.getExtras().getString("Address");
			storednumber = intent.getExtras().getString("Number");
			storedranking = intent.getExtras().getFloat("Ranking");
			storeddistance = intent.getExtras().getString("Distance");
		}
		
		fillData();
	}


	private void fillData() {
		switch(sorter) {
		case SORT_DISTANCE:
			cursor = dbHelper.fetchAllHotels(HotelDbAdapter.KEY_DISTANCE);
			break;
		case SORT_RATING:
			cursor = dbHelper.fetchAllHotels(HotelDbAdapter.KEY_RANKING);
			break;
		case SORT_NAME:
			cursor = dbHelper.fetchAllHotels(HotelDbAdapter.KEY_NAME);
			break;
		default:
			cursor = dbHelper.fetchAllHotels(HotelDbAdapter.KEY_NAME);
			break;
		}

		startManagingCursor(cursor);
		CursorAdapter hotels = new CursorAdapter(this, cursor) {

			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				View view = View.inflate(context, R.layout.hotel_row, null);
				view.setClickable(true);
				view.setFocusable(true);
				view.setBackgroundResource(android.R.drawable.menuitem_background);

				return view;
			}

			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				TextView name = (TextView) view.findViewById(R.id.hotelname);
				TextView address = (TextView) view.findViewById(R.id.hoteladdress);
				TextView number = (TextView) view.findViewById(R.id.hotelnumber);
				TextView distance = (TextView) view.findViewById(R.id.hoteldistance);
				RatingBar rating = (RatingBar) view.findViewById(R.id.hotelrating);
				Button options = (Button) view.findViewById(R.id.hoteloptions);
				ImageView iv = (ImageView) view.findViewById(R.id.optionShow);
				Drawable drawable;

				if(delete) {
					drawable = getResources().getDrawable(R.drawable.trashcan3);					
				}
				else if(edit) {
					drawable = getResources().getDrawable(R.drawable.edit);				
				} else {
					drawable = getResources().getDrawable(R.drawable.trashcan3);
					iv.setVisibility(View.INVISIBLE);  //TODO: Set this to the hotel image?
				}
				
				iv.setImageDrawable(drawable);
				
				final String call = cursor.getString(cursor.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER)).replace(" ", "");
				
				number.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
					     try {
					    	 String url = "tel:"+call;
					         Intent callIntent = new Intent(Intent.ACTION_CALL);
					         callIntent.setData(Uri.parse(url));				
					         edit = false;
					         delete = false;
					         startActivityForResult(callIntent, ACTIVITY_PHONE);
					     } catch (ActivityNotFoundException activityException) {
					         Log.e("dialing-example", "Call failed", activityException);
					     }
					
						
					}
					
				});
				final long id = cursor.getLong(cursor.getColumnIndexOrThrow(HotelDbAdapter.KEY_ROWID));
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(delete) {
							AlertDialog.Builder builder2 = new AlertDialog.Builder(HotelOverview.this);
							builder2.setMessage("Are you sure you want to delete?")
							       .setCancelable(false)
							       .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							           public void onClick(DialogInterface dialog, int location) {
							                deleteHotel(id);
							           }
							       })
							       .setNegativeButton("No", new DialogInterface.OnClickListener() {
							           public void onClick(DialogInterface dialog, int id) {
							                dialog.cancel();
							           }
							       });
							builder2.show();
							delete = false;
						}
						else if (edit){
							edit = false;
							Intent i = new Intent(HotelOverview.this, HotelDetails.class);
							i.putExtra(HotelDbAdapter.KEY_ROWID, id);
							startActivityForResult(i, ACTIVITY_EDIT);

						}
						else {
							Intent i = new Intent(HotelOverview.this, HotelSpecific.class);
							i.putExtra(HotelDbAdapter.KEY_ROWID, id);
							startActivityForResult(i, ACTIVTY_VIEW);
							
						}

					}

				});
				
					options.setOnClickListener(new OnClickListener() {

						@Override
						public void onClick(View arg0) {
							if(!delete && !edit) {
							position = id;
							showDialog(OPTIONS);
							}
						}

					});	
				
				name.setText(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbAdapter.KEY_NAME)));
				address.setText(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbAdapter.KEY_ADDRESS)));
				number.setText(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbAdapter.KEY_NUMBER)));
				distance.setText(cursor.getString(cursor.getColumnIndexOrThrow(HotelDbAdapter.KEY_DISTANCE)));
				rating.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(HotelDbAdapter.KEY_RANKING))); 


			}
		};
		setListAdapter(hotels);
	}


	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}
}

