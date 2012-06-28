package com.qad.lang;


import java.nio.charset.Charset;

public final class Encoding {

	public static final String UTF8 = "UTF-8";
	
	public static final String GBK="GBK";

	public static final String GB2312="GB2312";
	
	public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

	/**
	 * 长度为3的缓存空间
	 */
	private static final Charset[] cacheCharsets=new Charset[3];
	
	/**
	 * 缓存指针。使用循环队列算法
	 */
	private static int cacheIndex=0;
	
	
	public static String defaultEncoding(){
		return Charset.defaultCharset().name();
	}
	
	/**
	 * 根据一个编码格式来获得,若第一次取不到。那么就放入缓存，否则直接取出。<br>
	 * 缓存的大小为3个。
	 * @author 13leaf
	 * @param encode
	 * @return
	 */
	public static Charset getCacheCharset(String encode)
	{
		//检查是否含有引用
		for(int i=0;i<cacheCharsets.length;i++){
			if(cacheCharsets[i]==null){
				break;
			}
			if(cacheCharsets[i].name().equals(encode)){
				return cacheCharsets[i];
			}
		}
		cacheCharsets[cacheIndex]=Charset.forName(encode);
		cacheIndex++;
		if(cacheIndex==cacheCharsets.length){
			cacheIndex=0;//到头时返回循环头
			return cacheCharsets[0];
		}else {
			return cacheCharsets[cacheIndex-1];
		}
	}
	
	public static final int HINT_JAPANESE=1;
	
	public static final int HINT_CHINESE=2;
	
	public static final int HINT_SIMPLIFIED_CHINESE=3;
	
	public static final int HINT_TRADITIONAL_CHINESE=4;
	
	public static final int HINT_KOREAN=5;
	
	public static final int HINT_UNKNOWN=6;

}
