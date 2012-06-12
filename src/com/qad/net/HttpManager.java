package com.qad.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

/**
 * 1.读取网络文本
 * 2.下载/读取网络资源 
 * 3.打开网络流 4.全局配置参数 
 * 5.增加策略(如重试,http级别的缓存) 
 * 6.透明化代理 *
 * 7.透明化压缩/反压缩
 * 
 * @author 13leaf
 * 
 */
public class HttpManager {

	static IHttpManager proxy = new HttpManagerImpl();

	public static void setProxy(IHttpManager manager) {
		if (manager != null)
			proxy = manager;
	}

	public static IHttpManager getProxy() {
		return proxy;
	}

	public static HttpResponse executeHttpGet(String url) throws IOException {
		return proxy.executeHttpGet(url);
	}

	public static HttpResponse executeHttpPost(HttpPost post)
			throws IOException {
		return proxy.executeHttpPost(post);
	}

	public static boolean shouldUseProxy() {
		return proxy.shouldUseProxy();
	}

	public static DefaultHttpClient getHttpClient() {
		return proxy.getHttpClient();
	}
	
	public static HttpURLConnection getUrlConnection(String url) throws IOException{
		return proxy.getUrlConnection(url);
	}

	public static InputStream getInputStream(String url) throws IOException {
		return proxy.getInputStream(url);
	}

	public static String getHttpText(String url) throws IOException {
		return proxy.getHttpText(url);
	}
}
