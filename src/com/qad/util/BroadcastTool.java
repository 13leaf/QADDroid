package com.qad.util;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

public class BroadcastTool {
	public static final String ACTION_EXIT="com.qad.exit";

	private Context mContext;
	
	public BroadcastTool(Context context){
		mContext=context;
	}
	
	public void registerSDCardListener(BroadcastReceiver receiver){
        IntentFilter filter=new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_REMOVED);
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_CHECKING);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addAction(Intent.ACTION_MEDIA_BUTTON);
        filter.addDataScheme("file");
        
        mContext.registerReceiver(receiver, filter);
	}
}
