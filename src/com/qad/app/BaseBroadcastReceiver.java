package com.qad.app;

import android.content.BroadcastReceiver;
import android.content.IntentFilter;

public abstract class BaseBroadcastReceiver extends BroadcastReceiver {
	
	/**
	 * 获取该事件监听器关心的事件
	 * @return
	 */
	public abstract IntentFilter getIntentFilter();

}
