package demos.droids;

import java.util.ArrayList;

import demos.shared.Speed;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Rect;

public class Droid {
	
	private Bitmap bitmap; 	// actual bitmap
	private int x; 			// x coordinate
	private int y;			// y coordinate
	private boolean touched;// if droid is touched
	private Speed speed; 	// Speed object
	
	public Droid( Bitmap bitmap, int x, int y ) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		this.speed = new Speed();
	}
	
	public Droid( Bitmap bitmap, int x, int y, int testSpeedCase ) {
		this.bitmap = bitmap;
		this.x = x;	
		this.y = y;
		this.speed = new Speed(testSpeedCase);
	}

	public Droid( Bitmap bitmap, int x, int y, Speed speed ) {
		this.bitmap = bitmap;
		this.x = x;
		this.y = y;
		//this.speed = new Speed();
		this.speed = speed;
	}

	public Bitmap getBitmap() {
		return bitmap;
	}
	
	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}
	
	public int getX() {
		return x;
	}
	
	public void setX( int x ) {
		this.x = x;
	}
	
	public int getY() {
		return y;
	}
	
	public void setY( int y ) {
		this.y = y;
	}
	
	public boolean isTouched() {
		return touched;
	}
	
	public void setTouched( boolean touched ) {
		this.touched = touched;
	}
	
	public Speed getSpeed() {
		return speed;
	}
	
	public void setSpeed( Speed speed ) {
		this.speed = speed;
	}
	
	public void draw( Canvas canvas ) {
		//Coordinates are in the center of the bitmap... so move to top left corner
		canvas.drawBitmap(bitmap, x - (bitmap.getWidth() / 2), y - (bitmap.getHeight() / 2), null);
	}
	
	public void handleActionDown(int eventX, int eventY) {
		//Is the touch within the bitmap area?
		if (eventX >= (x - (2*bitmap.getWidth() / 2)) && (eventX <= (x + (2*bitmap.getWidth()/2)))) {
			if (eventY >= (y - (2*bitmap.getHeight() / 2)) && (eventY <= (y + (2*bitmap.getHeight()/2) ))) {
				//droid touched
				setTouched(true);
			} else {
				setTouched(false);
			} 
		} else { 
			setTouched(false);
		}
	}
	
	public void detectCollisionsInside(Rect box){
		
		// check collision with right wall if heading right
		if (this.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
				&& this.getX() + this.getBitmap().getWidth() / 2 >= box.right) {
			this.getSpeed().togglexDirection();
		}
		// check collision with left wall if heading left
		if (this.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
				&& this.getX() - this.getBitmap().getWidth() / 2 <= box.left) {
			this.getSpeed().togglexDirection();
		}
		// check collision with bottom wall if heading down
		if (this.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
				&& this.getY() + this.getBitmap().getHeight() / 2 >= box.bottom) {
			this.getSpeed().toggleyDirection();
		}
		// check collision with top wall if heading up
		if (this.getSpeed().getyDirection() == Speed.DIRECTION_UP
				&& this.getY() - this.getBitmap().getHeight() / 2 <= box.top) {
			this.getSpeed().toggleyDirection();
		}
	}
	
	public void detectCollisionsOutside(Rect box){
		
		int dxdir = this.getSpeed().getxDirection();
		int dydir = this.getSpeed().getyDirection();
		int dright = this.getX() + (this.getBitmap().getWidth() / 2);
		int dleft = this.getX() - (this.getBitmap().getWidth() / 2);
		int dtop = this.getY() - (this.getBitmap().getHeight() / 2);
		int dbottom = this.getY() + (this.getBitmap().getHeight() / 2);
		
		
		// check collision with left wall if heading right
		if (dxdir == Speed.DIRECTION_RIGHT && dright >= box.left && dright < box.left + (this.getBitmap().getWidth()/2) 
				&& ((dtop > box.top && dtop < box.bottom) 
						|| (dbottom > box.top && dbottom < box.bottom))) {
			this.getSpeed().togglexDirection();
		}
		
		
		// check collision with right wall if heading left
		if (dxdir == Speed.DIRECTION_LEFT && dleft <= box.right && dleft > box.right - (this.getBitmap().getWidth()/2)
				&& ((dtop > box.top && dtop < box.bottom) 
						|| (dbottom > box.top && dbottom < box.bottom))) {
			this.getSpeed().togglexDirection();
		}
		
		// check collision with bottom wall if heading down
		if (dydir == Speed.DIRECTION_DOWN && dbottom >= box.top && dbottom < box.top + (this.getBitmap().getHeight()/2)
				&& ((dright > box.left && dright < box.right) 
						|| (dleft > box.left && dleft < box.right))) {
			this.getSpeed().toggleyDirection();
		}
		
		// check collision with top wall if heading up
		if (dydir == Speed.DIRECTION_UP && dtop <= box.bottom && dtop > box.bottom - (this.getBitmap().getHeight()/2)
				&& ((dright > box.left && dright < box.right) 
						|| (dleft > box.left && dleft < box.right))) {
			this.getSpeed().toggleyDirection();
		}
	}
	
	public void detectCollisions(ArrayList<Rect> obstacles){
		
		// Assuming framebox is in the first location
		detectCollisionsInside(obstacles.get(0));
		
		
		// Detecting collisions 
		for ( int i = 1; i < obstacles.size(); i++ ) {
			//detectCollisionsInside(obstacles.get(i));
			detectCollisionsOutside(obstacles.get(i));
		}
	}
	
	public void update(Rect frameBox, int speedFactor) {
		if (!touched){
			
			//Saving up on paused animation
			if ( speedFactor != 0 ) {
			
				// Detect collisions and make proper changes when necessary
				detectCollisionsInside(frameBox);
				
				//Log.d(TAG, "x=" + this.x + " xv=" + this.speed.getXv());
				
				x += (speed.getXv() * speed.getxDirection() * speedFactor);
				y += (speed.getYv() * speed.getyDirection() * speedFactor);
			}
		}
	}
	
	public void update(ArrayList<Rect> obstacles, int speedFactor) {
		if (!touched){
			
			//Saving up on paused animation
			if ( speedFactor != 0 ) {		
			
				// Detect collisions and make proper changes when necessary
				detectCollisions(obstacles);
				
				//Log.d(TAG, "x=" + this.x + " xv=" + this.speed.getXv());
				
				x += (speed.getXv() * speed.getxDirection() * speedFactor);
				y += (speed.getYv() * speed.getyDirection() * speedFactor);
			}
		}
	}
	
	public void multiplySpeed(int factor) {
		this.speed.multiply(factor);
	}
}
