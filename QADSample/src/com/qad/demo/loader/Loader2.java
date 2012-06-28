package com.qad.demo.loader;

import java.io.File;
import java.util.Random;

import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

import com.qad.app.BaseActivity;
import com.qad.loader.ImageLoader;
import com.qad.loader.service.LoadServices;

public class Loader2 extends BaseActivity {

	File cacheFolder = new File(Environment.getExternalStorageDirectory(),
			"qad/cache");
	ImageLoader loader;
	ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loader=new ImageLoader(
				LoadServices.newHttpImage2Cache(cacheFolder,600 , this));
		imageView=new ImageView(this);
		loader.startLoading(getNextImage(),imageView);
		setContentView(imageView);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add("换一张");
		return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		loader.startLoading(getNextImage(),imageView);
		showMessage("载入中..请等待");
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		loader.destroy(false);//释放loader。这将杀死loader的后台线程
	}
	
	public String getNextImage()
	{
		if(currentImage==null){
			currentImage=images[random.nextInt(images.length)];
		}else {
			String temp=currentImage;
			while(temp.equals(currentImage))
			{
				currentImage=images[random.nextInt(images.length)];
			}
		}
		return currentImage;
	}
	
	Random random=new Random();
	String currentImage;
	
	String[] images = {
			"http://imgsrc.baidu.com/forum/pic/item/04297a8b4710b91298236b40c3fdfc0393452262.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/98052e976e39082654fb963f.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/5605f0dc16d18feacc116631.jpg",
			"http://imgsrc.baidu.com/forum/pic/item/0f64183885293ec8b211c7ba.jpg" };
}
