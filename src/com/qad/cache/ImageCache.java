package com.qad.cache;

import java.lang.ref.SoftReference;
import java.util.HashMap;

import com.qad.io.Bitmaps;

import android.graphics.Bitmap;
import android.util.Log;

public class ImageCache extends Cache<Bitmap> {

	private static ImageCache instance;

	private long heapSize = 0;// ImageCache包含的Bitmap大小

	private static final long MAX_HEAP_SIZE = 1024 * 1024 * 3;// 3MB大小

	private HashMap<String, Boolean> markRecycle = new HashMap<String, Boolean>();
	
	ImageCache(){}//确保单例

	@Override
	public synchronized void put(String key, Bitmap value) {
		if (value != null && !value.isRecycled()) {
			heapSize += Bitmaps.getBytesSize(value);// 计算不准确
			super.put(key, value);
		}
		Log.d("ImageCache", "currentHeapSize:" + heapSize);
		if (heapSize >= MAX_HEAP_SIZE) {
			Log.d("ImageCache", "heapSize:" + heapSize + ",cause recycle");
			clear();
		}
	}

	@Override
	public synchronized Bitmap get(String key) {
		if (!map.containsKey(key))
			return null;
		SoftReference<Bitmap> referent = map.get(key);
		Bitmap bmp = referent.get();

		if (bmp != null && bmp.isRecycled()) {
			map.remove(key);
			heapSize -= Bitmaps.getBytesSize(map.get(key).get());
		}
		if (heapSize >= MAX_HEAP_SIZE) {
			Log.d("ImageCache", "heapSize:" + heapSize + ",cause recycle");
			clear();
		}
		return super.get(key);
	}

	/**
	 * 标志recycle,当到达允许的最大HeapSize时将回收被标志的Bitmap对象
	 * 
	 * @param key
	 * @param wantRecycle
	 */
	public synchronized void markRecyle(String key, boolean wantRecycle) {
		markRecycle.put(key, wantRecycle);
	}

	@Override
	public synchronized void clear() {
//		for(String key:markRecycle.keySet())
//		{
//			Log.d("ImageCache",markRecycle.get(key)+":"+key);
//		}
		int recycleSizeCount=0;
		int recycleCount=0;
		for (String key : map.keySet()) {
			SoftReference<Bitmap> referent = map.get(key);

			if (markRecycle.get(key) != null && markRecycle.get(key))// want Recycle
			{
				Bitmap bmp=referent.get();
				if(bmp!=null)
				{
					recycleSizeCount+=Bitmaps.getBytesSize(bmp);
					recycleCount++;
					bmp.recycle();
				}
			}
		}
		Log.d("ImageCache","recycle "+recycleCount+"->"+recycleSizeCount);
		heapSize=0;
		markRecycle.clear();
		super.clear();
		System.gc();
	}

	public static synchronized ImageCache getInstance() {
		if (instance == null)
			instance = new ImageCache();
		return instance;
	}
}
