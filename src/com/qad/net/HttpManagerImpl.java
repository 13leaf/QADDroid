package com.qad.net;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.Proxy;
import java.net.URL;
import java.net.Proxy.Type;

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

public class HttpManagerImpl implements IHttpManager {

	private static final HttpParams defaultParams;
	public final static int CONNECTION_TIMEOUT = 3 * 1000;
	public final static int SO_TIMEOUT = 3 * 1000;
	
	static {
		defaultParams = new BasicHttpParams();

		// 设置连接超时和 Socket 超时，以及 Socket 缓存大小
		HttpConnectionParams.setConnectionTimeout(defaultParams,
				CONNECTION_TIMEOUT);
		HttpConnectionParams.setSoTimeout(defaultParams, CONNECTION_TIMEOUT);

	}

	/* (non-Javadoc)
	 * @see com.qad.net.IHttpManager#executeHttpGet(java.lang.String)
	 */
	@Override
	public HttpResponse executeHttpGet(String url) throws IOException {
		DefaultHttpClient httpclient = getHttpClient();
		HttpResponse response = httpclient.execute(new HttpGet(url));
		validResponse(response);
		return response;
	}

	private HttpResponse validResponse(HttpResponse response)
			throws IOException, ClientProtocolException {
		if (response.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
			throw new IOException("response Code:"
					+ response.getStatusLine().getStatusCode());
		}
		return response;
	}

	/* (non-Javadoc)
	 * @see com.qad.net.IHttpManager#executeHttpPost(org.apache.http.client.methods.HttpPost)
	 */
	@Override
	public HttpResponse executeHttpPost(HttpPost post) throws IOException {
		DefaultHttpClient httpClient = getHttpClient();
		HttpResponse response = httpClient.execute(post);
		validResponse(response);
		return response;
	}

	/* (non-Javadoc)
	 * @see com.qad.net.IHttpManager#shouldUseProxy()
	 */
	@Override
	public boolean shouldUseProxy() {
		return android.net.Proxy.getDefaultHost() != null
				&& android.net.Proxy.getDefaultPort() != -1;
	}

	/* (non-Javadoc)
	 * @see com.qad.net.IHttpManager#getHttpClient()
	 */
	@Override
	public DefaultHttpClient getHttpClient() {
		DefaultHttpClient httpclient = new DefaultHttpClient(defaultParams);
		if (shouldUseProxy()) {
			HttpHost proxy = new HttpHost(android.net.Proxy.getDefaultHost(),
					android.net.Proxy.getDefaultPort());
			httpclient.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
					proxy);
		}
		return httpclient;
	}

	/* (non-Javadoc)
	 * @see com.qad.net.IHttpManager#getInputStream(java.lang.String)
	 */
	@Override
	public InputStream getInputStream(String url) throws IOException {
		HttpURLConnection connection = getHttpUrlConnection(url);
		if (connection.getResponseCode() != 200) {
			throw new IOException("responseCode:"
					+ connection.getResponseCode());
		}
		return connection.getInputStream();
	}

	private HttpURLConnection getHttpUrlConnection(String url)
			throws MalformedURLException, IOException, ProtocolException {
		URL httpUrl = new URL(url);
		HttpURLConnection connection = null;
		if (shouldUseProxy()) {
			Proxy proxy = new java.net.Proxy(Type.HTTP, new InetSocketAddress(
					android.net.Proxy.getDefaultHost(),
					android.net.Proxy.getDefaultPort()));
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

	/* (non-Javadoc)
	 * @see com.qad.net.IHttpManager#getHttpText(java.lang.String)
	 */
	@Override
	public String getHttpText(String url) throws IOException {
		return Streams.readAndClose(new InputStreamReader(Streams
				.buff(getInputStream(url))));
	}

	@Override
	public HttpURLConnection getUrlConnection(String url) throws IOException{
		return getHttpUrlConnection(url);
	}
}
