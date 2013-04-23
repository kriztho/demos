package demos.shared;

import java.text.DecimalFormat;

import android.util.Log;
import android.view.SurfaceHolder;
import android.graphics.Canvas;

public class MainThread extends Thread {
	
	//Tag for logging on Android's Log
	private static final String TAG = MainThread.class.getSimpleName();
	
	// desired fps
	private final static int MAX_FPS = 50;
	// maximum number of frames to be skipped
	private final static int MAX_FRAME_SKIPS = 5;
	// the frame period
	private final static int FRAME_PERIOD = 1000 / MAX_FPS;
	
	// Stuff for stats
	private DecimalFormat df = new DecimalFormat("0.##");
	// we'll be reading the stats every second
	private final static int STAT_INTERVAL = 1000;
	// the average will be calculated by storing 
	// the last n FPSs
	private final static int FPS_HISTORY_NR = 10;
	// last time the status was stored
	private long lastStatusStore = 0;
	// the status time counter
	private long statusIntervalTimer = 0l;
	//number of frames skipped since the game started
	private long totalFramesSkipped = 0l;
	// number of frames skipped in a store cycle (1 sec)
	private long framesSkippedPerStatCycle = 0l;
	
	// number of rendered frames in an interval
	private int frameCountPerStatCycle = 0;
	private long totalFrameCount = 0l;
	// the last FPS values
	private double fpsStore[];
	// the number of times the stat has been read
	private long statsCount = 0;
	// the average FPS since the game started
	private double averageFps = 0.0;
	
	//To be able to lock the surface when we draw
	private SurfaceHolder surfaceHolder;
	//To be able to draw on to the surface
	private MainGamePanel gamePanel;
	//flag to hold game state
	private boolean running;
	
	// flag to hold game state
	public void setRunning(boolean running) {
		this.running = running;
	}
	
	//Constructor
	public MainThread(SurfaceHolder surfaceHolder, MainGamePanel gamePanel) {
		super(); //constructor of the parent class Thread
		this.surfaceHolder = surfaceHolder;
		this.gamePanel = gamePanel;
	}
	
	@Override
	public void run() {
		Canvas canvas;
		//long tickCount = 0L;
		//Log.d(TAG, "Starting game loop");
		
		// initialise timing elements for stat gathering
		initTimingElements();
		
		long beginTime;		// the time when the cycle began
		long timeDiff;		// the time it took for the cycle to execute
		//long timeAhead;
		int sleepTime;		// ms to skeep ( <0 if we're behind )
		int framesSkipped;	// number of frames being skipped
		//int updateTime;
		//int renderTime;
		
		sleepTime = 0;
		//timeAhead = 0l;
		
		//Thread loop that will keep on drawing to the canvas
		while (running) {
			canvas = null;
			// try locking the canvas for exclusive pixel editing 
			// on the surface			
			try {
				
				//Log.d(TAG," Loop ");
				
				canvas = this.surfaceHolder.lockCanvas();
				synchronized (surfaceHolder) {
					
					beginTime = System.currentTimeMillis();
						framesSkipped = 0; // resetting the frames skipped
						// Update game state
						this.gamePanel.update();
					
					//updateTime = (int)(System.currentTimeMillis() - beginTime);
					
						// render state to the screen
						// draws the canvas on the panel
						this.gamePanel.render(canvas);
					
					//renderTime = (int)(System.currentTimeMillis() - beginTime);
					
					// calculate how long did the cycle take
					timeDiff = System.currentTimeMillis() - beginTime;										
					
					// calculate sleep time
					//Corrects timing every 2 frames
					//sleepTime = (int)(FRAME_PERIOD - timeDiff - timeAhead);
					sleepTime = (int)(FRAME_PERIOD - timeDiff);
					
					//Log.d(TAG,"U + R = " + timeDiff + " ms / " + FRAME_PERIOD + " ms / " + STAT_INTERVAL + " ms " + sleepTime + " ms");
					
					// Where it really should have started
					// Corrects updating every 2 frames
					//beginTime += timeAhead;
					
					if ( sleepTime > 0 ){
						try {
							// send the thread to sleep for a short period
							// very useful for battery saving
							//Log.d(TAG,"Thread went to sleep for: " + sleepTime + " ms");
							Thread.sleep(sleepTime);
						} catch (InterruptedException e){}
					}
					
					
					while (sleepTime < 0 && framesSkipped < MAX_FRAME_SKIPS) {
	                    // we need to catch up
	                    // update without rendering
	                    this.gamePanel.update();
	                    // add frame period to check if in next frame
	                    sleepTime += FRAME_PERIOD;	                    
	                    //Log.d(TAG," sleepTime: " + sleepTime + "framesSkipped: " + framesSkipped );
	                    framesSkipped++;
					}
					
					/*
					// NOTE
					// Assuming that neither Update or Rendering take more than a Period to execute
					// This approach sleeps the remainder of the period before the next one starts on time
					// Catches up every second frame
					if ( sleepTime < 0 ) {
						// we need to catch up
						// update without rendering
						this.gamePanel.update();
						
						//add frame period to check if in next frame
						sleepTime += (FRAME_PERIOD - updateTime);
						
						if ( sleepTime > 0 ){
							try {
								// send the thread to sleep for a short period
								// very useful for battery saving
								Thread.sleep(sleepTime);
							} catch (InterruptedException e){}
						}
						
						// Rendering frames
						framesSkipped++;
					}
					*/
					
					/*
					// NOTE
					// Assuming that neither Update or Rendering take more than a Period to execute
					// Catches up every third frame
					if ( sleepTime < 0 ) {
						// we need to catch up
						// update without rendering				
						this.gamePanel.update();
						
						timeAhead = (beginTime + 2*FRAME_PERIOD) - System.currentTimeMillis();
						
						// Rendering frames
						framesSkipped++;
					}
					*/
					
					if ( framesSkipped > 0 ){
						Log.d(TAG, "Skipped: " + framesSkipped);						
					}
					// for statistics
					framesSkippedPerStatCycle += framesSkipped;
					// calling the routine to store the gathered statistics
					storeStats();
				}
			} catch (Exception e) {
			     // This will catch any exception, because they are all descended from Exception
				Log.d(TAG, "Exception: "+e.toString()+ "Cause: "+e.getCause());
			} finally {
				//in case of an exception the surface is not left in an inconsistent state
				if (canvas != null) {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			} //end finally
			//tickCount++;			
		}
		//Log.d(TAG,"Game loop executed " + tickCount + " times");
	}
	
	/**
	 * The statistics - it is called every cycle, it checks if time since last 
	 * store is greater than the statistics gathering period (1 sec) and if so
	 * it calculates the FPS for the last period and stores it.
	 * 
	 * It tracks the number of frames per period. The number of frames since
	 * the start of the period are summed up and the calculation takes part
	 * only if the next period and the frame count is reset to 0.
	 */
	private void storeStats() {
		frameCountPerStatCycle++;
		totalFrameCount++;
		
		// check the actual time
		statusIntervalTimer += (System.currentTimeMillis() - statusIntervalTimer);
		
		if ( statusIntervalTimer >= (lastStatusStore + STAT_INTERVAL )) {
			// calculate the actual frames per status check interval
			double actualFps = (double)(frameCountPerStatCycle / (STAT_INTERVAL / 1000));
			
			// stores the latest fps in the array
			fpsStore[(int) statsCount % FPS_HISTORY_NR] = actualFps;
			
			// increase the number of times statistics was calculated
			statsCount++;
			
			double totalFps = 0.0;
			// sum up the stored fps values
			for ( int i = 0; i < FPS_HISTORY_NR; i++ ){
				totalFps += fpsStore[i];
			}
			
			// obtain the average
			if ( statsCount < FPS_HISTORY_NR ) {
				//in case of the first 10 triggers
				averageFps = totalFps / statsCount;
			} else {
				averageFps = totalFps / FPS_HISTORY_NR;
			}
			
			// saving the number of total frames skipped
			totalFramesSkipped += framesSkippedPerStatCycle;
			
			// resetting the counters after a status record (1 sec)
			framesSkippedPerStatCycle = 0;
			statusIntervalTimer = 0;
			frameCountPerStatCycle = 0;
			
			statusIntervalTimer = System.currentTimeMillis();
			lastStatusStore = statusIntervalTimer;
			//Log.d(TAG, "Average FPS: " + df.format(averageFps));
			gamePanel.setAvgFps(averageFps);
		}
	}
	
	private void initTimingElements() {
		// init timing elements
		fpsStore = new double[FPS_HISTORY_NR];
		for ( int i = 0; i < FPS_HISTORY_NR; i++ ){
			fpsStore[i] = 0.0;
		}
		Log.d(TAG, "initTimingElements()", null);
	}
}
