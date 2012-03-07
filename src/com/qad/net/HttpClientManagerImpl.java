package com.qad.net;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.params.HttpClientParams;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import com.qad.lang.Streams;
import com.qad.lang.Strings;
import com.qad.util.WLog;


public class HttpClientManagerImpl implements HttpManager {
	
	private DefaultHttpClient delegate=new DefaultHttpClient();
	
	private ArrayList<HttpInterceptor> interceptors=new ArrayList<HttpInterceptor>();
	
	private WLog logger=WLog.getMyLogger(HttpClientManagerImpl.class);

	public HttpClientManagerImpl()
	{
        HttpParams params = getDefaultHttpParams();
        setHttpParams(params);
	}

	private HttpParams getDefaultHttpParams() {
		HttpParams params = new BasicHttpParams();

        // Turn off stale checking.  Our connections break all the time anyway,
        // and it's not worth it to pay the penalty of checking every time.
        HttpConnectionParams.setStaleCheckingEnabled(params, false);

        // Default connection and socket timeout of 20 seconds.  Tweak to taste.
        HttpConnectionParams.setConnectionTimeout(params, 20 * 1000);
        HttpConnectionParams.setSoTimeout(params, 20 * 1000);
        HttpConnectionParams.setSocketBufferSize(params, 8192);

        // Don't handle redirects -- return them to the caller.  Our code
        // often wants to re-POST after a redirect, which we must do ourselves.
        HttpClientParams.setRedirecting(params, false);
		return params;
	}
	
	@Override
	public HttpResponse executeHttpGet(String url) throws IOException {
		if(Strings.isEmpty(url)) return null;
		
		for(HttpInterceptor interceptor :interceptors)
		{
			if(!interceptor.interceptRequest(url))
			{
				logger.errorLog("intercept request url fail:"+interceptor.getClass().getSimpleName());
				executeHttpGet(url);//递归尝试
			}
		}
		return delegate.execute(new HttpGet(url));
	}

	@Override
	public InputStream getInputStream(String url) throws IOException {
		HttpResponse response=executeHttpGet(url);
		if(response!=null && response.getStatusLine().getStatusCode()==HttpStatus.SC_OK){
			return response.getEntity().getContent();
		}
		return null;
	}

	@Override
	public String getHttpText(String url) throws IOException {
		InputStream inputStream=getInputStream(url);
		if(inputStream!=null)
		{
		}
		return null;
	}

	@Override
	public void setHttpParams(HttpParams params) {
		delegate.setParams(params);
	}

	@Override
	public void addHttpInterceptor(HttpInterceptor interceptor) {
		interceptors.add(interceptor);

	}

	@Override
	public void removeHttpInterceptor(HttpInterceptor interceptor) {
		interceptors.remove(interceptor);
	}

	@Override
	public HttpManager getInstance() {
		return null;
	}

}
