package com.qad.loader.service;

import java.text.ParseException;

/**
 * 将字符串解析转化为java对象
 * @author 13leaf
 *
 * @param <T>
 */
public interface ParseAble<T> {
	
	/**
	 * 执行解析
	 */
	T parse(String s) throws ParseException;
}
