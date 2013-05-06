package demos.glviewer;

import android.content.Context;
import android.opengl.GLSurfaceView;

public class GLViewer extends GLSurfaceView {

	public GLViewer(Context context) {
		super(context);
		
		// Create an OpenGL ES 2.0 context
		setEGLContextClientVersion(2);
		
		// Set the Renderer for drawing on the GLSurfaceView
        setRenderer(new GLRenderer());	
		
		// Render the view only when there is a change in the drawing data
		//This setting prevents the GLSurfaceView frame from being redrawn until you call requestRender(), which is more efficient for this sample app
		setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
		
		setFocusable(true);
	}

}