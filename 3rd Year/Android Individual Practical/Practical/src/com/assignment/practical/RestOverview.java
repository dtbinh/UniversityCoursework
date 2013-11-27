package com.assignment.practical;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListActivity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.RatingBar;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

//TODO: Rename Hotel_List to be more generic

public class RestOverview extends ListActivity {
	private RestDbAdapter dbHelper;
	private static final int ACTIVITY_CREATE = 0;
	private static final int ACTIVITY_EDIT = 1;
	private static final int ACTIVITY_PHONE = 2;
	private static final int ACTIVTY_VIEW = 3;
	private static final int OPTIONS = 4;
	private static final int SORT_FOOD = 5;
	private static final int SORT_DISTANCE = 6;
	private static final int SORT_RATING = 7;
	private static final int SORT_NAME = 8;
	private static final int TEXT = 9;
	private Cursor cursor;
	private int sorter;
	private long position;
	private boolean delete = false;
	private boolean edit = false;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//TODO change to rest
		setContentView(R.layout.hotel_list);
		dbHelper = new RestDbAdapter(this);
		dbHelper.open();
		fillData();

	}

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
			createRest();
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
			return true;
		case R.id.sort:
			showDialog(SORT_NAME);
			return true;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.insert:
			createRest();
			return true;
		}
		return super.onOptionsItemSelected(item);
	}

	private void createRest() {
		Intent i = new Intent(this, RestDetails.class);
		startActivityForResult(i, ACTIVITY_CREATE);
	}
	
	private void deleteRest(long rowId) {
			dbHelper.deleteRest(rowId);
			fillData();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode,
			Intent intent) {
		super.onActivityResult(requestCode, resultCode, intent);
		dbHelper.open();
		if(requestCode == ACTIVITY_CREATE && resultCode == RESULT_FIRST_USER) {
			dbHelper.deleteRest(intent.getExtras().getLong("Row"));
		}

		fillData();
	}

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
						Intent i = new Intent(RestOverview.this, RestDetails.class);
						i.putExtra(RestDbAdapter.KEY_ROWID, position);
						if (dbHelper != null) {
							dbHelper.close();
						}
						startActivityForResult(i, ACTIVITY_EDIT);
						break;
					case 1:
						dialog.cancel();
						AlertDialog.Builder builder2 = new AlertDialog.Builder(RestOverview.this);
						builder2.setMessage("Are you sure you want to delete?")
						.setCancelable(false)
						.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int id) {
								deleteRest(position);
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
			final CharSequence[] items2 = {"Sort by Name", "Sort by Distance", "Sort by Rating", "Sort by Food", "Cancel"};
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
						sorter = SORT_FOOD;
						break;
					case 4:
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

	private void fillData() {

		switch(sorter) {
		case SORT_DISTANCE:
			cursor = dbHelper.fetchAllRest(RestDbAdapter.KEY_DISTANCE);
			break;
		case SORT_RATING:
			cursor = dbHelper.fetchAllRestByRanking();
			break;
		case SORT_NAME:
			cursor = dbHelper.fetchAllRest(RestDbAdapter.KEY_NAME);
			break;
		case SORT_FOOD:
			cursor = dbHelper.fetchAllRest(RestDbAdapter.KEY_FOOD);
			break;
		default:
			cursor = dbHelper.fetchAllRest(RestDbAdapter.KEY_NAME);
			break;
		}

		startManagingCursor(cursor);

		CursorAdapter rest = new CursorAdapter(this, cursor) {

			@Override
			public View newView(Context context, Cursor cursor, ViewGroup parent) {
				View view = View.inflate(context, R.layout.hotel_row, null);
				view.setClickable(true);
				view.setFocusable(true);
				view.setBackgroundResource(android.R.drawable.menuitem_background);
				
				RelativeLayout addtext = (RelativeLayout) view.findViewById(R.id.databaselayout);	
				RelativeLayout.LayoutParams lparams = new LayoutParams(android.view.ViewGroup.LayoutParams.WRAP_CONTENT, android.view.ViewGroup.LayoutParams.WRAP_CONTENT);
			    TextView tv = new TextView(context);
			    TextView distance = (TextView) view.findViewById(R.id.hoteldistance);
				RatingBar rating = (RatingBar) view.findViewById(R.id.hotelrating);
				RelativeLayout.LayoutParams lparams2 = (LayoutParams) rating.getLayoutParams();
			    
				lparams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
			    lparams.addRule(RelativeLayout.BELOW, distance.getId());
			    
			    tv.setId(TEXT);
			   
			    lparams2.addRule(RelativeLayout.BELOW, tv.getId());
			    rating.setLayoutParams(lparams2);
			    
			    tv.setText("test");
			    addtext.addView(tv, lparams);


				if(delete) {
					//TODO: Make the image a trashcan
				}
				if(edit) {
					//TODO: Make the image a notepad
				}
				return view;
			}

			@Override
			public void bindView(View view, Context context, Cursor cursor) {
				
				TextView name = (TextView) view.findViewById(R.id.hotelname);
				TextView address = (TextView) view.findViewById(R.id.hoteladdress);
				TextView number = (TextView) view.findViewById(R.id.hotelnumber);
				TextView distance = (TextView) view.findViewById(R.id.hoteldistance);
				TextView food = (TextView) view.findViewById(TEXT);
				RatingBar rating = (RatingBar) view.findViewById(R.id.hotelrating);
				Button options = (Button) view.findViewById(R.id.hoteloptions);

				final String call = cursor.getString(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_NUMBER)).replace(" ", "");

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
				final long id = cursor.getLong(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_ROWID));
				view.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						if(delete) {
							AlertDialog.Builder builder2 = new AlertDialog.Builder(RestOverview.this);
							builder2.setMessage("Are you sure you want to delete?")
							.setCancelable(false)
							.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
								public void onClick(DialogInterface dialog, int location) {
									deleteRest(id);
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
							Intent i = new Intent(RestOverview.this, RestDetails.class);
							i.putExtra(RestDbAdapter.KEY_ROWID, id);
							startActivityForResult(i, ACTIVITY_EDIT);

						}
						else {
							Intent i = new Intent(RestOverview.this, RestSpecific.class);
							i.putExtra(RestDbAdapter.KEY_ROWID, id);
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

				name.setText(cursor.getString(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_NAME)));
				address.setText(cursor.getString(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_ADDRESS)));
				number.setText(cursor.getString(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_NUMBER)));
				distance.setText(cursor.getString(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_DISTANCE)));
				food.setText(cursor.getString(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_FOOD)));
				rating.setRating(cursor.getFloat(cursor.getColumnIndexOrThrow(RestDbAdapter.KEY_RATING))); 


			}
		};
		setListAdapter(rest);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (dbHelper != null) {
			dbHelper.close();
		}
	}

}
