package demos.shared;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.renderscript.Float2;
import android.util.Log;

public class OnScreenJoyStick {

	//Tag for logging on Android's Log
	private static final String TAG = OnScreenJoyStick.class.getSimpleName();

	// main data
	private Float2 origin;							// Base point
	private Float2 endpoint;						// Current position of the finger
	private final float maxLength = 200;			// Of the line between the origin and endpoint
	private final float defaultSpeed = 0.5f;			// Base speed for calculations

	// derived data
	private float currentLength;					// Current length of the line between origin and endpoint 
	private Speed speed;							// speedX and speedY

	// for rendering
	private final int buttonSizes[] = {25, 50, 75, 100, 125, 150};	// List of available sizes for buttons
	private float x, y;								// The handle corner point
	private int width, height;						// Of the handle
	private Rect button;							// Handle of the stick
	private boolean touched;						// Current state
	private int alpha = 150;						// Transparency
	private int color;								// Color of the handle
	private int sizeCategory;						// Category index to use with the list of available sizes
	private float theta = 0.0f;
	private String direction = "";

	// Controlling Modes
	private final boolean resetMode = true;			// Resets the handle when the finger exits the active area
	private final boolean variableSpeed = false;	// Uses the distance between origin and endpoint to ratio speed

	public OnScreenJoyStick(Float2 origin, int sizeCategory) {
		// Stick properties
		this.origin = origin;
		this.endpoint = origin;
		this.speed = new Speed(0,0);
		this.currentLength = 0;
		
		// Handle properties
		this.width = this.height = buttonSizes[sizeCategory];
		this.x = endpoint.x - ((float) width / 2.0f);
		this.y = endpoint.y - ((float) height / 2.0f);
		button = new Rect((int)x, (int)y, (int)x + width, (int)y + height);
		color = Color.GRAY;
	}
	
	////////////////////////////////////////////////////////////////////////////////////
	// Getters and setters
	////////////////////////////////////////////////////////////////////////////////////
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

	public float getTheta() {
		return theta;
	}

	public void setTheta(float theta) {
		this.theta = theta;
	}
	
	public String getDirection() {
		return direction;
	}

	public void setDirection(String direction) {
		this.direction = direction;
	}

	
	////////////////////////////////////////////////////////////////////////////////////
	// Helper routines
	////////////////////////////////////////////////////////////////////////////////////

	// The input points are defined in absolute coordinates (From the upperleft corner of the screen)
	public float getDistanceBetween2Points(Float2 p1, Float2 p2) {
		float distance = 0f;

		//Using pythagorean theorem = sqrt((x2-x1)^2+(y2-y1)^2))
		distance = ((p1.x - p2.x) * (p1.x - p2.x)); 	
		distance += ((p1.y - p2.y) * (p1.y - p2.y));	
		distance = (float) Math.sqrt(distance);			

		return  distance;
	}
	
	// Calculates and truncates the speed vector to a 8Direction system
	public void calculate8dir(float xRel, float yRel) {
		
		// Determine speeds in X and Y
		float quotient = xRel / yRel;
		if ( quotient < 0 )
			quotient = -quotient;
		
		if ( quotient >= 4 ) {
			// 0°
			speed.setXv(defaultSpeed);
			speed.setYv(0);
			theta = 0f;
		} else if ( quotient >= 1.5 && quotient < 4 ) {
			// 22.5° -> 0.3926rad
			speed.setXv((float) (defaultSpeed*Math.cos(0.3926))); 	
			speed.setYv((float) (defaultSpeed*Math.sin(0.3926)));
			theta = 22.5f;
		} else if ( quotient >= 0.66 && quotient < 1.5 ) {
			// 45°
			speed.setXv(defaultSpeed);
			speed.setYv(defaultSpeed);
			theta = 45f;
		} else if ( quotient >= 0.25 && quotient < 0.66 ) {
			// 67.5
			speed.setXv((float) (defaultSpeed*Math.cos(1.1780))); 	
			speed.setYv((float) (defaultSpeed*Math.sin(1.1780)));
			theta = 67.5f;
		} else if ( quotient < 0.25 ) {
			// 90°
			speed.setXv(0);
			speed.setYv(defaultSpeed);
			theta = 90f;
		}
		
		//Determine 2-axis direction
		if ( xRel < 0 ) {
			speed.setxDirection(Speed.DIRECTION_RIGHT);
			Log.d(TAG, "Right");
			direction = "Right+";
		} else {			
			speed.setxDirection(Speed.DIRECTION_LEFT);
			Log.d(TAG, "Left");
			direction = "Left+";
		}
		if ( yRel < 0 ) {			
			speed.setyDirection(Speed.DIRECTION_DOWN);
			Log.d(TAG, "Down");
			direction = "Down";
		} else {
			speed.setyDirection(Speed.DIRECTION_UP);
			Log.d(TAG, "Up");
			direction = "Up";
		}
	}

	// Sets the speed value 
	public void calculateSpeed(Float2 p1, Float2 p2) {

		//Find speed ratios (increases on both directions)
		float xRel = p1.x - p2.x;
		float yRel = p1.y - p2.y;

		if ( variableSpeed ) {
			// Establishes the direction of the speed
			if ( xRel < 0 ) {
				xRel /= -maxLength;
				speed.setxDirection(Speed.DIRECTION_RIGHT);
			} else {
				xRel /= maxLength;
				speed.setxDirection(Speed.DIRECTION_LEFT);
			}
			if ( yRel < 0 ) {
				yRel /= -maxLength;
				speed.setyDirection(Speed.DIRECTION_DOWN);
			} else {
				yRel /= maxLength;
				speed.setyDirection(Speed.DIRECTION_UP);
			}
			
			// Multiply speedratio
			speed.setXv(xRel * defaultSpeed);
			speed.setYv(yRel * defaultSpeed);
			
			// Diminish the effects of the distance between origin and endpoint
			//speed.setXv(xRel + defaultSpeed);
			//speed.setYv(ySpeedRatio + defaultSpeed);
		} else {
			//Calculate the speed accoring to a 8Pad system
			calculate8dir(xRel, yRel);
		}
	}
	
	public double calculateAngle(float opposite, float hypo) {
		return Math.asin((double)opposite / (double)hypo);
	}

	// Calculates the corresponding endpoint with the current finger direction but with the maxlength limitation
	public Float2 calculateMaxEndPoint(int x, int y) {

		float hypo = getDistanceBetween2Points(origin, new Float2(x,y));

		// Find the endpoint relative to the origin
		x = (int) (x - origin.x);
		y = (int) (y - origin.y);

		double theta = Math.asin((double)y / (double)hypo);
		float ynew = (float) (maxLength * Math.sin(theta));
		float xnew = (float) (maxLength * Math.cos(theta));

		Log.d(TAG, "("+ x +","+ y+")" + " "+theta+"° " + "("+xnew +","+ ynew+")");

		return new Float2(origin.x+xnew, origin.y+ynew);
	}

	// A drag has occurred = origin was set and endpoint is different and updated here
	public void drag(int x, int y){

		//Check for maxlength and crop it if necessary
		if ( getDistanceBetween2Points(origin, endpoint) < maxLength ) {

			// Set the new value for endpoint and update
			setEndpoint(new Float2(x,y));
			updateHandle();
			
			// Calculate derived values if corresponding mode is on
			currentLength = getDistanceBetween2Points(origin, endpoint);
			calculateSpeed(origin, endpoint);
			
		} else {
			// Reset mode returns the handle to the resting position whenever thee finger exits the active area
			if ( resetMode ) {
				reset();
			} else {
				// Set the maximum new value for endpoint and update
				setEndpoint(calculateMaxEndPoint(x,y));
				updateHandle();
				
				// Calculate derived values if corresponding mode is on
				currentLength = getDistanceBetween2Points(origin, endpoint);
				calculateSpeed(origin, endpoint);
			}
		}
	}

	// Pulls back the joystick handle to rest position (origin)
	public void reset() {

		// Set the new value for endpoint and update
		setEndpoint(origin);
		updateHandle();
		theta = 0;
	}

	// Updates the position of the endpoint (handle)
	public void updateHandle() {

		// Get the upperleft corner of the new endpoint position
		this.x = endpoint.x - ((float) width / 2.0f);
		this.y = endpoint.y - ((float) height / 2.0f);		
		
		// Update the handle accordingly
		button = new Rect((int)x, (int)y, (int)x + width, (int)y + height);
	}

	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	// Touch resolving routines
	////////////////////////////////////////////////////////////////////////////////////
	
	// Checks if current finger position is reaching out the handle and if so,  it touches or releases the control
	public boolean isTouching(int x, int y) {

		if ( x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height ) {
			touch();
		} else {
			release();
		}

		return touched;
	}

	// Same as above but only checks for touches but it doesn't apply any effect to the control
	public boolean isTouchingNoChange(int x, int y) {

		if ( x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height ) {
			touched = true;
		} else {
			touched = false;
		}

		return touched;
	}

	// Same as above but only touches even if the finger was dragged outside the active area and it doesn't release the control
	public boolean isTouchingOnly(int x, int y) {
		if ( x > this.x && x < this.x + this.width && y > this.y && y < this.y + this.height ) {
			touched = true;			
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

	
	
	
	
	////////////////////////////////////////////////////////////////////////////////////
	// Rendering routines
	////////////////////////////////////////////////////////////////////////////////////
	public void render(Canvas canvas) {
		
		// Draw the handle
		Paint paint = new Paint();
		paint.setColor(color);
		paint.setAlpha(alpha);
		canvas.drawRect(button, paint);

		// Draw line from origin to endpoint
		paint.setColor(Color.CYAN);
		paint.setAlpha(255);
		paint.setStrokeWidth(5);
		canvas.drawLine(origin.x, origin.y, endpoint.x, endpoint.y, paint);

		// Draw bounding area with maxlength radius
		paint.setColor(Color.RED);
		paint.setAlpha(255);
		paint.setStyle(Paint.Style.STROKE);
		paint.setStrokeWidth(5);

		/*
		canvas.drawRect(origin.x - (int)(maxLength), //left 
						origin.y - (int)(maxLength), //top
						origin.x + (int)(maxLength), //right
						origin.y + (int)(maxLength), paint);	//bottom
		 */

		canvas.drawCircle(origin.x, origin.y, maxLength, paint);
	}
}
