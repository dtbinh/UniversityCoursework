package com.assignment.practical;

import android.app.ListActivity;
import android.content.res.Resources;
import android.os.Bundle;
import android.widget.ArrayAdapter;

public class HotelDirections extends ListActivity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.hotel_list);
		Bundle extras = getIntent().getExtras();
		int directions = extras.getInt("Directions");
		String hotel[] = null;
		try{
			hotel = getResources().getStringArray(directions);
		} catch (Resources.NotFoundException e){
			hotel = getResources().getStringArray(R.array.nothingarray);
		}

		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, hotel));
	}
}
