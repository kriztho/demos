package demos.droids;

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

public class DroidzActivity extends Activity {
	
	private static final String TAG = DroidzActivity.class.getSimpleName();
	private static final int REQUEST_CODE = 1;
	private Droidz gamePanel;
	private String aboutMsg;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		//request to turn the title OFF
		//requestWindowFeature(Window.FEATURE_NO_TITLE);
		//making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		aboutMsg = "Please define a msg on the preferences";
		gamePanel = new Droidz(getApplicationContext());
		setContentView(gamePanel);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_droidz, menu);
		return true;
	}
	
	public void makeToast(CharSequence text) {
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void showSettings() {
		//makeToast("Settings Page!");
		Intent preference = new Intent(getApplicationContext(), PrefDroidz.class);
		
		preference.putExtra("speedFactor", gamePanel.getAnimationSpeedFactor());
		preference.putExtra("numberDroids", gamePanel.getCurrentNumberDroids());
		//preference.putExtra("fps", gamePanel.getAvgFps());
		//startActivity(preference);
		startActivityForResult(preference, REQUEST_CODE);
	}
	
	public void showAbout() {
		makeToast(aboutMsg);		
	}
	
	 @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		 
		 super.onActivityResult(requestCode, resultCode, data);
		  
        //use a switch statement for more than one request code check
       if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
    	   
    	   Bundle extras = data.getExtras();
    	   
    	   if ( extras != null ) {
    		   int speedFactor = extras.getInt("speedFactor", -1);
    		   
    		   if( speedFactor != -1 ) {    		   
        		   gamePanel.setAnimationSpeedFactor(speedFactor);
        		   //makeToast(""+speedFactor+"X");
        	   } else {
        		   makeToast("Error at receiving speedFactor");
        	   }
    		   
    		   int numberDroids = extras.getInt("numberDroids", -1);
    		   if( numberDroids != -1 ) {    		   
        		   gamePanel.setCurrentNumberDroids(numberDroids);
        		   //makeToast(""+numberDroids+" Droids");
        	   } else {
        		   makeToast("Error at receiving numberDroids");
        	   }
    		   
    		   aboutMsg = extras.getString("aboutMsg");
    		   if ( aboutMsg == null ) {
    			   //makeToast("Msg is null");
    			   aboutMsg = "Message was null";
    		   }
    	   }
       }       
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
