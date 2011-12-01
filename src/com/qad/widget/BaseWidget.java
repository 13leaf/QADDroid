package com.qad.widget;

import com.qad.inject.ViewInjector;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

/**
 * 继承自最简单的FrameLayout。具备inject功能
 * @author 13leaf
 *
 */
public abstract class BaseWidget extends FrameLayout{

	public BaseWidget(Context context, AttributeSet attrs, int defStyle,int res) {
		super(context, attrs, defStyle);
		initializeComponent(res);
	}

	public BaseWidget(Context context, AttributeSet attrs,int res) {
		super(context, attrs,res);
		initializeComponent(res);
	}

	public BaseWidget(Context context,int res) {
		super(context);
		initializeComponent(res);
	}
	
	private void initializeComponent(int res)
	{
		LayoutInflater inflater=LayoutInflater.from(getContext());
		inflater.inflate(res, this);
		ViewInjector.inject(this, this);
		init();
	}
	
	/**
	 * 初始化入口点(包括xml inflate或create by context）
	 */
	protected abstract void init();

}
