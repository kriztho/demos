package demos.animatedElaine;

import java.util.ArrayList;

import com.demos.R;

import demos.shared.Background;
import demos.shared.FloatingDisplay;
import demos.shared.MainGamePanel;
import demos.shared.OnScreenController;

import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class AnimatedElaine extends MainGamePanel implements 
SurfaceHolder.Callback {

	// For multitouch events
	private static final int INVALID_POINTER_ID = -1;	
	private int firstPointerId = INVALID_POINTER_ID;
	private int secondPointerId = INVALID_POINTER_ID;

	//Tag for logging on Android's Log
	private static final String TAG = AnimatedElaine.class.getSimpleName();

	// Characters
	private ElaineAnimated elaine;

	// Helping objects
	private FloatingDisplay floatingDisplay;
	private GestureDetector gestureDetector;
	private boolean isScrolling = false;

	// Controllers
	private OnScreenController dPad;
	private OnScreenController actionButtons;
	private OnScreenController jStick;

	// Background
	private Background currentBackground;
	private boolean collisionDetection = true;


	public AnimatedElaine(Context context) {
		super(context);

		getHolder().addCallback(this);

		// This class is used for a single finger and allows to tap&hold and doubletapping
		//gestureDetector = new GestureDetector(context, new GestureListener());

		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);

		// Character
		elaine = new ElaineAnimated(BitmapFactory.decodeResource( getResources(), R.drawable.walk_elaine), getWidth()/2, getHeight()/2, 5, 5);

		// Framebox delimits the videogame screen-area
		frameBox = new Rect(0, 0, getWidth(), getHeight());
		currentBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.rpg_background), frameBox);

		// Controllers
		dPad = new OnScreenController("button", 4, 1, frameBox, 3, 150);
		actionButtons = new OnScreenController("button", 2, 0, frameBox, 4, 150);
		jStick = new OnScreenController("stick", 1, 0, frameBox, 4, 150);
		
		floatingDisplay = new FloatingDisplay(2, "topleft", Color.WHITE, getWidth(), getHeight());
		floatingDisplay.addParam("xStick", 0.0f);
		floatingDisplay.addParam("yStick", 0.0f);
		floatingDisplay.addParam("xElaine", 0.0f);
		floatingDisplay.addParam("yElaine", 0.0f);
		floatingDisplay.addParam("theta", 0.0f);
		floatingDisplay.addParam("direction", "");
	}

	private class GestureListener extends GestureDetector.SimpleOnGestureListener {

		// Notified when a tap occurs with the down MotionEvent that triggered it.
		// NECESSARY TO RETURN TRUE TO LET OTHER METHODS FIRE AND CATCH TOUCHES!!
		@Override
		public boolean onDown(MotionEvent e) {						

			if ( e.getPointerCount() == 1 ) {

				//Calculate if a button was pressed
				int dPadButtonTouched = dPad.isTouching((int)(e.getX()), (int)(e.getY()));
				int actionButtonTouched = actionButtons.isTouching((int)(e.getX()), (int)(e.getY()));

				if ( dPadButtonTouched != -1 && actionButtonTouched == -1 )

					switch( dPadButtonTouched ) {
					case 0:
						elaine.movePad("left");
						break;
					case 1:
						elaine.movePad("up");
						break;
					case 2:
						elaine.movePad("down");
						break;
					case 3:
						elaine.movePad("right");
						break;
					default:
						elaine.stop();
						break;
					} else if ( dPadButtonTouched == -1 && actionButtonTouched != -1 ) {

					}

			} else if ( e.getPointerCount() == 2 ) {
				//Calculate if a button was pressed
				int dPadButtonTouched = dPad.isTouching((int)(e.getX()), (int)(e.getY()));
				int actionButtonTouched = actionButtons.isTouching((int)(e.getX()), (int)(e.getY()));

				// If the running button is punched plus the direction pad
				if ( actionButtonTouched == 0 ) {

					elaine.setRunning(true);

					switch( dPadButtonTouched ) {
					case 0:
						elaine.movePad("left");
						break;
					case 1:
						elaine.movePad("up");
						break;
					case 2:
						elaine.movePad("down");
						break;
					case 3:
						elaine.movePad("right");
						break;
					default:
						elaine.stop();
						break;
					}
				} else {
					switch( dPadButtonTouched ) {
					case 0:
						elaine.movePad("left");
						break;
					case 1:
						elaine.movePad("up");
						break;
					case 2:
						elaine.movePad("down");
						break;
					case 3:
						elaine.movePad("right");
						break;
					default:
						elaine.stop();
						break;
					}
				}
			}

			return true;
		}

		// Notified when a double-tap occurs.
		@Override
		public boolean onDoubleTap(MotionEvent e) {			
			return false;
		}

		// Notified when an event within a double-tap gesture occurs, 
		// including the down, move, and up events.
		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

		//Notified when a single-tap occurs.
		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {	
			return false;
		}

		// Notified of a fling event when it occurs with the initial on down 
		// MotionEvent and the matching up MotionEvent.
		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}

		// Notified when a long press occurs with the initial on down 
		// MotionEvent that trigged it.
		@Override
		public void onLongPress(MotionEvent e) {	
		}

		// Notified when a scroll occurs with the initial on down 
		// MotionEvent and the current move MotionEvent.
		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX,
				float distanceY) {

			isScrolling = true;

			int buttonTouched1 = dPad.isTouching((int)(e1.getX()), (int)(e1.getY()));
			int buttonTouched2 = dPad.isTouching((int)(e2.getX()), (int)(e2.getY()));
			switch( buttonTouched2 ) {
			case 0:
				elaine.movePad("left");
				if ( buttonTouched1 != buttonTouched2 && buttonTouched1 != -1 )
					dPad.release(buttonTouched1);
				break;
			case 1:
				elaine.movePad("up");
				if ( buttonTouched1 != buttonTouched2 && buttonTouched1 != -1 )
					dPad.release(buttonTouched1);
				break;
			case 2:
				elaine.movePad("down");
				if ( buttonTouched1 != buttonTouched2 && buttonTouched1 != -1 )
					dPad.release(buttonTouched1);
				break;
			case 3:
				elaine.movePad("right");
				if ( buttonTouched1 != buttonTouched2 && buttonTouched1 != -1 )
					dPad.release(buttonTouched1);
				break;
			default:
				elaine.stop();
				break;
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
	public boolean onTouchEvent(MotionEvent e) {

		//int count = e.getPointerCount();
		//Log.d(TAG, "OnTouch Taps: " + count);

		float x, y;							// integer version of finger coordinates		
		int dPadButtonTouchedIndex = -1;	// index of the dPad button touched if touched. Returns -1 otherwise
		int actionButtonTouchedIndex = -1;	// index of the action button touched if touched. Returns -1 otherwise
		int jStickTouchedIndex = -1;		// index of the jStick touched if touched. Returns -1 otherwise

		// This part implements multitouch finger recognition depending on the pointers
		final int action = e.getAction();		
		switch( action & MotionEvent.ACTION_MASK ) {	// ACTION_MASK is used for identifying single o multiple touches

		// One finger touched down the screen
		case  MotionEvent.ACTION_DOWN:{

			//Calculate if a button was pressed and return the index if touched
			dPadButtonTouchedIndex = dPad.isTouching((int)(e.getX()), (int)(e.getY()));
			actionButtonTouchedIndex = actionButtons.isTouching((int)(e.getX()), (int)(e.getY()));
			jStickTouchedIndex = jStick.isTouchingNoChange((int)(e.getX()), (int)(e.getY()));

			// Catches the first finger to touch and stores it as the firstPointerId
			firstPointerId = e.getPointerId(0);																		

			// Determines the action to one finger touched the dPad
			if ( dPadButtonTouchedIndex != -1 ) {
				switch( dPadButtonTouchedIndex ) {
				case 0:
					elaine.movePad("left");
					break;
				case 1:
					elaine.movePad("up");
					break;
				case 2:
					elaine.movePad("down");
					break;
				case 3:
					elaine.movePad("right");
					break;
				default:
					elaine.stop();
					break;
				}
			}

			// Activates the corresponding action button effect. Currently only one button is considered.
			if ( actionButtonTouchedIndex != -1 )
				elaine.setRunning(true);

			// Activates the corresponding joystick if touched
			if ( jStickTouchedIndex != -1 ) {
				jStick.touch(jStickTouchedIndex);
			}

			break;
		}

		// Executed when a second finger touches the screen simultaneously 
		case MotionEvent.ACTION_POINTER_DOWN: {

			if ( e.getPointerCount() < 2 ) {

				// Find the pointer that triggered the action
				// Extract the index of the pointer that left the touch sensor
				x = e.getX(firstPointerId);
				y = e.getY(firstPointerId);
				dPadButtonTouchedIndex = dPad.isTouching((int)x, (int)y);
				actionButtonTouchedIndex = actionButtons.isTouching((int)x, (int)y);

				// Find if the new pointer pressed a button too
				final int pointerIndexNew = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
						>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
			secondPointerId = e.getPointerId(pointerIndexNew);
			x = e.getX(secondPointerId);
			y = e.getY(secondPointerId);
			if ( dPadButtonTouchedIndex == -1 )
				dPadButtonTouchedIndex = dPad.isTouching((int)x, (int)y);

			if ( actionButtonTouchedIndex == -1 )
				actionButtonTouchedIndex = actionButtons.isTouching((int)x, (int)y);					

			// If the running button is punched plus the direction pad
			if ( dPadButtonTouchedIndex != -1 && actionButtonTouchedIndex == 0 ) {

				elaine.setRunning(true);

				switch( dPadButtonTouchedIndex ) {
				case 0:
					elaine.movePad("left");
					break;
				case 1:
					elaine.movePad("up");
					break;
				case 2:
					elaine.movePad("down");
					break;
				case 3:
					elaine.movePad("right");
					break;
				default:
					elaine.stop();
					break;
				}
			} else if ( dPadButtonTouchedIndex != -1 ){

				switch( dPadButtonTouchedIndex ) {
				case 0:
					elaine.movePad("left");
					break;
				case 1:
					elaine.movePad("up");
					break;
				case 2:
					elaine.movePad("down");
					break;
				case 3:
					elaine.movePad("right");
					break;
				default:
					elaine.stop();
					break;
				}
			}
			break;
			}
		}

		// When a single-touching-finger stops touching the screen
		case MotionEvent.ACTION_UP: {

			// Check for the touched buttons to be released
			x = e.getX();
			y = e.getY();				
			dPadButtonTouchedIndex = dPad.isTouching((int)x, (int)y);
			actionButtonTouchedIndex = actionButtons.isTouching((int)x, (int)y);
			jStickTouchedIndex = jStick.isTouchingNoChange((int)x, (int)y);

			// Release dPad button
			if ( dPadButtonTouchedIndex != -1 ) {
				dPad.release(dPadButtonTouchedIndex);
				elaine.stop();
			}

			// Release running button
			if ( actionButtonTouchedIndex != -1 ) {
				actionButtons.release(actionButtonTouchedIndex);
				elaine.setRunning(false);	// Stop running
			}

			// Release joystick
			if ( jStickTouchedIndex != -1) {
				jStick.release(jStickTouchedIndex);
				jStick.reset(jStickTouchedIndex);
				elaine.stop();
			}

			break;
		}

		// When one of the simultaneous touches is raised
		case MotionEvent.ACTION_POINTER_UP: {
			
			if ( e.getPointerCount() < 2 ) {

				// Find the pointer that left
				int pointerIndexNew = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
					>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
				int pointerId = e.getPointerId(pointerIndexNew);
				x = e.getX(pointerId);
				y = e.getY(pointerId);
				dPadButtonTouchedIndex = dPad.isTouching((int)x, (int)y);
				actionButtonTouchedIndex = actionButtons.isTouching((int)x, (int)y);
				jStickTouchedIndex = jStick.isTouching((int)x, (int)y);

				// The object that is pointed by the one that left is the one to be released
				if ( dPadButtonTouchedIndex != -1 ) {
					dPad.release(dPadButtonTouchedIndex);
					elaine.stop();
				}

				// Release accordingly
				if ( actionButtonTouchedIndex != -1 ) {
					actionButtons.release(actionButtonTouchedIndex);
					elaine.setRunning(false);
				}
				
				// Release accordingly
				if ( jStickTouchedIndex != -1 ) {
					jStick.release(jStickTouchedIndex);
					jStick.reset(jStickTouchedIndex);
					elaine.stop();
				}

				break;
			}
		}

		// When the finger drags around
		case MotionEvent.ACTION_MOVE: {			

			// All fingers/touches have to be updated accordingly
			for ( int i = 0; i < e.getPointerCount(); i++ ) {

				// HACK. To only update one control at a time. Otherwise they are both updated at the same time
				x = e.getX(i);
				y = e.getY(i);
				int side = checkScreenSide((int)x, (int)y);

				switch (side) {

				// Left half of the screen
				case 1:{		

					// Check for touches on controls
					dPadButtonTouchedIndex = dPad.isTouching((int)x, (int)y);
					actionButtonTouchedIndex = actionButtons.isTouchingNoChange((int)x, (int)y);

					// TODO. Active when reset mode is off, but still buggy
					//jStickTouched = jStick.isTouchingOnly((int)x, (int)y);
					jStickTouchedIndex = jStick.isTouching((int)x, (int)y);	

					switch( dPadButtonTouchedIndex ) {
					case 0:
						elaine.movePad("left");
						break;
					case 1:
						elaine.movePad("up");
						break;
					case 2:
						elaine.movePad("down");
						break;
					case 3:
						elaine.movePad("right");
						break;
					default:
						elaine.stop();
						break;
					}

					if ( jStickTouchedIndex != -1 ) {
						jStick.drag(jStickTouchedIndex, (int)x,(int)y);
						elaine.moveStick(jStick.getSpeed(jStickTouchedIndex));
					}

					break;
				}
				
				// Right half of the screen
				case 2:{		

					// Check for touches on controls
					dPadButtonTouchedIndex = dPad.isTouchingNoChange((int)x, (int)y);
					actionButtonTouchedIndex = actionButtons.isTouching((int)x, (int)y);

					if ( actionButtonTouchedIndex == -1 )
						elaine.setRunning(false);
					else 
						elaine.setRunning(true);

					break;
				}
				default:
					break;
				}
			}

			break;
		}		
		}
		return true;
	}

	public int checkScreenSide(int x, int y) {

		int side = -1;
		if ( x < getWidth() / 2 )
			side = 1;
		else
			side = 2;

		return side;
	}

	public void render(Canvas canvas) {
		
		//fills the canvas with black 
		canvas.drawColor(Color.BLACK);
		
		// Renders the background bitmap
		currentBackground.render(canvas);

		// Draw the stage as a containing box
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(frameBox, paint);

		//Drawing Elaine
		elaine.draw(canvas);

		//Drawing on screen controllers
		//dPad.render(canvas);
		actionButtons.render(canvas);
		jStick.render(canvas);

		//displayFps(canvas, avgFps);
		if ( !floatingFPS.display(canvas) )
			makeToast("Error. There was a problem displaying FPS");
		
		floatingDisplay.updateParam("xStick", jStick.getSpeed(0).getXv());
		floatingDisplay.updateParam("yStick", jStick.getSpeed(0).getYv());
		floatingDisplay.updateParam("xElaine", elaine.getSpeed().getXv());
		floatingDisplay.updateParam("yElaine", elaine.getSpeed().getYv());
		floatingDisplay.updateParam("theta", jStick.getSticks()[0].getTheta());
		floatingDisplay.updateParam("direction", jStick.getSticks()[0].getDirection());
		
		if ( !floatingDisplay.display(canvas)){
			makeToast("Error. There was a problem with display");
		}

	}

	public void updateElaine(){

		//Updating Elaine accordingly
		if ( collisionDetection ) {		 			 
			elaine.update(System.currentTimeMillis(), currentBackground.getObstacles());

		} else {			 
			elaine.update(System.currentTimeMillis(), frameBox);			 
		}
	}

	// Wrapper
	public void update() {

		updateElaine();
	}
}
