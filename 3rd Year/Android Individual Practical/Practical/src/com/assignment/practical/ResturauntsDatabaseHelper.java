package com.assignment.practical;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class ResturauntsDatabaseHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "rest";
	
	private static final int DATABASE_VERSION = 1;
	
	private static final String DATABASE_CREATE = "create table rest (_id integer primary key autoincrement, "
		+ "name text not null, address text not null, number text not null, website text, food text not null, distance text, rating integer, directions integer);";
	private static final String DATABASE_INSERT_DUVIN = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, food, distance, rating, directions)"
		+ " VALUES ('Hotel Du Vin', '11 Bristo Place', '0131 247 4900', 'http://www.hotelduvin.com/', 'Italian', 'Close (5 Minutes)', 0, " + R.array.hotelduvin + ");";
	

	public ResturauntsDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INSERT_DUVIN);
	}
	
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		Log.w(ResturauntsDatabaseHelper.class.getName(), "Upgrading database, destroying all data");
		database.execSQL("DROP TABLE IF EXISTS rest");
		onCreate(database);
	}
}
