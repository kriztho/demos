package demos.shared;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Float2;

public class OnScreenJoyStick {

	// main data
	private Float2 origin;
	private Float2 endpoint;
	private final float maxLength = 100;
	private final float defaultSpeed = 5f;
	
	// derived data
	private float currentLength;
	//private Float2 direction;		// x and y
	private Speed speed;			// speedX and speedY
	
	// for rendering
	private final int buttonSizes[] = {25, 50, 75, 100, 125, 150};
	private float x, y;				// the button corner point
	private int width, height;
	private Rect button;
	private boolean touched;
	private int alpha = 150;
	private int color;
	private int sizeCategory;
	
	public OnScreenJoyStick() {
		origin = null;
		endpoint = null;
		speed = new Speed(0,0);
	}
	
	public OnScreenJoyStick(Float2 origin, Float2 endpoint, int sizeCategory) {
		this.origin = origin;
		this.endpoint = endpoint;
		this.speed = new Speed(0,0);
		
		this.currentLength = getDistanceBetween2Points(origin, endpoint);
		calculateSpeed(origin, endpoint);
		
		this.width = this.height = buttonSizes[sizeCategory];
		this.x = origin.x - ((float) width / 2.0f);
		this.y = origin.y - ((float) height / 2.0f);
		
		button = new Rect((int)x, (int)y, (int)x + width, (int)y + height);
		color = Color.GRAY;
	}

	////////////////////////////////////////////
	// Getters and setters
	////////////////////////////////////////////
	public Float2 getOrigin() {
		return origin;
	}

	public Float2 getEndpoint() {
		return endpoint;
	}

	public float getMaxLength() {
		return maxLength;
	}

	public float getDefaultSpeed() {
		return defaultSpeed;
	}

	public float getCurrentLength() {
		return currentLength;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setOrigin(Float2 origin) {
		this.origin = origin;
	}

	public void setEndpoint(Float2 endpoint) {
		this.endpoint = endpoint;
	}

	public void setCurrentLength(float currentLength) {
		this.currentLength = currentLength;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	
	////////////////////////////////////////////
	// Helper routines
	////////////////////////////////////////////
	public float getDistanceBetween2Points(Float2 p1, Float2 p2) {
		float distance = 0f;
		
		//Using pythagorean theorem = sqrt((x2-x1)^2+(y2-y1)^2))
		distance = ((p1.x - p2.x) * (p1.x - p2.x)); 	
		distance += ((p1.y - p2.y) * (p1.y - p2.y));	
		distance = (float) Math.sqrt(distance);			
		
		return  distance;
	}
	
	public void calculateSpeed(Float2 p1, Float2 p2) {
		
		//Find speed ratios (normalize)
		float xSpeedRatio = Math.abs(p1.x - p2.x);
		xSpeedRatio /= maxLength;
		float ySpeedRatio = Math.abs(p1.y - p2.y);
		ySpeedRatio /= maxLength;
		
		// Multiply speedratio
		this.speed.setXv(xSpeedRatio * defaultSpeed);
		this.speed.setYv(ySpeedRatio * defaultSpeed);
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
		paint.setColor(Color.GRAY);
		paint.setAlpha(alpha);
		canvas.drawRect(button, paint);
		
		// Draw line from origin to endpoint
		paint.setColor(Color.WHITE);
		paint.setAlpha(255);
		canvas.drawLine(origin.x, origin.y, endpoint.x, endpoint.y, paint);
		
		// Draw boundingbox with maxlength
		paint.setColor(Color.RED);
		paint.setAlpha(255);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);
		canvas.drawRect(origin.x - (int)(1.5*maxLength), //left 
						origin.y - (int)(1.5*maxLength), //top
						origin.x + (int)(1.5*maxLength), //right
						origin.y + (int)(1.5*maxLength), paint);	//bottom
	}
}
