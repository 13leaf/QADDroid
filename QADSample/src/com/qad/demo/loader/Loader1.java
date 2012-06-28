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

public class Loader1 extends BaseActivity {

	File cacheFolder = new File(Environment.getExternalStorageDirectory(),
			"qad/cache");
	ImageLoader loader;
	ImageView imageView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loader=new ImageLoader(
				LoadServices.newHttpImage2Cache(cacheFolder,this));
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
	String[] images={
			"http://png-1.findicons.com/files//icons/374/shiny_smiley/128/happy.png",
			"http://png-4.findicons.com/files//icons/1943/yazoo_smilies/128/smile.png",
			"http://png-5.findicons.com/files//icons/1786/oxygen_refit/128/face_smile.png",
			"http://png-1.findicons.com/files//icons/2198/dark_glass/128/emoticon.png",
			"http://png-4.findicons.com/files//icons/360/emoticons/128/smile_7.png",
			"http://png-1.findicons.com/files//icons/350/aqua_smiles/128/fun.png",
			"http://png-5.findicons.com/files//icons/2015/24x24_free_application/24/smile.png",
			"http://png-2.findicons.com/files//icons/360/emoticons/128/happy.png",
			"http://png-5.findicons.com/files//icons/360/emoticons/128/smile_4.png",
			"http://png-4.findicons.com/files//icons/2166/oxygen/22/face_smile.png",
			"http://png-5.findicons.com/files//icons/753/gnome_desktop/64/gnome_face_smile.png",
			"http://png-5.findicons.com/files//icons/408/vista_halloween/128/smile.png",
			"http://png-1.findicons.com/files//icons/350/aqua_smiles/128/happy.png",
			"http://png-5.findicons.com/files//icons/376/the_blacy/128/big_smile.png",
			"http://png-3.findicons.com/files//icons/1943/yazoo_smilies/128/big_smile.png",
			"http://png-1.findicons.com/files//icons/2198/dark_glass/128/presence_offline.png",
			"http://png-3.findicons.com/files//icons/360/emoticons/128/ok.png",
			"http://png-2.findicons.com/files//icons/360/emoticons/128/smile_1.png",
			"http://png-3.findicons.com/files//icons/1786/oxygen_refit/128/face_gearhead_male_smile.png",
			"http://png-3.findicons.com/files//icons/360/emoticons/128/glad.png",
			"http://png-2.findicons.com/files//icons/2023/standard_smile/48/smile.png",
			"http://png-4.findicons.com/files//icons/2192/flavour_extended/48/emote_smile.png",
			"http://png-1.findicons.com/files//icons/238/santa_claus/128/happy_santaclaus.png",
			"http://png-4.findicons.com/files//icons/1035/human_o2/128/face_smile.png"
	};

}
