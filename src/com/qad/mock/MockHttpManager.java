package com.qad.mock;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;

import com.qad.lang.Lang;
import com.qad.net.IHttpManager;

/**
 * 通过HttpManager.setProxy可完成网络模块的测试注入。
 * @author 13leaf
 *
 */
public class MockHttpManager implements IHttpManager{

	@Override
	public HttpResponse executeHttpGet(String url) throws IOException {
		Lang.noImplement();
		Lang.noImplement();
		return null;
	}

	@Override
	public HttpResponse executeHttpPost(HttpPost post) throws IOException {
		Lang.noImplement();
		return null;
	}

	@Override
	public boolean shouldUseProxy() {
		Lang.noImplement();
		return false;
	}

	@Override
	public DefaultHttpClient getHttpClient() {
		Lang.noImplement();
		return null;
	}

	@Override
	public InputStream getInputStream(String url) throws IOException {
		Lang.noImplement();
		return null;
	}

	@Override
	public String getHttpText(String url) throws IOException {
		Lang.noImplement();
		return null;
	}

	@Override
	public HttpURLConnection getUrlConnection(String url) throws IOException{
		Lang.noImplement();
		return null;
	}

}
