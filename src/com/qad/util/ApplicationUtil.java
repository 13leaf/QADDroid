package com.qad.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;

/**
 * Application信息可以使用 {@link Context#getApplicationInfo()}
 * @author 13leaf
 *
 */
@Deprecated
public class ApplicationUtil {

	private Context base;
	
	private ApplicationInfo appInfor;
	
	public ApplicationUtil(Context context)
	{
		base=context;
		appInfor=base.getApplicationInfo();
	}
	
	public String getAppName()
	{
		return appInfor.loadLabel(base.getPackageManager())+"";
	}
	
	public int getAppIcon()
	{
		return appInfor.icon;
	}
	
	
}
