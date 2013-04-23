package demos.shared;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Toast;
import android.util.Log;

public class MainGamePanel extends SurfaceView implements 
  SurfaceHolder.Callback {
	
	//Tag for logging on Android's Log
	private static final String TAG = MainGamePanel.class.getSimpleName();
	
	//Main Thread of the Game
	protected Context appContext;
	private MainThread thread;
	protected Rect frameBox;
	protected double avgFps;
	protected String[] floatingInfoArray;
	protected String floatingInfo;
	protected FloatingDisplay floatingFPS;

	public MainGamePanel(Context context) {	
		super(context);
		
		appContext = context;
	}
	
	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
	}

	@Override
	public void setZOrderMediaOverlay(boolean isMediaOverlay) {
		// TODO Auto-generated method stub
		super.setZOrderMediaOverlay(isMediaOverlay);
	}

	@Override
	public void setZOrderOnTop(boolean onTop) {
		// TODO Auto-generated method stub
		super.setZOrderOnTop(onTop);
	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) { 
		 
		 if ( thread != null ){
			 if( thread.getState() == Thread.State.TERMINATED ) {
				 Log.d(TAG, "Thread is TERMINATED");
					 
				//Create the game loop thread
				thread = new MainThread(getHolder(), this);
			} else {
				Log.d(TAG, "Thread is ALIVE");
			}
		} else {
			//Create the game loop thread
			thread = new MainThread(getHolder(), this);
		}
		 
		 thread.setRunning(true);
		 thread.start();
		 
		// Framing box for collisiong detection
		frameBox = new Rect(10, 10, getWidth()-10, getHeight()-30);
		
		//Init floating
		floatingFPS = new FloatingDisplay(1, "topright", Color.WHITE, getWidth(), getHeight());
		floatingFPS.addParam("fps", 0);
	 }

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		
		 //tell the thread to shut down and wait for it to finish
		 //this is a clean shutdown
		 boolean retry = true;
		 while ( retry ) {
			 try {
				 //Stopping the thread loop
				 thread.setRunning(false);
				 //Stopping the activity before joining the thread
				 //((Activity)getContext()).finish();
				 
				 //Joining the thread
				 thread.join();
				 retry = false;			
				 
			 } catch ( InterruptedException e) {
				 //try again shutting down the thread
				 Log.d(TAG, "Excepction caught: "+ e.getMessage());
			 }
		 }
		 Log.d(TAG, "Thread was shut down cleanly");
	 }

	@Override
	public boolean onTouchEvent(MotionEvent event){
		 return true;
	 }
	 
	public double getAvgFps() {
		return avgFps;
	}
	
	public void setAvgFps( double avgFps ) {
		 this.avgFps = avgFps;
		 floatingFPS.updateParam("fps", this.avgFps);
	}
	 
	public void render(Canvas canvas){}
	 
	public void update(){}
	
	protected void makeToast(CharSequence text) {
		Toast toast = Toast.makeText(appContext, text, Toast.LENGTH_SHORT);
		toast.show();
	}
}