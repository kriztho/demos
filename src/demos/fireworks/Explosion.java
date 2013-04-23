package demos.fireworks;

import android.graphics.Canvas;
import android.graphics.Rect;


public class Explosion {

	public static final int STATE_ALIVE = 0;   	// at least 1 particle is alive
	public static final int STATE_DEAD  = 1;	// all particles are dead
	
	private Particle[] particles;
	private int x, y;
	private int size;
	private int state;
	
	public Explosion( int particleNr, int x, int y) {
		this.state = STATE_ALIVE;
		this.particles = new Particle[particleNr];
		
		for( int i = 0; i < this.particles.length; i++ ) {
			Particle p = new Particle(x,y);
			this.particles[i] = p;
		}
		this.size = particleNr;
	}
	
	public Explosion( int particleNr, int x, int y, int maxSize, int fadeFactor, int defaultLifetime, int maxSpeed) {
		this.state = STATE_ALIVE;
		this.particles = new Particle[particleNr];
		
		for( int i = 0; i < this.particles.length; i++ ) {
			Particle p = new Particle(x,y,maxSize,fadeFactor, defaultLifetime, maxSpeed);
			this.particles[i] = p;
		}
		this.size = particleNr;
	}

	public void draw(Canvas canvas){
		// Draw all particles
		for( int i = 0; i < this.particles.length; i++ ) {			
			this.particles[i].draw(canvas);
		}
	}
	
	public void update(Rect frameBox) {
		// Update all particles
		for( int i = 0; i < this.particles.length; i++ ) {			
			this.particles[i].update(frameBox);
		}
	}
	
	/////////////////////////////////////
	// Class Method Wrappers
	/////////////////////////////////////
	
	public static int getMaxPartSize() {
		return Particle.getMaxSize();
	}
	
	public static void setMaxPartSize(int newSize) {
		Particle.setMaxPartSize(newSize);
	}
	
	public static int getDefaultLifetime() {
		return Particle.getDefaultLifetime();
	}
	
	public static void setDefaultLifetime(int newValue) {
		Particle.setDefaultLifetime(newValue);
	}
	
	public static int getMaxPartSpeed() {
		return Particle.getMaxSpeed();
	}
	
	public static void setMaxPartSpeed(int newValue) {
		Particle.setMaxSpeed(newValue);
	}
	
	public static int getFadeOutFactor() {
		return Particle.getFadeOutFactor();
	}
	
	public static void setFadeOutFactor(int newFactor) {
		Particle.setFadeOutFactor(newFactor);
	} 
}
