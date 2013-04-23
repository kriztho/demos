package demos.shared;

import com.demos.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.widget.LinearLayout;

public class Preferences extends PreferenceActivity {
	
	private LinearLayout layout = new LinearLayout(this);

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		getWindow().setTitle("Preferences");
		
		Intent i = getIntent();
		
		int demoId = i.getIntExtra("demoId", -1);
		
		setContentView(R.layout.activity_preferences);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_preferences, menu);
		return true;
	}

}
