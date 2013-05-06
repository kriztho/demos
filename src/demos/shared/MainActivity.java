package demos.shared;

import com.demos.R;

import demos.animatedElaine.AnimatedElaineActivity;
import demos.droids.DroidzActivity;
import demos.fireworks.FireworksActivity;
import demos.glviewer.GLViewerActivity;
import android.os.Bundle;
import android.app.ListActivity;
import android.content.Intent;
import android.graphics.Color;
import android.view.Menu;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

//Called when the activity is created
public class MainActivity extends ListActivity implements OnItemClickListener {
	
	private static final String TAG = MainActivity.class.getSimpleName();
	/*
	private ListView listViewDemos;
	private ArrayAdapter<String> dataAdapterDemos;
	private ArrayList<String> dataDemos;
	*/

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		
		super.onCreate(savedInstanceState);
		
		/*
		// Create LinearLayout
		LinearLayout layout = new LinearLayout(this);
		layout.setOrientation(LinearLayout.VERTICAL);
		layout.setLayoutParams(new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.FILL_PARENT, 
				LinearLayout.LayoutParams.FILL_PARENT));
		
		// Create ArrayList
		dataDemos = new ArrayList<String>();
		dataDemos.add("1. Droidz");
		dataDemos.add("2. Animated Elaine");
		dataDemos.add("3. Fireworks");
		
		// Create the adapter
		dataAdapterDemos = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
		
		// Create view
		listViewDemos = new ListView(this);
		listViewDemos.setAdapter(dataAdapterDemos);
		listViewDemos.setOnItemClickListener(this);
		*/
		
		// Creating label
		TextView txtViewTitle = new TextView(this);
		txtViewTitle.setText("Demos List");
		txtViewTitle.setTextSize(20);
		txtViewTitle.setGravity(Gravity.CENTER_HORIZONTAL);
		txtViewTitle.setTextColor(Color.GRAY);
		
		String[] menu = new String[] {
		        "1. Droidz",
		        "2. Animate Elaine",
		        "3. Fireworks",
		        "4. OpenGL Viewer"
		};
		
		// Configure the list
		getListView().addHeaderView(txtViewTitle);
		setListAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu));
		getListView().setChoiceMode(ListView.CHOICE_MODE_SINGLE);
		getListView().setTextFilterEnabled(true);
		
		setContentView(getListView());
		Log.d(TAG, "View Added");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_main, menu);
		return true;
	}
	
	@Override
	protected void onDestroy(){
		Log.d(TAG, "Destroying...");
		super.onDestroy();
	}
	
	@Override
	protected void onStop(){
		Log.d(TAG, "Stopping...");
		super.onStop();
	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		
		super.onListItemClick(l, v, position, id);
		launchDemo(position);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
	}
	
	public void makeToast(CharSequence text) {
		Toast toast = Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT);
		toast.show();
	}
	
	public void launchDemo(int id) {
		
		Intent demo = null;
		
		switch(id){
		
			case 1:
				demo = new Intent(getApplicationContext(), DroidzActivity.class);
				break;
			case 2:
				demo = new Intent(getApplicationContext(), AnimatedElaineActivity.class);
				break;
			case 3:
				demo = new Intent(getApplicationContext(), FireworksActivity.class);
				break;
			case 4:
				demo = new Intent(getApplicationContext(), GLViewerActivity.class);
				break;
		}
		
		if ( demo != null)
			startActivity(demo);
	}
}