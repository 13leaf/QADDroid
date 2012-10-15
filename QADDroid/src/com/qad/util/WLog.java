package com.qad.util;

import android.util.Log;

/**
 * 封装Log的简易实现。使用对应的Class作为键值打印
 * @author 13leaf
 *
 */
public class WLog {
	
	private final String myName;
	
	private static final String TEST_TAG="13leaf";
	
	private static boolean closedGlobal=false;
	
	private boolean closed=false;
	
	public WLog(final Class<?> targetClazz)
	{
		myName=targetClazz.getSimpleName();
	}
	
	public static WLog getMyLogger(final Class<?> targetClazz)
	{
		return new WLog(targetClazz);
	}
	
	/**
	 * 关闭所有logger
	 */
	public static void closeAllLogger()
	{
		closedGlobal=true;
	}
	
	public void openLogger()
	{
		closed=false;
	}
	
	public void closeLogger()
	{
		closed=true;
	}
	
	
	/**
	 * 添加日志输出文件,用于调试使用。
	 * 日志的级别将用Debug级别，日志的Tag将用类名
	 * @param msg	要输出的日志内容
	 */
	public void debugLog(Object msg){
		if(!closed && !closedGlobal)
			Log.d(myName, msg+"");
	}
	
	/**
	 * 添加日志输出文件,用于调试使用。
	 * 日志的级别将用Error级别，日志的Tag将用类名
	 * @param msg	要输出的日志内容
	 */
	public void errorLog(Object msg){
		if(!closed && !closedGlobal)
			Log.e(myName,msg+"");
	}

	/**
	 * 使用13leaf作为专属的Tag。用作组件级别的调试
	 * @param msg
	 */
	public void testLog(Object msg){
		if(!closed && !closedGlobal)
			Log.d(TEST_TAG,msg+"");
	}
	
	public void warnLog(Object msg)
	{
		if(!closed && !closedGlobal)
			Log.w(myName,msg+"");
	}
	
	public void infoLog(Object msg)
	{
		if(!closed && !closedGlobal)
			Log.i(myName,msg+"");
	}
	
	public void verboseLog(Object msg)
	{
		if(!closed && !closedGlobal)
			Log.v(myName, msg+"");
	}

}
