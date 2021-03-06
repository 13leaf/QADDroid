package com.qad.io;

import java.io.File;
import java.io.InputStream;

import com.qad.lang.Streams;

import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.media.ThumbnailUtils;
import android.os.Build;

public class Bitmaps {

	/**
	 * 将图片缩放至<=requiredSize*requiredSize的大小.
	 * 
	 * @param f
	 * @param requiredSize
	 * @return 若失败则返回null
	 */
	public static Bitmap decodeFile(File f, int requiredSize) {
		if (f.exists()) {
			// decode出图片大小
			BitmapFactory.Options tempOptions = null;
			tempOptions = getBitmapBounds(Streams.fileIn(f));

			// 计算需要缩放的倍数,用2的倍数尝试
			int width_tmp = tempOptions.outWidth, height_tmp = tempOptions.outHeight;// 自动识别缩放大小,以达到期望的大小
			int scale = 1;
			while (true) {
				if (width_tmp / 2 < requiredSize
						|| height_tmp / 2 < requiredSize)
					break;
				width_tmp /= 2;
				height_tmp /= 2;
				scale *= 2;
			}
			// 使用缩略图decode
			BitmapFactory.Options scaleOptions = new BitmapFactory.Options();
			scaleOptions.inSampleSize = scale;
			scaleOptions.inPurgeable = true;
			return BitmapFactory.decodeStream(Streams.fileIn(f), null,
					scaleOptions);
		}
		return null;
	}

	/**
	 * 返回的options可以读取其outerWidth和outerHeight来获取图片尺寸
	 * 
	 * @param is
	 * @return
	 */
	public static BitmapFactory.Options getBitmapBounds(InputStream is) {
		BitmapFactory.Options tempOptions = new BitmapFactory.Options();// 首先预测一下图片文件的尺寸
		tempOptions.inJustDecodeBounds = true;// 设置了该属性后不真正的decode,只是计算外围尺寸
		BitmapFactory.decodeStream(is, null, tempOptions);
		return tempOptions;
	}

	/**
	 * 默认使用70*70的尺寸
	 * 
	 * @param f
	 * @return
	 */
	public static Bitmap decodeFile(File f) {
		return decodeFile(f, 70);
	}

	/**
	 * 获取图片占用字节的大小
	 * 
	 * @param value
	 * @return
	 */
	public static int getBytesSize(Bitmap value) {
		if (value != null && value.getConfig() != null) {
			int perPixel = 0;
			switch (value.getConfig()) {
			case ALPHA_8:
				perPixel = 1;
				break;
			case ARGB_4444:
			case RGB_565:
				perPixel = 2;
				break;
			case ARGB_8888:
				perPixel = 4;
				break;
			}
			return value.getWidth() * value.getHeight() * perPixel;
		}
		return 0;
	}
	
	/**
	 * 通过硬编码补足Bitmap xml布局中gravity和TileMode不能共存的缺陷。
	 * @param res
	 * @param originalId
	 * @param scaledHeight
	 * @return
	 */
	public static BitmapDrawable fillHorizontalAndRepeatX(Resources res,int originalId,int scaledHeight)
	{
		Bitmap original=BitmapFactory.decodeResource(res, originalId);
		if(scaledHeight==0 || original.getHeight()==scaledHeight) {
			BitmapDrawable drawable= new BitmapDrawable(res,original);
			drawable.setTileModeX(TileMode.REPEAT);return drawable;
		}
		Bitmap scaled=Bitmap.createScaledBitmap(original, original.getWidth(),scaledHeight,true);
		//release original
		original.recycle();
		original=null;
		BitmapDrawable drawable=new BitmapDrawable(res,scaled);
		drawable.setTileModeX(TileMode.REPEAT);
		return drawable;
	}
	
	/**
	 * 压缩原图像为指定大小的缩略图。
	 * @param source
	 * @param width
	 * @param height
	 * @return
	 */
	@SuppressLint("NewApi")
	public static Bitmap extractThumbnail(Bitmap source,int width,int height)
	{
		if(Build.VERSION.SDK_INT>7)
			return ThumbnailUtils.extractThumbnail(source, width, height);
		else
		{
			return Bitmap.createBitmap(source, 0, 0, width, height);
		}
	}
}
