package com.assignment.practical;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class HotelDatabaseHelper extends SQLiteOpenHelper{
	private static final String DATABASE_NAME = "hotel";
	
	private static final int DATABASE_VERSION = 1;
	
	//Database creation sql statement
	private static final String DATABASE_CREATE = "create table if not exists hotel (_id integer primary key autoincrement, " 
			+ "name text not null, address text not null, number text, website text, distance text not null, ranking integer, directions integer);";
	//Database initial hotel values
	private static final String DATABASE_INSERT_DUVIN = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Hotel Du Vin', '11 Bristo Place', '0131 247 4900', 'http://www.hotelduvin.com/', 'Close (5 Minutes)', 0, " + R.array.hotelduvin + ");";

	private static final String DATABASE_INSERT_TENPLACE = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Ten Hill Place', '10 Hill Place', '0131 662 2080', 'www.tenhillplace.com/', 'Close (5 Minutes)', 0, " + R.array.tenhillplace + ");";

	private static final String DATABASE_INSERT_RICHMOND = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Richmond Place', '5 Richmond Place', '0131 651 2119', 'http://www.edinburghfirst.co.uk/for-accommodation/richmond-place-apartments-internal', 'Near (10 Minutes)', 0, " + R.array.richmondplace + ");";

	private static final String DATABASE_INSERT_KENNETH = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Kenneth Mackenzie', 'Richmond Place', '0131 651 2007', 'http://www.edinburghfirst.co.uk/for-accommodation/kenneth-mackenzie-internal', 'Near (10 Minutes)', 0, " + R.array.kennethmackenzie + ");";

	private static final String DATABASE_INSERT_TRAVELODGE = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Travelodge Central Hotel', '33 St. Marys Street', '0871 984 6137', 'http://www.travelodge.co.uk/hotels/info?hotelId=205', 'Far (15 Minutes)', 0, " + R.array.travelodgecentralhotel + ");";

	private static final String DATABASE_INSERT_EUROHOSTEL = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Euro Hostel', '4 Kincaids Court', '0845 490 0461', 'http://www.euro-hostels.co.uk/edinburgh', 'Near (10 Minutes)', 0, " + R.array.eurohostel + ");";

	private static final String DATABASE_INSERT_MEADOWPLACE = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Nineteen Meadow Place', '19 Meadow Place', '0131 229 8316', 'http://www.19meadowplace.com/', 'Far (15 Minutes)', 0, " + R.array.nineteenmeadowsplace + ");";

	private static final String DATABASE_INSERT_HOLIDAYINN = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Holiday Inn Express Royal Mile', '300 Cowgate', '0131 524 8400', 'http://www.hieedinburgh.co.uk/', 'Near (10 Minutes)', 0, " + R.array.holidayinnexpressroyalmile + ");";

	private static final String DATABASE_INSERT_GRASSMARKET = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Grassmarket Hotel', '94 Grassmarket', '0131 220 2299', 'http://www.grassmarkethoteledinburgh.co.uk/', 'Near (10 Minutes)', 0, " + R.array.grassmarkethotel + ");";

	private static final String DATABASE_INSERT_APEXCITY = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Apex City Hotel', '61 Grassmarket', '0131 243 3456', 'http://www.apexhotels.co.uk/', 'Near (10 Minutes)', 0, " + R.array.apexcityhotel + ");";

	private static final String DATABASE_INSERT_IBIS = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Ibis Edinburgh', '6 Hunter Square', '0131 240 7000', 'http://www.ibishotel.com/gb/hotel-2039-ibis-edinburgh-centre/index.shtml', 'Near (10 Minutes)', 0, " + R.array.ibisedinburgh + ");";

	private static final String DATABASE_INSERT_SMARTHOSTELS = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Smart City Hostels', '50 Blackfriars Street', '0131 524 1989', 'http://www.smartcityhostels.com/', 'Near (10 Minutes)', 0, " + R.array.smartcityhostels + ");";

	private static final String DATABASE_INSERT_ADVOCATES = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Advocates Apartments', '23 Blair Street', '0797 105 1484', 'http://www.advocates-apartments.com/', 'Near (10 Minutes)', 0, " + R.array.advocatesapartments + ");";

	private static final String DATABASE_INSERT_APEXINT = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Apex International', '31 Grassmarket', '0131 300 3456', 'http://www.apexhotels.co.uk/hotels/edinburgh-international/', 'Near (10 Minutes)', 0, " + R.array.apexinternational + ");";

	private static final String DATABASE_INSERT_MISSONI = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Hotel Missoni', '1 George IV Bridge', '0131 220 6666', 'http://www.hotelmissoni.com/', 'Near (10 Minutes)', 0, " + R.array.hotelmissoni + ");";

	private static final String DATABASE_INSERT_MEADOWS = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('The Meadows Hotel', '72 Causewayside', '0131 229 8316', 'http://www.themeadowshotel.com/', 'Far (15 Minutes)', 0, " + R.array.themeadowshotel + ");";

	private static final String DATABASE_INSERT_RADISSON = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Radisson Blu', '80 High Street', '0131 557 9797', 'http://www.radissonblu.co.uk/hotel-edinburgh', 'Near (10 Minutes)', 0, " + R.array.radissonblu + ");";

	private static final String DATABASE_INSERT_BANK = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
    		+ " VALUES ('Bank Hotel', '1 South Bridge', '0131 556 9940', 'http://www.bankhoteledinburgh.co.uk/', 'Near (10 Minutes)', 0, " + R.array.bankhotel + ");";

	private static final String DATABASE_INSERT_CASTLE = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Castle Apartments', '16 Johnston Terrace', '0131 240 0080', 'http://www.castleapartments.co.uk/', 'Far (15 Minutes)', 0, " + R.array.castleapartments + ");";

	private static final String DATABASE_INSERT_ROYALAPART = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Royal Mile Apartments', ' 375 High Street', '0131 477 3680', 'http://www.edinburgh-accommodation-edinburgh.com/', 'Near (10 Minutes)', 0, " + R.array.royalmileapartments + ");";

	private static final String DATABASE_INSERT_BRODIES = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Brodies Hostels', '93 High Street', '0131 556 2223', 'http://www.brodieshostels.co.uk/', 'Far (15 Minutes)', 0, " + R.array.brodieshostels + ");";

	private static final String DATABASE_INSERT_FRASER = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Fraser Suites', '12 St Giles Street ', '0131 221 7200', 'http://edinburgh.frasershospitality.com/', 'Near (10 Minutes)', 0, " + R.array.frasersuites + ");";

	private static final String DATABASE_INSERT_BARCELO = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Barcelo Edinburgh Carlton', '19 North Bridge', '0131 472 3000', 'http://www.barcelo-hotels.co.uk/hotels/scotland/barcelo-edinburgh-carlton-hotel', 'Near (10 Minutes)', 0, " + R.array.barceloedinburghcarlton + ");";

	private static final String DATABASE_INSERT_SCOTSMAN = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('The Scotsman Hotel', '20 North Bridge', '0131 556 5565', 'www.thescotsmanhotel.co.uk', 'Near (10 Minutes)', 0, " + R.array.thescotsmanhotel + ");";

	private static final String DATABASE_INSERT_JURYINN = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Jurys Inn Edinburgh', '43 Jeffery Street', '0131 200 3300', 'http://edinburghhotels.jurysinns.com/', 'Far (15 Minutes)', 0, " + R.array.jurysinnedinburgh + ");";

	private static final String DATABASE_INSERT_BALMORAL = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Balmoral', '1 Princes Street', '0131 556 2414', 'http://www.thebalmoralhotel.com/', 'Far (15 Minutes)', 0, " + R.array.balmoral + ");";

	private static final String DATABASE_INSERT_BESTWEST = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Best Western', '73 Lauriston Place', '0131 622 7979', 'http://www.edinburghcityhotel.com/', 'Far (15 Minutes)', 0, " + R.array.bestwestern + ");";

	private static final String DATABASE_INSERT_PREMIERINN = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Premier Inn', '82 Lauriston Place', '0871 527 8366', 'http://www.premierinn.com/en/edinburgh-hotels.html', 'Far (15 Minutes)', 0, " + R.array.premierinn + ");";

	private static final String DATABASE_INSERT_NOVOTEL = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Novotel', '80 Lauriston Place', '0131 229 1077', 'http://www.novotel.com/gb/hotel-3271-novotel-edinburgh-centre/index.shtml', 'Far (15 Minutes)', 0, " + R.array.novotel + ");";

	private static final String DATABASE_INSERT_MERCURE = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Mercure Point Hotel', '34 Bread Street', '0131 221 5555', 'http://www.pointhoteledinburgh.co.uk/', 'Far (15 Minutes)', 0, " + R.array.mercurepointhotel + ");";

	private static final String DATABASE_INSERT_WITCHERY = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('The Witchery by the Castle', '352 Castlehill', '0131 225 5613', 'http://www.thewitchery.com/', 'Far (15 Minutes)', 0, " + R.array.thewitcherybythecastle + ");";

	private static final String DATABASE_INSERT_COUNAN = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Counan Hotel', '6 Minto Street', '0131 667 4454', 'http://www.bnbselect.com/bnb/35183', 'Far (15 Minutes)', 0, " + R.array.counanhotel + ");";

	private static final String DATABASE_INSERT_SALISBURY = "INSERT INTO " + DATABASE_NAME + " (name, address, number, website, distance, ranking, directions)"
			+ " VALUES ('Salisbury Hotel', '43 Salisbury Road', '0131 667 1264', 'http://www.the-salisbury.co.uk/', 'Far (15 Minutes)', 0, " + R.array.salisburyhotel + ");"; 


	HotelDatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}
	
	//Method is called during creation of the Database
	@Override
	public void onCreate(SQLiteDatabase database) {
		database.execSQL(DATABASE_CREATE);
		database.execSQL(DATABASE_INSERT_DUVIN);
		database.execSQL(DATABASE_INSERT_TENPLACE);
		database.execSQL(DATABASE_INSERT_RICHMOND);
		database.execSQL(DATABASE_INSERT_KENNETH);
		database.execSQL(DATABASE_INSERT_TRAVELODGE);
		database.execSQL(DATABASE_INSERT_EUROHOSTEL);
		database.execSQL(DATABASE_INSERT_MEADOWPLACE);
		database.execSQL(DATABASE_INSERT_HOLIDAYINN);
		database.execSQL(DATABASE_INSERT_GRASSMARKET);
		database.execSQL(DATABASE_INSERT_APEXCITY);
		database.execSQL(DATABASE_INSERT_IBIS);
		database.execSQL(DATABASE_INSERT_BANK);
		database.execSQL(DATABASE_INSERT_SMARTHOSTELS);
		database.execSQL(DATABASE_INSERT_ADVOCATES);
		database.execSQL(DATABASE_INSERT_APEXINT);
		database.execSQL(DATABASE_INSERT_MISSONI);
		database.execSQL(DATABASE_INSERT_MEADOWS);
		database.execSQL(DATABASE_INSERT_RADISSON);
		database.execSQL(DATABASE_INSERT_CASTLE);
		database.execSQL(DATABASE_INSERT_ROYALAPART);
		database.execSQL(DATABASE_INSERT_BRODIES);
		database.execSQL(DATABASE_INSERT_FRASER);
		database.execSQL(DATABASE_INSERT_BARCELO);
		database.execSQL(DATABASE_INSERT_SCOTSMAN);
		database.execSQL(DATABASE_INSERT_JURYINN);
		database.execSQL(DATABASE_INSERT_BALMORAL);
		database.execSQL(DATABASE_INSERT_BESTWEST);
		database.execSQL(DATABASE_INSERT_PREMIERINN);
		database.execSQL(DATABASE_INSERT_NOVOTEL);
		database.execSQL(DATABASE_INSERT_MERCURE);
		database.execSQL(DATABASE_INSERT_WITCHERY);
		database.execSQL(DATABASE_INSERT_COUNAN);
		database.execSQL(DATABASE_INSERT_SALISBURY); 
	}
	
	//Method is called during an upgrade of the database
	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion,
			int newVersion) {
		Log.w(HotelDatabaseHelper.class.getName(), "Upgrading database from version "+ oldVersion + "to"
														+ newVersion + ", which will destroy all old data");
		database.execSQL("DROP TABLE IF EXISTS hotel");
		onCreate(database);
	}
}
