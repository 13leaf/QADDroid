package com.qad.util;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;

public class ViewTool {
	private View mView;
	
	public ViewTool(View view)
	{
		mView=view;
	}
	
	ViewTool()
	{
		
	}
	
	public View getView()
	{
		return mView;
	}
	
	/**
	 * 通过id名称，而非int型的id识别号来获取View。<br>
	 * 若查找失败，则会返回null。
	 * @param idName
	 * @return
	 */
	public View findViewByIdName(String idName)
	{
		int id=
			mView.getResources().getIdentifier(
				idName,"id",mView.getContext().getApplicationInfo().packageName);
		if(id==0) return null;
		else return mView.findViewById(id);
	}
	
	/**
	 * 设置装饰View，请勿随意调用
	 * @param decorView
	 */
	public void setDecorView(View decorView)
	{
		mView=decorView;
	}

	/**
	 * 方便将一组控件批量注册到同一个监听器。
	 */
	public void setOnClickListener(int[] ids,OnClickListener l)
	{
		for(int i=0;i<ids.length;i++)
			mView.findViewById(ids[i]).setOnClickListener(l);
	}
	
	/**
	 * 方便一组控件批量注册同一个监听器
	 * @param listener
	 * @param views
	 */
	public void setOnClickListener(OnClickListener listener,View... views)
	{
		for(int i=0;i<views.length;i++)
			views[i].setOnClickListener(listener);
	}
	
	public void setOnTouchListener(int[] ids,OnTouchListener l)
	{
		for(int i=0;i<ids.length;i++)
			mView.findViewById(ids[i]).setOnTouchListener(l);
	}
}
