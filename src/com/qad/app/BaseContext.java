package com.qad.app;

import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;

import com.qad.util.ContextTool;


public class BaseContext {

	/**
	 * 指向本地引用
	 */
	private Context me;
	
	private ContextTool tool;
	
	public void infoLog(Object msg) {
		tool.infoLog(msg);
	}

	public void warnLog(Object msg) {
		tool.warnLog(msg);
	}

	public void verboseLog(Object msg) {
		tool.verboseLog(msg);
	}

	public BaseContext(Context base) {
		me=base;
		tool=new ContextTool(base);
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
	 * 获取当前程序中的本地目标
	 * @param localIntent
	 * @return
	 */
	public Intent getLocalIntent(Class<?extends Context> localIntent)
	{
		return new Intent(me,localIntent);
	}
	
	/**
	 * 以无参数的模式启动Activity。
	 * @param activityClass
	 */
	public void startActivity(Class<? extends Activity> activityClass) {
		me.startActivity(
				getLocalIntent(activityClass));
	}
	
	
	/**
	 * 以无参数的模式启动service
	 */
	public void startService(Class<? extends Service> serviceClass) {
		me.startService(
				getLocalIntent(serviceClass));
	}
	
	/**
	 * 以绑定方式启动service。通过返回的绑定连接实例可以进行通信<br>
	 * 另请参照 @see {@link BindServiceConnection}
	 * @param <T> IBinder强类型
	 * @param intentService 目标服务
	 * @return 绑定连接
	 */
	public <T extends IBinder> BindServiceConnection<T> bindService(Intent intentService) {
		BindServiceConnection<T> conn=new BindServiceConnection<T>();
		me.bindService(intentService, conn, Context.BIND_AUTO_CREATE);
		return conn;
	}
	
	/**
	 * 很容易的绑定一个本地服务。
	 * @param <T>
	 * @param serviceClass
	 * @return
	 */
	public <T extends IBinder> BindServiceConnection<T> bindService(Class<?extends Service> serviceClass) {
		Intent intent=new Intent(me,serviceClass);
		return bindService(intent);
	}
	
	/**
	 * 以无参数的模式关闭service。<br>
	 * <strong>对于bind启动的服务，需要释放所有的ServiceConnection才可关闭。</strong>
	 * @param serviceClass
	 */
	public void stopService(Class<? extends Service> serviceClass) {
		Intent intent=new Intent(me,serviceClass);
		me.stopService(intent);
	}

	/**
	 * @param msg
	 * @see practice.utils.ContextTool#showMessage(java.lang.Object)
	 */
	public void showMessage(Object msg) {
		tool.showMessage(msg);
	}

	/**
	 * @param msg
	 * @see practice.utils.ContextTool#showLongMessage(java.lang.Object)
	 */
	public void showLongMessage(Object msg) {
		tool.showLongMessage(msg);
	}

	/**
	 * @param msg
	 * @see practice.utils.ContextTool#debugLog(java.lang.Object)
	 */
	public void debugLog(Object msg) {
		tool.debugLog(msg);
	}

	/**
	 * @param msg
	 * @see practice.utils.ContextTool#errorLog(java.lang.Object)
	 */
	public void errorLog(Object msg) {
		tool.errorLog(msg);
	}

	/**
	 * @param msg
	 * @see practice.utils.ContextTool#testLog(java.lang.Object)
	 */
	public void testLog(Object msg) {
		tool.testLog(msg);
	}
}
