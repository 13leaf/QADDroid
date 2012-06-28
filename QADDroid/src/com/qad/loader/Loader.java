package com.qad.loader;

/**
 * Loader的设计目标是封装线程任务的调度,排序。
 * @param <Param> 请求参数
 * @param <Target> 回调通知的目标,通常它应该是一个视图(View)或者Activity
 * @param <Result> 载入结果
 * @author 13leaf
 *
 */
public interface Loader<Param,Target,Result> {
	/**
	 * 发起载入的异步请求。
	 * @param context
	 */
	void startLoading(LoadContext<Param,Target,Result> context);
	
	/**
	 * 取消该上下文的任务
	 * @param context
	 */
	boolean cancelLoading(LoadContext<Param, Target, Result> context);
	
	/**
	 * 破坏Loader。终止所有异步线程和任务
	 * 
	 * @param now 指示是否希望立即破坏。(当有任务正在执行时若设置为now则应尝试中断)
	 */
	void destroy(boolean now);
	
	/**
	 * 添加载入完成的回调
	 * @param listener
	 */
	void addListener(LoadListener listener);
	
	/**
	 * 移除载入回调
	 * @param listener
	 */
	void removeListener(LoadListener listener);
}
