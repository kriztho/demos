package demos.fireworks;

import com.demos.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.WindowManager;

public class FireworksPrefs extends Activity {
	
	// Fireworks setup
	private int fireworksSize;
	
	// Explosion setup
	private int explosionSize;
	private int explosionDefaultLifeTime;
	private int explosionColor;
	
	// Particle setup
	private int particleFadeOutTime;
	private int particleMaxWidth;
	private int particleMaxHeight;
	private int particleMaxRandomSpeed;
	private boolean particleUseRandomSpeed;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_fireworks_prefs);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_fireworks_prefs, menu);
		
		//addPreferencesFromResource(R.layout.activity_pref_droidz);
		
		//making it full screen
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
		
		//reading information passed to this activity
        //Get the intent that started this activity
        Intent i = getIntent();
        
        //returns -1 if not initialized by calling activity        
        fireworksSize = i.getIntExtra("fireworksNumber", 0);
        explosionSize = i.getIntExtra("explosionsNumber", 0);
        explosionDefaultLifeTime = i.getIntExtra("explosionDefaultLifeTime", 0);
        explosionColor = i.getIntExtra("explosionColor", 0);
        particleFadeOutTime = i.getIntExtra("particleFadeOutTime", 0);
        particleMaxWidth = i.getIntExtra("particleMaxWidth", 0);
        particleMaxHeight = i.getIntExtra("particleMaxHeight", 0);
        particleMaxRandomSpeed = i.getIntExtra("particleMaxRandomSpeed", 0);
        particleUseRandomSpeed = i.getBooleanExtra("particleUseRandomSpeed", false);
		
        return true;
	}

}
