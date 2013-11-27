package com.assignment.practical;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DisplayFactsActivity extends Activity {
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.displayfacts);
		Button menu = (Button) findViewById(R.id.menu);
		Bundle b = getIntent().getExtras();
		TextView tv = (TextView) findViewById(R.id.showfacts);
		//checks if the number selected is greater than available data (aka array out of bounds index
		if(b.getInt("NUMBER")>=(getResources().getStringArray(R.array.information)).length) {
			tv.setText("I'm sorry. No information is available.");		
		}
		else {
			//TODO:set this to work by using the string intent to find the corresponding data
			tv.setText( (getResources().getStringArray(R.array.information))[b.getInt("NUMBER")]);
		}
		
		menu.setOnClickListener(new View.OnClickListener() {
			public void onClick(View arg0) {
				finish();				
			}
		});
	}
}
