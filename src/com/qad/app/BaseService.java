package com.qad.app;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.util.AndroidRuntimeException;
import android.util.SparseArray;

import com.qad.util.ContextTool;

/**
 * @author 13leaf
 * 
 */
public abstract class BaseService extends Service {

	/**
	 * 代理BaseContext的一些通用方法
	 */
	private BaseContext mWrapperContext;

	/**
	 * 指向服务引用
	 */
	protected BaseService me;

	private ContextTool mTool;

	private SparseArray<Notification> mManagedNotifications;

	private NotificationManager notificationManager;

	public BaseService() {
		mWrapperContext = new BaseContext(this);
		me = this;
		//
		mTool = new ContextTool(this);
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
	/**
	 * 以无参数的模式启动Activity。
	 * 
	 * @param activityClass
	 */
	public void startActivity(Class<? extends Activity> activityClass) {
		mWrapperContext.startActivity(activityClass);
	}

	/**
	 * 以无参数的模式启动service
	 */
	public void startService(Class<? extends Service> serviceClass) {
		mWrapperContext.startService(serviceClass);
	}

	/**
	 * 启动一个前台服务，展示一个通知。<br>
	 * 让该活动在通知中展示为一个"活动"，并且可以通过它启动活动组件。 <h3>尚未实现，勿调用</h3>
	 * 
	 * @param activityClass
	 */
	@Deprecated
	public void startForeground(Class<? extends Activity> activityClass) {
		// TODO 封装startForeground(Notification的那个方法)
	}

	/**
	 * 以绑定方式启动service。通过返回的绑定连接实例可以进行通信<br>
	 * 另请参照 @see {@link BindServiceConnection}
	 * 
	 * @param <T>
	 *            IBinder强类型
	 * @param intentService
	 *            目标服务
	 * @return 绑定连接
	 */
	public <T extends IBinder> BindServiceConnection<T> bindService(
			Intent intentService) {
		return mWrapperContext.bindService(intentService);
	}

	/**
	 * 很容易的绑定一个本地服务。
	 * 
	 * @param <T>
	 * @param serviceClass
	 * @return
	 */
	public <T extends IBinder> BindServiceConnection<T> bindService(
			Class<? extends Service> serviceClass) {
		return mWrapperContext.bindService(serviceClass);
	}

	/**
	 * 以无参数的模式关闭service。<br>
	 * <strong>对于bind启动的服务，需要释放所有的ServiceConnection才可关闭。</strong>
	 * 
	 * @param serviceClass
	 */
	public void stopService(Class<? extends Service> serviceClass) {
		mWrapperContext.stopService(serviceClass);
	}

	public void showMessage(Object msg) {
		mTool.showMessage(msg);
	}

	public void showLongMessage(Object msg) {
		mTool.showLongMessage(msg);
	}

	public void debugLog(Object msg) {
		mTool.debugLog(msg);
	}

	public void errorLog(Object msg) {
		mTool.errorLog(msg);
	}

	public void testLog(Object msg) {
		mTool.testLog(msg);
	}

	/**
	 * @param localIntent
	 * @return
	 * @see com.qad.app.BaseContext#getLocalIntent(java.lang.Class)
	 */
	public Intent getLocalIntent(Class<? extends Context> localIntent) {
		return mWrapperContext.getLocalIntent(localIntent);
	}

	public void infoLog(Object msg) {
		mTool.infoLog(msg);
	}

	public void warnLog(Object msg) {
		mTool.warnLog(msg);
	}

	public void verboseLog(Object msg) {
		mTool.verboseLog(msg);
	}

}
