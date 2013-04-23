package demos.droids;

import java.util.ArrayList;

import com.demos.R;

import demos.droids.Droid;
import demos.shared.FloatingDisplay;
import demos.shared.MainGamePanel;

import android.content.Context;
import android.gesture.Gesture;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.widget.Toast;

public class Droidz extends MainGamePanel implements SurfaceHolder.Callback {
	
	//Tag for logging on Android's Log
	private static final String TAG = Droidz.class.getSimpleName();
	private static final int MAX_NUMBER_DROIDS = 30;
	
	private Droid[] droids;
	private int animationSpeedFactor;
	private int currentNumberDroids;
	private int index;
	private FloatingDisplay floatingDisplay;
	private ArrayList<Rect> obstacles;
	private boolean collisionDetection = true; 
	private int finger;
	private GestureDetector gestureDetector;
	private boolean isScrolling = false;

	public Droidz(Context context) {
		super(context);
		
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		//Set context
		appContext = context;
		animationSpeedFactor = 1; 				//Normal speed
		currentNumberDroids = 0;				// Starts out with 0 droids
		index = 0;								// Checks if there is any more droids to create within the maximum allowed
		droids = new Droid[MAX_NUMBER_DROIDS];	//Create droid and load bitmap
		
		//Starts out 1X
		animationSpeedFactor = 1;
		
		obstacles = new ArrayList<Rect>();
		finger = -1;
		
		gestureDetector = new GestureDetector(context, new GestureListener());
		
		setFocusable(true);
	}
	
	private class GestureListener extends GestureDetector.SimpleOnGestureListener {				
		
		// Notified when a tap occurs with the down MotionEvent that triggered it.
		// NECESSARY TO RETURN TRUE TO LET OTHER METHODS FIRE AND CATCH TOUCHES!!
		@Override
		public boolean onDown(MotionEvent e) {			
			return true;
		}
		
		// Notified when a double-tap occurs.
		@Override
		public boolean onDoubleTap(MotionEvent e) {
			addDroid((int)(e.getX()), (int)(e.getY()));
			//makeToast("DoubleTap");
			return false;
		}

		// Notified when an event within a double-tap gesture occurs, 
		// including the down, move, and up events.
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			//makeToast("DoubleTapEvent");
			return false;
		}
		
		//Notified when a single-tap occurs.
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			
			addObstacle((int)(e.getX()), (int)(e.getY()), 100, 100);			 
			
			return false;
		}

		// Notified of a fling event when it occurs with the initial on down 
		// MotionEvent and the matching up MotionEvent.
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			// TODO Auto-generated method stub
			return false;
		}

		// Notified when a long press occurs with the initial on down 
		// MotionEvent that trigged it.
		@Override
		public void onLongPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		// Notified when a scroll occurs with the initial on down 
		// MotionEvent and the current move MotionEvent.
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {
			
			isScrolling = true;
			
			if ( finger == -1 ) {
				 finger = 1;
				 addObstacleAt(finger, (int)(e2.getX()), (int)(e1.getY()), 100, 100);
			} else {
				 obstacles.get(finger).left = (int) (e2.getX() - 50); 
				 obstacles.get(finger).right = obstacles.get(finger).left + 100;
				 obstacles.get(finger).top = (int) (e2.getY() - 50);
				 obstacles.get(finger).bottom = obstacles.get(finger).top + 100;
			 }				
			
			 return false;
		}

		// The user has performed a down MotionEvent and not performed 
		// a move or up yet.
		@Override
		public void onShowPress(MotionEvent e) {
			// TODO Auto-generated method stub
			
		}

		// Notified when a tap occurs with the up MotionEvent that triggered it.
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}
		
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		
		// Add framebox at the very first position
		if ( obstacles.size() == 0 )
			addObstacleAt(0,frameBox);					// Adding the framebox
		
		//Heads up display
		floatingDisplay = new FloatingDisplay(2, "bottomleft", Color.WHITE, getWidth(), getHeight());
		floatingDisplay.addParam("Speed", animationSpeedFactor);
		floatingDisplay.addParam("Droids", currentNumberDroids);
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		
		if ( gestureDetector.onTouchEvent(event) )
			return true;
		
		if ( event.getAction() == MotionEvent.ACTION_UP ) {
			if ( isScrolling ) {
				
				// Handle when scroll finished
				isScrolling = false;							
				obstacles.get(finger).setEmpty();
			}
		}
		return false;
		/*
		 if (event.getAction() == MotionEvent.ACTION_DOWN) {			 								 
			 
			 //delegating event handling to the droid
			 if ( droid != null ) {
				 droid.handleActionDown((int)event.getX(), (int)event.getY());
			 }
			 
			 
		 } if (event.getAction() == MotionEvent.ACTION_MOVE ) {
			 			 
			 //the gestures
			 
			 if( droid!= null) {
				 if (droid.isTouched()) {
					 //the droid was picked up and is being dragged
					 droid.setX((int)event.getX());
					 droid.setY((int)event.getY());
				 }
			 }
			 
			 
		 } if (event.getAction() == MotionEvent.ACTION_UP ){
			 
			 if( droid!= null) {
				 //touch was released
				 if (droid.isTouched()) {
					 droid.setTouched(false);
				 }
			 }			 
		 }
		 
	  return true;
	  */
	 }
	
	public int addObstacle(Rect obstacle) {
		obstacles.add(obstacle);
		return obstacles.indexOf(obstacle);
	}
	
	public void addObstacleAt(int index, Rect obstacle) {
		obstacles.add(index, obstacle);		
	}
	
	public int addObstacle(int x, int y, int width, int height) {
		 int left = (int) (x - (width / 2.0));
		 int right = left + width;
		 int top = (int) (y - (height / 2.0));
		 int bottom = top + height;
		 Rect obstacle = new Rect(left, top, right, bottom);
		obstacles.add(obstacle);
		return obstacles.indexOf(obstacle);
	}
	
	public void addObstacleAt(int index, int x, int y, int width, int height) {
		 int left = (int) (x - (width / 2.0));
		 int right = left + width;
		 int top = (int) (y - (height / 2.0));
		 int bottom = top + height;
		 Rect obstacle = new Rect(left, top, right, bottom);
		obstacles.add(index, obstacle);
	}
	
	public void removeObstacle(Rect obstacle) {
		obstacles.remove(obstacle);
	}
	
	public void render(Canvas canvas) {
		
		 //fills the canvas with black 
		canvas.drawColor(Color.BLACK);
		drawObstacles(canvas);
		drawDroids(canvas);		
		
		// Display Heads Up Information
		//displayFps(canvas, avgFps);
		if ( !floatingFPS.display(canvas) )
			makeToast("Error. There was a problem displaying FPS");
		if ( !floatingDisplay.display(canvas) )
			makeToast("Error. There was a problem with floating display");
	}
	
	public void drawObstacles(Canvas canvas) {
		
		// Draw the stage as a containing box
		 Paint paint = new Paint();
		 paint.setColor(Color.GREEN);
		 paint.setStyle(Paint.Style.STROKE);
		 canvas.drawRect(obstacles.get(0), paint);
		 
		 int start = 1;
		 
		 if ( finger == 1 ) {
			 // Draw the finger with a different color
			 paint.setColor(Color.YELLOW);
			 canvas.drawRect(obstacles.get(1), paint);
			 
			 start = 2;
		 }
		
		 // Draw the rest of the obstacles
		 paint.setColor(Color.RED);
		 
		// Drawing all droids in the array
		 for ( int i = start; i < obstacles.size(); i++ ) {
			 canvas.drawRect(obstacles.get(i), paint);
		 }
	}
	 
	public void drawDroids(Canvas canvas) {
		 
		// Drawing all droids in the array
		 for ( int i = 0; i < currentNumberDroids; i++ ) {
			 if (droids[i] != null)
				 droids[i].draw(canvas);
		 }
	 }
	 
	public void updateDroids(){
		
		if ( collisionDetection == true ) {
			
			// Updating all droids
			 for ( int i = 0; i < currentNumberDroids; i++ ) {
				 if (droids[i] != null)
					 droids[i].update(obstacles, animationSpeedFactor);
			 }
			
		} else {		 
			// Updating all droids
			 for ( int i = 0; i < currentNumberDroids; i++ ) {
				 if (droids[i] != null)
					 droids[i].update(frameBox, animationSpeedFactor);
			 }
		}
		 
	 }
	 
	public void update() {
		 
		 updateDroids();
	 }
	 
	public int getAnimationSpeedFactor() {
		return animationSpeedFactor;
	}

	public void setAnimationSpeedFactor(int animationSpeedFactor) {
		this.animationSpeedFactor = animationSpeedFactor;
		if ( !floatingDisplay.updateParam("Speed", animationSpeedFactor))
			makeToast("Param SPEED couldn't be found");
	}

	public void multiplyAnimationSpeed() {
		 //Multiply by speed factor all droids speeds
		 for ( int i = 0; i < currentNumberDroids; i++ ) {
			 if (droids[i] != null){		
				 droids[i].multiplySpeed(animationSpeedFactor);
			 }
		 }
	 }
	
	public int getCurrentNumberDroids() {
		return currentNumberDroids;
	}
	
	public void addDroid(int x, int y) {
		
		if ( currentNumberDroids < MAX_NUMBER_DROIDS ) {
			if ( index < MAX_NUMBER_DROIDS ) {
				droids[index] = new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1),x, y );
				//droids[index] = new Droid(BitmapFactory.decodeResource(getResources(), R.drawable.droid_1),x, y, 3 );
				currentNumberDroids++;
				index++;
				
				//Update the floating display				
				if ( !floatingDisplay.updateParam("Droids", currentNumberDroids))
					makeToast("Param DROIDS couldn't be found");		
				
			} else {				
				droids[currentNumberDroids].setX(x);
				droids[currentNumberDroids].setY(y);
				currentNumberDroids++;
				
				//Update the floating display
				if ( !floatingDisplay.updateParam("Droids", currentNumberDroids))
					makeToast("Param DROIDS couldn't be found");
			}
		} else 
			makeToast("Max Number Of Droids Reached");
	}
	
	public void setCurrentNumberDroids(int newNumberDroids) {
			
		// Add Droids
		if ( newNumberDroids > currentNumberDroids ) {
			// Add missing droids
			int missingDroids = newNumberDroids - currentNumberDroids;
			for ( int i = 0; i < missingDroids; i++ ) {
				addDroid(rndInt(frameBox.left, frameBox.right), rndInt(frameBox.top, frameBox.bottom)); 
			}
			
			//Delete droids
		} else if ( newNumberDroids < currentNumberDroids ) {
			
			// Possible to delete droids
			if ( newNumberDroids >= 0 ) {
				currentNumberDroids = newNumberDroids;
				
				//Update the floating display
				if ( !floatingDisplay.updateParam("Droids", currentNumberDroids))
					makeToast("Param DROIDS couldn't be found");
				
			} else {
				//Impossible to delete droids
				makeToast("No More Droids To Delete");
			}
		}
	}
	
	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}
}
