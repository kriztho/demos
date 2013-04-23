package demos.animatedElaine;

import java.util.ArrayList;

import demos.shared.Speed;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.view.MotionEvent;

public class ElaineAnimated {
	
	private static final String TAG = ElaineAnimated.class.getSimpleName();
	
	private Bitmap bitmap;		// the animation sequence
	private Bitmap bitmapOriginal;
	private Bitmap bitmapReversed;
	private Rect sourceRect;	// the rectangle to be drawn from the animation bitmap
	private int frameNr;		// number of frames in animation
	private int currentFrame;	// the current frame
	private long frameTicker;	// the time of the last frame update
	private int framePeriod;	// milliseconds between each frame 1000/fps
	
	private int spriteWidth;	// the width of the sprite to calculate the cut out rectangle
	private int spriteHeight;	// the hight of the sprite
	
	private int x;				// the x coordinate of the object
	private int y;				// the y coordinate of the object
	
	private Speed speed;
	
	private boolean animate;
	private boolean showSource;
	private boolean running;

	private String direction;

	public ElaineAnimated (Bitmap bitmap, int x, int y, int fps, int frameCount ) {
				
		this.bitmapOriginal = bitmap;
		this.bitmapReversed = flip(bitmap);
		this.bitmap = this.bitmapOriginal;
		this.x = x;
		this.y = y;
		currentFrame = 0;
		frameNr = frameCount;
		spriteWidth = bitmap.getWidth() / frameCount;
		spriteHeight = bitmap.getHeight();
		sourceRect = new Rect(0,0, spriteWidth, spriteHeight);
		framePeriod = 1000 / fps;
		frameTicker = 0l;
		this.speed = new Speed(0,0);
		this.animate = false;
		this.running = false;
		this.showSource = false;
	}
	
	public Bitmap getBitmap() {
		return bitmap;
	}

	public Rect getSourceRect() {
		return sourceRect;
	}

	public int getFrameNr() {
		return frameNr;
	}

	public int getCurrentFrame() {
		return currentFrame;
	}

	public long getFrameTicker() {
		return frameTicker;
	}

	public int getFramePeriod() {
		return framePeriod;
	}

	public int getSpriteWidth() {
		return spriteWidth;
	}

	public int getSpriteHeight() {
		return spriteHeight;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}
	
	public boolean getRunning() {
		return running;
	}

	public void setBitmap(Bitmap bitmap) {
		this.bitmap = bitmap;
	}

	public void setSourceRect(Rect sourceRect) {
		this.sourceRect = sourceRect;
	}

	public void setFrameNr(int frameNr) {
		this.frameNr = frameNr;
	}

	public void setCurrentFrame(int currentFrame) {
		this.currentFrame = currentFrame;
	}

	public void setFrameTicker(long frameTicker) {
		this.frameTicker = frameTicker;
	}

	public void setFramePeriod(int framePeriod) {
		this.framePeriod = framePeriod;
	}

	public void setSpriteWidth(int spriteWidth) {
		this.spriteWidth = spriteWidth;
	}

	public void setSpriteHeight(int spriteHeight) {
		this.spriteHeight = spriteHeight;
	}

	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	public Speed getSpeed() {
		return speed;
	}

	public void setSpeed(Speed speed) {
		this.speed = speed;
	}
	
	public void setRunning(boolean running) {
		this.running = running;
		
		if ( this.running == false ) {
			if ( this.speed.getXv() != 0 )
				this.speed.setXv(2);
			
			if ( this.speed.getYv() != 0 )
				this.speed.setYv(2);
		} else {
			if ( this.speed.getXv() != 0 )
				this.speed.setXv(5);
			
			if ( this.speed.getYv() != 0 )
				this.speed.setYv(5);
		}
	}
	
	//////////////////////////////////////////////
	// Controlling Methods
	/////////////////////////////////////////////
	
	Bitmap flip(Bitmap d)
	{
	    Matrix m = new Matrix();
	    m.preScale(-1, 1);
	    Bitmap dst = Bitmap.createBitmap(d, 0, 0, d.getWidth(), d.getHeight(), m, false);
	    dst.setDensity(DisplayMetrics.DENSITY_DEFAULT);
	    return (new BitmapDrawable(dst)).getBitmap();
	}
	
	public void move(String direction) {
		
		this.animate = true;
		this.direction = direction;
		
		if ( direction == "right") {
			this.speed.setxDirection(Speed.DIRECTION_RIGHT);
			this.speed.setXv(2);
			this.speed.setYv(0);
			this.bitmap = this.bitmapOriginal;
		}
		
		if ( direction == "left") {
			this.speed.setxDirection(Speed.DIRECTION_LEFT);
			this.speed.setXv(2);
			this.speed.setYv(0);
			this.bitmap = this.bitmapReversed;
		}
		
		if ( direction == "up") {
			this.speed.setyDirection(Speed.DIRECTION_UP);
			this.speed.setXv(0);
			this.speed.setYv(2);
		}
		
		if ( direction == "down") {
			this.speed.setyDirection(Speed.DIRECTION_DOWN);
			this.speed.setXv(0);
			this.speed.setYv(2);
		}
	}
	
	public void stop() {
		this.animate = false;
		stopX();
		stopY();
	}
	
	public void stopX() {
		this.speed.setXv(0);
	}
	
	public void stopY() {
		this.speed.setYv(0);
	}
	
	public void detectCollisionsInside(Rect box) {
		
		int dxdir = this.getSpeed().getxDirection();
		int dydir = this.getSpeed().getyDirection();
		int dright = this.getX() + this.getSpriteWidth();
		int dleft = this.getX();
		int dtop = this.getY();
		int dbottom = this.getY() + this.getSpriteHeight();
		
		// check collision with right wall
		if (dxdir == Speed.DIRECTION_RIGHT && dright >= box.right) 
			this.stopX();
		
		// check collision with left wall 
		if (dxdir == Speed.DIRECTION_LEFT && dleft <= box.left)
			this.stopX();		
		
		// check collision with bottom wall 
		if (dydir == Speed.DIRECTION_DOWN && dbottom >= box.bottom) 
			this.stopY();
		
		// check collision with top wall
		if (dydir == Speed.DIRECTION_UP && dtop <= box.top)
			this.stopY();
	}
	
	public void detectCollisionsOutside(Rect box) {		
		
		int dxdir = this.getSpeed().getxDirection();
		int dydir = this.getSpeed().getyDirection();
		int dright = this.getX() + this.getSpriteWidth();
		int dleft = this.getX();
		int dtop = this.getY() + this.getSpriteHeight() / 2; // only colliding with the lower half of the sprite 
		int dbottom = this.getY() + this.getSpriteHeight();
		
		
		// check collision with left wall if heading right
		if (dxdir == Speed.DIRECTION_RIGHT && dright >= box.left && dright < box.left + 2 
				&& ((dtop <= box.top && dbottom >= box.top) 
						|| (dtop >= box.top && dtop <= box.bottom))) {
			this.stopX();
		}
		
		
		// check collision with right wall if heading left
		if (dxdir == Speed.DIRECTION_LEFT && dleft <= box.right && dleft > box.right - 2
				&& ((dtop <= box.top && dbottom >= box.top) 
						|| (dtop >= box.top && dtop <= box.bottom))) {
			this.stopX();
		}
		
		// check collision with bottom wall if heading down
		if (dydir == Speed.DIRECTION_DOWN && dbottom >= box.top && dbottom < box.top + 2
				&& ((dleft <= box.left && dright >= box.left) 
						|| (dleft >= box.left && dleft <= box.right))) {
			this.stopY();
		}
		
		// check collision with top wall if heading up
		if (dydir == Speed.DIRECTION_UP && dtop <= box.bottom && dtop > box.bottom - 2
				&& ((dleft <= box.left && dright >= box.left) 
						|| (dleft >= box.left && dleft <= box.right))) {
			this.stopY();
		}
	}
	
	public void detectCollisions( ArrayList<Rect> obstacles ) {
		
		// Assuming framebox is in the first location
		detectCollisionsInside(obstacles.get(0));
		
		// Detecting collisions 
		for ( int i = 1; i < obstacles.size(); i++ ) {
			detectCollisionsOutside(obstacles.get(i));
		}		
	}
	
	public void draw( Canvas canvas ){
		//Where to draw the sprite
		Rect destRect = new Rect(getX(), getY(), getX() + spriteWidth, getY() + spriteHeight);
		canvas.drawBitmap(bitmap, sourceRect, destRect, null);
		
		if ( showSource ) {
			canvas.drawBitmap(bitmap, 20, 150, null);
			Paint paint = new Paint();
			paint.setARGB(50, 0, 255, 0);
			canvas.drawRect(20 + (currentFrame * destRect.width()),150, 
							20 + (currentFrame * destRect.width()) + destRect.width(), 
							150 + destRect.height(), paint);
		}
	}
	
	public void update( long gameTime, ArrayList<Rect> obstacles ){
		
		if ( animate ) {
			
			detectCollisions(obstacles);
			
			if (gameTime > frameTicker + framePeriod){
				frameTicker = gameTime;
				
				// increment the frame
				currentFrame++;
				if ( currentFrame >= frameNr ) {
					currentFrame = 0;
				}
			}
		}
		// define the rectangle to cut out sprite
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
		
		
		x += (speed.getXv() * speed.getxDirection());
		y += (speed.getYv() * speed.getyDirection());			
	}
	
	public void update( long gameTime, Rect frameBox ){
		
		if ( animate ) {
			
			detectCollisionsInside(frameBox);
			
			if (gameTime > frameTicker + framePeriod){
				frameTicker = gameTime;
				
				// increment the frame
				currentFrame++;
				if ( currentFrame >= frameNr ) {
					currentFrame = 0;
				}
			}
		}
		// define the rectangle to cut out sprite
		this.sourceRect.left = currentFrame * spriteWidth;
		this.sourceRect.right = this.sourceRect.left + spriteWidth;
		
		
		x += (speed.getXv() * speed.getxDirection());
		y += (speed.getYv() * speed.getyDirection());			
	}

}