package demos.glviewer;

import android.app.Activity;
import android.os.Bundle;
import android.view.WindowManager;

public class GLViewerActivity extends Activity {
	
	private static final String TAG = GLViewer.class.getSimpleName();
	private GLViewer gamePanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		gamePanel = new GLViewer(this);
		setContentView(gamePanel);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}
}
