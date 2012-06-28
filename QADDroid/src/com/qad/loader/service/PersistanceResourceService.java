package com.qad.loader.service;

import java.io.File;

import com.qad.lang.Files;

import android.graphics.Bitmap;

/**
 * 载入Bitmap
 * @author 13leaf
 *
 */
public class PersistanceResourceService extends PersistanceService<Bitmap> {

	protected int requiredSize=70;
	
	public PersistanceResourceService(File cacheDir) {
		super(cacheDir);
	}
	
	public PersistanceResourceService(File cacheDir,int requiredSize) {
		super(cacheDir);
		this.requiredSize=requiredSize;
	}
	
	/**
	 * 
	 * @param cacheDir
	 * @param size
	 */
	public PersistanceResourceService(File cacheDir,long expiredTimeSpan,int size)
	{
		super(cacheDir,expiredTimeSpan);
		if(size<=0)
			throw new IllegalArgumentException("Required Size can not be negative : "+size);
		requiredSize=size;
	}

	public Bitmap onLoad(String loadKey) {
		if (!onPreLoad(loadKey))
			throw new IllegalArgumentException(
					"invalidate LoadKey!Check validateLoadContext First!"
							+ loadKey);
		try {
			File cacheTarget = getCacheFile(loadKey);
			if (!cacheTarget.exists()) {
				logger.warnLog("not found cache file:"
						+ cacheTarget.getAbsolutePath());
				return null;
			}
			return Files.fetchImage(cacheTarget.getAbsolutePath(),requiredSize);
		} catch (Exception e) {
			logger.errorLog("load Bitmap fail:"+loadKey);
			e.printStackTrace();
			return null;
		}
	}
	
	@Override
	public boolean saveCache(String loadKey, Bitmap instance) {
		if (!onPreLoad(loadKey))
			throw new IllegalArgumentException(
					"invalidate LoadKey!Check validateLoadContext First!"
							+ loadKey);
		if(instance==null) return false;
		try {
			File cacheTarget=getCacheFile(loadKey);
			Files.writeCompressedImage(cacheTarget, (Bitmap) instance);
			logger.debugLog("save "+loadKey+" compress Image!");
		} catch (Exception e) {
			logger.errorLog("Save Bitmap fail:"+loadKey);
			e.printStackTrace();
		}
		return true;
	}

}
