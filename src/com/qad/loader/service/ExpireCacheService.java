package com.qad.loader.service;

import java.util.HashMap;

/**
 * 包装一个CacheService，通过指定一个过期时间来管理过期策略。<br>
 * 超出过期时间将会自动移除缓存并提示取缓存失败。
 * @author 13leaf
 *
 * @param <Param>
 * @param <Result>
 */
public class ExpireCacheService<Param,Result> extends BaseCacheLoadService<Param, Result> {

	private final long expiredTime;
	private BaseCacheLoadService<Param, Result> cacheLoadService;
	private HashMap<Param, Long> timeStamps=new HashMap<Param, Long>();
	
	/**
	 * 默认过期时间为5分钟
	 */
	public ExpireCacheService(BaseCacheLoadService<Param, Result> cacheService)
	{
		this(60L*1000*5,cacheService);
	}
	
	public ExpireCacheService(long expiredTime,BaseCacheLoadService<Param, Result> cacheLoadService)
	{
		if(cacheLoadService==null)
			throw new NullPointerException();
		this.expiredTime=expiredTime;
		this.cacheLoadService=cacheLoadService;
	}
	
	@Override
	public boolean saveCache(Param loadKey, Result instance) {
		boolean success=cacheLoadService.saveCache(loadKey, instance);
		if(success){
			timeStamps.put(loadKey, System.currentTimeMillis());
		}
		return success;
	}

	@Override
	public void clearCache() {
		cacheLoadService.clearCache();
		timeStamps.clear();
	}

	@Override
	public int length() {
		return cacheLoadService.length();
	}

	@Override
	protected Result onLoad(Param loadParam) {
		if(isExpired(loadParam))
		{
			onAbandonLoad(loadParam);
			return null;
		}
		return cacheLoadService.load(loadParam);
	}

	public boolean isExpired(Param param) {
		Long timeStamp=timeStamps.get(param);
		return timeStamp!=null && (System.currentTimeMillis()-timeStamp)>expiredTime;
	}

	@Override
	protected void onAbandonLoad(Param loadParam) {
		cacheLoadService.onAbandonLoad(loadParam);
		timeStamps.remove(loadParam);
	}

}
