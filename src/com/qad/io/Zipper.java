package com.qad.io;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.qad.lang.Files;
import com.qad.lang.Streams;

/**
 * 解压缩工具。解压使用zip压缩的内容
 * @author wangfeng
 *
 */
public class Zipper {
	
	public interface EntryHandler
	{
		/**
		 * fileName为ZipEntry中的相对文件名字
		 * @param fileName
		 * @param content
		 */
		void handlerEntry(String fileName,byte[] content);
	}

	public static void unzip(InputStream is,EntryHandler handler) throws IOException
	{
		 ZipInputStream zis = new ZipInputStream(new BufferedInputStream(is,1024));
		 try {
		     ZipEntry ze;
		     while ((ze = zis.getNextEntry()) != null) {
		         ByteArrayOutputStream baos = new ByteArrayOutputStream();
		         byte[] buffer = new byte[1024];
		         int count;
		         while ((count = zis.read(buffer)) != -1) {
		             baos.write(buffer, 0, count);
		         }
		         String filename = ze.getName();
		         byte[] bytes = baos.toByteArray();
		         handler.handlerEntry(filename, bytes);
		         // do something with 'filename' and 'bytes'...
		     }
		 } finally {
		     zis.close();
		 }
	}
	
	private static class UnzipFileHandler implements EntryHandler
	{
		private File target;
		
		public UnzipFileHandler(String targetDir)
		{
			target=new File(targetDir);
			Files.makeDir(target);
		}
		
		@Override
		public void handlerEntry(String fileName, byte[] content) {
			try {
				File file=new File(target,fileName);
				Files.createNewFile(file);
				Streams.writeAndClose(Streams.fileOut(file), content);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public static void unzip(String zipPath,String targetDir) throws IOException
	{
		unzip(Streams.fileIn(zipPath), new UnzipFileHandler(targetDir));
	}
	
}
