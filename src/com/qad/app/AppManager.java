package com.qad.app;

import android.app.Activity;

/**
 * 实现与程序密切相关的功能
 * @author 13leaf
 *
 */
public interface AppManager {

	/**
	 * 获取当前引用的顶层活动
	 * @return
	 */
	Activity getTopActivity();
}
