package com.qad.loader.service;

import java.io.IOException;
import java.text.ParseException;

import org.apache.http.client.ClientProtocolException;

import com.qad.net.HttpManager;

/**
 * 仅负责纯文本的载入服务
 * 
 * @author 13leaf
 * 
 * @param <T>
 */
public class HttpTextLoadService<T> extends BaseLoadService<String, T> {

	protected ParseAble<T> parser;
	
	/**
	 * 不执行任何Parse,返回原始获得字符串
	 * 
	 * @author 13leaf
	 * 
	 */
	public static class NoParse<T> implements ParseAble<T> {

		@SuppressWarnings("unchecked")
		@Override
		public T parse(String s) throws ParseException {
			return (T) s;
		}
	}
	
	/**
	 * 不包含任何解析器。将读取原始的html字符串
	 */
	public HttpTextLoadService()
	{
		this(new NoParse<T>());
	}

	public HttpTextLoadService(ParseAble<T> parser) {
		setParser(parser);
	}

	public void setParser(ParseAble<T> parser) {
		this.parser = parser;
	}

	public ParseAble<?> getParser() {
		return parser;
	}

	/**
	 * 验证载入上下文是否合理
	 * 
	 * @param url
	 * @return
	 */
	@Override
	public boolean onPreLoad(String url) {
		return LoadServiceUtil.validateHttpUrl(url);
	}

	/**
	 * 执行载入,载入失败返回null。
	 * 
	 * @param parseAble
	 * @param url
	 * @return
	 */
	public T executeLoad(ParseAble<T> parseAble, String url) {
		T result = null;
		if (!onPreLoad(url))
			throw new IllegalArgumentException(
					"invalidate Load Context!Check validateLoadContext First!"
							+ url);
		/*Unable to load Generic Type
		 * if (result instanceof Bitmap) {
			throw new UnsupportedOperationException(
					"Unsupport load Bitmap for this class!Please use HttpResouceLoadService for load resource.");
		}*/
		try {
			logger.debugLog(url);
			String content = HttpManager.getHttpText(url);
			logger.debugLog(content);
			result = parseAble.parse(content);
			return result;
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			logger.errorLog("occur parse error!"+e.getMessage());
			e.printStackTrace();
		}
		logger.warnLog("load " + url + " from Http fail!");
		return result;
	}

	@Override
	public T onLoad(String loadParam) {
		if (parser == null)
			throw new NullPointerException("Have not set Parser!");
		return executeLoad(parser, loadParam.toString());
	}

	@Override
	public void onAbandonLoad(String loadParam) {
		// do nothing
	}

}
