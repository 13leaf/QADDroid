package com.qad.demo.tool;

import java.util.concurrent.TimeUnit;

public class DemoTools {

//	private static Random random=new Random(System.nanoTime());
	
	/**
	 * 返回false的几率,percent的值设得越高，那么返回true的几率越大。
	 * @param percent
	 * @return
	 */
	public static boolean randomBoolean(int percent)
	{
		return (int)(Math.random()*percent)!=1;
	}
	
	/**
	 * 睡眠当前线程指定秒数
	 * @param second
	 */
	public static void sleepCurrentThread(long second)
	{
		sleepCurrentThread(second, TimeUnit.SECONDS);
	}
	
	/**
	 * 由于TimeUnit指定的时间长度
	 * @param second
	 */
	public static void sleepCurrentThread(long time,TimeUnit unit)
	{
		try {
			unit.sleep(time);
		} catch (InterruptedException e) {
			//ignore
		}
	}
	
}
