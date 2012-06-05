package com.qad.render;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

/**
 * 基于layout资源inflate的默认工厂
 * @author 13leaf
 *
 */
public class DefaultViewFactory implements ViewFactory{

	private int layout;
	
	public DefaultViewFactory(int layout)
	{
		this.layout=layout;
	}
	
	@Override
	public View createView(Context context,int position) {
		LayoutInflater inflater=LayoutInflater.from(context);
		return inflater.inflate(layout, null);
	}

	@Override
	public void render(View view,Object data,int position) {
	}
}
