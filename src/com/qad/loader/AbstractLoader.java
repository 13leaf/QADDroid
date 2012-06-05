package com.qad.loader;

import java.util.ArrayList;
import java.util.HashSet;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.qad.loader.service.BaseCacheLoadService;
import com.qad.loader.service.BaseLoadService;
import com.qad.util.WLog;

/**
 * 所有的Loader方法应当都在同一线程调用
 * 
 * @author 13leaf
 * 
 * @param <Param>
 * @param <Target>
 * @param <Result>
 */
public abstract class AbstractLoader<Param, Target, Result> implements
		Loader<Param, Target, Result> {

	private HashSet<LoadListener> listeners = new HashSet<LoadListener>();
	
	private LoaderCallback callback;

	/**
	 * 若设置了该Flag,则会过滤掉请求,当它们传入的参数和目标与发出的执行任务都一样时。
	 */
	public static final int FLAG_FILTER_DUPLICATE = 0x0001;

	/**
	 * 若设置了该Flag,则不会过滤请求。但会终止排队任务，并将最新任务重排到最前
	 */
	public static final int FLAG_ADD_OR_RESORT = 0x0002;

	/**
	 * 最后提交的任务总是优先执行
	 */
	public static final int FLAG_LIFO = 0x0010;

	/**
	 * 最先提交的任务总是最先执行
	 */
	public static final int FLAG_FIFO = 0x0020;

	protected ArrayList<LoadContext<Param, Target, Result>> submitedTask = new ArrayList<LoadContext<Param, Target, Result>>();

	protected final int flag;

	protected WLog logger = WLog.getMyLogger(getClass());

	protected final BaseLoadService<Param, Result> loadService;
	
	protected final BaseCacheLoadService<Param, Result> cacheLoadService;

	protected final static int MSG_DONE = 0;

	protected Object lock = new Object();

	/**
	 * 异步线程通过调用mainHandler来向主线程通知done
	 */
	protected Handler mainHandler = new Handler(Looper.getMainLooper()) {
		@SuppressWarnings("unchecked")
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_DONE:
				done((LoadContext<Param, Target, Result>) msg.obj);
				break;
			default:
				logger.errorLog("UnExpect message type:" + msg.what);
				break;
			}
		}
	};

	public static enum State {
		VIRGIN, // 尚未启动
		RUNNING, // 运行中
		PAUSING, // 暂停
		DESTROYED// 已经关闭
	}

	protected State state = State.VIRGIN;

	/**
	 * 返回当前的Loader状态
	 * 
	 * @return
	 */
	public State getState() {
		return state;
	}
	
	public void setCallback(LoaderCallback callback) {
		this.callback = callback;
	}

	/**
	 * 与Loader关联的Load服务<br>
	 * 默认flag:当任务已经提交但尚未运行时,将过滤掉重复的请求。任务加载顺序为优先加载最后提交的任务
	 * 
	 * @param loadService
	 */
	public AbstractLoader(BaseLoadService<Param, Result> loadService) {
		this(loadService, null,FLAG_FILTER_DUPLICATE | FLAG_LIFO);
	}
	
	public AbstractLoader(BaseLoadService<Param, Result> loadService,BaseCacheLoadService<Param, Result> cacheLoadService) {
		this(loadService, cacheLoadService,FLAG_FILTER_DUPLICATE | FLAG_LIFO);
	}
	
	public AbstractLoader(BaseLoadService<Param, Result> loadService,int flag) {
		this(loadService,null,flag);
	}

	/**
	 * 
	 * @param loadService
	 * @param cacheLoadService
	 * @throws NullPointerException
	 *             loadService为空
	 * @throws IllegalArgumentException
	 *             flag设置不正确
	 */
	public AbstractLoader(BaseLoadService<Param, Result> loadService, BaseCacheLoadService<Param, Result> cacheLoadService,int flag) {
		if (loadService == null)
			throw new NullPointerException("loadService can not be null!");
		if ((flag & (FLAG_FIFO | FLAG_LIFO)) == (FLAG_FIFO | FLAG_LIFO)) {
			throw new IllegalArgumentException(
					"Can not be set both flag FLAG_FIFO and FLAG_LIFO");
		}
		if ((flag & (FLAG_FILTER_DUPLICATE | FLAG_ADD_OR_RESORT)) == (FLAG_ADD_OR_RESORT | FLAG_FILTER_DUPLICATE)) {
			throw new IllegalArgumentException(
					"Can not be set both flag FLAG_FILTER_DUPLICATE and FLAG_ADD_OR_RESORT");
		}
		if ((flag & FLAG_LIFO) == 0 && (flag & FLAG_FIFO) == 0) {
			logger.warnLog("Unset sequence Flag,use default LIFO");
			flag = flag | FLAG_LIFO;
		}
		this.cacheLoadService=cacheLoadService;
		this.loadService = loadService;
		this.flag = flag;
	}

	public BaseLoadService<Param, Result> getLoadService() {
		return loadService;
	}
	
	public final void startLoading(Param param,Target target)
	{
		startLoading(new LoadContext<Param, Target, Result>(param, target));
	}

	@Override
	public final void startLoading(LoadContext<Param, Target, Result> context) {
		if (context == null || context.param == null)
			throw new NullPointerException("context param couldn't be null!");
		if (state == State.DESTROYED)
			throw new IllegalStateException("Loader destroyed!");
		// filter duplicate
		if ((flag & FLAG_FILTER_DUPLICATE) == FLAG_FILTER_DUPLICATE) {
			if (submitedTask.contains(context)) {
				logger.debugLog("detect duplicate request,filter it");
				return;
			} else {
				submitedTask.add(new LoadContext<Param,Target,Result>(context));
			}
		} else if ((flag & FLAG_ADD_OR_RESORT) == FLAG_ADD_OR_RESORT) {
			cancelLoading(context);
			submitedTask.add(new LoadContext<Param,Target,Result>(context));
		}
		if (state == State.PAUSING)
			return;
		//try loading by cache
		if(cacheLoadService!=null){
			Result result=cacheLoadService.load(context.param);
			if(result!=null)
			{
				context.result=result;
				done(context);
			}
		}
		boolean accept=onLoading(context);
		if (accept){
			state = State.RUNNING;
		}
		if(callback!=null){
			callback.onLoading(accept);
		}
	}

	@Override
	public final boolean cancelLoading(
			LoadContext<Param, Target, Result> context) {
		if (state == State.DESTROYED)
			throw new IllegalStateException("Loader destroyed!");
		submitedTask.remove(context);
		return onCancelLoading(context);
	}

	@Override
	public final void destroy(boolean now) {
		synchronized (lock) {
			state = State.DESTROYED;
			onDestroy(now);// let subClass destroy first
			if(callback!=null)
				callback.onDestroy();
			
			// remove all pending messages
			shutdownMainHandler();
			submitedTask.clear();
			submitedTask = null;
			callback=null;
			if(cacheLoadService!=null)
				cacheLoadService.clearCache();
		}
	}
	
	/**
	 * 从异步线程向UI线程发送消息
	 * @param message
	 */
	protected void sendToMainThread(Message message)
	{
		synchronized (lock) {
			message.setTarget(mainHandler);
			message.sendToTarget();
		}
	}

	/**
	 * 移除mainHandler所有处理的消息,释放mainHandler
	 */
	private void shutdownMainHandler() {
		if (mainHandler != null) {// maybe subclass remove it
			mainHandler.removeMessages(MSG_DONE);
		}
		mainHandler = null;
	}

	/**
	 * 挂起加载任务线程,调用该方法可能是为了给UI线程让步,以便提高相应速度。<br>
	 * 由于挂起状态的不可控,子类通常采取简单粗暴的停止任务,然后再回复阶段重新启动的方式。
	 */
	public final void pauseLoading() {
		if (state == State.DESTROYED)
			throw new IllegalStateException("Loader has destroyed!");
		if (state != State.RUNNING) {
			logger.errorLog("Invalidate state " + state);
			return;
		}
		state = State.PAUSING;
		onPause();
	}

	/**
	 * 回复执行加载任务线程
	 */
	public final void resumeLoading() {
		if (state == State.DESTROYED)
			throw new IllegalStateException("Loader has destroyed!");
		if (state != State.PAUSING) {
			logger.errorLog("Invalidate state " + state);
			return;
		}
		state = State.RUNNING;
		onResume();
	}

	/**
	 * 若验证载入结果无用，执行忽略。若任务还在队列，则移除任务。
	 * 
	 * @param context
	 */
	private final void abandon(LoadContext<Param, Target, Result> context) {
		if (state == State.DESTROYED)
			throw new IllegalStateException("Loader has destroyed!");
		if(cacheLoadService!=null)
			cacheLoadService.abandonLoad(context.param);
		onAbandon(context);
	}

	@Override
	public void addListener(LoadListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeListener(LoadListener listener) {
		listeners.remove(listener);
	}

	/**
	 * 当加载任务完成时被调用,该函数调用应当在<b>主线程</b>中<br>
	 * done至少需要做两件事:
	 * <ol>
	 * <li>检查当前上下文是否正常,若不正常则过滤回调。</li>
	 * <li>检查返回结果是否正确,若不正确则通知丢弃载入数据并且通知失败。否则通知成功</li>
	 * </ol>
	 */
	protected void done(LoadContext<Param, Target, Result> context) {
		// done task,remove it
		if ((flag & FLAG_FILTER_DUPLICATE) == FLAG_FILTER_DUPLICATE) {
			submitedTask.remove(context);
		}
		// filter if invalidate
		if (!validateTarget(context)) {
			logger.debugLog("invalidate target,won't callback!");
			return;
		}

		if (!validateResult(context)) {
			logger.debugLog("invalidate result ,abandon it!");
			abandon(context);
			notifyListeners(context, false);
			if(callback!=null) callback.onLoadFail();
		} else {
			notifyListeners(context, true);
			if(callback!=null) callback.onLoadComplete();
			if(cacheLoadService!=null)
				cacheLoadService.saveCache(context.param, context.result);
		}
	}

	/**
	 * 子类可以重写此处来拦截成功广播
	 * 
	 * @param context
	 */
	protected void notifyListeners(LoadContext<Param, Target, Result> context,
			boolean success) {
		for (LoadListener listener : listeners) {
			if (success)
				listener.loadComplete(context);
			else
				listener.loadFail(context);
		}
	}

	/**
	 * 验证返回结果是否正确。子类重写此处来验证是否出现了脏数据,默认实现是检查Result非空。返回false将阻止给LoadListener回调通知
	 * 
	 * @return
	 */
	protected boolean validateResult(LoadContext<Param, Target, Result> context) {
		return context.result != null;
	}

	/**
	 * 验证当前target是否正常。子类重写此处来验证是否当前上下文不正确,默认实现是检查target非空。
	 * 返回false将阻止给LoadListener回调通知
	 * 
	 * @param context
	 * @return
	 */
	protected boolean validateTarget(LoadContext<Param, Target, Result> context) {
		return context.target != null;
	}

	/**
	 * 提供载入上下文的调试支持
	 * 
	 * @param context
	 * @return
	 */
	public String dumpContext(LoadContext<Param, Target, Result> context) {
		return context.toString();
	}

	/**
	 * 返回true表示Loader接收请求并开始执行任务
	 * 
	 * @param context
	 * @return
	 */
	protected abstract boolean onLoading(
			LoadContext<Param, Target, Result> context);

	/**
	 * 返回是否取消成功
	 * 
	 * @param context
	 * @return
	 */
	protected abstract boolean onCancelLoading(
			LoadContext<Param, Target, Result> context);

	protected abstract void onPause();

	protected abstract void onResume();

	protected abstract void onAbandon(LoadContext<Param, Target, Result> context);

	protected abstract void onDestroy(boolean now);

}
