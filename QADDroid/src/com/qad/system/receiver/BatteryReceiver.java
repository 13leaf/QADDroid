package com.qad.system.receiver;

import java.util.LinkedList;

import com.qad.system.listener.BatteryListener;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

public class BatteryReceiver extends AbstractReceiver {

	private final LinkedList<BatteryListener> listeners=new LinkedList<BatteryListener>();
	private final Object lock=new Object();
	
	public BatteryReceiver(Context context) {
		super(context);
	}

	@Override
	protected IntentFilter getIntentFilter() {
		IntentFilter intentFilter=new IntentFilter();
		intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
		intentFilter.addAction(Intent.ACTION_BATTERY_LOW);
		intentFilter.addAction(Intent.ACTION_BATTERY_OKAY);
		return intentFilter;
	}
	
	public void addOnBatteryChangeListener(BatteryListener listener)
	{
		synchronized (lock) {
			if(!listeners.contains(listener))
				listeners.add(listener);
		}
	}
	
	public void removeOnBatteryChangeListener(BatteryListener listener)
	{
		synchronized (lock) {
			listeners.remove(listener);
		}
	}

	@Override
	public void onReceive(Context context, Intent intent) {
		synchronized (lock) {
			if(Intent.ACTION_BATTERY_LOW.equals(intent.getAction())){
				for(BatteryListener listener:listeners)
					listener.onBatteryLow();
			}else if(Intent.ACTION_BATTERY_OKAY.equals(intent.getAction())){
				for(BatteryListener listener:listeners)
					listener.onBatteryOKAY();
			}else {
				int level=intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
				int max=intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
				for(BatteryListener listener:listeners){
					listener.onBatteryChange(level, max, intent);
				}
			}
		}
	}
}
