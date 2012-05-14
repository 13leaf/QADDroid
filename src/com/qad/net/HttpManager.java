package com.qad.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.Proxy.Type;
import java.net.URL;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.qad.lang.Streams;

/**
 * 1.读取网络文本 * 
 * 2.下载/读取网络资源 * 
 * 3.打开网络流
 * 4.全局配置参数 
 * 5.增加策略(如重试,http级别的缓存) 
 * 6.透明化代理 *
 * 7.透明化压缩/反压缩
 * TODO 避免使用静态函数,让Http层可测试化
 * @author 13leaf
 * 
 */
public class HttpManager {

	public final static int CONNECTION_TIMEOUT = 3 * 1000;
	public final static int SO_TIMEOUT = 3 * 1000;

	private static final HttpParams defaultParams;
	static {
		defaultParams = new BasicHttpParams();

		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
		HttpConnectionParams.setConnectionTimeout(defaultParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(defaultParams, CONNECTION_TIMEOUT);

	}

	/**
	 * 返回Http应答,若不是200,则将抛出异常
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static HttpResponse executeHttpGet(String url) throws IOException {
		DefaultHttpClient httpclient = getHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(url));
		validResponse(response);
		return response;
	}

	private static HttpResponse validResponse(HttpResponse response) throws IOException,ClientProtocolException {
		if(response.getStatusLine().getStatusCode()!=HttpStatus.SC_OK)
		{
			throw new IOException("response Code:"+response.getStatusLine().getStatusCode());
		}
		return response;
	}
	
	/**
	 * 返回Http应答,若不是200,则将抛出异常
	 * @param post
	 * @return
	 * @throws IOException
	 */
	public static HttpResponse executeHttpPost(HttpPost post) throws IOException{
		DefaultHttpClient httpClient=getHttpClient();
		HttpResponse response=httpClient.execute(post);
		validResponse(response);
		return response;
	}

	public static DefaultHttpClient getHttpClient() {
		DefaultHttpClient httpclient = new DefaultHttpClient(defaultParams);
		if (ApnManager.useProxy) {
			HttpHost proxy = new HttpHost(ApnManager.proxy_server,
					ApnManager.proxy_port);
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
		return httpclient;
	}

	/**
	 * Http应答的便捷方法,如果Http返回的不是200也将抛出异常
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static InputStream getInputStream(String url) throws IOException {
		HttpURLConnection connection=getHttpUrlConnection(url);
		if(connection.getResponseCode()!=200){
			throw new IOException("responseCode:"+connection.getResponseCode());
		}
		return connection.getInputStream();
	}

	private static HttpURLConnection getHttpUrlConnection(String url)
			throws MalformedURLException, IOException, ProtocolException {
		URL httpUrl = new URL(url);
		HttpURLConnection connection = null;
		if (ApnManager.useProxy) {
			Proxy proxy=new java.net.Proxy(Type.HTTP, new InetSocketAddress(ApnManager.proxy_server, ApnManager.proxy_port));
			connection = (HttpURLConnection) httpUrl.openConnection(proxy);
		} else {
			connection = (HttpURLConnection) httpUrl.openConnection();
		}

		connection.setConnectTimeout(CONNECTION_TIMEOUT);
		connection.setReadTimeout(0);
		connection.setDoInput(true);
		connection.setUseCaches(false);
		connection.setRequestMethod("GET");
		connection.connect();
		return connection;
	}

	/**
	 * getInputStream的便捷方法,直接获得文本资源。
	 * 
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public static String getHttpText(String url) throws IOException {
		return Streams.readAndClose(
				new InputStreamReader(
						Streams.buff(getInputStream(url))));
	}
}
