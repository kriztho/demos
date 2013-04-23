package demos.droids;

import com.demos.R;

import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class PrefDroidz extends PreferenceActivity implements OnPreferenceClickListener {
	
	private int speedFactor;
	private int numberDroids;
	//private float fps;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.layout.activity_pref_droidz);
		
		//making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//reading information passed to this activity
        //Get the intent that started this activity
        Intent i = getIntent();
        
        //returns -1 if not initialized by calling activity        
        speedFactor = i.getIntExtra("speedFactor", 0);
        numberDroids = i.getIntExtra("numberDroids", 0);
        //int fps = i.getIntExtra("fps", -1); 
        
        /*
        Preference pref = findPreference("txtViewSpeedFactor");
        pref.setTitle(""+speedFactor);
        */
        
        //makeToast("Current Speed Factor: " + speedFactor);
        //makeToast("Droids: " + numberDroids);
	}	
	

	@Override
	public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen,
			Preference preference) {
		
		Intent data = new Intent();
		
		//Animation Speed Factor
		if ( preference.equals(findPreference("increaseSpeed")) ) {
			speedFactor++;
		}
		if ( preference.equals(findPreference("decreaseSpeed")) ) {
			speedFactor--;
			if ( speedFactor < 0 )
				speedFactor = 0;
		}
		
		//Number of Droids
		if ( preference.equals(findPreference("increaseDroids")) ) {
			numberDroids++;
			if ( numberDroids > 10){
				numberDroids = 10;
				makeToast("Maximum number of Droids reached!");
			}
		}
		if ( preference.equals(findPreference("decreaseDroids")) ) {
			numberDroids--;
			if ( numberDroids < 0 ) {
				numberDroids = 0;
				makeToast("No more Droids to delete");
			}
		}
		
		data.putExtra("speedFactor", speedFactor);
		data.putExtra("numberDroids", numberDroids);
		setResult(RESULT_OK, data);
		
		return super.onPreferenceTreeClick(preferenceScreen, preference);
	}

	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	}


	
	@Override
	protected void onDestroy() {
		finish();
		super.onDestroy();
	}

	@Override
	public boolean onPreferenceClick(Preference preference) {
		
		makeToast("OnPreferenceClick");			
		
		return false;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		//menu.add(Menu.NONE, 0, 0, "Show current settings");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 0:
            	makeToast("Maldita sea!");
                //startActivity(new Intent(this, ShowSettingsActivity.class));
                return true;
        }
        return false;
    }
	
	public void makeToast(CharSequence text) {
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
	}
}