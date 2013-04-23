package demos.fireworks;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.DisplayMetrics;
import android.util.Log;

import java.lang.Math;

import demos.shared.MainGamePanel;
import demos.shared.Speed;

public class Particle {
	
	//Tag for logging on Android's Log
	private static final String TAG = MainGamePanel.class.getSimpleName();

	public static final int STATE_ALIVE = 0;	// particle is alive
	public static final int STATE_DEAD = 1;		// particle is dead

	public static final int DEFAULT_LIFETIME 	= 200;	// play with this
	public static final int MAX_PARTICLE_SIZE	= 5;
	public static final int MAX_SPEED			= 1;	// maximum speed (per update)
	public static final int FADEOUT_FACTOR		= 2;
	
	private static int maxSize = MAX_PARTICLE_SIZE;	// the maximum width or height
	private static int fadeOutFactor = FADEOUT_FACTOR;		// the amount of decreasing alpha value
	private static int maxSpeed = MAX_SPEED;
	private static int default_lifetime = DEFAULT_LIFETIME;

	private int state;			// particle is alive or dead
	private float width;		// width of the particle
	private float height;		// height of the particle
	private float x, y;			// horizontal and vertical position
	private Speed speed;
	private int age;			// current age of the particle
	private int lifetime;		// particle dies when it reaches this value
	private int color;			// the color of the particle
	private Paint paint;		// internal use to avoid instantiation	
	
	public int getState() {
		return state;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
	
	public Speed getSpeed() {
		return speed;
	}

	public int getAge() {
		return age;
	}

	public int getLifetime() {
		return lifetime;
	}

	public int getColor() {
		return color;
	}

	public Paint getPaint() {
		return paint;
	}
	
	public void setState(int state) {
		this.state = state;
	}

	public void setWidth(float width) {
		this.width = width;
	}

	public void setHeight(float height) {
		this.height = height;
	}

	public void setX(float x) {
		this.x = x;
	}

	public void setY(float y) {
		this.y = y;
	}
	
	public void setSpeed(Speed speed){
		this.speed = speed;
	}

	public void setAge(int age) {
		this.age = age;
	}

	public void setLifetime(int lifetime) {
		this.lifetime = lifetime;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public void setPaint(Paint paint) {
		this.paint = paint;
	}
	
	//////////////////////////////////
	// Class Methods
	//////////////////////////////////
	
	public static int getMaxSize() {
		return Particle.maxSize;
	}
	
	public static int getFadeOutFactor() {
		return Particle.fadeOutFactor;
	}
	
	public static int getDefaultLifetime() {
		return Particle.default_lifetime;
	}
	
	public static int getMaxSpeed() {
		return Particle.maxSpeed;
	}
	
	public static void setMaxPartSize(int newMaxSize) {
		Particle.maxSize = newMaxSize;
	}
	
	public static void setFadeOutFactor(int newFactor) {
		Particle.fadeOutFactor = newFactor;
	}
	
	public static void setDefaultLifetime(int newValue) {
		Particle.default_lifetime = newValue;
	}
	
	public static void setMaxSpeed(int newValue) {
		Particle.maxSpeed = newValue;
	}
	
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////
	
	
	public Particle( int x, int y ){
		this.x = x;
		this.y = y;
		this.state = Particle.STATE_ALIVE;
		this.width = rndInt(1, maxSize);
		this.height = this.width;
		this.lifetime = DEFAULT_LIFETIME;
		this.age = 0;
		this.speed = new Speed();
		this.speed.random(0, MAX_SPEED);
		this.speed.smooth(MAX_SPEED, 0.7);
		Particle.maxSize = MAX_PARTICLE_SIZE;
		Particle.fadeOutFactor = FADEOUT_FACTOR;
		
		// Multicolor
		this.color = Color.argb(255, rndInt(0, 255), rndInt(0, 255), rndInt(0, 255));
		this.paint = new Paint(this.color);
	}
	
	public Particle( int x, int y, int maxSize, int fadeOutFactor, int defaultLifetime, int maxSpeed ){
		this.x = x;
		this.y = y;
		this.state = Particle.STATE_ALIVE;
		this.age = 0;
		
		if ( maxSize >= 0)
			Particle.maxSize = maxSize;
		else 
			Particle.maxSize = 1;
		
		this.width = rndInt(1, maxSize);
		this.height = this.width;

		Particle.fadeOutFactor = fadeOutFactor;
		
		if ( lifetime >= 0 ){
			this.lifetime = defaultLifetime;
			Particle.default_lifetime = defaultLifetime;
		} else {
			this.lifetime = 1;
			Particle.default_lifetime = 1;
		}
		
		this.speed = new Speed();
		this.speed.random(0, maxSpeed);
		this.speed.smooth(maxSpeed, 0.7);
		Particle.maxSpeed = maxSpeed;
		
		// Multicolor
		this.color = Color.argb(255, rndInt(0, 255), rndInt(0, 255), rndInt(0, 255));
		this.paint = new Paint(this.color);
	}
	
	// Return an integer that ranges from min inclusive to max inclusive.
	static int rndInt(int min, int max) {
		return (int) (min + Math.random() * (max - min + 1));
	}

	static double rndDbl(double min, double max) {
		return min + (max - min) * Math.random();
	}
	
	public void detectCollisions(Rect frameBox){
		
		// check collision with right wall if heading right
		if (this.getSpeed().getxDirection() == Speed.DIRECTION_RIGHT
				&& this.getX() + (this.getWidth() / 2.0) >= frameBox.right) {
			this.getSpeed().togglexDirection();
		}
		
		// check collision with left wall if heading left
		if (this.getSpeed().getxDirection() == Speed.DIRECTION_LEFT
				&& this.getX() - (this.getWidth() / 2.0) <= frameBox.left) {
			this.getSpeed().togglexDirection();
		}
		
		// check collision with bottom wall if heading down
		if (this.getSpeed().getyDirection() == Speed.DIRECTION_DOWN
				&& this.getY() + (this.getHeight() / 2.0) >= frameBox.bottom) {
			this.getSpeed().toggleyDirection();
		}
		// check collision with top wall if heading up
		if (this.getSpeed().getyDirection() == Speed.DIRECTION_UP
				&& this.getY() - (this.getHeight() / 2.0) <= frameBox.top) {
			this.getSpeed().toggleyDirection();
		}
	}
	
	public void draw(Canvas canvas){
		paint.setColor(this.color);
		canvas.drawRect(this.x, this.y, this.x + this.width, this.y + this.height, paint);
	}
	
	public void update(Rect frameBox){
		if ( this.state != STATE_DEAD ){
			
			// Detect collisions and make proper changes when necessary
			detectCollisions(frameBox);
			
			//Log.d(TAG, "x=" + this.x + " xv=" + this.speed.getXv());
			
			this.x += this.speed.getXv() * this.speed.getxDirection();
			this.y += this.speed.getYv() * this.speed.getyDirection();
			
			// extract alpha
			int a = this.color >>> 24;
			a -= fadeOutFactor;
			if ( a <= 0 ) {
				a = 0;
				this.state = STATE_DEAD;
				this.color = (this.color & 0x00ffffff) + (a << 24);
				this.paint.setAlpha(a);
			} else {
				this.color = (this.color & 0x00ffffff) + (a << 24);
				this.paint.setAlpha(a);
				this.age++; // increase the age of the particle
			}
			
			if ( this.age >= this.lifetime ) {
				this.state = STATE_DEAD;
				this.x += this.speed.getXv();
				this.y += this.speed.getYv();
			}
		}
	}
	
}
