package com.qad.lang.util;


/**
 * 文本工具类。涉及文本文件的一些工具功能<br>
 * 提供一些常用的format功能
 * @author 13leaf
 *
 */
public class Texts {
	
	/**
	 * 格式化成可读的方式。如 xxMB,xxKB。
	 * @param fileSize
	 * @param unAvaliableInfo 当文件不可达时的提示信息
	 * @return
	 */
	public static String formatSize(long fileSize) {
		
		String unit="";
		float val=0f;
		if(fileSize<SIZE_KB){
			unit="B ";
			val=fileSize/SIZE_KB;
		}else if(fileSize<SIZE_MB){
			unit="MB";
			val=fileSize/SIZE_MB;
		}else if(fileSize<SIZE_GB){
			unit="GB";
			val=fileSize/SIZE_GB;
		}
		String valString=String.format("%.2f", val);
		if(valString.indexOf('.')==3) valString=valString.substring(0,3);//cut if have 3 digit
		else valString=valString.substring(0,4);
		return valString+unit;
	}

	// KB字节参考量
	public static final long SIZE_KB = 1024L;
	// MB字节参考量
	public static final long SIZE_MB = SIZE_KB * 1024L;
	// GB字节参考量
	public static final long SIZE_GB = SIZE_MB * 1024L;
	// TB字节参考量
	public static final long SIZE_TB = SIZE_GB * 1024L;
}
