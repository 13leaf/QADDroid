package com.qad.app;

import java.lang.ref.WeakReference;
import java.util.Stack;

import android.app.Activity;
import android.app.Application;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.Intent;
import android.preference.PreferenceManager;
import android.util.AndroidRuntimeException;
import android.util.SparseArray;

import com.qad.net.ApnManager;
import com.qad.system.PhoneManager;
import com.qad.util.ContextTool;
import com.qad.util.WLog;

/**
 * 为自定义Application添加了工具方法的支持。<br>
 * 
 * @author 13leaf
 * 
 */
public class BaseApplication extends Application implements AppManager{

	private ContextTool mContextTool;
	public String getAppVersion(String uri) {
		return mContextTool.getAppVersion(uri);
	}

	private WeakReference<Activity> topActivity;
	private Stack<WeakReference<Activity>> taskStack=new Stack<WeakReference<Activity>>();
	
	@Override
	public void onCreate() {
		super.onCreate();
		mContextTool = new ContextTool(this);
		PhoneManager phoneManager=PhoneManager.getInstance(this);//init PhoneManager
		ApnManager apnManager=ApnManager.getInstance(this);
		phoneManager.addOnNetWorkChangeListioner(apnManager);
		if(!isDebugMode()) WLog.closeLogger();
	}

	
	@Override
	public boolean equals(Object o) {
		return super.equals(o);
	}
	
	@Override
	public int hashCode() {
		return super.hashCode();
	}

	@Override
	public String toString() {
		return super.toString();
	}
	
	/**
	 * 确保载入Preference中的默认定义。否则必须在进入设置界面后才能被初始化
	 * @param xmlRes
	 */
	public void ensureDefaultPreference(int xmlRes)
	{
		PreferenceManager.setDefaultValues(this, xmlRes, false);
	}

	/**
	 * @param msg
	 * @see com.qad.util.ContextTool#showMessage(java.lang.Object)
	 */
	public void showMessage(Object msg) {
		mContextTool.showMessage(msg);
	}


	/**
	 * @param msg
	 * @see com.qad.util.ContextTool#showLongMessage(java.lang.Object)
	 */
	public void showLongMessage(Object msg) {
		mContextTool.showLongMessage(msg);
	}


	/**
	 * @param msg
	 * @see com.qad.util.ContextTool#debugLog(java.lang.Object)
	 */
	public void debugLog(Object msg) {
		mContextTool.debugLog(msg);
	}

	/**
	 * @param msg
	 * @see com.qad.util.ContextTool#errorLog(java.lang.Object)
	 */
	public void errorLog(Object msg) {
		mContextTool.errorLog(msg);
	}
	
	public void onOpen(){
		
	}

	/**
	 * 当调用BaseActivity的closeApp后触发。所有活动均已关闭后将触发。
	 */
	public void onClose() {
		
	}

	/**
	 * @param msg
	 * @see com.qad.util.ContextTool#testLog(java.lang.Object)
	 */
	public void testLog(Object msg) {
		mContextTool.testLog(msg);
	}

	public void infoLog(Object msg) {
		mContextTool.infoLog(msg);
	}

	public void warnLog(Object msg) {
		mContextTool.warnLog(msg);
	}

	public void verboseLog(Object msg) {
		mContextTool.verboseLog(msg);
	}
	
	/**
	 * 请勿手动调用
	 * @param instance
	 */
	public void pushTaskStack(Activity instance)
	{
		taskStack.push(new WeakReference<Activity>(instance));
	}
	
	/**
	 * 请勿手动调用
	 * @return 返回是否栈为空
	 */
	public boolean popTaskStack(){
		taskStack.pop();
		return !taskStack.isEmpty();
	}

	/**
	 * 请勿手动调用
	 * @param instantce
	 */
	public void setTopActivity(Activity instantce) {
		this.topActivity=new WeakReference<Activity>(instantce);
	}

	@Override
	public Activity getTopActivity() {
		return topActivity.get();
	}
	
	private SparseArray<Notification> mManagedNotifications;

	private NotificationManager notificationManager;
	
	protected Notification onCreateNotification(int Id) {
		return null;
	}

	protected void onPrepareNotification(int Id, Notification notification) {

	}
	

	public void showNotification(int id) {
		ensureNotificationManager();
		if (mManagedNotifications == null) {
			mManagedNotifications = new SparseArray<Notification>();
		}
		Notification notification = mManagedNotifications.get(id);
		if (notification == null) {
			notification = onCreateNotification(id);
			mManagedNotifications.put(id, notification);
			if (notification == null) {
				throw new AndroidRuntimeException(
						"Are you sure create notification in onCreateNotification which id is :"
								+ id + "?");
			}
		}
		onPrepareNotification(id, notification);
		notificationManager.notify(id, notification);
	}

	public void cancelNotification(int id) {
		ensureNotificationManager();
		Notification notification=null;
		if (mManagedNotifications != null) {
			notification = mManagedNotifications.get(id);
		}
		if (notification == null) {
			warnLog("Have not ever notify that id " + id + " notification.");
		}
		notificationManager.cancel(id);
	}

	public void cancelAllNotification() {
		ensureNotificationManager();
		notificationManager.cancelAll();
	}
	
	private void ensureNotificationManager()
	{
		if (notificationManager == null) {
			notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
		}
	}

	@Override
	public int getTaskSize() {
		return taskStack.size();
	}


	public boolean containsActivity(Intent intent) {
		return mContextTool.containsActivity(intent);
	}


	public boolean isAppInstalled(String uri) {
		return mContextTool.isAppInstalled(uri);
	}


	public boolean isDebugMode() {
		return mContextTool.isDebugMode();
	}
}
