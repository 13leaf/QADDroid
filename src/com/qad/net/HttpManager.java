package com.qad.net;

import java.io.IOException;
import java.io.InputStream;

public interface HttpManager {

	int CONNECTION_TIME_OUT=0;
	int CONNECTION_SO_TIME_OUT=0;
	
	/**
	 * 通过get方式来打开一个网络连接流
	 * @param url
	 * @return
	 * @throws IOException
	 */
	InputStream executeGet(String url,int retryCount) throws IOException;
	
}
