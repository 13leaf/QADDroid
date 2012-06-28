package com.qad.demo.group;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.qad.demo.R;
import com.qad.demo.R.id;
import com.qad.util.DebugActivity;

public class ActivityBody extends DebugActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.main);
		
		debugLevel=2;
		
		((TextView) findViewById(R.id.text)).setText("This is in activity 1");
		showMessage("create body1");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showMessage("resume body1");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		showMessage("destroy body1");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(com.qad.demo.R.menu.body1, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case id.item_body1:
			showMessage("handle in Body1!");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
