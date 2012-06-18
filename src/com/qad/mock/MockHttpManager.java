package com.qad.mock;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.ProtocolVersion;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

import com.qad.lang.Lang;
import com.qad.net.IHttpManager;

/**
 * 通过HttpManager.setProxy可完成网络模块的测试注入。
 * @author 13leaf
 *
 */
public class MockHttpManager implements IHttpManager{

	public HttpResponse getOKResponse()
	{
		StatusLine line = new BasicStatusLine(new ProtocolVersion(
				"HTTP", 1, 1), HttpStatus.SC_OK, null);
		HttpResponse response = new BasicHttpResponse(line);
		try {
			response.setEntity(new StringEntity("ok", "utf-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		return response;
	}
	
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
