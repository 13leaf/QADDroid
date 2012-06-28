package com.qad.lang.util;

import java.io.File;
import java.io.FileFilter;

/**
 * 一个简单的后缀筛选器
 * @author 13leaf
 *
 */
public class SuffixFilter implements FileFilter {
	
	/**
	 * 后缀列表
	 */
	private String[] suffixList;
	
	/**
	 * 后缀列表。使用;来分隔
	 * @param suffiz
	 */
	public SuffixFilter(String suffiz){
		suffixList=suffiz.split(";");
	}
	
	/**
	 * 后缀列表。不需要为每个suffxi加;
	 * @param suffix
	 */
	public SuffixFilter(String... suffix){
		suffixList=suffix;
	}
	
	@Override
	public boolean accept(File pathname) {
		String fileName=pathname.getName();
		for (String availableSuffix : suffixList) {
			if(fileName.endsWith(availableSuffix)) return true;
		}
		return false;
	}

}
