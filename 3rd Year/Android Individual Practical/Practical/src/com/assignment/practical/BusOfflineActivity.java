package com.assignment.practical;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class BusOfflineActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.offlinebus);
		String[] landmarks = getResources().getStringArray(R.array.landmarks);
		ListView lv = (ListView) findViewById(R.id.landmarks_bus);
		lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, landmarks));
		Button b = (Button) findViewById(R.id.menu);


        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        	      // When clicked, show a toast with the TextView text
            	//TODO Make a switch case relating the String of Landmarks, with the case ID being passed in a bundle to the new activity. This activity uses this bundle to identify what to show.
        	      Toast.makeText(getApplicationContext(), ((TextView) view).getText(),
        	          Toast.LENGTH_SHORT).show();
        	      
        	    }
        	  });
        
        b.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				finish();
				
			}
		});
	}

}
