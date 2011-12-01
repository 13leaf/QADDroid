package com.qad.net;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;


import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

/**
 * 封装两个版本的。一个是HttpClient,一个是HttpUrlConnection。然后通过实际测试来发现哪个更快
 * @author wangfeng
 *
 */
public class HttpUtils {
	private static final String TAG = "HttpUtils";
	private static final int CONNECTION_TIMEOUT = 3 * 1000;//1秒的等待时间
	private static final int CONNECTION_SO_TIMEOUT=0;//无限等待socket

	public static HttpHost getProxy(Context ctx) {
		return null;
	}

	/**
	 * ISSUE:HttpClient是单线程的。多线程同时请求网络的时候有可能会发生阻塞现象
	 * @param proxy
	 * @param url
	 * @return
	 * @throws ClientProtocolException
	 * @throws IOException
	 */
	protected static HttpResponse getHttpResponse(HttpHost proxy, String url)
			throws ClientProtocolException, IOException {
		Log.d(TAG, "Get HTTP response >>> " + url + ", proxy: " + proxy);

		if (TextUtils.isEmpty(url)) {
			return null;
		}

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, CONNECTION_SO_TIMEOUT);
		if (proxy != null)
			ConnRouteParams.setDefaultProxy(params, proxy);

		HttpClient httpClient = new DefaultHttpClient(params);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (Exception es) {
		}
		return response;
	}
	
	public static HttpResponse getHttpResponse(String url)
			throws ClientProtocolException, IOException {
		Log.d(TAG, "Get HTTP response >>> " + url);

		if (TextUtils.isEmpty(url)) {
			return null;
		}

		HttpParams params = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(params, CONNECTION_SO_TIMEOUT);

		HttpClient httpClient = new DefaultHttpClient(params);
		HttpGet httpGet = new HttpGet(url);
		HttpResponse response = null;
		try {
			response = httpClient.execute(httpGet);
		} catch (Exception es) {
			es.printStackTrace();
		}
		return response;
	}
	
	
	/**
	 * UrlConnection
	 * @param proxy
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static URLConnection getHttpConnection(Proxy proxy, URL url)
			throws IOException {
		URLConnection conn = null;
		if (proxy != null) {
			conn = url.openConnection(proxy);
			conn.setConnectTimeout(CONNECTION_TIMEOUT);
		} else {
			conn = url.openConnection();
		}
		return conn;
	}

	public static URLConnection getHttpConnection(Proxy proxy, String url)
			throws IOException {
		return getHttpConnection(proxy, new URL(url));
	}
	
	public static URLConnection getHttpConnection(String url) throws IOException
	{
		return getHttpConnection(null,new URL(url));
	}

	/**
	 * 打开InputStream
	 * @param urlString
	 * @return
	 * @throws IOException
	 */
	public static InputStream openInputStream(String urlString) throws  IOException {
		HttpResponse response=executeGet(urlString);
		if(response!=null)
		{
			return response.getEntity().getContent();
		}else {
			return null;
		}
	}
	
	/**
	 * 若成功,则返回HttpResponse封装,否则返回null
	 * @return
	 * @throws IOException 
	 * @throws ClientProtocolException 
	 */
	public static HttpResponse executeGet(String urlString) throws ClientProtocolException, IOException
	{
		if(urlString==null) return null;
		DefaultHttpClient httpClient=new DefaultHttpClient();
		HttpGet request = new HttpGet(urlString);
		HttpResponse response = httpClient.execute(request);
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			request.abort();
		}
		return response;
	}
	
	public static String getResponseString(String urlString)
			throws ClientProtocolException, IOException {
		HttpResponse response = executeGet(urlString);
		if(response!=null)
			return EntityUtils.toString(response.getEntity());
		else {
			return "";
		}
	}

}
