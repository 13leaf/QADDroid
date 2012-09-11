package com.qad.view;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ScrollView;

import com.qad.form.PageManager;

/**
 * <p>PictureFall实现了一个瀑布图片流组件。</p>
 * <p>为了方便实现翻页效果，可以使用bindPageManager来完成翻页的绑定操作。<br>
 * 也可以使用addEntris来添加一批新的图片流数据。</p>
 * </p>图片流数据必须至少提供,url,width,height。width,height决定了初始的宽高比<br>
 * 根据组件的自身大小和列数，组件会自动等比缩放图片数据。</p>
 * <p>加载图片使用了N列的QueueLoader。假如列数为3列，则有3个QueueLoader。<br>
 * 有关Loader可以详见其loader文档。</p>
 * <p>xPadding,yPadding的值表示每块的横纵间距。numColumn表示展示列数。<br>
 * 设置selectedColorFilter可以实现选中状态。</p>
 * <p>可以通过继承PictureFallInternal并实现其onDrawEntry来实现进一步的渲染需求。<br>
 * 通过FallEntry的holdBitmap方法可以判断是否已经准备好图片对象。</p>
 * @author 13leaf
 *
 */
public class PictureFall extends ScrollView{
	protected PictureFallInternal internal;

	public PictureFall(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public PictureFall(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public PictureFall(Context context) {
		super(context);
		init();
	}

	private void init() {
		internal=new PictureFallInternal(getContext());
		addView(internal);
	}

	@Override
	protected void onScrollChanged(int l, int t, int oldl, int oldt) {
		super.onScrollChanged(l, t, oldl, oldt);
		internal.onScrollChanged(l, t, oldl, oldt);
	}
	
	@Override
	public boolean onTouchEvent(MotionEvent ev) {
		if(ev.getAction()!=MotionEvent.ACTION_DOWN){
			internal.onTouchEvent(ev);
		}
		return super.onTouchEvent(ev);
	}
	
	public void bindPageManager(PageManager<ArrayList<FallEntry>> manager) {
		internal.bindPageManager(manager);
	}

	public PageManager<ArrayList<FallEntry>> getPageManager() {
		return internal.getPageManager();
	}

	public void setBorderWidth(float borderWidth)
	{
		internal.setBorderWidth(borderWidth);
	}
	
	public void removeAllEntries(){
		internal.removeAllEntries();
	}

	public void addEntries(ArrayList<FallEntry> all) {
		internal.addEntries(all);
	}

	public ArrayList<ArrayList<FallEntry>> getEntries() {
		return internal.getEntries();
	}

	public FallEntry getEntry(int column, int pos) {
		return internal.getEntry(column, pos);
	}

	public void setXpadding(int xpadding) {
		internal.setXpadding(xpadding);
	}

	public void setYpadding(int ypadding) {
		internal.setYpadding(ypadding);
	}

	public void setNumColumn(int numColumn) {
		internal.setNumColumn(numColumn);
	}

	public int getXpadding() {
		return internal.getXpadding();
	}

	public int getYpadding() {
		return internal.getYpadding();
	}

	public void setOnFallClickedListener(onFallClikedListener listener) {
		internal.setOnFallClickedListener(listener);
	}

	public void setDefaultFall(Bitmap defaultFall) {
		internal.setDefaultFall(defaultFall);
	}

	public void setErrorFall(Bitmap errorFall) {
		internal.setErrorFall(errorFall);
	}

	public Bitmap getDefaultFall() {
		return internal.getDefaultFall();
	}

	public Bitmap getErrorFall() {
		return internal.getErrorFall();
	}

	public void setSelectedFilterColor(int filterColor) {
		internal.setSelectedFilterColor(filterColor);
	}

	public void setCacheFolder(File cacheFolder) {
		internal.setCacheFolder(cacheFolder);
	}

	public void destroy() {
		internal.destroy();
	}
}
