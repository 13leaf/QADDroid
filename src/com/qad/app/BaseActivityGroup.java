package com.qad.app;

import java.util.List;

import android.app.Activity;
import android.app.ActivityGroup;
import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.util.AndroidRuntimeException;
import android.util.SparseArray;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewStub;
import android.webkit.WebView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.qad.debug.ViewServer;
import com.qad.inject.ExtrasInjector;
import com.qad.inject.PreferenceInjector;
import com.qad.inject.ResourceInjector;
import com.qad.inject.SystemServiceInjector;
import com.qad.inject.ViewInjector;
import com.qad.util.ActivityTool;
import com.qad.util.ActivityTool.FinishListener;

public class BaseActivityGroup extends ActivityGroup {

	private BaseContext proxycContext;
	
	private ActivityTool tool;
	
	/**
	 * 简易访问本活动实例的指针
	 */
	protected BaseActivityGroup me;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		proxycContext=new BaseContext(this);
		tool=new ActivityTool(this);
		me=this;
		
		//do the inject
		SystemServiceInjector.inject(this, this);
		ExtrasInjector.inject(getIntent().getExtras(), this);
		ResourceInjector.inject(getApplicationContext(), this);
		PreferenceInjector.inject(getApplicationContext(), this);
		
		ViewServer.get(this).addWindow(this);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		
		ViewServer.get(this).removeWindow(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		
		ViewServer.get(this).setFocusedWindow(this);
	}
	
	
	@Override
	protected void onNewIntent(Intent intent) {
		super.onNewIntent(intent);
		
		//do the inject
		ExtrasInjector.inject(getIntent().getExtras(), this);
	}
	
	@Override
	public void setContentView(int layoutResID) {
		super.setContentView(layoutResID);
		ViewInjector.inject(getWindow().getDecorView(), this);
	}
	
	@Override
	public void setContentView(View view) {
		super.setContentView(view);
		ViewInjector.inject(getWindow().getDecorView(), this);
	}
	
	@Override
	public void setContentView(View view, LayoutParams params) {
		super.setContentView(view, params);
		ViewInjector.inject(getWindow().getDecorView(), this);
	}
	
	private ProgressDialog progressDialog;
	
	private static final String defaulProgressMsg="正在载入...请稍后";
	
	/**
	 * 获得一个默认的ProgressDialog,可通过setDefaultProgressMsg来设置该默认ProgressDialog的信息。<br>
	 * 子类重写本方法可以定制特色的进度框。
	 * @return
	 */
	protected ProgressDialog getDefaultProgressDialog()
	{
		if(progressDialog==null) 
		{
			progressDialog=new ProgressDialog(this);
			progressDialog.setMessage(defaulProgressMsg);
		}
		return progressDialog;
	}
	
	public String getDefaultProgressMsg() {
		return progressDialog==null?null:defaulProgressMsg;
	}
	
	public void setDefaultProgressMsg(String value){
		progressDialog.setMessage(value);
	}


	/**
	 * @param activityClass
	 * @see practice.utils.app.BaseContext#startActivity(java.lang.Class)
	 */
	public void startActivity(Class<? extends Activity> activityClass) {
		proxycContext.startActivity(activityClass);
	}

	/**
	 * @param serviceClass
	 * @see practice.utils.app.BaseContext#startService(java.lang.Class)
	 */
	public void startService(Class<? extends Service> serviceClass) {
		proxycContext.startService(serviceClass);
	}

	/**
	 * @param <T>
	 * @param intentService
	 * @return
	 * @see practice.utils.app.BaseContext#bindService(android.content.Intent)
	 */
	public <T extends IBinder> BindServiceConnection<T> bindService(
			Intent intentService) {
		return proxycContext.bindService(intentService);
	}

	/**
	 * @param <T>
	 * @param serviceClass
	 * @return
	 * @see practice.utils.app.BaseContext#bindService(java.lang.Class)
	 */
	public <T extends IBinder> BindServiceConnection<T> bindService(
			Class<? extends Service> serviceClass) {
		return proxycContext.bindService(serviceClass);
	}

	/**
	 * @param serviceClass
	 * @see practice.utils.app.BaseContext#stopService(java.lang.Class)
	 */
	public void stopService(Class<? extends Service> serviceClass) {
		proxycContext.stopService(serviceClass);
	}

	/**
	 * @param msg
	 * @see practice.utils.app.BaseContext#showMessage(java.lang.Object)
	 */
	public void showMessage(Object msg) {
		proxycContext.showMessage(msg);
	}

	/**
	 * @param msg
	 * @see practice.utils.app.BaseContext#showLongMessage(java.lang.Object)
	 */
	public void showLongMessage(Object msg) {
		proxycContext.showLongMessage(msg);
	}

	/**
	 * @param msg
	 * @see practice.utils.app.BaseContext#debugLog(java.lang.Object)
	 */
	public void debugLog(Object msg) {
		proxycContext.debugLog(msg);
	}
	
	/**
	 * @param objects
	 * @return
	 * @see practice.utils.ActivityTool#getSpinnerAdapter(java.lang.Object[])
	 */
	public ArrayAdapter<Object> getSpinnerAdapter(Object[] objects) {
		return tool.getSpinnerAdapter(objects);
	}


	/**
	 * @param list
	 * @return
	 * @see practice.utils.ActivityTool#getSpinnerAdapter(java.util.List)
	 */
	public ArrayAdapter<Object> getSpinnerAdapter(List<? extends Object> list) {
		return tool.getSpinnerAdapter(list);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findT(int)
	 */
	public TextView findT(int id) {
		return tool.findT(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findET(int)
	 */
	public EditText findET(int id) {
		return tool.findET(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findSP(int)
	 */
	public Spinner findSP(int id) {
		return tool.findSP(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findGV(int)
	 */
	public GridView findGV(int id) {
		return tool.findGV(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findLLayout(int)
	 */
	public LinearLayout findLLayout(int id) {
		return tool.findLLayout(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findPB(int)
	 */
	public ProgressBar findPB(int id) {
		return tool.findPB(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findB(int)
	 */
	public Button findB(int id) {
		return tool.findB(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findIB(int)
	 */
	public ImageButton findIB(int id) {
		return tool.findIB(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findST(int)
	 */
	public ViewStub findST(int id) {
		return tool.findST(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findWeb(int)
	 */
	public WebView findWeb(int id) {
		return tool.findWeb(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findLV(int)
	 */
	public ListView findLV(int id) {
		return tool.findLV(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findVSW(int)
	 */
	public ViewSwitcher findVSW(int id) {
		return tool.findVSW(id);
	}

	/**
	 * @param ids
	 * @param l
	 * @see practice.utils.ActivityTool#setOnClickListener(int[], android.view.View.OnClickListener)
	 */
	public void setOnClickListener(int[] ids, OnClickListener l) {
		tool.setOnClickListener(ids, l);
	}

	/**
	 * @param ids
	 * @param l
	 * @see practice.utils.ActivityTool#setOnTouchListener(int[], android.view.View.OnTouchListener)
	 */
	public void setOnTouchListener(int[] ids, OnTouchListener l) {
		tool.setOnTouchListener(ids, l);
	}

	/**
	 * @return
	 * @see practice.utils.ActivityTool#getFinishListener()
	 */
	public FinishListener getFinishListener() {
		return tool.getFinishListener();
	}

	/**
	 * @param localIntent
	 * @return
	 * @see practice.utils.app.BaseContext#getLocalIntent(java.lang.Class)
	 */
	public Intent getLocalIntent(Class<? extends Context> localIntent) {
		return proxycContext.getLocalIntent(localIntent);
	}

	/**
	 * @param msg
	 * @see practice.utils.app.BaseContext#errorLog(java.lang.Object)
	 */
	public void errorLog(Object msg) {
		proxycContext.errorLog(msg);
	}

	/**
	 * @param msg
	 * @see practice.utils.app.BaseContext#testLog(java.lang.Object)
	 */
	public void testLog(Object msg) {
		proxycContext.testLog(msg);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findSV(int)
	 */
	public ScrollView findSV(int id) {
		return tool.findSV(id);
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

	/**
	 * @param startUpClass
	 * @see practice.utils.ActivityTool#createShortCut(java.lang.Class)
	 */
	public void createShortCut(Class<? extends Activity> startUpClass) {
		tool.createShortCut(startUpClass);
	}

	/**
	 * @param message
	 * @param okListener
	 * @see practice.utils.ActivityTool#showConfirmDialog(java.lang.String, android.content.DialogInterface.OnClickListener)
	 */
	public void showConfirmDialog(String message,
			android.content.DialogInterface.OnClickListener okListener) {
		tool.showConfirmDialog(message, okListener);
	}

	/**
	 * @param message
	 * @see practice.utils.ActivityTool#showMessageDialog(java.lang.String)
	 */
	public void showMessageDialog(String message) {
		tool.showMessageDialog(message);
	}

	/**
	 * @param editView
	 * @see practice.utils.ActivityTool#hideInput(android.view.View)
	 */
	public void hideInput(View editView) {
		tool.hideInput(editView);
	}

	/**
	 * @param listener
	 * @param views
	 * @see practice.utils.ActivityTool#setOnClickListener(android.view.View.OnClickListener, android.view.View[])
	 */
	public void setOnClickListener(OnClickListener listener, View... views) {
		tool.setOnClickListener(listener, views);
	}

	/**
	 * @param idName
	 * @return
	 * @see practice.utils.ActivityTool#findViewByIdName(java.lang.String)
	 */
	public View findViewByIdName(String idName) {
		return tool.findViewByIdName(idName);
	}

	/**
	 * 
	 * @see practice.utils.ActivityTool#findAllViewFileds()
	 */
	@Deprecated
	public void findAllViewFileds() {
		tool.findAllViewFileds();
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ActivityTool#findIV(int)
	 */
	public ImageView findIV(int id) {
		return tool.findIV(id);
	}

	/**
	 * 
	 * @see com.qad.util.ActivityTool#lockPortrait()
	 */
	public void lockPortrait() {
		tool.lockPortrait();
	}

	/**
	 * 
	 * @see com.qad.util.ActivityTool#lockLandscape()
	 */
	public void lockLandscape() {
		tool.lockLandscape();
	}

	/**
	 * @param name
	 * @see com.qad.util.ActivityTool#testLog(java.lang.String)
	 */
	public void testLog(String name) {
		tool.testLog(name);
	}

	/**
	 * @param dialog
	 * @see com.qad.util.ActivityTool#safeShowDialog(android.app.Dialog)
	 */
	public void safeShowDialog(Dialog dialog) {
		tool.safeShowDialog(dialog);
	}

	/**
	 * @param dialogId
	 * @see com.qad.util.ActivityTool#safeShowDialog(int)
	 */
	public void safeShowDialog(int dialogId) {
		tool.safeShowDialog(dialogId);
	}

	/**
	 * @param dialog
	 * @see com.qad.util.ActivityTool#safeDismissDialog(android.app.Dialog)
	 */
	public void safeDismissDialog(Dialog dialog) {
		tool.safeDismissDialog(dialog);
	}

	/**
	 * @param dialogId
	 * @see com.qad.util.ActivityTool#safeDismissDialog(int)
	 */
	public void safeDismissDialog(int dialogId) {
		tool.safeDismissDialog(dialogId);
	}

	/**
	 * @param brightness
	 * @see com.qad.util.ActivityTool#setBrightness(int)
	 */
	public void setBrightness(int brightness) {
		tool.setBrightness(brightness);
	}

	/**
	 * 
	 * @see com.qad.util.ActivityTool#wakeLock()
	 */
	public void wakeLock() {
		tool.wakeLock();
	}

	public void registerSDCardListener(BroadcastReceiver receiver) {
		tool.registerSDCardListener(receiver);
	}

	public boolean isFullScreen() {
		return tool.isFullScreen();
	}

	public void toggleFullScreen() {
		tool.toggleFullScreen();
	}

	public void infoLog(Object msg) {
		proxycContext.infoLog(msg);
	}

	public void warnLog(Object msg) {
		proxycContext.warnLog(msg);
	}

	public void verboseLog(Object msg) {
		proxycContext.verboseLog(msg);
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
}
