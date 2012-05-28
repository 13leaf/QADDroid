package com.qad.loader;

import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;

import com.qad.loader.service.BaseLoadService;
import com.qad.loader.service.MixedCacheService;

/**
 * 
 * @author 13leaf
 * 
 */
public class ImageLoader extends QueueLoader<String, ImageView, Bitmap>
		implements LoadListener {
	
	//TODO 更改View为target，提供background??
	/**
	 * 回调处理预加载、加载完毕如何显示
	 * @author 13leaf
	 *
	 */
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
	
	private static class Pack
	{
		ImageDisplayer mDisplayer;
		String param;
		public Pack(ImageDisplayer mDisplayer, String param) {
			this.mDisplayer = mDisplayer;
			this.param = param;
		}
	}

	private ImageDisplayer defaultDisplayer;
	private MixedCacheService<Bitmap> cacheService=new MixedCacheService<Bitmap>(50);
	
	public ImageLoader(BaseLoadService<String, Bitmap> loadService)
	{
		super(loadService);
		defaultDisplayer=new DisplayShow();
		addListener(this);
	}

	public ImageLoader(BaseLoadService<String, Bitmap> loadService,
			Drawable defaultRes) {
		super(loadService);
		defaultDisplayer = new DefaultImageDisplayer(defaultRes);
		addListener(this);
	}

	public ImageLoader(BaseLoadService<String, Bitmap> loadService,
			ImageDisplayer displayer, int flag) {
		super(loadService, flag);
		this.defaultDisplayer = displayer;
		addListener(this);
	}
	
	/**
	 * 使用自定义的显示策略来进行加载
	 * @param context
	 * @param mDisplayer
	 */
	public final void startLoading(LoadContext<String, ImageView, Bitmap> context,ImageDisplayer mDisplayer) {
		if(context!=null && context.target!=null)//inject displayer
		{
			context.target.setTag(new Pack(mDisplayer!=null?mDisplayer:defaultDisplayer, context.param));
		}
		startLoading(context);
	}

	public void setDefaultImageDisplayer(ImageDisplayer displayer) {
		if(displayer==null) throw new NullPointerException();
		this.defaultDisplayer = displayer;
	}

	public ImageDisplayer getDefaultImageDisplayer() {
		return defaultDisplayer;
	}

	@Override
	protected boolean validateTarget(
			LoadContext<String, ImageView, Bitmap> context) {
		return context.param.equals(((Pack)context.target.getTag()).param);
	}

	@Override
	protected boolean onLoading(LoadContext<String, ImageView, Bitmap> context) {
		Bitmap cacheBitmap=cacheService.load(context.param);
		ImageDisplayer displayer=getMyDisplayer((Pack) context.target.getTag());
		if(cacheBitmap!=null){
			displayer.display(context.target, cacheBitmap);
			return true;
		}
		displayer.prepare(context.target);
		context.target.setTag(new Pack(displayer, context.param));
		return super.onLoading(context);
	}

	private ImageDisplayer getMyDisplayer(Pack pack) {
		if(pack!=null && pack.mDisplayer!=null)
			return pack.mDisplayer;
		else
			return defaultDisplayer;
	}

	@Override
	public void loadComplete(LoadContext<?, ?, ?> context) {
		ImageView target=(ImageView) context.target;
		Pack mPack=(Pack) target.getTag();
		mPack.mDisplayer.display(target, (Bitmap) context.result);
		cacheService.saveCache((String)context.param, (Bitmap)context.result);
	}

	@Override
	public void loadFail(LoadContext<?, ?, ?> context) {
		defaultDisplayer.display((ImageView) context.target, null);
	}

	/**
	 * 未加载或加载失败显示默认图片
	 * @author 13leaf
	 *
	 */
	public static class DefaultImageDisplayer implements
			ImageLoader.ImageDisplayer {
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

	/**
	 * 未加载或加载失败不显示任何图片
	 * @author 13leaf
	 *
	 */
	public static class DisplayShow implements ImageLoader.ImageDisplayer {

		private boolean gone;
		
		public DisplayShow()
		{
			gone=false;
		}
		
		public DisplayShow(boolean gone)
		{
			this.gone=gone;
		}
		
		@Override
		public void prepare(ImageView img) {
			img.setBackgroundDrawable(null);
			if(gone)
				img.setVisibility(View.GONE);
		}

		@Override
		public void display(ImageView img, Bitmap bmp) {
			if (bmp == null) {
				img.setImageDrawable(null);
				if(gone)
					img.setVisibility(View.GONE);
			} else {
				if(gone)
					img.setVisibility(View.VISIBLE);
				img.setImageBitmap(bmp);
			}
		}

	}

}
