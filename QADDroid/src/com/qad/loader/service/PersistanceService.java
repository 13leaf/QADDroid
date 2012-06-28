package com.qad.loader.service;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;

import android.os.Environment;
import android.text.TextUtils;

import com.qad.lang.Files;
import com.qad.lang.Lang;

/**
 * 持久缓存机制 。默认构造器表示一周过期，使用重载版可以定义缓存过期。<br>
 * 亦可设置永不过期
 * 
 * @author 13leaf
 * 
 * @param <Result>
 */
public class PersistanceService<Result> extends
		BaseCacheLoadService<String, Result> {
	private File cacheDir;

	private long expiredTimeSpan;
	
	private final static String EXPIRED_FLAG_FILE="expired_flag_dat.tmp";
	
	/**
	 * setExpiredTime永不过期
	 */
	public final static int NEVER_EXPIRE=-1;
	
	/**
	 * 
	 * @param cacheDir
	 * @param expiredTimeSpan 过期间隔。默认构造器为一周,如果设置为-1将永不过期
	 */
	public PersistanceService(File cacheDir,long expiredTimeSpan) {
		setCacheDir(cacheDir);
		setExpiredTimeSpan(expiredTimeSpan);
	}
	
	public PersistanceService(File cacheDir) {
		this(cacheDir, 7*24*60*60*1000L);//default for one week
	}

	public void setCacheDir(File cacheDir) {
		if(cacheDir==null)
			throw new NullPointerException();
		this.cacheDir = cacheDir;
		if (cacheDir.exists())
			return;
		boolean isSuccess = Files.makeDir(cacheDir);
		if (isSuccess)
			logger.debugLog("create cacheDir:" + cacheDir + " ok!");
		else {
			logger.warnLog("create cacheDir:" + cacheDir + " fail!");
		}
	}
	
	public void setExpiredTimeSpan(long expiredTimeSpan) {
		this.expiredTimeSpan = expiredTimeSpan;
		if(expiredTimeSpan!=-1)
			ensureExpire(expiredTimeSpan);
	}

	private void ensureExpire(long expiredTimeSpan) {
		File flag=new File(cacheDir,EXPIRED_FLAG_FILE);
		if(!flag.exists())
		{
			try {
				flag.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		long createTime=flag.lastModified();
		long now=System.currentTimeMillis();
		long span=now-createTime;
		if(span>=expiredTimeSpan){
			Files.deleteDir(cacheDir);
			Files.makeDir(cacheDir);
		}
	}
	
	public long getExpiredTimeSpan() {
		return expiredTimeSpan;
	}

	@Override
	public boolean onPreLoad(String loadParam) {
		return !TextUtils.isEmpty(loadParam);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Result onLoad(String url) {
		if (!onPreLoad(url))
			throw new IllegalArgumentException(
					"invalidate Load Context!Check validateLoadContext First!"
							+ url);
		try {
			Result result = null;
			File cacheTarget = getCacheFile(url);
			if (!cacheTarget.exists()) {
				logger.warnLog("not found cache file:"
						+ cacheTarget.getAbsolutePath());
				return null;
			}
			result = ((Result) Files.deserializeObject(getCacheFile(url)));
			logger.debugLog("load " + url + " ok!");
			return result;
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.warnLog("load " + url + " fail.");
		return null;
	}

	protected File getCacheFile(String urlKey) {
		String fileName = MD5.md5s(urlKey);
		if (cacheDir == null)
			throw new NullPointerException(
					"Have not set CacheDir!Please setCacheDir first!");
		if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED))
			return new File(cacheDir, fileName);
		else {
			logger.warnLog("SDCard not mounted,change cacheDir to default");
			return new File(Environment.getDownloadCacheDirectory(), fileName);
		}
	}

	@Override
	public boolean saveCache(String loadKey, Result instance) {
		if (!onPreLoad(loadKey))
			throw new IllegalArgumentException(
					"invalidate LoadKey!Check validateLoadContext First!"
							+ loadKey);
		File cacheTarget = getCacheFile(loadKey);
		if(!(instance instanceof Serializable))
		{
			throw new UnsupportedOperationException("The result should be a Serializeable class!");
		}
		try {
			Files.serializeObject(cacheTarget, (Serializable) instance);
			logger.debugLog("save " + loadKey + " ok!");
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		logger.debugLog("save " + loadKey + " fail!");
		return false;
	}

	@Override
	public void clearCache() {
		boolean success = Files.deleteDir(cacheDir);
		if (success)
			logger.debugLog("clear cahce ok!");
		else
			logger.warnLog("clear cache fail!");
	}

	@Override
	public void onAbandonLoad(String loadKey) {
		File cacheFile = getCacheFile(loadKey);
		if (cacheFile.exists()) {
			boolean isSuccess = cacheFile.delete();
			logger.debugLog("abandon " + loadKey + " success " + isSuccess);
		}
	}

	@Override
	public int length() {
		Lang.noImplement();
		return -1;
	}

}
