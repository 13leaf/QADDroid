package com.qad.loader.service;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.graphics.Bitmap;

import com.qad.lang.Files;
import com.qad.lang.Streams;
import com.qad.net.HttpManager;

/**
 * 加载Http资源。目前仅支持对图片的加载
 * 
 * @author 13leaf
 * 
 * @param <T>
 */
public class HttpResourceLoadService extends BaseLoadService<String, Bitmap> {

	private File cacheDir;
	private int requiredSize;
	
	public HttpResourceLoadService(final Context context,int requiredSize)
	{
		cacheDir=context.getCacheDir();
		if(requiredSize<=0)
			throw new IllegalArgumentException("Required Size can not be negative : "+requiredSize);
		this.requiredSize=requiredSize;
	}
	
	public HttpResourceLoadService(final Context context)
	{
		this(context, 70);
	}

	@Override
	public void onAbandonLoad(String url) {
		// nothing
	}

	@Override
	public boolean onPreLoad(String loadParam) {
		return LoadServiceUtil.validateHttpUrl(loadParam);
	}

	private File getCacheImageFile(String url) {
		File cacheFile = new File(cacheDir, url.hashCode() + "");
		if (!cacheFile.exists()) {
			try {
				cacheFile.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return cacheFile;
	}

	@Override
	public Bitmap onLoad(String url) {
		Bitmap result = null;
		if (!onPreLoad(url))
			throw new IllegalArgumentException(
					"invalidate Load Context!Check validateLoadContext First!"
							+ url);
		File cacheImageFile = getCacheImageFile(url);
		try {
			InputStream is = HttpManager.getInputStream(url);
			OutputStream fos = Streams.fileOut(cacheImageFile);
			BufferedInputStream bis = Streams.buff(is);
			Streams.writeAndClose(fos, bis);
			result =  Files.fetchImage(cacheImageFile.getAbsolutePath(),requiredSize);
			cacheImageFile.delete();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			cacheImageFile.delete();
		}
		return result;
	}

}
