package com.qad.loader.service;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;

/**
 * LoadService的工厂
 * 
 */
public class LoadServices {

	/**
	 * 优先从cache取,若cache没有记录才尝试载入
	 */
	public static final int FLAG_CACHE_FIRST = 0x1;

	/**
	 * 优先load,仅当load失败的时候才尝试走cache
	 */
	public static final int FLAG_LOAD_FIRST = 0x2;

	/**
	 * Http加载数据+softCache+FileCache两层缓存
	 * 
	 * @author 13leaf
	 * 
	 * @param <T>
	 */
	private static class Http2CacheService<T> extends
			BaseLoadService<String, T> {
		private BaseLoadService<String, T> loadService;

		private BaseCacheLoadService<String, T> cache1;

		private BaseCacheLoadService<String, T> cache2;

		private int flag;

		public Http2CacheService(BaseLoadService<String, T> service,
				BaseCacheLoadService<String, T> cache1,
				BaseCacheLoadService<String, T> cache2, int flag) {
			if (flag != FLAG_LOAD_FIRST && flag != FLAG_CACHE_FIRST) {
				throw new IllegalArgumentException("invalid flag:" + flag);
			}
			loadService = service;
			this.cache1 = cache1;
			this.cache2 = cache2;
			this.flag = flag;
		}

		@SuppressWarnings("unchecked")
		@Override
		public T onLoad(String url) {
			try {
				if (flag == FLAG_CACHE_FIRST) {
					T cache = LoadServiceUtil.tryLoadCache(url, cache1, cache2);
					if (cache != null) {
						cache1.saveCache(url, cache);// ensure cache1 saved
						return cache;// load from cache
					} else {
						T result = loadService.load(url);
						LoadServiceUtil.saveCache(url, result, cache1, cache2);// result为CacheLoadService?没可能吧....
						return result;
					}
				}
				if (flag == FLAG_LOAD_FIRST) {
					T result = null;
					try {
						result = loadService.load(url);// if faill,try load
														// caceh
					} catch (Exception e) {
						e.printStackTrace();
					}
					if (result != null) {
						LoadServiceUtil.saveCache(url, result, cache1, cache2);
						return result;
					} else {
						result = LoadServiceUtil.tryLoadCache(url, cache1,
								cache2);
						cache1.saveCache(url, result);// ensure cache1 saved
						return result;
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				throw new RuntimeException(e);
			}
			return null;//
		}

		@SuppressWarnings("unchecked")
		@Override
		public BaseCacheLoadService<String, T>[] getCacheServices() {
			return new BaseCacheLoadService[] { cache1, cache2 };
		}

		@Override
		public void onAbandonLoad(String loadParam) {
			cache1.abandonLoad(loadParam);
			cache2.abandonLoad(loadParam);
		}
	}

	/**
	 * 从Http载入纯文本数据,使用SoftCache+FileCache的两级缓存。
	 * 
	 * @param parser
	 *            解析者
	 * @param cacheDir
	 * @param flag
	 *            设置flag决定是cacheFirst还是loadFirst
	 * @return
	 */
	public static <T> BaseLoadService<String, T> newHttp2Cache(
			ParseAble<T> parser, File cacheDir, int flag) {
		return new Http2CacheService<T>(new HttpTextLoadService<T>(parser),
				new SoftCacheService<T>(), new PersistanceService<T>(cacheDir),
				flag);
	}

	/**
	 * 将一个LoadService包装成为SoftCache+PersistanceCache
	 * 
	 * @param helper
	 * @param parser
	 * @param cacheDir
	 * @param flag
	 * @return
	 */
	public static <T> BaseLoadService<String, T> wrapHttp2Cache(
			BaseLoadService<String, T> loadService, File cacheDir, int flag) {
		return new Http2CacheService<T>(loadService, new SoftCacheService<T>(),
				new PersistanceService<T>(cacheDir), flag);
	}

	/**
	 * 完整包装版本
	 * 
	 * @param loadService
	 * @param cache1
	 * @param cache2
	 * @param cacheDir
	 * @param flag
	 * @return
	 */
	public static <T> BaseLoadService<String, T> wrapHttp2Cache(
			BaseLoadService<String, T> loadService,
			BaseCacheLoadService<String, T> cache1,
			BaseCacheLoadService<String, T> cache2, File cacheDir, int flag) {
		if (loadService == null || cache1 == null || cache2 == null)
			throw new NullPointerException();
		return new Http2CacheService<T>(loadService, cache1, cache2, flag);
	}

	/**
	 * 从Http载入图片,使用SoftCache+FileCache的两级缓存
	 * 
	 * @param cacheDir
	 * @param context
	 * @return
	 */
	public static BaseLoadService<String, Bitmap> newHttpImage2Cache(
			File cacheDir, final Context context) {
		return new Http2CacheService<Bitmap>(new HttpResourceLoadService(
				context), new NoneCacheService<String, Bitmap>(),
				new PersistanceResourceService(cacheDir), FLAG_CACHE_FIRST);
	}

	public static BaseLoadService<String, Bitmap> newHttpImage2Cache(
			File cacheDir, int requiredSize, final Context context) {
		return new Http2CacheService<Bitmap>(new HttpResourceLoadService(
				context, requiredSize), new NoneCacheService<String, Bitmap>(),
				new PersistanceResourceService(cacheDir, requiredSize),
				FLAG_CACHE_FIRST);
	}

	/**
	 * 新建一个从Http载入数据,不包含缓存策略的载入服务。
	 * 
	 * @param parser
	 *            解析者
	 * @return
	 */
	public static <T> BaseLoadService<String, T> newHttpNoCache(
			ParseAble<T> parser) {
		return new HttpTextLoadService<T>(parser);
	}
}
