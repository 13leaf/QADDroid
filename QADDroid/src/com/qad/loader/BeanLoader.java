package com.qad.loader;

import java.io.Serializable;
import java.lang.annotation.Target;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;

import com.qad.loader.service.BaseCacheLoadService;
import com.qad.loader.service.BaseLoadService;

/**
 * 实体载入器。可以提供一个载入器根据实体类型动态执行不同的载入服务。
 * 在载入之前必须通过addLoadService配置LoadService与目标Parser之间的关系<br>
 * 注意,由于实现策略。载入应该使用startLoading(context,beanClass)的重载,使用startLoading(context)
 * 将会抛出异常。<br>
 * @author 13leaf
 */
public class BeanLoader<Target> extends ExecutorLoader<String, Target , Object> {

	private HashMap<Class<?>, BaseLoadService<String, ?>> loadMap = new HashMap<Class<?>, BaseLoadService<String, ?>>();
	private HashMap<LoadListener, Class<?>> listenerMap = new HashMap<LoadListener, Class<?>>();

	private static class NullLoadService extends
			BaseLoadService<String, Object> {
		@Override
		protected Object onLoad(String loadParam) {
			return null;
		}

		@Override
		protected void onAbandonLoad(String loadParam) {

		}
	}

	@SuppressWarnings("unchecked")
	public BeanLoader(ExecutorService threadPoolExecutor, BaseCacheLoadService<String,Target> cacheLoadService,int flag) {
		super(new NullLoadService(),(BaseCacheLoadService<String, Object>) cacheLoadService,threadPoolExecutor,flag);
	}
	
	public BeanLoader(ExecutorService threadPoolExecutor, int flag) {
		super(new NullLoadService(), threadPoolExecutor, flag);
	}

	public BeanLoader() {
		super(new NullLoadService());
	}

	public <T extends Serializable> void addListener(LoadListener listener,
			Class<T> classofT) {
		super.addListener(listener);
		listenerMap.put(listener, classofT);
	}
	
	@Override
	public void addListener(LoadListener listener) {
		throw new UnsupportedOperationException("Please use addListener(listener,classofT) instead of it.");
	}
	
	@Override
	public void removeListener(LoadListener listener) {
		throw new UnsupportedOperationException("Please use removeListener(listener,classofT) instead of it");
	}
	
	public <T extends Serializable> void removeListener(LoadListener listener,Class<T> classofT)
	{
		super.removeListener(listener);
		listenerMap.remove(listener);
	}

	public <T extends Serializable> void addLoadService(
			BaseLoadService<String, T> loadService, Class<T> classofT) {
		//当T为载入Class类型的时候，这里会出现问题。介于出现几率几乎不可能，因此这样写问题不大
		if (loadMap.containsKey(classofT)) {
			logger.warnLog("loadMap contains " + classofT.getSimpleName());
		}
		loadMap.put(classofT, loadService);
	}

	public final void startLoading(
			LoadContext<String, Target, Object> context,
			Class<? extends Serializable> beanClass) {
		context.result = beanClass;// interval set result
		startLoading(context);
	}

	@Override
	protected void notifyListeners(
			LoadContext<String, Target, Object> context, boolean success) {
		if (success) {
			Object result = context.result;
			for (LoadListener listener : listenerMap.keySet()) {
				Class<?> clazz = listenerMap.get(listener);
				if (result.getClass() == clazz)
					listener.loadComplete(context);
			}
		}else {
			Class<?> beanClass=(Class<?>) context.result;
			context.result=null;
			for(LoadListener listener:listenerMap.keySet())
			{
				Class<?> clazz=listenerMap.get(listener);
				if(beanClass == clazz)
					listener.loadFail(context);
			}
		}
	}
	
	@Override
	protected boolean validateResult(
			LoadContext<String, Target, Object> context) {
		return context.result!=null && !(context.result instanceof Class<?>);
	}

	@SuppressWarnings("unchecked")
	@Override
	protected boolean onLoading(LoadContext<String, Target, Object> context) {
		Class<?> beanClass = (Class<?>) context.result;
		if (beanClass == null) {
			throw new IllegalArgumentException(
					"Please use startLoading(context,beanClass) instead of startLoading(context)!");
		}
		BaseLoadService<String, ?> beanLoadService = loadMap.get(beanClass);
		if (beanLoadService == null) {
			throw new RuntimeException(
					String.format(
							"Have not yet config that %s's loadService,Please use addLoadService config firstly.",
							beanClass.getSimpleName()));
		}
		tasks.put(context, mExecutorService
				.submit(new LoadWorker<String, Target, Object>(
						(BaseLoadService<String, Object>) beanLoadService,
						context, mainHandler,this)));
		return true;
	}

}
