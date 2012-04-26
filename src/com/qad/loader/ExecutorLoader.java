package com.qad.loader;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import android.os.Handler;
import android.os.Message;
import android.os.Process;

import com.qad.loader.service.BaseLoadService;

/**
 * 基于线程池控制的加载器
 * 
 * @author 13leaf
 * 
 * @param <Param>
 * @param <Target>
 * @param <Result>
 */
public class ExecutorLoader<Param, Target, Result> extends
		AbstractLoader<Param, Target, Result> {

	protected ExecutorService mExecutorService;
	protected HashMap<LoadContext<Param, Target, Result>, Future<Result>> tasks = new HashMap<LoadContext<Param, Target, Result>, Future<Result>>();

	static class LoadWorker<Param, Target, Result> implements Callable<Result> {
		BaseLoadService<Param, Result> service;
		LoadContext<Param, Target, Result> context;
		Handler mainHandler;

		public LoadWorker(BaseLoadService<Param, Result> service,
				LoadContext<Param, Target, Result> context, Handler mainHandler) {
			this.service = service;
			this.context = context;
			this.mainHandler = mainHandler;
		}

		@Override
		public Result call() throws Exception {
			Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);
			Result result = service.load(context.param);
			if (result != null)
				context.result = result;
			Message message = Message.obtain(mainHandler);
			message.obj = context;
			message.sendToTarget();
			return result;
		}
	}

	public ExecutorLoader(BaseLoadService<Param, Result> loadService,
			ExecutorService threadPoolExecutor, int flag) {
		super(loadService, flag);
		this.mExecutorService = threadPoolExecutor;
	}

	/**
	 * 默认使用CachedThreadPool
	 * 
	 * @param loadService
	 */
	public ExecutorLoader(BaseLoadService<Param, Result> loadService) {
		super(loadService);
		mExecutorService = Executors.newCachedThreadPool();
	}

	@Override
	protected boolean onLoading(LoadContext<Param, Target, Result> context) {
		/*
		 * ignore flag,executorloader just do asynctask one by one
		 * if((flag&FLAG_FIFO) ==FLAG_FIFO) {
		 * 
		 * }else if((flag&FLAG_LIFO) == FLAG_LIFO) {
		 * 
		 * }
		 */
		tasks.put(context, mExecutorService
				.submit(new LoadWorker<Param, Target, Result>(loadService,
						context, mainHandler)));
		return true;
	}

	@Override
	protected void onPause() {
		for (Future<Result> future : tasks.values()) {
			future.cancel(true);
		}
	}

	@Override
	protected void onResume() {
		LinkedList<LoadContext<Param, Target, Result>> temp = new LinkedList<LoadContext<Param, Target, Result>>();
		for (LoadContext<Param, Target, Result> aTask : tasks.keySet()) {
			temp.add(aTask);
		}
		tasks.clear();
		for (LoadContext<Param, Target, Result> loadContext : temp) {
			startLoading(loadContext);
		}
	}

	@Override
	protected void done(LoadContext<Param, Target, Result> context) {
		super.done(context);
		tasks.remove(context);
	}

	@Override
	protected void onAbandon(LoadContext<Param, Target, Result> context) {
		loadService.abandonLoad(context.param);
	}

	@Override
	protected void onDestroy(boolean now) {
		if (now)
			mExecutorService.shutdownNow();
		else
			mExecutorService.shutdown();
		mExecutorService = null;
	}

	@Override
	protected boolean onCancelLoading(LoadContext<Param, Target, Result> context) {
		Future<Result> future=tasks.get(context);
		if(future!=null)
			future.cancel(true);
		return false;
	}

}
