package com.qad.theme;

import android.content.Context;
import android.view.View;

/**
 * TODO 建立一个主题框架。
 * 当接受到ConfigurationChange广播的时候更改前台的显示资源。
 * 目前仅考虑更换drawable
 * @author 13leaf
 *
 */
public class ThemeSelector {
	
	public ThemeSelector getInstance()
	{
		return null;
	}
	
	/*
	 * 根据主题路径来选择
	 */
	public void chooseTheme(String directory)
	{
		
	}
	
	/**
	 * 注册观测指定的view
	 * @param view
	 * @param recurision 通知时递归向下广播
	 */
	public void observeView(View view,boolean recurision)
	{
	}
	
	/**
	 * 广播通知
	 */
	public void notifyThemeChanged(Context foreground)
	{
		//当接受到ConfigChange的时候。及时通知View进行更新
	}
	
	public void filterQualifier()
	{
		
	}
}
