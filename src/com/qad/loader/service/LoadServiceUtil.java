package com.qad.loader.service;

import java.util.regex.Pattern;

import android.text.TextUtils;

public abstract class LoadServiceUtil {
	
	private static Pattern httpPattern=Pattern.compile("^http://\\S+[^/]$");
	
	/**
	 * 验证是否为http地址
	 * @param url
	 * @return
	 */
	public static boolean validateHttpUrl(CharSequence url)
	{
		return !TextUtils.isEmpty(url) && httpPattern.matcher(url).matches();
	}
	
	/**
	 * 按照CacheService的顺序来存入缓存
	 * @param loadParam
	 * @param cacheServices
	 * @return
	 */
	public static <Param,Result> Result saveCache(Param loadParam,Result result,BaseCacheLoadService<Param, Result>... cacheServices)
	{
		if(result!=null)
		{
			for (BaseCacheLoadService<Param, Result> cacheService : cacheServices) {
				cacheService.saveCache(loadParam, result);//save cache
			}
		}
		return result;
	}
	
	/**
	 * 尝试从缓存中按照顺序进行载入,一旦发现某个缓存有返回结果就立即返回。
	 * @param loadParam
	 * @param cacheServices
	 * @return
	 */
	public static <Param,Result> Result tryLoadCache(Param loadParam,BaseCacheLoadService<Param, Result>... cacheServices) {
		Result cache=null;
		for (BaseCacheLoadService<Param, Result> cacheService : cacheServices) {
			if((cache=cacheService.load(loadParam))!=null)
				return cache;
		}
		return cache;
	}
	
	public static <Param,Result> BaseCacheLoadService<Param, Result> wrapExpire(BaseCacheLoadService<Param, Result> cacheLoadService)
	{
		return new ExpireCacheService<Param, Result>(cacheLoadService);
	}
}
