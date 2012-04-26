package com.qad.loader.service;

import java.util.HashMap;

/**
 * 强引用的存储方式
 * @author 13leaf
 *
 * @param <T>
 */
public class StrongCacheService<T> extends BaseCacheLoadService<String, T> {

	HashMap<String, T> strongCache=new HashMap<String, T>();
	
	@Override
	public boolean saveCache(String loadKey, T instance) {
		if(strongCache.containsKey(strongCache) || instance==null) {
			return false;
		}else {
			strongCache.put(loadKey, instance);
			return true;
		}
		
	}

	@Override
	public void clearCache() {
		strongCache.clear();
	}

	@Override
	public int length() {
		return strongCache.size();
	}

	@Override
	protected T onLoad(String loadParam) {
		return strongCache.get(loadParam);
	}

	@Override
	protected void onAbandonLoad(String loadParam) {
		strongCache.remove(loadParam);
	}

}
