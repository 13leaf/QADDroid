package com.qad.demo.inject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.qad.annotation.InjectPreference;
import com.qad.annotation.InjectResource;
import com.qad.annotation.InjectSystemService;
import com.qad.annotation.InjectView;
import com.qad.app.BaseActivity;
import com.qad.demo.R.id;
import com.qad.demo.R.layout;
import com.qad.demo.R.string;
import com.qad.demo.net.DownLoadDemo;
import com.qad.util.NotificationBuilder;

public class InjectActivity extends BaseActivity {

	//inject resource
	@InjectResource(id=string.hello) String helloText;
	
	//inject views
	@InjectView(id=id.inject_button) Button injectedButton;
	@InjectView(id=id.inject_textview) TextView injectedTextView;
	
	//inject system service
	@InjectSystemService(name=LAYOUT_INFLATER_SERVICE)
		LayoutInflater inflater;
	
	@InjectSystemService(name=NOTIFICATION_SERVICE)
		NotificationManager notificationManager;
	
	//we can inject preference by key's name string
	@InjectPreference(name="first")
		String firstName;
	//or we can inject preference by key's resource id
	@InjectPreference(id=string.key_second)
		String secondName;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		showMessage("injectPreference:"+firstName+" "+secondName);
		
		Notification notification=buildNotification();
		notificationManager.notify(1, notification);
		
		//system Service is first injected
		View contentView=inflater.inflate(layout.inject_view_demo, null);
		setContentView(contentView);
		//auto inject view complete
		injectedTextView.setText("I have been injected");
		injectedButton.setText("Click Me!");
		injectedButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				showMessage("hello resource is "+helloText);
			}
		});
		
	}

	private Notification buildNotification() {
		PendingIntent pendingIntent=
				PendingIntent.getActivity(this, 0, new Intent(this,DownLoadDemo.class), 0);
		PendingIntent deleteIntent=
				PendingIntent.getActivity(this, 0, new Intent(this,QadSetting.class), 0);
		return new NotificationBuilder(this)
					.setTicker("Inject ok!")
					.setContentTitle("Inject Activity")
					.setContentText("InjectView will done after setContentView")
//					.setSmallIcon(drawable.default_thumb)
					.setOnlyAlertOnce(true)
					.setAutoCancel(true)
					.setContentIntent(pendingIntent)
					.setDeleteIntent(deleteIntent)
					.getNotification();
	}
}
