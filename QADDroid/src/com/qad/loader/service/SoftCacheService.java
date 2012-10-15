package com.qad.loader.service;

import android.text.TextUtils;

import com.qad.cache.Cache;

/**
 * 软引用的缓存服务
 * @author 13leaf
 *
 * @param <T>
 */
public class SoftCacheService<T> extends BaseCacheLoadService<String,T>{

	private Cache<T> cache=new Cache<T>(); 
	
	public SoftCacheService()
	{
		logger.closeLogger();
	}
	
	@Override
	public boolean onPreLoad(String key) {
		return !TextUtils.isEmpty(key);
	}
	
	@Override
	public T onLoad(String key) {
		if(!onPreLoad(key))
			throw new IllegalArgumentException("invalidate Load Context!Check validateLoadContext First!"+key);
		T result=cache.get(key);
		if(result!=null)
			logger.debugLog("load "+key+" from softCache ok");
		else
			logger.warnLog("load "+key+" from softCache fail");
		return result;
	}

	@Override
	public boolean saveCache(String loadKey, T instance) {
		cache.put(loadKey, instance);
		logger.debugLog("save "+loadKey+" to softCache "+instance);
		return true;
	}

	@Override
	public void clearCache() {
		cache.clear();
	}

	@Override
	public void onAbandonLoad(String loadKey) {
		cache.put(loadKey, null);
		logger.debugLog("abadon "+loadKey);
	}

	@Override
	public int length() {
		return cache.size();
	}

}
