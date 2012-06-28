package com.qad.loader.test;


import android.test.AndroidTestCase;

import com.qad.loader.LoadContext;
import com.qad.loader.LoadListener;
import com.qad.loader.QueueLoader;
import com.qad.loader.service.HttpTextLoadService;
import com.qad.loader.service.LoadServices;

public class TestQueueLoader extends AndroidTestCase {

	public void testLoad()
	{
		QueueLoader<String, Object, String> queueLoader=
				new QueueLoader<String, Object, String>(LoadServices.newHttpNoCache(new HttpTextLoadService.NoParse<String>()));
		queueLoader.addListener(new LoadListener() {
			
			@Override
			public void loadFail(LoadContext<?, ?, ?> context) {
				System.out.println("load Fail");
			}
			
			@Override
			public void loadComplete(LoadContext<?, ?, ?> context) {
				System.out.println(context);
			}
		});
		LoadContext<String, Object, String> firstLoad=new LoadContext<String, Object, String>("http://www.baidu.com", this);
		LoadContext<String, Object, String> secondLoad=new LoadContext<String, Object, String>("http://www.google.com", this);
		queueLoader.startLoading(firstLoad);
		//test duplicate
		queueLoader.startLoading(firstLoad);
		
		queueLoader.startLoading(secondLoad);
	}
}
