package demos.glviewer;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.view.MotionEvent;

public class GLViewer extends GLSurfaceView {

	private final GLRenderer mRenderer;
	private final float TOUCH_SCALE_FACTOR = 180.0f / 320;
	private float mPreviousX;
	private float mPreviousY;
	
	public GLViewer(Context context) {
		super(context);
		
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		
		// Set the Renderer for drawing on the GLSurfaceView
		mRenderer = new GLRenderer();
        setRenderer(mRenderer);	
		
		// Render the view only when there is a change in the drawing data
		//This setting prevents the GLSurfaceView frame from being redrawn until you call requestRender(), which is more efficient for this sample app
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		setFocusable(true);
	}

	@Override
	public boolean onTouchEvent(MotionEvent e) {
	    // MotionEvent reports input details from the touch screen
	    // and other input controls. In this case, you are only
	    // interested in events where the touch position changed.

	    float x = e.getX();
	    float y = e.getY();

	    switch (e.getAction()) {
	        case MotionEvent.ACTION_MOVE:

	            float dx = x - mPreviousX;
	            float dy = y - mPreviousY;

	            // Calculate the angle
	            //mRenderer.mAngle += (dx + dy) * TOUCH_SCALE_FACTOR;  // = 180.0f / 320
                mRenderer.mAngleX += dx * TOUCH_SCALE_FACTOR;
                mRenderer.mAngleY += dy * TOUCH_SCALE_FACTOR;
	            requestRender();
	    }

	    mPreviousX = x;
	    mPreviousY = y;
	    return true;
	}
}