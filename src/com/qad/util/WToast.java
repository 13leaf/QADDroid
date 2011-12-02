package com.qad.util;

import java.lang.ref.WeakReference;

import android.content.Context;
import android.widget.Toast;

/**
 * 封装Toast的简易实现
 * @author 13leaf
 *
 */
public class WToast {

	private final WeakReference<Context> context;
	
	public WToast(final Context context) {
		this.context=new WeakReference<Context>(context);
	}
	
	/**
	 * 在底部显示一条toast信息,大约3秒钟时间。<br>
	 * 若想让toast显示时间较长，请调用showLongMessage
	 * @param msg
	 */
	public void showMessage(Object msg)
	{
		if(context.get()!=null)
			Toast.makeText(context.get(), msg+"", Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 以较长的时间来toast显示，大约5秒钟显示。
	 * @param msg
	 */
	public void showLongMessage(Object msg)
	{
		if(context.get()!=null)
			Toast.makeText(context.get(), msg+"", Toast.LENGTH_LONG).show();
	}
	
}
