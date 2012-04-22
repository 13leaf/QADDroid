package com.qad.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import android.app.Activity;
import android.app.Application;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewStub;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
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
	 * 重新启动Activity,若是API5版本或者以上版本则将使用反射来关闭动画显示。<br>
	 * 并不能保证一定能关闭动画
	 */
	public void restartActivity()
	{
		Intent intent=mActivity.getIntent();
		if(Build.VERSION.SDK_INT>=5)//eclair
		{
			//hack to close animation http://stackoverflow.com/questions/1397361/how-do-i-restart-an-android-activity
			try {
				//prepare reflection
				Field field=Intent.class.getField("FLAG_ACTIVITY_NO_ANIMATION");
				Method method=Activity.class.getMethod("overridePendingTransition", int.class,int.class);
				
				method.invoke(mActivity, 0,0);
				intent.addFlags(field.getInt(null));
				mActivity.finish();
				
				method.invoke(mActivity, 0,0);
				mActivity.startActivity(intent);
				return;
			} catch (Exception ex){
				//ignore
			}
		}
		
		//otherwise
		mActivity.finish();
		mActivity.startActivity(intent);
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
	 * 通过反射和findViewById来获得所有该Activity下的成员变量。<br>
	 * id名必须与成员变量的名称一致。<br>
	 * 例如某layout文件中指定<code><TextView android:id="text"...></code>,则对应的Activity中期望包含<code>private TextView text;</code>
	 * 本方法已过时,请使用InjectView注解来进行注入。
	 */
	@Deprecated
	public void findAllViewFileds()
	{
		View decorView=getDecorView();
		if(decorView==null) throw new RuntimeException("请在setContentView(int layout)后调用本方法");
		
		Field[] fields=mActivity.getClass().getDeclaredFields();//由于reflect是允许期间反射的，所以尽管Activity是个通用基类，但是此处将反射得到自定义的子类字段。
		for (Field field : fields) {
			//仅判断View成员变量
			if(View.class.isAssignableFrom(field.getType())){
				testLog(field.getName());
				String idName=field.getName();
				View fieldView=findViewByIdName(idName);//找到idName对应的View
				field.setAccessible(true);//开启访问权限,可以private访问
				try {
					field.set(mActivity,fieldView);
				} catch (IllegalArgumentException e) {
					//ignore
				} catch (IllegalAccessException e) {
					//ignore
				}
			}
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
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findT(int)
	 */
	public TextView findT(int id) {
		return getmViewTool().findT(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findET(int)
	 */
	public EditText findET(int id) {
		return getmViewTool().findET(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findSP(int)
	 */
	public Spinner findSP(int id) {
		return getmViewTool().findSP(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findGV(int)
	 */
	public GridView findGV(int id) {
		return getmViewTool().findGV(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findLLayout(int)
	 */
	public LinearLayout findLLayout(int id) {
		return getmViewTool().findLLayout(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findPB(int)
	 */
	public ProgressBar findPB(int id) {
		return getmViewTool().findPB(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findB(int)
	 */
	public Button findB(int id) {
		return getmViewTool().findB(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findIB(int)
	 */
	public ImageButton findIB(int id) {
		return getmViewTool().findIB(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findST(int)
	 */
	public ViewStub findST(int id) {
		return getmViewTool().findST(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findWeb(int)
	 */
	public WebView findWeb(int id) {
		return getmViewTool().findWeb(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findLV(int)
	 */
	public ListView findLV(int id) {
		return getmViewTool().findLV(id);
	}

	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findVSW(int)
	 */
	public ViewSwitcher findVSW(int id) {
		return getmViewTool().findVSW(id);
	}


	/**
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findSV(int)
	 */
	public ScrollView findSV(int id) {
		return getmViewTool().findSV(id);
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
	 * @param id
	 * @return
	 * @see practice.utils.ViewTool#findIV(int)
	 */
	public ImageView findIV(int id) {
		return getmViewTool().findIV(id);
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
