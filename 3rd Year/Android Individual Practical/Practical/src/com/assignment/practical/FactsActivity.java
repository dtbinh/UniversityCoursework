package com.assignment.practical;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class FactsActivity extends Activity{
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.facts);
		String[] facts = getResources().getStringArray(R.array.listknowledge);
		ListView lv = (ListView) findViewById(R.id.facts);
		lv.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, facts));
		
        lv.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            		Bundle b = new Bundle();
            		b.putInt("NUMBER", position);
            		Intent i = new Intent(FactsActivity.this, DisplayFactsActivity.class);
            		i.putExtras(b);
            		startActivity(i);
        	    }
        	  }); 
	}

}

//Create bundle
//bundle.put shit
//create intent
//put extras into intent (as string
//start intent
//Bundle = gentIntent().getExtras()
//bundle.getString