package com.qad.loader.test;

import java.io.File;
import java.text.ParseException;

import junit.framework.TestCase;
import android.os.Environment;

import com.qad.loader.service.BaseLoadService;
import com.qad.loader.service.HttpTextLoadService;
import com.qad.loader.service.LoadServices;
import com.qad.loader.service.ParseAble;
import com.qad.loader.service.PersistanceService;
import com.qad.loader.service.SoftCacheService;

public class TestLoadService extends TestCase {

	public void testHttpLoad() {
		HttpTextLoadService<String> loadService = new HttpTextLoadService<String>(
				new HttpTextLoadService.NoParse());
		String result = loadService.load("http://www.baidu.com");
		assertNotNull(result);
		assertTrue(result.trim().length() != 0);
	}

	public void testSoftCache() {
		SoftCacheService<String> cacheService = getSoftCacheService();
		final String key = "1";
		final String content = "abcdef";

		assertNull(cacheService.load(key));
		cacheService.saveCache(key, content);
		assertEquals(content, cacheService.load(key));
	}

	private SoftCacheService<String> getSoftCacheService() {
		SoftCacheService<String> cacheService = new SoftCacheService<String>();
		return cacheService;
	}

	public void testPersistCache() {
		PersistanceService<String> persitService = getPersistanceService();
		persitService.clearCache();
		final String key = "1";
		final String content = "abcdef";
		assertNull(persitService.load(key));
		persitService.saveCache(key, content);
		assertEquals(content, persitService.load(key));

		//
		final String content2 = "bbbbddddd";
		persitService.saveCache(key, content2);
		assertEquals(content2, persitService.load(key));

		persitService.clearCache();
		assertNull(persitService.load(key));
	}

	private PersistanceService<String> getPersistanceService() {
		File sdCard = Environment.getExternalStorageDirectory();
		PersistanceService<String> persitService = new PersistanceService<String>(
				new File(sdCard, "trashTest"));
		return persitService;
	}

	class Wrapper {
		Wrapper(String content) {
			this.content = content;
		}

		String content;
	}

	class TestParser implements ParseAble<Wrapper> {
		@Override
		public Wrapper parse(String s) throws ParseException {
			return new Wrapper(s);
		}
	}

	public void testParser() {
		TestParser parser = new TestParser();
		HttpTextLoadService<Wrapper> loadService = new HttpTextLoadService<Wrapper>(
				parser);
		Wrapper wrapper = loadService.load("http://www.google.com");
		assertNotNull(wrapper);
		assertNotNull(wrapper.content);
	}

	public void testAbandon() {
		PersistanceService<String> persistanceService = getPersistanceService();
		persistanceService.saveCache("2", "Hello world!");
		persistanceService.onAbandonLoad("2");
		assertNull(persistanceService.load("2"));

		SoftCacheService<String> softCacheService = getSoftCacheService();
		softCacheService.saveCache("2", "Hello world!");
		softCacheService.onAbandonLoad("2");
		assertNull(softCacheService.load("2"));
	}

	public void testClear() {
		SoftCacheService<String> cacheService = getSoftCacheService();
		final int size = 5;
		for (int i = 0; i < size; i++)
			cacheService.saveCache(i + "", i * 2 + "");
		assertTrue(size == cacheService.length());
		cacheService.clearCache();
		assertTrue(cacheService.length() == 0);
	}

	public void testHttp2Cache() {
		File testCacheDir=new File(Environment.getExternalStorageDirectory(), "test");
		BaseLoadService<String, String> loadService = LoadServices
				.newHttp2Cache(new HttpTextLoadService.NoParse(), testCacheDir ,
						LoadServices.FLAG_LOAD_FIRST);
		final String[] testUrls = {
				"http://www.baidu.com",
				"http://www.google.com",
				"http://www.ifeng.com",
				"http://www.sina.com",
				"http://www.renren.com",
				"http://www.sohu.com" };
		
		final String[] results=new String[testUrls.length];
		for(int i=0;i<testUrls.length;i++){
			String content=loadService.load(testUrls[i]);
			System.out.println(content);
			results[i]=content;
			assertNotNull(content);
			assertTrue(content.length()!=0);
		}
		
		
		
	}

}
