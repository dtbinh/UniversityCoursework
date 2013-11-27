package com.assignment.practical;

import android.app.Activity;
import android.os.Bundle;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.View;


/*
 * Code adapted from http://www.anddev.org/large_image_scrolling_using_low_level_touch_events-t11182.html
 */

public class HotelMap extends Activity {


	private static int directions;

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Bundle extras = getIntent().getExtras();
		directions = extras.getInt("Directions");
		setContentView(new SampleView(this));
	}

	private static class SampleView extends View {
		private static int viewWidth = 0; //view width, changes dynamically
		private static int viewHeight = 0; //view height, changes dynamically
		private static Bitmap bmLargeImage; // bitmap large enough to be scrolled
		private static Rect displayRect = null; // rect we display to
		private Rect scrollRect = null; // rect we scroll over our bitmap with
		private int scrollRectX = 0; // current left location of scroll rect
		private int scrollRectY = 0; // current top location of scroll rect
		private float scrollByX = 0; // x amount to scroll by
		private float scrollByY = 0; // y amount to scroll by
		private float startX = 0; // track x from one ACTION_MOVE to the next
		private float startY = 0; // track y from one ACTION_MOVE to the next

		public SampleView(Context context) {
			super(context);

			// Load a large bitmap into an offscreen area of memory.
			switch(directions) {
			case R.array.advocatesapartments:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.advocates);
				break;
			case R.array.apexcityhotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.apexcity);
				break;
			case R.array.apexinternational:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.apexinternational);
				break;
	        case R.array.balmoral:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.balmoral);
				break;
	        case R.array.bankhotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.bank);
				break;
	        case R.array.barceloedinburghcarlton:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.barcelo);
				break;
	        case R.array.bestwestern:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.bestwestern);
				break;
	        case R.array.brodieshostels:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.brodies);
				break;
	        case R.array.castleapartments:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.castleapart);
				break;
	        case R.array.counanhotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.counan);
				break;
	        case R.array.eurohostel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.eurohostel);
				break;
	        case R.array.frasersuites:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.fraser);
				break;
	        case R.array.grassmarkethotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.grassmarket);
				break;
	        case R.array.holidayinnexpressroyalmile:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.holidayinn);
				break;
	        case R.array.hotelduvin:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.duvin);
				break;
	        case R.array.hotelmissoni:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.missoni);
				break;
	        case R.array.ibisedinburgh:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.ibis);
				break;
	        case R.array.jurysinnedinburgh:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.jurys);
				break;
	        case R.array.kennethmackenzie:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.kenneth);
				break;
	        case R.array.mercurepointhotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.mercure);
				break;
	        case R.array.nineteenmeadowsplace:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.nineteenmeadow);
				break;
	        case R.array.novotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.novotel);
				break;
	        case R.array.premierinn:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.premierinn);
				break;
	        case R.array.radissonblu:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.radissonblu);
				break;
	        case R.array.richmondplace:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.richmond);
				break;
	        case R.array.royalmileapartments:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.royalmileapart);
				break;
	        case R.array.salisburyhotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.salisbury);
				break;
	        case R.array.smartcityhostels:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.smartcityhostels);
				break;
	        case R.array.tenhillplace:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.tenhill);
				break;
	        case R.array.themeadowshotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.meadowshotel);
				break;
	        case R.array.thescotsmanhotel:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.scotsman);
				break;
	        case R.array.thewitcherybythecastle:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.innersanctum);
				break;
	        case R.array.travelodgecentralhotel:
	        	bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.travelodge);
				break;
			default:
				bmLargeImage = BitmapFactory.decodeResource(getResources(), R.drawable.largemap);
				break;
			}
		}

		// Our view dimensions change depending on, among other things, screen
		// orientation. onSizeChanged() is a notification that such a change has 
		// occurred. For our purposes, we can use the newly changed values to set up
		// our scroll and display rectangles. This is how we handle a user switch
		// between portrait and landscape modes, or any other type of 'view has 
		// changed' operation.
		@Override
		protected void onSizeChanged(int w, int h, int oldw, int oldh) {
			
			// Cache our new dimensions; we'll need them for drawing.
			viewWidth = w;
			viewHeight = h;
			
			// Destination rect for our main canvas draw.
			displayRect = new Rect(0, 0, viewWidth, viewHeight);
			// Scroll rect: this will be used to 'scroll around' over the
			// bitmap in memory. Initialize as above.
			scrollRect = new Rect(0, 0, viewWidth, viewHeight);

			super.onSizeChanged(w, h, oldw, oldh);
		}

		@Override
		public boolean onTouchEvent(MotionEvent event) {

			switch (event.getAction()) {
			case MotionEvent.ACTION_DOWN:
				// Remember our initial down event location.
				startX = event.getRawX();
				startY = event.getRawY();
				break;

			case MotionEvent.ACTION_MOVE:
				float x = event.getRawX();
				float y = event.getRawY();
				// Calculate move update. This will happen many times
				// during the course of a single movement gesture.
				scrollByX = x - startX; // move update x increment
				scrollByY = y - startY; // move update y increment
				startX = x; // reset initial values to latest
				startY = y;
				invalidate(); // force a redraw
				break;
			}
			return true; // done with this event so consume it
		}

		@Override
		protected void onDraw(Canvas canvas) {
			
			// Our move updates are calculated in ACTION_MOVE in the opposite direction
			// from how we want to move the scroll rect. Think of this as dragging to
			// the left being the same as sliding the scroll rect to the right.
			int newScrollRectX = scrollRectX - (int) scrollByX;
			int newScrollRectY = scrollRectY - (int) scrollByY;

			// Don't scroll off the left or right edges of the bitmap.
			if (newScrollRectX < 0)
				newScrollRectX = 0;
			else if (newScrollRectX > (bmLargeImage.getWidth() - viewWidth))
				newScrollRectX = (bmLargeImage.getWidth() - viewWidth);

			// Don't scroll off the top or bottom edges of the bitmap.
			if (newScrollRectY < 0)
				newScrollRectY = 0;
			else if (newScrollRectY > (bmLargeImage.getHeight() - viewHeight))
				newScrollRectY = (bmLargeImage.getHeight() - viewHeight);

			// We have our updated scroll rect coordinates, set them and draw.
			scrollRect.set(newScrollRectX, newScrollRectY, 
					newScrollRectX + viewWidth, newScrollRectY + viewHeight);
			Paint paint = new Paint();
			canvas.drawBitmap(bmLargeImage, scrollRect, displayRect, paint);

			// Reset current scroll coordinates to reflect the latest updates,
			// so we can repeat this update process.
			scrollRectX = newScrollRectX;
			scrollRectY = newScrollRectY;
		}
	}
}
