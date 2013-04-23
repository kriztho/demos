package demos.shared;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Rect;

public class OnScreenController {
	
	private int x, y;
	//private int width, height;
	private int alpha, color;
	private int numButtons;
	private OnScreenButton[] buttons;
	private int layout;
	private int size;
	private Rect frame;

	public OnScreenController(int numButtons, int layout, int x, int y, int size, int alpha) {		
		this.numButtons = numButtons;
		this.layout = layout;
		this.alpha = alpha;	
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = Color.GRAY;
		createController();			
	}
	
	public OnScreenController(int numButtons, int layout, Rect frame, int size, int alpha) {		
		this.numButtons = numButtons;
		this.layout = layout;
		this.alpha = alpha;
		this.frame = frame;
		this.size = size;
		this.color = Color.GRAY;
		createController();			
	}
	
	public void createController() {
		
		buttons = new OnScreenButton[numButtons];
		
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
	}
	
	public int isTouching(int x, int y) {
		
		int buttonIndex = 0;
		while ( buttonIndex < this.numButtons ) {
			if ( buttons[buttonIndex].isTouching(x, y) )
				return buttonIndex;
			buttonIndex++;
		}
		
		return -1;
	}
	
	public int isTouchingNoChange(int x, int y) {
		
		int buttonIndex = 0;
		while ( buttonIndex < this.numButtons ) {
			if ( buttons[buttonIndex].isTouchingNoChange(x, y) )
				return buttonIndex;
			buttonIndex++;
		}
		
		return -1;
	}
	
	public void touch(int buttonIndex) {
		buttons[buttonIndex].touch();
	}
	
	public void release(int buttonIndex) {
		buttons[buttonIndex].release();
	}
	
	public void render(Canvas canvas) {
		int buttonIndex = 0;
		while ( buttonIndex < this.numButtons ) {
			buttons[buttonIndex].render(canvas);
			buttonIndex++;
		}
	}

}
