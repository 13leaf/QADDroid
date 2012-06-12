package com.qad.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

public interface IHttpManager {

	/**
	 * 返回Http应答,若不是200,则将抛出异常
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public abstract HttpResponse executeHttpGet(String url) throws IOException;

	/**
	 * 返回Http应答,若不是200,则将抛出异常
	 * 
	 * @param post
	 * @return
	 * @throws IOException
	 */
	public abstract HttpResponse executeHttpPost(HttpPost post)
			throws IOException;

	/**
	 * 是否应当使用代理
	 * 
	 * @return
	 */
	public abstract boolean shouldUseProxy();

	public abstract DefaultHttpClient getHttpClient();

	/**
	 * Http应答的便捷方法,如果Http返回的不是200也将抛出异常
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public abstract InputStream getInputStream(String url) throws IOException;

	/**
	 * getInputStream的便捷方法,直接获得文本资源。
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public abstract String getHttpText(String url) throws IOException;

	public abstract HttpURLConnection getUrlConnection(String url) throws IOException;

}