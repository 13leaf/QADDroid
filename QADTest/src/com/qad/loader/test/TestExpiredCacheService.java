package com.qad.loader.test;

import java.util.concurrent.TimeUnit;

import com.qad.loader.service.ExpireCacheService;
import com.qad.loader.service.SoftCacheService;

import junit.framework.TestCase;

public class TestExpiredCacheService extends TestCase{

	public void testExpiredLoad() throws InterruptedException
	{
		ExpireCacheService<String, String> cacheService=new ExpireCacheService<String, String>(500, new SoftCacheService<String>());
		cacheService.saveCache("test", "yes");
		TimeUnit.SECONDS.sleep(1);
		assertNull(cacheService.load("test"));
	}
	
	public void testNonExpireLoad()
	{
		ExpireCacheService<String, String> cacheService=new ExpireCacheService<String, String>(5000, new SoftCacheService<String>());
		cacheService.saveCache("test", "yes");
		assertEquals("yes", cacheService.load("test"));
	}
	
}
