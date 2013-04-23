package demos.fireworks;

import demos.fireworks.Explosion;
import demos.fireworks.Particle;
import demos.shared.FloatingDisplay;
import demos.shared.MainGamePanel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;

public class Fireworks extends MainGamePanel implements 
SurfaceHolder.Callback {
	
	//Tag for logging on Android's Log
	private static final String TAG = Fireworks.class.getSimpleName();
	private static final int MAX_FIREWORKS_SIZE = 100;
	private static final int MAX_EXPLOSIONS_SIZE = 30;
	
	private int currentFireworksSize;
	private Explosion[] fireworks;
	private Explosion explosion;
	private int index;	
	private int explosionSize;
	private Particle particle;
	
	private FloatingDisplay floatingDisplay;
	
	// wrapper variables because class objects are destroyed and created everytime
	private int maxPartSize;
	private int fadeOutFactor;
	private int defaultLifetime;
	private int maxPartSpeed;

	public Fireworks(Context context) {
		super(context);
		
		// adding the callback (this) to the surface holder to intercept events
		getHolder().addCallback(this);
		
		//Create droid and load bitmap
		currentFireworksSize = 20;
		fireworks = new Explosion[MAX_FIREWORKS_SIZE];
		index = 0;
		
		explosionSize = 10;
		maxPartSize = 5;
		fadeOutFactor = 2;
		defaultLifetime = 200;
		maxPartSpeed = 1;
		
		// make the GamePanel focusable so it can handle events
		setFocusable(true);
	}
	
	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		super.surfaceCreated(holder);
		
		//Heads up display
		floatingDisplay = new FloatingDisplay(2, "bottomleft", Color.WHITE, getWidth(), getHeight());
		floatingDisplay.addParam("Exp", currentFireworksSize);
		floatingDisplay.addParam("Part", explosionSize);
		floatingDisplay.addParam("Size", maxPartSize);
		floatingDisplay.addParam("Fade", fadeOutFactor);
		floatingDisplay.addParam("Age", defaultLifetime);
		floatingDisplay.addParam("Speed", maxPartSpeed);
	}

	 @Override
	 public boolean onTouchEvent(MotionEvent event) {
		 
		 if (event.getAction() == MotionEvent.ACTION_DOWN) {
			 
			 //addExplosion((int)(event.getX()), (int)(event.getY()));
			 addExplosion((int)(event.getX()), (int)(event.getY()));
			 
		 } if (event.getAction() == MotionEvent.ACTION_MOVE ) {
			 
			 //addExplosion((int)(event.getX()), (int)(event.getY()));
			 addExplosion((int)(event.getX()), (int)(event.getY()));
			 
		 } if (event.getAction() == MotionEvent.ACTION_UP ){
			 
		 }
		 
	  return true;
	 }
	 
	 public void render(Canvas canvas) {
		
		 //fills the canvas with black 
		canvas.drawColor(Color.BLACK);
		
		// Draw fireworks
		drawFireworks(canvas);
		
		//displayFps(canvas, avgFps);
		if ( !floatingFPS.display(canvas) )
			makeToast("Error. There was a problem displaying FPS");
		if ( !floatingDisplay.display(canvas) )
			makeToast("Error. There was a problem with floating display");
	 }
	 
	 public void drawFireworks(Canvas canvas) {
		 
		 Paint paint = new Paint();
		 paint.setARGB(255, 0, 255, 0);
		 paint.setStyle(Paint.Style.STROKE);
		 canvas.drawRect(frameBox, paint);
		 
		// Drawing all explosions in the array
		 for ( int i = 0; i < currentFireworksSize; i++ ) {
			 if (fireworks[i] != null)
				 fireworks[i].draw(canvas);
		 }
	 }
	 
	 public void updateParticle(){
		
		// Framing box for collision detection
		Rect frameBox = new Rect(0, 0, getWidth(), getHeight());
		particle.update(frameBox);
	 }

	 public void updateExplosion(){		 
		 explosion.update(frameBox);
	 }
	 
	 public void updateFireworks() {
		 
		 // Updating all explosions in the array
		 for ( int i = 0; i < currentFireworksSize; i++ ) {
			 if (fireworks[i] != null){
				 fireworks[i].update(frameBox);
			 }
		 }
	 }
	 
	 public void update() {

		 updateFireworks();
	 }
	 
	 public void addExplosion(int x, int y) {
		// Populating the array and reusing the objects inside
		 // Using the garbage collector to put back the memory as it always creates new objects
		 try {
		fireworks[index] = new Explosion(explosionSize, x, y, maxPartSize, fadeOutFactor, defaultLifetime, maxPartSpeed);
		index++;
		if ( index == currentFireworksSize)
			index = 0;
		 } catch (Exception e) {
			 Log.d(TAG, "Exception: " + e.toString() + "at: " + index);
		 }
	 }
	 
	 public void setFireworksSize(int newFireworksSize) {
		 
		// Add Explosions
		 if ( newFireworksSize > currentFireworksSize ) {
			 
			 if ( newFireworksSize <= MAX_FIREWORKS_SIZE ) {			 
				 currentFireworksSize = newFireworksSize;				 
			 } else {
				 
				 if ( currentFireworksSize < MAX_FIREWORKS_SIZE ) {
					 currentFireworksSize = MAX_FIREWORKS_SIZE;
					 makeToast("Max Number Of Explosions Exceeded!. " +
					 		"Only "+(newFireworksSize - MAX_FIREWORKS_SIZE)+" were added.");
				 } else {
					 makeToast("No more explosions were added.");
				 }
			 }			 
			 index = 0;
			 
			 if ( !floatingDisplay.updateParam("Exp", currentFireworksSize))
				 makeToast("Param Explosions couldn't be found");
			 
		 } else if (newFireworksSize < currentFireworksSize ) {
			 
			 if ( newFireworksSize >= 0 ) {
				 currentFireworksSize = newFireworksSize;
			 } else {
				 
				 if ( currentFireworksSize > 0 ) {
					 currentFireworksSize = 0;
					 makeToast("Min Number Of Explosions Exceeded!. " +
					 		"Only "+(newFireworksSize - MAX_FIREWORKS_SIZE)+" were deleted");
				 } else {
					 makeToast("No more explosions were deleted.");
				 }
			 }
			 index = 0;
			 
			 if ( !floatingDisplay.updateParam("Exp", currentFireworksSize))
				 makeToast("Param Explosions couldn't be found");
		 }
	 }
	 
	 public void setExplosionSize(int newSize){
		 explosionSize = newSize;
		 
		 if ( !floatingDisplay.updateParam("Part", explosionSize))
			 makeToast("Param Part couldn't be found");
	 }
	 
	 public void setMaxPartSize(int newSize){
		 maxPartSize = newSize;
		 
		 if ( !floatingDisplay.updateParam("Size", maxPartSize))
			 makeToast("Param Size couldn't be found");
	 }
	 
	 public void setFadeOutFactor(int newFactor){
		 fadeOutFactor = newFactor;
		 
		 if ( !floatingDisplay.updateParam("Fade", fadeOutFactor))
			 makeToast("Param Fade couldn't be found");
	 }
	 
	 public void setDefaultLifetime(int newValue){
		 defaultLifetime = newValue;
		 
		 if ( !floatingDisplay.updateParam("Age", defaultLifetime))
			 makeToast("Param Fade couldn't be found");
	 }
	 
	 public void setMaxPartSpeed(int newValue){
		 maxPartSpeed = newValue;
		 
		 if ( !floatingDisplay.updateParam("Speed", maxPartSpeed))
			 makeToast("Param Fade couldn't be found");
	 }
	 
	 public int getFireworksSize() {
		 return currentFireworksSize;
	 }
	 
	 public int getExplosionSize() {		 
		 return explosionSize;
	 }
	 
	 public int getMaxPartSize() {
		 return maxPartSize;
	 }
	 
	 public int getFadeOutFactor() {
		 return fadeOutFactor;
	 }
	 
	 public int getDefaultLifetime() {
		 return defaultLifetime;
	 }
	 
	 public int getMaxPartSpeed() {
		 return maxPartSpeed;
	 }
}
