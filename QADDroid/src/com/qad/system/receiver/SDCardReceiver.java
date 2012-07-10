package com.qad.system.receiver;

import java.util.LinkedList;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.qad.system.listener.SDCardListioner;

public class SDCardReceiver extends AbstractReceiver {

	private final LinkedList<SDCardListioner> listioners=new LinkedList<SDCardListioner>();
	private final Object lock=new Object();
	
	public SDCardReceiver(Context context) {
		super(context);
	}

	@Override
	protected IntentFilter getIntentFilter() {
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_MEDIA_MOUNTED);   
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_STARTED);   
        intentFilter.addAction(Intent.ACTION_MEDIA_SCANNER_FINISHED);   
        intentFilter.addAction(Intent.ACTION_MEDIA_REMOVED);   
        intentFilter.addAction(Intent.ACTION_MEDIA_UNMOUNTED);   
        intentFilter.addAction(Intent.ACTION_MEDIA_BAD_REMOVAL);  
        intentFilter.addAction(Intent.ACTION_MEDIA_SHARED);
        intentFilter.addAction(Intent.ACTION_MEDIA_EJECT);
        intentFilter.addDataScheme("file"); 
		return intentFilter;
	}
	
	public void addOnSDCardListioner(SDCardListioner listioner)
	{
		synchronized (lock) {
			if(!listioners.contains(listioner))
				listioners.add(listioner);
		}
	}
	
	public void removeOnSDCardListioner(SDCardListioner listioner)
	{
		synchronized (lock) {
			listioners.remove(listioner);
		}
	}

	/**
	 * 在P1000上连接USB模式发生的广播顺序为:
	 * Eject->Unmounted->Shared
	 * 移除USB模式时广播顺序为:
	 * UnMounted->Mounted->Scanner_Started->Scanner_Finished
	 * FIXME 移除USB模式时不应当相应UnMounted广播
	 */
	@Override
	public void onReceive(Context context, Intent intent) {
		String action=intent.getAction();
		synchronized (lock) {
			if(action.equals(Intent.ACTION_MEDIA_SCANNER_FINISHED))
			{
				for(SDCardListioner listioner:listioners)
					listioner.onSDCardMounted();
			}else if(action.equals(Intent.ACTION_MEDIA_SHARED) || action.equals(Intent.ACTION_MEDIA_REMOVED))
			{
				//被USB共享或者被拔出
				for(SDCardListioner listioner:listioners)
					listioner.onSDCardRemoved();
			}
		}
	}

}
