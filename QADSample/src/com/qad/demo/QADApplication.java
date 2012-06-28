package com.qad.demo;

import com.qad.app.BaseApplication;
import com.qad.demo.R.xml;

public class QADApplication extends BaseApplication {

	@Override
	public void onCreate() {
		super.onCreate();
		ensureDefaultPreference(xml.qad_setting);
	}
	
	@Override
	public void onOpen() {
		showMessage("open App");
	}
	
	@Override
	public void onClose() {
		showMessage("close App");
	}
}
