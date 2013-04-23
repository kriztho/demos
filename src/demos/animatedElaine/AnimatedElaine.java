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
	
	private static final int INVALID_POINTER_ID = -1;
		
	//Tag for logging on Android's Log
	private static final String TAG = AnimatedElaine.class.getSimpleName();
	private ElaineAnimated elaine;
	private FloatingDisplay floatingDisplay;
	private GestureDetector gestureDetector;
	private boolean isScrolling = false;
	private OnScreenController dPad;
	private OnScreenController actionButtons;
	private Background currentBackground;
	private int firstPointerId = INVALID_POINTER_ID;
	private int secondPointerId = INVALID_POINTER_ID;
	private boolean collisionDetection = true;

	public AnimatedElaine(Context context) {
		super(context);
		
		getHolder().addCallback(this);
				
		//gestureDetector = new GestureDetector(context, new GestureListener());
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		
		elaine = new ElaineAnimated(BitmapFactory.decodeResource( getResources(), R.drawable.walk_elaine), getWidth()/2, getHeight()/2, 5, 5);
		frameBox = new Rect(0, 0, getWidth(), getHeight());
		dPad = new OnScreenController(4, 1, frameBox, 'm', 150);
		actionButtons = new OnScreenController(2, 0, frameBox, 'l', 150);
		
		currentBackground = new Background(BitmapFactory.decodeResource(getResources(), R.drawable.rpg_background), frameBox, createObstacles());
	}
	
	public ArrayList<Rect> createObstacles() {
		
		ArrayList<Rect> obstacles = new ArrayList<Rect>();
		
		// FrameBox
		obstacles.add(frameBox);
		
		// Trees
		int twidth = 34;
		int theight = 26;
		obstacles.add(new Rect(64, 0, 64 + twidth, 0 + theight));
		obstacles.add(new Rect(114, 37, 114 + twidth, 37 + theight));
		obstacles.add(new Rect(177, 35, 177 + twidth, 35 + theight));
		obstacles.add(new Rect(196, 103, 196 + twidth, 103 + theight));
		obstacles.add(new Rect(161, 134, 161 + twidth, 134 + theight));		
		obstacles.add(new Rect(127, 197, 127 + twidth, 197 + theight));		
		obstacles.add(new Rect(33, 197, 33 + twidth, 197 + theight));
		obstacles.add(new Rect(1, 261, 1 + twidth, 261 + theight));
		
		// Water
		obstacles.add(new Rect(125, 219, 125 + 261, 219 + 39));
		obstacles.add(new Rect(59, 251, 59 + 421, 251 + 37));
		obstacles.add(new Rect(28, 282, 28 + 452, 282 + 38));
		
		// Hills
		obstacles.add(new Rect(319, 0, 319 + 161, 222));
		obstacles.add(new Rect(0, 0, 31, 33));
		obstacles.add(new Rect(0, 32, 64, 32 + 94));
		
		return obstacles;
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
							elaine.move("left");
							break;
						case 1:
							elaine.move("up");
							break;
						case 2:
							elaine.move("down");
							break;
						case 3:
							elaine.move("right");
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
							elaine.move("left");
							break;
						case 1:
							elaine.move("up");
							break;
						case 2:
							elaine.move("down");
							break;
						case 3:
							elaine.move("right");
							break;
						default:
							elaine.stop();
							break;
					}
				} else {
					switch( dPadButtonTouched ) {
					case 0:
						elaine.move("left");
						break;
					case 1:
						elaine.move("up");
						break;
					case 2:
						elaine.move("down");
						break;
					case 3:
						elaine.move("right");
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
				elaine.move("left");
				if ( buttonTouched1 != buttonTouched2 && buttonTouched1 != -1 )
					dPad.release(buttonTouched1);
				break;
			case 1:
				elaine.move("up");
				if ( buttonTouched1 != buttonTouched2 && buttonTouched1 != -1 )
					dPad.release(buttonTouched1);
				break;
			case 2:
				elaine.move("down");
				if ( buttonTouched1 != buttonTouched2 && buttonTouched1 != -1 )
					dPad.release(buttonTouched1);
				break;
			case 3:
				elaine.move("right");
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
		 
		float x, y;		 		
		int dPadButtonTouched = -1;
		int actionButtonTouched = -1;
		
		final int action = e.getAction();		
		switch( action & MotionEvent.ACTION_MASK ) {
		 
			case  MotionEvent.ACTION_DOWN:{
				
				//Calculate if a button was pressed
				dPadButtonTouched = dPad.isTouching((int)(e.getX()), (int)(e.getY()));
				actionButtonTouched = actionButtons.isTouching((int)(e.getX()), (int)(e.getY()));
				
				firstPointerId = e.getPointerId(0);																		
				
				if ( dPadButtonTouched != -1 ) {
					switch( dPadButtonTouched ) {
						case 0:
							elaine.move("left");
							break;
						case 1:
							elaine.move("up");
							break;
						case 2:
							elaine.move("down");
							break;
						case 3:
							elaine.move("right");
							break;
						default:
							elaine.stop();
							break;
					}
				}
				
				if ( actionButtonTouched != -1 )
					elaine.setRunning(true);
				
				break;
			}
			
			case MotionEvent.ACTION_POINTER_DOWN: {
				
				// Find the pointer that triggered the action
				// Extract the index of the pointer that left the touch sensor
				x = e.getX(firstPointerId);
				y = e.getY(firstPointerId);
				dPadButtonTouched = dPad.isTouching((int)x, (int)y);
				actionButtonTouched = actionButtons.isTouching((int)x, (int)y);
												
				// Find if the new pointer pressed a button too
				final int pointerIndexNew = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
		                >> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		        secondPointerId = e.getPointerId(pointerIndexNew);
				x = e.getX(secondPointerId);
				y = e.getY(secondPointerId);
				if ( dPadButtonTouched == -1 )
					dPadButtonTouched = dPad.isTouching((int)x, (int)y);
				
				if ( actionButtonTouched == -1 )
					actionButtonTouched = actionButtons.isTouching((int)x, (int)y);					
				
					// If the running button is punched plus the direction pad
					if ( dPadButtonTouched != -1 && actionButtonTouched == 0 ) {
						
						elaine.setRunning(true);
						
						switch( dPadButtonTouched ) {
							case 0:
								elaine.move("left");
								break;
							case 1:
								elaine.move("up");
								break;
							case 2:
								elaine.move("down");
								break;
							case 3:
								elaine.move("right");
								break;
							default:
								elaine.stop();
								break;
						}
					} else if ( dPadButtonTouched != -1 ){
						
						switch( dPadButtonTouched ) {
						case 0:
							elaine.move("left");
							break;
						case 1:
							elaine.move("up");
							break;
						case 2:
							elaine.move("down");
							break;
						case 3:
							elaine.move("right");
							break;
						default:
							elaine.stop();
							break;
						}
					}
				break;
			}
			
			case MotionEvent.ACTION_UP: {
				
				x = e.getX();
				y = e.getY();				
				dPadButtonTouched = dPad.isTouching((int)x, (int)y);
				actionButtonTouched = actionButtons.isTouching((int)x, (int)y);
				 
				// Released walking button
				if ( dPadButtonTouched != -1 ) {
					dPad.release(dPadButtonTouched);
					elaine.stop();
				}
				
				// Running button is not being pressed
				if ( actionButtonTouched != -1 ) {
					actionButtons.release(actionButtonTouched);
					elaine.setRunning(false);	// Stop running
				}
				break;
			}
			
			case MotionEvent.ACTION_POINTER_UP: {
				
				// Find the pointer that left
				int pointerIndexNew = (action & MotionEvent.ACTION_POINTER_INDEX_MASK) 
						>> MotionEvent.ACTION_POINTER_INDEX_SHIFT;
		        int pointerId = e.getPointerId(pointerIndexNew);
				x = e.getX(pointerId);
				y = e.getY(pointerId);
				dPadButtonTouched = dPad.isTouching((int)x, (int)y);
				actionButtonTouched = actionButtons.isTouching((int)x, (int)y);
				
				// The object that is pointed by the one that left is the one to be released
				if ( dPadButtonTouched != -1 ) {
					dPad.release(dPadButtonTouched);
					elaine.stop();
				}
				
				if ( actionButtonTouched != -1 ) {
					actionButtons.release(actionButtonTouched);
					elaine.setRunning(false);
				}							
				
				break;
			}
			
			case MotionEvent.ACTION_MOVE: {			
				
				for ( int i = 0; i < e.getPointerCount(); i++ ) {
					
					x = e.getX(i);
					y = e.getY(i);
					int side = checkScreenSide((int)x, (int)y);
					
					switch (side) {
											
						case 1:{		// Left half of the screen
							
							dPadButtonTouched = dPad.isTouching((int)x, (int)y);
							actionButtonTouched = actionButtons.isTouchingNoChange((int)x, (int)y);
							assert actionButtonTouched == -1;							
							
							switch( dPadButtonTouched ) {
							case 0:
								elaine.move("left");
								break;
							case 1:
								elaine.move("up");
								break;
							case 2:
								elaine.move("down");
								break;
							case 3:
								elaine.move("right");
								break;
							default:
								elaine.stop();
								break;
							}
							
							break;
						}
						case 2:{		// Right half of the screen
							
							dPadButtonTouched = dPad.isTouchingNoChange((int)x, (int)y);
							actionButtonTouched = actionButtons.isTouching((int)x, (int)y);
							assert dPadButtonTouched == -1;
							
							if ( actionButtonTouched == -1 )
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
		currentBackground.render(canvas);
		
		// Draw the stage as a containing box
		Paint paint = new Paint();
		paint.setColor(Color.GREEN);
		paint.setStyle(Paint.Style.STROKE);
		canvas.drawRect(frameBox, paint);
		
		//Drawing Elaine
		elaine.draw(canvas);		
		dPad.render(canvas);
		actionButtons.render(canvas);
		
		//displayFps(canvas, avgFps);
		if ( !floatingFPS.display(canvas) )
			makeToast("Error. There was a problem displaying FPS");
		
	 }
	 
	 public void updateElaine(){
		
		//Updating Elaine		 
		 if ( collisionDetection ) {		 			 
			 elaine.update(System.currentTimeMillis(), currentBackground.getObstacles());
			 
		 } else {			 
			 elaine.update(System.currentTimeMillis(), frameBox);
			 
		 }
	 }
	 
	 public void update() {
		
		updateElaine();
	 }
}
