package com.qad.demo.group;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qad.demo.R;
import com.qad.demo.R.id;
import com.qad.util.DebugActivity;

public class ActivityBody2 extends DebugActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		debugLevel=2;
		
		setContentView(R.layout.main);
		
		((TextView) findViewById(R.id.text)).setText("This is in activity 2!");
		((Button)findViewById(id.btn1)).setText("exitApp");
		findViewById(R.id.btn1).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				exitApp();
			}
		});
		showMessage("create body2");
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		showMessage("resume body2");
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		showMessage("destroy body2");
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(com.qad.demo.R.menu.body2, menu);
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case id.item_body2:
			showMessage("handle in body2!");
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
