package com.qad.util;

import java.lang.ref.WeakReference;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.qad.app.BaseBroadcastReceiver;

public class CloseBroadCastReceiver extends BaseBroadcastReceiver {

	public static final String ACTION_EXIT="action.com.qad.exit";
	
	private WeakReference<Activity> mActivityReference;
	
	public CloseBroadCastReceiver(Activity activity)
	{
		mActivityReference=new WeakReference<Activity>(activity);
	}
	
	@Override
	public IntentFilter getIntentFilter() {
		IntentFilter filter=new IntentFilter();
		filter.addAction(ACTION_EXIT);
		return filter;
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		Activity mActivity=mActivityReference.get();
		if(mActivity!=null)
		{
			mActivity.finish();
		}
	}

}
