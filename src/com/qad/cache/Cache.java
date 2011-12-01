package com.qad.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;

import android.graphics.Bitmap;



//并提供从softReference<->filepool的二层cache机制。
/**
 * 通过getCache获得指定类型的Cache对象。
 * 这里的Cache特指SoftCache
 * @author 13leaf
 *
 * @param <T>
 */
public class Cache<T> {

	public Cache()
	{
	}
	
	//FIXED 1 使用ConcurrentHashmap让操作线程安全
	protected final ConcurrentHashMap<String, SoftReference<T>> map=new ConcurrentHashMap<String, SoftReference<T>>();
	
	/**
	 * 当且仅当存在该key,并且key所指向的value不为空时返回true
	 * @param key
	 */
	public boolean contains(String key)
	{
		return map.containsKey(key)&&map.get(key)!=null;
	}
	
	/**
	 * 将值put进缓存
	 * @param key
	 * @param value
	 */
	public void put(String key,T value)
	{
		map.put(key, new SoftReference<T>(value));
	}
	
	/**
	 * 
	 * @param key
	 * @param builder 若对应的值已经因为缓存过大而回收。则尝试通过ValueBuilder去同步创建
	 */
	public T get(String key,ValueBuilder<T> builder)
	{
		SoftReference<T> reference=map.get(key);
		if(reference!=null)
		{
			T value=reference.get();
			if(value!=null){
				return value;
			}
			else if(builder!=null){
				value=builder.buildValue();
				put(key, value);//refresh put in value
				return value;
			}else{
				return null;
			}
		}
		return null;
	}
	
	/**
	 * 可能在内存满时引起gc并返回空值。如果在意gc回收的话,应该使用重载函数的ValueBuilder重新同步。
	 * @param key
	 * @return
	 */
	public T get(String key)
	{
		return get(key,null);
	}
	
	/**
	 * 清空所有缓存
	 */
	public void clear()
	{
		for(SoftReference<T> value:map.values())
		{
			value.clear();
			value.enqueue();
		}
		map.clear();
	}
	
	/**
	 * 获得当前缓存的大小
	 * @return
	 */
	public int size()
	{
		return map.size();
	}
	
	private static final HashMap<Class<?>, Cache<Class<?>>> caches=new HashMap<Class<?>, Cache<Class<?>>>();
	
	/**
	 * 创建一个单例的Cache。加上sychronized确保其同步安全
	 * @deprecated
	 * @param type
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public static synchronized <T> Cache<T> getCache(Class<? extends T> type)
	{
		if(type.isAssignableFrom(Bitmap.class))
		{
			return (Cache<T>) ImageCache.getInstance();
		}
		if(caches.get(type)==null){
			caches.put(type, (Cache<Class<?>>) new Cache<T>());
		}
		return (Cache<T>) caches.get(type);
	}
}
