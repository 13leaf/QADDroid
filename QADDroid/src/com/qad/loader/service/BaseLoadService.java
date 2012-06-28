package com.qad.loader.service;
import com.qad.util.WLog;


public abstract class BaseLoadService<Param,Result> {
	
	protected WLog logger=WLog.getMyLogger(this.getClass());
	
	/**
	 * 准备载入
	 * @param url
	 * @return 返回true表示通过预验证,false表示不具备load条件
	 */
	protected boolean onPreLoad(Param loadParam)
	{
		return loadParam!=null;
	}
	
	/**
	 * 子类实现此处来完成载入数据的逻辑
	 * @param loadParam
	 * @return
	 */
	protected abstract Result onLoad(Param loadParam);
	
	/**
	 * 丢弃载入上下文的脏数据。避免因为Cache导致一直取得脏数据的问题。通常应该根据此方法来移除Cache数据<br>
	 * 如当过期或者验证是脏数据的时候应当丢弃Cache.
	 * @param loadParam
	 */
	protected abstract void onAbandonLoad(Param loadParam);
	
	/**
	 * 返回缓存服务
	 * @return
	 */
	public BaseCacheLoadService<Param, Result>[] getCacheServices()
	{
		return null;
	}
	
	
	private final boolean preLoad(Param loadParam)
	{
		return onPreLoad(loadParam);
	}
	
	//TODO 为load增加异常。如果出现异常,则可以将其放置进入result中。
	public final Result load(Param loadParam)
	{
		if(preLoad(loadParam))
		{
			return onLoad(loadParam);
		}else {
			logger.debugLog("invalidate loadParam "+loadParam);
			return null;
		}
	}
	
	public final void abandonLoad(Param loadParam)
	{
		onAbandonLoad(loadParam);
	}
	
}
