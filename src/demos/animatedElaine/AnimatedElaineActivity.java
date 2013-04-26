package demos.animatedElaine;

import com.demos.R;

import demos.droids.PrefDroidz;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class AnimatedElaineActivity extends Activity {
	
	private static final String TAG = AnimatedElaine.class.getSimpleName();
	private AnimatedElaine gamePanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
		
		//request to turn the title OFF
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		//making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);		
		
		gamePanel = new AnimatedElaine(getApplicationContext());
		setContentView(gamePanel);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_animated_elaine, menu);
		return true;
	}
	
	public void makeToast(CharSequence text) {
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void showSettings() {
		//makeToast("Settings Page!");
		Intent preference = new Intent(getApplicationContext(), PrefDroidz.class);
		
		makeToast("Speed: "+gamePanel.getAvgFps());
		
		preference.putExtra("speed", gamePanel.getAvgFps());
		//preference.putExtra("numberDroids", gamePanel.get);
		//preference.putExtra("fps", gamePanel.getAvgFps());
		startActivity(preference);
	}
	
	public void showAbout() {
		//makeToast("About Page");
		makeToast("Speed: "+gamePanel.getAvgFps());
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.settings:	        	
	            showSettings();
	            return true;
	        case R.id.about:	        	
	            showAbout();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}

	@Override
	protected void onDestroy(){
		Log.d(TAG, "Destroying...");
		finish();
		super.onDestroy();
	}
	
	@Override
	protected void onStop(){
		Log.d(TAG, "Stopping...");
		super.onStop();
	}
}
