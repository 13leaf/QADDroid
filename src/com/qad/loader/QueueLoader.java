package com.qad.loader;

import java.util.ArrayList;

import android.os.Handler;
import android.os.Handler.Callback;
import android.os.HandlerThread;
import android.os.Message;
import android.os.Process;

import com.qad.loader.service.BaseCacheLoadService;
import com.qad.loader.service.BaseLoadService;

/**
 * 使用基于Handler的多线程模式。以LIFO的形式执行任务,换句话说,总是以最后发出的请求为优先响应
 * 
 * @author 13leaf
 * 
 */
public class QueueLoader<Param, Target, Result> extends
		AbstractLoader<Param, Target, Result> {

	private HandlerThread thread = null;

	private Handler mHandler;

	private static final int MSG_LOADING = 1;

	/**
	 * 默认使用LIFO的方式执行任务
	 * 
	 * @param loadService
	 * @param flag
	 */
	public QueueLoader(BaseLoadService<Param, Result> loadService) {
		this(loadService, FLAG_ADD_OR_RESORT | FLAG_LIFO);
	}

	public QueueLoader(BaseLoadService<Param, Result> loadService, int flag) {
		super(loadService, flag);
	}

	public QueueLoader(BaseLoadService<Param, Result> loadService,
			BaseCacheLoadService<Param, Result> cacheLoadService) {
		super(loadService, cacheLoadService);
	}

	public QueueLoader(BaseLoadService<Param, Result> loadService,
			BaseCacheLoadService<Param, Result> cacheLoadService, int flag) {
		super(loadService, cacheLoadService, flag);
	}

	@Override
	protected boolean onLoading(LoadContext<Param, Target, Result> context) {
		if (thread == null) {
			thread = new HandlerThread("sequenceLoader",Process.THREAD_PRIORITY_BACKGROUND);
			thread.start();
			mHandler = new Handler(thread.getLooper(), loadHandler);// add call
																	// back
		}
		// send message to start new task
		Message message = Message.obtain(mHandler);
		message.what = MSG_LOADING;
		message.obj = context;
		if ((flag & FLAG_LIFO) == FLAG_LIFO)
			mHandler.sendMessageAtFrontOfQueue(message);// 插队通知
		else
			mHandler.sendMessage(message);// 排队通知
		return true;
	}

	public void clearQueue() {
		mHandler.removeMessages(MSG_LOADING);
	}

	@Override
	protected void onPause() {
		mHandler.removeMessages(MSG_LOADING);
	}

	@Override
	protected void onResume() {
		ArrayList<LoadContext<Param, Target, Result>> saveState=new ArrayList<LoadContext<Param, Target, Result>>(submitedTask);
		submitedTask.clear();
		for (LoadContext<Param, Target, Result> loadContext : saveState) {
			startLoading(loadContext);
		}
	}

	@Override
	protected void onAbandon(LoadContext<Param, Target, Result> context) {
		loadService.abandonLoad(context.param);
	}

	@Override
	protected void onDestroy(boolean now) {
		if (mHandler!=null) {
			mHandler.getLooper().quit();
		}
		mHandler = null;
		loadHandler = null;
	}

	private Callback loadHandler = new Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			if (getState() == State.DESTROYED)
				return true;
			@SuppressWarnings("unchecked")
			LoadContext<Param, Target, Result> context = (LoadContext<Param, Target, Result>) msg.obj;
			context.result = loadService.load(context.param);
			Message message = new Message();
			message.obj = context;
			sendToMainThread(message);
			return true;// will not send message to Handler
		}
	};

	@Override
	protected boolean onCancelLoading(LoadContext<Param, Target, Result> context) {
		int index=submitedTask.indexOf(context);
		if(index!=-1)
		{
			mHandler.removeMessages(MSG_LOADING, submitedTask.get(index));//remove token
		}
		return false;
	}

}
