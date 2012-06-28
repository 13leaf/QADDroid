package com.qad.demo.loader;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.qad.app.BaseActivity;
import com.qad.demo.R.drawable;
import com.qad.loader.ImageLoader;
import com.qad.loader.ImageLoader.ImageDisplayer;
import com.qad.loader.service.LoadServices;
import com.qad.loader.LoadContext;

public class Loader3 extends BaseActivity{

	ImageLoader loader;
	ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		imageView=new ImageView(this);
		imageView.setDrawingCacheEnabled(false);
		loader=new ImageLoader(LoadServices.newHttpImageNoCache(this), null, getResources().getDrawable(drawable.icon));
		setContentView(imageView);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.destroy(false);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 1, "DefaultDisplay");
		menu.add(2,2,2,"DisplayShow");
		menu.add(3,3,3,"CustomDisplay");
		return super.onCreateOptionsMenu(menu);
	}
	
	static class MyDisplayer implements ImageDisplayer
	{
		@Override
		public void prepare(ImageView img) {
			img.setImageBitmap(null);
			img.setBackgroundColor(Color.GREEN);
		}

		@Override
		public void display(ImageView img, Bitmap bmp) {
			if(bmp==null){
				img.setBackgroundColor(Color.RED);
			}else {
				img.setBackgroundColor(Color.BLACK);
				img.setImageBitmap(bmp);
			}
		}
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		ImageDisplayer displayer = null;
		switch (item.getItemId()) {
		case 1:
			displayer=new ImageLoader.DefaultImageDisplayer(getResources().getDrawable(drawable.icon));
			break;
		case 2:
			displayer=new ImageLoader.DisplayShow();
			break;
		case 3:
			displayer=new MyDisplayer();
			break;
		}
		loader.startLoading(
				new LoadContext<String, ImageView, Bitmap>("http://imgsrc.baidu.com/forum/pic/item/98052e976e39082654fb963f.jpg",imageView)
				, displayer);
		return super.onOptionsItemSelected(item);
	}
}
