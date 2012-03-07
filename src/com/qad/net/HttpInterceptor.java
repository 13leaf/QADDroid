package com.qad.net;

public interface HttpInterceptor {

	/**
	 * 拦截请求。若处理失败,则由上层负责处理。最通常的处理方式是重新请求。
	 * @param url
	 * @return 返回true表示处理拦截完毕,false表示处理失败
	 */
	public boolean interceptRequest(String url);
}
