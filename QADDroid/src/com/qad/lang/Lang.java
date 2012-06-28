package com.qad.lang;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Iterator;

/**
 *  Nutz is Licensed under the Apache License, Version 2.0 (the "License")
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * copy by 13leaf
 *
 */
public class Lang {

	/**
	 * @return 一个未实现的运行时异常
	 */
	public static RuntimeException noImplement() {
		return new RuntimeException("Not implement yet!");
	}
	
	/**
	 * 从一个文本输入流读取所有内容，并将该流关闭
	 * 
	 * @param reader
	 *            文本输入流
	 * @return 输入流所有内容
	 */
	public static String readAll(Reader reader) {
		if (!(reader instanceof BufferedReader))
			reader = new BufferedReader(reader);
		try {
			StringBuilder sb = new StringBuilder();

			char[] data = new char[64];
			int len;
			while (true) {
				if ((len = reader.read(data)) == -1)
					break;
				sb.append(data, 0, len);
			}
			return sb.toString();
		}
		catch (IOException e) {
			throw Lang.wrapThrow(e);
		}
		finally {
			Streams.safeClose(reader);
		}	
	}

	/**
	 * 将抛出对象包裹成运行时异常，并增加自己的描述
	 * 
	 * @param e
	 *            抛出对象
	 * @param fmt
	 *            格式
	 * @param args
	 *            参数
	 * @return 运行时异常
	 */
	public static RuntimeException wrapThrow(Throwable e, String fmt, Object... args) {
		return new RuntimeException(String.format(fmt, args), e);
	}
	
	/**
	 * 用运行时异常包裹抛出对象，如果抛出对象本身就是运行时异常，则直接返回。
	 * <p>
	 * 如果是 InvocationTargetException，那么将其剥离，只包裹其 TargetException
	 * 
	 * @param e
	 *            抛出对象
	 * @return 运行时异常
	 */
	public static RuntimeException wrapThrow(Throwable e) {
		if (e instanceof RuntimeException)
			return (RuntimeException) e;
		if (e instanceof InvocationTargetException)
			return wrapThrow(((InvocationTargetException) e).getTargetException());
		return new RuntimeException(e);
	}
	
	/**
	 * 根据格式化字符串，生成运行时异常
	 * 
	 * @param format
	 *            格式
	 * @param args
	 *            参数
	 * @return 运行时异常
	 */
	public static RuntimeException makeThrow(String format, Object... args) {
		return new RuntimeException(String.format(format, args));
	}

	/**
	 * 将一个数组转换成字符串
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param c
	 *            分隔符
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concat(Object c, T[] objs) {
		StringBuilder sb = new StringBuilder();
		if (null == objs || 0 == objs.length)
			return sb;

		sb.append(objs[0]);
		for (int i = 1; i < objs.length; i++)
			sb.append(c).append(objs[i]);

		return sb;
	}

	/**
	 * 将一个长整型数组转换成字符串
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param c
	 *            分隔符
	 * @param vals
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static StringBuilder concat(Object c, long[] vals) {
		StringBuilder sb = new StringBuilder();
		if (null == vals || 0 == vals.length)
			return sb;

		sb.append(vals[0]);
		for (int i = 1; i < vals.length; i++)
			sb.append(c).append(vals[i]);

		return sb;
	}

	/**
	 * 将一个整型数组转换成字符串
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param c
	 *            分隔符
	 * @param vals
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static StringBuilder concat(Object c, int[] vals) {
		StringBuilder sb = new StringBuilder();
		if (null == vals || 0 == vals.length)
			return sb;

		sb.append(vals[0]);
		for (int i = 1; i < vals.length; i++)
			sb.append(c).append(vals[i]);

		return sb;
	}

	/**
	 * 将一个数组的部分元素转换成字符串
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param offset
	 *            开始元素的下标
	 * @param len
	 *            元素数量
	 * @param c
	 *            分隔符
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concat(int offset, int len, Object c, T[] objs) {
		StringBuilder sb = new StringBuilder();
		if (null == objs || len < 0 || 0 == objs.length)
			return sb;

		if (offset < objs.length) {
			sb.append(objs[offset]);
			for (int i = 1; i < len && i + offset < objs.length; i++) {
				sb.append(c).append(objs[i + offset]);
			}
		}
		return sb;
	}

	/**
	 * 将一个数组所有元素拼合成一个字符串
	 * 
	 * @param objs
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concat(T[] objs) {
		StringBuilder sb = new StringBuilder();
		for (T e : objs)
			sb.append(e.toString());
		return sb;
	}

	/**
	 * 将一个数组部分元素拼合成一个字符串
	 * 
	 * @param offset
	 *            开始元素的下标
	 * @param len
	 *            元素数量
	 * @param array
	 *            数组
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concat(int offset, int len, T[] array) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < len; i++) {
			sb.append(array[i + offset].toString());
		}
		return sb;
	}

	/**
	 * 将一个集合转换成字符串
	 * <p>
	 * 每个元素之间，都会用一个给定的字符分隔
	 * 
	 * @param c
	 *            分隔符
	 * @param coll
	 *            集合
	 * @return 拼合后的字符串
	 */
	public static <T> StringBuilder concat(Object c, Collection<T> coll) {
		StringBuilder sb = new StringBuilder();
		if (null == coll || coll.isEmpty())
			return sb;
		Iterator<T> it = coll.iterator();
		sb.append(it.next());
		while (it.hasNext())
			sb.append(c).append(it.next());
		return sb;
	}

	public static RuntimeException makeThrow(Class<NoSuchMethodException> class1,
			String format, Object... args) {
		return new RuntimeException(String.format(format, args));
	}
	
	/**
	 * 使用当前线程的ClassLoader加载给定的类
	 * 
	 * @param className
	 *            类的全称
	 * @return 给定的类
	 * @throws ClassNotFoundException
	 *             如果无法用当前线程的ClassLoader加载
	 */
	public static Class<?> loadClass(String className) throws ClassNotFoundException {
		try {
			return Thread.currentThread().getContextClassLoader().loadClass(className);
		}
		catch (ClassNotFoundException e) {
			return Class.forName(className);
		}
	}
}
