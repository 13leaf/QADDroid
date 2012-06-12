package com.qad.loader.service;

/**
 * 无缓存。
 * @author 13leaf
 *
 * @param <Param>
 * @param <Result>
 */
public class NoneCacheService<Param,Result> extends BaseCacheLoadService<Param, Result> {
	@Override
	public boolean saveCache(Param loadKey, Result instance) {
		return true;
	}

	@Override
	public void clearCache() {
		
	}

	@Override
	public int length() {
		return 0;
	}

	@Override
	protected Result onLoad(Param loadParam) {
		return null;
	}

	@Override
	protected void onAbandonLoad(Param loadParam) {
		
	}
}
