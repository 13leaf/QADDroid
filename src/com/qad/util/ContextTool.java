package com.qad.util;

import android.content.Context;

/**
 * 上下文实用工具。比如消息提示，日志输出等。
 * 日志输出的Tag使用当前上下文的具体类名。
 * @author 13leaf
 *
 */
public class ContextTool {

	private WToast toast;
	
	private WLog log;
	
	public ContextTool(Context base)
	{
		//初始化Toast
		toast=new WToast(base);
		log=new WLog(base.getClass());
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
