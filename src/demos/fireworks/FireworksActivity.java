package demos.fireworks;

import com.demos.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

public class FireworksActivity extends Activity {
	
	private static final String TAG = FireworksActivity.class.getSimpleName();
	private Fireworks gamePanel;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//request to turn the title OFF
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		gamePanel = new Fireworks(getApplicationContext());
		setContentView(gamePanel);			
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_fireworks, menu);
		return true;
	}

	public void makeToast(CharSequence text) {
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void updateFireworks(int option) {
		
		int size = gamePanel.getFireworksSize();
		
		// Increase fireworks
		if ( option == 0 ) {
			size+=10;
			gamePanel.setFireworksSize(size);
		} else { // decrease fireworks
			size-=10;
			gamePanel.setFireworksSize(size);
		}
	}
	
	public void updateExplosions(int option) {
		
		int size = gamePanel.getExplosionSize();
		
		// Increase fireworks
		if ( option == 0 ) {
			size+=5;
			gamePanel.setExplosionSize(size);
		} else { // decrease fireworks
			size-=5;
			gamePanel.setExplosionSize(size);
		}
	}

	public void updateMaxPartSize(int option) {
	
		int size = gamePanel.getMaxPartSize();
		
		// Increase fireworks
		if ( option == 0 ) {
			size+=2;
			gamePanel.setMaxPartSize(size);
		} else { // decrease fireworks
			size-=2;
			gamePanel.setMaxPartSize(size);
		}
	}
	
	public void updateFadeOutFactor(int option) {
		
		int size = gamePanel.getFadeOutFactor();
		
		// Increase fireworks
		if ( option == 0 ) {
			size+=1;
			gamePanel.setFadeOutFactor(size);
		} else { // decrease fireworks
			size-=1;
			gamePanel.setFadeOutFactor(size);
		}
		
		makeToast(""+size);
	}
	
	public void updateDefaultLifetime(int option) {
		
		int size = gamePanel.getDefaultLifetime();
		
		// Increase fireworks
		if ( option == 0 ) {
			size+=25;
			gamePanel.setDefaultLifetime(size);
		} else { // decrease fireworks
			size-=25;
			gamePanel.setDefaultLifetime(size);
		}
		
		makeToast(""+size);
	}

	public void updateMaxPartSpeed(int option) {
	
		int size = gamePanel.getMaxPartSpeed();
		
		// Increase fireworks
		if ( option == 0 ) {
			size+=1;
			gamePanel.setMaxPartSpeed(size);
		} else { // decrease fireworks
			size-=1;
			gamePanel.setMaxPartSpeed(size);
		}
		
		makeToast(""+size);
	}
	
	public void setDefaultMode(int option) {
		
		// Increase fireworks
		if ( option == 0 ) {
			gamePanel.setFireworksSize(100);
			gamePanel.setExplosionSize(15);
			gamePanel.setMaxPartSize(3);
			gamePanel.setFadeOutFactor(1);
			gamePanel.setDefaultLifetime(5);
			gamePanel.setMaxPartSpeed(1);
		} 
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	        case R.id.incFireworks:
	            updateFireworks(0); // increase
	            break;
	        case R.id.decFireworks:
	            updateFireworks(1); // decrease
	            break;
	        case R.id.incExplosions:
	        	updateExplosions(0);
	        	break;
	        case R.id.decExplosions:
	        	updateExplosions(1);
	        	break;
	        case R.id.incMaxPartSize:
	        	updateMaxPartSize(0);
	        	break;
	        case R.id.decMaxPartSize:
	        	updateMaxPartSize(1);
	        	break;
	        case R.id.incFadeOutFactor:
	        	updateFadeOutFactor(0);
	        	break;
	        case R.id.decFadeOutFactor:
	        	updateFadeOutFactor(1);
	        	break;
	        case R.id.incDefaultLifetime:
	        	updateDefaultLifetime(0);
	        	break;
	        case R.id.decDefaultLifetime:
	        	updateDefaultLifetime(1);
	        	break;
	        case R.id.incMaxPartSpeed:
	        	updateMaxPartSpeed(0);
	        	break;
	        case R.id.decMaxPartSpeed:
	        	updateMaxPartSpeed(1);
	        	break;
	        case R.id.setDrawMode:
	        	setDefaultMode(0);
	        	break;		        
	        
	        default:
	            return super.onOptionsItemSelected(item);	            	     
	    }
	    
	    return true;
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
