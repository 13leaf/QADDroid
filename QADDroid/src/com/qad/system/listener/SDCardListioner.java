package com.qad.system.listener;

/**
 * 关于SD挂载类的监听器
 * @author 13leaf
 *
 */
public interface SDCardListioner {

	/**
	 * SD卡移除的时候进行回调通知。<br>
	 * 包括被拔出或者连接USB共享两种情况
	 */
	void onSDCardRemoved();
	/**
	 * SD卡载入时的回调通知
	 */
	void onSDCardMounted();
	
}
