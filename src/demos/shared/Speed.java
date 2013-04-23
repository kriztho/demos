package demos.shared;

import android.util.Log;

import demos.shared.MainGamePanel;

public class Speed {
	
	//Tag for logging on Android's Log
	private static final String TAG = MainGamePanel.class.getSimpleName();

	// Unitary axes
	public static final int DIRECTION_RIGHT = 1;
	public static final int DIRECTION_LEFT = -1;
	public static final int DIRECTION_UP = -1;
	public static final int DIRECTION_DOWN = 1;
	
	private float xv = 1; // velocity value on the X axis
	private float yv = 1; // velocity value on the Y axis
	
	private int xDirection = DIRECTION_RIGHT;
	private int yDirection = DIRECTION_DOWN;
	
	public Speed() {
		
		this.xv = 5;
		this.yv = 5;
		
		// generate a random direction
		if (rndInt(0, 1) == 0)
			this.xDirection = DIRECTION_RIGHT;
		else
			this.xDirection = DIRECTION_LEFT;
		
		if (rndInt(0, 1) == 0)
			this.yDirection = DIRECTION_UP;
		else
			this.yDirection = DIRECTION_DOWN;
	}
	
	public Speed(int testSpeedCase){
		
		switch(testSpeedCase) {
			case 0:
				//To the right
				this.xv = 1;
				this.yv = 0;
				this.xDirection = DIRECTION_RIGHT;
				break;
			case 1:
				//To the left
				this.xv = 1;
				this.yv = 0;
				this.xDirection = DIRECTION_LEFT;
				break;
			case 2:
				//To the top
				this.xv = 0;
				this.yv = 1;
				this.yDirection = DIRECTION_UP;
				break;
			case 3:
				//To the bottom
				this.xv = 0;
				this.yv = 5;
				this.yDirection = DIRECTION_DOWN;
				break;
			default:
				//To the right
				this.xv = 1;
				this.yv = 1;
				this.xDirection = DIRECTION_RIGHT;
				this.yDirection = DIRECTION_DOWN;
				break;
		}
	}
	
	public Speed(float xv, float yv) {
		this.xv = xv;
		this.yv = yv;
		
		// generate a random direction
		if (rndInt(0, 1) == 0)
			this.xDirection = DIRECTION_RIGHT;
		else
			this.xDirection = DIRECTION_LEFT;
		
		if (rndInt(0, 1) == 0)
			this.yDirection = DIRECTION_UP;
		else
			this.yDirection = DIRECTION_DOWN;
	}
	
	public float getXv(){
		return xv;
	}
	
	public void setXv(float xv){
		this.xv = xv;
	}
	
	public float getYv(){
		return yv;
	}
	
	public void setYv(float yv){
		this.yv = yv;
	}
	
	public int getxDirection(){
		return xDirection;
	}
	
	public void setxDirection(int xDirection){
		this.xDirection = xDirection;
	}
	
	public int getyDirection(){
		return yDirection;
	}
	
	public void setyDirection(int yDirection){
		this.yDirection = yDirection;
	}
	
	//Changes the direction on the X axis
	public void togglexDirection(){
		xDirection *= -1;
	}
	
	//Changes the direction on the X axis
	public void toggleyDirection(){
		yDirection *= -1;
	}

	public void random(int min, int max) {	
		this.xv = (float) (rndDbl(0, max));
		
		if (this.xv < 0)
			Log.d(TAG, "Something was negative!!: min="+ min+ " max="+max+" n="+this.xv);
		
		this.yv = (float) (rndDbl(0, max));
		
		if (this.yv < 0)
			Log.d(TAG, "Something was negative!!: min="+ min+ " max="+max+" n="+this.yv);
	}

	public void smooth(int maxSpeed, double smoothFactor) {
		if (xv * xv + yv * yv > maxSpeed * maxSpeed) {
			xv *= smoothFactor;
			yv *= smoothFactor;
		}		
	}
	
	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		
		return min + (max - min) * Math.random();
	}
	
	public void multiply(int factor){
		this.xv *= factor;
		this.yv *= factor;
	}
}