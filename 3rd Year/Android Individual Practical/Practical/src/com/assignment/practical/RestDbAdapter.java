package com.assignment.practical;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class RestDbAdapter {
	
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_NUMBER = "number";
	public static final String KEY_WEBSITE = "website";
	public static final String KEY_FOOD = "food";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_RATING = "rating";
	public static final String KEY_DIRECTIONS = "directions";
	public static final String DATABASE_TABLE = "rest";
	private Context context;
	private SQLiteDatabase database;
	private ResturauntsDatabaseHelper dbHelper;
	
	public RestDbAdapter(Context context) {
		this.context = context;
	}
	
	public RestDbAdapter open() throws SQLException {
		dbHelper = new ResturauntsDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}
	
	public void close() {
		dbHelper.close();
	}
	
	public long createRest(String name, String address, String number, String website, String food, String distance, float rating, int directions) {
		ContentValues initialValues = createContentValues(name, address, number, website, food, distance, rating, directions);
		return database.insert(DATABASE_TABLE, null, initialValues);
	}
	
	public boolean updateRest(long rowId, String name, String address, String number, String website, String food, String distance, float rating, int directions) {
		ContentValues updateValues = createContentValues(name, address, number, website, food, distance, rating, directions);
		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "=" + rowId, null) > 0;	
	}
	
	public boolean deleteRest(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}
	
	public Cursor fetchRest(long rowId) throws SQLException {
		
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String [] {KEY_ROWID, KEY_NAME, KEY_ADDRESS, KEY_NUMBER, KEY_WEBSITE, KEY_FOOD, KEY_DISTANCE, KEY_RATING, KEY_DIRECTIONS}, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if(mCursor!=null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}
	
	
	private ContentValues createContentValues(String name, String address, String number, String website, String food, String distance, float rating, int directions) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_ADDRESS, address);
		values.put(KEY_NUMBER, number);
		values.put(KEY_WEBSITE, website);
		values.put(KEY_FOOD, food);
		values.put(KEY_DISTANCE, distance);
		values.put(KEY_RATING, rating);
		values.put(KEY_DIRECTIONS, directions);
		return values;
	}
	
	public Cursor fetchAllRest(String specific) {
		return database.query(DATABASE_TABLE, new String [] {KEY_ROWID, 
				KEY_NAME, KEY_ADDRESS, KEY_NUMBER, KEY_WEBSITE, KEY_FOOD, KEY_DISTANCE, KEY_RATING, KEY_DIRECTIONS }, null, null, null, 
				null, specific);
	}
	
	public Cursor fetchAllRestByRanking() {
		return database.query(DATABASE_TABLE, new String [] {KEY_ROWID, 
				KEY_NAME, KEY_ADDRESS, KEY_NUMBER, KEY_WEBSITE, KEY_FOOD, KEY_DISTANCE, KEY_RATING, KEY_DIRECTIONS }, null, null, null, 
				null, KEY_RATING + " DESC");
	}
	
}
