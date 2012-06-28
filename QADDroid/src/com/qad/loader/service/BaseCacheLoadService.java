package com.qad.loader.service;

/**
 * 通过load来获取cache,通过saveCache来保存实例Cache,通过clearCache来清空Cache.
 * @author 13leaf
 *
 * @param <Param>
 * @param <Result>
 */
public abstract class BaseCacheLoadService<Param,Result> extends BaseLoadService<Param, Result>{

	/**
	 * 保存缓存对象
	 * @param instance
	 * @return true表示缓存成功,false表示缓存失败
	 */
	public abstract boolean saveCache(Param loadKey,Result instance);
	
	/**
	 * 清空缓存
	 */
	public abstract void clearCache();
	
	/**
	 * 获得缓存数据的大小
	 * @return
	 */
	public abstract int length();
	
}
