package demos.shared;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class OnScreenButton {

	private int x, y;
	private int width, height;
	private Rect button;
	private boolean touched; 
	private int alpha, color;
	private char size;

	public OnScreenButton(int x, int y, char size, int color, int alpha) {
		
		this.x = x;
		this.y = y;
		this.size = size;
		switch(size) {
		case 's':
			width = 25;
			height = 25;
			break;
		case 'm':
			width = 50;
			height = 50;
			break;
		case 'l':
			width = 75;
			height = 75;
			break;
		}
		
		button = new Rect(x, y, x + width, y + height);
		touched = false;
		this.color = color;
		this.alpha = alpha;		
	}
	
	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}

	public Rect getButton() {
		return button;
	}

	public boolean isTouched() {
		return touched;
	}

	public int getAlpha() {
		return alpha;
	}

	public int getColor() {
		return color;
	}

	public char getSize() {
		return size;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public void setHeight(int height) {
		this.height = height;
	}

	public void setButton(Rect button) {
		this.button = button;
	}

	public void setTouched(boolean touched) {
		this.touched = touched;
	}

	public void setAlpha(int alpha) {
		this.alpha = alpha;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setSize(char size) {
		this.size = size;
	}

	public boolean isTouching(int x, int y) {
		
		if ( x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height ) {
			touch();
		} else {
			release();
		}
		
		return touched;
	}
	
	public boolean isTouchingNoChange(int x, int y) {
		
		if ( x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height ) {
			touched = true;
		} else {
			touched = false;
		}
		
		return touched;
	}
	
	public void touch() {
		touched = true;
		color = Color.RED;
	}
	
	public void release() {
		touched = false;
		color = Color.GRAY;
	}
	
	public void render(Canvas canvas) {
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAlpha(alpha);
		canvas.drawRect(button, paint);
	}

}
