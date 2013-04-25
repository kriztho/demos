package demos.shared;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;
import android.renderscript.Float2;

public class OnScreenController {
	
	private int x, y;					// X, Y coordinates of the upperleft corner
	private int alpha, color;			// Appearance attributes
	private int numButtons;				// Number of elements in the controller
	private OnScreenButton[] buttons;	// Button-typed controllers
	private OnScreenJoyStick[] sticks;	// Stick-typed controllers
	private int layout;					// vertical, horizontal, cross-shape, etc. Not all of them are currently available
	private int size;					// size of the controller buttons (index to a scale within an array)
	private Rect frame;					// bounding box
	private String type;				// Button or Stick

	// It allows to input X and Y coordinates for the upperleft corner of the control
	public OnScreenController(String type, int numButtons, int layout, int x, int y, int size, int alpha) {		
		this.numButtons = numButtons;
		this.layout = layout;
		this.alpha = alpha;	
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = Color.GRAY;
		this.type = type;
		createController();			
	}
	
	// It considers the framebox in which the game is happening
	public OnScreenController(String type, int numButtons, int layout, Rect frame, int size, int alpha) {		
		this.numButtons = numButtons;
		this.layout = layout;
		this.alpha = alpha;
		this.frame = frame;
		this.size = size;
		this.color = Color.GRAY;
		this.type = type;
		createController();			
	}
	
	public void createController() {
		
		// Check for the type of controller to make
		if ( type == "button" ) {
			buttons = new OnScreenButton[numButtons];
			sticks = null;

			// 2 buttons controller
			if ( numButtons == 2 ) {

				buttons[0] = new OnScreenButton(size, color, alpha);
				buttons[1] = new OnScreenButton(size, color, alpha);

				//Horizontal
				if ( layout == 0 ) {
					buttons[0].setX(frame.right - (2*buttons[0].getWidth()) - 6);
					buttons[0].setY(frame.bottom - buttons[0].getHeight() - 3);
					buttons[1].setX(buttons[0].getX() + buttons[0].getWidth() + 3);
					buttons[1].setY(frame.bottom - buttons[0].getHeight() - 3);
				} else { //Vertical

				}
			}

			// 4 buttons controller
			if ( numButtons == 4 ) {

				buttons[0] = new OnScreenButton(size, color, alpha);
				buttons[1] = new OnScreenButton(size, color, alpha);
				buttons[2] = new OnScreenButton(size, color, alpha);
				buttons[3] = new OnScreenButton(size, color, alpha);

				// On a horizontal line
				if ( layout == 0 ) { 	
					buttons[0].setX(3);
					buttons[0].setY(frame.bottom - buttons[0].getHeight() - 3);

					buttons[1].setX(buttons[0].getX() + buttons[0].getWidth() + 3);
					buttons[1].setY(frame.bottom - buttons[0].getHeight() - 3);

					buttons[2].setX(buttons[1].getX() + buttons[0].getWidth() + 3);
					buttons[2].setY(frame.bottom - buttons[0].getHeight() - 3);

					buttons[3].setX(buttons[2].getX() + buttons[0].getWidth() + 3);
					buttons[3].setY(frame.bottom - buttons[0].getHeight() - 3);
				} else {

					// On a cross shape				
					buttons[0].setX(3);
					buttons[0].setY(frame.bottom - 2*buttons[0].getHeight() - 3);

					buttons[1].setX(buttons[0].getX() + buttons[0].getWidth());
					buttons[1].setY(buttons[0].getY() - buttons[0].getHeight());

					buttons[2].setX(buttons[0].getX() + buttons[0].getWidth());
					buttons[2].setY(buttons[0].getY() + buttons[0].getHeight());

					buttons[3].setX(buttons[1].getX() + buttons[0].getWidth());
					buttons[3].setY(buttons[1].getY() + buttons[0].getHeight());
				}
			}
		} else if (type == "stick") {
			
			sticks = new OnScreenJoyStick[numButtons];
			buttons = null;
			
			for ( int i = 0; i < numButtons; i++ ) {
				sticks[i] = new OnScreenJoyStick(new Float2(203,485), 4);
			}
		}
	}
	
	// Checks if the touch is currently pointing to a controller button and returns the index of this or -1 otherwise
	public int isTouching(int x, int y) {
		
		int index = 0;
		while ( index < this.numButtons ) {
			
			if ( type == "button" ) {
				if ( buttons[index].isTouching(x, y) )
					return index;
			} else if ( type == "stick" ) {
				if ( sticks[index].isTouching(x, y) )
					return index;
			}
			index++;
		}
		
		return -1;
	}
	
	// Same as above but it doesn't apply the effects of actually touching the buttons
	public int isTouchingNoChange(int x, int y) {
		
		int index = 0;
		while ( index < this.numButtons ) {
			if ( type == "button" ) {
				if ( buttons[index].isTouchingNoChange(x, y) )
					return index;
			} else if (type == "stick" ) {
				if ( sticks[index].isTouchingNoChange(x, y) )
					return index;
			}
			index++;
		}
		
		return -1;
	}
	
	// Applies the effects of touching to the selected button
	public void touch(int index) {
		if ( type == "button" )
			buttons[index].touch();
		else if (type == "stick" )
			sticks[index].touch();
	}
	
	// Applies the effects of releasing to the selected button
	public void release(int index) {
		if ( type == "button" )
			buttons[index].release();
		else if (type == "stick" )
			sticks[index].release();
	}
	
	///////////////////////////////////////////////
	// Rendering
	///////////////////////////////////////////////
	// Renders all buttons within the controller 
	public void render(Canvas canvas) {
		int index = 0;
		if ( type == "button" ) {
			while ( index < this.numButtons ) {

				buttons[index].render(canvas);
				index++;
			}
		} else if ( type == "stick" ) {
			while ( index < this.numButtons ) {
				sticks[index].render(canvas);
				index++;
			}
		}
	}
	
	////////////////////////////////////////////////
	// JoyStick specific routines
	////////////////////////////////////////////////
	// Resets the handle to the stick origin
	public void reset(int index) {
		sticks[index].reset();
	}
	
	// Updates the effects of dragging the stick around
	public void drag(int index, int x, int y) {
		sticks[index].drag(x, y);
	}
	
	// Gets the speed generated with the stick 
	public Speed getSpeed(int index) {
		return sticks[index].getSpeed();
	}
}
