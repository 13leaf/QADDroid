package com.qad.loader;

import android.view.View;

/**
 * 提供切换状态视图的能力
 * @author 13leaf
 *
 */
public interface StateAble {
	int STATE_LOADING=1;
	int STATE_NORMAL=2;
	int STATE_RETRY=3;
	
	int getCurrentState();
	void showLoading();
	void showNormal();
	void showRetryView();
	/**
	 * 设置引发重试事件的视图
	 * @param view
	 */
	void setRetryTrigger(View view);
	/**
	 * 设置重试监听
	 * @param listener
	 */
	void setOnRetryListener(onRetryListener listener);
}
