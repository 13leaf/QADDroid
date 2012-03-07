package com.qad.net;

import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.params.HttpParams;

/**
 * 预期使用httpClient和httpUrlConnection两个实现者,功能如下:
 * 1.读取网络文本
 * 2.下载/读取网络资源
 * 3.打开网络流
 * 4.全局配置参数
 * 5.增加策略(如重试,http级别的缓存)
 * 6.透明化代理
 * 7.透明化压缩/反压缩
 * 
 * @author 13leaf
 *
 */
public interface HttpManager {

	/**
	 * 返回Http应答
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public HttpResponse executeHttpGet(String url) throws IOException;
	
	/**
	 * Http应答的便捷方法,如果Http返回的不是200也将抛出异常
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public InputStream getInputStream(String url) throws IOException;
	
	/**
	 * getInputStream的便捷方法,直接获得文本资源。
	 * @param url
	 * @return
	 * @throws IOException
	 */
	public String getHttpText(String url) throws IOException;
	
	/**
	 * 设置一些全局配置参数
	 * @param params
	 */
	public void setHttpParams(HttpParams params);
	
	/**
	 * 增加请求拦截器,实现一些策略
	 */
	public void addHttpInterceptor(HttpInterceptor interceptor);
	/**
	 * 移除请求拦截器
	 * @param interceptor
	 */
	public void removeHttpInterceptor(HttpInterceptor interceptor);
	
	/**
	 * 期望HttpManager是一个单例
	 * @return
	 */
	public HttpManager getInstance();
}
