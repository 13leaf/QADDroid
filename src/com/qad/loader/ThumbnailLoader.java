package com.qad.loader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.widget.ImageView;

import com.qad.loader.service.BaseLoadService;
import com.qad.loader.service.MixedCacheService;

/**
 * 
 * @author 13leaf
 * 
 */
public class ThumbnailLoader extends QueueLoader<String, ImageView, Bitmap>
		implements LoadListener {

	private ImageDisplayer displayer;
	private MixedCacheService<Bitmap> cacheService=new MixedCacheService<Bitmap>(50);

	public ThumbnailLoader(BaseLoadService<String, Bitmap> loadService,
			Drawable defaultRes) {
		super(loadService);
		displayer = new DefaultImageDisplayer(defaultRes);
		addListener(this);
	}

	public ThumbnailLoader(BaseLoadService<String, Bitmap> loadService,
			ImageDisplayer displayer, int flag) {
		super(loadService, flag);
		this.displayer = displayer;
		addListener(this);
	}

	// FIXME rewrite here cause not thread safe
	public void setImageDisplayer(ImageDisplayer displayer) {
		this.displayer = displayer;
	}

	public ImageDisplayer getImageDisplayer() {
		return displayer;
	}

	@Override
	protected boolean validateTarget(
			LoadContext<String, ImageView, Bitmap> context) {
		return context.param.equals(context.target.getTag());
	}

	@Override
	protected boolean onLoading(LoadContext<String, ImageView, Bitmap> context) {
		Bitmap cacheBitmap=cacheService.load(context.param);
		if(cacheBitmap!=null){
			displayer.display(context.target, cacheBitmap);
			return true;
		}
		displayer.prepare(context.target);
		context.target.setTag(context.param);
		return super.onLoading(context);
	}

	@Override
	public void loadComplete(LoadContext<?, ?, ?> context) {
		displayer.display((ImageView) context.target, (Bitmap) context.result);
		cacheService.saveCache((String)context.param, (Bitmap)context.result);
	}

	@Override
	public void loadFail(LoadContext<?, ?, ?> context) {
		displayer.display((ImageView) context.target, null);
	}

	public interface ImageDisplayer {
		/**
		 * 通知预备
		 * 
		 * @param img
		 */
		void prepare(ImageView img);

		/**
		 * 通知下载完毕,显示图片
		 * 
		 * @param img
		 * @param bmp
		 */
		void display(ImageView img, Bitmap bmp);
	}

	public static class DefaultImageDisplayer implements
			ThumbnailLoader.ImageDisplayer {
		private final Drawable defaultDrawable;

		public DefaultImageDisplayer(Drawable drawable) {
			this.defaultDrawable = drawable;
		}

		@Override
		public void prepare(ImageView img) {
			img.setImageDrawable(defaultDrawable);
		}

		@Override
		public void display(ImageView img, Bitmap bmp) {
			if (bmp == null)
				img.setImageDrawable(defaultDrawable);
			else
				img.setImageBitmap(bmp);
		}
	}

	public static class DisplayShow implements ThumbnailLoader.ImageDisplayer {

		@Override
		public void prepare(ImageView img) {
			img.setBackgroundDrawable(null);
		}

		@Override
		public void display(ImageView img, Bitmap bmp) {
			if (bmp == null) {
				img.setImageDrawable(null);
			} else {
				img.setImageBitmap(bmp);
			}
		}

	}

}
