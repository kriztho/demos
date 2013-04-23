package demos.shared;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

public class OnScreenButton {
	
	private final int buttonSizes[] = {25, 50, 75, 100, 125, 150};
	public enum buttonSizeCategories {
		SMALL, MEDIUM, LARGE, XLARGE, XXLARGE, XXXLARGE;
	}

	private int x, y;
	private int width, height;
	private Rect button;
	private boolean touched; 
	private int alpha, color;
	private int sizeCategory;
	
	public OnScreenButton(int sizeCategory, int color, int alpha) {
		this.sizeCategory = sizeCategory;
		this.touched = false;
		this.color = color;
		this.alpha = alpha;
		width = height = buttonSizes[sizeCategory];
		button = new Rect(0,0,width, height);
	}

	public OnScreenButton(int x, int y, int sizeCategory, int color, int alpha) {
		
		this.x = x;
		this.y = y;
		this.sizeCategory = sizeCategory;
		switch(sizeCategory) {
		case 1:
			width = 25;
			height = 25;
			break;
		case 2:
			width = 50;
			height = 50;
			break;
		case 3:
			width = 75;
			height = 75;
			break;
		case 4:
			width = 100;
			height = 100;
			break;
		case 5:
			width = 125;
			height = 125;
			break;
		case 6:
			width = 150;
			height = 150;
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

	public int getSize() {
		return sizeCategory;
	}

	public void setX(int x) {
		this.x = x;
		button.left = this.x;
		button.right = this.x + this.width;
	}

	public void setY(int y) {
		this.y = y;
		button.top = this.y;
		button.bottom = this.y + this.height;
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

	public void setSize(char sizeCategory) {
		this.sizeCategory = sizeCategory;
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
