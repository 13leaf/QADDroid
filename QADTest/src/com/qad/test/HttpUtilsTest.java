package com.qad.test;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.zip.GZIPInputStream;

import junit.framework.TestCase;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.util.EntityUtils;

import com.qad.lang.Streams;
import com.qad.net.HttpManager;

public class HttpUtilsTest extends TestCase{

	public void testExecute() throws ClientProtocolException, IOException
	{
		HttpResponse response=HttpManager.executeHttpGet("http://www.baidu.com");
		assertTrue(response.getStatusLine().getStatusCode()==HttpStatus.SC_OK);
		String content=EntityUtils.toString(response.getEntity());
		assertNotNull(content);
	}

	
	public void testGzip() throws Exception
	{
		//test http text
		InputStream is=HttpManager.getInputStream("http://www.apache.org/");
		assertTrue(is instanceof GZIPInputStream);
		String s=Streams.readAndClose(new InputStreamReader(is));
		System.out.println(s);
		
		//auto gzipped
		HttpResponse response=HttpManager.executeHttpGet("http://www.apache.org/");
		is=response.getEntity().getContent();
		boolean acceptGzip=false;
		for (Header header : response.getAllHeaders()) {
			if(header.getName().equals("Content-Encoding") && header.getValue().equals("gzip"))
			{
				acceptGzip=true;
			}
		}
		assertTrue(acceptGzip);
		s=Streams.readAndClose(new InputStreamReader(is));
		
	}
	
}
