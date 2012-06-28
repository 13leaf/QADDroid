package com.qad.demo.inject;

import android.os.Bundle;

import com.qad.app.BasePreferenceActivity;
import com.qad.demo.R.xml;

public class QadSetting extends BasePreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTitle("设置此处值可以看到InjectActivity的效果");
		addPreferencesFromResource(xml.qad_setting);
	}
}
