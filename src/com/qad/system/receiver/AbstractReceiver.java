package com.qad.system.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;

public abstract class AbstractReceiver extends BroadcastReceiver {

	public AbstractReceiver(final Context context)
	{
		context.registerReceiver(this, getIntentFilter());
	}
	
	/**
	 * 获取监听的筛选器
	 * @return
	 */
	protected abstract IntentFilter getIntentFilter();
	
}
