package com.qad.view;

import android.graphics.Bitmap;

public class FallEntry implements Comparable<FallEntry>{
	public Object tag;
	public String url;
	public int width;
	public int height;
	boolean loadError;
	Bitmap bitmap;
	int top;
	boolean isSelected;
	
	public FallEntry(Object tag,String url,int width,int height)
	{
		this.tag=tag;
		this.url=url;
		this.width=width;
		this.height=height;
		if(url==null) throw new NullPointerException("url can not be null!");
		if(width<=0 || height<=0) throw new IllegalArgumentException("Width height must be positive! "+width+","+height);
	}
	
	void scale(int baseWidth)
	{
		if(width==baseWidth) return;
		this.height=baseWidth*height/width;
		this.width=baseWidth;
	}
	
	/**
	 * 是否已经持有图片
	 * @return
	 */
	public boolean holdBitmap()
	{
		return bitmap!=null && (!bitmap.isRecycled());
	}

	@Override
	public int compareTo(FallEntry another) {
		if(another==this) return 0;
		if(another==null) return -1;
		return top-another.top;
	}
}
