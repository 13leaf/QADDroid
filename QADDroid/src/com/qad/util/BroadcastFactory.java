package com.qad.util;

import android.content.Intent;
import android.content.IntentFilter;

public class BroadcastFactory {
	public static final String ACTION_EXIT="com.qad.exit";

	/**
	 * MEDIA_REMVOED:
	 * MEDIA_MOUNTED:
	 * MEDIA_EJECT:
	 * MEDIA_BUTTON:
	 * @return
	 */
	public static IntentFilter getSDCardReceiver(){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_BUTTON);
        filter.addDataScheme("file");
        return filter;
	}
	
	public static IntentFilter getExitReceiver()
	{
		IntentFilter filter=new IntentFilter();
		filter.addAction(ACTION_EXIT);
		return filter;
	}

}
