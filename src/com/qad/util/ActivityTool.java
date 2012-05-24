package com.qad.util;

import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;

/**
 * 提供Activity的一些工具方法。
 * @author 13leaf
 *
 */
public class ActivityTool {
	
	private Activity mActivity;
	
	private ViewTool mViewTool=new ViewTool();
	
	
	private DialogTool dialogTool;
	
	private boolean isFullScreen=false;
	
	
	public ActivityTool(Activity activity)
	{
		mActivity=activity;
		
		dialogTool=new DialogTool(activity);
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
	 * 获取系统当前的亮度值
	 * @return
	 */
	public int getBrightness()
	{
		try {
			return Settings.System.getInt(mActivity.getContentResolver(), Settings.System.SCREEN_BRIGHTNESS);
		} catch (SettingNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}


	/**
	 * 返回findViewById的装饰视图
	 * @return
	 */
	private View getDecorView()
	{
		return mActivity.getWindow().getDecorView();
	}
	
	/**
	 * 是否当前活动处于全屏状态,全屏状态下将不显示状态栏
	 * @return
	 */
	public boolean isFullScreen() {
		return isFullScreen;
	}
	
	/**
	 * 切换全屏状态
	 */
	public void toggleFullScreen(){
		if(isFullScreen){
			mActivity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}else {
			mActivity.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
		}
	}
	
	/**
	 * 锁住为竖屏状态
	 */
	public void lockPortrait()
	{
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
	}
	
	/**
	 * 锁住为横屏状态
	 */
	public void lockLandscape()
	{
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
	}
	
	/**
	 * 解锁屏幕,允许感应
	 */
	public void unLockOrientation()
	{
		mActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
	}
	
	/**
	 * 测试log
	 * @param name
	 */
	public void testLog(String name)
	{
		Log.d("13leaf",name);
	}
	
	/**
	 * 弹出一个询问对话框。
	 * @param message
	 * @param okListener
	 */
	public void showConfirmDialog(String message,DialogInterface.OnClickListener okListener)
	{
		dialogTool.createNoTitleConfirmDialog(message, okListener).show();
	}
	
	/**
	 * 弹出一个消息对话框
	 * @param message
	 */
	public void showMessageDialog(String message)
	{
		dialogTool.createNoTitleMessageDialog(message).show();
	}
	
	/**
	 * 若当前的Activity已经关闭,则尝试showDialog会引起错误。
	 * 这尤其在AsyncTask中经常发生
	 * @param dialog 想要做show操作的对话框 
	 */
	public void safeShowDialog(Dialog dialog)
	{
		if(!mActivity.isFinishing())
			dialog.show();
	}
	
	/**
	 * 默认的showDialog(id)方法并不判断上下文的合法性。<br>
	 * 尤其在AsyncTask中尤其容易出错。使用safeShowDialog(id)会帮你判断上下文是否合法，避免其出现错误。
	 * @param dialogId
	 */
	public void safeShowDialog(int dialogId)
	{
		if(!mActivity.isFinishing())
			mActivity.showDialog(dialogId);
	}
	
	/**
	 * 判断上下文是否可用，然后安全的关闭dialog。<br>
	 * @param dialog
	 */
	public void safeDismissDialog(Dialog dialog)
	{
		if(!mActivity.isFinishing())
			dialog.dismiss();
	}
	
	/**
	 * 判断上下文是否可用，然后安全的关闭dialog。
	 * @param dialogId
	 */
	public void safeDismissDialog(int dialogId)
	{
		if(!mActivity.isFinishing())
			mActivity.dismissDialog(dialogId);
	}
	
	/**
	 * 设置亮度
	 * @param brightness 亮度级别。0-255递增
	 */
	 public void setBrightness(int brightness)
	 {
	    WindowManager.LayoutParams localLayoutParams = mActivity.getWindow().getAttributes();
	    float f = brightness / 255.0f;
	    localLayoutParams.screenBrightness = f;
	    mActivity.getWindow().setAttributes(localLayoutParams);
	 }	
	 
	 /**
	  * 锁定系统休眠。使用<use-permission android:name="WAKE_LOCK">也可以达到锁定系统休眠。<br>
	  * 但是背光会变暗，wakeLock方法通过操纵Window的LayoutParams来改善锁定休眠的背光问题。
	  */
	 public void wakeLock()
	 {
		 mActivity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	 }
	
	/**
	 * 将与某个View关联的软键盘隐藏掉。
	 * @param view
	 */
	public void hideInput(View editView)
	{
		InputMethodManager inputMethodManager=
			(InputMethodManager)mActivity.getSystemService(Activity.INPUT_METHOD_SERVICE);
		inputMethodManager.hideSoftInputFromWindow(
				editView.getWindowToken(),
//				InputMethodManager.RESULT_HIDDEN
				0
				);
		
	}
	
	/**
	 * 为程序创建一个桌面的快捷方式.注意，该快捷方式只创建一次
	 * @param startUpClass 必须为Category是Main,Action是Launcher的Class
	 */
	public void createShortCut(Class<? extends Activity> startUpClass)
	{
		final String shortCutKey="isCreatedShortcut";
		SharedPreferences settings=
			mActivity.getApplication().getSharedPreferences("MyAppPref", Application.MODE_PRIVATE);
		boolean isCreated=settings.getBoolean(shortCutKey, false);
		if(!isCreated)
		{
			//create shortcut
			ApplicationInfo appInfor=mActivity.getApplicationInfo();
			Intent createIntent=IntentFactory.getCreateShortCutIntent(
					appInfor.loadLabel(mActivity.getPackageManager())+"", 
					Intent.ShortcutIconResource.fromContext(mActivity, appInfor.icon),
					new Intent(mActivity,startUpClass));
			mActivity.sendBroadcast(createIntent);
			
			//set key,non duplication shortcut
			SharedPreferences.Editor editor=settings.edit();
			editor.putBoolean(shortCutKey,true);
			editor.commit();
		}
	}
	
	/**
	 * 发送关闭广播。关闭所有的活动，活动关闭完成之后将触发BaseApplication的onClose。
	 */
	public void closeApp()
	{
		mActivity.sendBroadcast(new Intent(CloseBroadCastReceiver.ACTION_EXIT));
	}
	
	/**
	 * 简单的Spinner适配器实现
	 * @param objects
	 * @return
	 */
	public ArrayAdapter<Object> getSpinnerAdapter(Object[] objects)
	{
		ArrayAdapter<Object> adapter=new ArrayAdapter<Object>(
				mActivity, 
				android.R.layout.simple_spinner_item,
				objects
				);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		return adapter;
	}
	
	/**
	 * 简单的Spinner适配器实现重载
	 * @param list
	 * @return
	 */
	public ArrayAdapter<Object> getSpinnerAdapter(List<? extends Object> list)
	{
		return getSpinnerAdapter(list.toArray());
	}

	private ViewTool getmViewTool() {
		mViewTool.setDecorView(getDecorView());
		return mViewTool;
	}
	
	/**
	 * @param ids
	 * @param l
	 * @see practice.utils.ViewTool#setOnClickListener(int[], android.view.View.OnClickListener)
	 */
	public void setOnClickListener(int[] ids, OnClickListener l) {
		getmViewTool().setOnClickListener(ids, l);
	}

	/**
	 * @param ids
	 * @param l
	 * @see practice.utils.ViewTool#setOnTouchListener(int[], android.view.View.OnTouchListener)
	 */
	public void setOnTouchListener(int[] ids, OnTouchListener l) {
		getmViewTool().setOnTouchListener(ids, l);
	}
	
	/**
	 * 获取一个简易的关闭监听实现。可用于结束活动或者关闭对话框
	 * @return
	 */
	public FinishListener getFinishListener()
	{
		if(finishListenerInstance==null)
			finishListenerInstance=new FinishListener();
		return finishListenerInstance;
	}
	
	private static FinishListener finishListenerInstance=null;//单例的关闭监听

	/**
	 * 监听关闭事件
	 */
	 public class FinishListener implements OnClickListener,DialogInterface.OnClickListener{
	
		@Override
		public void onClick(View v) {
			mActivity.finish();
		}
	
		@Override
		public void onClick(DialogInterface dialog, int which) {
			mActivity.finish();
		}
		
	}

	/**
	 * @param listener
	 * @param views
	 * @see practice.utils.ViewTool#setOnClickListener(android.view.View.OnClickListener, android.view.View[])
	 */
	public void setOnClickListener(OnClickListener listener, View... views) {
		getmViewTool().setOnClickListener(listener, views);
	}


	/**
	 * @param idName
	 * @return
	 * @see practice.utils.ViewTool#findViewByIdName(java.lang.String)
	 */
	public View findViewByIdName(String idName) {
		return getmViewTool().findViewByIdName(idName);
	}

}
