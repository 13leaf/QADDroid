package com.qad.system;

import android.content.Context;
import android.telephony.TelephonyManager;

/**
 * 
 * @author wangfeng
 *
 */
/*package*/class PhoneInfo {
	
	private String imei;
	
	/*package*/PhoneInfo(final Context context)
	{
		TelephonyManager manager=
				(TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
		imei=manager.getDeviceId();
		
	}
	
	public String getImei()
	{
		return imei;
	}
}
