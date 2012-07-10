package com.qad.system;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.os.PowerManager;
import android.telephony.TelephonyManager;

/**
 * 
 * @author wangfeng
 *
 */
/*package*/class PhoneInfo {
	
	private String imei;
	private WeakReference<Context> contextRef;
	
	/*package*/PhoneInfo(final Context context)
	{
		contextRef=new WeakReference<Context>(context);
		TelephonyManager manager=
				(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		imei=manager.getDeviceId();
		
	}
	
	public String getImei()
	{
		return imei;
	}
	
	public boolean isScreenOn()
	{
		Context context=contextRef.get();
		if(context!=null){
			PowerManager manager=(PowerManager) context.getSystemService(Context.POWER_SERVICE);
			return manager.isScreenOn();
		}else {
			return false;
		}
	}
}
