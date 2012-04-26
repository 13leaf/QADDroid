package com.qad.lang.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.qad.lang.Encoding;
import com.qad.lang.Files;
import com.qad.lang.Streams;

/**
 * 文本工具类。涉及文本文件的一些工具功能<br>
 * 提供一些常用的format功能
 * @author 13leaf
 *
 */
public class Texts {

	/**
	 * 转换文本文件的字符编码.
	 * @param textFile
	 * @param readEncoding
	 * @param writeEncoding
	 */
	public static void convertEncoding(File textFile,String readEncoding,String writeEncoding){
		String text=Streams.readAndClose(
						Streams.fileInr(textFile, readEncoding));
		//read over
		Streams.writeAndClose(
					Streams.fileOutw(textFile,writeEncoding),text );
		//write over
	}
	
	/**
	 * 将一个项目的所有java文件从gbk编码转换为utf8编码。重复使用会有副作用
	 * @param projectPath
	 * @param backUp 是否在转换前先保存一下项目
	 */
	public static void convertProjectEncode(String projectPath,boolean backUp){
		File projectRoot=new File(projectPath);
		if(backUp){
			try {
				Files.copyDir(projectRoot, new File(projectRoot.getAbsolutePath()+"_backup"));
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		//
		File[] javaFiles=Files.scanFiles(projectRoot, new SuffixFilter("java"));
		for (File file : javaFiles) {
			try {
				convertEncoding(file, Encoding.detectCharset(file).name(),Encoding.UTF8);
			} catch (FileNotFoundException e) {
				//ignore
			}
		}
	}
	
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
