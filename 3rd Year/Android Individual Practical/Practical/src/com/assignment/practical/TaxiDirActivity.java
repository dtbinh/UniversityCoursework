package com.assignment.practical;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class TaxiDirActivity extends Activity {
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.taxi);
        TextView taxiIntro = (TextView) findViewById(R.id.taxitop);
        TextView taxiPhone = (TextView) findViewById(R.id.taxiphone);
        TextView taxiBottom = (TextView) findViewById(R.id.taxiBottom);
        Button taxiButton = (Button) findViewById(R.id.moretaxi);
        ImageView taxiImage = (ImageView) findViewById(R.id.taxiImage);
        
        taxiButton.setVisibility(Button.INVISIBLE);
        
        /*All 24 hour Taxi ranks are
        marked on the map overleaf
        with a ‘TAXI’ symbol – drivers
        will return to these bays when
        they do not have customers.
        Below are some of the major
        Edinburgh Taxi telephone
        numbers:
        Central Taxis –
        0131 229 2468
        City Cabs –
        0131 228 1211
        Com Cabs –
        0131 272 8000
        Private hire cars (PHC) are not
        permitted to uplift passengers
        on the street – they are
        only permitted to pick up
        passengers through prior
        arrangements.
        Below are some telephone
        numbers of Edinburgh PHC:
        Direct Cabs –
        0131 444 1313
        Festival Cabs –
        0131 552 1777
        Bluebird –
        0131 621 6666
        There are many other taxi and
        private hire company numbers
        that can be found in the
        yellow pages.
        You should always ensure
        that you travel in a licensed
        taxi and PHC by checking
        the vehicle’s signage or
        plate and the driver’s
        badge. You should never
        agree to travel in an
        unlicensed vehicle with
        an unlicensed driver.
        Transport marshals are available
        on Friday and Saturday nights
        between the hours of 10.30pm
        and 4.00am to give you advice
        on the best way home at the
        following taxi stances:
        Leith Street
        High Street (outside Radisson Hotel)
        Lothian Road (outside Sheraton Hotel)
        George Street */
    }

}
