package com.qad.demo;

import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertNotSame;
import static junit.framework.Assert.assertNull;
import static junit.framework.Assert.assertSame;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;

import com.qad.app.BaseActivity;
import com.qad.cache.Cache;
import com.qad.cache.ImageCache;
import com.qad.cache.ValueBuilder;
import com.qad.demo.R.drawable;


public class GenericCache extends BaseActivity {
	
	Cache<String> testCache1=new Cache<String>();
	
	Cache<String> testCache2=new Cache<String>();
	
	Cache<Bitmap> imageCache=ImageCache.getInstance();;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		assertNotNull(testCache1);
		assertNotNull(testCache2);
		assertNotNull(imageCache);
		
		final String sentence="the quick brown fox jumps over a lazy dog";
		testCache1.put("test",sentence);
		//there must only one instance
		assertNotNull(testCache2.get("test", null));
		assertSame(testCache1.get("test", null), testCache2.get("test", null));
		
		assertNotSame(testCache1, imageCache);
		
		imageCache.put("test", BitmapFactory.decodeResource(getResources(), drawable.icon));
		assertNotNull(imageCache.get("test", null));
		getWindow().getDecorView().setBackgroundDrawable(new BitmapDrawable(imageCache.get("test", null)));
		
		//test valuebuilder
		imageCache.put("test", null);
		assertNull(imageCache.get("test",null));
		//and then
		Bitmap bmp=imageCache.get("test",new ValueBuilder<Bitmap>() {
			@Override
			public Bitmap buildValue() {
				return BitmapFactory.decodeResource(getResources(), drawable.icon);
			}
		});
		assertNotNull(bmp);
		assertNotNull(imageCache.get("test"));
	}
}
