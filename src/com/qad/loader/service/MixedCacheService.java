package com.qad.loader.service;

import java.io.Serializable;
import java.util.HashMap;

/**
 * 混合缓存方式,使用某种策略强制缓存一部分内容。
 * 使用命中率计数的方式强制缓存一部分最多使用的信息。
 * @author 13leaf
 *
 * @param <T>
 */
public class MixedCacheService<T> extends BaseCacheLoadService<String, T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6670369844935866066L;

	private static int DEFAULT_CACHE_SIZE=20;
	
	private transient SoftCacheService<T> cache2=new SoftCacheService<T>();
	private StrongCacheService<T> cache1=new StrongCacheService<T>();
	
	
	private final int maxSize;
	private HashMap<String, Integer> loadCount=new HashMap<String, Integer>();
	
	public MixedCacheService()
	{
		maxSize=DEFAULT_CACHE_SIZE;
	}
	
	public MixedCacheService(int maxSize)
	{
		this.maxSize=maxSize;
	}
	
	/**
	 * 为了保证序列化的可用性，应当在反序列化后重新设置。
	 */
	public void resetSoftCache()
	{
		cache2=new SoftCacheService<T>();
	}
	
	@Override
	public boolean saveCache(String loadKey, T instance) {
		ensureShrinkStrong();
		boolean success=false;
		success|=saveAndRecordStrongCache(loadKey,instance);
		success|=cache2.saveCache(loadKey, instance);
		return success;
	}

	@Override
	public void clearCache() {
		cache1.clearCache();
		cache2.clearCache();
		loadCount.clear();
	}

	@Override
	public int length() {
		return cache2.length();
	}

	@Override
	protected T onLoad(String loadParam) {
		T result=cache1.load(loadParam);
		if(result!=null)//
		{
			int count=loadCount.get(loadParam);
			loadCount.put(loadParam, ++count);
			logger.debugLog(String.format("strong cached %s count:%s", loadParam,count));
			return result;
		}
		//try load softcache
		result=cache2.load(loadParam);
		if(result!=null)
		{
			ensureShrinkStrong();
			saveAndRecordStrongCache(loadParam,result);
			return result;
		}
		return null;
	}

	private boolean saveAndRecordStrongCache(String loadParam, T result) {
		loadCount.put(loadParam, 0);
		return cache1.saveCache(loadParam, result);
	}

	private void ensureShrinkStrong() {
		if(cache1.length()<maxSize) return;//don't need shrink
		int minCount=Integer.MAX_VALUE;
		String minParam="";
		for(String param:loadCount.keySet())
		{
			int t=loadCount.get(param);
			if(t<minCount)
			{
				minCount=t;
				minParam=param;
				if(minCount==0) break;
			}
		}
		loadCount.remove(minParam);
		cache1.abandonLoad(minParam);//remove minParam out
		logger.debugLog(String.format("shrink strong and remove :%s,count:%s",minParam,minCount));
	}

	@Override
	protected void onAbandonLoad(String loadParam) {
		cache1.abandonLoad(loadParam);
		cache2.abandonLoad(loadParam);
		loadCount.remove(loadParam);
	}

}
