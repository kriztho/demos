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
	private char size;
	private Rect frame;

	public OnScreenController(int numButtons, int layout, int x, int y, char size, int alpha) {		
		this.numButtons = numButtons;
		this.layout = layout;
		this.alpha = alpha;	
		this.x = x;
		this.y = y;
		this.size = size;
		this.color = Color.GRAY;
		createController();			
	}
	
	public OnScreenController(int numButtons, int layout, Rect frame, char size, int alpha) {		
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
			//Horizontal
			if ( layout == 0 ) {
				buttons[0] = new OnScreenButton(frame.right - 156, frame.bottom - 80, size, color, alpha);
				buttons[1] = new OnScreenButton(buttons[0].getX() + buttons[0].getWidth() + 3, buttons[0].getY(), size, color, alpha);
			} else { //Vertical
				
			}
		}
		
		if ( numButtons == 4 ) {
			if ( layout == 0 ) { // On a horizontal line
				buttons[0] = new OnScreenButton(x, y, size, color, alpha);
				buttons[1] = new OnScreenButton(x + buttons[0].getWidth() + 3, y, size, color, alpha);
				buttons[2] = new OnScreenButton(buttons[1].getX() + buttons[1].getWidth() + 3, y, size, color, alpha);
				buttons[3] = new OnScreenButton(buttons[2].getX() + buttons[2].getWidth() + 3, y, size, color, alpha);
			} else {			// On a cross shape
				buttons[0] = new OnScreenButton(3, frame.bottom - 103, size, color, alpha);
				buttons[1] = new OnScreenButton(buttons[0].getX() + buttons[0].getWidth(), 
												buttons[0].getY() - buttons[0].getHeight(), 
													size, color, alpha);
				buttons[2] = new OnScreenButton(buttons[0].getX() + buttons[0].getWidth(), 
												buttons[0].getY() + buttons[0].getHeight(), 
													size, color, alpha);
				buttons[3] = new OnScreenButton(buttons[1].getX() + buttons[0].getWidth(), 
												buttons[1].getY() + buttons[0].getHeight(), 
													size, color, alpha);
								
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
