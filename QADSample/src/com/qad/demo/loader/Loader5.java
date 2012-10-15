package com.qad.demo.loader;

import java.io.File;
import java.text.ParseException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ListView;

import com.qad.loader.BeanLoader;
import com.qad.loader.ImageLoader;
import com.qad.loader.LoadContext;
import com.qad.loader.LoadableActivity;
import com.qad.loader.StateAble;
import com.qad.loader.onRetryListener;
import com.qad.loader.service.LoadServices;
import com.qad.loader.service.ParseAble;

public class Loader5 extends LoadableActivity<BlogList>{

	static class DiSanJiParser implements ParseAble<BlogList>
	{
		Pattern title=Pattern.compile("<h1 class=\"p_title\">\\s*<a.*?>([\\s\\S]+?)</a>\\s*</h1>");
		Pattern item=Pattern.compile("<div class=\"summary\">([\\s\\S]+?)</div>");
		Pattern img=Pattern.compile("<img src=\"(.+?)\".+?/>");
		Pattern description=Pattern.compile("<p>([\\s\\S]+?)</p>");
		
		@Override
		public BlogList parse(String s) throws ParseException {
			BlogList list=new BlogList();
			Matcher titleMatcher=title.matcher(s);
			while(titleMatcher.find())
			{
				Blog blog=new Blog();
				blog.setTitle(titleMatcher.group(1).trim());
				list.add(blog);
			}
			Matcher itemMatcher=item.matcher(s);
			int i=0;
			while(itemMatcher.find()){
				if(i>=list.size()) break;
				String temp=itemMatcher.group(1);
				Matcher imgMatcher=img.matcher(temp);
				if(imgMatcher.find()) {
					list.get(i).setThumbnail(imgMatcher.group(1));
				}
				Matcher descripMatcher=description.matcher(temp);
				if(descripMatcher.find()){
					list.get(i).setDescription(descripMatcher.group(1).trim());
				}
				i++;
			}
			return list;
		}
	}
	
	BeanLoader loader=new BeanLoader();
	ImageLoader imageLoader;
	MyStateAble stateAble=new MyStateAble();
	File cacheFolder = new File(Environment.getExternalStorageDirectory(),
			"qad/cache");
	ListView listView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		loader.addLoadService(LoadServices.newHttp2Cache(new DiSanJiParser(),cacheFolder, LoadServices.FLAG_LOAD_FIRST), BlogList.class);
		imageLoader=new ImageLoader(LoadServices.newHttpImage2Cache(cacheFolder, this));
		listView=new ListView(this);
		setContentView(listView);
		startLoading();
	}
	
	@Override
	public void startLoading() {
		super.startLoading();
		loader.startLoading(new LoadContext<String, Activity, Object>("http://disanji.net", this),BlogList.class);
	}
	
	@Override
	public void render(BlogList data) {
		listView.setAdapter(new BlogListAdapter(data, imageLoader));
	}

	@Override
	public Class<BlogList> getGenericType() {
		return BlogList.class;
	}
	
	@Override
	public StateAble getStateAble() {
		return stateAble;
	}

	@Override
	public BeanLoader getLoader() {
		return loader;
	}
	
	class MyStateAble implements StateAble
	{
		@Override
		public void showLoading() {
			showMessage("加载中...");
		}

		@Override
		public void showNormal() {
			showMessage("加载完成");
		}
		@Override public void showRetryView() {
			showMessage("加载失败，请重试");
		}
		@Override public int getCurrentState() {return 0;}
		@Override public void setRetryTrigger(View view) {}
		@Override public void setOnRetryListener(onRetryListener listener) {}
	}
}
