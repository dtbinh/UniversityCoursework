package com.assignment.practical;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

public class HotelDbAdapter {

	//Database fields
	public static final String KEY_ROWID = "_id";
	public static final String KEY_NAME = "name";
	public static final String KEY_RANKING = "ranking";
	public static final String KEY_NUMBER = "number";
	public static final String KEY_ADDRESS = "address";
	public static final String KEY_DISTANCE = "distance";
	public static final String KEY_WEBSITE = "website";
	public static final String KEY_DIRECTIONS = "directions";
	private static final String DATABASE_TABLE = "hotel";
	private Context context;
	private SQLiteDatabase database;
	private HotelDatabaseHelper dbHelper;

	public HotelDbAdapter(Context context) {
		this.context = context;
	}

	public HotelDbAdapter open() throws SQLException {
		dbHelper = new HotelDatabaseHelper(context);
		database = dbHelper.getWritableDatabase();
		return this;
	}

	public void close() {
		dbHelper.close();
	}




	public long createHotel(String name, String address, String number, String website, String distance, float ranking, int directions) {
		ContentValues initialValues = createContentValues(name, address, number, website, distance, ranking, directions);
		return database.insert(DATABASE_TABLE, null, initialValues);
	}

	public boolean updateHotel(long rowId, String name, String address, String number, String website, String distance, float ranking, int directions) {
		ContentValues updateValues = createContentValues(name, address, number, website, distance, ranking, directions);

		return database.update(DATABASE_TABLE, updateValues, KEY_ROWID + "=" + rowId, null) > 0;

	}

	public boolean deleteHotel(long rowId) {
		return database.delete(DATABASE_TABLE, KEY_ROWID + "=" + rowId, null) > 0;
	}

	public Cursor fetchHotel(long rowId) throws SQLException {
		Cursor mCursor = database.query(true, DATABASE_TABLE, new String [] {
				KEY_ROWID, KEY_NAME, KEY_ADDRESS, KEY_NUMBER, KEY_WEBSITE, KEY_DISTANCE, KEY_RANKING, KEY_DIRECTIONS }, KEY_ROWID + "=" + rowId, null, null, null, null, null);
		if(mCursor !=null) {
			mCursor.moveToFirst();
		}
		return mCursor;
	}

	private ContentValues createContentValues(String name, String address, String number, String website, String distance, float ranking, int directions) {
		ContentValues values = new ContentValues();
		values.put(KEY_NAME, name);
		values.put(KEY_ADDRESS, address);
		values.put(KEY_NUMBER, number);
		values.put(KEY_WEBSITE, website);
		values.put(KEY_DISTANCE, distance);
		values.put(KEY_RANKING, ranking);
		values.put(KEY_DIRECTIONS, directions);
		return values;
	}

	public Cursor fetchAllHotels(String orderby) {
		return database.query(DATABASE_TABLE, new String[] {KEY_ROWID,
				KEY_NAME, KEY_ADDRESS, KEY_NUMBER, KEY_WEBSITE, KEY_DISTANCE, KEY_RANKING, KEY_DIRECTIONS }, null, null, null,
				null, orderby);
		}
	
	public Cursor fetchAllHotelsByRanking() {
		return database.query(DATABASE_TABLE, new String[] {KEY_ROWID,
				KEY_NAME, KEY_ADDRESS, KEY_NUMBER, KEY_WEBSITE, KEY_DISTANCE, KEY_RANKING, KEY_DIRECTIONS }, null, null, null,
				null, KEY_RANKING + " DESC");
	}

}
