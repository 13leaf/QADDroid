package com.qad.util;

import android.view.View;
import android.view.ViewStub;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.webkit.WebView;
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

	public TextView findT(int id) {
		return (TextView) mView.findViewById(id);
	}
	public ImageView findIV(int id)
	{
		return (ImageView) mView.findViewById(id);
	}
	public EditText findET(int id) {
		return (EditText) mView.findViewById(id);
	}
	public Spinner findSP(int id) {
		return (Spinner) mView.findViewById(id);
	}
	public GridView findGV(int id) {
		return (GridView) mView.findViewById(id);
	}
	public LinearLayout findLLayout(int id) {
		return (LinearLayout) mView.findViewById(id);
	}
	public ProgressBar findPB(int id) {
		return (ProgressBar) mView.findViewById(id);
	}
	public Button findB(int id) {
		return (Button) mView.findViewById(id);
	}
	public ImageButton findIB(int id) {
		return (ImageButton) mView.findViewById(id);
	}
	public ViewStub findST(int id) {
		return (ViewStub) mView.findViewById(id);
	}
	public WebView findWeb(int id) {
		return (WebView) mView.findViewById(id);
	}
	public ListView findLV(int id) {
		return (ListView) mView.findViewById(id);
	}
	public ViewSwitcher findVSW(int id) {
		return (ViewSwitcher) mView.findViewById(id);
	}
	public ScrollView findSV(int id){
		return (ScrollView) mView.findViewById(id);
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
