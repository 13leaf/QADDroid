package com.qad.util;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

/**
 * 上下文实用工具。比如消息提示，日志输出等。 日志输出的Tag使用当前上下文的具体类名。
 * 
 * @author 13leaf
 * 
 */
public class ContextTool {

	private WToast toast;

	private WLog log;

	private final WeakReference<Context> contextWrapper;

	public ContextTool(Context base) {
		// 初始化Toast
		toast = new WToast(base);
		log = new WLog(base.getClass());
		contextWrapper = new WeakReference<Context>(base);
	}

	/**
	 * 测试是否本机存在满足指定intent的活动
	 * 
	 * @param intent
	 * @return
	 */
	public boolean containsActivity(Intent intent) {
		Context mContext = contextWrapper.get();
		if (mContext != null) {
			return mContext.getPackageManager()
					.queryIntentActivities(intent, 0).size() != 0;
		}
		return false;
	}

	public boolean isAppInstalled(String uri) {
		Context mContext = contextWrapper.get();
		if (mContext != null) {
			PackageManager pm = mContext.getPackageManager();
			boolean installed = false;
			try {
				pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
				installed = true;
			} catch (PackageManager.NameNotFoundException e) {
				installed = false;
			}
			return installed;
		}else {
			errorLog("context not availiable in ContextTool");
			return false;
		}
	}
	
	/**
	 * 返回null表示该app没有安装
	 * @param uri
	 * @return
	 */
	public String getAppVersion(String uri){
		Context mContext = contextWrapper.get();
		if (mContext != null) {
			PackageManager pm = mContext.getPackageManager();
			try {
				PackageInfo info=pm.getPackageInfo(uri, PackageManager.GET_ACTIVITIES);
				return info.versionName;
			} catch (PackageManager.NameNotFoundException e) {
				return null;
			}
		}else {
			errorLog("context not availiable in ContextTool");
			return null;
		}
	}
	
	/**
	 * 是否设置了debuggable
	 * @return
	 */
	public boolean isDebugMode()
	{
		Context mContext=contextWrapper.get();
		if(mContext==null) return false;
		return (mContext.getApplicationInfo().flags & ApplicationInfo.FLAG_DEBUGGABLE) != 0;
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}

	@Override
	public String toString() {
		return super.toString();
	}

	public void debugLog(Object msg) {
		log.debugLog(msg);
	}

	public void errorLog(Object msg) {
		log.errorLog(msg);
	}

	public void testLog(Object msg) {
		log.testLog(msg);
	}

	public void infoLog(Object msg) {
		log.infoLog(msg);
	}

	public void warnLog(Object msg) {
		log.warnLog(msg);
	}

	public void verboseLog(Object msg) {
		log.verboseLog(msg);
	}

	public void showMessage(Object msg) {
		toast.showMessage(msg);
	}

	public void showLongMessage(Object msg) {
		toast.showLongMessage(msg);
	}

}
